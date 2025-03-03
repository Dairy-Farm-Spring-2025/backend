package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.models.enums.PenType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pens")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long penId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private PenStatus penStatus;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private AreaEntity areaBelongto;
}
