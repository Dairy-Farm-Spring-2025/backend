package com.capstone.dfms.schedules;

import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.repositories.ICowPenRepository;
import com.capstone.dfms.repositories.IPenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class CowPenSchedule {
    private final IPenRepository penRepository;
    private final ICowPenRepository cowPenRepository;

    @Scheduled(fixedRate = 43200000) // Runs every 60 seconds
    @Transactional
    public void updatePenStatus() {
        List<PenEntity> pens = penRepository.getPenWithStatusInPlanning();
        LocalDate currentDate = LocalDate.now(); // Get today's date

        for (PenEntity pen : pens) {
            boolean isOccupied = penRepository.isOccupiedPen(pen.getPenId(), currentDate);
            if (isOccupied && pen.getPenStatus() != PenStatus.occupied) {
                pen.setPenStatus(PenStatus.occupied);
                penRepository.save(pen);
            }
        }
    }
}
