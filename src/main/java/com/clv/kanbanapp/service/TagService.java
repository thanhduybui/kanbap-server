package com.clv.kanbanapp.service;


import com.clv.kanbanapp.dto.response.TagDTO;
import com.clv.kanbanapp.dto.request.TagRequestBody;
import com.clv.kanbanapp.entity.TaskTag;
import com.clv.kanbanapp.mapper.TagMapper;
import com.clv.kanbanapp.repository.TagRepository;
import com.clv.kanbanapp.response.ServiceResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Builder
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private static final String TAG_CREATED_SUCCESSFULLY = "Tag created successfully";

    public ServiceResponse createTag(TagRequestBody requestBody) {
        TaskTag tag = TaskTag.builder()
                .name(requestBody.getName())
                .color(requestBody.getColor())
                .build();

        tagRepository.save(tag);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.CREATED)
                .status(ResponseStatus.SUCCESS.toString())
                .message(TAG_CREATED_SUCCESSFULLY)
                .build();
    }

    public ServiceResponse getTags() {

        List<TaskTag> tags = tagRepository.findAll();
        List<TagDTO> tagDTOs = tagMapper.listTagToListTagDTO(tags);

        return ServiceResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(ResponseStatus.SUCCESS.toString())
                .data(Map.of("tags", tagDTOs))
                .build();
    }
}
