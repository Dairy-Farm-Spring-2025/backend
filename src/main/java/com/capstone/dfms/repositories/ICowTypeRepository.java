package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICowTypeRepository extends JpaRepository<CowTypeEntity, Long> {
    boolean existsByName(String name);
}
