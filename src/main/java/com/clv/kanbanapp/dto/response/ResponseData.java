package com.clv.kanbanapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData{
    public String status;
    public String message;
    public Object data;
    public Date timestamp;
}
