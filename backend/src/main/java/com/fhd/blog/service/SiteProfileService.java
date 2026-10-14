package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.entity.SiteProfile;

import java.util.List;
import java.util.Map;

public interface SiteProfileService extends IService<SiteProfile> {
    Map<String, String> getAll();
    void saveOrUpdateProfile(String key, String value);
    void saveBatchProfile(List<SiteProfile> list);
}
