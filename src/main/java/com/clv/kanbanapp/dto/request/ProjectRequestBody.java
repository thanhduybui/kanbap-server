package com.clv.kanbanapp.dto.request;


import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestBody {
    @Pattern(regexp = "^[a-zA-Z0-9 _-]+$", message = "Project name can only contain alphanumeric " +
            "characters, spaces, hyphens, and underscores.")
    private String projectName;
}
