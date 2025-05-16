package com.hzy.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hzy.constant.StatusConstant;
import com.hzy.dto.CategoryDTO;
import com.hzy.dto.CategoryTypePageDTO;
import com.hzy.entity.Category;
import com.hzy.mapper.CategoryMapper;
import com.hzy.result.PageResult;
import com.hzy.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增分类
     * @param categoryDTO
     */
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.ENABLE);  // 默认启用
        categoryMapper.add(category);
    }

    /**
     * 根据type条件分页查询
     * @param categoryTypePageDTO
     * @return
     */
    //TODO 条件分页查询无需构造实体对象，直接传入即可
    public PageResult<Category> getPageList(CategoryTypePageDTO categoryTypePageDTO) {
        PageHelper.startPage(categoryTypePageDTO.getPage(), categoryTypePageDTO.getPageSize());
        Page<Category> pagelist = categoryMapper.getPageList(categoryTypePageDTO);
        return new PageResult<>(pagelist.getTotal(), pagelist.getResult());
    }

    /**
     * 获取所有分类列表
     * @return
     */
    public List<Category> getList(Integer type) {
        List<Category> categoryList = categoryMapper.getList(type);
        return categoryList;
    }

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    public Category getById(Integer id) {
        return categoryMapper.getById(id);
    }

    /**
     * 更新分类信息
     * @param categoryDTO
     */
    //TODO 复制为实体类
    public void udpate(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    public void delete(Integer id) {
        categoryMapper.delete(id);
    }

    /**
     * 分类起售/停售
     */
    public void onOff(Integer id) {
        categoryMapper.onOff(id);
    }
}
