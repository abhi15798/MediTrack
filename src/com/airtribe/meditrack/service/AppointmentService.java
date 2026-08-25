package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;

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
}