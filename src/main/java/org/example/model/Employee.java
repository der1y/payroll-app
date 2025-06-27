package org.example.model;

import java.util.List;

public class Employee {
    private String name;
    private List<ShiftRecord> shifts;
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

    public void setShifts(List<ShiftRecord> shifts) {
        this.shifts = shifts;
    }

    public double getTotalTips() {
        return totalTips;
    }

    public void setTotalTips(double totalTips) {
        this.totalTips = totalTips;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
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
