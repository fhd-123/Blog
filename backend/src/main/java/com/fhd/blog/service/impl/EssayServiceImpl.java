package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.Essay;
import com.fhd.blog.mapper.EssayMapper;
import com.fhd.blog.service.EssayService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EssayServiceImpl extends ServiceImpl<EssayMapper, Essay> implements EssayService {
    @Override
    public List<Essay> listSorted() {
        return this.list(new LambdaQueryWrapper<Essay>().orderByAsc(Essay::getSort).orderByDesc(Essay::getCreateTime));
    }
}
