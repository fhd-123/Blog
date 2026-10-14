package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.entity.SiteProfile;
import com.fhd.blog.mapper.SiteProfileMapper;
import com.fhd.blog.service.SiteProfileService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiteProfileServiceImpl extends ServiceImpl<SiteProfileMapper, SiteProfile> implements SiteProfileService {

    @Override
    public Map<String, String> getAll() {
        Map<String, String> map = new LinkedHashMap<>();
        List<SiteProfile> list = this.list(new LambdaQueryWrapper<SiteProfile>().orderByAsc(SiteProfile::getId));
        for (SiteProfile p : list) {
            map.put(p.getKey(), p.getValue());
        }
        return map;
    }

    @Override
    public void saveOrUpdateProfile(String key, String value) {
        SiteProfile one = this.getOne(new LambdaQueryWrapper<SiteProfile>().eq(SiteProfile::getKey, key));
        if (one == null) {
            one = new SiteProfile();
            one.setKey(key);
        }
        one.setValue(value);
        this.saveOrUpdate(one);
    }

    @Override
    public void saveBatchProfile(List<SiteProfile> list) {
        for (SiteProfile p : list) {
            saveOrUpdateProfile(p.getKey(), p.getValue());
        }
    }
}
