package com.leanx.app.service.modules.user.auth.exceptions;

public class AccountDeactivatedException extends Exception {

    public AccountDeactivatedException(String msg) {
        super(msg);
    }

    public AccountDeactivatedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
