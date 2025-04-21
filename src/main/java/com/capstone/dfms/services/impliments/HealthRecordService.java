package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.CowUtlis;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.IHealthReportMapper;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IHealthRecordRepository;
import com.capstone.dfms.requests.HealthReportRequest;
import com.capstone.dfms.responses.CowHealthInfoResponse;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.services.IHealthRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.not.found")));

        CowUtlis.validateCow(cow);
        LocalDate reportDate = LocalDate.now();
        LocalDateTime startOfDay = reportDate.atStartOfDay();
        LocalDateTime endOfDay = reportDate.atTime(LocalTime.MAX); // End of the day

        // Check if a report for the same cow on the same date already exists
        if (healthRecordRepository.existsByCowEntity_CowIdAndReportTimeBetween(cow.getCowId(), startOfDay, endOfDay)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("health.report.exists", cow.getCowId(), reportDate));
        }

        HealthRecordEntity entity = mapper.toModel(request);

        entity.setCowEntity(cow);
        entity.setReportTime(LocalDateTime.now());
        entity.setWeight(90 * (request.getChestCircumference()*request.getChestCircumference()*request.getBodyLength()));

        cow.setCowStatus(request.getPeriod());
        cowRepository.save(cow);

        return healthRecordRepository.save(entity);
    }

    @Override
    public HealthRecordEntity getHealthReportById(Long id) {
        return healthRecordRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("health.report.not.exist")));
    }

    @Override
    public List<HealthRecordEntity> getAllHealthReports() {
        return healthRecordRepository.findAll();
    }

    @Override
    public HealthRecordEntity updateHealthReport(Long id, HealthReportRequest request) {
        if(request.getCowId() != null){
            CowEntity cow = cowRepository.findById(request.getCowId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.not.found")));
        }

        HealthRecordEntity oldEntity = this.getHealthReportById(id);

        // Check if the update is within 1 day of reportTime
        if (oldEntity.getReportTime() != null) {
            LocalDateTime oneDayAfterReport = oldEntity.getReportTime().plusDays(1);
            if (LocalDateTime.now().isAfter(oneDayAfterReport)) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("health.record.update.exceeded"));
            }
        }

        mapper.updateEntityFromDto(request, oldEntity);
        oldEntity.setWeight(90 * (oldEntity.getChestCircumference()*oldEntity.getChestCircumference()*oldEntity.getBodyLength()));

        CowEntity existingCow = oldEntity.getCowEntity();

        existingCow.setCowStatus(oldEntity.getPeriod());
        cowRepository.save(existingCow);

        return healthRecordRepository.save(oldEntity);
    }

    @Override
    public void deleteHealthReport(Long id) {
        HealthRecordEntity existingRecord = getHealthReportById(id);
        healthRecordRepository.delete(existingRecord);
    }

    @Override
    public CowPenBulkResponse<HealthRecordEntity> createBulkHealthReport(List<HealthReportRequest> requests) {
        List<HealthRecordEntity> healthRecordEntities = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        requests.forEach(request -> {
            try{
                healthRecordEntities.add(this.createHealthReport(request));
            }
            catch (Exception exception){
                errors.add("Error create health record with cow: " + request.getCowId());
            }
        });

        return CowPenBulkResponse.<HealthRecordEntity>builder()
                .successes(healthRecordEntities)
                .errors(errors)
                .build();
    }
}
