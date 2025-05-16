package com.hzy.service.serviceImpl;


import com.hzy.dto.GoodsSalesDTO;
import com.hzy.entity.Order;
import com.hzy.mapper.OrderMapper;
import com.hzy.mapper.UserMapper;
import com.hzy.service.ReportService;
import com.hzy.service.WorkSpaceService;
import com.hzy.vo.*;
import jakarta.servlet.ServletOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDates(begin, end);
        List<Map<String, Object>> dataTimeList = getMaps(dateList);
        List<Double> turnoverList =orderMapper.sumByMaps(dataTimeList);
        //数据封装
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }



    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUser(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDates(begin, end);
        List<Map<String, Object>> dateTimeList = getMaps(dateList);
        //每日新增用户数
        List<Integer> newUserList = userMapper.countByMaps(dateTimeList);
        //每日总用户数，去除begin
        dateTimeList.forEach(map -> map.remove("begin"));
        List<Integer> totalUserList =userMapper.countByMaps(dateTimeList);
        // 数据封装
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrder(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDates(begin, end);
        List<Map<String, Object>> dataTimeList = getMaps(dateList);//有status
        // 日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        String dateListStr = StringUtils.join(dateList, ",");
        // 每日有效订单数，以逗号分隔，例如：20,21,10,有status
        List<Integer> orderCountList = orderMapper.countByMaps(dataTimeList);
        String orderCountListStr = StringUtils.join(orderCountList, ",");
        // 有效订单数
        List<Integer> validOrderCount = orderMapper.countByMaps(dataTimeList);
        //TODO stream流求和
        Integer validCount = validOrderCount.stream().reduce(Integer::sum).get();
        // 每日订单数，以逗号分隔，例如：260,210,215,无status
        dataTimeList.forEach(map -> map.remove("status"));//先删除status
        List<Integer> validOrderCountList = orderMapper.countByMaps(dataTimeList);
        String validOrderCountListStr = StringUtils.join(validOrderCountList, ",");
        // 订单总数
        List<Integer> totalOrderCount = orderMapper.countByMaps(dataTimeList);
        Integer totalCount = 0;
        for (Integer integer : totalOrderCount) {
            totalCount += integer;
        }
        // 订单完成率
        Double orderCompletionRate=0.0;
        if(totalCount != 0 && validCount != 0){
             orderCompletionRate = validCount.doubleValue() / totalCount;
        }
        return OrderReportVO.builder()
                .dateList(dateListStr)
                .orderCountList(orderCountListStr)
                .validOrderCountList(validOrderCountListStr)
                .totalOrderCount(totalCount)
                .validOrderCount(validCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销量Top10统计
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        // Top 10 列表，每个元素都有商品名称和销量值
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);
        // 流式操作，拿到列表中所有name/number属性组成的列表，然后再用","序列化成字符串，变成xxx,xxx,xxx的形式
        String nameList = StringUtils.join(goodsSalesDTOList.stream()
                .map(GoodsSalesDTO::getName).collect(Collectors.toList()),",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream()
                .map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),",");
        // 数据封装返回
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出近30天的运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        // 提前将 运营数据报表模板.xlsx 拷贝到项目的resources/template目录中
        // 拿到 前30天 - 前1天 的数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        // 日期 转 日期加时间，转的时候要指定时间字段
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        // 调用service方法来获取工作台数据（注意是service而不是mapper，因为这个功能之前实现过，直接拿来用就行）
        BusinessDataVO businessData = workSpaceService.getBusinessData(beginTime, endTime);
        //TODO 通过反射获取IO流对象
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet1 = excel.getSheetAt(0);
            // 第2行写入时间字段
            sheet1.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            // 第4、5行写入概览数据
            XSSFRow row4 = sheet1.getRow(3);
            // 获取单元格，填入营业额、订单完成率、新增用户数量
            row4.getCell(2).setCellValue(businessData.getTurnover());
            row4.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row4.getCell(6).setCellValue(businessData.getNewUsers());
            XSSFRow row5 = sheet1.getRow(4);
            // 获取单元格，填入有效订单数、订单平均价格
            row5.getCell(2).setCellValue(businessData.getValidOrderCount());
            row5.getCell(4).setCellValue(businessData.getUnitPrice());


            List<LocalDate> localDates = getLocalDates(begin, end);
            List<Map<String, Object>> dataTimeList = getMaps(localDates);
            //营业额
            List<Double> turnoverList = orderMapper.sumByMaps(dataTimeList);
            //有效订单数
            List<Integer> completedList = orderMapper.countByMaps(dataTimeList);
            //查询新增用户数
            List<Integer> newUsersList = userMapper.countByMaps(dataTimeList);
            //去除status
            dataTimeList.forEach(map -> map.remove("status"));
            //总订单数
            List<Integer>  totalOrderCountList = orderMapper.countByMaps(dataTimeList);

            // 插入30行明细数据，每行6个单元格的值对应一天的数据概览
//            for (int i = 0; i < 30; i++) {
//                LocalDate date = begin.plusDays(i);
//                // 准备每天的明细数据
//                businessData = workSpaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
//                XSSFRow row = sheet.getRow(7 + i);
//                row.getCell(1).setCellValue(date.toString());
//                row.getCell(2).setCellValue(businessData.getTurnover());
//                row.getCell(3).setCellValue(businessData.getValidOrderCount());
//                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
//                row.getCell(5).setCellValue(businessData.getUnitPrice());
//                row.getCell(6).setCellValue(businessData.getNewUsers());
//            }

            for(int i=0;i<localDates.size();i++){
                //营业额
                Double turnover = turnoverList.get(i);
                //有效订单数
                Integer validOrderCount = completedList.get(i);
                //总订单数
                Integer total = totalOrderCountList.get(i);
                //新增用户数
                Integer newUser  = newUsersList.get(i);
                //订单完成率
                Double orderCompletionRate = 0.0;
                if (total!=0 && validOrderCount!=0) {
                    orderCompletionRate = Double.valueOf(validOrderCount)/ total;
                }
                //平均客单价
                Double unitPrice = 0.0;
                if (validOrderCount!=0 && turnover!=0) {
                    unitPrice = turnover / validOrderCount;
                    //控制输出的小数位数
                    unitPrice = Math.round(unitPrice * 1e2)/100.0;
                }


                //插入excel第8，9，10...行的2，3，4，5，6，7列
                XSSFRow  row8i = sheet1.getRow(7+i);
                row8i.getCell(1).setCellValue(begin.plusDays(i).toString());
                row8i.getCell(2).setCellValue(turnover);
                row8i.getCell(3).setCellValue(validOrderCount);
                row8i.getCell(4).setCellValue(orderCompletionRate);
                row8i.getCell(5).setCellValue(unitPrice);
                row8i.getCell(6).setCellValue(newUser);
            }
            // 创建输出流，excel数据放进流里，通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            // 刷新并关闭资源
            out.flush();
            out.close();
            excel.close();
        } catch (IOException e) {
            // 打印错误就行，不要抛异常使程序中断
            e.printStackTrace();
        }
    }


    //公共方法
    private static List<Map<String, Object>> getMaps(List<LocalDate> dateList) {
        //转化为LocalDateTime形式，方便后续查询
        List<Map<String, Object>> dataTimeList = dateList.stream().map(date -> {
            Map<String, Object> map = new HashMap<>();
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Order.COMPLETED);
            return map;
        }).toList();
        return dataTimeList;
    }

    private static List<LocalDate> getLocalDates(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1); // 日期计算，获得指定日期后1天的日期
            dateList.add(begin);
        }
        return dateList;
    }}
