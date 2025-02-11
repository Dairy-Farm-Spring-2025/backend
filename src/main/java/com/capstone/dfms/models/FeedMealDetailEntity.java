package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ItemUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feed_meal_details")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedMealDetailEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedMealDetailId;

    @Enumerated(EnumType.STRING)
    private ItemUnit quantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity itemEntity;

    @ManyToOne
    @JoinColumn(name = "feed_meal_id")
    private FeedMealEntity feedMealEntity;
}
