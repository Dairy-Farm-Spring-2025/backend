package com.capstone.dfms.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskTypeId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleId;

    private String description;
}
