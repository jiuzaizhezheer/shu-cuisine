package com.hzy.vo;

import com.hzy.entity.OrderDetail;
import com.hzy.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Order implements Serializable {
    //TODO 继承 Order，并添加 orderDishes 和 orderDetailList 属性
    private String orderDishes; // 订单菜品信息
    private List<OrderDetail> orderDetailList; // 订单详情

}
