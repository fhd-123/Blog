package com.fhd.blog.dto;

import lombok.Data;

@Data
public class ArticleQueryDTO {
    private Long categoryId;
    private String tag;
    private String keyword;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
