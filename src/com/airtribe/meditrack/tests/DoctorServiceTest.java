package com.airtribe.meditrack.tests;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Specialization;
import com.airtribe.meditrack.entity.TimeSlot;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.util.SMSNotifier;

import java.time.LocalDateTime;
import java.util.List;

public class DoctorServiceTest {

    public static void main(String[] args) {
        DoctorServiceTest runner = new DoctorServiceTest();

        System.out.println("Starting DoctorService manual tests...");
        runner.testCreateDoctor();
        runner.testUpdateContact();
        runner.testUpdateFee();
        runner.testAddAvailability();
        runner.testGetAllDoctors();
        runner.testSearchById();
        runner.testSearch();
        runner.testFindAllDoctors();
        runner.testFilterBySpecialization();
        runner.testAverageConsultationFee();
        runner.testDeleteDoctor();

        System.out.println("All DoctorService manual tests passed.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public void testCreateDoctor() {
        DoctorService doctorService = new DoctorService();
        Doctor doctor = doctorService.createDoctor("Dr. Abhi", "9876545677", Specialization.CARDIOLOGY, 500.00, List.of(new SMSNotifier()));

        assertTrue(doctor != null, "createDoctor should return a created Doctor object");
        assertTrue(doctor.getId().startsWith("DOC-"), "createDoctor should generate DOC id");
        assertTrue(doctor.getName().equals("Dr. Abhi"), "createDoctor should store the doctor name");
        assertTrue(doctor.getSpecialization() == Specialization.CARDIOLOGY, "createDoctor should store specialization");
        assertTrue(Math.abs(doctor.getFee() - 500.00) < 0.0001, "createDoctor should store consultation fee");

        System.out.println("PASS: testCreateDoctor");
    }

    public void testUpdateContact() {
        DoctorService doctorService = new DoctorService();
        Doctor doctor = doctorService.createDoctor("Dr. Iyer", "9123456780", Specialization.GENERAL_PHYSICIAN, 650.00, List.of(new SMSNotifier()));

        doctorService.updateContact(doctor.getId(), "9988776655");
        Doctor updatedDoctor = doctorService.searchById(doctor.getId());

        assertTrue(updatedDoctor.getContact().equals("9988776655"), "updateContact should update contact");
        System.out.println("PASS: testUpdateContact");
    }

    public void testUpdateFee() {
        DoctorService doctorService = new DoctorService();
        Doctor doctor = doctorService.createDoctor("Dr. Rao", "9876543210", Specialization.DERMATOLOGY, 600.00, List.of(new SMSNotifier()));

        doctorService.updateFee(doctor.getId(), 750.00);
        Doctor updatedDoctor = doctorService.searchById(doctor.getId());

        assertTrue(Math.abs(updatedDoctor.getFee() - 750.00) < 0.0001, "updateFee should update consultation fee");
        System.out.println("PASS: testUpdateFee");
    }

    public void testAddAvailability() {
        DoctorService doctorService = new DoctorService();
        Doctor doctor = doctorService.createDoctor("Dr. Singh", "9898989898", Specialization.PEDIATRICS, 550.00, List.of(new SMSNotifier()));
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 12, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 9, 12, 10, 30);

        TimeSlot slot = doctorService.addAvailability(doctor.getId(), startTime, endTime);

        assertTrue(slot != null, "addAvailability should return a created time slot");
        assertTrue(slot.getStartTime().equals(startTime), "addAvailability should store the start time");
        assertTrue(slot.getEndTime().equals(endTime), "addAvailability should store the end time");
        assertTrue(doctorService.searchById(doctor.getId()).getTimeslots().size() == 1, "addAvailability should add slot to the doctor");
        System.out.println("PASS: testAddAvailability");
    }

    public void testGetAllDoctors() {
        DoctorService doctorService = new DoctorService();
        doctorService.createDoctor("Dr. A", "1111111111", Specialization.CARDIOLOGY, 500.00, List.of(new SMSNotifier()));
        doctorService.createDoctor("Dr. B", "2222222222", Specialization.ORTHOPEDICS, 600.00, List.of(new SMSNotifier()));

        List<Doctor> doctors = doctorService.getAllDoctors();

        assertTrue(doctors.size() == 2, "getAllDoctors should return all doctors");
        System.out.println("PASS: testGetAllDoctors");
    }

