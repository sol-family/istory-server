package com.solfamily.istory.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class ReportEntityId implements Serializable {
    @Column(name = "user_id") // 컬럼 매핑 추가
    private String userId;

    @Column(name = "familymission_no") // 컬럼 매핑 추가
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
