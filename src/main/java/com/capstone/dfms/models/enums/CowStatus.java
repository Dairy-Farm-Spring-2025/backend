package com.capstone.dfms.models.enums;

public enum CowStatus {
    milkingCow,
    dryCow, //temporarily end cycle milking
    pregnantCow,
    openCow, //not pregnant, waiting for breeding
    calvingCow, //Cows that are in the process of giving birth.
    sickCow,
    breedingCow,
    quarantinedCow, //Cows isolated due to illness, disease suspicion, or after introduction to the farm.
    culling, //Cows identified for removal from the herd due to age, low productivity, or health

}
