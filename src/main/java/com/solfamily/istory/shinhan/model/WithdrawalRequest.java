package com.solfamily.istory.shinhan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest {

    private String withdrawalAccountNo;
    private long depositBalance;
}
