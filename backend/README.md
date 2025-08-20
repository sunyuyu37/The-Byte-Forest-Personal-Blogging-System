# 个人博客系统 (Personal Blog System)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.8-4FC08D.svg)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Node.js](https://img.shields.io/badge/Node.js-18+-green.svg)](https://nodejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0+-red.svg)](https://redis.io/)

## 📋 项目概述

这是一个功能完整的现代化个人博客系统，采用前后端分离架构。后端使用 Spring Boot 3.2 + MySQL + Redis，前端采用 Vue 3 + Element Plus + Vite。系统支持文章管理、用户管理、评论系统、分类标签、搜索功能、公告系统等完整的博客功能。

### 🎯 核心特性

- **🚀 现代化技术栈**: Spring Boot 3.2 + Vue 3 + Vite + Element Plus
- **🔐 安全认证**: JWT Token + Spring Security + 用户权限管理
- **📝 富文本编辑**: Markdown 编辑器，支持代码高亮和数学公式
- **🔍 全文搜索**: 基于 MySQL 全文索引的高效搜索
- **📁 文件管理**: 支持图片上传、文件管理和 CDN 加速
- **💬 评论系统**: 支持嵌套评论和评论审核
- **📊 数据统计**: 访问统计、文章统计和用户行为分析
- **🔔 通知系统**: 实时消息通知和公告管理
- **📱 响应式设计**: 支持桌面端和移动端自适应

## 🏗️ 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端 (Vue 3)   │    │  后端 (Spring)   │    │   数据库层      │
│                │    │                │    │                │
│ • Vue 3        │    │ • Spring Boot   │    │ • MySQL 8.0    │
│ • Element Plus │◄──►│ • Spring Sec.   │◄──►│ • Redis        │
│ • Vite         │    │ • Spring Data   │    │ • 文件存储      │
│ • Axios        │    │ • JWT Auth      │    │                │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 📂 项目结构

```
boke/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/blog/
│   │   ├── controller/         # REST API 控制器
│   │   ├── service/           # 业务逻辑层
│   │   ├── repository/        # 数据访问层
│   │   ├── entity/           # 实体类
│   │   ├── dto/              # 数据传输对象
│   │   ├── config/           # 配置类
│   │   ├── security/         # 安全配置
│   │   └── exception/        # 异常处理
│   ├── src/main/resources/
│   │   ├── application.yml   # 应用配置
│   │   └── static/           # 静态资源
│   └── pom.xml              # Maven 依赖配置
├── frontend/                  # Vue 3 前端
│   ├── src/
│   │   ├── views/            # 页面组件
│   │   │   ├── admin/        # 后台管理页面
│   │   │   └── auth/         # 认证页面
│   │   ├── components/       # 公共组件
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── router/           # Vue Router 路由
│   │   ├── api/              # API 接口封装
│   │   ├── utils/            # 工具函数
│   │   └── styles/           # 样式文件
│   ├── package.json          # npm 依赖配置
│   └── vite.config.js        # Vite 构建配置
├── mysql/                     # 数据库脚本
│   ├── personal_blog_*.sql   # 数据表结构
│   └── generate_*.sql        # 数据生成脚本
└── public/                    # 公共静态资源
```

## 🛠️ 技术栈详情

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17 | 开发语言 |
| **Spring Boot** | 3.2.0 | 应用框架 |
| **Spring Security** | 6.x | 安全框架 |
| **Spring Data JPA** | 3.x | 数据访问 |
| **Spring Data Redis** | 3.x | 缓存支持 |
| **MySQL Connector** | 8.0.33 | 数据库驱动 |
| **JWT** | 0.11.5 | 令牌认证 |
| **Flexmark** | 0.64.8 | Markdown 处理 |
| **Commons FileUpload** | 1.5 | 文件上传 |
| **Jackson** | 2.15.x | JSON 序列化 |
| **Maven** | 3.9+ | 项目构建 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Vue.js** | 3.3.8 | 前端框架 |
| **Element Plus** | 2.4.4 | UI 组件库 |
| **Vite** | 5.0.6 | 构建工具 |
| **Vue Router** | 4.2.5 | 路由管理 |
| **Pinia** | 2.1.7 | 状态管理 |
| **Axios** | 1.6.2 | HTTP 客户端 |
| **Day.js** | 1.11.10 | 日期处理 |
| **Highlight.js** | 11.9.0 | 代码高亮 |
| **Marked** | 11.1.1 | Markdown 解析 |
| **NProgress** | 0.2.0 | 进度条 |
| **JS Cookie** | 3.0.5 | Cookie 管理 |
| **Node.js** | 18+ | 运行环境 |

### 数据库与中间件

| 技术 | 版本 | 用途 |
|------|------|------|
| **MySQL** | 8.0+ | 主数据库 |
| **Redis** | 7.0+ | 缓存和会话 |

## 🚀 快速开始

### 环境要求

- **Java**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Maven**: 3.9+

