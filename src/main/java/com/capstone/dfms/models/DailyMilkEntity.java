package com.capstone.dfms.models;

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

    private LocalDate milkDate;

    @ManyToOne
    @JoinColumn(name = "milk_batch_id", nullable = false)
    @JsonBackReference
    private MilkBatchEntity milkBatch;
}
