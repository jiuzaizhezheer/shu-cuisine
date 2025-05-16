package com.hzy.service.serviceImpl;

import com.hzy.constant.StatusConstant;
import com.hzy.entity.Order;
import com.hzy.mapper.DishMapper;
import com.hzy.mapper.OrderMapper;
import com.hzy.mapper.SetmealMapper;
import com.hzy.mapper.UserMapper;
import com.hzy.service.WorkSpaceService;
import com.hzy.vo.BusinessDataVO;
import com.hzy.vo.DishOverViewVO;
import com.hzy.vo.OrderOverViewVO;
import com.hzy.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 工作台今日数据总览
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */

        Map<String,Object> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        // 先查询总订单数作为分母
        List<Integer> totalOrderCount = orderMapper.countByMaps(List.of(map));
        map.put("status", Order.COMPLETED);
        // 1、营业额
        List<Double> turnover = orderMapper.sumByMaps(List.of(map));
//        turnover = turnover == null? 0.0 : turnover;
        // 2、有效订单数
        List<Integer> validOrderCount = orderMapper.countByMaps(List.of(map));
        Double orderCompletionRate = 0.0;
        Double unitPrice = 0.0;
        //非0才可以做分母
        if(totalOrderCount.get(0) != 0 && validOrderCount.get(0)  != 0){
            // 3、订单完成率
            orderCompletionRate = validOrderCount.get(0).doubleValue() / totalOrderCount.get(0);
            // 4、平均客单价
            unitPrice = turnover.get(0) / validOrderCount.get(0);
        }
        // 5、新增用户数
        List<Integer> newUsers = userMapper.countByMaps(List.of(map));
        return BusinessDataVO.builder()
                .turnover(turnover.get(0))
                .validOrderCount(validOrderCount.get(0))
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers.get(0))
                .build();
    }

    /**
     * 订单数据情况
     * @return
     */
    public OrderOverViewVO getOrderOverView(LocalDateTime begin, LocalDateTime end) {
        /**
         * 全部订单 待接单 待派送 已完成 已取消
         */
        Map<String,Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        List<Integer> allOrders = orderMapper.countByMaps(List.of(map));
        //添加status字段
        map.put("status", Order.TO_BE_CONFIRMED);
        List<Integer> toConfirmed = orderMapper.countByMaps(List.of(map));
        map.put("status", Order.CONFIRMED);
        List<Integer> toDelivery = orderMapper.countByMaps(List.of(map));
        map.put("status", Order.COMPLETED);
        List<Integer> completed = orderMapper.countByMaps(List.of(map));
        map.put("status", Order.CANCELLED);
        List<Integer> canceled = orderMapper.countByMaps(List.of(map));

        return OrderOverViewVO.builder()
                .allOrders(allOrders.get(0))
                .waitingOrders(toConfirmed.get(0))
                .deliveredOrders(toDelivery.get(0))
                .completedOrders(completed.get(0))
                .cancelledOrders(canceled.get(0))
                .build();
    }

    /**
     * 菜品总览
     * @return
     */
    public DishOverViewVO getDishOverView() {
        /**
         * 已起售 已停售
         */
        Integer on = dishMapper.getByStatus(StatusConstant.ENABLE);
        Integer off = dishMapper.getByStatus(StatusConstant.UNABLE);
        return DishOverViewVO.builder()
                .sold(on)
                .discontinued(off)
                .build();
    }

    /**
     * 套餐总览
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        /**
         * 已起售 已停售
         */
        Integer on = setmealMapper.getByStatus(StatusConstant.ENABLE);
        Integer off = setmealMapper.getByStatus(StatusConstant.UNABLE);
        return SetmealOverViewVO.builder()
                .sold(on)
                .discontinued(off)
                .build();
    }

}
