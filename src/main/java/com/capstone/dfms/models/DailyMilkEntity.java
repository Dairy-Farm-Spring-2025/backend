package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.MilkShift;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(name = "daily_milks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyMilkEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyMilkId;

    @Enumerated(EnumType.STRING)
    private MilkShift shift;

    private LocalDate milkDate;

    @ManyToOne
    @JoinColumn(name = "milk_batch_id", nullable = true)
    @JsonBackReference
    private MilkBatchEntity milkBatch;

    private Long volume;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private UserEntity worker;

    @ManyToOne
    @JoinColumn(name = "cow_id")
    private CowEntity cow;
}
