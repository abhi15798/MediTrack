package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.interfaces.NotificationStrategy;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Person implements  Cloneable{
    private List<String> medicalHistory;
    private final LocalDate dob;
    // mutable field — the reason clone() needs to be deep

    public Patient(String id, String name, String contact,LocalDate dob, List<NotificationStrategy> notificationStrategies) {
        super(id, name, contact, notificationStrategies);
        this.medicalHistory = new ArrayList<>();
        this.dob = dob;
    }

    @Override
    public String getRole() {
        return "Patient";
    }
    public LocalDate getDob() {
        return dob;
    }

    public int getAge() {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public List<String> getMedicalHistory() {
        return new ArrayList<>(medicalHistory); // defensive copy — see note below
    }

    public void addMedicalRecord(String record) {
        medicalHistory.add(record);
    }

    @Override
    public Patient clone() {
        try {
            Patient cloned = (Patient) super.clone();          // shallow copy: id, name, contact, and
            // the medicalHistory reference itself
            cloned.medicalHistory = new ArrayList<>(this.medicalHistory); // deep copy: new list, same contents
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Patient implements Cloneable, this should never happen", e);
        }
    }

    @Override
    public String toString() {
        return "Patient{" +
                super.toString() +
                "medicalHistory=" + medicalHistory +
                ", dob= " + dob +
                ", age= " + getAge() +
                '}';
    }

}
