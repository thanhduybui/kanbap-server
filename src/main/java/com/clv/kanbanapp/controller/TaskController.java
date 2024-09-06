package com.clv.kanbanapp.controller;


import com.clv.kanbanapp.dto.TaskRequestBody;
import com.clv.kanbanapp.response.ResponseData;
import com.clv.kanbanapp.response.ServiceResponse;
import com.clv.kanbanapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ResponseData> getTasks(@RequestParam("status") String status, @RequestParam("public") Boolean isPublic ) {
        ServiceResponse<?> serviceResponse = taskService.getTasks(status, isPublic);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }
}
