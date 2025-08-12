package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String name;
    private List<ShiftRecord> shifts = new ArrayList<>();
    private double totalTips;
    private double hoursWorked;
    private double wage = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShiftRecord> getShifts() {
        return shifts;
    }

    public void setShifts(ShiftRecord shift) {
        this.shifts.add(shift);
        this.wage = -1;
    }

    public double getTotalTips() {
        return calculateTotalTips();
    }

    public void setTotalTips(double totalTips) {
        this.totalTips = totalTips;
    }

    public double getHoursWorked() {
        return calculateHoursWorked();
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public double getWage() {
        if (wage == -1) {
            wage = calculateTotalWage();
        }
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public double calculateTotalWage() {
        return Math.round(shifts.stream()
                .mapToDouble(ShiftRecord::getWage)
                .sum() * 100.0) / 100.0;
    }

    public double calculateHoursWorked() {
        return Math.round(shifts.stream()
                .mapToDouble(ShiftRecord::getHoursWorked)
                .sum() * 100.0) / 100.0;

//        double totalHours = 0;
//        for (ShiftRecord shift : shifts) {
//            try {
//                totalHours += shift.getHoursWorked();
//            } catch (NumberFormatException e) {
//                System.err.println("Invalid hours format for shift on " + shift.getDate() + ": " + shift.getHoursWorked());
//            }
//        }
//
//        return Math.round(totalHours * 100.0) / 100.0;
    }

    public double calculateTotalTips() {
        return Math.round(shifts.stream()
                .mapToDouble(ShiftRecord::getTips)
                .sum() * 100.00) / 100.00;

//        double total = 0;
//        for (ShiftRecord shift : shifts) {
//            try {
//                total += shift.getTips();
//            } catch (NumberFormatException e) {
//                System.err.println("Invalid tips format for shift on " + shift.getDate() + ": " + shift.getTips());
//            }
//        }
//        return Math.round(total * 100.0) / 100.0;
    }
    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", shifts=" + shifts +
                ", totalTips=" + totalTips +
                ", hoursWorked=" + hoursWorked +
                ", wage=" + wage +
                '}';
    }
}
