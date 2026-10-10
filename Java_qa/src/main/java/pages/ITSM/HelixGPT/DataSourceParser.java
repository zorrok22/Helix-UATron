package pages.ITSM.HelixGPT;

import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

import io.restassured.path.json.JsonPath;

public class DataSourceParser {
    
    private static final Logger logger = Logger.getLogger(DataSourceParser.class.getName());
    
    /**
     * Extracts data source IDs from JSON response where name contains the specified keyword
     * @param jsonResponse The JSON response as string
     * @param nameKeyword The keyword to search for in the name field
     * @return List of data source IDs matching the criteria
     */
    public static List<String> getDataSourceIdsByName(String jsonResponse, String nameKeyword) {
        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataSources = jsonPath.getList("$");
            
            List<String> dataSourceIds = dataSources.stream()
                .filter(ds -> ds.get("name") != null && 
                             ds.get("name").toString().toUpperCase().contains(nameKeyword.toUpperCase()))
                .map(ds -> ds.get("dataSourceId").toString())
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
                
            logger.info("Found " + dataSourceIds.size() + " data source(s) with name containing '" + nameKeyword + "'");
            return dataSourceIds;
            
        } catch (Exception e) {
            logger.severe("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Extracts data source IDs from JSON response where name exactly matches the specified name
     * @param jsonResponse The JSON response as string
     * @param exactName The exact name to match
     * @return List of data source IDs matching the exact name
     */
    public static List<String> getDataSourceIdsByExactName(String jsonResponse, String exactName) {
        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataSources = jsonPath.getList("$");
            
            List<String> dataSourceIds = dataSources.stream()
                .filter(ds -> ds.get("name") != null && 
                             ds.get("name").toString().equals(exactName))
                .map(ds -> ds.get("dataSourceId").toString())
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
                
            logger.info("Found " + dataSourceIds.size() + " data source(s) with exact name '" + exactName + "'");
            return dataSourceIds;
            
        } catch (Exception e) {
            logger.severe("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Extracts data source IDs from JSON response where dataSource.code matches the specified code
     * @param jsonResponse The JSON response as string
     * @param code The data source code to match
     * @return List of data source IDs matching the code
     */
    public static List<String> getDataSourceIdsByCode(String jsonResponse, String code) {
        try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataSources = jsonPath.getList("$");
            
            List<String> dataSourceIds = dataSources.stream()
                .filter(ds -> {
                    Map<String, Object> dataSource = (Map<String, Object>) ds.get("dataSource");
                    return dataSource != null && 
                           dataSource.get("code") != null && 
                           dataSource.get("code").toString().equals(code);
                })
                .map(ds -> ds.get("dataSourceId").toString())
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
                
            logger.info("Found " + dataSourceIds.size() + " data source(s) with code '" + code + "'");
            return dataSourceIds;
            
        } catch (Exception e) {
            logger.severe("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Your JSON response
        String jsonResponse = "[{\"name\":\"Helix Business Workflows Connection\",\"dataSourceId\":\"AGGDI202EKB9FASF38R3SF38R3NOSA\",\"dataSource\":{\"name\":\"Helix Business Workflows Knowledge Management\",\"code\":\"BWF\",\"supportsIndexing\":true,\"supportsAgents\":false,\"id\":\"AGGDI202EKB9FASF38R3SF38R3NOSA\",\"displayId\":\"000000000000002\",\"createdBy\":\"Demo\",\"createDate\":\"2023-12-09T00:30:49.000Z\",\"modifiedBy\":\"Demo\",\"modifiedDate\":\"2024-06-19T18:26:43.000Z\",\"status\":null},\"lastIndexTime\":null,\"configuration\":null,\"ruleName\":null,\"dataSourceCode\":null,\"id\":\"AGGDI202EKB9FASFX5D9SFX5D95SS9\",\"displayId\":\"000000000000001\",\"createdBy\":\"Demo\",\"createDate\":\"2023-12-19T18:23:43.000Z\",\"modifiedBy\":\"Demo\",\"modifiedDate\":\"2024-02-12T21:57:11.000Z\",\"status\":null},{\"name\":\"SPT\",\"dataSourceId\":\"AGGDI202EKB9FASF38S1SF38S1NOWV\",\"dataSource\":{\"name\":\"Microsoft SharePoint Online\",\"code\":\"SPT\",\"supportsIndexing\":true,\"supportsAgents\":false,\"id\":\"AGGDI202EKB9FASF38S1SF38S1NOWV\",\"displayId\":\"000000000000004\",\"createdBy\":\"Demo\",\"createDate\":\"2023-12-09T00:31:23.000Z\",\"modifiedBy\":\"Liam\",\"modifiedDate\":\"2025-08-17T12:45:53.000Z\",\"status\":null},\"lastIndexTime\":null,\"configuration\":\"{}\",\"ruleName\":null,\"dataSourceCode\":null,\"id\":\"AGGCWNREEX9Y4ATCQXGCTCQXGC692Q\",\"displayId\":\"000000000000403\",\"createdBy\":\"Liam\",\"createDate\":\"2025-09-17T18:39:38.000Z\",\"modifiedBy\":\"Liam\",\"modifiedDate\":\"2025-09-17T18:39:38.000Z\",\"status\":null}]";
        
        // Test different extraction methods
        System.out.println("=== Testing Data Source ID Extraction ===");
        
        // Method 1: By name containing "SPT"
        List<String> sptIds = getDataSourceIdsByName(jsonResponse, "SPT");
        System.out.println("Data Source IDs with name containing 'SPT': " + sptIds);
        
        // Method 2: By exact name "SPT"
        List<String> exactSptIds = getDataSourceIdsByExactName(jsonResponse, "SPT");
        System.out.println("Data Source IDs with exact name 'SPT': " + exactSptIds);
        
        // Method 3: By data source code "SPT"
        List<String> codeSptIds = getDataSourceIdsByCode(jsonResponse, "SPT");
        System.out.println("Data Source IDs with code 'SPT': " + codeSptIds);
    }
}


