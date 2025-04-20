package com.capstone.dfms.repositories;

import com.capstone.dfms.models.MilkBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IMilkBatchRepository extends JpaRepository<MilkBatchEntity, Long> {

}
