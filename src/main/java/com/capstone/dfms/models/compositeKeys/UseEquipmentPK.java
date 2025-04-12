package com.capstone.dfms.models.compositeKeys;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UseEquipmentPK implements Serializable {
    private long equipmentId;

    private long taskTypeId;
}
