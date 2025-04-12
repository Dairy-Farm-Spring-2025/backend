package com.capstone.dfms.repositories;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IAreaRepository extends JpaRepository<AreaEntity, Long> {
    boolean existsByName(String name);
    @Query("SELECT a FROM AreaEntity a WHERE a.name = :name")
    AreaEntity findByName(@Param("name") String name);
}
