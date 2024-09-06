package com.clv.kanbanapp.mapper;


import com.clv.kanbanapp.dto.TagDTO;
import com.clv.kanbanapp.entity.TaskTag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDTO tagToTagDTO(TaskTag tag);

    List<TagDTO> listTagToListTagDTO(List<TaskTag> tags);
}
