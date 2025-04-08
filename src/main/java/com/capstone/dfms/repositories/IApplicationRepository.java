package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IApplicationRepository  extends JpaRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findByType(ApplicationTypeEntity type);
    List<ApplicationEntity> findByRequestBy(UserEntity requestBy);

    boolean existsByRequestByAndFromDateAndToDateAndType(
            UserEntity requestBy, LocalDate fromDate, LocalDate toDate, ApplicationTypeEntity type);

    @Query("SELECT a FROM ApplicationEntity a WHERE a.requestBy.id = :userId " +
            "AND a.type.name = 'Đơn xin nghỉ phép' " +
            "AND a.fromDate <= :toDate " +
            "AND a.toDate >= :fromDate")
    ApplicationEntity findByUserAndOverlappingDateRange(@Param("userId") Long userId,
                                                              @Param("fromDate") LocalDate fromDate,
                                                              @Param("toDate") LocalDate toDate);

    @Query("SELECT a FROM ApplicationEntity a WHERE a.status = :status")
    List<ApplicationEntity> findByStatus(@Param("status") ApplicationStatus status);

}
