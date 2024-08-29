package com.solfamily.istory.Family.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvitedUserInfo {

    private String familyKey;
    private String representativeId;
    private List<String> memberId = new ArrayList<String>();
}
