package com.fhd.blog.config;

import com.fhd.blog.common.JwtUtils;
import com.fhd.blog.exception.AuthException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验 Authorization: Bearer <token>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行 OPTIONS 预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new AuthException("未登录或 token 缺失");
        }
        String token = auth.substring(7);
        try {
            if (jwtUtils.isExpired(token)) {
                throw new AuthException("登录已过期，请重新登录");
            }
            request.setAttribute("username", jwtUtils.getUsername(token));
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("token 无效");
        }
        return true;
    }
}
