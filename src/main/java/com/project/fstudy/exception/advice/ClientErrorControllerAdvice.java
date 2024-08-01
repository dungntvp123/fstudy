package com.project.fstudy.exception.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.exception.DataConstraintViolationException;
import com.project.fstudy.exception.InputConstraintViolationException;
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
    public List<String> inputConstraintViolationExceptionHandler(InputConstraintViolationException exception) throws JsonProcessingException {
        List<String> violations = (new ObjectMapper()).readValue(exception.getMessage(), List.class);
        return violations;
    }

    @ResponseBody
    @ExceptionHandler(DataConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String dataConstraintViolationExceptionHandler(DataConstraintViolationException exception) {
        return exception.getMessage();
    }


}
