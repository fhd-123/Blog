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

    @Override
    public List<Photo> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new java.util.ArrayList<>();
        LambdaQueryWrapper<Photo> q = new LambdaQueryWrapper<Photo>()
                .like(Photo::getCaption, keyword)
                .or().like(Photo::getEmoji, keyword)
                .orderByDesc(Photo::getCreateTime);
        q.last("LIMIT 8");
        return this.list(q);
    }
}
