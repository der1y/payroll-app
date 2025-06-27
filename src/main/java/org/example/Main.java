package org.example;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import org.example.model.Employee;
import org.example.model.ShiftRecord;

import java.io.FileReader;
import java.sql.Array;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, List<ShiftRecord>> shiftsByDate = new HashMap<>();
            List<ShiftRecord> records = new ArrayList<>();
            List<Employee> employees = new ArrayList<>();
            CSVReader reader = new CSVReader(new FileReader("data/tips_report.csv"));

            String[] header = reader.readNext();
            String currentEmployee = null;

            String[] line;
            while ((line = reader.readNext()) != null) {
                String employee = line[0].trim();
                String role = line[1].trim();
                String date = line[2].trim();
                String timeIn = line[3].trim();
                String timeOut = line[4].trim();
                String tips = line[5].trim();
                String sales = line[6].trim();

                if (employee.toLowerCase().contains("total") || role.equalsIgnoreCase("N/A")) {
                    continue;
                }

                if (!employee.isEmpty()) {
                    currentEmployee = employee;
                }
                Employee worker = new Employee();

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



                    shiftsByDate.computeIfAbsent(record.getDate(), k -> new ArrayList<>()).add(record);
                }
            }

            for (ShiftRecord r : records) {
                System.out.println(r.getName() + " worked as " + r.getRole() + " on " + r.getDate());
            }

            for (String date : shiftsByDate.keySet()) {
                System.out.println("Date: " + date);

                for (ShiftRecord r : shiftsByDate.get(date)) {
                    System.out.println("  - " + r.getName() + " (" + r.getRole() + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        };
    }
}