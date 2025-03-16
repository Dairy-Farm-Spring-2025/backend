package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ReportTaskImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReportTaskImageRepository  extends JpaRepository<ReportTaskImageEntity, Long> {
}
