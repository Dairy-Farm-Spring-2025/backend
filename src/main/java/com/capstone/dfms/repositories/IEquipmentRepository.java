package com.capstone.dfms.repositories;

import com.capstone.dfms.models.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEquipmentRepository extends JpaRepository<EquipmentEntity, Long> {
    Optional<EquipmentEntity> findEquipmentEntityByName(String name);

    @Query("SELECT e FROM EquipmentEntity e WHERE e.warehouseLocationEntity.warehouseLocationId = :locationId")
    List<EquipmentEntity> findByWarehouseLocation(@Param("locationId") Long locationId);

}
