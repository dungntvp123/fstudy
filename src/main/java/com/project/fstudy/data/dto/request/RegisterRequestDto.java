package com.project.fstudy.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "username must not be empty")
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "username can contain only word or number character")
    @Size(min = 8, max = 24, message = "username must have at least 8 and most 24 character")
    private String username;
    @NotBlank(message = "password must not be empty")
    @Size(min = 8, max = 24, message = "password must have at least 8 and most 24 character")
    private String password;
    @NotBlank(message = "email must not be empty")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "email must in correct format")
    @Size(max = 64, message = "email must less than 64 characters")
    private String email;
}
