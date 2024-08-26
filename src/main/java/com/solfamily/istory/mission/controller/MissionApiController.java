package com.solfamily.istory.mission.controller;

import com.solfamily.istory.mission.model.dto.ReportDto;
import com.solfamily.istory.mission.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("missionImg")
    public ResponseEntity<Map> registMissionImg(long familyMissionNo, MultipartFile missionImg) {
        return service.registMissionImg(familyMissionNo,missionImg);
    }

    @PostMapping("/roundMissions")
    public ResponseEntity<Map> getMissionsByRound(String userId, int roundNum) {
        return service.getMissionsByRound(userId,roundNum);
    }

    @PostMapping("/week")
    public ResponseEntity<Map> getMissionByWeek(String userId, int roundNum,int weekNum) {
        return service.getMissionByWeek(userId, roundNum, weekNum);
    }

//    @PostMapping("missions")
//    public ResponseEntity<Map> createMissions(String familyKey,String startDate) {
//        return service.createMissionsByFamilyKey(familyKey,startDate);
//    }

//    @PostMapping
//    public ResponseEntity<Map> showMission(String userId, int weekNum) {
//        return service.getMissionByWeekNum(userId,weekNum);
//    }
}
