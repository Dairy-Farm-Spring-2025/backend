package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.requests.CreateAccountRequest;
import com.capstone.dfms.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.capstone.dfms.mappers.UserMapper.INSTANCE;
@RestController
@RequestMapping("${app.api.version.v1}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @PostMapping("/create")
    public CoreApiResponse<?> createAccountManager(
            @Valid @RequestBody CreateAccountRequest accountRequest
    ){
         userService.createAccount(INSTANCE.toModel(accountRequest));
        return CoreApiResponse.success("Create account successfully");
    }

}
