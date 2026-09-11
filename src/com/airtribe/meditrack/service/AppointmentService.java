package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.*;

public class AppointmentService {

    private final DataStore<Appointment> appointmentStore = new DataStore<>();
    private final DoctorService doctorService;
    private final PatientService patientService;

    public AppointmentService(DoctorService doctorService, PatientService patientService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public Appointment bookAppointment(String doctorId, String patientId, String timeSlotId) {
        Doctor doctor = doctorService.searchById(doctorId);   // throws if doctor doesn't exist
        Patient patient = patientService.searchById(patientId); // throws if patient doesn't exist

        TimeSlot slot = doctor.findTimeSlotById(timeSlotId)
                .orElseThrow(() -> new InvalidDataException("No such time slot for this doctor"));

        slot.book(); // throws IllegalStateException if not AVAILABLE — fails loud, no silent double-booking

        String appointmentId = IdGenerator.getInstance().generateId("APT");
        Appointment appointment = new Appointment(appointmentId, LocalDateTime.now(),
                doctorId, patientId, timeSlotId);

        appointmentStore.save(appointmentId, appointment);
        notifyObservers( "Mr. " + patient.getName() + " booked appointment with you at " + slot.getStartTime(), doctor);
        notifyObservers("Your appointment booking with " + doctor.getName() + " is successful and scheduled at " + slot.getStartTime(), patient);
        return appointment;
    }

    public void cancelAppointment(String appointmentId) {
        Appointment appointment = searchById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        Doctor doctor = doctorService.searchById(appointment.getDoctorId());
        Patient patient = patientService.searchById(appointment.getPatientId());
        TimeSlot slot = doctor.findTimeSlotById(appointment.getTimeSlotId())
                .orElseThrow(() -> new InvalidDataException("No such time slot for this doctor"));

        slot.release(); // throws IllegalStateException if not BOOKED — fails loud, no silent cancellation

        appointmentStore.delete(appointmentId);
        notifyObservers( "Mr. " + patient.getName() + " cancelled the appointment with you at " + slot.getStartTime(), doctor);
        notifyObservers("Your appointment with " + doctor.getName() + " at " + slot.getStartTime() + " is cancelled.", patient);
    }

    public Optional<Appointment> searchById(String appointmentId) {
        return appointmentStore.findById(appointmentId);
    }

    public List<Appointment> findAllAppointments() {
        return appointmentStore.findAll();
    }

    public void notifyObservers(String message, Doctor doctor) {
        doctor.update(message);
    }

    public void notifyObservers(String message, Patient doctor) {
        doctor.update(message);
    }
}