package com.capstone.dfms.models.enums;

public enum PenStatus {
    occupied,
    empty,
    reserved, //The pen is designated for a specific purpose or animal but not yet occupied.
    underMaintenance,
    decommissioned //The pen is no longer in use, either permanently or temporarily.
}
