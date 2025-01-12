package com.capstone.dfms.models.compositeKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CowPenPK implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long penId;
    private Long cowId;
}
