package com.capstone.dfms.models.enums;

public enum IllnessDetailStatus {
    // Status when illness details are being observed but no treatment has been started
    observed,

    // Status when treatment has started for the illness
    treated,

    // Status when the illness has been cured or resolved
    cured,

    // Status when the illness is still ongoing, and further treatment is needed
    ongoing,

    // Status when the illness was too severe, and the cow did not survive
    deceased,

    cancel
}