### 🔧 安装步骤

#### 1. 克隆项目

```bash
git clone <repository-url>
cd boke
```

#### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE personal_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据表结构
cd mysql
mysql -u root -p personal_blog < personal_blog_users.sql
mysql -u root -p personal_blog < personal_blog_article.sql
mysql -u root -p personal_blog < personal_blog_category.sql
mysql -u root -p personal_blog < personal_blog_tag.sql
mysql -u root -p personal_blog < personal_blog_comment.sql
mysql -u root -p personal_blog < personal_blog_announcement.sql
mysql -u root -p personal_blog < personal_blog_system_config.sql
mysql -u root -p personal_blog < personal_blog_operation_log.sql
mysql -u root -p personal_blog < personal_blog_file_info.sql
```

#### 3. 配置后端

```bash
cd backend

# 修改 application.yml 配置文件
# 数据库连接信息
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personal_blog?...
    username: your_username
    password: your_password
    
# Redis 连接信息  
  data:
    redis:
      host: localhost
      port: 6379

# 邮件配置 (可选)
  mail:
    host: smtp.gmail.com
    username: your-email@gmail.com
    password: your-app-password
```

#### 4. 启动后端服务

```bash
cd backend
mvn clean install
mvn spring-boot:run

# 服务启动在 http://localhost:8080/api
```

#### 5. 配置前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 前端启动在 http://localhost:4090
```

### 🔗 访问地址

- **前端**: http://localhost:4090
- **后端 API**: http://localhost:8080/api
- **API 文档**: http://localhost:8080/api/swagger-ui.html (如已配置)

## 📊 功能模块

### 🏠 前台功能

#### 主要页面
- **首页 (Home)**: 最新文章展示、每日一句、留言板、回到顶部
- **文章列表 (Articles)**: 分类浏览、分页加载、搜索过滤
- **文章详情 (ArticleDetail)**: Markdown 渲染、评论系统、分享功能
- **分类页面**: 按技术分享、学习笔记、生活随笔、项目实践、竞赛活动、新闻资讯分类
- **标签页面 (Tag)**: 标签云、标签文章列表
- **搜索页面 (Search)**: 全文搜索、高级筛选
- **关于页面 (About)**: 个人介绍、联系方式
- **联系页面 (Contact)**: 联系表单、社交链接

#### 用户功能
- **用户注册/登录**: JWT 认证、密码加密
- **个人资料 (Profile)**: 头像上传、信息修改
- **评论系统**: 发表评论、回复评论、点赞功能

### 🔧 后台管理

#### 仪表板 (Dashboard)
- 系统概览统计
- 访问趋势图表
- 快捷操作入口

#### 内容管理
- **文章管理 (Articles)**: CRUD 操作、批量管理、状态控制
- **文章编辑 (ArticleForm)**: Markdown 编辑器、实时预览、草稿保存
- **分类管理 (Categories)**: 分类 CRUD、层级管理
- **标签管理 (Tags)**: 标签 CRUD、使用统计
- **评论管理 (Comments)**: 审核评论、批量操作、垃圾评论过滤

#### 系统管理
- **用户管理 (Users)**: 用户列表、权限管理、状态控制
- **文件管理 (FileManager)**: 文件上传、批量管理、存储统计
- **公告管理 (AnnouncementManager)**: 公告发布、置顶设置
- **统计分析 (Statistics)**: 数据报表、访问分析
- **通知中心 (NotificationsView)**: 系统通知、消息推送

#### 安全中心
- **账户设置 (AccountSettings)**: 密码修改、个人信息
- **安全中心 (SecurityCenter)**: 登录日志、安全设置
- **Token 状态 (TokenStatus)**: 令牌管理、会话控制

## 🛡️ 安全特性

### 认证与授权
- **JWT Token 认证**: 无状态身份验证
- **Spring Security**: 细粒度权限控制
- **密码加密**: BCrypt 哈希加密
- **角色权限**: 管理员/普通用户角色区分

### 安全防护
- **CORS 配置**: 跨域请求控制
- **XSS 防护**: 输入内容过滤和转义
- **SQL 注入防护**: JPA 参数化查询
- **文件上传安全**: 文件类型和大小限制
- **请求频率限制**: 防止暴力攻击

### 数据保护
- **敏感信息加密**: 密码等敏感数据加密存储
- **日志记录**: 操作日志和安全事件记录
- **会话管理**: Token 过期机制和会话控制

## 📡 API 接口

### 认证相关 (`/api/auth`)
- `POST /login` - 用户登录
- `POST /register` - 用户注册
- `POST /logout` - 用户登出
- `GET /profile` - 获取用户信息

### 文章相关 (`/api/articles`)
- `GET /` - 获取文章列表
- `GET /{id}` - 获取文章详情
- `POST /` - 创建文章 (需认证)
- `PUT /{id}` - 更新文章 (需认证)
- `DELETE /{id}` - 删除文章 (需认证)
- `POST /{id}/like` - 点赞文章

