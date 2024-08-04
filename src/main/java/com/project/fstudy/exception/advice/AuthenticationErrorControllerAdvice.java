package com.project.fstudy.exception.advice;

import com.project.fstudy.common.cls.BodyFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

@ControllerAdvice
public class AuthenticationErrorControllerAdvice {
    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BodyFormatter<?> badCredentialsExceptionHandler() {
        return BodyFormatter.format("Username or password isn't correct","bad_credentials");
    }

    @ResponseBody
    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BodyFormatter<?> lockedExceptionHandler() {
        return BodyFormatter.format("Account has been unlimitedly locked", "locked");
    }

    @ResponseBody
    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BodyFormatter<?> DisabledExceptionHandler() {
        return BodyFormatter.format("Account hasn't been activated yet", "disable");
    }

    @ResponseBody
    @ExceptionHandler(AccountExpiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BodyFormatter<?> accountExpiredExceptionHandler(AccountExpiredException ex) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "Your account has been expired");
        map.put("expireTime", ex.getMessage());
        return BodyFormatter.format(map, "account_expired");

    }

    @ResponseBody
    @ExceptionHandler(CredentialsExpiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BodyFormatter<?> credentialsExpiredExceptionHandler() {
        return BodyFormatter.format("Your password has been expired", "credentials_expired");

    }

}
