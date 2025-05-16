package com.hzy.mapper;

import com.github.pagehelper.Page;
import com.hzy.annotation.AutoFill;
import com.hzy.dto.SetmealPageDTO;
import com.hzy.entity.Setmeal;
import com.hzy.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

     List<Setmeal> getSetmealByIds(List<Integer> ids);


    @AutoFill(OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    Page<Setmeal> getPageList(SetmealPageDTO setmealPageDTO);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getSetmealById(Integer id);

    @Update("update setmeal set status = IF(status = 1, 0, 1) where id = #{id}")
    void onOff(Integer id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    void deleteBatch(List<Integer> ids);

    List<Setmeal> getList(Setmeal setmeal);

    @Select("select count(id) from setmeal where status = #{i}")
    Integer getByStatus(int i);
}
