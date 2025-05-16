package com.hzy.controller.admin;

import com.hzy.dto.OrderCancelDTO;
import com.hzy.dto.OrderConfirmDTO;
import com.hzy.dto.OrderPageDTO;
import com.hzy.dto.OrderRejectionDTO;
import com.hzy.result.PageResult;
import com.hzy.result.Result;
import com.hzy.service.OrderService;
import com.hzy.vo.OrderStatisticsVO;
import com.hzy.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 条件分页查询订单信息
     * @param orderPageDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult<OrderVO>> conditionSearch(OrderPageDTO orderPageDTO){
        log.info("条件分页查询订单信息：{}", orderPageDTO);
        PageResult<OrderVO> pageResult =  orderService.conditionSearch(orderPageDTO);
        return Result.success(pageResult);

    }

    /**
     * 不同状态订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 根据订单id查询订单信息
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Integer id){
        log.info("根据订单id查询订单详情");
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param orderConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrderConfirmDTO orderConfirmDTO){
        log.info("修改订单状态为接单：{}", orderConfirmDTO);
        orderService.confirm(orderConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param orderRejectionDTO
     * @return
     */
    @PutMapping("/reject")
    public Result reject(@RequestBody OrderRejectionDTO orderRejectionDTO){
        log.info("拒单原因：{}", orderRejectionDTO);
        orderService.reject(orderRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param orderCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrderCancelDTO orderCancelDTO){
        log.info("取消订单，有原因：{}", orderCancelDTO);
        orderService.cancel(orderCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Integer id){
        log.info("派送中：{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Integer id){
        log.info("已完成：{}", id);
        orderService.complete(id);
        return Result.success();
    }

}
