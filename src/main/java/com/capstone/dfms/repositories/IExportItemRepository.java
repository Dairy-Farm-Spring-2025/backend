package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ExportItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IExportItemRepository extends JpaRepository<ExportItemEntity, Long> {
    List<ExportItemEntity> findByPicker_Id(Long userId);
}
