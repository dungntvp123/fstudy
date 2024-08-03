package com.project.fstudy.exception.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ClientErrorControllerAdvice {
    @ResponseBody
    @ExceptionHandler(InputConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<?> inputConstraintViolationExceptionHandler(InputConstraintViolationException exception) throws JsonProcessingException {
        return (new ObjectMapper()).readValue(exception.getMessage(), List.class);
    }

    @ResponseBody
    @ExceptionHandler(DataConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String dataConstraintViolationExceptionHandler(DataConstraintViolationException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidAuthenticationPrincipalException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String invalidAuthenticationPrincipalExceptionHandler(InvalidAuthenticationPrincipalException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DataValueConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String dataValueConflictExceptionHandler(DataConstraintViolationException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(PersistentDataNotFoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String persistentDataNotFoundExceptionHandler(DataConstraintViolationException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(GoogleIdTokenUnknownException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String googleIdTokenUnknownExceptionHandler(GoogleIdTokenUnknownException exception) {
        return exception.getMessage();
    }

}
