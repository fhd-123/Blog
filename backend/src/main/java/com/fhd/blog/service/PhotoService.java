package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.entity.Photo;

import java.util.List;

public interface PhotoService extends IService<Photo> {
    List<Photo> listSorted();
}
