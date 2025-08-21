# Personal Blog System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.8-4FC08D.svg)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Node.js](https://img.shields.io/badge/Node.js-18+-green.svg)](https://nodejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0+-red.svg)](https://redis.io/)

## 📋 Project Overview

This is a fully-featured modern personal blog system with a frontend-backend separation architecture. The backend uses Spring Boot 3.2 + MySQL + Redis, while the frontend adopts Vue 3 + Element Plus + Vite. The system supports complete blog functionality including article management, user management, comment system, categories and tags, search functionality, and announcement system.

### 🎯 Core Features

- **🚀 Modern Tech Stack**: Spring Boot 3.2 + Vue 3 + Vite + Element Plus
- **🔐 Security Authentication**: JWT Token + Spring Security + User Permission Management
- **📝 Rich Text Editing**: Markdown editor with code highlighting and mathematical formulas
- **🔍 Full-Text Search**: Efficient search based on MySQL full-text indexing
- **📁 File Management**: Support for image upload, file management, and CDN acceleration
- **💬 Comment System**: Support for nested comments and comment moderation
- **📊 Data Statistics**: Access statistics, article statistics, and user behavior analysis
- **🔔 Notification System**: Real-time message notifications and announcement management
- **📱 Responsive Design**: Support for desktop and mobile adaptive design

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Frontend (Vue) │    │ Backend (Spring) │    │  Database Layer │
│                │    │                │    │                │
│ • Vue 3        │    │ • Spring Boot   │    │ • MySQL 8.0    │
│ • Element Plus │◄──►│ • Spring Sec.   │◄──►│ • Redis        │
│ • Vite         │    │ • Spring Data   │    │ • File Storage  │
│ • Axios        │    │ • JWT Auth      │    │                │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 📂 Project Structure

```
boke/
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/com/blog/
│   │   ├── controller/         # REST API Controllers
│   │   ├── service/           # Business Logic Layer
│   │   ├── repository/        # Data Access Layer
│   │   ├── entity/           # Entity Classes
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── config/           # Configuration Classes
│   │   ├── security/         # Security Configuration
│   │   └── exception/        # Exception Handling
│   ├── src/main/resources/
│   │   ├── application.yml   # Application Configuration
│   │   └── static/           # Static Resources
│   └── pom.xml              # Maven Dependencies
├── frontend/                  # Vue 3 Frontend
│   ├── src/
│   │   ├── views/            # Page Components
│   │   │   ├── admin/        # Admin Management Pages
│   │   │   └── auth/         # Authentication Pages
│   │   ├── components/       # Common Components
│   │   ├── stores/           # Pinia State Management
│   │   ├── router/           # Vue Router Configuration
│   │   ├── api/              # API Interface Encapsulation
│   │   ├── utils/            # Utility Functions
│   │   └── styles/           # Style Files
│   ├── package.json          # npm Dependencies
│   └── vite.config.js        # Vite Build Configuration
├── mysql/                     # Database Scripts
│   ├── personal_blog_*.sql   # Database Table Structure
│   └── generate_*.sql        # Data Generation Scripts
└── public/                    # Public Static Resources
```

## 🛠️ Technology Stack Details

### Backend Technology Stack

| Technology | Version | Purpose |
|------------|---------|----------|
| **Java** | 17 | Development Language |
| **Spring Boot** | 3.2.0 | Application Framework |
| **Spring Security** | 6.x | Security Framework |
| **Spring Data JPA** | 3.x | Data Access |
| **Spring Data Redis** | 3.x | Cache Support |
| **MySQL Connector** | 8.0.33 | Database Driver |
| **JWT** | 0.11.5 | Token Authentication |
| **Flexmark** | 0.64.8 | Markdown Processing |
| **Commons FileUpload** | 1.5 | File Upload |
| **Jackson** | 2.15.x | JSON Serialization |
| **Maven** | 3.9+ | Project Build |

### Frontend Technology Stack

| Technology | Version | Purpose |
|------------|---------|----------|
| **Vue.js** | 3.3.8 | Frontend Framework |
| **Element Plus** | 2.4.4 | UI Component Library |
| **Vite** | 5.0.6 | Build Tool |
| **Vue Router** | 4.2.5 | Routing Management |
| **Pinia** | 2.1.7 | State Management |
| **Axios** | 1.6.2 | HTTP Client |
| **Day.js** | 1.11.10 | Date Processing |
| **Highlight.js** | 11.9.0 | Code Highlighting |
| **Marked** | 11.1.1 | Markdown Parsing |
| **NProgress** | 0.2.0 | Progress Bar |
| **JS Cookie** | 3.0.5 | Cookie Management |
| **Node.js** | 18+ | Runtime Environment |

