package com.capstone.dfms.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String name;
}
