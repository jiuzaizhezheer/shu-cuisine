package com.hzy.service;

import com.hzy.dto.CategoryDTO;
import com.hzy.dto.CategoryTypePageDTO;
import com.hzy.entity.Category;
import com.hzy.result.PageResult;

import java.util.List;

public interface CategoryService {
    void addCategory(CategoryDTO categoryDTO);

    PageResult<Category> getPageList(CategoryTypePageDTO categoryTypePageDTO);

    List<Category> getList(Integer type);

    Category getById(Integer id);
    void onOff(Integer id);

    void udpate(CategoryDTO categoryDTO);

    void delete(Integer id);

}
