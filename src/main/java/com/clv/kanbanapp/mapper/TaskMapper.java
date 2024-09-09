package com.clv.kanbanapp.mapper;


import com.clv.kanbanapp.dto.TaskDTO;
import com.clv.kanbanapp.dto.TaskRequestBody;
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

    List<TaskDTO> toListTaskDTO(List<Task> tasks);

    @Mapping(target = "tag", source = "tagId")
    void updateTaskFromRequest(TaskRequestBody requestBody,@MappingTarget Task task);
}
