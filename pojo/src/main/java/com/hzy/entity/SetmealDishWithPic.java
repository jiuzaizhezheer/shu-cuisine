package com.hzy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetmealDishWithPic implements Serializable {
    //TODO 连接表实体类
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer copies;
    private String pic;
    private Integer setmealId;
    private Integer dishId;
}
