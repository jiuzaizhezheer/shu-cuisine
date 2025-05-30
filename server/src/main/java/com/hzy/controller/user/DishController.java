package com.hzy.controller.user;

import com.hzy.result.Result;
import com.hzy.service.DishService;
import com.hzy.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@CacheConfig(cacheNames="dishCache")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询该分类下的所有菜品
     *
     * @return
     */
    @GetMapping("/list/{id}")
    @Cacheable(key = "#id")
    public Result<List<DishVO>> getDishList(@PathVariable Integer id) {
        log.info("要查询当前的分类categoryId下的所有商品：{}", id);
        // 构造redis中的key,规则：dish_分类id
        //String key = "dish_" + id;
        // 查询redis中是否存在菜品数据
        //List<DishVO> dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //if (dishes != null && !dishes.isEmpty()) {
            //如果存在，直接返回，无须查询数据库
         //   return Result.success(dishes);
       // }


        List<DishVO> dishes=dishService.getDishWithFlavorByCategoryId(id);

        // 如果不存在，查询数据库，将查询到的数据放入redis中
//        dishes = dishService.getDishesWithFlavorById(dish);
       //redisTemplate.opsForValue().set(key, dishes);
        return Result.success(dishes);
    }

    /**
     * 根据菜品id查询菜品详情和对应口味
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public Result<DishVO> getDish(@PathVariable Integer id) {
        log.info("用户根据菜品id查询菜品详情和对应口味：{}", id);
        DishVO dishVO = dishService.getDishWithFlavorById(id);
        return Result.success(dishVO);
    }

}
