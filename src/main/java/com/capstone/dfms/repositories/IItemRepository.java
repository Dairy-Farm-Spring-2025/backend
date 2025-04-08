package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IItemRepository extends JpaRepository<ItemEntity, Long> {
    @Query("SELECT i FROM ItemEntity i WHERE i.categoryEntity.categoryId = :categoryId")
    List<ItemEntity> findItemsByCategoryId(@Param("categoryId") Long categoryId);


    @Query("SELECT i FROM ItemEntity i WHERE i.warehouseLocationEntity.warehouseLocationId = :locationId")
    List<ItemEntity> findItemsByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT i FROM ItemEntity i WHERE i.categoryEntity.name = :categoryName")
    List<ItemEntity> findItemsByCategoryName(@Param("categoryName") String categoryName);
}
