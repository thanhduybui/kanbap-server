package com.clv.kanbanapp.service;

import com.clv.kanbanapp.dto.request.MoveTaskRequestBody;
import com.clv.kanbanapp.dto.response.TaskDTO;
import com.clv.kanbanapp.dto.request.TaskRequestBody;
import com.clv.kanbanapp.entity.AppUser;
import com.clv.kanbanapp.entity.Image;
import com.clv.kanbanapp.entity.Task;
import com.clv.kanbanapp.entity.TaskStatus;
import com.clv.kanbanapp.exception.ResourceNotFoundException;
import com.clv.kanbanapp.mapper.TaskMapper;
import com.clv.kanbanapp.repository.AppUserRepository;
import com.clv.kanbanapp.repository.ImageRepository;
import com.clv.kanbanapp.repository.TaskRepository;
import com.clv.kanbanapp.dto.response.ServiceResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AppUserRepository appUserRepository;
    private final ImageRepository imageRepository;
    private final static String TASK_CREATED_SUCCESSFULLY = "Task created successfully";
    private final static String TASK_UPDATED_SUCCESSFULLY = "Task updated successfully";
    private final static String TASK_NOT_FOUND = "Task not found with id: {}";
    private final static String TASK_TAKEN_SUCCESSFULLY = "Task taken successfully";
    private final static String TASK_TAKEN_BY_ANOTHER_USER = "Private task cannot be taken by another user";
    private final static String GROUP_TASK_CANNOT_BE_DELETED = "Group task cannot be deleted";
    private final static String INVALID_DELETE_REQUEST = "Private task cannot be deleted by another user";
    private final static String TASK_MOVED_SUCCESSFULLY = "Task moved successfully";

    /* Create new task */
    @Transactional
    public ServiceResponse<?> createTask(TaskRequestBody requestBody) {
        // Convert the request body to Task entity using a mapper (assuming taskMapper exists)
        Task task = taskMapper.toEntity(requestBody);

        // Get the email of the user creating the task from the security context
        String createdUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // If the task is not a group task, assign it to the created user
        if (!requestBody.isGroupTask()) {
            AppUser user = appUserRepository.findByEmail(createdUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedUser(user);
        }

        // Set the task's position as the max position in the user's task list
        setTaskPosition(task, createdUserEmail);

        // Save the task (images will be saved automatically because of CascadeType.PERSIST)
        Task savedTask = taskRepository.save(task);

        List<Image> images = new ArrayList<>();
        // If there are images in the request, add them to the task
        if (requestBody.getImages() != null && !requestBody.getImages().isEmpty()) {
            requestBody.getImages().forEach(imageUrl -> {
                Image image = Image.builder()
                        .imgUrl(imageUrl)
                        .task(savedTask)
                        .build();
                images.add(image);  // The addImage method in the Task entity ensures task_id is set
            });
        }
        task.setImages(images);



        // Build the response
        return ServiceResponse.builder()
                .statusCode(HttpStatus.CREATED)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_CREATED_SUCCESSFULLY)
                .build();
    }

    /* Set task position = max(position + 1) when user create new task */
    private void setTaskPosition(Task task, String createdUserEmail) {
        Integer maxPosition = taskRepository.findMaxPosition(task.getStatus(), createdUserEmail, task.isGroupTask());
        if (maxPosition == null) {
            maxPosition = 0;
        }
        task.setPosition(maxPosition + 1);
    }

    /* Get a task by id */
    public ServiceResponse<TaskDTO> getOneTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskDTO taskDTO = taskMapper.toDTO(task);
        return ServiceResponse.<TaskDTO>builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("task", taskDTO))
                .build();
    }

    /* Get tasks, filter by access scope (public or private),
    status and the user who do tasks*/
    public ServiceResponse<?> getTasks(String status, boolean isPublic, Pageable pageable) {

        log.info("Getting tasks with status: {} and isPublic: {}", status, isPublic);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        TaskStatus taskStatus = TaskStatus.valueOf(status);
        List<Task> tasks;
        if (!isPublic) {
            tasks = taskRepository.findPrivateTasks(taskStatus, email, pageable);
        } else {
            tasks = taskRepository.findPublicTasksByStatus(taskStatus, pageable);
        }
        List<TaskDTO> taskDTOs = taskMapper.toListTaskDTO(tasks);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("tasks", taskDTOs))
                .build();

    }

    /* Update information of a task */
    @Transactional
    public ServiceResponse<?> updateTask(TaskRequestBody requestBody, Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));

        // If the task is not a group task and the request body is a group task,
        // set the assigned user to null
        if (!task.isGroupTask() && requestBody.isGroupTask()) {
            task.setAssignedUser(null);
        }

        taskMapper.updateTaskFromRequest(requestBody, task);

        taskRepository.save(task);
        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_UPDATED_SUCCESSFULLY)
                .build();
    }

    /* UnTake task: set assigned user to null and release to public's TODO column */
    private ServiceResponse<?> unTakeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));

        if (!task.isGroupTask()) {
            throw new IllegalArgumentException(TASK_TAKEN_BY_ANOTHER_USER);
        }

        task.setAssignedUser(null);
        task.setStatus(TaskStatus.valueOf("TODO"));
        taskRepository.save(task);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_TAKEN_SUCCESSFULLY)
                .build();
    }

    /* Take a task to do */
    @Transactional
    private ServiceResponse<?> takeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));

        if (!task.isGroupTask()) {
            throw new IllegalArgumentException(TASK_TAKEN_BY_ANOTHER_USER);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = appUserRepository.findByEmail(email).orElseThrow(()
                -> new ResourceNotFoundException("User not found"));

        task.setAssignedUser(user);
        taskRepository.save(task);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_TAKEN_SUCCESSFULLY)
                .build();
    }

    /* This function handle assign user to do a task or release
    task to the public section*/
    public ServiceResponse<?> assignTask(Long id, boolean unTake) {
        if (unTake) {
            return unTakeTask(id);
        }
        return takeTask(id);
    }

    /* Move task function:
    - Move task within the same list:
        - Move task up or down based on the current position
        - Update the moved task's position
    - Move task to a different list
        - If the destination list is empty, move the task to the top
        - If the destination list is not empty,
        move the task to the middle or the end of the list

    */
    @Transactional
    public ServiceResponse<?> moveTask(MoveTaskRequestBody request) {
        String assignedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Task movedTask = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, request.getTaskId())));

        // Get list of tasks with the destination status
        List<Task> tasks = taskRepository.findPrivateTasks(TaskStatus.valueOf(request.getDestinationStatus()), assignedUser, Pageable.unpaged());

        // Handle case where task is moved within the same list
        if (request.getSourceStatus().equals(request.getDestinationStatus())) {
            moveWithinSameList(movedTask, tasks, request.getSourceTaskPosition(), request.getDestinationTaskPosition());
        } else {
            moveToDifferentList(movedTask, tasks, request);
        }

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message("Task moved successfully")
                .build();
    }

    public void moveWithinSameList(Task movedTask, List<Task> tasks, int sourceTaskPosition, int destinationTaskPosition) {
        Task destinationTask = tasks.get(destinationTaskPosition);
        int destinationTaskPositionValue = destinationTask.getPosition();
        int movedTaskPosition = movedTask.getPosition();

        // Move up or down based on the current position
        if (movedTaskPosition < destinationTaskPositionValue) {
            moveUp(tasks, sourceTaskPosition, destinationTaskPosition);
        } else {
            moveDown(tasks, sourceTaskPosition, destinationTaskPosition);
        }

        // Update the moved task's position
        movedTask.setPosition(destinationTaskPositionValue);
        taskRepository.save(movedTask);
    }

    public void moveUp(List<Task> tasks, int sourceTaskPosition, int destinationTaskPosition) {
        for (int i = destinationTaskPosition; i < sourceTaskPosition; i++) {
            Task task = tasks.get(i);
            task.setPosition(task.getPosition() - 1);
            taskRepository.save(task);
        }
    }

    public void moveDown(List<Task> tasks, int sourceTaskPosition, int destinationTaskPosition) {
        for (int i = sourceTaskPosition + 1; i <= destinationTaskPosition; i++) {
            Task task = tasks.get(i);
            task.setPosition(task.getPosition() + 1);
            taskRepository.save(task);
        }
    }

    public void moveToDifferentList(Task movedTask, List<Task> tasks, MoveTaskRequestBody request) {
        if (request.getDestinationTaskPosition() == 0 && tasks.isEmpty()) {
            moveToEmptyList(movedTask, request.getDestinationStatus());
        } else if (request.getDestinationTaskPosition() == tasks.size()) {
            moveToEndOfList(movedTask, tasks, request.getDestinationStatus());
        } else {
            moveToMiddleOfList(movedTask, tasks, request.getDestinationTaskPosition(), request.getDestinationStatus());
        }
    }

    public void moveToEmptyList(Task movedTask, String destinationStatus) {
        movedTask.setStatus(TaskStatus.valueOf(destinationStatus));
        movedTask.setPosition(0);
        taskRepository.save(movedTask);
    }

    public void moveToEndOfList(Task movedTask, List<Task> tasks, String destinationStatus) {
        Task lastTask = tasks.get(tasks.size() - 1);
        movedTask.setStatus(TaskStatus.valueOf(destinationStatus));
        movedTask.setPosition(lastTask.getPosition() - 1);
        taskRepository.save(movedTask);
    }

    public void moveToMiddleOfList(Task movedTask, List<Task> tasks, int destinationTaskPosition, String destinationStatus) {
        for (int i = destinationTaskPosition - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            task.setPosition(task.getPosition() + 1);
            taskRepository.save(task);
        }

        Task destinationTask = tasks.get(destinationTaskPosition);
        movedTask.setStatus(TaskStatus.valueOf(destinationStatus));
        movedTask.setPosition(destinationTask.getPosition() + 1);
        taskRepository.save(movedTask);
    }

    /* Delete task function:
    - Check if the task is a group task: if yes,
        throw an exception (group task cannot be deleted)
    - Check if the task is assigned to the user who is trying to delete it
        if not, throw an exception (private task cannot be deleted by another user)
    - Delete all images associated with the task
    - Delete the task
    - Return success response
    */
    @Transactional
    public ServiceResponse<?> deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (task.isGroupTask()) {
            throw new IllegalArgumentException(GROUP_TASK_CANNOT_BE_DELETED);
        }

        // Check if the task is assigned to the user who is trying to delete it
        if (!task.getAssignedUser().getEmail().equals(email)) {
            throw new IllegalArgumentException(INVALID_DELETE_REQUEST);
        }
        List<Image> images = task.getImages();

        // Delete all images associated with the task
        imageRepository.deleteAll(images);
        // Delete the task
        taskRepository.delete(task);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TASK_MOVED_SUCCESSFULLY)
                .build();
    }

    /* Add new image in the description of the task,
      the image will be stored in the database in form of URL
     */
    public ServiceResponse<?> addTaskImage(Long id, String imageUrl) {
        // Validate that imageUrl is not null or empty
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        // Find the task by ID or throw an exception if not found
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));

        // Add the new image to the task's images list
        imageRepository.save(Image.builder().imgUrl(imageUrl).task(task).build());

        // Return success response
        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message("Image added successfully")
                .build();
    }

    /* Get all images of a tasks to show in the UI */
    public ServiceResponse<?> getTaskImages(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, id)));

        List<Image> images = task.getImages();
        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("images", images))
                .build();
    }
}
