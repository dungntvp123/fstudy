package com.project.fstudy.data.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.fstudy.data.entity.TaskCategory;
import com.project.fstudy.data.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequestDto {
    @NotBlank(message = "Title must not be blank")
    @Size(min = 1, max = 999, message = "Title size must be between 1 and 999 characters")
    private String title;
    @NotBlank(message = "Description must not be blank")
    @Size(min = 1, max = 9999, message = "Description size must be between 1 and 9999 characters")
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @NotBlank(message = "Status must not be blank")
    private String status;
    @NotNull(message = "User ID must not be null")
    private Integer userId;
    @NotBlank(message = "Task category ID must not be blank")
    private String taskCategoryId;
}
