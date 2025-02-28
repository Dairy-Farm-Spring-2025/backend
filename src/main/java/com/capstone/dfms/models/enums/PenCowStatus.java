package com.capstone.dfms.models.enums;

public enum PenCowStatus {
    planning, //waiting manager approve
    assigned, //manager approve
    cancel, //manager reject
    expired,// expired time of pen for Cow
    inPen,
    finished

}
