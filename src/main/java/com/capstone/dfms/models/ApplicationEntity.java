package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "applications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    private String title;

    @Column(length = 10000)
    private String content;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDate fromDate;
    private LocalDate toDate;

    @Column(length = 1000)
    private String commentApprove;

    private LocalDate requestDate;
    private LocalDate approveDate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private ApplicationTypeEntity type;

    @ManyToOne
    @JoinColumn(name = "approve_by")
    private UserEntity approveBy;

    @ManyToOne
    @JoinColumn(name = "request_by")
    private UserEntity requestBy;
}
