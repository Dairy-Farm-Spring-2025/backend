package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyMilkRequest {
    private String shift;

    private Long volume;

    private Long cowId;
}
