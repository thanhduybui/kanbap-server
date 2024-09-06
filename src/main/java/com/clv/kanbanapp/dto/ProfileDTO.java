package com.clv.kanbanapp.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProfileDTO {
    private Integer id;
    private String username;
    private String email;
}
