package com.capstone.dfms.components.utils;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.services.ICowServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CowUtlis {
    private static ICowRepository repository;

    public static void validateCow(CowEntity cow){
        if(cow.getCowStatus().equals(CowStatus.culling)){
            throw new AppException(HttpStatus.BAD_REQUEST, "Cow is out system!!");
        }
    }
}
