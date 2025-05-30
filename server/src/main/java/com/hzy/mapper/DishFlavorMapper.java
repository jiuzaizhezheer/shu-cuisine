package com.hzy.mapper;

import com.hzy.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insertBatch(List<DishFlavor> flavorList);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Integer id);

    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Integer dishId);

    void deleteBatchByDishIds(List<Integer> ids);

   List<DishFlavor> getDishFlavorByDishIds(List<Integer> dishIds);
}
