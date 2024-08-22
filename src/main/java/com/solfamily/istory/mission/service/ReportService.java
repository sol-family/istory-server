package com.solfamily.istory.mission.service;

import com.solfamily.istory.mission.db.FamilyMissionRepository;
import com.solfamily.istory.mission.db.ReportRepository;
import com.solfamily.istory.mission.model.dto.ReportDto;
import com.solfamily.istory.mission.model.user.UserDto;
import com.solfamily.istory.mission.model.entity.ReportEntity;
import com.solfamily.istory.mission.model.entity.id.ReportEntityId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService implements ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private FamilyMissionRepository familyMissionRepository;

    @Override
    public Map<String, ReportDto> createReportByMember(List<UserDto> member, Long familyMissionNo) {
        Map<String, ReportDto> reports = new HashMap<>();
        for(UserDto user : member) {
            ReportEntity entity = new ReportEntity();
            entity.setId(new ReportEntityId(user.getUserId(),familyMissionNo));
            entity.setThoughts("");
            entity.setComplete(0);
            ReportDto report = new ReportDto();
            report.setUser(user);
            report.setFamilymissionNo(familyMissionNo);
            report.setThoughts("");
            report.setComplete(0);
            reports.put(user.getUserId(),report);
        }
        // 가족별 소감 인스턴스 생성 완료
        familyMissionRepository.updateComplete(1,familyMissionNo);
        return  reports;
    }

    @Override
    public Map<String, ReportDto> getReportsByMember(List<UserDto> member, Long familyMissionNo) {
        Map<String, ReportDto> reports = new HashMap<>();
        for(UserDto user : member) {
            ReportEntity entity = reportRepository.getReferenceById(new ReportEntityId(user.getUserId(),familyMissionNo));
            ReportDto report = new ReportDto();
            report.setUser(user);
            report.setFamilymissionNo(entity.getId().getFamilymissionNo());
            report.setThoughts(entity.getThoughts());
            report.setComplete(entity.getComplete());
            reports.put(user.getUserId(),report);
        }
        return  reports;
    }

    @Override
    public boolean updateReportByEntitly(ReportDto report) {
        String userId = report.getUser().getUserId();
        String thoughts = report.getThoughts();
        long familyMissionNo = report.getFamilymissionNo();
        return reportRepository.updateReport(thoughts,userId, familyMissionNo)==1;
    }
}
