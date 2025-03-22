package com.capstone.dfms.repositories;

import com.capstone.dfms.models.VaccineInjectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IVaccineInjectionRepository extends JpaRepository<VaccineInjectionEntity, Long> {
    @Query("""
        SELECT vi FROM VaccineInjectionEntity vi
        JOIN FETCH vi.cowEntity c
        JOIN FETCH vi.vaccineCycleDetail vcd
        WHERE c.id = :cowId AND vcd.id = :vaccineCycleDetailId
        ORDER BY vi.injectionDate DESC
    """)
    List<VaccineInjectionEntity> findVaccineInjectionsByCowAndVaccineCycleDetail(
            @Param("cowId") Long cowId,
            @Param("vaccineCycleDetailId") Long vaccineCycleDetailId
    );

    List<VaccineInjectionEntity> findByCowEntity_CowId(Long cowId);

}
