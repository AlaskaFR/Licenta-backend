package com.example.licenta_backend2.controller;

import com.example.licenta_backend2.dto.LoginRequest;
import com.example.licenta_backend2.dto.RegisterRequest;
import com.example.licenta_backend2.model.User;
import com.example.licenta_backend2.security.JwtUtil;
import com.example.licenta_backend2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setTelephone(registerRequest.getTelephone());
        user.setRole(registerRequest.getRole());
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }
}
