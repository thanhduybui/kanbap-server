package com.clv.kanbanapp.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MoveTaskRequestBody {
    @NotNull(message = "Task ID is required")
    private Long taskId;
    @NotNull(message = "Source task position is required")
    private Integer sourceTaskPosition;
    @NotNull(message = "Destination task position is required")
    private Integer destinationTaskPosition;
    @Pattern(regexp = "^(TODO|IN_PROGRESS|DONE|CANCEL)$", message = "Status must be one of: TODO, IN_PROGRESS, DONE, CANCEL")
    private String sourceStatus;
    @Pattern(regexp = "^(TODO|IN_PROGRESS|DONE|CANCEL)$", message = "Status must be one of: TODO, IN_PROGRESS, DONE, CANCEL")
    private String destinationStatus;
}
