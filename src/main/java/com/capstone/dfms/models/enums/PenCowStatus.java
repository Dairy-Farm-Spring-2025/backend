package com.capstone.dfms.models.enums;

public enum PenCowStatus {
    assigned,       // Cow is currently assigned to the pen
    temporarilyMoved, // Cow is temporarily moved to another location
    underTreatment, // Cow is in a treatment pen
    quarantined,    // Cow is in a quarantine pen
    weaning,        // Cow (calf) is in the weaning pen
    resting,        // Cow is in a resting or non-milking pen
    breeding        // Cow is in a breeding pen
}
