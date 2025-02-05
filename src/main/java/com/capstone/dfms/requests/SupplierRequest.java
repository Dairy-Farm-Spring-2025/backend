package com.capstone.dfms.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Phone number must start with 0 and contain 10 or 11 digits.")
    private String phone;

    @NotBlank
    @Email
    private String email;
}
