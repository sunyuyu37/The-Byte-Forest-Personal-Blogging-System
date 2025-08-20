package com.blog.service;

import com.blog.dto.UserDTO;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    
    /**
     * 用户注册
     */
    UserDTO register(String username, String email, String password, String nickname);
    
    /**
     * 用户登录
     */
    String login(String usernameOrEmail, String password);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找用户
     */
    User findByUsernameOrEmail(String usernameOrEmail);
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据用户名获取用户ID
     */
    Long getUserIdByUsername(String username);
    
    /**
     * 根据用户名或邮箱获取用户ID
     */
    Long getUserIdByUsernameOrEmail(String usernameOrEmail);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 更新用户信息
     */
    UserDTO updateUser(Long id, UserDTO userDTO);
    
    /**
     * 更新用户密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 更新用户头像
     */
    UserDTO updateAvatar(Long id, String avatar);
    
    /**
     * 分页查询用户
     */
    Page<UserDTO> findUsers(Pageable pageable);
    
    /**
     * 搜索用户
     */
    Page<UserDTO> searchUsers(String keyword, Pageable pageable);
    
    /**
     * 根据角色查询用户
     */
    Page<UserDTO> findUsersByRole(User.Role role, Pageable pageable);
    
    /**
     * 启用/禁用用户
     */
    UserDTO toggleUserStatus(Long id);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 创建用户（管理员功能）
     */
    UserDTO createUser(UserDTO userDTO, String password);
    
    /**
     * 管理员更新用户
     */
    UserDTO updateUserByAdmin(Long id, UserDTO userDTO);
    
    /**
     * 重置用户密码（管理员功能）
     */
    void resetUserPassword(Long id);
    
    /**
     * 批量启用用户
     */
    void batchEnableUsers(java.util.List<Long> userIds);
    
    /**
     * 批量禁用用户
     */
    void batchDisableUsers(java.util.List<Long> userIds);
    
    /**
     * 批量删除用户
     */
    void batchDeleteUsers(java.util.List<Long> userIds);
    
    /**
     * 获取用户统计信息
     */
    long countActiveUsers();
    
    long countAdminUsers();
    
    /**
     * 验证邮箱
     */
    void verifyEmail(String token);
    
    /**
     * 发送邮箱验证邮件
     */
    void sendVerificationEmail(String email);
    
    /**
     * 重置密码
     */
    void resetPassword(String email);
    
    /**
     * 确认重置密码
     */
    void confirmResetPassword(String token, String newPassword);
    
    // ========== 图表数据方法 ==========
    
    /**
     * 获取用户注册趋势数据（按日期）
     */
    java.util.List<Object[]> getUserRegistrationTrendData(String period);
}