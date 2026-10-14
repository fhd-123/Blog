package com.fhd.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    @TableField(value = "cover_url")
    private String coverUrl;

    private String content;

    @TableField(value = "category_id")
    private Long categoryId;

    /** 非数据库字段：分类名称（列表展示用） */
    @TableField(exist = false)
    private String categoryName;

    /** 非数据库字段：标签列表 */
    @TableField(exist = false)
    private List<Tag> tags;

    private Integer views;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
