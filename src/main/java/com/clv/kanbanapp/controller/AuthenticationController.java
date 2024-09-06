package com.clv.kanbanapp.controller;


import com.clv.kanbanapp.dto.LoginRequestBody;
import com.clv.kanbanapp.dto.RegisterRequestBody;
import com.clv.kanbanapp.response.ResponseData;
import com.clv.kanbanapp.response.ServiceResponse;
import com.clv.kanbanapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<ResponseData> register(@RequestBody @Valid RegisterRequestBody requestBody) {
        ServiceResponse<String> serviceResponse = authService.register(requestBody);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                .status(serviceResponse.getStatus())
                .message(serviceResponse.getMessage())
                .data(serviceResponse.getData())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseData> login(@RequestBody @Valid LoginRequestBody requestBody) {
        ServiceResponse<String> serviceResponse = authService.login(requestBody);
        return ResponseEntity.status(serviceResponse.getStatusCode())
                .body(ResponseData.builder()
                .status(serviceResponse.getStatus())
                .message(serviceResponse.getMessage())
                .data(serviceResponse.getData())
                .build());
    }


}
