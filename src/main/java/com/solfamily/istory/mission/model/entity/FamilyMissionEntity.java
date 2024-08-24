package com.solfamily.istory.mission.model.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name="istory_familymission")
@Entity
public class FamilyMissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="familymission_no",nullable = false)
    private Long familymissionNo;

    @Column(name="mission_no",nullable = false)
    private Long missionNo;

    @Column(name="family_key",nullable = false)
    private String familyKey;

    @Column(name="regist_date")
    private String registDate;

    @Column(name="expiration_date")
    private String expirationDate;

    @Column(name="complete")
    private int complete;

}
