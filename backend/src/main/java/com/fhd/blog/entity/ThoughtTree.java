package com.fhd.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("thought_tree")
public class ThoughtTree {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String tags;
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
