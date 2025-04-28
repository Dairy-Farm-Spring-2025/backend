package com.capstone.dfms.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowHealthInfoResponse<T>{
    private Long id;
    private String type;
    private LocalDate date;
    private T health;
}
