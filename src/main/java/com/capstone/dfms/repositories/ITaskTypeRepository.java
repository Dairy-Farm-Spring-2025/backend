package com.capstone.dfms.repositories;

import com.capstone.dfms.models.TaskTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskTypeRepository extends JpaRepository<TaskTypeEntity, Long> {
}
