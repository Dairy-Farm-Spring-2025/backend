package com.capstone.dfms.requests;

import com.capstone.dfms.models.compositeKeys.CowPenPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowPenMovingRequest {
    private CowPenPK oldCowPen;
    private CowPenCreateRequest newCowPen;
}
