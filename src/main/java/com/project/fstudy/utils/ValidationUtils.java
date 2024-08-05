package com.project.fstudy.utils;

import com.project.fstudy.data.entity.Task;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
@Component
public class ValidationUtils {
    public <T> List<String> getViolationMessage(T data) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(data);

        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

    public boolean isTimeSpanOverlap(LocalDateTime startTime, LocalDateTime endTime,
                                 LocalDateTime comparingStartTime, LocalDateTime comparingEndTime){

        return  !(endTime.isBefore(comparingStartTime) || startTime.isAfter(comparingEndTime));

    }
}
