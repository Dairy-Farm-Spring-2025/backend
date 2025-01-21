package com.capstone.dfms.responses;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowPenBulkResponse<T> {
    private List<T> successes;
    private List<String> errors;
}
