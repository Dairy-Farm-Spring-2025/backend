package com.capstone.dfms.repositories;

import com.capstone.dfms.models.TaskTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ITaskTypeRepository extends JpaRepository<TaskTypeEntity, Long> {
    boolean existsByName(String name);
    Optional<TaskTypeEntity> findByName(String name);

    @Query("SELECT t FROM TaskTypeEntity t WHERE t.name NOT IN ('Tiêm ngừa', 'Chữa bệnh')")
    List<TaskTypeEntity> findAllExcludingMedical();

}
