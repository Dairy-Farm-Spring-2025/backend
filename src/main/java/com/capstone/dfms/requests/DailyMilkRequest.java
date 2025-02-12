package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyMilkRequest {

    private Long volume;

    private Long cowId;
}
