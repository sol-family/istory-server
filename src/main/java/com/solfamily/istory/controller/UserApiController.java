//package com.solfamily.istory.controller;
//
//import com.solfamily.istory.model.entity.UserEntity;
//import com.solfamily.istory.service.Impl.UserServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/user")
//@RequiredArgsConstructor
//public class UserApiController {
//
//    private final UserServiceImpl userServiceImpl;
//
//    @PostMapping("/save-user")
//    public UserEntity save(
//            @RequestBody
//            UserEntity userEntity
//    ) {
//        return userServiceImpl.save(userEntity);
//    }
//
//    @GetMapping("/find-all")
//    public List<UserEntity> findAll() {
//        return userServiceImpl.findAll();
//    }
//}
