package com.capstone.dfms.repositories;

import com.capstone.dfms.models.IllnessDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IIllnessDetailRepository extends JpaRepository<IllnessDetailEntity, Long> {
    List<IllnessDetailEntity> findByIllnessEntityIllnessId(Long illnessId);
}
