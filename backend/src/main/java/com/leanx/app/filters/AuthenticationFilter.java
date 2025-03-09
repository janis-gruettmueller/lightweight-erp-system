package com.leanx.app.filters;

import java.io.IOException;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException; // Updated import
import com.auth0.jwt.interfaces.DecodedJWT; // Updated import

import jakarta.servlet.Filter; // Updated import
import jakarta.servlet.FilterChain; // Updated import
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/api/*", filterName = "authenticationFilter")
public class AuthenticationFilter implements Filter {

    private static final String SECRET_KEY = System.getenv("SECRET_KEY");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic (if needed)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String token = req.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
            try {
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
                JWTVerifier verifier = JWT.require(algorithm)
                        .withIssuer("yourIssuer")
                        .build();
                DecodedJWT jwt = verifier.verify(token);

                String username = jwt.getSubject();
                List<String> roles = jwt.getClaim("roles").asList(String.class);

                req.setAttribute("username", username);
                req.setAttribute("roles", roles);

                chain.doFilter(request, response);
            } catch (JWTVerificationException exception) {
                res.setStatus(401); // Unauthorized
                return;
            }
        } else {
            res.setStatus(401); // Unauthorized
            return;
        }
    }

    @Override
    public void destroy() {
        // Cleanup logic (if needed)
    }
}