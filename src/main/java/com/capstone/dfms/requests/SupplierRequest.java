package com.capstone.dfms.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierRequest {

    private String name;

    private String address;

    private String phone;

    @NotBlank
    @Email
    private String email;
}
