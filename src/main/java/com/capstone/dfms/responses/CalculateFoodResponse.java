package com.capstone.dfms.responses;

import com.capstone.dfms.models.enums.ItemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculateFoodResponse {
    private String name;
    private ItemUnit unit;
    private BigDecimal quantityNeeded;
}
