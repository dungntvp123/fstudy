package com.project.fstudy.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fstudy.data.constant.TaskStatus;
import com.project.fstudy.data.dto.request.CreateTaskRequestDto;
import com.project.fstudy.data.dto.response.TaskResponseDto;
import com.project.fstudy.data.entity.Task;
import com.project.fstudy.data.entity.TaskCategory;
import com.project.fstudy.data.entity.User;
import com.project.fstudy.exception.DataValueConflictException;
import com.project.fstudy.exception.InputConstraintViolationException;
import com.project.fstudy.exception.PersistentDataNotFound;
import com.project.fstudy.repository.TaskCategoryRepository;
import com.project.fstudy.repository.TaskRepository;
import com.project.fstudy.repository.UserRepository;
import com.project.fstudy.service.TaskService;
import com.project.fstudy.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskCategoryRepository taskCategoryRepository;
    @Autowired
    private ValidationUtils validationUtils;

    @Override
    public ResponseEntity<?> createTask(CreateTaskRequestDto createTaskRequestDto) throws JsonProcessingException {
        List<String> violations = validationUtils.getViolationMessage(createTaskRequestDto);
        LocalDateTime startTimeDto = createTaskRequestDto.getStartTime().withSecond(0).withNano(0);
        LocalDateTime endTimeDto = createTaskRequestDto.getEndTime().withSecond(0).withNano(0);
        LocalDateTime nowTime = LocalDateTime.now().withSecond(0).withNano(0);
        if (startTimeDto.isBefore(nowTime)){
            violations.add("Start time must be in present or future");
        }
        if (endTimeDto.isBefore(nowTime)){
            violations.add("End time must be in present or future");
        }
        if(!startTimeDto.toLocalDate().isEqual(endTimeDto.toLocalDate())){
            violations.add("Time span of a task must be in the same day");
        }
        if (!startTimeDto.isBefore(endTimeDto)){
            violations.add("Start time must before end time");
        }
        TaskStatus taskStatus = TaskStatus.fromDisplayName(createTaskRequestDto.getStatus());
        if (taskStatus == null){
            violations.add("Invalid task status");
        }
        if (!violations.isEmpty()) {
            String message = (new ObjectMapper()).writeValueAsString(violations);
            throw new InputConstraintViolationException(message);
        }

        User user = userRepository.findById(createTaskRequestDto.getUserId())
                .orElseThrow(() -> new PersistentDataNotFound("User not found"));

        TaskCategory taskCategory = taskCategoryRepository.findById(createTaskRequestDto.getTaskCategoryId())
                .orElseThrow(() -> new PersistentDataNotFound("Task Category not found"));

        List<Task> tasksCurrentDate = taskRepository.findTasksByUserAndDate(createTaskRequestDto.getUserId(),startTimeDto);
        for (Task task: tasksCurrentDate) {
            if (validationUtils.isTimeSpanOverlap(startTimeDto,endTimeDto,task.getStartTime(),task.getEndTime())){
                throw new DataValueConflictException("The new task is overlaps with an existing task");
            }
        }

        Task task = Task.builder()
                .title(createTaskRequestDto.getTitle())
                .description(createTaskRequestDto.getDescription())
                .startTime(startTimeDto)
                .endTime(endTimeDto)
                .status(taskStatus.getDisplayName())
                .user(user)
                .taskCategory(taskCategory)
                .build();

        try{
            taskRepository.save(task);
        }catch (Exception ex){
            log.error("Encounter a exception: {}", ex.getMessage());
            throw ex;
        }

        return ResponseEntity.ok(task);
    }

    @Override
    public ResponseEntity<?> getAllUserTask(int userId) {
        List<Task> tasksInDB = taskRepository.findTasksByUser(userId);
        if (tasksInDB.isEmpty()){
            throw new PersistentDataNotFound("No task found!");
        }

        List<TaskResponseDto> taskResponseDtoList = new ArrayList<>();
        for (Task task:tasksInDB) {
            TaskResponseDto responseDto = TaskResponseDto.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .startTime(task.getStartTime())
                    .endTime(task.getEndTime())
                    .status(task.getStatus())
                    .build();

            taskResponseDtoList.add(responseDto);
        }

        return ResponseEntity.ok(taskResponseDtoList);
    }


}
