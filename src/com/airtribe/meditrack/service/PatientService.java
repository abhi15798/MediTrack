package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PatientService implements Searchable<Patient> {

    private final DataStore<Patient> patientStore = new DataStore<>();

    public Patient createPatient(String name, String contact, LocalDate dob) {
        String id = IdGenerator.getInstance().generateId("PAT");
        Patient patient = new Patient(id, name, contact, dob);
        patientStore.save(id, patient);
        return patient;
    }

    public void updateContact(String patientId, String newContact) {
        Patient patient = searchById(patientId);
        patient.setContact(newContact); // Validator runs inside Person.setContact()
    }

    public void addMedicalRecord(String patientId, String record) {
        Patient patient = searchById(patientId);
        patient.addMedicalRecord(record);
    }

    public void deletePatient(String patientId) {
        searchById(patientId); // throws if not found, fails loud before attempting delete
        patientStore.delete(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientStore.findAll();
    }

    @Override
    public Patient searchById(String id) {
        return patientStore.findById(id)
                .orElseThrow(() -> new InvalidDataException("No patient found with id " + id));
    }

    @Override
    public List<Patient> searchByName(String name) {
        return patientStore.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    // Patient-specific overload — not part of Searchable<T>, per the age-mismatch decision
    public List<Patient> searchByAge(int age) {
        return patientStore.findAll().stream()
                .filter(p -> p.getAge() == age)
                .collect(Collectors.toList());
    }

    // used by AppointmentService/BillFactory for the repeat-visit discount rule
    public int countCompletedAppointmentsPlaceholder() {
        throw new UnsupportedOperationException("This count lives on AppointmentService, not here");
    }
}