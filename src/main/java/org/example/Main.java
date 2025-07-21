package org.example;

import com.opencsv.CSVReader;
import org.example.model.Employee;
import org.example.model.ShiftRecord;

import java.io.FileReader;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        try {
            Map<String, List<ShiftRecord>> shiftsByDate = new HashMap<>();
            List<ShiftRecord> records = new ArrayList<>();
            Map<String, Employee> employeeMap = new HashMap<>();
            CSVReader reader = new CSVReader(new FileReader("data/tips_report.csv"));

            String[] header = reader.readNext();
            String currentEmployee = null;

            String[] line;

            // Parse through the CSV file and breakdown the information accordingly
            while ((line = reader.readNext()) != null) {
                String employee = line[0].trim();
                String role = line[1].trim();
                String date = line[2].trim();
                String timeIn = line[3].trim();
                String timeOut = line[4].trim();
                if (line[5].trim().isEmpty()) {
                    line[5] = "0"; // Default to 0 if tips are not provided
                } else {
                    line[5] = line[5].replace("$", ""); // Remove dollar sign if present
                    line[5] = line[5].replace(",", "");
                }
                double tips = Double.parseDouble(line[5].trim());
                if (line[6].trim().isEmpty()) {
                    line[6] = "0"; // Default to 0 if sales are not provided
                } else {
                    line[6] = line[6].replace("$", ""); // Remove dollar sign if present
                    line[6] = line[6].replace(",", "");
                }
                double sales = Double.parseDouble(line[6].trim());

                // Ignores any line that doesn't deal with an employees info
                if (employee.toLowerCase().contains("total") || role.equalsIgnoreCase("N/A")) {
                    continue;
                }

                // Keeps you on the same employee after a new one has been established for the first time
                if (!employee.isEmpty()) {
                    currentEmployee = employee;
                    Employee emp = employeeMap.get(employee);
                    if (emp == null) {
                        emp = new Employee();
                        emp.setName(employee);
                        employeeMap.put(employee, emp);
                    }
                }

                // Fill out a record for the shift worked
                if(!date.isEmpty() && currentEmployee != null) {
                    ShiftRecord record = new ShiftRecord();
                    record.setName(currentEmployee);
                    record.setRole(role);
                    record.setDate(date);
                    record.setTimeIn(timeIn);
                    record.setTimeOut(timeOut);
                    record.setTips(tips);
                    record.setSales(sales);

                    records.add(record);
                    employeeMap.get(currentEmployee).setShifts(record);
                    // Create a new date to hold shifts by date if one doesn't exist
                    shiftsByDate.computeIfAbsent(record.getDate(), k -> new ArrayList<>()).add(record);
                }

            }

//            for (ShiftRecord r : records) {
//                System.out.println(r.getName() + " worked as " + r.getRole() + " on " + r.getDate());
//            }
//
//            for (String date : shiftsByDate.keySet()) {
//                System.out.println("Date: " + date);
//
//                for (ShiftRecord r : shiftsByDate.get(date)) {
//                    System.out.println("  - " + r.getName() + " (" + r.getRole() + ")");
//                }
//            }
            for (Employee emp : employeeMap.values()) {
                System.out.println("Employee: " + emp.getName());
                System.out.println("Total Tips: $" + emp.getTotalTips());
                System.out.println("Hours Worked: " + emp.getHoursWorked());
                System.out.println("Wage: $" + emp.getWage());
                System.out.println("Shifts:");
                for (ShiftRecord shift : emp.getShifts()) {
                    System.out.println("  - " + shift);
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        };
    }
}