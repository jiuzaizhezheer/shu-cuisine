package com.hzy.service;

import com.hzy.dto.CartDTO;
import com.hzy.entity.Cart;

import java.util.List;

public interface CartService {
    void add(CartDTO cartDTO);

    List<Cart> getList();

    void clean();

    void sub(CartDTO cartDTO);
}
