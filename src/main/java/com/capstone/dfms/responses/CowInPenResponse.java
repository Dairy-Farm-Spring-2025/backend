package com.capstone.dfms.responses;

import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.PenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowInPenResponse {
    private Long cowId;
    private String name;
    private CowStatus cowStatus;
    private String cowType;
    private Long penId;
    private String penName;
    private PenStatus penStatus;

}
