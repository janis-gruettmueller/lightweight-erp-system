package com.leanx.app.exceptions;

public class FirstLoginException extends Exception {

    public FirstLoginException(String msg) {
        super(msg);
    }

    public FirstLoginException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
