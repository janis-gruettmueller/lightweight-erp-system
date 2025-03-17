package com.leanx.app;

import com.leanx.app.service.AuthenticationService;

public class IntegrationTest {
    public static void main(String[] args) {
        AuthenticationService authService = new AuthenticationService();
        String username = "DEFAULT_USR";
        String password = "initERP@2025";

        boolean success = authService.authenticate(username, password);
        System.out.println(success);
    }
}
