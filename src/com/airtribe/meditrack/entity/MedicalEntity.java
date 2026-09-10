package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

public abstract class MedicalEntity {
    private final String id;
    private final LocalDateTime createdDate;

    public MedicalEntity(String id, LocalDateTime createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    // abstract — Appointment and Bill each override this differently;
    // this is the polymorphism/overriding demonstration for this hierarchy
    public abstract String getDetails();

    @Override
    public String toString() {
        return "MedicalEntity{" +
                "id='" + id + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
