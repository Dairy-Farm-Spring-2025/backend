package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICowTypeRepository extends JpaRepository<CowTypeEntity, Long> {
    boolean existsByName(String name);
    Optional<CowTypeEntity> findByName(String name);
}
