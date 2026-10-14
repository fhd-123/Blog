package com.fhd.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.fhd.blog.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    /** 列表查询（关联分类名称），支持条件 */
    @Select("SELECT a.*, c.name AS category_name FROM article a "
            + "LEFT JOIN category c ON a.category_id = c.id ${ew.customSqlSegment}")
    List<Article> selectArticleList(@Param(Constants.WRAPPER) Wrapper<Article> wrapper);

    /** 根据文章 id 查询其标签 */
    @Select("SELECT t.* FROM tag t "
            + "LEFT JOIN article_tag at ON t.id = at.tag_id "
            + "WHERE at.article_id = #{articleId}")
    List<com.fhd.blog.entity.Tag> selectTagsByArticleId(@Param("articleId") Long articleId);

    /** 阅读量 +1 */
    @Select("UPDATE article SET views = views + 1 WHERE id = #{id}")
    void incrementViews(@Param("id") Long id);
}
