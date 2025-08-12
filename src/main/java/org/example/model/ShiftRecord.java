package org.example.model;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Duration;

public class ShiftRecord {
    private String name;
    private String role;
    private String date;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private double hoursWorked;
    private double wage;
    private double tips;
    private double sales;
    private double tipOut;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ShiftRecord(String name, String role, String date, LocalDateTime timeIn, LocalDateTime timeOut, double tips, double sales) {
        this.name = name;
        this.role = role;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.tips = tips;
        this.sales = sales;
    }

    public ShiftRecord() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalDateTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeInStr) {
        this.timeIn = LocalDateTime.parse(timeInStr, TIME_FORMATTER);
    }

    public LocalDateTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOutStr) {
        this.timeOut = LocalDateTime.parse(timeOutStr, TIME_FORMATTER);
        calculateHoursWorked(); // Calculate hours worked when timeOut is set
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public double getTips() {
        return tips - tipOut;
    }

    public void setTips(double tips) {
        this.tips = tips;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
        
    }

    public double getTipOut() {
        return tipOut;
    }

    public void setTipOut(double tipOut) {
        this.tipOut = tipOut;
    }

    private void calculateHoursWorked() {
        if (timeIn != null && timeOut != null) {
            Duration duration = Duration.between(timeIn, timeOut);
            this.hoursWorked = duration.toMinutes() / 60.0; // Convert minutes to hours
        }
    }

    @Override
    public String toString() {
        return "ShiftRecord{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", date='" + date + '\'' +
                ", timeIn='" + timeIn.format(TIME_FORMATTER) + '\'' +
                ", timeOut='" + timeOut.format(TIME_FORMATTER) + '\'' +
                ", tips='" + tips + '\'' +
                ", sales='" + sales + '\'' +
                '}';
    }
}
