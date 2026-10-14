package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.ArticleTag;
import com.fhd.blog.entity.Tag;
import com.fhd.blog.mapper.ArticleTagMapper;
import com.fhd.blog.mapper.TagMapper;
import com.fhd.blog.service.TagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Override
    public List<Map<String, Object>> listWithCount() {
        List<Tag> tags = this.list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tag t : tags) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("name", t.getName());
            long count = articleTagMapper.selectCount(
                    new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, t.getId()));
            map.put("count", count);
            result.add(map);
        }
        return result;
    }
}
