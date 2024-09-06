package com.clv.kanbanapp.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TagDTO {
    private Integer id;
    private  String name;
    private String color;
}
