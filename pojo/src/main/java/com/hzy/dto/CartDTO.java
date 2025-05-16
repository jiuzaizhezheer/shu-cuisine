package com.hzy.dto;

import lombok.Data;

import java.io.Serializable;
//TODO DTO不需要Builder,
@Data
public class CartDTO implements Serializable {

    private Integer dishId;
    private Integer setmealId;
    private String dishFlavor;

}
