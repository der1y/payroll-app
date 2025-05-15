import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

public static void main(String[] args) {
    try {
        CSVReader reader = new CSVReader(new FileReader("data/tips_report.csv"));
        List<String[]> rows = reader.readAll();
        for (String[] row : rows) {
            System.out.println(row[0] + row[1]);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}