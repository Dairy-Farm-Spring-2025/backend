package com.capstone.dfms.components.validations;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateConverter implements Converter<LocalDate> {
    @Override
    public Class<LocalDate> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public LocalDate convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        try {
            if (cellData.getNumberValue() != null) {
                // If Excel stores the date as a number, convert it
                Date date = new Date(cellData.getNumberValue().longValue());
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            // If Excel stores the date as text, parse it
            return LocalDate.parse(cellData.getStringValue());
        } catch (Exception e) {
            throw new RuntimeException("Error converting Excel date: " + cellData.getStringValue(), e);
        }
    }
}
