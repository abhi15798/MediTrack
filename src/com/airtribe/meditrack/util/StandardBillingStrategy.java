package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.interfaces.BillingStrategy;

public class StandardBillingStrategy implements BillingStrategy {
    @Override
    public double calculate(double baseAmount) {
        return baseAmount + (baseAmount * Constants.TAX_RATE);
    }
}
