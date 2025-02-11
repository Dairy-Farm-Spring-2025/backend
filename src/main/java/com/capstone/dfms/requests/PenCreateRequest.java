package com.capstone.dfms.requests;


import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.models.enums.PenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenCreateRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotNull(message = "Pen type is required.")
    private PenType penType;

//    @Positive(message = "Length must be a positive number.")
//    private float length;
//
//    @Positive(message = "Width must be a positive number.")
//    private float width;

    @NotNull(message = "Pen status is required.")
    private PenStatus penStatus;

    @NotNull(message = "Area ID is required.")
    @Positive(message = "Area ID must be a positive number.")
    private Long areaId;
}
