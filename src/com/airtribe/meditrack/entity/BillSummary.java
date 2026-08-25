package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

public class BillSummary {
    private final String billId;
    private final String patientId;
    private final double totalAmount;
    private final LocalDateTime generatedOn;
    public BillSummary(String billId, String patientId, double totalAmount, LocalDateTime generatedOn) {
        this.billId = billId;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
        this.generatedOn = generatedOn;
    }

    public String getBillId() {
        return billId;
    }

    public String getPatientId() {
        return patientId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getGeneratedOn() {
        return generatedOn;
    }

    @Override
    public String toString() {
        return "BillSummary{billId='" + billId + "', patientId='" + patientId +
                "', totalAmount=" + totalAmount + ", generatedOn=" + generatedOn + "}";
    }
}
