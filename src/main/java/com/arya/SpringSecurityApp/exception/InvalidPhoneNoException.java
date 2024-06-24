package com.arya.SpringSecurityApp.exception;

public class InvalidPhoneNoException extends RuntimeException {
    public InvalidPhoneNoException(String message) {
        super(message);
    }
}
