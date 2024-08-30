package com.solfamily.istory.mission.controller;

import com.solfamily.istory.mission.model.dto.ReportDto;
import com.solfamily.istory.mission.service.MissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
@RequestMapping("/mission/*")
public class MissionApiController {
    @Autowired
    private MissionService service;

    @PostMapping("weeklyMission")
    public ResponseEntity<Map> getWeeklyMission(HttpServletRequest request) {
        return service.getWeeklyMission(request);
    }

    @PostMapping("report")
    public ResponseEntity<Map> updateReport(HttpServletRequest request,@RequestBody ReportDto report) {
        return service.updateReportByEntity(request, report);
    }

    @PostMapping("missionImg")
    public ResponseEntity<Map> registMissionImg(String familymissionNo, MultipartFile missionImg) {
        return service.registMissionImg(familymissionNo,missionImg);
    }

    @PostMapping("roundMissions")
    public ResponseEntity<Map> getMissionsByRound(HttpServletRequest request, @RequestParam(required = false,defaultValue = "0") int roundNum) {
        return service.getMissionsByRound(request,roundNum);
    }

    @PostMapping("week")
    public ResponseEntity<Map> getMissionByWeek(HttpServletRequest request, int roundNum,int weekNum) {
        return service.getMissionByWeek(request, roundNum, weekNum);
    }
}
