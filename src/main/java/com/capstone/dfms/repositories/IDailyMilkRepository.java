package com.capstone.dfms.repositories;

import com.capstone.dfms.models.DailyMilkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDailyMilkRepository extends JpaRepository<DailyMilkEntity, Long> {
}
