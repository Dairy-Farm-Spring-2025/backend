package com.capstone.dfms.repositories;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAreaRepository extends JpaRepository<AreaEntity, Long> {
    boolean existsByName(String name);
}
