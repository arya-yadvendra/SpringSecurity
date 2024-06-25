package com.arya.SpringSecurityApp.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String auth_Token;
    private String username;
    private String issuedTime;
    private String validTime;
}
