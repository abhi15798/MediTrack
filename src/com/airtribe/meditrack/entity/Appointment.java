package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

public class Appointment extends MedicalEntity implements Cloneable{
    private String doctorId;
    private String patientId;
    private String timeSlotId;
    private AppointmentStatus status;

    public Appointment(String id, LocalDateTime createdDate,
                       String doctorId, String patientId, String timeSlotId) {
        super(id, createdDate);
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.timeSlotId = timeSlotId;
        this.status = AppointmentStatus.CONFIRMED;;
    }

    public String getDoctorId() { return doctorId; }
    public String getPatientId() { return patientId; }
    public String getTimeSlotId() { return timeSlotId; }
    public AppointmentStatus getStatus() { return status; }

    public void complete() { status = AppointmentStatus.COMPLETED; }
    public void cancel() { status = AppointmentStatus.CANCELLED; }

    @Override
    public String getDetails() {
        return "Appointment{id='" + getId() + "', doctorId='" + doctorId +
                "', patientId='" + patientId + "', status=" + status + "}";
    }

    @Override
    public Appointment clone() {
        try {
            return (Appointment) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Appointment implements Cloneable, this should never happen", e);
        }
    }

    @Override
    public String toString() {
        return "Appointment{" +
                super.toString() +
                "doctorId='" + doctorId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", timeSlotId='" + timeSlotId + '\'' +
                ", status=" + status +
                '}';
    }
}
