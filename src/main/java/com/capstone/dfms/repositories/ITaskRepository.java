package com.capstone.dfms.repositories;

import com.capstone.dfms.models.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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


    @Query("SELECT t FROM TaskEntity t WHERE t.fromDate <= :toDate AND t.toDate >= :fromDate")
    List<TaskEntity> findTasksInDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT t FROM TaskEntity t WHERE t.assignee.id = :userId AND t.fromDate <= :toDate AND t.toDate >= :fromDate")
    List<TaskEntity> findMyTasksInDateRange(@Param("userId") Long userId,
                                            @Param("fromDate") LocalDate fromDate,
                                            @Param("toDate") LocalDate toDate);

    @Query("SELECT t FROM TaskEntity t WHERE t.taskId = :taskId AND t.assignee.id = :userId")
    Optional<TaskEntity> findMyTaskById(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("SELECT t FROM TaskEntity t WHERE t.assignee.id = :userId")
    List<TaskEntity> findMyTasks(@Param("userId") Long userId);
}
