package com.hzy.controller.admin;

import com.hzy.dto.DishDTO;
import com.hzy.dto.DishPageDTO;
import com.hzy.entity.Dish;
import com.hzy.result.PageResult;
import com.hzy.result.Result;
import com.hzy.service.DishService;
import com.hzy.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加菜品
     * @return
     */
    @PostMapping
    @CacheEvict(cacheNames = "dishCache",key  = "#dishDTO.categoryId")
    public Result addDishWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("要添加的菜品信息：{}", dishDTO);
        dishService.addDishWithFlavor(dishDTO);
        //TODO 传统方法操作缓存
//        // 清理缓存数据
//        String key = "dish_" + dishDTO.getCategoryId();
//        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品条件分页查询
     * @param dishPageDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult<Dish>> getPageList(DishPageDTO dishPageDTO){
        log.info("菜品dish分页条件page：{}", dishPageDTO);
        PageResult<Dish> pageResult = dishService.getPageList(dishPageDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询菜品和对应口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getDishWithFlavorById(@PathVariable Integer id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getDishWithFlavorById(id);
        return Result.success(dishVO);
    }

    /**
     * 根据id修改起售停售状态
     * @param id
     * @return
     */
    @PutMapping("/status/{id}")
    @CacheEvict(cacheNames = "dishCache",allEntries = true)
    public Result onOff(@PathVariable Integer id){
        log.info("根据id修改状态，{}", id);
        dishService.onOff(id);
        // 将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 更新菜品以及对应口味
     * @param dishDTO
     * @return
     */
    @PutMapping
    @CacheEvict(cacheNames = "dishCache",key  = "#dishDTO.categoryId")
    public Result updateDishWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("用户传过来的新菜品数据，{}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        // 将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据ids批量删除菜品数据
     * @RequestParam 表示必须要ids参数，否则会报错
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = "dishCache",allEntries = true)
    //TODO 必须使用 @RequestParam注解 的场景，因为Spring MVC 默认不会自动从请求中提取复杂类型的参数（如 List<Integer>）
    // 使用 @RequestParam 明确告诉 Spring 这个参数是从 HTTP 请求的查询参数（Query Parameters）中获取的
    //  当请求类似 ?ids=1,2,3,?ids=1&ids=2&ids=3,?ids=1,2&ids=3这些形式，都可以正确映射到 List<Integer>
    public Result deleteBatch(@RequestParam List<Integer> ids){
        log.info("要删除的菜品id列表，{}", ids);
        dishService.deleteBatch(ids);
        // 将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
//    private void cleanCache(String pattern){
//        Set<String> keys = redisTemplate.keys(pattern);
//        redisTemplate.delete(keys);
//    }
}
