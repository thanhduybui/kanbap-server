package com.clv.kanbanapp.service;


import com.clv.kanbanapp.dto.request.LoginRequestBody;
import com.clv.kanbanapp.dto.request.RegisterRequestBody;
import com.clv.kanbanapp.entity.AppUser;
import com.clv.kanbanapp.mapper.AppUserMapper;
import com.clv.kanbanapp.repository.AppUserRepository;
import com.clv.kanbanapp.response.ServiceResponse;
import com.clv.kanbanapp.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String USER_ALREADY_EXISTS = "User already exists";
    private static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully";
    private static final String LOGIN_FAIL = "Invalid email or password";
    private static final String LOGIN_SUCCESS = "Login successful";

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public ServiceResponse<String> register(RegisterRequestBody requestBody) {

            Optional<AppUser> foundUser = appUserRepository.findByEmail(requestBody.getEmail());

            if (foundUser.isPresent()) {
                return ServiceResponse.<String>builder()
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .status(ResponseStatus.ERROR.toString())
                        .message(USER_ALREADY_EXISTS)
                        .build();

            }

            AppUser user = appUserMapper.registerRequestBodyToAppUser(requestBody);
            appUserRepository.save(user);

            String token = JwtUtils.generateToken(user);

            return ServiceResponse.<String>builder()
                    .statusCode(HttpStatus.CREATED)
                    .status(ResponseStatus.SUCCESS.toString())
                    .data(Map.of("token", token))
                    .message(USER_REGISTERED_SUCCESSFULLY)
                    .build();
    }

    public ServiceResponse<String> login(LoginRequestBody requestBody) {
        Optional<AppUser> foundUser = appUserRepository.findByEmail(requestBody.getEmail());

        if (foundUser.isEmpty() || !foundUser.get().getPassword().equals(requestBody.getPassword())) {
            return ServiceResponse.<String>builder()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .status(ResponseStatus.ERROR.toString())
                    .message(LOGIN_FAIL)
                    .build();
        }

        String token = JwtUtils.generateToken(foundUser.get());

        return ServiceResponse.<String>builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("token",token ))
                .message(LOGIN_SUCCESS)
                .build();
    }
}
