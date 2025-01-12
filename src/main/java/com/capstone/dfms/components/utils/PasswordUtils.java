package com.capstone.dfms.components.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordUtils {
        public static String generateRandomString(int length) {
            return RandomStringUtils.randomAlphanumeric(length);
        }
}
