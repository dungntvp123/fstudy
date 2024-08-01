package com.project.fstudy.exception;

public class DataConstraintViolationException extends RuntimeException {
    public DataConstraintViolationException(String message) {
        super(message);
    }
}
