package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IDailyMilkRepository;
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

    @Override
    public DailyMilkEntity createDailyMilk(DailyMilkEntity dailyMilk) {
        CowEntity cow = cowRepository.findById(dailyMilk.getCow().getCowId()).orElseThrow(()
                -> new AppException(HttpStatus.OK,"Cow not found"));

//        UserEntity worker = userRepository.findById(dailyMilk.getWorker().getId()).orElseThrow(()
//                -> new AppException(HttpStatus.OK,"Worker not found"));
        dailyMilk.setCow(cow);
//        dailyMilk.setWorker(worker);
        dailyMilk.setMilkDate(LocalDate.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        dailyMilk.setWorker(user);
        return dailyMilkRepository.save(dailyMilk);
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

}
