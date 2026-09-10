package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.Payment;
import com.airtribe.meditrack.interfaces.BillingStrategy;
import com.airtribe.meditrack.interfaces.PaymentStrategy;
import com.airtribe.meditrack.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class BillService {

    private final DataStore<Bill> billDataStore = new DataStore<>();

    public void generateBill(String patientName, String doctorName, double consultationCharges, double medicineCharges,
                             LocalDateTime generatedOn, BillingStrategy billingStrategy, PaymentStrategy paymentStrategy) {

        String billId = IdGenerator.getInstance().generateId("BILL");
        Bill bill = new Bill(billId, patientName, doctorName, consultationCharges, medicineCharges, generatedOn);
        bill.setBillingStrategy(billingStrategy);
        double totalPayable = bill.generateBill();
        bill.setPaymentStrategy(paymentStrategy);
        bill.pay(totalPayable);
        billDataStore.save(billId, bill);
    }

    public Optional<Bill> getBill(String billId) {
        return billDataStore.findById(billId);
    }

}
