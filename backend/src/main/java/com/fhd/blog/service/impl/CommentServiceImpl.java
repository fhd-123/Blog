package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.common.PageResult;
import com.fhd.blog.dto.CommentDTO;
import com.fhd.blog.entity.Comment;
import com.fhd.blog.mapper.CommentMapper;
import com.fhd.blog.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public Long addComment(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());
        comment.setNickname(dto.getNickname());
        comment.setEmail(dto.getEmail());
        comment.setContent(dto.getContent());
        comment.setStatus(1); // 默认通过（公开博客可改为 0 待审核）
        this.save(comment);
        return comment.getId();
    }

    @Override
    public PageResult<Comment> pageComments(Long articleId, int page, int pageSize) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        IPage<Comment> ipage = this.page(new Page<>(page, pageSize), wrapper);
        return new PageResult<>(ipage.getTotal(), page, pageSize, ipage.getRecords());
    }

    @Override
    public PageResult<Comment> pageAll(int page, int pageSize, Integer status) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>();
        if (status != null) wrapper.eq(Comment::getStatus, status);
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> ipage = this.page(new Page<>(page, pageSize), wrapper);
        return new PageResult<>(ipage.getTotal(), page, pageSize, ipage.getRecords());
    }
}
