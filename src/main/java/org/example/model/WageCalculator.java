package org.example.model;

import java.util.Map;

public class WageCalculator {
    private static final Map<String, Double> ROLE_RATES = Map.of(
            "Server", 2.13,
            "Bartender", 6.00,
            "Host", 10.00
    );

    public static double calculateWage(String role, double hoursWorked) {
        return ROLE_RATES.getOrDefault(role, 0.0) * hoursWorked;
    }
}
