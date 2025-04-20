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
                double excelDate = cellData.getNumberValue().doubleValue();

                long milliseconds = (long) ((excelDate - 25569) * 86400 * 1000);
                Date date = new Date(milliseconds);
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                System.out.println("Parsed numeric date: " + localDate);
                return localDate;
            }

            String stringValue = cellData.getStringValue();
            if (stringValue != null && !stringValue.isEmpty()) {
                LocalDate localDate = LocalDate.parse(stringValue);
                System.out.println("Parsed string date: " + localDate);
                return localDate;
            }

            System.err.println("Invalid date value: " + cellData.getStringValue());
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error converting Excel date: " + cellData.getStringValue(), e);
        }
    }
}
