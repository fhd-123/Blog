# 傅浩东の代码宝藏 · 个人博客（前后端分离）

一个参考 [XingHuiSamaの宝藏之地](https://www.xinghuisama.top) 视觉风格（深空暗色主题 + 衬线标题 + 金/宝石色点缀 + 星空 Hero）的个人博客，采用 **Spring Boot + Vue3 + Element Plus + MySQL** 前后端分离架构。**全站内容（首页简介、关于/简历、项目、照片墙、音乐、灵境、说说、杂谈）均由后端驱动，可在管理后台直接编辑。**

> 视觉风格仿自 xinghuisama.top，内容来自个人简历（Java 后端方向），种子数据均为示例，可在后台替换。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Spring Boot 2.7 · MyBatis-Plus · MySQL 8 · JWT · Lombok |
| 前端 | Vue 3 · Vue Router · Element Plus · Vite · Axios |
| 部署 | Docker Compose / GitHub Pages（前端） |

## 目录结构

```
.
├── backend/                          # Spring Boot 后端（:8080/api）
│   ├── pom.xml
│   ├── sql/schema.sql                # 建库建表 + 全部种子数据
│   ├── Dockerfile                    # 多阶段构建
│   └── src/main/java/com/fhd/blog/
│       ├── entity/                   # Article/Category/Tag/... 7 张内容表 + 基础表
│       ├── mapper/  service/  controller/
│       ├── dto/  common/  config/  exception/
│       └── resources/application.yml
├── frontend/                         # Vue3 前端
│   ├── package.json  vite.config.js
│   ├── Dockerfile  nginx.conf        # nginx 托管 + 反代 /api
│   └── src/
│       ├── views/                    # Home/Archive/Projects/PhotoWall/Music/Tree/Moments/Chatter/Friends/About
│       ├── views/admin/              # 仪表盘 + 各内容 CRUD + 评论/友链审核
│       ├── api/  router/  styles/  main.js  App.vue
├── docker-compose.yml                # MySQL + 后端 + 前端 一键编排
├── .github/workflows/deploy.yml      # GitHub Pages 自动部署前端
└── README.md
```

## 快速开始

### 1. 数据库

```bash
mysql -u root -p1234
CREATE DATABASE blog CHARACTER SET utf8mb4;
exit
mysql -u root -p1234 blog < backend/sql/schema.sql
```

> 默认账号 `root` / 密码 `1234`；不一致请改 `backend/src/main/resources/application.yml` 的 `spring.datasource.password`。

### 2. 启动后端（JDK 8 + Maven）

```bash
cd backend && mvn spring-boot:run
# 接口：http://localhost:8080/api
```

默认管理员（`application.yml` 中 `blog.admin-username` / `blog.admin-password`）：`admin` / `admin123`。

### 3. 启动前端（Node 16+）

```bash
cd frontend && npm install && npm run dev
# 访问 http://localhost:5173 （Vite 已代理 /api -> localhost:8080）
```

### 4. 构建前端（部署用）

```bash
cd frontend && npm run build   # 产物在 frontend/dist
```

## Docker Compose 一键部署（推荐自托管）

```bash
docker compose up -d --build
```

- MySQL `3306`（`root/1234`，首次自动执行 `schema.sql`）
- 后端容器内 `8080`，对外 `8081`
- 前端 nginx 托管并反代 `/api`，访问 **http://localhost:8080**
- 数据卷 `mysql-data` 持久化；改密码同步改 `docker-compose.yml` 与 `application.yml`

```bash
docker compose down          # 停止，保留数据卷
docker compose down -v       # 停止并清空数据库
```

## GitHub Pages 自动部署前端

仓库已内置 `.github/workflows/deploy.yml`：push `main` 后自动构建并发布 `frontend/dist`。

1. 仓库 **Settings → Pages → Source 选 GitHub Actions**
2. push 后访问 `https://fhd-123.github.io/Blog/`

> 静态前端不含后端。要线上读数据，请把后端部署到公网，再在仓库 **Settings → Variables** 设 `VITE_API_BASE = https://你的域名/api`，工作流构建时注入；未设置时默认同源 `/api`。

## 功能一览

- 🌌 **公开端**：星空 Hero 首页、归档（分类/标签筛选 + 分页）、项目矩阵、照片墙、音乐、灵境（知识树）、说说、杂谈、友链、关于（简历）
- 🔐 **管理端**（`/admin`）：JWT 登录 + 仪表盘 + 文章/分类/标签/站点资料/项目/照片墙/音乐/灵境/说说/杂谈/友链/评论 的全套增删改查
- 🎨 深空主题（可切浅色）、路由过渡与卡片入场动画、响应式布局
- 🗄️ **全站内容后端驱动**：前端不写死任何内容，均由 `/api/public/*` 读取，后台 `/api/admin/*` 维护

## 接口说明

统一返回：`{ code: 200, message: "success", data: ... }`。公共前缀 `/api/public`，管理前缀 `/api/admin`（除 `/admin/login` 外均需 `Authorization: Bearer <token>`）。

主要公开接口：`/public/profile`、`/public/articles`、`/public/projects`、`/public/photos`、`/public/musics`、`/public/tree`、`/public/moments`、`/public/essays`、`/public/friends`、`/public/categories`、`/public/tags`、`/public/stats`。
