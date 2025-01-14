package com.capstone.dfms.repositories;

import com.capstone.dfms.models.WarehouseLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWarehouseLocationRepository extends JpaRepository<WarehouseLocationEntity, Long> {
}
