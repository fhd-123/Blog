package com.fhd.blog.exception;

/**
 * 鉴权失败异常（未登录 / token 无效）
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
