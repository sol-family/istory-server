package com.solfamily.istory.savings.controller;

import com.solfamily.istory.savings.service.SavingsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/savings")
@RequiredArgsConstructor
public class SavingsController {
    private final SavingsService savingsService;
//    // 적금 계좌 생성
//    @PostMapping("/savingsaccount")
//    public  ResponseEntity<Map<String,String>> createSavingsAccount(
//            HttpServletRequest request,
//            String withdrawalAccountNo,
//            long depositBalance)
//    {
//        return savingsService.createSavingsAccount(request, withdrawalAccountNo, depositBalance);
//    }
}
