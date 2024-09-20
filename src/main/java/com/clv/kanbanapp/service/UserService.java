package com.clv.kanbanapp.service;

import com.clv.kanbanapp.dto.response.ProfileDTO;
import com.clv.kanbanapp.entity.AppUser;
import com.clv.kanbanapp.exception.ResourceNotFoundException;
import com.clv.kanbanapp.repository.AppUserRepository;
import com.clv.kanbanapp.dto.response.ServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final AppUserRepository appUserRepository;
    private static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: %s";
    private static final String GET_PROFILE_SUCCESS = "User profile retrieved successfully";

    public ServiceResponse<ProfileDTO> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Getting profile for user with email: {}", email);
        AppUser foundUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(USER_NOT_FOUND_WITH_EMAIL, email)));


        return ServiceResponse.<ProfileDTO>builder()
                .data(Map.of("profile",ProfileDTO.builder()
                        .username(foundUser.getUsername())
                        .email(email)
                        .build()))
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .message(GET_PROFILE_SUCCESS)
                .build();
    }
}
