package com.arya.SpringSecurityApp.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private String username;

    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private String password;
}
