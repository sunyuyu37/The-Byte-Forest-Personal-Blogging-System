package com.blog.filter;

import com.blog.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        // 添加调试信息
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin")) {
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
                if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin")) {
                    System.out.println("Extracted username: " + username);
                }
            } catch (Exception e) {
                logger.warn("JWT token extraction failed: " + e.getMessage());
                if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin")) {
                    System.out.println("JWT extraction error: " + e.getMessage());
                }
            }
        } else if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin")) {
            System.out.println("No valid Authorization header found");
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin")) {
                    System.out.println("Authentication set successfully for user: " + username);
                }
            } else if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin")) {
                System.out.println("Token validation failed for user: " + username);
            }
        } else if (requestURI.contains("/notifications") || requestURI.contains("/security") || requestURI.contains("/admin")) {
            System.out.println("Username is null or authentication already exists");
        }
        
        filterChain.doFilter(request, response);
    }
}