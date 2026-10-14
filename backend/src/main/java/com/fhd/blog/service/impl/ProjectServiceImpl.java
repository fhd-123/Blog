package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.Project;
import com.fhd.blog.mapper.ProjectMapper;
import com.fhd.blog.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Override
    public List<Project> listVisible() {
        return this.list(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, 1)
                .orderByAsc(Project::getSort).orderByDesc(Project::getCreateTime));
    }

    @Override
    public List<Project> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new java.util.ArrayList<>();
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, 1)
                .and(w -> w.like(Project::getTitle, keyword)
                        .or().like(Project::getDescription, keyword)
                        .or().like(Project::getTags, keyword))
                .orderByAsc(Project::getSort);
        q.last("LIMIT 8");
        return this.list(q);
    }
}
