package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.entity.Music;

import java.util.List;

public interface MusicService extends IService<Music> {
    List<Music> listSorted();
}
