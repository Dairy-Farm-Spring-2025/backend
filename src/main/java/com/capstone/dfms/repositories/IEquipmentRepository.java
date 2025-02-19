package com.capstone.dfms.repositories;

import com.capstone.dfms.models.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEquipmentRepository extends JpaRepository<EquipmentEntity, Long> {
    Optional<EquipmentEntity> findEquipmentEntityByName(String name);
}
