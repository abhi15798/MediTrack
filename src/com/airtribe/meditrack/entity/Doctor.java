package com.airtribe.meditrack.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Doctor extends Person{
    private Specialization specialization;
    private double fee;
    private List<TimeSlot> availability;

    public Doctor(String id, String name, String contactNo,
                  Specialization specialization, double fee) {
        super(id, name, contactNo);
        this.specialization = specialization;
        this.fee = fee;
        this.availability  = new ArrayList<>();
    }


    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        if (fee < 0) {
            throw new IllegalArgumentException("Consultation fee cannot be negative");
        }
        this.fee = fee;
    }

    public List<TimeSlot> getTimeslots() {
        return new ArrayList<>(availability);
    }
    public Optional<TimeSlot> findTimeSlotById(String timeSlotId) {
        return availability.stream()
                .filter(slot -> slot.getId().equals(timeSlotId))
                .findFirst();
    }
    public void addTimeSlot(TimeSlot slot) {
        availability.add(slot);
    }
    public boolean hasAvailableSlot(TimeSlot slot) {
        return availability.contains(slot);
    }

    @Override
    public String getRole() {
        return "Doctor";
    }
}
