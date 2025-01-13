package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "issues")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "approve_by")
    private UserEntity approveBy;

    @ManyToOne
    @JoinColumn(name = "request_by")
    private UserEntity requestBy;
}
