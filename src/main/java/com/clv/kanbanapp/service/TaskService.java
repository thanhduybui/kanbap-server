package com.clv.kanbanapp.service;


import com.clv.kanbanapp.dto.TaskDTO;
import com.clv.kanbanapp.dto.TaskRequestBody;
import com.clv.kanbanapp.entity.AppUser;
import com.clv.kanbanapp.entity.Task;
import com.clv.kanbanapp.entity.TaskStatus;
import com.clv.kanbanapp.exception.ResourceNotFoundException;
import com.clv.kanbanapp.mapper.TaskMapper;
import com.clv.kanbanapp.repository.AppUserRepository;
import com.clv.kanbanapp.repository.TaskRepository;
import com.clv.kanbanapp.response.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AppUserRepository appUserRepository;
    private final static String TASK_CREATED_SUCCESSFULLY = "Task created successfully";

    public ServiceResponse<?> createTask(TaskRequestBody requestBody) {
        Task task = taskMapper.toEntity(requestBody);
        if (!requestBody.isGroupTask()){
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            AppUser user = appUserRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedUser(user);
        }
        task.setPosition(0);
        taskRepository.save(task);
        return ServiceResponse.builder()
                .statusCode(HttpStatus.CREATED)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_CREATED_SUCCESSFULLY )
                .build();
    }

    public ServiceResponse<TaskDTO> getOneTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskDTO taskDTO = taskMapper.toDTO(task);
        return ServiceResponse.<TaskDTO>builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("task", taskDTO))
                .build();
    }

    public ServiceResponse getTasks(String status, boolean isPublic) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = appUserRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaskStatus taskStatus = TaskStatus.valueOf(status);

       List<Task> tasks = taskRepository.findTasks(taskStatus, isPublic, email);
       List<TaskDTO> taskDTOs = taskMapper.toListTaskDTO(tasks);

       return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("tasks", taskDTOs))
                .build();

    }
}
