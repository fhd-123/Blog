package com.fhd.blog.controller;

import com.fhd.blog.common.PageResult;
import com.fhd.blog.common.Result;
import com.fhd.blog.dto.ArticleQueryDTO;
import com.fhd.blog.dto.CommentDTO;
import com.fhd.blog.entity.Article;
import com.fhd.blog.entity.Category;
import com.fhd.blog.entity.FriendLink;
import com.fhd.blog.entity.Tag;
import com.fhd.blog.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Resource
    private ArticleService articleService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private TagService tagService;
    @Resource
    private FriendLinkService friendLinkService;
    @Resource
    private CommentService commentService;
    @Resource
    private SiteProfileService siteProfileService;
    @Resource
    private ProjectService projectService;
    @Resource
    private PhotoService photoService;
    @Resource
    private MusicService musicService;
    @Resource
    private ThoughtTreeService thoughtTreeService;
    @Resource
    private MomentService momentService;
    @Resource
    private EssayService essayService;

    @GetMapping("/articles")
    public Result<PageResult<Article>> articles(ArticleQueryDTO query) {
        return Result.success(articleService.pageArticles(query));
    }

    @GetMapping("/articles/latest")
    public Result<List<Article>> latest(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(articleService.listLatest(limit));
    }

    @GetMapping("/articles/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleService.getDetail(id);
        if (article == null) return Result.error(404, "文章不存在");
        articleService.incrementViews(id);
        return Result.success(article);
    }

    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> categories() {
        return Result.success(categoryService.listWithCount());
    }

    @GetMapping("/tags")
    public Result<List<Map<String, Object>>> tags() {
        return Result.success(tagService.listWithCount());
    }

    @GetMapping("/friends")
    public Result<List<FriendLink>> friends() {
        return Result.success(friendLinkService.listSorted());
    }

    @PostMapping("/comments")
    public Result<Long> addComment(@RequestBody CommentDTO dto) {
        return Result.success(commentService.addComment(dto));
    }

    @GetMapping("/comments")
    public Result<PageResult<com.fhd.blog.entity.Comment>> comments(
            @RequestParam Long articleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(commentService.pageComments(articleId, page, pageSize));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("articleCount", articleService.count());
        map.put("categoryCount", categoryService.count());
        map.put("tagCount", tagService.count());
        map.put("friendCount", friendLinkService.count());
        map.put("commentCount", commentService.count());
        map.put("photoCount", photoService.count());
        map.put("chatterCount", essayService.count());
        return Result.success(map);
    }

    // ===================== 全站可配置内容 =====================
    @GetMapping("/profile")
    public Result<Map<String, String>> profile() {
        return Result.success(siteProfileService.getAll());
    }

    @GetMapping("/projects")
    public Result<List<Project>> projects() {
        return Result.success(projectService.listVisible());
    }

    @GetMapping("/photos")
    public Result<List<Photo>> photos() {
        return Result.success(photoService.listSorted());
    }

    @GetMapping("/musics")
    public Result<List<Music>> musics() {
        return Result.success(musicService.listSorted());
    }

    @GetMapping("/tree")
    public Result<List<ThoughtTree>> tree() {
        return Result.success(thoughtTreeService.listSorted());
    }

    @GetMapping("/moments")
    public Result<List<Moment>> moments() {
        return Result.success(momentService.listSorted());
    }

    @GetMapping("/essays")
    public Result<List<Essay>> essays() {
        return Result.success(essayService.listSorted());
    }
}
