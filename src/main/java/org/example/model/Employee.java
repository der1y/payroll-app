package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String name;
    private List<ShiftRecord> shifts = new ArrayList<>();
    private double totalTips;
    private double hoursWorked;
    private double wage;

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
        return calculateTotalWage();
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public double calculateTotalWage() {
        double totalWage = 0;
        for (ShiftRecord shift: shifts) {
            try {
                 totalWage += shift.getWage();
            } catch (NumberFormatException e) {
                System.err.println("Invalid wage on " + shift.getDate());
            }
        }
        return Math.round(totalWage * 100.00) / 100.0;
    }

    public double calculateHoursWorked() {
        double totalHours = 0;
        for (ShiftRecord shift : shifts) {
            try {
                totalHours += shift.getHoursWorked();
            } catch (NumberFormatException e) {
                System.err.println("Invalid hours format for shift on " + shift.getDate() + ": " + shift.getHoursWorked());
            }
        }

        return Math.round(totalHours * 100.0) / 100.0;
    }

    public double calculateTotalTips() {
        double total = 0;
        for (ShiftRecord shift : shifts) {
            try {
                total += shift.getTips();
            } catch (NumberFormatException e) {
                System.err.println("Invalid tips format for shift on " + shift.getDate() + ": " + shift.getTips());
            }
        }
        return Math.round(total * 100.0) / 100.0;
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
