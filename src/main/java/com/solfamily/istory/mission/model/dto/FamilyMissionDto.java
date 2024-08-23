package com.solfamily.istory.mission.model.dto;

import com.solfamily.istory.user.model.UserDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FamilyMissionDto {
    private Long familymissionNo;
    private Long missionNo;
    private String missionContents;
    private String familyKey;
    private String registDate;
    private String expirationDate;
    private int complete;
    private List<UserDto> member;
    // key : 가족 구성원 아이디 value : 감상문
    private Map<String, ReportDto> reports;

}
