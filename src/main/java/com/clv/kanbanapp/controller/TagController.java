package com.clv.kanbanapp.controller;

import com.clv.kanbanapp.dto.request.TagRequestBody;
import com.clv.kanbanapp.response.ResponseData;
import com.clv.kanbanapp.response.ServiceResponse;
import com.clv.kanbanapp.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Slf4j
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ResponseData> createTag(@RequestBody @Valid TagRequestBody tagRequestBody) {

        ServiceResponse serviceResponse = tagService.createTag(tagRequestBody);
        log.info("Tag created successfully");

        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getTags() {
        ServiceResponse serviceResponse = tagService.getTags();
        log.info("Tags retrieved successfully");

        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }
}
