package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ApplicationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IApplicationTypeRepository extends JpaRepository<ApplicationTypeEntity, Long> {
    Optional<ApplicationTypeEntity> findByName(String name);

}
