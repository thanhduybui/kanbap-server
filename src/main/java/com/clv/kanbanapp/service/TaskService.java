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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AppUserRepository appUserRepository;
    private final static String TASK_CREATED_SUCCESSFULLY = "Task created successfully";
    private final static String TASK_UPDATED_SUCCESSFULLY = "Task updated successfully";
    private final static String TASK_NOT_FOUND = "Task not found with id: {}";


    @Transactional
    public ServiceResponse<?> createTask(TaskRequestBody requestBody) {
        Task task = taskMapper.toEntity(requestBody);
        String createdUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // if task is not a group task, assign it to the created user
        if (!requestBody.isGroupTask()) {
            AppUser user = appUserRepository.findByEmail(createdUserEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedUser(user);
        }
        // service update the task position is the max position in the task list
        setTaskPosition(task, createdUserEmail);
        taskRepository.save(task);
        return ServiceResponse.builder()
                .statusCode(HttpStatus.CREATED)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_CREATED_SUCCESSFULLY)
                .build();
    }

    private void setTaskPosition(Task task, String createdUserEmail) {
        Integer maxPosition = taskRepository.findMaxPosition(task.getStatus(), createdUserEmail, task.isGroupTask());
        if (maxPosition == null) {
            maxPosition = 0;
        }
        task.setPosition(maxPosition + 1);
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

        log.info("Getting tasks with status: {} and isPublic: {}", status, isPublic);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        TaskStatus taskStatus = TaskStatus.valueOf(status);
        List<Task> tasks;
        if (!isPublic ){
            tasks = taskRepository.findPrivateTasks(taskStatus, email);
        }else {
            tasks = taskRepository.findPublicTasksByStatus(taskStatus);
        }
        List<TaskDTO> taskDTOs = taskMapper.toListTaskDTO(tasks);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("tasks", taskDTOs))
                .build();

    }

    public ServiceResponse<?> updateTask(TaskRequestBody requestBody, Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));
        taskMapper.updateTaskFromRequest(requestBody, task);
        taskRepository.save(task);
        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_UPDATED_SUCCESSFULLY)
                .build();
    }
}
