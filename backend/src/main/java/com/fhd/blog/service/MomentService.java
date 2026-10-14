package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.entity.Moment;

import java.util.List;

public interface MomentService extends IService<Moment> {
    List<Moment> listSorted();
}
