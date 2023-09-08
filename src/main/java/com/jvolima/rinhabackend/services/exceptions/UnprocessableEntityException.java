package com.jvolima.rinhabackend.services.exceptions;

public class UnprocessableEntityException extends RuntimeException {

    public UnprocessableEntityException(String msg) {
        super(msg);
    }
}
