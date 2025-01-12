package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_records")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthRecord extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long healthRecordId;

    private HealthRecordStatus status;
    private float weight;
    private float size;
    private CowStatus period;

    @ManyToOne
    @JoinColumn(name = "cow_id")
    private CowEntity cowEntity;
}
