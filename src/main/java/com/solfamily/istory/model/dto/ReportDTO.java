package com.solfamily.istory.model.dto;

import com.solfamily.istory.model.entity.id.ReportEntityId;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ReportDTO {
    private UserDTO user;
    private Long familymissionNo;
    private String thoughts;
}
