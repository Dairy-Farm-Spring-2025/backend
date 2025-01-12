package com.capstone.dfms.models.enums;

public enum ItemStatus {
    available,      // The item is available and in stock
    outOfStock,     // The item is currently out of stock
    damaged,        // The item is damaged and unusable
    expired,        // The item has passed its usable date
    reserved        // The item is reserved for a specific purpose or order
}
