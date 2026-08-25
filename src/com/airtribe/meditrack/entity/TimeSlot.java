package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimeSlot {
    private final String id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private SlotStatus status;

    public TimeSlot(String id,LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = SlotStatus.AVAILABLE; // every slot starts available
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void book() {
        if (status != SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Cannot book a slot that is not AVAILABLE");
        }
        status = SlotStatus.BOOKED;
    }

    public void release() {
        status = SlotStatus.AVAILABLE; // used on cancellation
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return id.equals(timeSlot.id); // identity by id, same pattern as Person
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TimeSlot{id='" + id + "', start=" + startTime + ", end=" + endTime + ", status=" + status + "}";
    }
}
