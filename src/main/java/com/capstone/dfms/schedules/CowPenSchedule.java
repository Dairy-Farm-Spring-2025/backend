package com.capstone.dfms.schedules;

import com.capstone.dfms.models.CowPenEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.PenCowStatus;
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

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    @Transactional
    public void updatePenStatus() {
        this.setEmptyForAvailablePen();
        this.setOccupiedForAssignedPen();
        this.setAssignerForCowPen();
    }

    private void setOccupiedForAssignedPen(){
        List<PenEntity> inPlanningPens = penRepository.getPenWithStatus(PenStatus.inPlaning);
        LocalDate currentDate = LocalDate.now(); // Get today's date

        for (PenEntity pen : inPlanningPens) {
            boolean isOccupied = penRepository.isOccupiedPen(pen.getPenId(), currentDate);
            if (isOccupied && pen.getPenStatus() != PenStatus.occupied) {
                pen.setPenStatus(PenStatus.occupied);
                penRepository.save(pen);
            }
        }
    }

    private void setEmptyForAvailablePen(){
        LocalDate currentDate = LocalDate.now(); // Get today's date
        List<CowPenEntity> expiredPen = cowPenRepository.findToDateBefore(currentDate);
        for (CowPenEntity cowPen : expiredPen) {
            PenEntity pen = cowPen.getPenEntity();

            boolean isOccupied = penRepository.isAvailablePen(pen.getPenId(), currentDate);
            if (isOccupied && pen.getPenStatus() != PenStatus.empty) {
                pen.setPenStatus(PenStatus.empty);
                penRepository.save(pen);

                cowPen.setStatus(PenCowStatus.expired);
                cowPenRepository.save(cowPen);
            }
        }
    }

    private void setAssignerForCowPen(){
        LocalDate currentDate = LocalDate.now(); // Get today's date
        currentDate.plusDays(1);
        List<CowPenEntity> planningCowPen = cowPenRepository.findByStatus(PenCowStatus.planning);

        for(CowPenEntity cowPen: planningCowPen){
            if(cowPen.getId().getFromDate().isBefore(currentDate)){
                cowPen.setStatus(PenCowStatus.assigned);

                cowPenRepository.save(cowPen);
            }
        }
    }
}
