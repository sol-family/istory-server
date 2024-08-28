package com.solfamily.istory.global.service;

import com.solfamily.istory.mission.db.FamilyMissionRepository;
import com.solfamily.istory.mission.db.MissionImgRepository;
import com.solfamily.istory.mission.db.MissionRepository;
import com.solfamily.istory.mission.model.entity.MissionImgEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
public class FileService {
    @Value("${FILE_DIRECTORY}")
    private String saveFolder;

    public ResponseEntity<Resource> getThumbnailResource(String systemname) throws Exception {
        Path path = Paths.get(saveFolder+systemname);
        String contentType = Files.probeContentType(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return new ResponseEntity<>(resource,headers, HttpStatus.OK);
    }

    public ResponseEntity<Map> uploadImg(MultipartFile image) {
        if (image == null) {
            return ResponseEntity.ok(Collections.singletonMap("result", "false"));
        }
        try {
            String orgName = image.getOriginalFilename();

            int lastIdx = orgName.lastIndexOf(".");
            String extension = orgName.substring(lastIdx);

            LocalDateTime now = LocalDateTime.now();
            String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String systemname = time + UUID.randomUUID() + extension;
            String path = saveFolder+systemname;
            image.transferTo(new File(path));
            return ResponseEntity.ok(Collections.singletonMap("result", "true"));
        }catch (Exception e){
            return ResponseEntity.ok(Collections.singletonMap("result", "false"));
        }
    }
}
