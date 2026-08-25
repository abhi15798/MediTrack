package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.interfaces.BillingStrategy;
import com.airtribe.meditrack.interfaces.Payable;
import com.airtribe.meditrack.interfaces.Searchable;
import com.sun.media.sound.InvalidDataException;

import java.time.LocalDateTime;

public class Bill extends MedicalEntity implements Payable {
    private final String appointmentId;
    private final String patientId;
    private final double baseAmount;
    private final BillingStrategy strategy;
    private boolean paid;
    public Bill(String id, LocalDateTime createdDate,
                String appointmentId, String patientId,
                double baseAmount, BillingStrategy strategy) {
        super(id, createdDate);
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.baseAmount = baseAmount;
        this.strategy = strategy;
        this.paid = false;
    }

    public double generateBill() {
        return strategy.calculate(baseAmount);
    }
    public BillSummary toSummary() {
        double total = generateBill();
        return new BillSummary(getId(), patientId, total, getCreatedDate());
    }

    @Override
    public String getDetails() {
        return "Bill{id='" + getId() + "', appointmentId='" + appointmentId +
                "', total=" + generateBill() + ", paid=" + paid + "}";
    }

    @Override
    public void pay() {
        if (paid) {
            throw new IllegalStateException("Bill " + getId() + " is already paid");
        }
        paid = true;
    }

    @Override
    public boolean isPaid() {
        return paid;
    }
}
