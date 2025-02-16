package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationApproveRequest {
    private ApprovalStatus approvalStatus;
    private String commentApprove;
}
