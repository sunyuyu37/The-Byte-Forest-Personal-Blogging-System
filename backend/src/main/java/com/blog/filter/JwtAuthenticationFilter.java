package com.blog.filter;

import com.blog.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        boolean shouldSkip = path.equals("/") ||
               path.startsWith("/health") ||
               path.startsWith("/actuator") ||
               path.startsWith("/auth") ||
               path.startsWith("/api/auth") ||
               path.startsWith("/captcha") ||
               path.startsWith("/api/captcha") ||
               path.startsWith("/files") ||
               path.startsWith("/api/files") ||
               path.startsWith("/public") ||
               path.startsWith("/articles") ||
               path.startsWith("/api/articles") ||
               path.startsWith("/categories") ||
               path.startsWith("/api/categories") ||
               path.startsWith("/tags") ||
               path.startsWith("/api/tags") ||
               path.equals("/comments/recent") ||
               path.equals("/api/comments/recent") ||
               path.equals("/announcements/unread") || path.equals("/api/announcements/unread") ||
               path.equals("/announcements/unread/count") || path.equals("/api/announcements/unread/count") ||
               path.equals("/announcements/active") || path.equals("/api/announcements/active") ||
               path.equals("/announcements/important") || path.equals("/api/announcements/important") ||
               path.equals("/announcements/test") || path.equals("/api/announcements/test") || // public test
               // do not skip /announcements/read-batch (requires auth)

               path.startsWith("/search") ||
               path.startsWith("/api/search") ||
               path.equals("/error");
        
        // 添加调试信息
        if (path.contains("/announcements")) {
            System.out.println("=== shouldNotFilter Debug ===");
            System.out.println("Request URI: " + path);
            System.out.println("Should skip JWT filter: " + shouldSkip);
        }
        
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        // 添加调试信息
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin") || requestURI.contains("/announcements")) {
            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Request URI: " + requestURI);
            System.out.println("Authorization Header: " + authorizationHeader);
        }
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin") || requestURI.contains("/announcements")) {
                    System.out.println("Extracted username: " + username);
                }
            } catch (Exception e) {
                logger.warn("JWT token extraction failed: " + e.getMessage());
                if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin") || requestURI.contains("/announcements")) {
                    System.out.println("JWT extraction error: " + e.getMessage());
                }
            }
        } else if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin") || requestURI.contains("/announcements")) {
            System.out.println("No valid Authorization header found");
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin") || requestURI.contains("/announcements")) {
                    System.out.println("Authentication set successfully for user: " + username);
                }
            } else if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin") || requestURI.contains("/announcements")) {
                System.out.println("Token validation failed for user: " + username);
            }
        } else if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin") || requestURI.contains("/announcements")) {
            System.out.println("Username is null or authentication already exists");
        }
        
        filterChain.doFilter(request, response);
    }
}