package pages.ITSM.HelixGPT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import io.restassured.path.json.JsonPath;

public class Utils {

     /**
     * Gets all available data source names from the JSON response
     * @param jsonResponse The JSON response as string
     * @param logger Logger instance for logging
     * @return List of data source names
     */
    public static List<String> getAllDataSourceNames(String jsonResponse, Logger logger) {
        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataList = jsonPath.getList("data");
            List<String> names = new ArrayList<>();
            
            for (Map<String, Object> item : dataList) {
                if (item.get("8") != null) {
                    names.add(item.get("8").toString());
                }
            }
            
            logger.info("Found " + names.size() + " data source names");
            return names;
            
        } catch (Exception e) {
            logger.severe("Error parsing JSON response for data source names: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
    /**
     * Extracts data source ID from JSON response where dataSource.name exactly matches the specified name
     * @param jsonResponse The JSON response as string
     * @param dataSourceName The exact dataSource name to match
     * @param logger Logger instance for logging
     * @return Data source ID if found, null otherwise
     */
    public static String getDataSourceIdByDataSourceName(String jsonResponse, String dataSourceName, Logger logger) {
        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataList = jsonPath.getList("data");
            
            for (Map<String, Object> item : dataList) {
                if (item.get("8") != null && 
                    item.get("8").toString().equalsIgnoreCase(dataSourceName)) {
                    String dataSourceId = item.get("379").toString();
                    logger.info("Found DataSource ID: " + dataSourceId);
                    return dataSourceId;
                }
            }
            
            logger.warning("DataSource ID not found in JSON response");
            return null;
            
        } catch (Exception e) {
            logger.severe("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
}
