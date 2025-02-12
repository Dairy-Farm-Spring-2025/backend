package com.capstone.dfms.components.statics;

import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.models.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserStatic {
    public static UserEntity getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        return user;
    }
}
