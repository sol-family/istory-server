package com.solfamily.istory.model.dto;

import lombok.Data;

import java.util.Map;
import java.util.List;

@Data
public class FamilyMissionDTO {
    private Long familymissionNo;
    private Long missionNo;
    private String familyId;
    private String registDate;
    private String expirationDate;
    private int complete;
    private List<UserDTO> member;
    // key : 가족 구성원 아이디 value : 감상문
    private Map<String,ReportDTO> reportList;

}
