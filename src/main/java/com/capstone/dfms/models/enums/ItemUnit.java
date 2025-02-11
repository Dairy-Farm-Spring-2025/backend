package com.capstone.dfms.models.enums;

public enum ItemUnit {

    // Units for weight
    kilogram,  // kg
    gram,      // g

    // Units for volume
    liter,     // L
    milliliter,// mL

    // Units for count
    piece,     // For items counted individually
    pack,      // For items packaged together

    // Units for area (if applicable)
    squareMeter, // m²

    // Miscellaneous units
    bottle,    // For liquids in bottles
    bag,       // For large quantities of items (e.g., feed)
    box        // For items stored in boxes
}
