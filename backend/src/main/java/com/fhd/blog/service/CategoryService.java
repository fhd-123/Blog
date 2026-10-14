package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.dto.ArticleQueryDTO;
import com.fhd.blog.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {
    List<Map<String, Object>> listWithCount();
}
