package com.capstone.dfms.repositories;

import com.capstone.dfms.models.TaskTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITaskTypeRepository extends JpaRepository<TaskTypeEntity, Long> {
    boolean existsByName(String name);
    Optional<TaskTypeEntity> findByName(String name);

}
