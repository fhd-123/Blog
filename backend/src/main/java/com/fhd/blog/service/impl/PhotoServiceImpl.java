package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.Photo;
import com.fhd.blog.mapper.PhotoMapper;
import com.fhd.blog.service.PhotoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {
    @Override
    public List<Photo> listSorted() {
        return this.list(new LambdaQueryWrapper<Photo>().orderByAsc(Photo::getSort).orderByDesc(Photo::getCreateTime));
    }
}
