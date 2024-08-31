package com.solfamily.istory.Family.controller;

import com.solfamily.istory.Family.model.InviteCodeRequest;
import com.solfamily.istory.shinhan.model.familyConfirmRequest;
import com.solfamily.istory.Family.service.FamilyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping("/invite-code")
    public ResponseEntity<Map<String, Object>> getInviteCode(
            HttpServletRequest request
    ) {
        return familyService.getInviteCode(request);
    }

    @PostMapping("/invite-code-represent")
    public ResponseEntity<Map<String, Object>> getRepresentativeName(
            @RequestBody
            InviteCodeRequest inviteCodeRequest
    ) {
        return familyService.getRepresentativeName(inviteCodeRequest);
    }

    @PostMapping("/invite-accept")
    public ResponseEntity<Map<String, Object>> acceptInvite(
            @RequestBody
            InviteCodeRequest inviteCodeRequest,
            HttpServletRequest request
    ) {
        return familyService.acceptInvite(request, inviteCodeRequest);
    }

    @PostMapping("/invite-cancel")
    public ResponseEntity<Map<String, Object>> cancelInvite(
            @RequestBody
            InviteCodeRequest inviteCodeRequest
    ) {
        return familyService.expiredInvitedStatus(inviteCodeRequest);
    }

    @PostMapping("/all-userInfo")
    public ResponseEntity<Map<String, Object>> getAllUserInfo(
            @RequestBody
            InviteCodeRequest inviteCodeRequest
    ) {
        return familyService.getAllUserInfo(inviteCodeRequest);
    }

    @PostMapping("/exclude-user")
    public ResponseEntity<Map<String, Object>> excludeUser(
            @RequestBody
            InviteCodeRequest inviteCodeRequest,
            HttpServletRequest request
    ) {
        return familyService.excludeUser(request, inviteCodeRequest);
    }

    @PostMapping("/confirm-family")
    public ResponseEntity<Map<String, Object>> confirmFamily(
            HttpServletRequest request,
            @RequestBody
            familyConfirmRequest familyConfirmRequest
    ) {
        return familyService.confirmFamily(request, familyConfirmRequest);
    }

}