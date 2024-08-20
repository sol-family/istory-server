package com.solfamily.istory.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "istory_user")
@Entity
public class UserEntity {
    @Id
    @Column(name="user_id",nullable = false)
    private String userId;

    @Column(name="user_pw",nullable = false)
    private String userPw;

    @Column(name="user_name",nullable = false)
    private String userName;

    @Column(name="phone",nullable = false)
    private String phone;

    @Column(name="gender",nullable = false)
    private String gender;

    @Column(name="birth",nullable = false)
    private String birth;

    @Column(name="user_type",nullable = false)
    private String userType;

    @Column(name="user_key")
    private String userKey;

    @Column(name="join_date")
    private String joinDate;

    @Column(name="user_profile")
    private String userProfile;

    @Column(name="family_id")
    private String familyId;
}
