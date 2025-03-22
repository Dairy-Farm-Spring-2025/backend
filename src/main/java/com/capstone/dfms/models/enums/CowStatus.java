package com.capstone.dfms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CowStatus {
    milkingCow,
    dryCow, //temporarily end cycle milking
    sickCow,
    seriousSickcow,
    youngCow,
    culling;//Cows identified for removal from the herd due to age, low productivity, or health

    @JsonCreator
    public static CowStatus fromString(String value) {
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
