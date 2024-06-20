package com.arya.SpringSecurityApp.response;

import lombok.Data;

@Data
public class GenericResponse<T> {
    private String status;
    private String message;
    private T data;
}
