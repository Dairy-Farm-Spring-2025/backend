package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMilkBatchRequest {
    private List<Long> dailyMilkIdsToAdd;
    private List<Long> dailyMilkIdsToRemove;
}
