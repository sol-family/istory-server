package com.solfamily.istory.Family.controller;

import com.solfamily.istory.Family.model.InviteCodeRequest;
import com.solfamily.istory.Family.service.FamilyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @RequestMapping("/invite-code")
    public ResponseEntity<Map<String, Object>> getInviteCode (
            HttpServletRequest request
    ) {
        return familyService.getInviteCode(request);
    }

    @RequestMapping("/invite-code-represent")
    public ResponseEntity<Map<String, Object>> getRepresentiveName (
            @RequestBody
            InviteCodeRequest inviteCodeRequest
    ) {
        return familyService.getRepresentativeName(inviteCodeRequest);
    }
}
