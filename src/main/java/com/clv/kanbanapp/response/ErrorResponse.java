package com.clv.kanbanapp.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Builder
@Data
public class ErrorResponse {
    private HttpStatusCode statusCode;
    private String message;
    private final String status = "error";
}
