package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVaccineCycleRequest {
    private String name;

    private String description;

    private List<VaccineCycleDetailRequest> details;

    private List<UpdateVaccineCycleDetailRequest> updateDetail;

    private List<Long> delete;

}
