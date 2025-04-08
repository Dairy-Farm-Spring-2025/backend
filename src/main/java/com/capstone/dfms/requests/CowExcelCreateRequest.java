package com.capstone.dfms.requests;

import com.alibaba.excel.annotation.ExcelProperty;
import com.capstone.dfms.components.validations.LocalDateConverter;
import com.capstone.dfms.models.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CowExcelCreateRequest {
    @NotNull(message = "Name is required")
    @ExcelProperty("Name")
    private String name;

    @NotBlank(message = "Cow Status is required")
    @ExcelProperty("Cow Status")
    private String cowStatusStr;

    @NotNull(message = "Date of Birth is required")
    @ExcelProperty(value = "Date of Birth", converter = LocalDateConverter.class)
    private LocalDate dateOfBirth;

    @NotNull(message = "Date of Enter is required")
    @ExcelProperty(value = "Date of Enter", converter = LocalDateConverter.class)
    private LocalDate dateOfEnter;

    @NotBlank(message = "Cow Origin is required")
    @ExcelProperty("Cow Origin")
    private String cowOriginStr;

    @NotBlank(message = "Gender is required")
    @ExcelProperty("Gender")
    private String genderStr;

    @NotNull(message = "Cow Type is required")
    @ExcelProperty("Cow Type")
    private String cowTypeName;

    private String description;

    // Custom setters to convert String -> Enum
    public CowStatus getCowStatus() {
        return CowStatus.fromString(cowStatusStr);
    }

    public CowOrigin getCowOrigin() {
        return CowOrigin.fromString(cowOriginStr);
    }

    public Gender getGender() {
        return Gender.fromString(genderStr);
    }
}
