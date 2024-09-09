package com.clv.kanbanapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskRequestBody {
    @NotBlank(message = "Task name is required")
    @Size(max = 255, message = "Task name must be less than 255 characters")
    @JsonAlias("task_name")
    private String title;

    private String description;
    @Pattern(regexp = "^(TODO|IN_PROGRESS|DONE|CANCEL)$", message = "Status must be one of: TODO, IN_PROGRESS, DONE, CANCEL")
    private String status;


    private boolean groupTask;


    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$", message = "Due time must be in the format: yyyy-MM-ddTHH:mm:ssZ")
    private String dueTime;

    @JsonAlias("tag_id")
    private Integer tagId;
}
