package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.common.PageResult;
import com.fhd.blog.dto.CommentDTO;
import com.fhd.blog.entity.Comment;

public interface CommentService extends IService<Comment> {
    Long addComment(CommentDTO dto);

    PageResult<Comment> pageComments(Long articleId, int page, int pageSize);

    PageResult<Comment> pageAll(int page, int pageSize, Integer status);
}
