package com.capstone.dfms.components.utils;

import java.text.Normalizer;

public class StringUtils {
    public static String NameStandardlizing(String name){
        name = name.trim();
        String[] words = name.split("\\s+");
        StringBuilder newName = new StringBuilder();
        for(String word: words){
            if (!word.isEmpty()) {
                word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                newName.append(word).append(" ");
            }
        }
        return newName.toString().trim();
    }

    public static String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "")
                .toLowerCase();
    }
}
