package com.clv.kanbanapp.controller;

import com.clv.kanbanapp.dto.response.ResponseData;
import com.clv.kanbanapp.dto.response.ServiceResponse;
import com.clv.kanbanapp.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> deleteImage(@PathVariable Long id) {
        ServiceResponse<?> response = imageService.deleteImage(id);
        return ResponseEntity.status(response.getStatusCode())
                .body(ResponseData.builder()
                        .status(response.getStatus())
                        .message(response.getMessage())
                        .data(response.getData())
                        .build());
    }
}
