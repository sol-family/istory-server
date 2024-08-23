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
@RequestMapping("/mission/*")
public class MissionApiController {
    @Autowired
    private MissionService service;

    // 금주 미션 조회
    @PostMapping("weekly")
    public ResponseEntity<Map> weekly(String userId) {
        System.out.println("weekly");
        return service.getWeeklyMission(userId);
    }

    @PostMapping("reportUpdate")
    public ResponseEntity<String> reportUpdate(@RequestBody ReportDto report){
        return  service.updateReportByEntitly(report);
    }


    @GetMapping("create")
    public ResponseEntity<String> createMissionList(String familyKey,String startDate) {
        boolean success = service.createMissionsByFamilyKey(familyKey,startDate);
        return new ResponseEntity<>("{\"success\": "+success+"}", HttpStatus.OK);
    }

}
