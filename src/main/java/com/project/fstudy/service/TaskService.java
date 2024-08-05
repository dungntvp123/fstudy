package com.project.fstudy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.data.dto.request.CreateTaskRequestDto;
import org.springframework.http.ResponseEntity;

public interface TaskService {
    public ResponseEntity<?> createTask(CreateTaskRequestDto createTaskRequestDto) throws JsonProcessingException;
    public ResponseEntity<?> getAllUserTask(int userId);
}
