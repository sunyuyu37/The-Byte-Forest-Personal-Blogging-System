# 配置说明

## 环境配置

### 1. 数据库配置

复制 `backend/src/main/resources/application.yml.template` 为 `application.yml`，并修改以下配置：

```yaml
spring:
  datasource:
    username: your_database_username
    password: your_database_password
```

### 2. JWT 密钥配置

```yaml
jwt:
  secret: your_jwt_secret_key_at_least_32_characters_long
```

### 3. 邮件服务配置

```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-gmail-app-password
```

### 4. 文件上传路径配置

```yaml
file:
  upload:
    path: /your/upload/path/
```

## 环境变量方式配置（推荐）

你也可以通过环境变量来配置敏感信息：

```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret
export MAIL_USERNAME=your_email
export MAIL_PASSWORD=your_mail_password
export FILE_UPLOAD_PATH=/your/upload/path/
```

## 注意事项

- `application.yml` 文件已被添加到 `.gitignore`，不会被提交到版本控制
- 请确保 JWT 密钥至少 32 个字符长度
- 邮件密码应使用应用专用密码，而非账户密码
- 生产环境建议使用环境变量方式配置敏感信息