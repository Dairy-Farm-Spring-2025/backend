package com.capstone.dfms.repositories;

import com.capstone.dfms.models.IllnessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IIllnessRepository extends JpaRepository<IllnessEntity, Long> {
    List<IllnessEntity> findByCowEntityCowId(Long cowId);

    @Query("SELECT i FROM IllnessEntity i LEFT JOIN FETCH i.illnessDetails WHERE i.illnessId = :id")
    Optional<IllnessEntity> findByIdWithDetails(@Param("id") Long id);
}
