package com.capstone.dfms.repositories;

import com.capstone.dfms.models.MilkBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMilkBatchRepository extends JpaRepository<MilkBatchEntity, Long> {

}
