package com.arya.SpringSecurityApp.controller;

import com.arya.SpringSecurityApp.model.User;
import com.arya.SpringSecurityApp.service.JwtService;
import com.arya.SpringSecurityApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (service.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Username already exists!!."));
        }
        User newUser = service.saveUser(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", newUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateToken(user.getUsername());
                Map<String, String> response = new LinkedHashMap<>();
                response.put("status", "LOGIN SUCCESSFUL");
                response.put("accessToken", accessToken);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new LinkedHashMap<>();
                response.put("status", "LOGIN FAILED!!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (AuthenticationException e) {
            Map<String, String> response = new LinkedHashMap<>();
            response.put("status", "LOGIN FAILED");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("all-users")
    public ResponseEntity<?> getAllUsernames() {
        List<String> usernames = service.getAllUsernames();
        return ResponseEntity.ok(usernames);
    }

}