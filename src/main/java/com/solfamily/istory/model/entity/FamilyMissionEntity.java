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
@Table(name="istory_familymission")
@Entity
public class FamilyMissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="familymission_no",nullable = false)
    private Long familymissionNo;

    @Column(name="mission_no",nullable = false)
    private Long missionNo;

    @Column(name="family_id",nullable = false)
    private String familyId;

    @Column(name="regist_date",nullable = false)
    private String registDate;

    @Column(name="expiration_date",nullable = false)
    private String expirationDate;

    @Column(name="complete",nullable = false)
    private int complete;

}
