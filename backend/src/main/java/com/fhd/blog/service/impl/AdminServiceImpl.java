package com.fhd.blog.service.impl;

import com.fhd.blog.common.JwtUtils;
import com.fhd.blog.exception.AuthException;
import com.fhd.blog.service.AdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Value("${blog.admin-username}")
    private String adminUsername;

    @Value("${blog.admin-password}")
    private String adminPassword;

    private final JwtUtils jwtUtils;

    public AdminServiceImpl(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public String login(String username, String password) {
        if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
            throw new AuthException("用户名或密码错误");
        }
        return jwtUtils.generateToken(username);
    }
}
