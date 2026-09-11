package com.airtribe.meditrack.tests;

import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.interfaces.BillingStrategy;
import com.airtribe.meditrack.interfaces.PaymentStrategy;
import com.airtribe.meditrack.service.BillService;
import com.airtribe.meditrack.util.CashPaymentStrategy;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.DiscountBillingStrategy;
import com.airtribe.meditrack.util.StandardBillingStrategy;
import com.airtribe.meditrack.util.UPIPaymentStrategy;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BillServiceTest {

    public static void main(String[] args) {
        BillServiceTest runner = new BillServiceTest();

        System.out.println("Starting BillService manual tests...");
        runner.testGenerateBillWithStandardStrategy();
        runner.testGenerateBillWithDiscountStrategy();
        runner.testGetBill();

        System.out.println("All BillService manual tests passed.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static DataStore<Bill> getBillDataStore(BillService billService) {
        try {
            Field field = BillService.class.getDeclaredField("billDataStore");
            field.setAccessible(true);
            return (DataStore<Bill>) field.get(billService);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access BillService.billDataStore", e);
        }
    }

    private static Bill getOnlySavedBill(BillService billService) {
        DataStore<Bill> store = getBillDataStore(billService);
        List<Bill> bills = store.findAll();
        assertTrue(!bills.isEmpty(), "generateBill should persist a bill in the datastore");
        return bills.get(0);
    }

    public void testGenerateBillWithStandardStrategy() {
        BillService billService = new BillService();
        BillingStrategy standardStrategy = new StandardBillingStrategy();
        PaymentStrategy paymentStrategy = new CashPaymentStrategy();
        LocalDateTime generatedOn = LocalDateTime.of(2026, 9, 12, 9, 30);

        billService.generateBill("Alice", "Dr. Shah", 600.00, 200.00, generatedOn, standardStrategy, paymentStrategy);

        Bill savedBill = getOnlySavedBill(billService);
        double expectedTotal = 840.00;

        assertTrue(savedBill.isPaid(), "generateBill should mark the bill as paid after payment");
        assertTrue(Math.abs(savedBill.generateBill() - expectedTotal) < 0.0001,
                "generateBill with StandardBillingStrategy should add 5% tax to the total");
        System.out.println("PASS: testGenerateBillWithStandardStrategy");
    }

    public void testGenerateBillWithDiscountStrategy() {
        BillService billService = new BillService();
        BillingStrategy discountStrategy = new DiscountBillingStrategy(10.0);
        PaymentStrategy paymentStrategy = new UPIPaymentStrategy();
        LocalDateTime generatedOn = LocalDateTime.of(2026, 9, 12, 10, 00);

        billService.generateBill("Bob", "Dr. Mehta", 500.00, 150.00, generatedOn, discountStrategy, paymentStrategy);

        Bill savedBill = getOnlySavedBill(billService);
        double expectedTotal = 614.25;

        assertTrue(savedBill.isPaid(), "generateBill should pay the bill using the payment strategy");
        assertTrue(Math.abs(savedBill.generateBill() - expectedTotal) < 0.0001,
                "generateBill with DiscountBillingStrategy should apply discount and tax correctly");
        System.out.println("PASS: testGenerateBillWithDiscountStrategy");
    }

    public void testGetBill() {
        BillService billService = new BillService();
        BillingStrategy standardStrategy = new StandardBillingStrategy();
        PaymentStrategy paymentStrategy = new CashPaymentStrategy();
        LocalDateTime generatedOn = LocalDateTime.of(2026, 9, 12, 11, 15);

        billService.generateBill("Charlie", "Dr. Rao", 700.00, 300.00, generatedOn, standardStrategy, paymentStrategy);

        Bill savedBill = getOnlySavedBill(billService);
        String billId = savedBill.getId();
        Optional<Bill> foundBill = billService.getBill(billId);

        assertTrue(foundBill.isPresent(), "getBill should return a bill for a valid bill id");
        assertTrue(foundBill.get().getId().equals(billId), "getBill should return the matching bill by id");
        assertTrue(Math.abs(foundBill.get().generateBill() - 1050.00) < 0.0001,
                "getBill should return the stored bill with the correct amount");
        System.out.println("PASS: testGetBill");
    }
}
