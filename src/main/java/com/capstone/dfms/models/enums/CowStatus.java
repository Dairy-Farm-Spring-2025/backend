package com.capstone.dfms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CowStatus {
    milkingCow,
    dryCow,
    sickCow,
    seriousSickcow,
    youngCow,
    culling;

    @JsonCreator
    public static CowStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // or throw a custom exception if you want to enforce it
        }

        for (CowStatus status : CowStatus.values()) {
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
