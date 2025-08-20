-- 创建登录日志表
CREATE TABLE login_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_info VARCHAR(100),
    location VARCHAR(100),
    login_time DATETIME NOT NULL,
    logout_time DATETIME,
    session_id VARCHAR(255),
    is_current_session BOOLEAN DEFAULT FALSE,
    login_status ENUM('SUCCESS', 'FAILED', 'LOGOUT') NOT NULL DEFAULT 'SUCCESS',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_session_id (session_id),
    INDEX idx_ip_address (ip_address),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建安全设置表
CREATE TABLE security_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    two_factor_method ENUM('NONE', 'TOTP', 'SMS', 'EMAIL') DEFAULT 'NONE',
    two_factor_secret VARCHAR(255),
    login_notification_enabled BOOLEAN DEFAULT TRUE,
    anomaly_protection_enabled BOOLEAN DEFAULT TRUE,
    session_timeout_minutes INT DEFAULT 30,
    password_change_required BOOLEAN DEFAULT FALSE,
    last_password_change DATETIME,
    failed_login_attempts INT DEFAULT 0,
    account_locked_until DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_two_factor_enabled (two_factor_enabled),
    INDEX idx_account_locked_until (account_locked_until),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 为现有用户创建默认安全设置
INSERT INTO security_settings (user_id, two_factor_enabled, two_factor_method, login_notification_enabled, anomaly_protection_enabled, session_timeout_minutes, password_change_required, failed_login_attempts)
SELECT id, FALSE, 'NONE', TRUE, TRUE, 30, FALSE, 0
FROM users
WHERE id NOT IN (SELECT user_id FROM security_settings);