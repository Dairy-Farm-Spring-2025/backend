package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.models.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IItemBatchRepository extends JpaRepository<ItemBatchEntity, Long> {

    @Query("SELECT ib FROM ItemBatchEntity ib WHERE ib.itemEntity.itemId = :itemId AND ib.status = :status ORDER BY ib.importDate ASC")
    List<ItemBatchEntity> findByItemEntity_ItemIdAndStatusOrderByImportDateAsc(@Param("itemId") Long itemId, @Param("status") BatchStatus status);

}
