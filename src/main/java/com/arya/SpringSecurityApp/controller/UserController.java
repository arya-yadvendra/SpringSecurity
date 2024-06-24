package com.arya.SpringSecurityApp.controller;


import com.arya.SpringSecurityApp.exception.InvalidPasswordException;
import com.arya.SpringSecurityApp.exception.InvalidPhoneNoException;
import com.arya.SpringSecurityApp.exception.InvalidUsernameException;
import com.arya.SpringSecurityApp.request.ChangePasswordRequest;
import com.arya.SpringSecurityApp.request.ForgetPasswordRequest;
import com.arya.SpringSecurityApp.entity.User;
import com.arya.SpringSecurityApp.request.ResetPasswordRequest;
import com.arya.SpringSecurityApp.response.GenericResponse;
import com.arya.SpringSecurityApp.service.JwtService;
import com.arya.SpringSecurityApp.service.OTPService;
import com.arya.SpringSecurityApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.naming.InvalidNameException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;


@Slf4j
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
        GenericResponse<User> response = new GenericResponse<>();

        if (userService.existsByUsername(user.getUsername())) {
            response.setStatus(BAD_REQUEST.toString());
            response.setMessage("Username already exist!");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }

        if (user.getUsername() == null || user.getPassword() == null) {
            response.setStatus(BAD_REQUEST.toString());
            response.setMessage("Email, Password, Name are not allowed to be null");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }

        if (user.getName().length()<2){
            response.setStatus(BAD_REQUEST.toString());
            response.setMessage("Name field seems to be incorrect");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }

        try {
            User newUser = userService.saveUser(user);
            response.setStatus(HttpStatus.OK.toString());
            response.setMessage("User registered successfully");
            response.setData(newUser);
            return ResponseEntity.ok(response);
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            response.setStatus(BAD_REQUEST.toString());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (InvalidNameException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler({InvalidUsernameException.class, InvalidPasswordException.class, InvalidPhoneNoException.class})
    public ResponseEntity<?> handleValidationExceptions(RuntimeException e) {
        GenericResponse<Void> response = new GenericResponse<>();
        response.setStatus(BAD_REQUEST.toString());
        response.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User user) {
        GenericResponse<String> response = new GenericResponse<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateToken(user.getUsername());
                response.setStatus(OK.toString());
                response.setMessage("LOGIN SUCCESSFUL...");
                response.setData(accessToken);
                return ResponseEntity.ok(response);
            } else {
                response.setStatus(BAD_REQUEST.toString());
                response.setMessage("Incorrect details!");
                return ResponseEntity.status(BAD_REQUEST).body(response);
            }
        } catch (AuthenticationException e) {
            response.setStatus(UNAUTHORIZED.toString());
            response.setMessage("Bad credentials!!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @GetMapping("all-users")
    public ResponseEntity<GenericResponse<List<String>>> getAllUsernames() {
        GenericResponse<List<String>> response = new GenericResponse<>();
        List<String> usernames = userService.getAllUsernames();
        response.setStatus(HttpStatus.OK.toString());
        response.setMessage("Usernames retrieved successfully");
        response.setData(usernames);
        return ResponseEntity.ok(response);
    }


    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            return UserService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgetPasswordRequest request) {
        GenericResponse<String> response = new GenericResponse<>();
        String username = request.getUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            response.setStatus((BAD_REQUEST.toString()));
            response.setMessage("user not found!");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
        otpService.generateOTP(username);

        response.setStatus(HttpStatus.OK.toString());
        response.setMessage("OTP sent to email");
        response.setData("/reset-password?username=" + username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        GenericResponse<String> response = new GenericResponse<>();
        boolean flag = otpService.validateOTP(request.getUsername(), request.getOtp());
        if (!flag) {
            response.setStatus(BAD_REQUEST.toString());
            response.setMessage("Invalid OTP");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            response.setStatus(BAD_REQUEST.toString());
            response.setMessage("User not found!");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
        UserService.resetPassword(user.getUsername(), request.getNewPassword());
        response.setStatus(OK.toString());
        response.setMessage("Password reset Successfully for Username:"+ request.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}


