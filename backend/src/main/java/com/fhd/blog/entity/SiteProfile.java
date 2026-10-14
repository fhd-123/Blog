package com.fhd.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("site_profile")
public class SiteProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** key/value 为 MySQL 保留字，必须加反引号转义，否则自动生成的 SQL 语法错误 */
    @TableField("`key`")
    private String key;
    @TableField("`value`")
    private String value;
}
