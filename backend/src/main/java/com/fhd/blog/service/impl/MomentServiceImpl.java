package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.Moment;
import com.fhd.blog.mapper.MomentMapper;
import com.fhd.blog.service.MomentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {
    @Override
    public List<Moment> listSorted() {
        return this.list(new LambdaQueryWrapper<Moment>().orderByDesc(Moment::getTime));
    }
}
