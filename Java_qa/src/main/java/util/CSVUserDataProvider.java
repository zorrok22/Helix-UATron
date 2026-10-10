package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;

public class CSVUserDataProvider {

    @DataProvider(name = "csvUserData")
    public static Object[][] getCSVUserData() {
        return getDataFromCSV("resources" + File.separator + "ITSM" + File.separator + "ZDT" + File.separator + "UserData.csv");
    }
    
    public static Object[][] getDataFromCSV(String filePath) {
        List<Object[]> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // String[] headers = null;
            // int lineNumber = 0;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // skip header
                    continue;
                }
                String[] parts = line.split(",");
                String username = parts[0];
                String password = parts[1];
                data.add(new Object[]{username, password});
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + filePath, e);
        }
        return data.toArray(new Object[0][0]);
    }
}
