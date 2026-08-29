package com.airtribe.meditrack.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillSummary {
    private final String billId;
    private final String patientName;
    private final String doctorName;
    private final double totalAmount;
    private final LocalDate generatedOn;

    public BillSummary(String billId, String patientName, String doctorName, double totalAmount,
                       LocalDate generatedOn) {
        this.billId = billId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.totalAmount = totalAmount;
        this.generatedOn = generatedOn;
    }

    public String getBillId() {
        return billId;
    }

    public String getPatientName() {
        return patientName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDate getGeneratedOn() {
        return generatedOn;
    }

    @Override
    public String toString() {
        return "BillSummary{billId='" + billId + "', patientId='" + patientName +
                "', totalAmount=" + totalAmount + ", generatedOn=" + generatedOn + "}";
    }

    public void printBillSummary() {
        System.out.println();
        System.out.println("=====================================================================");
        System.out.println("                         PATIENT BILL SUMMARY                        ");
        System.out.println("=====================================================================");
        System.out.printf("Billed Date:    %-30s Bill Id:      %-10s%n", generatedOn, billId);
        System.out.printf("Patient Name:   %-30s Doctor Name:  %-10s%n", patientName, doctorName);
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-55s ₹%-20.2f%n", "Total Charges:", totalAmount);
        System.out.println("=====================================================================");
        System.out.println("\n\n");
    }

}
