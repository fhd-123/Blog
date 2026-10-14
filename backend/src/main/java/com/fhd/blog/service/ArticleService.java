package com.fhd.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhd.blog.common.PageResult;
import com.fhd.blog.dto.ArticleDTO;
import com.fhd.blog.dto.ArticleQueryDTO;
import com.fhd.blog.entity.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {

    PageResult<Article> pageArticles(ArticleQueryDTO query);

    List<Article> listLatest(int limit);

    Article getDetail(Long id);

    Long createArticle(ArticleDTO dto);

    void updateArticle(ArticleDTO dto);

    void deleteArticle(Long id);

    void incrementViews(Long id);
}
