package com.capstone.dfms.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "feed_meals")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedMealEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedMealId;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "cow_type_id")
    private CowTypeEntity cowTypeEntity;

    @OneToMany(mappedBy = "feedMealEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedMealDetailEntity> feedMealDetails;
}
