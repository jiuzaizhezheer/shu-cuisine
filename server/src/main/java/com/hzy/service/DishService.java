package com.hzy.service;

import com.hzy.dto.DishDTO;
import com.hzy.dto.DishPageDTO;
import com.hzy.entity.Dish;
import com.hzy.result.PageResult;
import com.hzy.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDishWithFlavor(DishDTO dishDTO);

    PageResult<Dish> getPageList(DishPageDTO dishPageDTO);

    DishVO getDishWithFlavorById(Integer id);

    void updateDishWithFlavor(DishDTO dishDTO);

    void deleteBatch(List<Integer> ids);

    void onOff(Integer id);

    List<DishVO> getDishWithFlavorByCategoryId(Integer id);
}
