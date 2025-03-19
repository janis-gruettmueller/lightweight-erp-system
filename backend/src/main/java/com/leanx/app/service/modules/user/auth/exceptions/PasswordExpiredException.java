package com.leanx.app.service.modules.user.auth.exceptions;

public class PasswordExpiredException extends Exception {

    public PasswordExpiredException(String msg) {
        super(msg);
    }

    public PasswordExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
