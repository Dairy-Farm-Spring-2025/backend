package com.capstone.dfms.repositories;

import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPenRepository extends JpaRepository<PenEntity, Long> {
    boolean existsByName(String name);
}
