package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineCycleRequest {
    private String name;

    private String description;

    private Long cowTypeId;

    private List<VaccineCycleDetailRequest> details;

}