    public void testSearchById() {
        DoctorService doctorService = new DoctorService();
        Doctor doctor = doctorService.createDoctor("Dr. C", "3333333333", Specialization.GENERAL_PHYSICIAN, 700.00, List.of(new SMSNotifier()));

        Doctor foundDoctor = doctorService.searchById(doctor.getId());

        assertTrue(foundDoctor != null, "searchById should return a doctor");
        assertTrue(foundDoctor.getId().equals(doctor.getId()), "searchById should return the matching doctor by id");
        System.out.println("PASS: testSearchById");
    }

    public void testSearch() {
        DoctorService doctorService = new DoctorService();
        doctorService.createDoctor("Dr. Sameer", "4444444444", Specialization.PEDIATRICS, 550.00, List.of(new SMSNotifier()));
        doctorService.createDoctor("Dr. Sameer", "5555555555", Specialization.DERMATOLOGY, 600.00, List.of(new SMSNotifier()));

        List<Doctor> doctors = doctorService.search("Dr. Sameer");

        assertTrue(doctors.size() == 2, "search should return all doctors matching the name ignoring case");
        System.out.println("PASS: testSearch");
    }

    public void testFindAllDoctors() {
        DoctorService doctorService = new DoctorService();
        doctorService.createDoctor("Dr. D", "6666666666", Specialization.CARDIOLOGY, 500.00, List.of(new SMSNotifier()));
        doctorService.createDoctor("Dr. E", "7777777777", Specialization.ORTHOPEDICS, 600.00, List.of(new SMSNotifier()));

        List<Doctor> doctors = doctorService.findAllDoctors();

        assertTrue(doctors.size() == 2, "findAllDoctors should return all doctors");
        System.out.println("PASS: testFindAllDoctors");
    }

    public void testFilterBySpecialization() {
        DoctorService doctorService = new DoctorService();
        doctorService.createDoctor("Dr. F", "8888888888", Specialization.CARDIOLOGY, 500.00, List.of(new SMSNotifier()));
        doctorService.createDoctor("Dr. G", "9999999999", Specialization.CARDIOLOGY, 650.00, List.of(new SMSNotifier()));
        doctorService.createDoctor("Dr. H", "1010101010", Specialization.DERMATOLOGY, 700.00, List.of(new SMSNotifier()));

        List<Doctor> cardioDoctors = doctorService.filterBySpecialization(Specialization.CARDIOLOGY);

        assertTrue(cardioDoctors.size() == 2, "filterBySpecialization should return only matching specialization doctors");
        assertTrue(cardioDoctors.stream().allMatch(doc -> doc.getSpecialization() == Specialization.CARDIOLOGY),
                "filterBySpecialization should contain only CARDIOLOGY doctors");
        System.out.println("PASS: testFilterBySpecialization");
    }

    public void testAverageConsultationFee() {
        DoctorService doctorService = new DoctorService();
        doctorService.createDoctor("Dr. J", "1212121212", Specialization.CARDIOLOGY, 500.00, List.of(new SMSNotifier()));
        doctorService.createDoctor("Dr. K", "1313131313", Specialization.GENERAL_PHYSICIAN, 700.00, List.of(new SMSNotifier()));
        doctorService.createDoctor("Dr. L", "1414141414", Specialization.DERMATOLOGY, 900.00, List.of(new SMSNotifier()));

        double averageFee = doctorService.averageConsultationFee();

        assertTrue(Math.abs(averageFee - 700.00) < 0.0001, "averageConsultationFee should return the average fee");
        System.out.println("PASS: testAverageConsultationFee");
    }

    public void testDeleteDoctor() {
        DoctorService doctorService = new DoctorService();
        Doctor doctor = doctorService.createDoctor("Dr. Delete", "1515151515", Specialization.ORTHOPEDICS, 800.00, List.of(new SMSNotifier()));

        doctorService.deleteDoctor(doctor.getId());

        boolean isDeleted = doctorService.getAllDoctors().stream().noneMatch(d -> d.getId().equals(doctor.getId()));
        assertTrue(isDeleted, "deleteDoctor should remove the doctor from service");

        try {
            doctorService.searchById(doctor.getId());
            throw new AssertionError("deleteDoctor should make searchById fail for deleted doctor");
        } catch (InvalidDataException expected) {
            // expected exception
        }

        System.out.println("PASS: testDeleteDoctor");
    }
}
