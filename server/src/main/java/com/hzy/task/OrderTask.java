package com.hzy.task;

import com.hzy.entity.Order;
import com.hzy.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO自定义定时任务，实现订单状态定时处理
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理{支付}超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("处理支付超时订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        updateOrdersByStatusAndTime(
                Order.PENDING_PAYMENT,
                time,
                Order.CANCELLED,
                "支付超时，自动取消"
        );
    }


    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理派送中订单：{}", LocalDate.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        updateOrdersByStatusAndTime(
                Order.DELIVERY_IN_PROGRESS,
                time,
                Order.COMPLETED,
                "前一天订单自动完成"
        );
    }


    /**
     * 更新指定状态且下单时间早于给定时间的订单
     *
     * @param status 状态码
     * @param threshold 时间阈值
     * @param newStatus 新状态
     * @param cancelReason 取消原因
     */
    private void updateOrdersByStatusAndTime(int status, LocalDateTime threshold, int newStatus, String cancelReason) {
        List<Order> ordersList = orderMapper.getByStatusAndOrderTimeLT(status, threshold);

        if (ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(order -> {
                order.setStatus(newStatus);
                order.setCancelReason(cancelReason);
                order.setCancelTime(LocalDateTime.now());
            });

            orderMapper.batchUpdate(ordersList);
        }
    }


}