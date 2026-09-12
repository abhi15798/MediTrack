package com.airtribe.meditrack.tests;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.entity.Specialization;
import com.airtribe.meditrack.entity.TimeSlot;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.SMSNotifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AppointmentServiceTest {

    public static void main(String[] args) {
        AppointmentServiceTest runner = new AppointmentServiceTest();

        System.out.println("Starting AppointmentService manual tests...");
        runner.testBookAppointment();
        runner.testCancelAppointment();
        runner.testSearchById();
        runner.testFindAllAppointments();

        System.out.println("All AppointmentService manual tests passed.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public void testBookAppointment() {
        DoctorService doctorService = new DoctorService();
        PatientService patientService = new PatientService();
        AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

        Doctor doctor = doctorService.createDoctor("Dr. Kapoor", "9876543211", Specialization.CARDIOLOGY, 700.00, List.of(new SMSNotifier()));
        Patient patient = patientService.createPatient("Amit", "9988776655", LocalDate.of(1988, 3, 15), List.of(new SMSNotifier()));
        TimeSlot slot = doctorService.addAvailability(doctor.getId(),
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 10, 30));

        Appointment appointment = appointmentService.bookAppointment(doctor.getId(), patient.getId(), slot.getId());

        assertTrue(appointment != null, "bookAppointment should return a created appointment");
        assertTrue(appointment.getDoctorId().equals(doctor.getId()), "bookAppointment should store doctor id");
        assertTrue(appointment.getPatientId().equals(patient.getId()), "bookAppointment should store patient id");
        assertTrue(appointment.getTimeSlotId().equals(slot.getId()), "bookAppointment should store time slot id");
        assertTrue(doctorService.searchById(doctor.getId()).findTimeSlotById(slot.getId()).get().getStatus().name().equals("BOOKED"),
                "bookAppointment should mark the doctor time slot as BOOKED");
        System.out.println("PASS: testBookAppointment");
    }

    public void testCancelAppointment() {
        DoctorService doctorService = new DoctorService();
        PatientService patientService = new PatientService();
        AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

        Doctor doctor = doctorService.createDoctor("Dr. Nair", "9812345678", Specialization.GENERAL_PHYSICIAN, 600.00, List.of(new SMSNotifier()));
        Patient patient = patientService.createPatient("Priya", "9090909090", LocalDate.of(1996, 11, 20), List.of(new SMSNotifier()));
        TimeSlot slot = doctorService.addAvailability(doctor.getId(),
                LocalDateTime.of(2026, 9, 16, 11, 0),
                LocalDateTime.of(2026, 9, 16, 11, 30));

        Appointment appointment = appointmentService.bookAppointment(doctor.getId(), patient.getId(), slot.getId());
        appointmentService.cancelAppointment(appointment.getId());

        assertTrue(appointmentService.searchById(appointment.getId()).isEmpty(),
                "cancelAppointment should remove the appointment from the store");
        assertTrue(doctorService.searchById(doctor.getId()).findTimeSlotById(slot.getId()).get().getStatus().name().equals("AVAILABLE"),
                "cancelAppointment should release the time slot back to AVAILABLE");
        System.out.println("PASS: testCancelAppointment");
    }

    public void testSearchById() {
        DoctorService doctorService = new DoctorService();
        PatientService patientService = new PatientService();
        AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

        Doctor doctor = doctorService.createDoctor("Dr. Sethi", "9876501234", Specialization.DERMATOLOGY, 550.00, List.of(new SMSNotifier()));
        Patient patient = patientService.createPatient("Ravi", "9123456789", LocalDate.of(1985, 5, 5), List.of(new SMSNotifier()));
        TimeSlot slot = doctorService.addAvailability(doctor.getId(),
                LocalDateTime.of(2026, 9, 17, 12, 0),
                LocalDateTime.of(2026, 9, 17, 12, 30));

        Appointment appointment = appointmentService.bookAppointment(doctor.getId(), patient.getId(), slot.getId());
        Optional<Appointment> found = appointmentService.searchById(appointment.getId());

        assertTrue(found.isPresent(), "searchById should return an appointment for a valid id");
        assertTrue(found.get().getId().equals(appointment.getId()), "searchById should return the matching appointment by id");
        System.out.println("PASS: testSearchById");
    }

    public void testFindAllAppointments() {
        DoctorService doctorService = new DoctorService();
        PatientService patientService = new PatientService();
        AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

        Doctor doctor = doctorService.createDoctor("Dr. Paul", "9911223344", Specialization.ORTHOPEDICS, 800.00, List.of(new SMSNotifier()));
        Patient patientOne = patientService.createPatient("Nina", "7000000001", LocalDate.of(1992, 2, 10), List.of(new SMSNotifier()));
        Patient patientTwo = patientService.createPatient("Omar", "7000000002", LocalDate.of(1994, 4, 12), List.of(new SMSNotifier()));

        TimeSlot slotOne = doctorService.addAvailability(doctor.getId(),
                LocalDateTime.of(2026, 9, 18, 9, 0),
                LocalDateTime.of(2026, 9, 18, 9, 30));
        TimeSlot slotTwo = doctorService.addAvailability(doctor.getId(),
                LocalDateTime.of(2026, 9, 18, 9, 30),
                LocalDateTime.of(2026, 9, 18, 10, 0));

        appointmentService.bookAppointment(doctor.getId(), patientOne.getId(), slotOne.getId());
        appointmentService.bookAppointment(doctor.getId(), patientTwo.getId(), slotTwo.getId());

        List<Appointment> appointments = appointmentService.findAllAppointments();

        assertTrue(appointments.size() == 2, "findAllAppointments should return all booked appointments");
        System.out.println("PASS: testFindAllAppointments");
    }
}
