package com.clv.kanbanapp.mapper;

import com.clv.kanbanapp.dto.response.ProfileDTO;
import com.clv.kanbanapp.entity.AppUser;
import com.clv.kanbanapp.entity.TaskTag;
import com.clv.kanbanapp.repository.AppUserRepository;
import com.clv.kanbanapp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class TaskMapperHelper {

    private final TagRepository tagRepository;
    private final AppUserRepository appUserRepository;
    public TaskTag tagIdToTag(Integer tagId) {
        if (tagId == null) {
            return null;
        }
        return tagRepository.findById(tagId).orElse(null);
    }

    public ProfileDTO emailToProfileDTO(String email) {
        AppUser appUser = appUserRepository.findByEmail(email).orElse(null);
        return ProfileDTO.builder()
                .userId(appUser.getUserId())
                .username(appUser.getUsername())
                .email(email)
                .build();
    }

}
