package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.FeedMealShift;
import com.capstone.dfms.models.enums.ItemUnit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    private BigDecimal quantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity itemEntity;


    @ManyToOne
    @JoinColumn(name = "feed_meal_id")
    @JsonIgnore
    private FeedMealEntity feedMealEntity;
}
