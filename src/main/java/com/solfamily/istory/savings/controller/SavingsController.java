package com.solfamily.istory.savings.controller;

import com.solfamily.istory.global.ShinhanApiService;
import com.solfamily.istory.savings.service.SavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savings")
@RequiredArgsConstructor
public class SavingsController {

    private final SavingsService savingsService;

    // 적금 상품 조회
    @RequestMapping("/inquireProducts")
    public ResponseEntity<String> inquireProducts() {
        return savingsService.inquireSavingsProducts();
    }

    // 적금 계좌 생성
    @RequestMapping("/createSavingAccount")
    public  ResponseEntity<String> createSavingsAccount(
            String accountTypeUniqueNo, // 상품고유번호
            String depositBalance, // 가입금액
            String withdrawalAccountNo // 출금계좌번호
    ) {
        return savingsService.createSavingsAccount(accountTypeUniqueNo, depositBalance, withdrawalAccountNo);
    }
}
