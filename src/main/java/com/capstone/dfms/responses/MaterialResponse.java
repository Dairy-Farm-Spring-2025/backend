package com.capstone.dfms.responses;

import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.models.VaccineInjectionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialResponse {
    private IllnessDetailEntity illnessDetail;
    private VaccineInjectionEntity vaccineInjection;
    private IllnessEntity illness;
}
