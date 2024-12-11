package com.example.warehouse.utils.exceptions;

public class UserAlreadyExists extends RuntimeException {

    public UserAlreadyExists(String message) {
        super(message);
    }

}
