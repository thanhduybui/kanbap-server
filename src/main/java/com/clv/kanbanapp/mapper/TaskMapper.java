package com.clv.kanbanapp.mapper;


import com.clv.kanbanapp.dto.response.ImageDTO;
import com.clv.kanbanapp.dto.response.TaskDTO;
import com.clv.kanbanapp.dto.request.TaskRequestBody;
import com.clv.kanbanapp.entity.Image;
import com.clv.kanbanapp.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring", uses = {TaskMapperHelper.class})
public interface TaskMapper {

    @Mapping(target = "tag", source = "tagId")
    Task toEntity(TaskRequestBody requestBody);

    TaskDTO toDTO(Task task);

    ImageDTO toImageDTO(Image image);

    List<ImageDTO> toListImageDTO(List<Image> images);

    List<TaskDTO> toListTaskDTO(List<Task> tasks);

    @Mapping(target = "tag", source = "tagId")
    void updateTaskFromRequest(TaskRequestBody requestBody,@MappingTarget Task task);
}
