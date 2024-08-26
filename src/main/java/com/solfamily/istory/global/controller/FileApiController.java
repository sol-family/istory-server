package com.solfamily.istory.global.controller;

import com.solfamily.istory.global.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/file/*")
public class FileApiController {
    @Autowired
    private FileService service;

    @GetMapping("image")
    public ResponseEntity<Resource> showImg(String systemname) throws Exception {
        return service.getThumbnailResource(systemname);
    }

}
