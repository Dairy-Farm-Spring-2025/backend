package com.capstone.dfms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HealthRecordStatus {
    good,       // Health condition is good
    fair,       // Health condition is fair
    poor,       // Health condition is poor
    critical,   // Health condition is critical
    recovering;  // Cow is recovering

    @JsonCreator
    public static HealthRecordStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // or throw a custom exception if you want to enforce it
        }
        for (HealthRecordStatus status : HealthRecordStatus.values()) {
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
