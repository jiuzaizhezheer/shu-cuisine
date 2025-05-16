package com.hzy.service;

import com.hzy.dto.*;
import com.hzy.result.PageResult;
import com.hzy.vo.OrderPaymentVO;
import com.hzy.vo.OrderStatisticsVO;
import com.hzy.vo.OrderSubmitVO;
import com.hzy.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrderSubmitDTO orderSubmitDTO);

    Integer unPayOrderCount();

    OrderVO getById(Integer id);

    PageResult<OrderVO> userPage(int page, int pageSize, Integer status);

    void userCancelById(Integer id) throws Exception;

    void reOrder(Integer id);

    OrderPaymentVO payment(OrderPaymentDTO orderPaymentDTO);

    PageResult<OrderVO>  conditionSearch(OrderPageDTO orderPageDTO);

    OrderStatisticsVO statistics();

    void confirm(OrderConfirmDTO orderConfirmDTO);

    void reject(OrderRejectionDTO orderRejectionDTO);

    void cancel(OrderCancelDTO orderCancelDTO);

    void delivery(Integer id);

    void complete(Integer id);

    void reminder(Integer id);
}
