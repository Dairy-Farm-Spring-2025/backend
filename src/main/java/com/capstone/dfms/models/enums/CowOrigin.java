package com.capstone.dfms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CowOrigin {
    european,       // Breeds from Europe (e.g., Holstein, Jersey)
    indian,         // Breeds from India (e.g., Gir, Sahiwal)
    african,        // Breeds from Africa (e.g., Ankole, Boran)
    american,       // Breeds developed in the Americas (e.g., Brahman, Beefmaster)
    australian,     // Breeds developed in Australia (e.g., Droughtmaster)
    exotic,         // Specialized breeds (e.g., Yak)
    indigenous,     // Local breeds (specific to a region)
    crossbreed;      // Hybrids or mixed breeds

    @JsonCreator
    public static CowOrigin fromString(String value) {
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
