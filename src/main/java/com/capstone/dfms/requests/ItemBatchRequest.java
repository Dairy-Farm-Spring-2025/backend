package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemBatchRequest {
    private float quantity;

    private LocalDate expiryDate;

    private Long itemId;

    private Long supplierId;

}
