package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.Article;
import com.fhd.blog.entity.Category;
import com.fhd.blog.mapper.CategoryMapper;
import com.fhd.blog.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private com.fhd.blog.mapper.ArticleMapper articleMapper;

    @Override
    public List<Map<String, Object>> listWithCount() {
        List<Category> categories = this.list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category c : categories) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("description", c.getDescription());
            long count = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, c.getId()).eq(Article::getStatus, 1));
            map.put("count", count);
            result.add(map);
        }
        return result;
    }
}
