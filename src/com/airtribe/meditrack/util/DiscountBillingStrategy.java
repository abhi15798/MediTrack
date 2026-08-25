package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.interfaces.BillingStrategy;

public class DiscountBillingStrategy implements BillingStrategy {

    private final int discountPercent;

    public DiscountBillingStrategy(int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("discountPercent must be between 0 and 100");
        }
        this.discountPercent = discountPercent;
    }

    @Override
    public double calculate(double baseAmount) {
        double discountedAmount = baseAmount - (baseAmount * discountPercent / 100.0);
        return discountedAmount + (discountedAmount * Constants.TAX_RATE);
    }
}
