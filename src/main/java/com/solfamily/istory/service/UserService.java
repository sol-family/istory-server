package com.solfamily.istory.service;

import com.solfamily.istory.model.dto.UserDTO;

import java.util.List;

public interface UserService {
    String getFamilyIdByUserId(String userId);

    List<UserDTO> getMemberByFamilyId(String familyId);
}