### 管理接口 (`/api/admin`)
- `/admin/articles` - 文章管理
- `/admin/tags` - 标签管理
- `/admin/announcements` - 公告管理

### 其他接口
- `/api/categories` - 分类管理
- `/api/comments` - 评论系统
- `/api/files` - 文件上传
- `/api/search` - 搜索功能
- `/api/stats` - 统计数据
- `/api/notifications` - 通知系统

## 🗄️ 数据库设计

### 核心数据表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `personal_blog_users` | 用户表 | id, username, email, password, role |
| `personal_blog_article` | 文章表 | id, title, content, author_id, category_id |
| `personal_blog_category` | 分类表 | id, name, description, parent_id |
| `personal_blog_tag` | 标签表 | id, name, color, usage_count |
| `personal_blog_article_tag` | 文章标签关联 | article_id, tag_id |
| `personal_blog_comment` | 评论表 | id, article_id, user_id, content, parent_id |
| `personal_blog_announcement` | 公告表 | id, title, content, is_pinned, status |
| `personal_blog_file_info` | 文件信息表 | id, filename, file_path, file_size |
| `personal_blog_operation_log` | 操作日志表 | id, user_id, operation, ip_address |
| `personal_blog_system_config` | 系统配置表 | config_key, config_value, description |

## 🎨 UI/UX 设计

### 设计理念
- **简洁优雅**: 简约现代的设计风格
- **响应式布局**: 支持多终端自适应
- **用户友好**: 直观的交互和清晰的导航
- **性能优化**: 快速加载和流畅动画

### 主题特色
- **现代化组件**: Element Plus 组件库
- **动态效果**: CSS3 动画和过渡效果
- **深色模式**: 支持暗色主题切换 (可扩展)
- **自定义样式**: 个性化配色和字体

## ⚡ 性能优化

### 前端优化
- **代码分割**: Vite 动态导入和懒加载
- **资源压缩**: Gzip 压缩和文件优化
- **缓存策略**: HTTP 缓存和本地存储
- **图片优化**: 懒加载和格式优化

### 后端优化
- **数据库优化**: 索引优化和查询调优
- **Redis 缓存**: 热点数据缓存
- **连接池**: 数据库连接池优化
- **异步处理**: 非阻塞 I/O 操作

### 网络优化
- **API 合并**: 减少请求次数
- **数据分页**: 大数据集分页加载
- **压缩传输**: Response 数据压缩

## 🔄 部署方案

### 开发环境
```bash
# 后端
cd backend && mvn spring-boot:run

# 前端  
cd frontend && npm run dev
```

### 生产环境
```bash
# 构建前端
cd frontend && npm run build

# 构建后端
cd backend && mvn clean package

# 使用 jar 包部署
java -jar target/personal-blog-backend-1.0.0.jar
```

### Docker 部署 (可选)
```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: personal_blog
      MYSQL_ROOT_PASSWORD: 12345
    
  redis:
    image: redis:7-alpine
    
  backend:
    build: ./backend
    depends_on: [mysql, redis]
    
  frontend:
    build: ./frontend
    depends_on: [backend]
```

## 📈 未来规划

### 功能增强
- [ ] 实时聊天系统
- [ ] 多媒体内容支持
- [ ] 社交分享功能
- [ ] SEO 优化
- [ ] 移动端 App
- [ ] 第三方登录集成

### 技术升级
- [ ] 微服务架构改造
- [ ] 容器化部署
- [ ] CI/CD 流水线
- [ ] 监控告警系统
- [ ] 负载均衡配置

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/new-feature`)
3. 提交变更 (`git commit -am 'Add new feature'`)
4. 推送分支 (`git push origin feature/new-feature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 📞 联系方式

- **项目作者**: [您的姓名]
- **邮箱**: [您的邮箱]
- **GitHub**: [您的GitHub]
- **博客**: [您的博客地址]

## 🙏 致谢

感谢以下开源项目的支持:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MySQL](https://www.mysql.com/)
- [Redis](https://redis.io/)

---

## 📚 开发文档

### 开发规范
- Java 代码遵循 Google Java Style Guide
- Vue 代码遵循 Vue.js Style Guide
- Git 提交信息遵循 Conventional Commits

### 调试指南
- 后端日志: `logs/spring.log`
- 前端调试: Vue DevTools
- 数据库监控: MySQL Workbench
- 缓存监控: Redis Commander

### 常见问题

**Q: 启动时出现数据库连接错误?**
A: 检查 `application.yml` 中的数据库配置，确保 MySQL 服务已启动。

**Q: 前端页面无法加载？**
A: 检查后端服务是否正常启动，确认代理配置正确。

**Q: 文件上传失败？**
A: 检查上传目录权限和文件大小限制配置。

---

> 🎉 **欢迎使用个人博客系统！如有任何问题或建议，请提交 Issue 或 Pull Request。**