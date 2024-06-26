package com.arya.SpringSecurityApp.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class GenericResponse<T> {
    private String status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
}
