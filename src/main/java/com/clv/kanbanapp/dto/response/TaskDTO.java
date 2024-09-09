package com.clv.kanbanapp.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Integer position;
    private TagDTO tag;
    private String status;
    private String dueTime;
    private boolean groupTask;
    private ProfileDTO createdByUser;
    private ProfileDTO assignedUser;
}
