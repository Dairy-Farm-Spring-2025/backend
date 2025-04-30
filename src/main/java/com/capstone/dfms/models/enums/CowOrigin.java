package com.capstone.dfms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CowOrigin {
    european,
    indian,
    african,
    american,
    australian,
    exotic,
    indigenous,
    crossbreed;

    @JsonCreator
    public static CowOrigin fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // or throw a custom exception if you want to enforce it
        }

        for (CowOrigin status : CowOrigin.values()) {
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
