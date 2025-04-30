package com.capstone.dfms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HealthRecordStatus {
    good,
    fair,
    poor,
    critical,
    recovering;

    @JsonCreator
    public static HealthRecordStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
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
