package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Specialization;
import com.airtribe.meditrack.entity.TimeSlot;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.NotificationStrategy;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorService implements Searchable {
    private final DataStore<Doctor> doctorStore = new DataStore<>();

    public Doctor createDoctor(String name, String contact,
                               Specialization specialization, double consultationFee, List<NotificationStrategy> notificationStrategies) {
        String id = IdGenerator.getInstance().generateId("DOC");
        Doctor doctor = new Doctor(id, name, contact, specialization, consultationFee, notificationStrategies);
        doctorStore.save(id, doctor);
        return doctor;
    }

    public void updateContact(String doctorId, String newContact) {
        Doctor doctor = searchById(doctorId);
        doctor.setContact(newContact); // Validator runs inside Person.setContact()
    }

    public void updateFee(String doctorId, double newFee) {
        Doctor doctor = searchById(doctorId);
        doctor.setFee(newFee); // Doctor's own inline check runs here
    }

    public void deleteDoctor(String doctorId) {
        searchById(doctorId); // throws if not found, so delete fails loudly on a bad id
        doctorStore.delete(doctorId);
    }
    public TimeSlot addAvailability(String doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        Doctor doctor = searchById(doctorId); // throws if doctor doesn't exist
        String slotId = IdGenerator.getInstance().generateId("TS");
        TimeSlot slot = new TimeSlot(slotId, startTime, endTime);
        doctor.addTimeSlot(slot);
        return slot;
    }

    public List<Doctor> getAllDoctors() {
        return doctorStore.findAll();
    }

    @Override
    public Doctor searchById(String id) {
        return doctorStore.findById(id)
                .orElseThrow(()->new InvalidDataException("No Doctor Found with Id: "+ id));
    }

    @Override
    public List<Doctor> search(String name) {
        return doctorStore.findAll().stream()
                .filter(doc -> doc.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Doctor> findAllDoctors() {
        return doctorStore.findAll();
    }

    // Bonus D — filter by specialization
    public List<Doctor> filterBySpecialization(Specialization specialization) {
        return doctorStore.findAll().stream()
                .filter(doc -> doc.getSpecialization() == specialization)
                .collect(Collectors.toList());
    }

    // Bonus D — average consultation fee across all doctors
    public double averageConsultationFee() {
        return doctorStore.findAll().stream()
                .mapToDouble(Doctor::getFee)
                .average()
                .orElse(0.0);
    }
}
