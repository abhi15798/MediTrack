package com.airtribe.meditrack.tests;

import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.SMSNotifier;

import java.time.LocalDate;
import java.util.List;

public class PatientServiceTest {

    public static void main(String[] args) {
        PatientServiceTest runner = new PatientServiceTest();

        System.out.println("Starting PatientService manual tests...");
        runner.testCreatePatient();
        runner.testUpdateContact();
        runner.testAddMedicalRecord();
        runner.testDeletePatient();
        runner.testGetAllPatients();
        runner.testSearchById();
        runner.testSearchByName();
        runner.testFindAllPatients();
        runner.testSearchByAge();
        runner.testCountCompletedAppointmentsPlaceholder();

        System.out.println("All PatientService manual tests passed.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public void testCreatePatient() {
        PatientService patientService = new PatientService();
        Patient patient = patientService.createPatient("Alice", "9876543210", LocalDate.of(1995, 5, 12), List.of(new SMSNotifier()));

        assertTrue(patient != null, "createPatient should return a patient object");
        assertTrue(patient.getId().startsWith("PAT-"), "createPatient should generate PAT id");
        assertTrue(patient.getName().equals("Alice"), "createPatient should store the given name");
        assertTrue(patient.getDob().equals(LocalDate.of(1995, 5, 12)), "createPatient should store the date of birth");
        System.out.println("PASS: testCreatePatient");
    }

    public void testUpdateContact() {
        PatientService patientService = new PatientService();
        Patient patient = patientService.createPatient("Bob", "9123456780", LocalDate.of(2000, 1, 20), List.of(new SMSNotifier()));

        patientService.updateContact(patient.getId(), "9988776655");
        Patient updatedPatient = patientService.searchById(patient.getId());

        assertTrue(updatedPatient.getContact().equals("9988776655"), "updateContact should update contact number");
        System.out.println("PASS: testUpdateContact");
    }

    public void testAddMedicalRecord() {
        PatientService patientService = new PatientService();
        Patient patient = patientService.createPatient("Charlie", "9090909090", LocalDate.of(1988, 7, 7), List.of(new SMSNotifier()));

        patientService.addMedicalRecord(patient.getId(), "Asthma diagnosed in 2024");
        Patient updatedPatient = patientService.searchById(patient.getId());

        assertTrue(updatedPatient.getMedicalHistory().contains("Asthma diagnosed in 2024"),
                "addMedicalRecord should append a new record to patient history");
        System.out.println("PASS: testAddMedicalRecord");
    }

    public void testDeletePatient() {
        PatientService patientService = new PatientService();
        Patient patient = patientService.createPatient("David", "8080808080", LocalDate.of(1990, 9, 9), List.of(new SMSNotifier()));

        patientService.deletePatient(patient.getId());
        boolean isDeleted = patientService.getAllPatients().stream().noneMatch(p -> p.getId().equals(patient.getId()));

        assertTrue(isDeleted, "deletePatient should remove the patient from the store");

        try {
            patientService.searchById(patient.getId());
            throw new AssertionError("deletePatient should cause searchById to fail for deleted patient");
        } catch (InvalidDataException expected) {
            // expected
        }

        System.out.println("PASS: testDeletePatient");
    }

    public void testGetAllPatients() {
        PatientService patientService = new PatientService();
        patientService.createPatient("Eva", "1111111111", LocalDate.of(1985, 2, 2), List.of(new SMSNotifier()));
        patientService.createPatient("Frank", "2222222222", LocalDate.of(1991, 3, 3), List.of(new SMSNotifier()));

        List<Patient> patients = patientService.getAllPatients();

        assertTrue(patients.size() == 2, "getAllPatients should return all created patients");
        System.out.println("PASS: testGetAllPatients");
    }

    public void testSearchById() {
        PatientService patientService = new PatientService();
        Patient patient = patientService.createPatient("Grace", "3333333333", LocalDate.of(1978, 12, 30), List.of(new SMSNotifier()));

        Patient foundPatient = patientService.searchById(patient.getId());

        assertTrue(foundPatient != null, "searchById should return a patient");
        assertTrue(foundPatient.getId().equals(patient.getId()), "searchById should return the matching patient by id");
        System.out.println("PASS: testSearchById");
    }

    public void testSearchByName() {
        PatientService patientService = new PatientService();
        patientService.createPatient("Heidi", "4444444444", LocalDate.of(2002, 4, 4), List.of(new SMSNotifier()));
        patientService.createPatient("heidi", "5555555555", LocalDate.of(2003, 5, 5), List.of(new SMSNotifier()));

        List<Patient> patients = patientService.search("Heidi");

        assertTrue(patients.size() == 2, "search(String) should return all patients with the same name ignoring case");
        System.out.println("PASS: testSearchByName");
    }

    public void testFindAllPatients() {
        PatientService patientService = new PatientService();
        patientService.createPatient("Ivan", "6666666666", LocalDate.of(1998, 6, 6), List.of(new SMSNotifier()));
        patientService.createPatient("Judy", "7777777777", LocalDate.of(1997, 7, 7), List.of(new SMSNotifier()));

        List<Patient> patients = patientService.findAllPatients();

        assertTrue(patients.size() == 2, "findAllPatients should return all patients");
        System.out.println("PASS: testFindAllPatients");
    }

    public void testSearchByAge() {
        PatientService patientService = new PatientService();
        LocalDate now = LocalDate.now();
        LocalDate dob1 = now.minusYears(30).minusDays(5);
        LocalDate dob2 = now.minusYears(30).plusDays(5);

        patientService.createPatient("Kane", "8888888888", dob1, List.of(new SMSNotifier()));
        patientService.createPatient("Lena", "9999999999", dob2, List.of(new SMSNotifier()));

        List<Patient> age30Patients = patientService.search(30);

        assertTrue(age30Patients.size() >= 1, "search(int) should return patients matching the given age");
        System.out.println("PASS: testSearchByAge");
    }

    public void testCountCompletedAppointmentsPlaceholder() {
        PatientService patientService = new PatientService();

        try {
            patientService.countCompletedAppointmentsPlaceholder();
            throw new AssertionError("countCompletedAppointmentsPlaceholder should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // expected
        }

        System.out.println("PASS: testCountCompletedAppointmentsPlaceholder");
    }
}
