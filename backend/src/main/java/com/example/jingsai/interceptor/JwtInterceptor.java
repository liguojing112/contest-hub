package com.example.jingsai.interceptor;

import com.example.jingsai.util.JwtUtil;
import com.example.jingsai.util.UserContext;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT拦截器 - 用于解析Token并设置用户上下文
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 允许OPTIONS请求通过
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取Token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 公开接口不需要登录
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 公开的GET接口（竞赛列表、详情、排行、活跃竞赛）
        // 但 /my、/my/stats 和 /review 需要登录
        if (path.startsWith("/api/competitions") && "GET".equalsIgnoreCase(method)
                && !path.equals("/api/competitions/my")
                && !path.equals("/api/competitions/my/stats")
                && !path.equals("/api/competitions/review")) {
            return true;
        }

        // 公告列表公开可查看
        if (path.startsWith("/api/announcements") && "GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 评论列表公开可查看
        if (path.startsWith("/api/comments") && "GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 获取教师列表是公开的
        if (path.equals("/api/auth/teachers") && "GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 认证相关公开接口
        if (path.contains("/api/auth/login") ||
                path.contains("/api/auth/register") ||
                path.contains("/api/auth/check-username") ||
                path.contains("/api/auth/forgot-password")) {
            return true;
        }

        // 验证Token
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"请先登录\"}");
            return false;
        }

        try {
            Claims claims = JwtUtil.parseToken(token);
            UserContext.UserInfo userInfo = new UserContext.UserInfo();
            userInfo.setUserId(Long.parseLong(claims.get("userId").toString()));
            userInfo.setUserName(claims.get("username").toString());
            userInfo.setName(claims.get("name") != null ? claims.get("name").toString() : claims.get("username").toString());
            userInfo.setUserRole(claims.get("userRole").toString());
            userInfo.setToken(token);
            UserContext.setCurrentUser(userInfo);
            return true;
        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Token无效或已过期\"}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 清除线程上下文，防止内存泄漏
        UserContext.clear();
    }
}