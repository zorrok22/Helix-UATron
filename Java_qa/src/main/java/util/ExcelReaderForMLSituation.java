package util;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;

public class ExcelReaderForMLSituation {

    private static final String excelFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "AIOPS"+File.separator+"ML-Situation.xlsx";
    private static final int headerstartno = 1;
    private static final int datastartno = 2;
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final String base_url = config.get("ADE_PLATFORM_URL");
    /*
    public static final String default_tenant = "https://tenant1.qa.example.com"; // if no entry exist related to tenant than use same model name as present in ocitennt1
    */


    // Alternative implementation using regex for more robustness
    public static String getHostnameRegex(String url) {
        // Ensure the URL has a scheme
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        try {
            URI uri = new URI(url);
            String hostname = uri.getHost();

            if (hostname == null) {
                return "";
            }

            // Remove port if present (though getHost() usually handles this)
            hostname = hostname.split(":")[0];

            // Get only the first part before any dots
            return hostname.split("\\.")[0];

        } catch (URISyntaxException e) {
            System.err.println("Invalid URL: " + url);
            return "";
        }
    }

    private static boolean matchesCondition(Cell cell, Object expectedValue) {
        if (cell == null || expectedValue == null) return false;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().equals(expectedValue.toString());
            case NUMERIC:
                if (expectedValue instanceof Number) {
                    return cell.getNumericCellValue() == ((Number) expectedValue).doubleValue();
                }
                return false;
            case BOOLEAN:
                if (expectedValue instanceof Boolean) {
                    return cell.getBooleanCellValue() == (Boolean) expectedValue;
                }
                return false;
            default:
                return false;
        }
    }

    private static String getColumnName(Row headerRow, int columnIndex) {
        Cell cell = headerRow.getCell(columnIndex);
        return cell != null ? cell.getStringCellValue() : "Column_" + columnIndex;
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return cell.getNumericCellValue();
            case BOOLEAN: return cell.getBooleanCellValue();
            default: return null;
        }
    }

    public static List<Map<String, Object>> findRowsByColumnNames(
            String column1Name, Object column1Value,
            String column2Name, Object column2Value,
            String mlSheetName) throws IOException {

        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Integer> columnMap = new HashMap<>();
        column2Value = getHostnameRegex((String) column2Value);

        try (FileInputStream file = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheet(mlSheetName);
            Row headerRow = sheet.getRow(headerstartno);

            // Create column name to index mapping
            for (Cell cell : headerRow) {
                columnMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            Integer col1Index = columnMap.get(column1Name);
            Integer col2Index = columnMap.get(column2Name);

            if (col1Index == null || col2Index == null) {
                throw new IllegalArgumentException("Column names not found");
            }

            for (int i = datastartno; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cell1 = row.getCell(col1Index);
                Cell cell2 = row.getCell(col2Index);

                if (cell1 != null && cell2 != null &&
                        matchesCondition(cell1, column1Value) &&
                        matchesCondition(cell2, column2Value)) {

                    Map<String, Object> rowData = new LinkedHashMap<>();
                    for (Cell cell : row) {
                        String columnName = getColumnName(headerRow, cell.getColumnIndex());
                        rowData.put(columnName, getCellValue(cell));
                    }
                    results.add(rowData);
                }
            }
        }

        return results;
    }
    public static List<Map<String, Object>> getTestDataFromExcel(int testcase_number, String mlSheetName) throws IOException {
        List<Map<String, Object>> row_data = findRowsByColumnNames("TC",testcase_number,"tenant_name",base_url, mlSheetName);
        /*
        if (row_data.size() < 1){
            row_data = findRowsByColumnNames("TC",testcase_number,"tenant_name",default_tenant, mlSheetName);
        }
        */
        if (row_data.size() < 1){
            row_data = findRowsByColumnNames("TC",testcase_number,"tenant_name","default", mlSheetName);
        }
        return row_data;
    }
}
