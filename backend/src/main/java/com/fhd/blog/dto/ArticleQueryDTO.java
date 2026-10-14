package com.fhd.blog.dto;

import lombok.Data;

@Data
public class ArticleQueryDTO {
    private Long categoryId;
    private String category;   // 按分类名筛选（公开端使用）
    private String tag;        // 按标签名筛选（公开端使用）
    private String keyword;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
