package com.capstone.dfms.models;

import com.capstone.dfms.models.compositeKeys.CowPenPK;
import com.capstone.dfms.models.enums.PenCowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cow_pens")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowPenEntity extends BaseEntity{
    @EmbeddedId
    private CowPenPK id;

    @ManyToOne
    @MapsId("cowId")
    @JoinColumn(name = "cow_id")
    private CowEntity cowEntity;

    @ManyToOne
    @MapsId("penId")
    @JoinColumn(name = "pen_id")
    private PenEntity penEntity;

    private LocalDateTime toDate;

    @Enumerated(EnumType.STRING)
    private PenCowStatus status;
}
