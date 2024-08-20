package com.solfamily.istory.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.solfamily.istory.model.dto.FamilyMissionDTO;
import com.solfamily.istory.model.dto.UserDTO;
import com.solfamily.istory.model.entity.FamilyMissionEntity;
import com.solfamily.istory.service.FamilyMissionService;
import com.solfamily.istory.service.MissionService;
import com.solfamily.istory.service.ReportService;
import com.solfamily.istory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/mission/*")
public class MissionController {
    @Autowired
    private MissionService missionService;
    @Autowired
    private FamilyMissionService familyMissionService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReportService reportService;

    @PostMapping("weekly")
    public ResponseEntity<String> weekly(String userid) {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();

        // 로그인한 유저의 가족키 먼저 구하기
        String familyId = userService.getFamilyIdByUserId(userid);
        System.out.println(userid);
        Date nowDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(nowDate);

        FamilyMissionDTO weeklyMission = new FamilyMissionDTO();

        // 가족 구성원
        List<UserDTO> member = userService.getMemberByFamilyId(familyId);
        weeklyMission.setMember(member);

        FamilyMissionEntity missionEntity = familyMissionService.getWeeklyFamilyMission(date, familyId);

        // 가족미션 인스턴스만 생성됐을 때
        if(missionEntity.getComplete()==0){
            reportService.createReportByMember(member,missionEntity.getMissionNo());
        }


        json.add("weeklyMission", gson.toJsonTree(weeklyMission));
        String response = gson.toJson(json);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @GetMapping("create")
    public ResponseEntity<String> createMissionList(String familyId,String startDate) {
        familyMissionService.createMissionsByFamilyId(familyId,startDate);
        return new ResponseEntity<String>("fail", HttpStatus.OK);
    }


}
