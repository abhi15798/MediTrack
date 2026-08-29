package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.Payment;
import com.airtribe.meditrack.util.*;

import java.time.LocalDate;
import java.util.Optional;

public class BillService {

    private final DataStore<Bill> billDataStore = new DataStore<>();

    public void generateBill(String patientName, String doctorName, double consultationCharges, double medicineCharges,
                             LocalDate generatedOn, int discountPercent, Payment paymentMethod) {

        String billId = IdGenerator.getInstance().generateId("BILL");
        Bill bill = new Bill(billId, patientName, doctorName, consultationCharges, medicineCharges, generatedOn);
        if (discountPercent > 0) {
            bill.setBillingStrategy(new DiscountBillingStrategy(discountPercent));
        } else {
            bill.setBillingStrategy(new StandardBillingStrategy());
        }
        double totalPayable = bill.generateBill();

        if (Payment.UPI.equals(paymentMethod)) {
            bill.setPaymentStrategy(new UPIPaymentStrategy());
        } else if (Payment.CARD.equals(paymentMethod)) {
            bill.setPaymentStrategy(new CardPaymentStrategy());
        } else {
            bill.setPaymentStrategy(new CashPaymentStrategy());
        }
        bill.pay(totalPayable);
        billDataStore.save(billId, bill);

    }

    public Optional<Bill> getBill(String billId) {
        return billDataStore.findById(billId);
    }

}
