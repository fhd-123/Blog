package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.ThoughtTree;
import com.fhd.blog.mapper.ThoughtTreeMapper;
import com.fhd.blog.service.ThoughtTreeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThoughtTreeServiceImpl extends ServiceImpl<ThoughtTreeMapper, ThoughtTree> implements ThoughtTreeService {
    @Override
    public List<ThoughtTree> listSorted() {
        return this.list(new LambdaQueryWrapper<ThoughtTree>().orderByAsc(ThoughtTree::getSort).orderByDesc(ThoughtTree::getCreateTime));
    }
}
