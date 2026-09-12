package com.airtribe.meditrack.interfaces;

public interface Payable {
    void pay(double amount);
    boolean isPaid();
}
