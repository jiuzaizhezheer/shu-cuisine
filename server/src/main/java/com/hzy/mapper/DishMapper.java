package com.hzy.mapper;

import com.github.pagehelper.Page;
import com.hzy.annotation.AutoFill;
import com.hzy.dto.DishPageDTO;
import com.hzy.entity.Dish;
import com.hzy.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {
    @AutoFill(OperationType.INSERT)
    void addDish(Dish dish);

    Page<Dish> getPageList(DishPageDTO dishPageDTO);


    List<Dish> getByIds(List<Integer> ids);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    void deleteBatchByIds(List<Integer> ids);

    @Update("update dish set status = IF(status = 0, 1, 0) where id = #{id}")
    void onOff(Integer id);

    List<Dish> getList(Dish dish);

    @Select("select count(id) from dish where status = #{i}")
    Integer getByStatus(int i);
    @Select("SELECT * FROM dish WHERE id=#{id}")
    Dish getById(Integer id);
}
