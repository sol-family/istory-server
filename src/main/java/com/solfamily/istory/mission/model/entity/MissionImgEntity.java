package com.solfamily.istory.mission.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="istory_missionImg")
@Entity
public class MissionImgEntity {
    @Id
    @Column(name="systemname",nullable = false)
    private String systemname;

    @Column(name="originname",nullable = false)
    private String originname;

    @Column(name="familymission_no",nullable = false)
    private Long familymissionNo;
}
