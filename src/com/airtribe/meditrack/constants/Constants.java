package com.airtribe.meditrack.constants;

public final class Constants {
    private Constants() {} // prevent instantiation — this is a pure static holder

    public static final double TAX_RATE = 0.05; // 5% — used by BillingStrategy implementations

    public static final String DATA_DIR = "data/";
    public static final String DOCTORS_FILE = DATA_DIR + "doctors.csv";
    public static final String PATIENTS_FILE = DATA_DIR + "patients.csv";
    public static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.csv";
    public static final String CSV_DELIMITER = ",";
}
