package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.entity.Project;

import java.util.List;

public interface ProjectService extends IService<Project> {
    List<Project> listVisible();

    List<Project> search(String keyword);
}
