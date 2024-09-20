package com.clv.kanbanapp.controller;


import com.clv.kanbanapp.dto.response.ProfileDTO;
import com.clv.kanbanapp.dto.response.ResponseData;
import com.clv.kanbanapp.dto.response.ServiceResponse;
import com.clv.kanbanapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseData> getProfile() {
        ServiceResponse<ProfileDTO> serviceResponse = userService.getProfile();
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                        .status(serviceResponse.getStatus())
                        .message(serviceResponse.getMessage())
                        .data(serviceResponse.getData())
                        .build());
    }
}
