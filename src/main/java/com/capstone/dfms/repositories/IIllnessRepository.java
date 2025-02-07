package com.capstone.dfms.repositories;

import com.capstone.dfms.models.IllnessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IIllnessRepository extends JpaRepository<IllnessEntity, Long> {
    List<IllnessEntity> findByCowEntityCowId(Long cowId);
}
