package com.capstone.dfms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    private boolean revoked;

    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;
}