### Database & Middleware

| Technology | Version | Purpose |
|------------|---------|----------|
| **MySQL** | 8.0+ | Primary Database |
| **Redis** | 7.0+ | Cache and Session |

## 🚀 Quick Start

### Environment Requirements

- **Java**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Maven**: 3.9+

### 🔧 Installation Steps

#### 1. Clone Project

```bash
git clone <repository-url>
cd boke
```

#### 2. Database Initialization

```bash
# Create database
mysql -u root -p
CREATE DATABASE personal_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Import database table structure
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

#### 3. Backend Configuration

```bash
cd backend

# Modify application.yml configuration file
# Database connection information
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personal_blog?...
    username: your_username
    password: your_password
    
# Redis connection information  
  data:
    redis:
      host: localhost
      port: 6379

# Email configuration (optional)
  mail:
    host: smtp.gmail.com
    username: your-email@gmail.com
    password: your-app-password
```

#### 4. Start Backend Service

```bash
cd backend
mvn clean install
mvn spring-boot:run

# Service starts at http://localhost:8080/api
```

#### 5. Frontend Configuration

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend starts at http://localhost:4090
```

### 🔗 Access URLs

- **Frontend**: http://localhost:4090
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/api/swagger-ui.html (if configured)

## 📊 Functional Modules

### 🏠 Frontend Features

#### Main Pages
- **Home**: Latest articles display, daily quotes, message board, back to top
- **Articles**: Category browsing, pagination loading, search filtering
- **Article Detail**: Markdown rendering, comment system, sharing functionality
- **Category Pages**: Categorized by tech sharing, study notes, life essays, project practice, competitions, news
- **Tag Pages**: Tag cloud, tag article lists
- **Search Page**: Full-text search, advanced filtering
- **About Page**: Personal introduction, contact information
- **Contact Page**: Contact form, social links

#### User Features
- **User Registration/Login**: JWT authentication, password encryption
- **User Profile**: Avatar upload, information modification
- **Comment System**: Post comments, reply to comments, like functionality

### 🔧 Backend Management

#### Dashboard
- System overview statistics
- Access trend charts
- Quick operation entries

#### Content Management
- **Article Management**: CRUD operations, batch management, status control
- **Article Editor**: Markdown editor, real-time preview, draft saving
- **Category Management**: Category CRUD, hierarchical management
- **Tag Management**: Tag CRUD, usage statistics
- **Comment Management**: Comment moderation, batch operations, spam filtering

#### System Management
- **User Management**: User list, permission management, status control
- **File Management**: File upload, batch management, storage statistics
- **Announcement Management**: Announcement publishing, pinning settings
- **Statistics Analysis**: Data reports, access analysis
- **Notification Center**: System notifications, message pushing

#### Security Center
- **Account Settings**: Password modification, personal information
- **Security Center**: Login logs, security settings
- **Token Status**: Token management, session control

## 🛡️ Security Features

### Authentication & Authorization
- **JWT Token Authentication**: Stateless identity verification
- **Spring Security**: Fine-grained permission control
- **Password Encryption**: BCrypt hash encryption
- **Role Permissions**: Admin/regular user role distinction

### Security Protection
- **CORS Configuration**: Cross-origin request control
- **XSS Protection**: Input content filtering and escaping
- **SQL Injection Protection**: JPA parameterized queries
- **File Upload Security**: File type and size restrictions
- **Request Rate Limiting**: Prevent brute force attacks

### Data Protection
- **Sensitive Information Encryption**: Encrypted storage of passwords and sensitive data
- **Logging**: Operation logs and security event recording
- **Session Management**: Token expiration mechanism and session control

## 📡 API Interfaces

### Authentication Related (`/api/auth`)
- `POST /login` - User login
- `POST /register` - User registration
- `POST /logout` - User logout
- `GET /profile` - Get user information

### Article Related (`/api/articles`)
- `GET /` - Get article list
- `GET /{id}` - Get article details
- `POST /` - Create article (authentication required)
- `PUT /{id}` - Update article (authentication required)
- `DELETE /{id}` - Delete article (authentication required)
- `POST /{id}/like` - Like article

### Admin Interfaces (`/api/admin`)
- `/admin/articles` - Article management
- `/admin/tags` - Tag management
- `/admin/announcements` - Announcement management

### Other Interfaces
- `/api/categories` - Category management
- `/api/comments` - Comment system
- `/api/files` - File upload
- `/api/search` - Search functionality
- `/api/stats` - Statistics data
- `/api/notifications` - Notification system

