# 傅浩东的个人博客

一个基于纯静态 **HTML / CSS / JavaScript** 构建的个人博客，记录 Java 后端开发、数据库优化、全栈实践与学习思考。灵感来自个人博客类站点，包含：

- 🏠 博客首页（Hero + 最新文章流 + 侧边栏：关于/分类/标签/统计）
- 📚 文章列表页（按分类 / 标签筛选，支持 URL 参数 `?cat=` / `?tag=`）
- 🙋 关于页（完整简历：简介 / 技能 / 实习 / 项目 / 教育荣誉 / 联系）
- 🌗 亮 / 暗主题切换（自动记忆，首次跟随系统）
- 📱 响应式布局 + 移动端菜单
- ✨ 滚动揭示动画、导航高亮、回到顶部
- 📝 多篇技术文章（`posts/` 目录，可自行扩充）

## 目录结构

```
.
├── index.html            # 博客首页（文章流 + 侧边栏）
├── about.html            # 关于 / 简历
├── articles.html         # 文章列表（分类 / 标签筛选）
├── assets/
│   ├── css/style.css     # 样式与主题变量
│   └── js/main.js        # 主题 / 菜单 / 动画 / 导航 / 文章筛选
├── posts/                # 博客文章（静态 HTML）
│   ├── springboot-cache.html
│   ├── mysql-index.html
│   ├── jwt-auth.html
│   ├── vue3-frontend.html
│   ├── graduation-review.html
│   ├── jiangfu-concurrency.html
│   ├── git-maven-workflow.html
│   └── se-learning-path.html
├── .nojekyll             # 禁用 GitHub Pages 的 Jekyll 处理
└── README.md
```

## 本地预览

直接用浏览器打开 `index.html` 即可；或在项目根目录启动一个静态服务器：

```bash
python -m http.server 8080
# 访问 http://localhost:8080
```

## 部署到 GitHub Pages

仓库已启用 GitHub Pages（分支 `main`、根目录 `/`，构建类型 legacy）。推送后通常 1 分钟内自动更新：

```bash
git add .
git commit -m "update"
git push
```

线上地址：**https://fhd-123.github.io/Blog/**

## 自定义

- **改内容**：编辑 `index.html` / `about.html` / `articles.html` 与 `posts/` 下的文章。
- **改主色**：编辑 `assets/css/style.css` 顶部的 `--accent` 变量。
- **加文章**：复制 `posts/` 下任一 HTML 文件，修改内容后，在 `index.html` 与 `articles.html` 的 `#postFeed` 中各加一张卡片（带上 `data-category` 与 `data-tags`），并在侧边栏分类/标签处同步数量即可。
- **加分类/标签**：在两张列表页的筛选栏、侧边栏与文章卡片上保持一致。
