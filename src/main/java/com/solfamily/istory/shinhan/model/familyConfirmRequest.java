package com.solfamily.istory.shinhan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class familyConfirmRequest {

    private String inviteCode;
    private String withdrawalAccountNo;
    private long depositBalance;
}
