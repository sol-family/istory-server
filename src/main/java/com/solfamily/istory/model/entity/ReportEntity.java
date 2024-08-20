package com.solfamily.istory.model.entity;

import com.solfamily.istory.model.entity.id.ReportEntityId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="istory_report")
@Entity
public class ReportEntity {
    @EmbeddedId
    private ReportEntityId id;

    @Column(name="thoughts",nullable = false)
    private String thoughts;

    @Column(name="write_date",nullable = false)
    private String write_date;

    @Column(name="complete",nullable = false)
    private int complete;
}