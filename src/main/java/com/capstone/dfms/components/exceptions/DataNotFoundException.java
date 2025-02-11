package com.capstone.dfms.components.exceptions;

import lombok.Data;

@Data
public class DataNotFoundException extends RuntimeException{
    private DataNotFound data;

    public DataNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        data = new DataNotFound(resourceName,fieldName,fieldValue);
    }

    public String getResourceName() {
        return data.getResourceName();
    }

    public String getFieldName() {
        return data.getFieldName();
    }

    public Object getFieldValue() {
        return data.getFieldValue();
    }
}
