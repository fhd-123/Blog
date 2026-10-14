package com.fhd.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("site_profile")
public class SiteProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String key;
    private String value;
}
