package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.MilkBatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private MilkBatchStatus status;

    private Long volume;



//    @OneToMany(mappedBy = "milkBatch", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<DailyMilkEntity> dailyMilks;


}
