package com.capstone.dfms.repositories;

import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.roleId.id = :roleId")
    List<UserEntity> findByRoleId(@Param("roleId") Long roleId);

    boolean existsByRoleId(RoleEntity role);

    @Query("""
    SELECT u FROM UserEntity u
    WHERE u.roleId.id = 3
      AND NOT EXISTS (
          SELECT t FROM TaskEntity t
          WHERE t.assignee = u
            AND t.taskTypeId.name = 'Khám định kì'
            AND :today BETWEEN t.fromDate AND t.toDate
      )
""")
    List<UserEntity> findAvailableVet(@Param("today") LocalDate today);

    @Query("SELECT u FROM UserEntity u WHERE u.roleId.id = :roleId AND u.isActive = true")
    List<UserEntity> findAllActiveUsersByRoleId(@Param("roleId") Long roleId);

}
