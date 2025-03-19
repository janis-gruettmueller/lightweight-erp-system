package com.leanx.app.service.modules.user.auth.exceptions;

public class AccountLockedException extends Exception {

    public AccountLockedException(String msg) {
        super(msg);
    }

    public AccountLockedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
