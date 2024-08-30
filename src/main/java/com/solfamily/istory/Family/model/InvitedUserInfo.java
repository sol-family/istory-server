package com.solfamily.istory.Family.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvitedUserInfo implements Serializable {
    private static final long serialVersionUID = 6494678977089006639L;

    private String familyKey;
    private String representativeId;
    private List<String> memberId = new ArrayList<String>();
}
