package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Patient;

public class DiscountCalculator {

    public static int calculateDiscountPercent(Patient patient, int completedVisitCount){
        int discount = 0;
        if (completedVisitCount >= 2) {
            discount += 5;
        }
        if (patient.getAge() >= 60) {
            discount += 10;
        }
        return discount;
    }
}
