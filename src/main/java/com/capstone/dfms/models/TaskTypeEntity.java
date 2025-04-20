package com.capstone.dfms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(mappedBy = "taskType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnoreProperties("taskType")
    private List<UseEquipmentEntity> useEquipments;
}
