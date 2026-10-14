-- ============================================================
-- 傅浩东の代码宝藏 · 数据库初始化脚本
-- MySQL 8.0，账号 root / 密码 1234，数据库名 blog
-- 执行前请先创建数据库：CREATE DATABASE blog CHARACTER SET utf8mb4;
-- ============================================================

CREATE DATABASE IF NOT EXISTS blog CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE blog;

SET FOREIGN_KEY_CHECKS = 0;

-- 分类
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 标签
DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章
DROP TABLE IF EXISTS article;
CREATE TABLE article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500) DEFAULT '',
    cover_url VARCHAR(500) DEFAULT '',
    content LONGTEXT,
    category_id BIGINT DEFAULT NULL,
    views INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1-发布 0-草稿',
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章-标签关系
DROP TABLE IF EXISTS article_tag;
CREATE TABLE article_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    KEY idx_article (article_id),
    KEY idx_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 友链
DROP TABLE IF EXISTS friend_link;
CREATE TABLE friend_link (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) DEFAULT '',
    url VARCHAR(500) NOT NULL,
    avatar VARCHAR(500) DEFAULT '',
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    email VARCHAR(100) DEFAULT '',
    content VARCHAR(1000) NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '1-通过 0-待审核',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_article (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================== 种子数据 =====================

-- 分类（id 固定，便于下方文章映射）
INSERT INTO category (id, name, description) VALUES
(1, '后端实践', 'SpringBoot、缓存、架构等后端实战'),
(2, '数据库', 'MySQL 索引、SQL 优化与存储'),
(3, '安全', 'JWT、SpringSecurity 与鉴权'),
(4, '前端', 'Vue3、组件库与联调'),
(5, '项目复盘', '真实项目的技术取舍与复盘'),
(6, '学习笔记', '学习路线与工程习惯'),
(7, '工具', 'Git、Maven 等协作与构建工具');

-- 标签
INSERT INTO tag (id, name) VALUES
(1, 'SpringBoot'), (2, 'Redis'), (3, 'MySQL'), (4, '索引'),
(5, 'JWT'), (6, 'SpringSecurity'), (7, 'Vue3'), (8, '联调'),
(9, 'Git'), (10, 'Maven'), (11, '毕业设计'), (12, '并发'),
(13, '事务'), (14, '软件工程'), (15, '学习路线'), (16, '本科'),
(17, '协作'), (18, 'CI/CD'), (19, '小程序'), (20, '一致性'),
(21, '权限'), (22, 'Element-Plus'), (23, 'SQL优化');

-- 文章
INSERT INTO article (id, title, summary, content, category_id, views, status) VALUES
(1, '软件工程本科四年，我学到的最重要的几件事', '从课堂到项目，复盘这四年真正影响我工程能力的习惯与方法。',
'<p>四年软件工程本科，课堂教会了我语法与算法，但真正让代码「能上线、能协作」的，是项目里踩过的坑和养成的习惯。</p><p>第一件是「先想清楚再写」：需求拆分、接口约定、异常边界，比闷头敲代码更重要。第二件是版本协作：分支模型、提交规范让多人开发不再互相覆盖。第三件是把数据库当一等公民——索引、事务、慢查询，往往是线上问题的根。</p><p>这些习惯，我在江服快跑、家庭健康平台、芒果记账等项目里反复验证，也成了我求职后端开发最实在的底气。</p>',
6, 128, 1),

(2, 'Git 与 Maven：我的版本协作与构建工作流', '分支模型、提交规范与 Maven 多模块依赖管理的一些实战心得。',
'<p>Git 不是「commit 一下」那么简单。我习惯用 main + feature/* 的分支模型：新功能开分支，自测后再合并；提交信息用「类型: 简述」的约定式格式，方便回溯。</p><p>Maven 方面，多模块项目用父子 POM 管理依赖版本，避免版本漂移；本地用 <code>mvn -pl module -am</code> 按需构建，CI 里跑 <code>mvn verify</code> 保证质量门禁。</p><p>工具链顺了，团队协作的摩擦就少一大半。</p>',
7, 96, 1),

(3, '江服快跑：校园跑腿小程序里的并发与数据一致性', '订单状态流转、声明式事务与缓存如何共同保障多用户场景下的正确性。',
'<p>江服快跑是商业校园服务项目，订单、用户、配送员模块都由我独立完成后端。高并发下最棘手的，是「同一单被多个配送员抢」和「金额与状态不一致」。</p><p>我用 Spring 声明式事务兜住多表操作原子性；对高频读的地址与配送员信息接 Redis 缓存；并为高频查询建立联合索引，把慢 SQL 压下去。</p><p>边界 case（非法状态流转、参数异常）则在接口层统一校验，配合全局异常处理，保证小程序稳定运行。</p>',
5, 142, 1),

(4, '毕业设计复盘：家庭健康协同管理平台', '从需求设计到接口落地，复盘一个完整全栈项目的技术取舍。',
'<p>毕业设计做了基于微信小程序的家庭健康协同管理平台：健康指标录入、用药提醒、异常预警与统计看板。</p><p>权限隔离是重点——不同家庭只能看自家数据，我在业务层做了严格的成员归属校验，配合 JWT 鉴权。</p><p>热点统计写入 Redis 缓存，DB 聚合计算不再每次硬扛；统一异常处理 + 参数校验让联调顺畅，前后端各司其职。</p>',
5, 110, 1),

(5, 'Vue3 + Element-Plus 前后端联调实践', '作为后端如何高效地与前端协作：接口约定、参数校验与异常对接。',
'<p>作为后端，我并非要写整套前端，但要「看得懂接口需求、联得顺数据」。Vue3 + Element-Plus 是我的常用组合。</p><p>联调关键是契约：字段名、状态码、分页结构提前约定；后端用统一 Result 包装响应，前端用 axios 拦截器统一处理错误。</p><p>参数校验失败返回 400 与字段级 message，前端直接映射成表单错误提示，体验丝滑。</p>',
4, 88, 1),

(6, 'SpringBoot + Redis 缓存实战：从穿透到雪崩的防护', '结合项目经验，梳理热点数据缓存设计、过期策略与防护思路。',
'<p>缓存能扛住热点读，但也带来穿透、击穿、雪崩三大坑。我的实践是：热点数据设随机过期时间避免同时失效；空值也缓存短时而防穿透；关键路径加互斥锁防击穿。</p><p>在江服快跑和家庭健康平台里，地址、配送员、家庭统计等高频读都走了 Redis，DB 压力明显下降。</p><p>记住：缓存是「加速层」不是「真相源」，一致性策略要和业务容忍度匹配。</p>',
1, 156, 1),

(7, 'MySQL 慢 SQL 优化：我是如何用联合索引把查询砍掉 30%', '从芒果记账项目出发，讲解多条件查询的索引设计与动态查询封装。',
'<p>芒果记账的多条件查询（时间、类型、金额）最初平均 120ms。分析执行计划后发现筛选字段缺索引，于是建立复合索引（type, date）。</p><p>同时用 MyBatis-Plus 的 QueryWrapper 封装动态条件，避免拼接 SQL；用框架自动填充统一维护时间字段，规避脏数据。</p><p>优化后平均耗时降到 80ms 左右，性能提升约 30%。统计接口再叠加 Redis 过期缓存，聚合开销进一步降低。</p>',
2, 173, 1),

(8, 'JWT + SpringSecurity 实现家庭数据权限隔离', '如何用 JWT 鉴权与业务层权限校验，保证不同家庭只能访问自家数据。',
'<p>JWT 负责「你是谁」，SpringSecurity 负责「你能干什么」。在毕业设计里，我让登录后下发 JWT，过滤器校验签名与过期。</p><p>更关键的是数据权限：每个资源都带 familyId，查询前在业务层校验「当前用户是否属于该家庭」，越权直接拒绝。</p><p>鉴权框架 + 业务归属校验双保险，才能做到真正的家庭数据隔离。</p>',
3, 134, 1);

-- 文章-标签关系
INSERT INTO article_tag (article_id, tag_id) VALUES
(1, 14), (1, 15), (1, 16),
(2, 9), (2, 10), (2, 17), (2, 18),
(3, 12), (3, 13), (3, 20), (3, 19),
(4, 11), (4, 1), (4, 21),
(5, 7), (5, 22), (5, 8),
(6, 1), (6, 2),
(7, 3), (7, 4), (7, 23),
(8, 5), (8, 6), (8, 21);

-- 友链
INSERT INTO friend_link (id, name, description, url, avatar, sort) VALUES
(1, 'XingHuiSamaの宝藏之地', '在代码、学术与分子动力学模拟间穿梭的普通人。', 'https://www.xinghuisama.top', '', 1),
(2, '傅浩东の代码宝藏', 'Java 后端开发与技术笔记，在代码与系统之间收集微光。', 'https://fhd-123.github.io/Blog/', '傅', 2),
(3, '示例友链', '欢迎交换友链，留下你的站点，我们一起在数据海洋里相遇。', 'https://example.com', '', 3);

SET FOREIGN_KEY_CHECKS = 1;

-- ===================== 站点可配置内容（全站后端驱动） =====================

-- 站点资料（关于/首页简介/联系方式等，单行 key=value 形式）
DROP TABLE IF EXISTS site_profile;
CREATE TABLE site_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    `key` VARCHAR(60) NOT NULL,
    `value` TEXT,
    UNIQUE KEY uk_key (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO site_profile (`key`, `value`) VALUES
('site_name', '傅浩东の代码宝藏'),
('slogan', '在代码与系统之间，收集值得收藏的微光'),
('about_intro', '傅浩东，Java 后端开发工程师，专注 Spring 生态、数据库优化与全栈实践。软件工程本科在读，在代码里收集属于自己的宝藏。'),
('contact_email', '3375106693@qq.com'),
('contact_phone', '17870390643'),
('contact_wechat', 'fhd_123'),
('resume_name', '傅浩东'),
('resume_title', 'Java 后端开发工程师'),
('resume_base', '江西南昌 · 本科 · 软件工程'),
('resume_education', '江西服装学院 · 软件工程（本科） · 2022.09 - 2026.06'),
('resume_skills', 'Java / Spring Boot / MyBatis-Plus / MySQL / Redis / SpringSecurity / Vue3 / 小程序'),
('resume_highlights', '毕业设计家庭健康平台 / 江服快跑商业项目 / 芒果记账 / 全国大学生软件创新大赛'),
('about_skills', '[{"t":"Java 后端与框架","d":"扎实 Java SE 基础；精通 Spring、SpringMVC、SpringBoot，熟练 MyBatis / MyBatis-Plus；理解 IOC、AOP 核心原理。","tags":["Java SE","Spring Boot","MyBatis-Plus","IOC/AOP"]},{"t":"数据库与存储","d":"熟练 MySQL，掌握索引设计、慢 SQL 排查与优化；熟练 Redis 缓存与过期策略。","tags":["MySQL","Redis","索引优化","SQL 调优"]},{"t":"安全与接口","d":"掌握 JWT 令牌鉴权，了解 SpringSecurity 权限控制；熟悉接口开发与统一异常处理；熟练 Maven、Git。","tags":["JWT","SpringSecurity","Maven","Git"]},{"t":"前端（辅助）","d":"了解 Vue3、Element-Plus、Vant 组件库，能够看懂前端接口需求，高效完成前后端联调。","tags":["Vue3","Element-Plus","Vant","联调"]}]'),
('about_projects', '[{"title":"江西思诚科技 · Java 开发实习生","time":"2025.06 - 2025.07","role":"江服快跑 校园跑腿小程序","points":["独立承担订单、用户、配送员模块后端接口开发，处理并发与数据一致性。","基于 SpringBoot + MyBatis-Plus 落地订单全套接口；通过联合索引优化高频慢 SQL。","用 Spring 声明式事务保障多表原子性；高频数据接 Redis 缓存。"]},{"title":"家庭健康协同管理平台","time":"2025.09 - 2025.12","role":"个人毕业设计 · 独立后端开发","points":["基于微信小程序的家庭健康管理：健康指标录入、用药提醒、异常预警与统计看板。","实现手机号注册登录、JWT 鉴权；家庭创建、成员邀请与权限隔离。","开发各类健康记录 CRUD，封装健康阈值检测逻辑；统一异常处理与参数校验。"]},{"title":"芒果记账","time":"2024.06 - 2024.09","role":"后端开发 · 账单与统计模块","points":["账单 CRUD，基于 QueryWrapper 封装动态多条件查询（时间/类型/金额）。","对筛选字段建立复合索引，复杂查询平均耗时由 120ms 降至 80ms，性能提升约 30%。","编写 GROUP BY 多维统计 SQL 支撑趋势图表与月报；DB 计算 + Redis 过期缓存。"]}]'),
('about_education', '[{"title":"江西服装学院","desc":"本科 · 软件工程 · 2022 - 2026","tags":["蓝桥杯省赛三等奖","校级润华奖程序设计赛二等奖","校级微电影大赛一等奖"]}]'),
('footer_marquee', '["收集值得收藏的微光","Spring Boot","Vue3","MySQL","Redis","全栈实践"]'),
('footer_badges', '["Spring Boot","Vue3","Element Plus","MySQL","MyBatis-Plus","前后端分离"]'),
('danmaku_list', '["保持热爱，奔赴山海","在代码里收集宝藏","愿每一次提交都有回响","慢一点，也没关系","今天也要好好写代码","万物皆有裂痕，那是光照进来的地方","祝大家少些 Bug，多点 Star","星光不问赶路人"]'),
('friend_apply_format', '站点名称：\n站点地址：\n站点描述：\n头像链接：');

-- 项目（项目矩阵）
DROP TABLE IF EXISTS project;
CREATE TABLE project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    icon VARCHAR(20) DEFAULT '🚀',
    title VARCHAR(120) NOT NULL,
    description VARCHAR(500) DEFAULT '',
    url VARCHAR(500) DEFAULT '',
    tags VARCHAR(255) DEFAULT '',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1-显示 0-隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO project (icon, title, description, url, tags, sort) VALUES
('🚀', 'XingHuiSama の Cloud（博客工程）', '基于 Spring Boot + Vue3 的个人博客，前后端分离架构，含文章、评论、友链与 JWT 管理后台。', 'https://github.com/fhd-123/Blog', 'SpringBoot,MyBatis-Plus,Vue3,MySQL', 1),
('🏥', '家庭健康协同管理平台', '毕业设计（2025.09–2025.12）。微信小程序 + Vue 管理端，健康指标录入、用药提醒、异常预警与统计看板，含家庭权限隔离。', 'https://github.com/fhd-123', 'SpringBoot,SpringSecurity,JWT,Redis,MySQL', 2),
('🏃', '江服快跑 · 校园跑腿小程序', '商业校园服务项目，独立承担订单、用户、配送员模块后端：并发处理、事务一致性、Redis 缓存与慢 SQL 优化。', 'https://github.com/fhd-123', 'SpringBoot,MyBatis-Plus,Redis,MySQL', 3),
('🧮', '芒果记账', '个人记账应用（2024.06–2024.09）。账单 CRUD 与多维度统计看板，复合索引 + Redis 缓存把复杂查询耗时从 120ms 降至 80ms。', 'https://github.com/fhd-123', 'SpringBoot,MyBatis-Plus,MySQL,Redis,Vue3', 4);

-- 照片墙
DROP TABLE IF EXISTS photo;
CREATE TABLE photo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    caption VARCHAR(120) DEFAULT '',
    `date` VARCHAR(20) DEFAULT '',
    emoji VARCHAR(20) DEFAULT '🌄',
    gradient VARCHAR(120) DEFAULT '',
    image_url VARCHAR(500) DEFAULT '',   -- 真实图片地址；填写后优先于 emoji/gradient 展示
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO photo (caption, `date`, emoji, gradient, sort) VALUES
('山脊线日落', '2026-02-14', '🌄', 'linear-gradient(135deg,#6366f1,#8b5cf6)', 1),
('海边的风', '2026-01-30', '🌊', 'linear-gradient(135deg,#0ea5e9,#6366f1)', 2),
('夜色城市', '2026-01-12', '🏙️', 'linear-gradient(135deg,#f43f5e,#f59e0b)', 3),
('林间小径', '2025-12-25', '🌿', 'linear-gradient(135deg,#10b981,#0ea5e9)', 4),
('秋日校园', '2025-11-08', '🍂', 'linear-gradient(135deg,#f59e0b,#e0a93b)', 5),
('初雪清晨', '2025-12-01', '❄️', 'linear-gradient(135deg,#38bdf8,#a78bfa)', 6),
('赛博霓虹', '2026-02-02', '🌃', 'linear-gradient(135deg,#8b5cf6,#f43f5e)', 7),
('星轨延时', '2026-02-20', '✨', 'linear-gradient(135deg,#6366f1,#6366f1)', 8);

-- 音乐
DROP TABLE IF EXISTS music;
CREATE TABLE music (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    artist VARCHAR(120) DEFAULT '',
    emoji VARCHAR(20) DEFAULT '🎧',
    mood VARCHAR(60) DEFAULT '',
    url VARCHAR(500) DEFAULT '',
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO music (name, artist, emoji, mood, url, sort) VALUES
('夜的第七章', '周杰伦', '🎧', '深夜 Coding', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 1),
('Ref:rain', 'Aimer', '🌊', '专注', '', 2),
('群青', 'YOASOBI', '✨', '灵感', '', 3),
('Lemon', '米津玄师', '🍃', '沉淀', '', 4),
('deity', 'BABYMETAL', '🔥', '提神', '', 5),
('海阔天空', 'Beyond', '🌙', '怀旧', '', 6);

-- 灵境（知识树）
DROP TABLE IF EXISTS thought_tree;
CREATE TABLE thought_tree (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(500) DEFAULT '',
    tags VARCHAR(255) DEFAULT '',
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO thought_tree (title, description, tags, sort) VALUES
('Java 并发', '线程池、锁、JMM 与并发容器的踩坑记录。', 'JUC,线程池,锁', 1),
('Spring 生态', 'IoC / AOP / 事务 / 自动装配原理与实战。', 'Spring,SpringBoot,MyBatis', 2),
('数据库优化', '索引、慢 SQL、执行计划与缓存策略。', 'MySQL,Redis,调优', 3),
('前端工程', 'Vue3 组合式 API、Vite 与工程化实践。', 'Vue3,Vite,ElementPlus', 4),
('算法与刷题', 'LeetCode 专题：树、回溯、动态规划。', '算法,题解', 5);

-- 说说
DROP TABLE IF EXISTS moment;
CREATE TABLE moment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(1000) NOT NULL,
    author VARCHAR(60) DEFAULT '傅浩东',
    likes INT DEFAULT 0,
    comments INT DEFAULT 0,
    `time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO moment (content, author, likes, comments, `time`) VALUES
('终于把博客前后端分离跑通了，深空主题真好看 ✨', '傅浩东', 12, 3, '2026-03-01 22:14:00'),
('今天调了一下午 MySQL 索引，慢查询从 1.2s 降到 80ms，爽。', '傅浩东', 28, 6, '2026-02-26 19:40:00'),
('早八前刷完两道树的题，状态不错，继续。', '傅浩东', 9, 1, '2026-02-20 08:05:00'),
('情人节和代码过，也挺好 🌹', '傅浩东', 45, 11, '2026-02-14 23:30:00');

-- 杂谈（随笔，复用文章式内容）
DROP TABLE IF EXISTS essay;
CREATE TABLE essay (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500) DEFAULT '',
    content LONGTEXT,
    cover_url VARCHAR(500) DEFAULT '',   -- 杂谈封面图，首页 Records 轮播使用
    `date` VARCHAR(20) DEFAULT '',
    tags VARCHAR(255) DEFAULT '',
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO essay (title, summary, `date`, tags, sort) VALUES
('为什么我选择前后端分离', '从维护成本、协作效率与部署灵活性三个角度聊聊。', '2026-02-18', '架构,随笔', 1),
('实习那些事：第一次上线', '从本地到生产，心跳加速的半小时。', '2026-01-22', '实习,成长', 2),
('我的 2025 技术复盘', '学了什么、踩了什么坑、明年怎么走。', '2025-12-30', '复盘,年度', 3),
('关于「收集宝藏」这件事', '博客名里的浪漫，也是一种记录方式。', '2025-11-11', '生活,随笔', 4);
