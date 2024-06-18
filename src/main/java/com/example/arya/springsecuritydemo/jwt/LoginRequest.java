package com.example.arya.springsecuritydemo.jwt;


import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
