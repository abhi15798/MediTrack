package com.airtribe.meditrack.entity;

public enum Specialization {
    CARDIOLOGY("Cardiology"),
    DERMATOLOGY("Dermatology"),
    ORTHOPEDICS("Orthopedics"),
    GENERAL_PHYSICIAN("General Physician"),
    PEDIATRICS("Pediatrics");

    private final String displayName;

    Specialization(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Specialization fromDisplayName(String value) {
        for (Specialization s : values()) {
            if (s.displayName.equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum found for: " + value);
    }
}
