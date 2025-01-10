package com.capstone.dfms.components.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final Object firstObj =  new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        final Object secondObj = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

        return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
    }


}