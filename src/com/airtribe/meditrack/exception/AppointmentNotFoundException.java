package com.airtribe.meditrack.exception;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(String appointmentId) {
        super("No appointment found with id " + appointmentId);
    }

}
