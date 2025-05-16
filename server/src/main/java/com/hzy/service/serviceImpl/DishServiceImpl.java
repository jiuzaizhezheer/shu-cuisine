package com.hzy.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hzy.constant.MessageConstant;
import com.hzy.constant.StatusConstant;
import com.hzy.dto.DishDTO;
import com.hzy.dto.DishPageDTO;
import com.hzy.entity.Dish;
import com.hzy.entity.DishFlavor;
import com.hzy.entity.SetmealDish;
import com.hzy.exception.DeleteNotAllowedException;
import com.hzy.mapper.DishFlavorMapper;
import com.hzy.mapper.DishMapper;
import com.hzy.mapper.SetmealDishMapper;
import com.hzy.result.PageResult;
import com.hzy.service.DishService;
import com.hzy.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDishWithFlavor(DishDTO dishDTO) {
        // 不仅要向dish表添加数据，还要向dish_flavor表添加口味数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.ENABLE);
        dishMapper.addDish(dish);
        log.info("添加菜品成功");
        //TODO  由于在动态sql中，用了useGeneralKeys=true，因此会在插入数据后自动返回该行数据的主键id，
        // 并且使用keyProperty="id"，表示将返回的主键值赋值给dish的id属性，下面就可以dish.getId()获取到id
        Integer dishId = dish.getId();
        // 有了DTO中的flavors，加上上面的dish_id，就可以批量插入口味数据到dish_flavor表了

        // 1. 先拿到口味列表
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        //若存在再考虑插入
        if (flavorList != null && !flavorList.isEmpty()) {
            // 2. 再遍历这些口味，每条口味信息都加上dish_id字段，这样相当于DishFlavor就有id,name,value,dish_id四个完整字段了
            flavorList.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            // 3. 最后批量插入口味数据，动态sql中需要根据,分割，foreach批量插入
            dishFlavorMapper.insertBatch(flavorList);
        }

    }

    /**
     * 根据条件page信息分页查询菜品
     * @param dishPageDTO
     * @return
     */
    public PageResult<Dish> getPageList(DishPageDTO dishPageDTO) {
        PageHelper.startPage(dishPageDTO.getPage(), dishPageDTO.getPageSize());
        Page<Dish> dishList = dishMapper.getPageList(dishPageDTO);
        return new PageResult<>(dishList.getTotal(), dishList.getResult());
    }

    /**
     * 根据id查询对应的dish和对应的flavors
     * @param id
     * @return
     */
    public DishVO getDishWithFlavorById(Integer id) {
        // 使用 useGenerateKey 和 keyProperty 来返回对应的id
        Dish dish = dishMapper.getById(id);
        // 根据id查询对应的口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 更新菜品和对应口味数据
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 先根据id更新菜品数据
        dishMapper.update(dish);
        // 再根据where dishId=菜品id，去批量更新对应的口味数据
        Integer dishId = dishDTO.getId();
        // 原来的口味和新的口味的行数据量可能不一样，不能直接更新，只能批量删除再批量插入
        // 1. 根据dishId删除之前的记录
        dishFlavorMapper.deleteByDishId(dishId);
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        if (flavorList != null && !flavorList.isEmpty()){
            flavorList.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            // 2. 再批量插入口味数据
            dishFlavorMapper.insertBatch(flavorList);
        }
    }

    /**
     * 根据菜品id列表，批量删除菜品
     * @param ids
     */
    //TODO 批量删除注意事项
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Integer> ids) {
        log.info("要删除的菜品id列表，{}", ids);
        // 1. 查询该ids的所有菜品，如果有任何一个在起售，则抛异常表示不能删除
        List<Dish> dishList = dishMapper.getByIds(ids);
        dishList.forEach(dish -> {
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeleteNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        // 2. 遍历所有菜品，如果有关联套餐也不能删除
        List<SetmealDish> setmealDishList = setmealDishMapper.getSetmealDishByDishIds(ids);
        if (setmealDishList != null && !setmealDishList.isEmpty()){
            throw new DeleteNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 可以批量删除菜品和对应口味数据了
        dishMapper.deleteBatchByIds(ids);
        dishFlavorMapper.deleteBatchByDishIds(ids);
    }

    /**
     * 根据id修改起售停售状态
     * @param id
     */
    public void onOff(Integer id) {
        dishMapper.onOff(id);
    }

    /**
     * 获取对应分类下的所有菜品，包括对应口味
     * @param id
     * @return
     */
    @Override
    public List<DishVO> getDishWithFlavorByCategoryId(Integer id) {
        // 用户端除了分类条件限制，且只能展示起售中的商品，因此还有status限制
        Dish dish = new Dish();
        dish.setCategoryId(id);
        dish.setStatus(StatusConstant.ENABLE);

        List<Dish> dishList = dishMapper.getList(dish);
        if(dishList == null || dishList.isEmpty()){
            return  new ArrayList<>();
        }
        List<DishVO> dishVOList = dishList.stream().map(x -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(x, dishVO);
            return dishVO;
        }).toList();
        //获取所有dishId
        List<Integer> dishIdList = dishList.stream().map(Dish::getId).toList();
        //获取可能的口味
        //TODO 先查询数据库，再在service里面分组，再赋值给dishVOList
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getDishFlavorByDishIds(dishIdList);
        //先分组
        Map<Integer, List<DishFlavor>> dishFlavorMap = dishFlavorList.stream().collect(Collectors.groupingBy(DishFlavor::getDishId));
        //再遍历赋值
        for (DishVO dishVO : dishVOList) {
            // TODO 妙啊，防止空指针异常
            dishVO.setFlavors(dishFlavorMap.getOrDefault(dishVO.getId(), Collections.emptyList()));
        }
        return dishVOList;
    }

}
