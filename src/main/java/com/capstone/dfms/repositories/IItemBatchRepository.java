package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ItemBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IItemBatchRepository extends JpaRepository<ItemBatchEntity, Long> {
}
