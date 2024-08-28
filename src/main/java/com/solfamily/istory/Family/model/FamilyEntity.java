package com.solfamily.istory.Family.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Entity(name = "family")
@Table(name = "istory_family")
public class FamilyEntity {

    @Id
    String familyKey;
    String savingsAccountNo;
    String repesentiveUserId;
    String familyNickName;
}
