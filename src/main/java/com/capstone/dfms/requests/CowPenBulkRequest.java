package com.capstone.dfms.requests;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.PenEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowPenBulkRequest {
    private List<Long> cowEntities;
    private List<Long> penEntities;
}
