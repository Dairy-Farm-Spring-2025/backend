package com.capstone.dfms.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    private String name;

    private String address;

    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Phone number must start with 0 and contain 10 or 11 digits.")
    private String phone;

    @Email
    private String email;
}
