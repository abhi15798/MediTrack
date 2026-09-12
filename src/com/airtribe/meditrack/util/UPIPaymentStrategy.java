package com.airtribe.meditrack.util;

import com.airtribe.meditrack.interfaces.PaymentStrategy;

public class UPIPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via UPI.");
    }
}
