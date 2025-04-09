package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ExportItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IExportItemRepository extends JpaRepository<ExportItemEntity, Long> {
    List<ExportItemEntity> findByPicker_Id(Long userId);

    @Query("SELECT i.itemEntity.name, SUM(e.quantity) " +
            "FROM ExportItemEntity e " +
            "JOIN e.itemBatchEntity i " +
            "WHERE DATE(e.exportDate) = :today " +
            "GROUP BY i.itemEntity.name")
    List<Object[]> getExportedItemsByDate(@Param("today") LocalDate today);
}
