package com.fhd.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentDTO {
    @NotNull(message = "文章 id 不能为空")
    private Long articleId;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String email;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
