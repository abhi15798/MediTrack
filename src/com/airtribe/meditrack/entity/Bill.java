package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.interfaces.BillingStrategy;
import com.airtribe.meditrack.interfaces.Payable;
import com.airtribe.meditrack.interfaces.PaymentStrategy;

import java.time.LocalDateTime;

public class Bill extends MedicalEntity implements Payable {
//    private final String appointmentId;
    private final String patientName;
    private final String doctorName;
    private final double consultationCharges;
    private final double medicineCharges;
    private BillingStrategy billingStrategy;
    private PaymentStrategy paymentStrategy;
    private final LocalDateTime generatedOn;
    private boolean paid;

    public Bill(String id, String patientName, String doctorName,
                double consultationCharges, double medicineCharges, LocalDateTime generatedOn) {
        super(id, generatedOn);
//        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.consultationCharges = consultationCharges;
        this.medicineCharges = medicineCharges;
        this.generatedOn = generatedOn;
        this.paid = false;
    }

    public void setBillingStrategy(BillingStrategy billingStrategy) {
        this.billingStrategy = billingStrategy;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public double generateBill() {
        return billingStrategy.calculate(consultationCharges + medicineCharges);
    }

    public BillSummary toSummary() {
        double total = generateBill();
        return new BillSummary(getId(), patientName, doctorName, total, generatedOn);
    }

    @Override
    public String getDetails() {
        return "Bill{id='" + getId() + "', patientName='" + patientName +
                "', total=" + generateBill() + ", paid=" + generateBill() + "}";
    }

    @Override
    public void pay(double amount) {
        paymentStrategy.pay(amount);
        paid = true;
        toSummary().printBillSummary();
    }

    @Override
    public boolean isPaid() {
        return paid;
    }
}
