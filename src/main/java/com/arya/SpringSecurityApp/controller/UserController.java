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
import org.springframework.security.access.prepost.PreAuthorize;
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
            response.setStatus("failure");
            response.setMessage("Username already exist!");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }

        if (user.getUsername() == null || user.getPassword() == null) {
            response.setStatus("failure");
            response.setMessage("Email, Password, Name are not allowed to be null");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }

        if (user.getName().length()<2){
            response.setStatus("failure");
            response.setMessage("Name field seems to be incorrect");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }

        try {
            User newUser = userService.saveUser(user);
            response.setStatus("success");
            response.setMessage("User registered successfully");
            response.setData(newUser);
            return ResponseEntity.status(OK).body(response);
        }
        catch (InvalidUsernameException | InvalidPasswordException e) {
            response.setStatus("failure");
            response.setMessage(e.getMessage());
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
        catch (InvalidNameException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler({InvalidUsernameException.class, InvalidPasswordException.class, InvalidPhoneNoException.class})
    public ResponseEntity<?> handleValidationExceptions(RuntimeException e) {
        GenericResponse<Void> response = new GenericResponse<>();
        response.setStatus("failure");
        response.setMessage(e.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User user) {
        GenericResponse<String> response = new GenericResponse<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateToken(user.getUsername());
                response.setStatus("success");
                response.setMessage("LOGIN SUCCESSFUL...");
                response.setData(accessToken);
                return ResponseEntity.ok(response);
            } else {
                response.setStatus("failure");
                response.setMessage("Incorrect details!");
                return ResponseEntity.status(BAD_REQUEST).body(response);
            }
        } catch (AuthenticationException e) {
            response.setStatus("failure");
            response.setMessage("Bad credentials!!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @GetMapping("all-users")
    public ResponseEntity<GenericResponse<List<String>>> getAllUsernames() {
        GenericResponse<List<String>> response = new GenericResponse<>();
        List<String> usernames = userService.getAllUsernames();
        response.setStatus("success");
        response.setMessage("Usernames retrieved successfully");
        response.setData(usernames);
        return ResponseEntity.status(OK).body(response);
    }


    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String token,@RequestBody ChangePasswordRequest request) {

        String tokenWithoutBearer = token.substring(7); // Remove 'Bearer ' prefix
        String usernameFromToken = jwtService.extractUserName(tokenWithoutBearer);

        if (!usernameFromToken.equals(request.getUsername())) {
            GenericResponse<String> response = new GenericResponse<>();
            response.setStatus("failure");
            response.setMessage("You are not authorized to change this user's password");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

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
            response.setStatus("failure");
            response.setMessage("user not found!");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
        otpService.generateOTP(username);

        response.setStatus("success");
        response.setMessage("OTP sent to email");
        response.setData("/reset-password?username=" + username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        GenericResponse<String> response = new GenericResponse<>();
        boolean flag = otpService.validateOTP(request.getUsername(), request.getOtp());
        if (!flag) {
            response.setStatus("failure");
            response.setMessage("Invalid OTP");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            response.setStatus("failure");
            response.setMessage("User not found!");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
        UserService.resetPassword(user.getUsername(), request.getNewPassword());
        response.setStatus("success");
        response.setMessage("Password reset Successfully for Username:"+ request.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}


