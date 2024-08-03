package com.project.fstudy.exception.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.exception.InputConstraintViolationException;
import com.project.fstudy.exception.ServerUnhandledErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ServerErrorControllerAdvice {
    @ResponseBody
    @ExceptionHandler(ServerUnhandledErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String serverUnhandledErrorExceptionHandler(ServerUnhandledErrorException exception) {
        return exception.getMessage();
    }
}
