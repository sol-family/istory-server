package com.solfamily.istory.mission.controller;

import com.solfamily.istory.mission.model.dto.ReportDto;
import com.solfamily.istory.mission.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/api/mission/*")
public class MissionController {
    @Autowired
    private MissionService service;

    // 금주 미션 조회
    @PostMapping("weekly")
    public ResponseEntity<Map> weekly(String userId) {
        Map<String, Object> response = service.getWeeklyMission(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("reportUpdate")
    public ResponseEntity<String> reportUpdate(@RequestBody ReportDto report){
        if(reportService.updateReportByEntitly(report)){
            return new ResponseEntity<>("{\"success\": true}",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("{\"success\": false}",HttpStatus.OK);
        }
    }


    @GetMapping("create")
    public ResponseEntity<String> createMissionList(String familyKey,String startDate) {
        boolean success = familyMissionService.createMissionsByFamilyKey(familyKey,startDate);
        return new ResponseEntity<>("{\"success\": "+success+"}", HttpStatus.OK);
    }

    @GetMapping("test")
    public ResponseEntity<String> test() {
        String key = userService.getFamilyKeyByUserId("apple@naver.com");
        return new ResponseEntity<>(key,HttpStatus.OK);
    }


}
