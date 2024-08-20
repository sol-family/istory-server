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
@Table(name="istory_family")
@Entity
public class FamilyEntity {
    @Id
    @Column(name="family_id",nullable = false)
    private String familyId;

    @Column(name="saving_account_no",nullable = false)
    private String savingAccountNo;

    @Column(name="family_nickname",nullable = false)
    private String familyNickname;

    // 대표자 유저 아이디
    @Column(name="representative_id",nullable = false)
    private String representativeId;

}
