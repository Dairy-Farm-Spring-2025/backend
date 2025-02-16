package com.capstone.dfms.repositories;

import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IApplicationRepository  extends JpaRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findByType(ApplicationTypeEntity type);
    List<ApplicationEntity> findByRequestBy(UserEntity requestBy);
}
