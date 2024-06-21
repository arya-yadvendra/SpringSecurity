package com.arya.SpringSecurityApp.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private String username;

    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private String oldPassword;

    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private String newPassword;
}
