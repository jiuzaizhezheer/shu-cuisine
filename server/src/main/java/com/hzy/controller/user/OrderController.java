package com.hzy.controller.user;

import com.hzy.dto.OrderPaymentDTO;
import com.hzy.dto.OrderSubmitDTO;
import com.hzy.result.PageResult;
import com.hzy.result.Result;
import com.hzy.service.OrderService;
import com.hzy.vo.OrderPaymentVO;
import com.hzy.vo.OrderSubmitVO;
import com.hzy.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param orderSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        log.info("用户传过来的订单信息-------------------------：{}", orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 当前用户未支付订单数量
     *
     * @return
     */
    @GetMapping("/unPayOrderCount")
    public Result<Integer> unPayOrderCount() {
        log.info("查询当前用户未支付订单数量");
        return Result.success(orderService.unPayOrderCount());
    }

    /**
     * 订单支付
     *
     * @param orderPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrderPaymentDTO orderPaymentDTO) throws Exception {
        log.info("订单支付：{}", orderPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(orderPaymentDTO);
        log.info("生成的预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 根据id查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getById(@PathVariable Integer id) {
        log.info("订单id:{}", id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    /**
     * 条件分页查询历史订单
     *
     * @param page
     * @param pageSize
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult<OrderVO>> page(int page, int pageSize, Integer status) {
        PageResult<OrderVO> pageResult = orderService.userPage(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 用户取消订单
     *
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id) throws Exception {
        log.info("用户手动取消订单，id为：{}", id);
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/reOrder/{id}")
    public Result reOrder(@PathVariable Integer id) {
        log.info("根据订单id再来一单：{}", id);
        orderService.reOrder(id);
        return Result.success();
    }


    /**
     * 用户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Integer id){
        log.info("用户催单，订单id:{}", id);
        orderService.reminder(id);
        return Result.success();
    }
}
