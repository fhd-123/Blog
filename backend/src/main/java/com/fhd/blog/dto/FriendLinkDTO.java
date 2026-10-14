package com.fhd.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FriendLinkDTO {
    private Long id;

    @NotBlank(message = "站点名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "链接地址不能为空")
    private String url;

    private String avatar;

    private Integer sort = 0;
}
