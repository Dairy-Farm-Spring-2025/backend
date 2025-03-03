package com.capstone.dfms.repositories;

import com.capstone.dfms.models.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ITaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("SELECT t FROM TaskEntity t WHERE t.assignee.id = :assigneeId AND t.fromDate BETWEEN :startDate AND :endDate")
    List<TaskEntity> findByAssigneeAndFromDateBetween(
            @Param("assigneeId") Long assigneeId, @Param("startDate")
            LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM TaskEntity t WHERE t.assignee.id = :assigneeId AND t.taskTypeId.taskTypeId = :taskTypeId " +
            "AND t.areaId.areaId = :areaId AND (t.fromDate BETWEEN :startDate AND :endDate OR t.toDate BETWEEN :startDate AND :endDate)")
    List<TaskEntity> findByAssigneeAndTaskTypeAndAreaAndDateRange(@Param("assigneeId") Long assigneeId,
                                                                  @Param("taskTypeId") Long taskTypeId,
                                                                  @Param("areaId") Long areaId,
                                                                  @Param("startDate") LocalDate startDate,
                                                                  @Param("endDate") LocalDate endDate);


}
