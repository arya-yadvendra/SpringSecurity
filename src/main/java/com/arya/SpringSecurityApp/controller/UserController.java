package com.arya.SpringSecurityApp.controller;

import com.arya.SpringSecurityApp.request.ChangePasswordRequest;
import com.arya.SpringSecurityApp.request.ForgetPasswordRequest;
import com.arya.SpringSecurityApp.model.User;
import com.arya.SpringSecurityApp.request.ResetPasswordRequest;
import com.arya.SpringSecurityApp.service.JwtService;
import com.arya.SpringSecurityApp.service.OTPService;
import com.arya.SpringSecurityApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OTPService otpService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getUsername() == null || user.getPassword() == null ||
                user.getName() == null || user.getPhone_no() == null || user.getAddress() == null) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Fields not allowed to be null");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existsByUsername(user.getUsername())) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Username already exists!!");
            return ResponseEntity.badRequest().body(response);
        }

        User newUser = userService.saveUser(user);
        response.put("status", HttpStatus.OK.value());
        response.put("message", "User registered successfully");
        response.put("user", newUser);
        return ResponseEntity.ok(response);
    }


    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateToken(user.getUsername());
                response.put("status", HttpStatus.OK.value());
                response.put("message", "LOGIN SUCCESSFUL");
                response.put("accessToken", accessToken);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", HttpStatus.UNAUTHORIZED.value());
                response.put("message", "LOGIN FAILED!!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (AuthenticationException e) {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "LOGIN FAILED");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @GetMapping("all-users")
    public ResponseEntity<?> getAllUsernames() {
        List<String> usernames = userService.getAllUsernames();
        return ResponseEntity.ok(usernames);
    }


    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            return UserService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }


    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgetPasswordRequest request) {
        String username = request.getUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        otpService.generateOTP(username);

        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to email",
                "redirect", "/reset-password"
        ));
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean flag = otpService.validateOTP(request.getUsername(), request.getOtp());
        if (!flag) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        UserService.resetPassword(user.getUsername(), request.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully");
    }

}