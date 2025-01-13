package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICowRepository extends JpaRepository<CowEntity, Long> {
    boolean existsByName(String name);
}
