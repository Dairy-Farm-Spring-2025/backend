package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.AreaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaCreateRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    @Size(max = 255, message = "Description should not exceed 255 characters.")
    private String description;

    @Positive(message = "Length must be a positive number.")
    private Float length;

    @Positive(message = "Width must be a positive number.")
    private Float width;

    @Positive(message = "Length of pen must be a positive number.")
    private Float penLength;

    @Positive(message = "Width of pen must be a positive number.")
    private Float penWidth;

    @Positive(message = "Max of pen must be a positive number.")
    private Integer maxPen;

    private Integer numberInRow;

    @NotNull(message = "Area type is required.")
    private AreaType areaType;
}
