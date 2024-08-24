package com.solfamily.istory.mission.controller;

import com.solfamily.istory.mission.model.dto.ReportDto;
import com.solfamily.istory.mission.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/mission/*")
public class MissionApiController {
    @Autowired
    private MissionService service;

    @PostMapping("weeklyMission")
    public ResponseEntity<Map> getWeeklyMission(String userId) {
        return service.getWeeklyMission(userId);
    }

    @PostMapping("report")
    public ResponseEntity<Map> updateReport(@RequestBody ReportDto report) {
        return service.updateReportByEntity(report);
    }


    @GetMapping("missions")
    public ResponseEntity<Map> createMissions(String familyKey,String startDate) {
        return service.createMissionsByFamilyKey(familyKey,startDate);
    }

}
