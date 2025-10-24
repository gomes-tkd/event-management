package com.github.gomestkd.eventmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Invalid username or password");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
