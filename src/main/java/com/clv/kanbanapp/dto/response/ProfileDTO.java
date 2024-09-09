package com.clv.kanbanapp.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProfileDTO {
    private Integer userId;
    private String username;
    private String email;
}
