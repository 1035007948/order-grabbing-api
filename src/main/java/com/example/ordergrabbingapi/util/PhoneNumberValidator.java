package com.example.ordergrabbingapi.util;

import java.util.regex.Pattern;

/**
 * Utility class for phone number validation
 */
public class PhoneNumberValidator {

    // Chinese phone number pattern: starts with 1, followed by 10 digits
    private static final Pattern CHINA_PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    // International phone number pattern (simplified): starts with +, followed by 7-15 digits
    private static final Pattern INTERNATIONAL_PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    /**
     * Validate phone number format
     * @param phoneNumber the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        String cleaned = phoneNumber.replaceAll("[\\s-]", "");

        return CHINA_PHONE_PATTERN.matcher(cleaned).matches()
                || INTERNATIONAL_PHONE_PATTERN.matcher(cleaned).matches();
    }

    /**
     * Validate phone number format with specific region
     * @param phoneNumber the phone number to validate
     * @param region the region code (e.g., "CN", "US")
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String phoneNumber, String region) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        String cleaned = phoneNumber.replaceAll("[\\s-]", "");

        if ("CN".equals(region)) {
            return CHINA_PHONE_PATTERN.matcher(cleaned).matches();
        }

        return INTERNATIONAL_PHONE_PATTERN.matcher(cleaned).matches();
    }
}
