package com.capstone.dfms.models.compositeKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CowPenPK implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long penId;
    private Long cowId;
    private LocalDate fromDate;
}
