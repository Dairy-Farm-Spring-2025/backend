package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.SwapStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "swap_schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SwapScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    private String reason;

    private LocalDate startTime;

    private LocalDate endTime;

    private LocalDate requestDate;

    @ManyToOne
    @JoinColumn(name = "approve_by")
    private UserEntity approveBy;

    @Enumerated(EnumType.STRING)
    private SwapStatus status;

    @ManyToOne
    @JoinColumn(name = "request_by")
    private UserEntity requestBy;

    @ManyToOne
    @JoinColumn(name = "receive_by")
    private UserEntity receiveBy;
}
