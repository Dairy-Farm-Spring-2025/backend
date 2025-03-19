package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.models.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IReportTaskRepository extends JpaRepository<ReportTaskEntity, Long> {
    @Query("SELECT COUNT(r) > 0 FROM ReportTaskEntity r WHERE r.taskId.taskId = :taskId AND r.date = :date")
    boolean existsByTaskAndDate(@Param("taskId") Long taskId, @Param("date") LocalDate date);

    List<ReportTaskEntity> findByTaskId(TaskEntity task);

    @Query("SELECT r FROM ReportTaskEntity r WHERE r.taskId = :task AND r.date = :date")
    ReportTaskEntity findByTaskIdAndDate(@Param("task") TaskEntity task, @Param("date") LocalDate date);

    @Query("SELECT r FROM ReportTaskEntity r WHERE r.taskId.assignee.id = :userId AND r.date BETWEEN :startDate AND :endDate")
    List<ReportTaskEntity> findReportTasksByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM ReportTaskEntity r WHERE r.date BETWEEN :startDate AND :endDate")
    List<ReportTaskEntity> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<ReportTaskEntity> findByDate(LocalDate date);


}
