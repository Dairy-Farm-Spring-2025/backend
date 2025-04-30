package com.capstone.dfms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    female,
    male;

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        for (Gender status : Gender.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CowStatus: " + value);
    }

    @JsonValue
    public String toJson() {
        return this.name();
    }

}
