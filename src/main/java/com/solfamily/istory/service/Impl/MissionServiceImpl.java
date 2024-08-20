package com.solfamily.istory.service.Impl;

import com.solfamily.istory.db.MissionRepository;
import com.solfamily.istory.model.entity.MissionEntity;
import com.solfamily.istory.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MissionServiceImpl implements MissionService {
    @Autowired
    private MissionRepository missionRepository;

    public MissionEntity save(String contents) {
        MissionEntity missionEntity = new MissionEntity();
        missionEntity.setContents(contents);
        return missionRepository.save(missionEntity);
    }

    public List<MissionEntity> findAll() {
        return missionRepository.findAll();
    }
}
