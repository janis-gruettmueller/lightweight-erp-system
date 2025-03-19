package com.leanx.app.api;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.service.modules.user.admin.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/test")
public class TestController extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        logger.log(Level.INFO, "Received GET request to /api/test");

        response.setStatus(HttpServletResponse.SC_OK); // Changed to 200 OK
        response.getWriter().write("{\"message\": \"Application is reachable!\"}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.log(Level.WARNING, "Received POST request to /api/test, which is not supported");

        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // Changed to 405
        response.getWriter().write("{\"error\": \"POST method not supported for this endpoint\"}");
    }
}
