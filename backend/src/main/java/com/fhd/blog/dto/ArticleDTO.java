package com.fhd.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ArticleDTO {
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String summary;

    private String coverUrl;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Long categoryId;

    private List<Long> tagIds;

    private Integer status = 1;
}
