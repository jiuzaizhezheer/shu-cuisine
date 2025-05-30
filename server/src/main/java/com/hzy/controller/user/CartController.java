package com.hzy.controller.user;

import com.hzy.dto.CartDTO;
import com.hzy.entity.Cart;
import com.hzy.result.Result;
import com.hzy.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result add(@RequestBody CartDTO cartDTO){
        log.info("将如下菜品/套餐添加进购物车：{}", cartDTO);
        cartService.add(cartDTO);
        return Result.success();
    }

    @PutMapping("/sub")
    public Result sub(@RequestBody CartDTO cartDTO){
        log.info("将如下菜品/套餐数量减一：{}", cartDTO);
        cartService.sub(cartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Cart>> getList(){
        log.info("拿到当前用户的购物车列表");
        List<Cart> cartList = cartService.getList();
        return Result.success(cartList);
    }

    @DeleteMapping("/clean")
    public Result cleanCart(){
        log.info("清空购物车");
        cartService.clean();
        return Result.success();
    }
}
