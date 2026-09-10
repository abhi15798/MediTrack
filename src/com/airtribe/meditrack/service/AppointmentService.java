package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        return appointment;
    }

    public void cancelAppointment(String appointmentId) {
        Appointment appointment = searchById(appointmentId)
                .orElseThrow(() -> new InvalidDataException("No appointment found with id " + appointmentId));

        Doctor doctor = doctorService.searchById(appointment.getDoctorId());
        TimeSlot slot = doctor.findTimeSlotById(appointment.getTimeSlotId())
                .orElseThrow(() -> new InvalidDataException("No such time slot for this doctor"));

        slot.release(); // throws IllegalStateException if not BOOKED — fails loud, no silent cancellation

        appointmentStore.delete(appointmentId);
    }

    public Optional<Appointment> searchById(String appointmentId) {
        return appointmentStore.findById(appointmentId);
    }

    public List<Appointment> findAllAppointments() {
        return appointmentStore.findAll();
    }
}