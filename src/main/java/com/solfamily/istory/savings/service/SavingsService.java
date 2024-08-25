package com.solfamily.istory.savings.service;

import com.solfamily.istory.global.ShinhanApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SavingsService {

    private final ShinhanApiService shinhanApiService;

    // 적금 상품 조회
    public ResponseEntity<String> inquireSavingsProducts() {
        return shinhanApiService.inquireSavingsProducts();
    }

    // 적금 계좌 생성
    public ResponseEntity<String> createSavingsAccount(
            String accountTypeUniqueNo, // 상품고유번호
            String depositBalance, // 가입금액
            String withdrawalAccountNo // 출금계좌번호
    ) {
        return shinhanApiService.createSavingAccount(accountTypeUniqueNo, depositBalance, withdrawalAccountNo);
    }
}
