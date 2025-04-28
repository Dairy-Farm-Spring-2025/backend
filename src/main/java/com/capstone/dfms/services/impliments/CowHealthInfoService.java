package com.capstone.dfms.services.impliments;

import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.repositories.IHealthRecordRepository;
import com.capstone.dfms.repositories.IIllnessRepository;
import com.capstone.dfms.responses.CowHealthInfoResponse;
import com.capstone.dfms.services.ICowHealthInfoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class CowHealthInfoService implements ICowHealthInfoService {
    private final IHealthRecordRepository healthRecordRepository;
    private final IIllnessRepository illnessRepository;

    @Override
    public List<CowHealthInfoResponse<?>> getAllHealthInfoOrderedDesc(Long cowId) {
        List<HealthRecordEntity> healthRecords = healthRecordRepository.findByCowEntityCowId(cowId);
        List<IllnessEntity> illnesses = illnessRepository.findByCowEntityCowId(cowId);

        List<CowHealthInfoResponse<?>> responses = new ArrayList<>();

        for (HealthRecordEntity record : healthRecords) {
            LocalDate reportDate = record.getReportTime().toLocalDate();
            CowHealthInfoResponse<HealthRecordEntity> response = CowHealthInfoResponse.<HealthRecordEntity>builder()
                    .id(record.getHealthRecordId())
                    .type("HEALTH_RECORD")
                    .date(reportDate)
                    .health(record)
                    .build();
            responses.add(response);
        }

        for (IllnessEntity illness : illnesses) {
            LocalDate startDate = illness.getStartDate();
            CowHealthInfoResponse<IllnessEntity> response = CowHealthInfoResponse.<IllnessEntity>builder()
                    .id(illness.getIllnessId())
                    .type("ILLNESS")
                    .date(startDate)
                    .health(illness)
                    .build();
            responses.add(response);
        }

        responses.sort(
                Comparator.comparing(
                        (CowHealthInfoResponse<?> r) -> r.getDate(),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );


        return responses;
    }
}
