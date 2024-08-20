package com.solfamily.istory.service;


import com.solfamily.istory.model.entity.MissionEntity;

import java.util.List;

public interface MissionService {

    MissionEntity save(String contents);

    List<MissionEntity> findAll();

}
