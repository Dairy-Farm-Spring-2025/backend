package com.capstone.dfms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "illness-images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IllnessMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long illnessImageId;

    private String url;

//    @Enumerated(EnumType.STRING)
    private String type;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "illness_id ")
    private IllnessEntity illness;
}
