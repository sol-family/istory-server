package com.solfamily.istory.global.controller;

import com.solfamily.istory.global.service.DummyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/dummy/*")
public class DummyController {
    @Autowired
    private DummyService service;

    @GetMapping("people")
    public ResponseEntity<Map<String,Object>> createPeople(){
        return service.createDummyUsers();
    }

    @GetMapping("familyMission")
    public ResponseEntity<Map> createFamilyMission(String familyKey,String date){
        return service.createDummyFamilyMissions(familyKey,date);
    }

    @PostMapping("shinhantest")
    public ResponseEntity<Map> shinhanTest(String userKey){
        return service.shinhanTest(userKey);
    }
    @PostMapping("shinhantest1")
    public ResponseEntity<Map> shinhanTest1(String userKey){
        return service.shinhanCreateTest(userKey);
    }
}