## 🗄️ Database Design

### Core Data Tables

| Table Name | Description | Key Fields |
|------------|-------------|------------|
| `personal_blog_users` | User table | id, username, email, password, role |
| `personal_blog_article` | Article table | id, title, content, author_id, category_id |
| `personal_blog_category` | Category table | id, name, description, parent_id |
| `personal_blog_tag` | Tag table | id, name, color, usage_count |
| `personal_blog_article_tag` | Article-tag association | article_id, tag_id |
| `personal_blog_comment` | Comment table | id, article_id, user_id, content, parent_id |
| `personal_blog_announcement` | Announcement table | id, title, content, is_pinned, status |
| `personal_blog_file_info` | File information table | id, filename, file_path, file_size |
| `personal_blog_operation_log` | Operation log table | id, user_id, operation, ip_address |
| `personal_blog_system_config` | System configuration table | config_key, config_value, description |

## 🎨 UI/UX Design

### Design Philosophy
- **Simple & Elegant**: Minimalist modern design style
- **Responsive Layout**: Multi-terminal adaptive support
- **User-Friendly**: Intuitive interaction and clear navigation
- **Performance Optimization**: Fast loading and smooth animations

### Theme Features
- **Modern Components**: Element Plus component library
- **Dynamic Effects**: CSS3 animations and transition effects
- **Dark Mode**: Support for dark theme switching (extensible)
- **Custom Styles**: Personalized color schemes and fonts

## ⚡ Performance Optimization

### Frontend Optimization
- **Code Splitting**: Vite dynamic imports and lazy loading
- **Resource Compression**: Gzip compression and file optimization
- **Caching Strategy**: HTTP caching and local storage
- **Image Optimization**: Lazy loading and format optimization

### Backend Optimization
- **Database Optimization**: Index optimization and query tuning
- **Redis Caching**: Hot data caching
- **Connection Pooling**: Database connection pool optimization
- **Asynchronous Processing**: Non-blocking I/O operations

### Network Optimization
- **API Merging**: Reduce request count
- **Data Pagination**: Large dataset pagination loading
- **Compression Transfer**: Response data compression

## 🔄 Deployment Solutions

### Development Environment
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend  
cd frontend && npm run dev
```

### Production Environment
```bash
# Build frontend
cd frontend && npm run build

# Build backend
cd backend && mvn clean package

# Deploy using jar package
java -jar target/personal-blog-backend-1.0.0.jar
```

### Docker Deployment (Optional)
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

## 📈 Future Plans

### Feature Enhancements
- [ ] Real-time chat system
- [ ] Multimedia content support
- [ ] Social sharing functionality
- [ ] SEO optimization
- [ ] Mobile app
- [ ] Third-party login integration

### Technology Upgrades
- [ ] Microservices architecture transformation
- [ ] Containerized deployment
- [ ] CI/CD pipeline
- [ ] Monitoring and alerting system
- [ ] Load balancing configuration

## 🤝 Contributing Guidelines

1. Fork the project
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create Pull Request

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

## 📞 Contact Information

- **Project Author**: [Your Name]
- **Email**: [Your Email]
- **GitHub**: [Your GitHub]
- **Blog**: [Your Blog URL]

## 🙏 Acknowledgments

Thanks to the following open source projects for their support:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MySQL](https://www.mysql.com/)
- [Redis](https://redis.io/)

---

## 📚 Development Documentation

### Development Standards
- Java code follows Google Java Style Guide
- Vue code follows Vue.js Style Guide
- Git commit messages follow Conventional Commits

### Debugging Guide
- Backend logs: `logs/spring.log`
- Frontend debugging: Vue DevTools
- Database monitoring: MySQL Workbench
- Cache monitoring: Redis Commander

### Common Issues

**Q: Database connection error on startup?**
A: Check database configuration in `application.yml`, ensure MySQL service is running.

**Q: Frontend pages won't load?**
A: Check if backend service is running properly, confirm proxy configuration is correct.

**Q: File upload fails?**
A: Check upload directory permissions and file size limit configuration.

---

> 🎉 **Welcome to the Personal Blog System! If you have any questions or suggestions, please submit an Issue or Pull Request.**

制作不易😘😘😘各位老板动动小手，奴家会有更多更新的动力！！！！
> ![Image text](https://github.com/sunyuyu37/blog/blob/main/backend/uploads/avatars/Alipay.jpg)
> ![Image text](https://github.com/sunyuyu37/blog/blob/main/backend/uploads/avatars/wechat.jpg)