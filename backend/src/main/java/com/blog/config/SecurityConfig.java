package com.blog.config;

import com.blog.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/health", "/actuator/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/captcha/**").permitAll()
                .requestMatchers("/api/captcha/**").permitAll()
                .requestMatchers("/files/**").permitAll()
                .requestMatchers("/api/files/**").permitAll()
                .requestMatchers("/api/security/**").authenticated()
                .requestMatchers("/api/admin/**").authenticated()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/articles/**").permitAll()
                .requestMatchers("/api/articles/**").permitAll()
                .requestMatchers("/categories/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/tags/**").permitAll()
                .requestMatchers("/api/tags/**").permitAll()
                .requestMatchers("/comments/recent").permitAll()
                .requestMatchers("/api/comments/recent").permitAll()
                .requestMatchers("/comments/messages/trend").permitAll()
                .requestMatchers("/api/comments/messages/trend").permitAll()
                .requestMatchers("/announcements/unread", "/api/announcements/unread").permitAll()
                .requestMatchers("/announcements/unread/count", "/api/announcements/unread/count").permitAll()
                .requestMatchers("/announcements/active", "/api/announcements/active").permitAll()
                .requestMatchers("/announcements/important", "/api/announcements/important").permitAll()
                .requestMatchers("/announcements/test", "/api/announcements/test").permitAll()
                .requestMatchers("/announcements/*/read", "/api/announcements/*/read").authenticated()
                .requestMatchers("/announcements/read-batch", "/api/announcements/read-batch").authenticated()
                .requestMatchers("/announcements/**", "/api/announcements/**").authenticated()
                .requestMatchers("/search/**").permitAll()
                .requestMatchers("/api/search/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}