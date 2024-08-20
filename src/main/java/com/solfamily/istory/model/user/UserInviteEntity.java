package com.solfamily.istory.model.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Entity(name = "userInvite")
@Table(name = "istory_user_invite")
public class UserInviteEntity {

    @Id
    private String inviteCode;

    private String familyKey;

    private LocalDateTime expiryDate;

    private boolean isUsed;

    private LocalDateTime createdDate;
}
