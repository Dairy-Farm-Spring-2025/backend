package com.capstone.dfms.repositories;

import com.capstone.dfms.models.IllnessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIllnessRepository extends JpaRepository<IllnessEntity, Long> {
}
