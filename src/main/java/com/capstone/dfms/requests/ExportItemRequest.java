package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExportItemRequest {
    private int quantity;

    private Long itemId;

    private Long taskId;
}
