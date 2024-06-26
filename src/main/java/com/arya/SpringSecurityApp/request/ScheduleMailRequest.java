package com.arya.SpringSecurityApp.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleMailRequest {
    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private List<String> to;

    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private String subject;

    @NotBlank(message = "Can't be blank")
    @NotNull(message = "Can't be null field")
    private String text;
}
