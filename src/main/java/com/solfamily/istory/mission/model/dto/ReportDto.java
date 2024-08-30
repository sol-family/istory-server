package com.solfamily.istory.mission.model.dto;

import com.solfamily.istory.mission.model.entity.ReportEntity;
import com.solfamily.istory.user.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private UserDto user;
    private Long familymissionNo;
    private String thoughts;
    private int complete;

    public ReportDto(ReportEntity reportEntity, UserDto user) {
        this.user = user;
        this.familymissionNo = reportEntity.getId().getFamilymissionNo();
        this.thoughts = reportEntity.getThoughts();
        this.complete = reportEntity.getComplete();
    }
}
