package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.Music;
import com.fhd.blog.mapper.MusicMapper;
import com.fhd.blog.service.MusicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    @Override
    public List<Music> listSorted() {
        return this.list(new LambdaQueryWrapper<Music>().orderByAsc(Music::getSort).orderByDesc(Music::getCreateTime));
    }
}
