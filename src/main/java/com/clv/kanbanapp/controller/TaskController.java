package com.clv.kanbanapp.controller;


import com.clv.kanbanapp.dto.request.ImageRequestBody;
import com.clv.kanbanapp.dto.request.MoveTaskRequestBody;
import com.clv.kanbanapp.dto.request.TaskRequestBody;
import com.clv.kanbanapp.dto.response.ResponseData;
import com.clv.kanbanapp.dto.response.ServiceResponse;
import com.clv.kanbanapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    @PostMapping
    public ResponseEntity<ResponseData> createTask(@RequestBody @Valid TaskRequestBody requestBody) {
        ServiceResponse<?> serviceResponse = taskService.createTask(requestBody);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getOneTask(@PathVariable Long id) {
        ServiceResponse<?> serviceResponse = taskService.getOneTask(id);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getTasks(@RequestParam("status") String status, @RequestParam("isPublic") Boolean isPublic,
                                                 @RequestParam(value = "page", required = false) Integer page,
                                                 @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = Pageable.unpaged() ;

        if (page != null && size != null){
           pageable = PageRequest.of(page, size);
        }
        ServiceResponse<?> serviceResponse = taskService.getTasks(status, isPublic, pageable);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateTask(@RequestBody @Valid TaskRequestBody requestBody, @PathVariable Long id){
        ServiceResponse<?> serviceResponse = taskService.updateTask(requestBody, id);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }

    @PutMapping("/{id}/take")
    public ResponseEntity<ResponseData> takeTask(@PathVariable Long id,
                                                 @RequestParam(value = "unTake", required = false, defaultValue = "true") boolean unTake) {
        ServiceResponse<?> serviceResponse = taskService.assignTask(id, unTake);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }

    @PutMapping("/move")
    public ResponseEntity<ResponseData> moveTask(@RequestBody @Valid MoveTaskRequestBody request) {
        ServiceResponse<?> serviceResponse = taskService.moveTask(request);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> deleteTask(@PathVariable Long id) {
        ServiceResponse<?> serviceResponse = taskService.deleteTask(id);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }

    @PutMapping("/{id}/images")
    public ResponseEntity<ResponseData> addTaskImage(@PathVariable Long id, @RequestBody @Valid ImageRequestBody img) {
        ServiceResponse<?> serviceResponse = taskService.addTaskImage(id, img.getUrl());
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<ResponseData> getTaskImages(@PathVariable Long id) {
        ServiceResponse<?> serviceResponse = taskService.getTaskImages(id);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }
}
