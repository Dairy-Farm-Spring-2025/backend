package com.capstone.dfms.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {
    @NotEmpty(message = "Please enter old password")
    private String oldPassword;

    @NotEmpty(message = "Please enter new password")
    private String newPassword;

    @NotEmpty(message = "Please re-enter new password")
    private String confirmedPassword;
}
