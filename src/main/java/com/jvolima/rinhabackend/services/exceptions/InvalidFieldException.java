package com.jvolima.rinhabackend.services.exceptions;

public class InvalidFieldException extends RuntimeException {

    public InvalidFieldException(String msg) {
        super(msg);
    }
}
