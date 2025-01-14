package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowPenEntity;
import com.capstone.dfms.models.compositeKeys.CowPenPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICowPenRepository extends JpaRepository<CowPenEntity, CowPenPK> {
    List<CowPenEntity> findByIdCowId(Long cowId);

    // Find all cow pens by pen ID
    List<CowPenEntity> findByIdPenId(Long penId);

    // Custom query to find active cow pens
    @Query("SELECT c FROM CowPenEntity c WHERE c.status = 'ACTIVE'")
    List<CowPenEntity> findActiveCowPens();
}
