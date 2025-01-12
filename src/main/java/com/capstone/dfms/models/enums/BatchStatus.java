package com.capstone.dfms.models.enums;

public enum BatchStatus {
    available,      // The batch is available for use
    inUse,          // The batch is currently in use
    depleted,       // The batch has been fully used
    expired,        // The batch has expired
    quarantined     // The batch is isolated due to issues (e.g., contamination)
}
