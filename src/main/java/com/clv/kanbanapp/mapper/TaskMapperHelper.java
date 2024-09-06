package com.clv.kanbanapp.mapper;

import com.clv.kanbanapp.dto.ProfileDTO;
import com.clv.kanbanapp.entity.AppUser;
import com.clv.kanbanapp.entity.TaskTag;
import com.clv.kanbanapp.repository.AppUserRepository;
import com.clv.kanbanapp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class TaskMapperHelper {

    private final TagRepository tagRepository;
    private final AppUserRepository appUserRepository;
    public TaskTag tagIdToTag(Integer tagId) {
        return tagRepository.findById(tagId).orElse(null);
    }

    public ProfileDTO emailToProfileDTO(String email) {
        AppUser appUser = appUserRepository.findByEmail(email).orElse(null);
        return ProfileDTO.builder()
                .id(appUser.getUserId())
                .username(appUser.getUsername())
                .email(email)
                .build();
    }


}
