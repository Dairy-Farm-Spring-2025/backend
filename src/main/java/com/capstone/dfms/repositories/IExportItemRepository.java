package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ExportItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IExportItemRepository extends JpaRepository<ExportItemEntity, Long> {
}
