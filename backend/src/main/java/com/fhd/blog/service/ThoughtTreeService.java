package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.entity.ThoughtTree;

import java.util.List;

public interface ThoughtTreeService extends IService<ThoughtTree> {
    List<ThoughtTree> listSorted();
}
