package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.example.model.Employee;
import org.example.model.PayrollCalculator;
import org.example.model.ShiftRecord;
import org.example.model.WageCalculator;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Main {
    static void main(String[] args) {
        try {
            Map<String, Employee> employeeMap = getEmployeeMap();
            printEmployeeRecords(employeeMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Employee> getEmployeeMap() throws IOException, CsvValidationException {
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
            if (!date.isEmpty() && currentEmployee != null) {
                ShiftRecord record = new ShiftRecord();
                record.setName(currentEmployee);
                record.setRole(role);
                record.setDate(date);
                record.setTimeIn(timeIn);
                record.setTimeOut(timeOut);
                record.setTips(tips);
                record.setSales(sales);
                double wage = WageCalculator.calculateWage(record.getRole(), record.getHoursWorked());
                record.setWage(wage);

                records.add(record);
                employeeMap.get(currentEmployee).setShifts(record);
                // Create a new date to hold shifts by date if one doesn't exist
                shiftsByDate.computeIfAbsent(record.getDate(), _ -> new ArrayList<>()).add(record);
            }

        }

        // Apply tip-out rules to the collected shifts
        applyTipOuts(shiftsByDate);

        return employeeMap;
    }

    // Public helper so tests can run tip-out logic using synthetic shift data
    public static void applyTipOuts(Map<String, List<ShiftRecord>> shiftsByDate) {
        // Loop through all the shifts to determine tipOut and distribute cleanly
        for (Map.Entry<String, List<ShiftRecord>> entry : shiftsByDate.entrySet()) {
            List<ShiftRecord> dayShifts = entry.getValue();

            int hostCount = 0;
            int bartenderCount = 0;
            for (ShiftRecord shift : dayShifts) {
                if (shift.getRole().equalsIgnoreCase("Host")) hostCount++;
                if (shift.getRole().equalsIgnoreCase("Bartender")) bartenderCount++;
            }

            double totalHostTipOutGiven = 0.0;   // tips that should go to hosts
            double totalTipToBartenders = 0.0;   // tips that should go to bartenders (from servers)

            // First: compute tipOuts and apply them to payers immediately
            for (ShiftRecord shift : dayShifts) {
                String role = shift.getRole();
                if (role.equalsIgnoreCase("Server")) {
                    double bartenderTip = shift.getSales() * 0.02; // server -> bartender pool
                    double hostTip = 0.0; // server -> host pool depends on host count
                    if (hostCount == 1) {
                        hostTip = shift.getSales() * 0.01;
                    } else if (hostCount >= 2) {
                        hostTip = shift.getSales() * 0.02;
                    }
                    double totalTipOut = bartenderTip + hostTip;
                    shift.applyTipOut(totalTipOut); // subtract from server's tips and record tipOut

                    totalHostTipOutGiven += hostTip;
                    totalTipToBartenders += bartenderTip;
                } else if (role.equalsIgnoreCase("Bartender")) {
                    // Bartenders tip out a percentage to hosts
                    double hostTip = shift.getSales() * 0.01; // bartender -> hosts
                    shift.applyTipOut(hostTip);
                    totalHostTipOutGiven += hostTip;
                } else {
                    // hosts/managers/non-payers: no immediate tip-out change
                }
            }

            // Second: distribute host pool equally among hosts who worked that day
            if (hostCount > 0 && totalHostTipOutGiven > 0.0) {
                double perHostShare = Math.round((totalHostTipOutGiven / hostCount) * 100.0) / 100.0;
                for (ShiftRecord shift : dayShifts) {
                    if (shift.getRole().equalsIgnoreCase("Host")) {
                        shift.setTips(Math.round((shift.getTips() + perHostShare) * 100.0) / 100.0);
                    }
                }
            }

            // Third: distribute server->bartender pool equally among bartenders who worked that day
            if (bartenderCount > 0 && totalTipToBartenders > 0.0) {
                double perBartenderShare = Math.round((totalTipToBartenders / bartenderCount) * 100.0) / 100.0;
                for (ShiftRecord shift : dayShifts) {
                    if (shift.getRole().equalsIgnoreCase("Bartender")) {
                        shift.setTips(Math.round((shift.getTips() + perBartenderShare) * 100.0) / 100.0);
                    }
                }
            }

        }
    }


    private static void printEmployeeRecords(Map<String, Employee> employeeMap) {
        for (Employee emp : employeeMap.values()) {
            System.out.println("Employee: " + emp.getName());
            System.out.println("Total Tips: $" + emp.getTotalTips());
            System.out.println("Hours Worked: " + emp.getHoursWorked());
            System.out.println("Wage: $" + PayrollCalculator.calculateTotalWage(emp));
            System.out.println("Shifts:");
            for (ShiftRecord shift : emp.getShifts()) {
                System.out.println("  - " + shift);
            }
            System.out.println();
        }
    }


}