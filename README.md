# 傅浩东の代码宝藏

一个基于纯静态 **HTML / CSS / JavaScript** 构建的个人博客，记录 Java 后端开发、数据库优化、全栈实践与学习思考。视觉风格参考 [XingHuiSamaの宝藏之地](https://www.xinghuisama.top)（深空暗色主题 + 衬线标题 + 金/琥珀主色 + 宝石色点缀 + 星空 Hero + 漂浮装饰）。

## 特性

- 🌌 **深空主题**：默认暗色「流萤飞舞的夜空」，可一键切换到温暖羊皮纸浅色；主题偏好记忆在本地。
- 🏠 **博客首页**：星空 Hero（文学化标语 + 统计 + 漂浮装饰 / 流萤）+ 最新文章流 + 侧边栏（关于/分类/标签/统计）。
- 📚 **文章列表页**：按分类 / 标签筛选，支持 URL 参数 `?cat=` / `?tag=`（侧边栏直达）。
- 🤝 **友链页**：友链交换入口，欢迎同好互换链接。
- 🙋 **关于页**：完整简历（简介 / 技能 / 实习 / 项目 / 教育荣誉 / 联系）。
- 📊 **页脚运行时**：「本站已稳定运行」实时计数器 + 技术栈徽章 + 装饰。
- 📱 响应式布局 + 移动端菜单；✨ 滚动揭示动画、导航高亮、回到顶部。

## 目录结构

```
.
├── index.html            # 博客首页（星空 Hero + 文章流 + 侧边栏）
├── about.html            # 关于 / 简历
├── articles.html         # 文章列表（分类 / 标签筛选）
├── links.html            # 友链交换
├── assets/
│   ├── css/style.css     # 深空主题样式与变量
│   └── js/main.js        # 主题 / 菜单 / 动画 / 导航 / 筛选 / 运行时计数
├── posts/                # 博客文章（静态 HTML，8 篇）
├── .nojekyll             # 禁用 GitHub Pages 的 Jekyll 处理
└── README.md
```

## 本地预览

直接用浏览器打开 `index.html` 即可；或在项目根目录启动静态服务器：

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

- **改内容**：编辑 `index.html` / `about.html` / `articles.html` / `links.html` 与 `posts/` 下的文章。
- **改主色 / 主题**：编辑 `assets/css/style.css` 顶部的 `--accent`（宝藏金）与 `--gem-*` 宝石色变量。
- **加文章**：复制 `posts/` 下任一 HTML 文件，修改内容后，在 `index.html` 与 `articles.html` 的 `#postFeed` 中各加一张卡片（带上 `data-category` 与 `data-tags`），并在侧边栏分类/标签处同步数量即可。
- **改站点启航日**：编辑 `assets/js/main.js` 末尾 `LAUNCH` 常量，用于页脚运行时计数器。
- **交换友链**：在 `links.html` 的「我的朋友们」网格中添加卡片，并在「申请友链」处更新联系方式。
