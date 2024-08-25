package com.solfamily.istory.mission.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class ReportEntityId implements Serializable {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "familymission_no")
    private Long familymissionNo;

    public ReportEntityId() {}

    public ReportEntityId(String userId, Long familymissionNo) {
        this.userId = userId;
        this.familymissionNo = familymissionNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportEntityId that = (ReportEntityId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(familymissionNo, that.familymissionNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, familymissionNo);
    }
}
