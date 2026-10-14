package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.dto.FriendLinkDTO;
import com.fhd.blog.entity.FriendLink;

import java.util.List;

public interface FriendLinkService extends IService<FriendLink> {
    List<FriendLink> listSorted();
    Long createLink(FriendLinkDTO dto);
    void updateLink(FriendLinkDTO dto);
}
