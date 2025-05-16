package com.hzy.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hzy.constant.MessageConstant;
import com.hzy.constant.StatusConstant;
import com.hzy.dto.SetmealDTO;
import com.hzy.dto.SetmealPageDTO;
import com.hzy.entity.Setmeal;
import com.hzy.entity.SetmealDish;
import com.hzy.entity.SetmealDishWithPic;
import com.hzy.exception.DeleteNotAllowedException;
import com.hzy.mapper.SetmealDishMapper;
import com.hzy.mapper.SetmealMapper;
import com.hzy.result.PageResult;
import com.hzy.service.SetmealService;
import com.hzy.vo.DishItemVO;
import com.hzy.vo.SetmealVO;
import com.hzy.vo.SetmealWithPicVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.ENABLE);  // 默认启用套餐
        setmealMapper.addSetmeal(setmeal);
        // 套餐包含的菜品批量插入
        Integer setmealId = setmeal.getId();
        // 1. 遍历setmealDTO中的菜品列表，每个菜品都为其setmealId字段赋值
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐条件分页查询
     * @param setmealPageDTO
     * @return
     */
    public PageResult<Setmeal> getPageList(SetmealPageDTO setmealPageDTO) {
        PageHelper.startPage(setmealPageDTO.getPage(), setmealPageDTO.getPageSize());
        Page<Setmeal> setmealList = setmealMapper.getPageList(setmealPageDTO);
        return new PageResult<>(setmealList.getTotal(), setmealList.getResult());
    }

    /**
     * 根据套餐id查询套餐，包括菜品信息
     * @param id
     * @return
     */
    public SetmealVO getSetmealById(Integer id) {
        Setmeal setmeal = setmealMapper.getSetmealById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesBySetmealId(id);
        // 组成SetmealVO后返回
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 起售停售套餐
     * @param id
     */
    public void onOff(Integer id) {
        setmealMapper.onOff(id);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 先修改套餐setmeal
        setmealMapper.update(setmeal);
        // 再修改套餐下的菜品setmealDish
        // 由于行数据可能不同，因此需要先根据setmealId批量删除
        Integer setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        //再考虑批量插入
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    /**
     * 批量删除套餐
     * @param ids
     */
    public void deleteBatch(List<Integer> ids) {
        // 遍历要删除的所有套餐，如果但凡有一个在起售就抛异常
        List<Setmeal> setmealList = setmealMapper.getSetmealByIds(ids);
        setmealList.forEach(setmeal -> {
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeleteNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBatch(ids);
    }

    /**
     * 根据分类id查询所有套餐
     * @return
     */
    public List<Setmeal> getList(Integer categoryId) {
        // 还有一个条件：起售的套餐才能展示在客户端
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> setmealList = setmealMapper.getList(setmeal);
        return setmealList;
    }

    /**
     * 根据套餐id查询所有菜品
     * @param id
     * @return
     */
    public List<DishItemVO> getSetmealDishesById(Integer id) {
        List<SetmealDish> setmealDishList = setmealDishMapper.getDishesBySetmealId(id);
        List<DishItemVO> dishItemVOList = setmealDishList.stream().map(setmealDish -> {
            DishItemVO dishItemVO = new DishItemVO();
            BeanUtils.copyProperties(setmealDish, dishItemVO);
            return dishItemVO;
        }).toList();
        return dishItemVOList;
    }

    /**
     * 根据套餐id获取套餐详情，其中菜品都要有pic图片信息
     * @param id
     * @return
     */
    public SetmealWithPicVO getSetmealWithPic(Integer id) {
        Setmeal setmeal = setmealMapper.getSetmealById(id);
        //  获取菜品列表
        List<SetmealDishWithPic> dishWithPics = setmealDishMapper.getDishesWithPic(id);
        // 组成setmealWithPicVO后返回
        SetmealWithPicVO setmealWithPicVO = new SetmealWithPicVO();
        BeanUtils.copyProperties(setmeal, setmealWithPicVO);
        setmealWithPicVO.setSetmealDishes(dishWithPics);
        return setmealWithPicVO;
    }

}
