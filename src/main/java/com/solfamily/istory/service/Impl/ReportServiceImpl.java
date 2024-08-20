package com.solfamily.istory.service.Impl;

import com.solfamily.istory.db.ReportRepository;
import com.solfamily.istory.model.dto.UserDTO;
import com.solfamily.istory.model.entity.ReportEntity;
import com.solfamily.istory.model.entity.id.ReportEntityId;
import com.solfamily.istory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Override
    public boolean createReportByMember(List<UserDTO> member,Long missionNo) {
        for(UserDTO user : member) {
            ReportEntity report = new ReportEntity();
            report.setId(new ReportEntityId(user.getUserId(),missionNo));
            report.setThoughts("");
            report.setComplete(0);
            reportRepository.save(report);
        }
        return true;
    }
}
