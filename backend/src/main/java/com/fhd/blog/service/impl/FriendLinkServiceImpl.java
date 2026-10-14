package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.dto.FriendLinkDTO;
import com.fhd.blog.entity.FriendLink;
import com.fhd.blog.mapper.FriendLinkMapper;
import com.fhd.blog.service.FriendLinkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Override
    public List<FriendLink> listSorted() {
        return this.list(new LambdaQueryWrapper<FriendLink>().orderByAsc(FriendLink::getSort).orderByDesc(FriendLink::getCreateTime));
    }

    @Override
    public Long createLink(FriendLinkDTO dto) {
        FriendLink link = new FriendLink();
        link.setName(dto.getName());
        link.setDescription(dto.getDescription());
        link.setUrl(dto.getUrl());
        link.setAvatar(dto.getAvatar());
        link.setSort(dto.getSort());
        this.save(link);
        return link.getId();
    }

    @Override
    public void updateLink(FriendLinkDTO dto) {
        FriendLink link = this.getById(dto.getId());
        if (link == null) throw new RuntimeException("友链不存在");
        link.setName(dto.getName());
        link.setDescription(dto.getDescription());
        link.setUrl(dto.getUrl());
        link.setAvatar(dto.getAvatar());
        link.setSort(dto.getSort());
        this.updateById(link);
    }
}
