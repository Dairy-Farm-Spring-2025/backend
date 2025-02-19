package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.CowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DryMatterRequest {
    private CowStatus cowStatus;
    private Long cowTypeId;
}
