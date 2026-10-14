package com.fhd.blog.controller;

import com.fhd.blog.common.Result;
import com.fhd.blog.dto.*;
import com.fhd.blog.entity.Article;
import com.fhd.blog.entity.Category;
import com.fhd.blog.entity.Comment;
import com.fhd.blog.entity.Essay;
import com.fhd.blog.entity.FriendLink;
import com.fhd.blog.entity.Moment;
import com.fhd.blog.entity.Music;
import com.fhd.blog.entity.Photo;
import com.fhd.blog.entity.Project;
import com.fhd.blog.entity.SiteProfile;
import com.fhd.blog.entity.Tag;
import com.fhd.blog.entity.ThoughtTree;
import com.fhd.blog.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;
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

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        String token = adminService.login(dto.getUsername(), dto.getPassword());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", dto.getUsername());
        return Result.success(data);
    }

    // ---------- 文章 ----------
    @GetMapping("/articles")
    public Result<?> listArticles(ArticleQueryDTO query) {
        query.setStatus(null); // 后台列出全部
        return Result.success(articleService.pageArticles(query));
    }

    @GetMapping("/articles/{id}")
    public Result<Article> articleDetail(@PathVariable Long id) {
        return Result.success(articleService.getDetail(id));
    }

    @PostMapping("/articles")
    public Result<Long> createArticle(@RequestBody ArticleDTO dto) {
        return Result.success(articleService.createArticle(dto));
    }

    @PutMapping("/articles")
    public Result<Void> updateArticle(@RequestBody ArticleDTO dto) {
        articleService.updateArticle(dto);
        return Result.success();
    }

    @DeleteMapping("/articles/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    // ---------- 分类 ----------
    @GetMapping("/categories")
    public Result<?> listCategories() {
        return Result.success(categoryService.list());
    }

    @PostMapping("/categories")
    public Result<Void> createCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping("/categories")
    public Result<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }

    // ---------- 标签 ----------
    @GetMapping("/tags")
    public Result<?> listTags() {
        return Result.success(tagService.list());
    }

    @PostMapping("/tags")
    public Result<Void> createTag(@RequestBody Tag tag) {
        tagService.save(tag);
        return Result.success();
    }

    @DeleteMapping("/tags/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.removeById(id);
        return Result.success();
    }

    // ---------- 友链 ----------
    @GetMapping("/friends")
    public Result<?> listFriends() {
        return Result.success(friendLinkService.list());
    }

    @PostMapping("/friends")
    public Result<Long> createFriend(@RequestBody FriendLinkDTO dto) {
        return Result.success(friendLinkService.createLink(dto));
    }

    @PutMapping("/friends")
    public Result<Void> updateFriend(@RequestBody FriendLinkDTO dto) {
        friendLinkService.updateLink(dto);
        return Result.success();
    }

    @DeleteMapping("/friends/{id}")
    public Result<Void> deleteFriend(@PathVariable Long id) {
        friendLinkService.removeById(id);
        return Result.success();
    }

    // ---------- 评论 ----------
    @GetMapping("/comments")
    public Result<?> listComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentService.pageAll(page, pageSize, status));
    }

    @PutMapping("/comments/{id}/status")
    public Result<Void> setCommentStatus(@PathVariable Long id, @RequestParam Integer status) {
        com.fhd.blog.entity.Comment c = commentService.getById(id);
        if (c == null) return Result.error(404, "评论不存在");
        c.setStatus(status);
        commentService.updateById(c);
        return Result.success();
    }

    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("articleCount", articleService.count());
        map.put("commentCount", commentService.count());
        map.put("friendCount", friendLinkService.count());
        map.put("photoCount", photoService.count());
        map.put("chatterCount", essayService.count());
        return Result.success(map);
    }

    // ===================== 站点资料 =====================
    @GetMapping("/profile")
    public Result<Map<String, String>> profile() {
        return Result.success(siteProfileService.getAll());
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody List<SiteProfile> list) {
        siteProfileService.saveBatchProfile(list);
        return Result.success();
    }

    // ===================== 项目 =====================
    @GetMapping("/projects")
    public Result<?> listProjects() { return Result.success(projectService.list()); }

    @PostMapping("/projects")
    public Result<Long> createProject(@RequestBody Project p) { projectService.save(p); return Result.success(p.getId()); }

    @PutMapping("/projects")
    public Result<Void> updateProject(@RequestBody Project p) { projectService.updateById(p); return Result.success(); }

    @DeleteMapping("/projects/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) { projectService.removeById(id); return Result.success(); }

    // ===================== 照片墙 =====================
    @GetMapping("/photos")
    public Result<?> listPhotos() { return Result.success(photoService.list()); }

    @PostMapping("/photos")
    public Result<Long> createPhoto(@RequestBody Photo p) { photoService.save(p); return Result.success(p.getId()); }

    @PutMapping("/photos")
    public Result<Void> updatePhoto(@RequestBody Photo p) { photoService.updateById(p); return Result.success(); }

    @DeleteMapping("/photos/{id}")
    public Result<Void> deletePhoto(@PathVariable Long id) { photoService.removeById(id); return Result.success(); }

    // ===================== 音乐 =====================
    @GetMapping("/musics")
    public Result<?> listMusics() { return Result.success(musicService.list()); }

    @PostMapping("/musics")
    public Result<Long> createMusic(@RequestBody Music m) { musicService.save(m); return Result.success(m.getId()); }

    @PutMapping("/musics")
    public Result<Void> updateMusic(@RequestBody Music m) { musicService.updateById(m); return Result.success(); }

    @DeleteMapping("/musics/{id}")
    public Result<Void> deleteMusic(@PathVariable Long id) { musicService.removeById(id); return Result.success(); }

    // ===================== 灵境 =====================
    @GetMapping("/tree")
    public Result<?> listTree() { return Result.success(thoughtTreeService.list()); }

    @PostMapping("/tree")
    public Result<Long> createTree(@RequestBody ThoughtTree t) { thoughtTreeService.save(t); return Result.success(t.getId()); }

    @PutMapping("/tree")
    public Result<Void> updateTree(@RequestBody ThoughtTree t) { thoughtTreeService.updateById(t); return Result.success(); }

    @DeleteMapping("/tree/{id}")
    public Result<Void> deleteTree(@PathVariable Long id) { thoughtTreeService.removeById(id); return Result.success(); }

    // ===================== 说说 =====================
    @GetMapping("/moments")
    public Result<?> listMoments() { return Result.success(momentService.list()); }

    @PostMapping("/moments")
    public Result<Long> createMoment(@RequestBody Moment m) { momentService.save(m); return Result.success(m.getId()); }

    @PutMapping("/moments")
    public Result<Void> updateMoment(@RequestBody Moment m) { momentService.updateById(m); return Result.success(); }

    @DeleteMapping("/moments/{id}")
    public Result<Void> deleteMoment(@PathVariable Long id) { momentService.removeById(id); return Result.success(); }

    // ===================== 杂谈 =====================
    @GetMapping("/essays")
    public Result<?> listEssays() { return Result.success(essayService.list()); }

    @PostMapping("/essays")
    public Result<Long> createEssay(@RequestBody Essay e) { essayService.save(e); return Result.success(e.getId()); }

    @PutMapping("/essays")
    public Result<Void> updateEssay(@RequestBody Essay e) { essayService.updateById(e); return Result.success(); }

    @DeleteMapping("/essays/{id}")
    public Result<Void> deleteEssay(@PathVariable Long id) { essayService.removeById(id); return Result.success(); }
}
