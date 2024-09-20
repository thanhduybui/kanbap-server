package com.clv.kanbanapp.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@Data
public class ServiceResponse< T> {
    private HttpStatus statusCode;
    private String status;
    private Map<String, T> data;
    private String message;
}
