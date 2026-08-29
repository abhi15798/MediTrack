package com.airtribe.meditrack.util;

import com.airtribe.meditrack.interfaces.PaymentStrategy;

public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via cash.");
    }
}
