package com.capstone.dfms.requests;

import com.alibaba.excel.annotation.ExcelProperty;
import com.capstone.dfms.components.validations.LocalDateConverter;
import com.capstone.dfms.models.enums.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CowExcelCreateRequest {
    @ExcelProperty("Name")
    private String name;
    @ExcelProperty("Cow Status")
    private String cowStatusStr;

    @ExcelProperty(value = "Date of Birth", converter = LocalDateConverter.class)
    private LocalDate dateOfBirth;

    @ExcelProperty(value = "Date of Enter", converter = LocalDateConverter.class)
    private LocalDate dateOfEnter;


    @ExcelProperty("Cow Origin")
    private String cowOriginStr;

    @ExcelProperty("Gender")
    private String genderStr;

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
