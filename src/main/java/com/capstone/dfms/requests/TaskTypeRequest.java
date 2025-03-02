package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskTypeRequest {
    private String name;

    private Long roleId;

    private String description;
}
