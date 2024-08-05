package com.project.fstudy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.fstudy.data.dto.request.CreateTaskRequestDto;
import com.project.fstudy.service.TaskService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createNewTask(@RequestBody CreateTaskRequestDto createTaskRequestDto) throws JsonProcessingException {
        return taskService.createTask(createTaskRequestDto);
    }

    @GetMapping
    public  ResponseEntity<?> getAllTask(@RequestParam("userid") int userId){
        return taskService.getAllUserTask(userId);
    }
}
