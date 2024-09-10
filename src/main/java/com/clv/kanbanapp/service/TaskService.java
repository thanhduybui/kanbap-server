package com.clv.kanbanapp.service;


import com.clv.kanbanapp.dto.request.MoveTaskRequestBody;
import com.clv.kanbanapp.dto.response.TaskDTO;
import com.clv.kanbanapp.dto.request.TaskRequestBody;
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

import java.util.HashMap;
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
    private final static String TASK_TAKEN_SUCCESSFULLY = "Task taken successfully";
    private final static String TASK_TAKEN_BY_ANOTHER_USER = "Private task cannot be taken by another user";
    private final static String GROUP_TASK_CANNOT_BE_CHANGED_TO_PRIVATE = "Group task cannot be changed to private";

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
        if (!isPublic) {
            tasks = taskRepository.findPrivateTasks(taskStatus, email);
        } else {
            tasks = taskRepository.findPublicTasksByStatus(taskStatus);
        }
        List<TaskDTO> taskDTOs = taskMapper.toListTaskDTO(tasks);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("tasks", taskDTOs))
                .build();

    }

    @Transactional
    public ServiceResponse<?> updateTask(TaskRequestBody requestBody, Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));


        if (!task.isGroupTask() && requestBody.isGroupTask()) {
            task.setAssignedUser(null);
        }

        if (!requestBody.getStatus().equals(task.getStatus())) {
            taskMapper.updateTaskFromRequest(requestBody, task);
            setTaskPosition(task, SecurityContextHolder.getContext().getAuthentication().getName());
        } else {
            taskMapper.updateTaskFromRequest(requestBody, task);
        }

        taskRepository.save(task);
        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_UPDATED_SUCCESSFULLY)
                .build();
    }

    public ServiceResponse<?> takeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));

        if (!task.isGroupTask()) {
            throw new IllegalArgumentException(TASK_TAKEN_BY_ANOTHER_USER);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = appUserRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        task.setAssignedUser(user);
        taskRepository.save(task);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_TAKEN_SUCCESSFULLY)
                .build();
    }

    @Transactional
    public ServiceResponse<?> moveTask(MoveTaskRequestBody request) {
        String assignedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Task movedTask = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, request.getTaskId())));
        // get list dropped task
        List<Task> tasks = taskRepository.findPrivateTasks(TaskStatus.valueOf(request.getDestinationStatus()), assignedUser);

        // create hashmap <index, task> index have format [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
        HashMap<Integer, Task> taskMap = new HashMap<>();
        for (int i = 0; i < tasks.size(); i++) {
            taskMap.put(i, tasks.get(i));
        }

        // move in same list
        if (request.getSourceStatus().equals(request.getDestinationStatus())) {
            // get task position in the list
            int destinationTaskPosition = request.getDestinationTaskPosition();
            Task destinationTask = taskMap.get(destinationTaskPosition);

            int movedTaskPosition = movedTask.getPosition();
            movedTask.setPosition(destinationTask.getPosition());
            destinationTask.setPosition(movedTaskPosition);

            taskRepository.save(movedTask);
            taskRepository.save(destinationTask);
        } else {
            if ( request.getDestinationTaskPosition() == 0 && tasks.isEmpty()){
                movedTask.setStatus(TaskStatus.valueOf(request.getDestinationStatus()));
                movedTask.setPosition(0);
                taskRepository.save(movedTask);
                return ServiceResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(ResponseStatus.SUCCESS.toString())
                        .message("Task moved successfully")
                        .build();

            }


            for (int i = request.getDestinationTaskPosition() + 1; i < taskMap.size(); i++) {
                Task task = taskMap.get(i);
                task.setPosition(task.getPosition() + 1);
                taskRepository.save(task);
            }

            Task destinationTask = taskMap.get(request.getDestinationTaskPosition());
            movedTask.setStatus(TaskStatus.valueOf(request.getDestinationStatus()));
            movedTask.setPosition(destinationTask.getPosition() + 1);
            taskRepository.save(movedTask);
        }

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message("Task moved successfully")
                .build();
    }

    public ServiceResponse<?> searchTasks(String keyword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> availableTasks = taskRepository.findByKeywordAndEmail(email, keyword);
        List<TaskDTO> taskDTOs = taskMapper.toListTaskDTO(availableTasks);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("tasks", taskDTOs))
                .build();
    }
}
