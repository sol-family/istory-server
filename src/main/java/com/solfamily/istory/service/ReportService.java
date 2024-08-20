package com.solfamily.istory.service;

import com.solfamily.istory.model.dto.UserDTO;

import java.util.List;

public interface ReportService {
    boolean createReportByMember(List<UserDTO> member,Long missionNo);
}
