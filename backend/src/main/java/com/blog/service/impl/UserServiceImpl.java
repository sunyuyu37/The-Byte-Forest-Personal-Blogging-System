package com.blog.service.impl;

import com.blog.dto.UserDTO;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public UserDTO register(String username, String email, String password, String nickname) {
        // 检查用户名和邮箱是否已存在
        if (existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        if (existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        // 如果没有提供昵称，使用用户名作为默认昵称
        user.setNickname(nickname != null && !nickname.trim().isEmpty() ? nickname : username);
        user.setRole(User.Role.USER);
        user.setStatus(true);
        user.setEmailVerified(false);
        user.setArticleCount(0);
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        user.setDeleted(false);
        
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
    
    @Override
    public String login(String usernameOrEmail, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        if (!user.getStatus()) {
            throw new RuntimeException("账户已被禁用");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 生成JWT token
        return jwtUtil.generateToken(user.getUsername(), user.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        return userOpt.orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getUserIdByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getUserIdByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        return user.map(User::getId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新用户信息
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getWebsite() != null) {
            user.setWebsite(userDTO.getWebsite());
        }
        if (userDTO.getGithub() != null) {
            user.setGithub(userDTO.getGithub());
        }
        
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
    
    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Override
    public UserDTO updateAvatar(Long id, String avatar) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setAvatar(avatar);
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findUsers(Pageable pageable) {
        Page<User> users = userRepository.findAllActive(pageable);
        return users.map(UserDTO::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String keyword, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(keyword, pageable);
        return users.map(UserDTO::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findUsersByRole(User.Role role, Pageable pageable) {
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(UserDTO::new);
    }
    
    @Override
    public UserDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setStatus(!user.getStatus());
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        
        // 硬删除
        userRepository.delete(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAdminUsers() {
        return userRepository.countAdminUsers();
    }
    
    @Override
    public void verifyEmail(String token) {
        // TODO: 实现邮箱验证逻辑
        throw new RuntimeException("功能暂未实现");
    }
    
    @Override
    public void sendVerificationEmail(String email) {
        // TODO: 实现发送验证邮件逻辑
        throw new RuntimeException("功能暂未实现");
    }
    
    @Override
    public void resetPassword(String email) {
        // TODO: 实现重置密码逻辑
        throw new RuntimeException("功能暂未实现");
    }
    
    @Override
    public void confirmResetPassword(String token, String newPassword) {
        // TODO: 实现确认重置密码逻辑
        throw new RuntimeException("功能暂未实现");
    }
    
    @Override
    public UserDTO createUser(UserDTO userDTO, String password) {
        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setNickname(userDTO.getNickname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : User.Role.USER);
        user.setAvatar(userDTO.getAvatar());
        user.setBio(userDTO.getBio());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : true);
        user.setEmailVerified(false);
        user.setArticleCount(0);
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        user.setDeleted(false);
        
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
    
    @Override
    public UserDTO updateUserByAdmin(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新用户信息
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getEmail() != null) {
            // 检查邮箱是否已被其他用户使用
            Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
    
    @Override
    public void resetUserPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 生成默认密码（可以是随机密码或固定密码）
        String defaultPassword = "123456";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
        
        // TODO: 发送新密码到用户邮箱
    }
    
    @Override
    public void batchEnableUsers(java.util.List<Long> userIds) {
        for (Long userId : userIds) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setStatus(true);
                userRepository.save(user);
            }
        }
    }
    
    @Override
    public void batchDisableUsers(java.util.List<Long> userIds) {
        for (Long userId : userIds) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 不能禁用管理员
                if (user.getRole() != User.Role.ADMIN) {
                    user.setStatus(false);
                    userRepository.save(user);
                }
            }
        }
    }
    
    @Override
    public void batchDeleteUsers(java.util.List<Long> userIds) {
        for (Long userId : userIds) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 不能删除管理员
                if (user.getRole() == User.Role.ADMIN) {
                    throw new RuntimeException("不能删除管理员用户");
                }
                
                // 硬删除
                userRepository.delete(user);
            }
        }
    }
    
    // ========== 图表数据方法实现 ==========
    
    @Override
    public java.util.List<Object[]> getUserRegistrationTrendData(String period) {
        java.time.LocalDateTime startDate;
        switch (period.toLowerCase()) {
            case "week":
                startDate = java.time.LocalDateTime.now().minusWeeks(1);
                break;
            case "month":
                startDate = java.time.LocalDateTime.now().minusMonths(1);
                break;
            case "year":
                startDate = java.time.LocalDateTime.now().minusYears(1);
                break;
            default:
                startDate = java.time.LocalDateTime.now().minusMonths(1);
        }
        return userRepository.getUserRegistrationByDate(startDate);
    }
}