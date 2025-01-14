package com.capstone.dfms.repositories;

import com.capstone.dfms.models.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISupplierRepository extends JpaRepository<SupplierEntity, Long> {
}
