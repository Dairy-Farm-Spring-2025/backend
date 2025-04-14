package com.capstone.dfms.repositories;

import com.capstone.dfms.models.UseEquipmentEntity;
import com.capstone.dfms.models.compositeKeys.UseEquipmentPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUseEquipmentRepository extends JpaRepository<UseEquipmentEntity, UseEquipmentPK> {
}
