package org.example.model;

public class PayrollCalculator {
    public static double calculateTotalWage(Employee employee) {
        return Math.round(employee.getShifts().stream()
                .mapToDouble(ShiftRecord::getWage)
                .sum() * 100.0) / 100.0;
    }
}
