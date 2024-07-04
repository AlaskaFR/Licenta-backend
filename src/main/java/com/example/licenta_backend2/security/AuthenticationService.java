package com.example.licenta_backend2.security;

import com.example.licenta_backend2.model.User;
import com.example.licenta_backend2.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public void authenticate(String token) {
        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = userService.loadUserByUsername(username);
        if (!jwtUtil.validateToken(token, userDetails)) {
            throw new IllegalArgumentException("Invalid token");
        }
    }

    public User getUser(String token) {
        String username = jwtUtil.extractUsername(token);
        return userService.findByEmail(username);
    }
}
