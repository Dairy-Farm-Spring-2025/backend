package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.MilkBatchEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IDailyMilkRepository;
import com.capstone.dfms.repositories.IMilkBatchRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.services.IDailyMilkService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class DailyMilkService implements IDailyMilkService {
    private final IDailyMilkRepository dailyMilkRepository;

    private final ICowRepository cowRepository;

    private final IUserRepository userRepository;

    private final IMilkBatchRepository milkBatchRepository;

    @Override
    public void createDailyMilk(DailyMilkEntity dailyMilk) {


        CowEntity cow = cowRepository.findById(dailyMilk.getCow().getCowId()).orElseThrow(()
                -> new AppException(HttpStatus.OK,"Cow not found"));

        long milkCountToday = dailyMilkRepository.countByCowAndMilkDate(cow, LocalDate.now());
        if (milkCountToday >= 2) {
            throw new AppException(HttpStatus.BAD_REQUEST, "The cow has already reached the daily limit of 2 milk entries.");
        }

        if (cow.getCowStatus() != CowStatus.milkingCow) {
            throw new AppException(HttpStatus.BAD_REQUEST, "The cow is not in milking state. Please update the cow status to perform the function.");
        }
        dailyMilk.setCow(cow);
        dailyMilk.setMilkDate(LocalDate.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        dailyMilk.setWorker(user);

        dailyMilkRepository.save(dailyMilk);
    }
    @Override
    public List<DailyMilkEntity> getDailyMilksByCowId(Long cowId) {
        return dailyMilkRepository.findByCowId(cowId);
    }

    @Override
    public List<DailyMilkEntity> searchDailyMilk(Long cowId, Long areaId) {
        LocalDate today = LocalDate.now();
        return dailyMilkRepository.searchDailyMilk(cowId, areaId, today);
    }

    @Override
    public void updateDailyMilkVolume(Long dailyMilkId, Long newVolume) {
        DailyMilkEntity dailyMilk = dailyMilkRepository.findById(dailyMilkId)
                .orElseThrow(() -> new DataNotFoundException("Daily Milk", "id", dailyMilkId));

        MilkBatchEntity milkBatch = dailyMilk.getMilkBatch();

        if (milkBatch != null) {
            long currentTotalVolume = milkBatch.getTotalVolume();
            long updatedTotalVolume = currentTotalVolume - dailyMilk.getVolume() + newVolume;

            milkBatch.setTotalVolume(updatedTotalVolume);
            milkBatchRepository.save(milkBatch);
        }

        dailyMilk.setVolume(newVolume);
        dailyMilkRepository.save(dailyMilk);
    }

    @Override
    public void deleteDailyMilk(long id) {
        DailyMilkEntity dailyMilk = dailyMilkRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Daily Milk", "id", id));

        if (dailyMilk.getMilkBatch() != null) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Cannot delete Daily Milk because it is associated with a Milk Batch.");
        }

        dailyMilkRepository.delete(dailyMilk);
    }

}
