package com.fhd.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhd.blog.common.PageResult;
import com.fhd.blog.dto.ArticleDTO;
import com.fhd.blog.dto.ArticleQueryDTO;
import com.fhd.blog.entity.Article;
import com.fhd.blog.entity.ArticleTag;
import com.fhd.blog.entity.Category;
import com.fhd.blog.mapper.ArticleMapper;
import com.fhd.blog.mapper.ArticleTagMapper;
import com.fhd.blog.mapper.CategoryMapper;
import com.fhd.blog.mapper.TagMapper;
import com.fhd.blog.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ArticleTagMapper articleTagMapper;
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public PageResult<Article> pageArticles(ArticleQueryDTO query) {
        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery(Article.class);
        if (query.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, query.getCategoryId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Article::getTitle, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Article::getStatus, query.getStatus());
        } else {
            wrapper.eq(Article::getStatus, 1);
        }
        wrapper.orderByDesc(Article::getCreateTime);

        long page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        long size = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        IPage<Article> ipage = this.page(new Page<>(page, size), wrapper);
        List<Article> records = ipage.getRecords();
        // 填充分类名与标签
        for (Article a : records) {
            if (a.getCategoryId() != null) {
                Category c = categoryMapper.selectById(a.getCategoryId());
                if (c != null) a.setCategoryName(c.getName());
            }
            List<Tag> tags = baseMapper.selectTagsByArticleId(a.getId());
            a.setTags(tags);
        }
        return new PageResult<>(ipage.getTotal(), page, size, records);
    }

    @Override
    public List<Article> listLatest(int limit) {
        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery(Article.class)
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getCreateTime)
                .last("LIMIT " + limit);
        List<Article> list = this.list(wrapper);
        for (Article a : list) {
            List<Tag> tags = baseMapper.selectTagsByArticleId(a.getId());
            a.setTags(tags);
        }
        return list;
    }

    @Override
    public Article getDetail(Long id) {
        Article a = this.getById(id);
        if (a == null) return null;
        if (a.getCategoryId() != null) {
            Category c = categoryMapper.selectById(a.getCategoryId());
            if (c != null) a.setCategoryName(c.getName());
        }
        a.setTags(baseMapper.selectTagsByArticleId(a.getId()));
        return a;
    }

    @Override
    public void incrementViews(Long id) {
        baseMapper.incrementViews(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArticle(ArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setCoverUrl(dto.getCoverUrl());
        article.setContent(dto.getContent());
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus());
        article.setViews(0);
        this.save(article);
        bindTags(article.getId(), dto.getTagIds());
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleDTO dto) {
        Article article = this.getById(dto.getId());
        if (article == null) throw new RuntimeException("文章不存在");
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setCoverUrl(dto.getCoverUrl());
        article.setContent(dto.getContent());
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus());
        this.updateById(article);
        // 重建标签关系
        articleTagMapper.delete(Wrappers.lambdaQuery(ArticleTag.class).eq(ArticleTag::getArticleId, article.getId()));
        bindTags(article.getId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        this.removeById(id);
        articleTagMapper.delete(Wrappers.lambdaQuery(ArticleTag.class).eq(ArticleTag::getArticleId, id));
    }

    private void bindTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        List<ArticleTag> list = new ArrayList<>();
        for (Long tagId : tagIds) {
            ArticleTag at = new ArticleTag();
            at.setArticleId(articleId);
            at.setTagId(tagId);
            list.add(at);
        }
        for (ArticleTag at : list) {
            articleTagMapper.insert(at);
        }
    }
}
