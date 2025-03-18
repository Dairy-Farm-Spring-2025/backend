package com.capstone.dfms.schedules;

import com.capstone.dfms.services.impliments.VaccineInjectionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VaccineInjectionSchedule {
    private final VaccineInjectionService vaccineInjectionService;

}
