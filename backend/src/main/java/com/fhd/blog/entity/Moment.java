package com.fhd.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("moment")
public class Moment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private String author;
    private Integer likes;
    private Integer comments;
    private LocalDateTime time;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
