package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.mappers.IHealthReportMapper;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IHealthRecordRepository;
import com.capstone.dfms.requests.HealthReportRequest;
import com.capstone.dfms.services.IHealthRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class HealthRecordService implements IHealthRecordService{
    private final IHealthRecordRepository healthRecordRepository;
    private final ICowRepository cowRepository;
    private final IHealthReportMapper mapper;



    @Override
    public HealthRecordEntity createHealthReport(HealthReportRequest request) {
        // Validate that the referenced Cow exists
        CowEntity cow = cowRepository.findById(request.getCowId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Cow not found with id " + request.getCowId()));

        LocalDate reportDate = LocalDate.now();
        LocalDateTime startOfDay = reportDate.atStartOfDay();
        LocalDateTime endOfDay = reportDate.atTime(LocalTime.MAX); // End of the day

        // Check if a report for the same cow on the same date already exists
        if (healthRecordRepository.existsByCowEntity_CowIdAndReportTimeBetween(cow.getCowId(), startOfDay, endOfDay)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Health report already exists for cow " + cow.getCowId() + " on date " + reportDate);
        }

        HealthRecordEntity entity = mapper.toModel(request);

        entity.setCowEntity(cow);
        entity.setReportTime(LocalDateTime.now());

        return healthRecordRepository.save(entity);
    }

    @Override
    public HealthRecordEntity getHealthReportById(Long id) {
        return healthRecordRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Health record not found with id " + id));
    }

    @Override
    public List<HealthRecordEntity> getAllHealthReports() {
        return healthRecordRepository.findAll();
    }

    @Override
    public HealthRecordEntity updateHealthReport(Long id, HealthReportRequest request) {
        if(request.getCowId() != null){
            CowEntity cow = cowRepository.findById(request.getCowId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Cow not found with id " + request.getCowId()));
        }

        HealthRecordEntity oldEntity = this.getHealthReportById(id);
        mapper.updateEntityFromDto(request, oldEntity);

        return healthRecordRepository.save(oldEntity);
    }

    @Override
    public void deleteHealthReport(Long id) {
        HealthRecordEntity existingRecord = getHealthReportById(id);
        healthRecordRepository.delete(existingRecord);
    }
}
