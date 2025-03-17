package com.capstone.dfms.models.enums;

public enum IllnessDetailStatus {
    // when no treatment
    pending,

    // Status when treatment has started for the illness
    treated,

    // Status when the illness has been cured or resolved
    cured,

    // Status when the illness was too severe, and the cow did not survive
    deceased,

    cancel
}
