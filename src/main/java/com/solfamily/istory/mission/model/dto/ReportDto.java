package com.solfamily.istory.mission.model.dto;

import com.solfamily.istory.mission.model.user.UserDto;
import lombok.Data;

@Data
public class ReportDto {
    private UserDto user;
    private Long familymissionNo;
    private String thoughts;
    private int complete;
}
