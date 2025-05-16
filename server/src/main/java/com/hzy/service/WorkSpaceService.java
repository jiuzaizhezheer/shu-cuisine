package com.hzy.service;

import com.hzy.vo.BusinessDataVO;
import com.hzy.vo.DishOverViewVO;
import com.hzy.vo.OrderOverViewVO;
import com.hzy.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    OrderOverViewVO getOrderOverView(LocalDateTime begin, LocalDateTime end);

    DishOverViewVO getDishOverView();

    SetmealOverViewVO getSetmealOverView();
}
