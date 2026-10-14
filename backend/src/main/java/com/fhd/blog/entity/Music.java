package com.fhd.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("music")
public class Music {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String artist;
    private String emoji;
    private String mood;
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
