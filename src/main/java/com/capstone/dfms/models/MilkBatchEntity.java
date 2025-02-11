package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.MilkBatchStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "milk_batchs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MilkBatchEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long milkBatchId;

    private Long totalVolume;

    private LocalDateTime date;

    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private MilkBatchStatus status;

    @OneToMany(mappedBy = "milkBatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyMilkEntity> dailyMilks = new ArrayList<>();

}
