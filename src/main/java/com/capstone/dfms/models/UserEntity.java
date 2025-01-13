package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.Gender;
import com.capstone.dfms.models.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    private String phoneNumber;

    private String employeeNumber;

    @Email
    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JsonIgnore
    private String password;

    private String address;

    private String profilePhoto;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleId;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean emailVerified;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TokenEntity> tokens;

    @JsonIgnore
    private Boolean isActive;

    @JsonIgnore
    private Boolean updateInfo;

    @JsonIgnore
    private Boolean changePassword;
}
