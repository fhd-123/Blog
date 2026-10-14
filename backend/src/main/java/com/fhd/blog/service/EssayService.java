package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.entity.Essay;

import java.util.List;

public interface EssayService extends IService<Essay> {
    List<Essay> listSorted();

    List<Essay> search(String keyword);
}
