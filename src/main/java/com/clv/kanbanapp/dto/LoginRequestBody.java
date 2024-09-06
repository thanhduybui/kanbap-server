package com.clv.kanbanapp.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequestBody {
    private String email;
    private String password;
}
