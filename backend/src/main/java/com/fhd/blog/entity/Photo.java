package com.fhd.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("photo")
public class Photo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String caption;
    private String date;
    private String emoji;
    private String gradient;
    private String imageUrl;   // 真实图片地址；填写后优先于 emoji/gradient 展示
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
