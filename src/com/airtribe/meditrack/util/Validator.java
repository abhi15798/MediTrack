package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

import java.util.regex.Matcher;

import static com.airtribe.meditrack.constants.AppConstants.*;

public class Validator {

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

    public static boolean isValidAmount(double amount) {
        return amount >= 0.0;
    }

    public static boolean isValidDiscount(int percentage) {
        return (percentage >= 0 && percentage < 100);
    }

    public static boolean isValidPaymentChoice(int paymentChoice) {
        return (paymentChoice >= 1 && paymentChoice <= 4);
    }

    /**
     * Basic validations for provided student name.
     * @param patientFirstName Student name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidFirstName(String patientFirstName) {
        if (patientFirstName == null || patientFirstName.trim().isEmpty()) {
            System.out.println("Please enter valid first name.");
            return false;
        }

        if (patientFirstName.length() < 2 || patientFirstName.length() > 50) {
            System.out.println("Please enter valid first name.");
            return false;
        }

        if (!NAME_PATTERN.matcher(patientFirstName).matches()) {
            System.out.println("Please enter valid first name.");
            return false;
        }

        return true;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Please enter valid name.");
            return false;
        }

        if (name.length() < 2 || name.length() > 50) {
            System.out.println("Please enter valid name.");
            return false;
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            System.out.println("Please enter valid name.");
            return false;
        }

        return true;
    }

    /**
     * Validates provided string is neither null nor blank.
     * @param str The string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNotBlank(String str) {
        if (str == null || str.isBlank()) {
            System.out.println("Please enter valid last name.");
            return false;
        }
        return true;
    }

    /**
     * Validates the format of an email ID.
     * @param email The email string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email != null && !email.isBlank()) {
            Matcher matcher = EMAIL_PATTERN.matcher(email);
            if (matcher.matches()) {
                return true;
            } else {
                System.out.println(EQUALS + " Invalid email format. Try again (e.g., name@domain.com) " + EQUALS);
                return false;
            }
        }
        return true;
    }

    public static boolean isValidContact(String contact) {
        if (contact == null || !CONTACT_PATTERN.matcher(contact).matches()) {
            System.out.println(EQUALS + " Please enter valid contact number. " + EQUALS);
            return false;
        }
        return true;
    }

    public static boolean isValidPatientId(String id) {
        // ^PAT checks if it starts with PAT
        // \\d+ checks if it is followed by one or more digits
        if (id == null || !id.matches("^PAT-\\d+")) {
            System.out.println(EQUALS + " Invalid patient id, please search with valid id. " + EQUALS);
            return false;
        }
        return true;
    }

    public static boolean isValidDoctorId(String id) {
        // ^DOC checks if it starts with DOC
        // \\d+ checks if it is followed by one or more digits
        if (id == null || !id.matches("^DOC-\\d+")) {
            System.out.println(EQUALS + " Invalid doctor id, please search with valid id. " + EQUALS);
            return false;
        }
        return true;
    }

    public static boolean isValidBillId(String id) {
        // ^BILL checks if it starts with BILL
        // \\d+ checks if it is followed by one or more digits
        if (id == null || !id.matches("^BILL-\\d+")) {
            System.out.println(EQUALS + " Invalid bill id, please search with valid id. " + EQUALS);
            return false;
        }
        return true;
    }

    public static boolean isValidAppointmentId(String id) {
        // ^APT checks if it starts with APT
        // \\d+ checks if it is followed by one or more digits
        if (id == null || !id.matches("^APT-\\d+")) {
            System.out.println(EQUALS + " Invalid appointment id, please search with valid id. " + EQUALS);
            return false;
        }
        return true;
    }
}
