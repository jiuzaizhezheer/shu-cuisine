package com.hzy.service;

import com.hzy.dto.SetmealDTO;
import com.hzy.dto.SetmealPageDTO;
import com.hzy.entity.Setmeal;
import com.hzy.result.PageResult;
import com.hzy.vo.DishItemVO;
import com.hzy.vo.SetmealVO;
import com.hzy.vo.SetmealWithPicVO;

import java.util.List;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult<Setmeal> getPageList(SetmealPageDTO setmealPageDTO);

    SetmealVO getSetmealById(Integer id);

    void onOff(Integer id);

    void update(SetmealDTO setmealDTO);

    void deleteBatch(List<Integer> ids);

    List<Setmeal> getList(Integer categoryId);

    List<DishItemVO> getSetmealDishesById(Integer id);

    SetmealWithPicVO getSetmealWithPic(Integer id);
}
