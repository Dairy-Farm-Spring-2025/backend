package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.RepairRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "repair_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepairRequestEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repairRequestId;

    private LocalDate requestDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private RepairRequestStatus status;

    @ManyToOne
    private UserEntity userRequest;

    @ManyToOne
    private UserEntity userApprove;

}
