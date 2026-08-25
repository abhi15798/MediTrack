package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^[0-9]{10}$");

    private Validator() {} // static-only utility class, same reasoning as Constants

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty");
        }
    }

    public static void validateContact(String contact) {
        if (contact == null || !CONTACT_PATTERN.matcher(contact).matches()) {
            throw new InvalidDataException("Contact must be a valid 10-digit number");
        }
    }

    public static void validatePositiveAmount(double amount) {
        if (amount < 0) {
            throw new InvalidDataException("Amount cannot be negative");
        }
    }
}
