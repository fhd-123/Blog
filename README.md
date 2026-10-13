# 傅浩东的个人博客

一个基于纯静态 **HTML / CSS / JavaScript** 构建的个人博客与技术简历站点，灵感来自个人博客类站点，包含：

- 🏠 首页 Hero（关于 / 技能 / 实习经历 / 项目经历 / 教育荣誉 / 博客 / 联系）
- 🌗 亮 / 暗主题切换（自动记忆，首次跟随系统）
- 📱 响应式布局 + 移动端菜单
- ✨ 滚动揭示动画、导航高亮、回到顶部
- 📝 示例技术文章（`posts/` 目录，可自行扩充）

## 目录结构

```
.
├── index.html              # 主页面（简历 + 博客首页）
├── assets/
│   ├── css/style.css       # 样式与主题变量
│   └── js/main.js          # 主题 / 菜单 / 动画 / 导航逻辑
├── posts/                  # 博客文章（静态 HTML）
│   ├── springboot-cache.html
│   ├── mysql-index.html
│   └── jwt-auth.html
└── README.md
```

## 本地预览

直接用浏览器打开 `index.html` 即可；或在项目根目录启动一个静态服务器：

```bash
# Python
python -m http.server 8080
# 然后访问 http://localhost:8080
```

## 部署到 GitHub Pages

1. 将本仓库推送到 GitHub（见下方命令）。
2. 在仓库 **Settings → Pages** 中，Source 选择 `main` 分支、`/ (root)` 目录，保存。
3. 等待片刻，访问 `https://<用户名>.github.io/<仓库名>/`。

### 推送到 GitHub

```bash
git init
git add .
git commit -m "feat: 初始化个人博客站点"
git branch -M main
git remote add origin https://github.com/fhd-123/Blog.git
git push -u origin main
```

> 若推送提示认证失败，请使用 GitHub **Personal Access Token**（Settings → Developer settings → Fine-grained PAT）代替密码，或配置 SSH key。

## 自定义

- 修改 `index.html` 中的文字即可更新简历内容。
- 主题主色：编辑 `assets/css/style.css` 顶部的 `--accent` 变量。
- 新增文章：复制 `posts/` 下任一 HTML 文件，修改内容后在 `index.html` 的「博客文章」区块添加卡片链接。
