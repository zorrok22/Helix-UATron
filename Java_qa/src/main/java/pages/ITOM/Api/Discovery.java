package pages.ITOM.Api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import util.ReadPropertiesFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import static io.restassured.RestAssured.given;

/** *********************************************************************
 Purpose:		This class contains common functions for Discovery using API's
 @author			almahaja
  ********************************************************************** */

public class Discovery {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static String discUrl = config.get("ADE_SMART_GRAPH_URL");
    private static String discCreateToken = "/api/token";
    private static String discGetPartitions = "/api/v1.2/data/partitions";
    private static String discSearch = "/api/v1.2/data/search";
    private static String discWrite = "/api/v1.2/data/write";
    private static String discServiceModel = "/api/v1.13/models/<SERVICE_KEY>";
    // Get discovery token for authentication
    public String getDiscoveryToken(String username, String password) {
        try {
            String urlString = discUrl + discCreateToken;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String params = "grant_type=password&username=" + username + "&password=" + password;
            try (OutputStream os = connection.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.optString("access_token", null);
            } else {
                System.out.println("Failed to retrieve token. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
        return null;
    }


    // Fetch default partition ID for Discovery instance
    public String fetchDefaultPartitionId(String discToken) {
        System.out.println("Fetching default partition id for Discovery instance.");
        RestAssured.baseURI = discUrl;
        try {
            Response response = given()
                    .header("Authorization","Bearer " + discToken)
                    .when()
                    .get(discGetPartitions);

            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
            System.out.println(jsonNode.asText());
            return jsonNode.get("Default").asText();

        } catch (Exception e) {
            System.out.println("Caught " + e.getMessage() + " while fetching default partition id.");
            throw new RuntimeException(e);
        }
    }


    public List<String> getServicesFromAPIWithRelatedCis(String ciName, String accessKey, String secretKey) {
        System.out.println("Fetching Services related to CI " + ciName + " using API");
        String helixJwtToken = new Events_API().getJwtToken(accessKey, secretKey);
        List<String> services = fetchBusinesServicesForGivenCI(ciName, helixJwtToken);
        System.out.println("Services related to CI " + ciName + " : " + services);
        return services;
    }


    public List<String> fetchBusinesServicesForGivenCI(String ciName, String discToken) throws RuntimeException {

        RestAssured.baseURI = discUrl;
        try {
            Map<String, String> data = new HashMap<>();
            data.put("query", String.format("search BusinessService where nodecount(traverse AggregateSoftware:HostedSoftware:Host:Host where name has subword '%s') ",
                    ciName));

            System.out.println("Search Ci Payload is: " + data);

            Response response = given()
                    .header("Authorization", "Bearer " + discToken)
                    .header("Content-Type", "application/json")
                    .body(data)
                    .when()
                    .post(discSearch);

            System.out.println("Response Code for Search CI is " + response.getStatusCode());

            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
            System.out.println(jsonNode.asText());
            JsonNode results = jsonNode.get(0).get("results");

            List<String> serviceNames = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode item : results) {
                    int nodeCount = item.get(2).asInt();
                    if (nodeCount >= 1) {
                        String serviceName = item.get(1).asText();
                        serviceNames.add(serviceName);
                    }
                }
            }

            return serviceNames;

        } catch (Exception e) {
            System.out.println("Caught exception " + e.getMessage() + " in search_imported_ci.");
            throw new RuntimeException(e);
        }

    }



    public Map<String, Object> fetchCiIdForLatestImport(String ciName, String importType, String discToken) {

        RestAssured.baseURI = discUrl;
        try {
            String kind = "";
            Map<String, String> data = new HashMap<>();
            switch (importType.toUpperCase()) {
                case "ENTUITY":
                    kind = "NetworkDevice";
                    data.put("query", String.format("search %s where name = '%s' and external_ids has SUBWORD '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "DYNATRACE":
                    kind = "SoftwareCluster";
                    data.put("query", String.format("search %s where name = '%s' and external_ids has SUBWORD '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "STACKIFY":
                    kind = "SoftwareCluster";
                    data.put("query", String.format("search %s where name = '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "DATADOG":
                    kind = "Host";
                    data.put("query", String.format("search %s where name = '%s' and external_ids has SUBWORD '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "NEWRELIC":
                    kind = "SoftwareCluster";
                    data.put("query", String.format("search %s where name = '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;

                case "APPDYNAMICS":
                    kind = "SoftwareCluster";
                    data.put("query", String.format("search %s where name = '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;

                case "CAAPM":
                    kind = "ExternalElement";
                    data.put("query", String.format("search %s where name = '%s' and external_ids has SUBWORD '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "MSSCOM":
                    kind = "SoftwareInstance";
                    data.put("query", String.format("search %s where name = '%s' and external_ids has SUBWORD '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "VCENTER":
                    kind = "Cluster";
                    data.put("query", String.format("search %s where name = '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "K8S":
                    kind = "Cluster";
                    data.put("query", String.format("search %s where name has subword '%%%s%%' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
                case "NODE":
                    kind = "Host";
                    data.put("query", String.format("search %s where name has subword '%%%s%%' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;


                default:
                    System.out.println("Kind value for " + importType + " not available.");
                    data.put("query", String.format("search %s where name = '%s' and external_ids has SUBWORD '%s' order by starttime desc show #id, external_ids",
                            kind, ciName, importType));
                    break;
            }


            System.out.println("Search Ci Payload is: " + data);

            Response response = given()
                    .header("Authorization", "Bearer " + discToken)
                    .header("Content-Type", "application/json")
                    .body(data)
                    .when()
                    .post(discSearch);

            System.out.println("Response Code for Search CI is " + response.getStatusCode());

            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
            JsonNode results = jsonNode.get(0).get("results");

            if (results.size() > 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", results.get(0).get(0).asText());
                result.put("external_ids", results.get(0).get(1));
                return result;
            } else {
                System.out.println("No imported CI's found with name= " + ciName + " from " + importType + " source.");
                throw new RuntimeException("No CI found");
            }

        } catch (Exception e) {
            System.out.println("Caught exception " + e.getMessage() + " in search_imported_ci.");
            throw new RuntimeException(e);
        }
    }


    public int countDevicesOfType(Map<String, Object> deviceMap, String typeToMatch) {
        List<Map<String, String>> devices =
                (List<Map<String, String>>) deviceMap.get("devices");

        return (int) devices.stream()
                .filter(d -> typeToMatch.equalsIgnoreCase(d.get("type")))
                .count();
    }

    public Map<String, Object> fetchOtelDeviceTypes(String ciName, String discToken) {

        try {
            Map<String, String> data = new HashMap<>();
            data.put("query", String.format(
                    "search ExternalElement where name has subword '%s' order by starttime desc show name, type",
                    ciName));

            Response response = given()
                    .baseUri(discUrl)
                    .header("Authorization", "Bearer " + discToken)
                    .header("Content-Type", "application/json")
                    .body(data)
                    .when()
                    .post(discSearch);

            System.out.println("Response Code: " + response.getStatusCode());

            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
            JsonNode results = jsonNode.get(0).get("results");

            if (results == null || results.size() == 0) {
                System.out.println("No imported OTEL's found with name= " + ciName);
                throw new RuntimeException("No OTEL CI found");
            }

            // Final result object to return
            Map<String, Object> finalResult = new HashMap<>();

            // List of all device objects
            List<Map<String, String>> devices = new ArrayList<>();

            for (int i = 0; i < results.size(); i++) {
                String name = results.get(i).get(0).asText();
                String type = results.get(i).get(1).asText();
                System.out.println("Found OTEL CI with Name: " + name + " and Type: " + type);

                // Create each device entry
                Map<String, String> device = new HashMap<>();
                device.put("name", name);
                device.put("type", type);

                devices.add(device);
            }

            // Put list into final map
            finalResult.put("devices", devices);

            return finalResult;

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Fetch model key for service
    public String fetchModelKey(String serviceName, String discToken) {
        System.out.println("Fetch model key for " + serviceName);
        RestAssured.baseURI = discUrl;
        try {
            Map<String, String> data = new HashMap<>();
            data.put("query", String.format("search ModelDefinition where state = 'published' and (kind = 'BusinessService' and name = '%s') show key",
                    serviceName));

            System.out.println("Get model key using query:\n" + data);

            Response response = given()
                    .header("Authorization", "Bearer " + discToken)
                    .header("Content-Type", "application/json")
                    .body(data)
                    .when()
                    .post(discSearch);

            System.out.println("Model found with name " + serviceName + " with contents " + response.getBody().asString());

            if (response.getStatusCode() != 200) {
                System.out.println("API returned response code " + response.getStatusCode());
                throw new RuntimeException("API call failed");
            }

            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
            String nodeId = jsonNode.get(0).get("results").get(0).get(0).asText();

            System.out.println("Key for " + serviceName + " is " + nodeId);
            return nodeId;

        } catch (Exception e) {
            System.out.println("Caught exception " + e.getMessage() + " in fetch_model_id");
            throw new RuntimeException(e);
        }
    }

    // Create relation between service and CI
    public JsonNode createRelation(String serviceId, String partitionId, String ciId, String discToken) {

        RestAssured.baseURI = discUrl;
        try {
            String id = "124235345345cvbvcvbcv243545dfbdas";
            System.out.println("Temp id is " + id);
            System.out.println("Default Partition id is " + partitionId);

            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> relationData = new HashMap<>();
            relationData.put("cmd", "create_rel");
            relationData.put("partition", partitionId);
            relationData.put("kind", "Dependency");
            relationData.put("id", id);
            relationData.put("state", new HashMap<>());

            Map<String, Object> role1 = new HashMap<>();
            role1.put("role", "DependedUpon");
            role1.put("id", ciId);
            role1.put("partition", partitionId);
            relationData.put("role1", role1);

            Map<String, Object> role2 = new HashMap<>();
            role2.put("role", "Dependant");
            role2.put("id", serviceId);
            role2.put("partition", partitionId);
            relationData.put("role2", role2);

            data.add(relationData);

            System.out.println("Payload Data to create link is:\n " + data);

            Response response = given()
                    .header("Authorization", "Bearer " + discToken)
                    .header("Content-Type", "application/json")
                    .body(data)
                    .when()
                    .post(discWrite);

            JsonNode responseJson = objectMapper.readTree(response.getBody().asString());
            System.out.println("API Response Content for linking " + serviceId + " and " + ciId + ":\n" + responseJson);

            if (response.getStatusCode() == 200) {
                return responseJson;
            } else {
                throw new RuntimeException("Failed to create relation");
            }

        } catch (Exception e) {
            System.out.println("Caught exception " + e.getMessage() + " in create_relation.");
            throw new RuntimeException(e);
        }
    }

    // Delete service
    public String deleteService(String serviceKey, String discToken) {
        RestAssured.baseURI = discUrl;
        String apiEndpoint = discServiceModel.replace("<SERVICE_KEY>",serviceKey);
        System.out.println("Deleting Service with key: " + serviceKey + ", API End Point: " + apiEndpoint);

        try {
            Response response = given()
                    .header("accept", "*/*")
                    .header("Authorization", "Bearer " + discToken)
                    .when()
                    .delete(apiEndpoint);

            System.out.println("Delete response is:\n " + response.getStatusCode());

            if (!response.getBody().asString().isEmpty()) {
                System.out.println("Service Model not deleted.");
                throw new RuntimeException("Failed to delete service model");
            }
            return response.getBody().asString();

        } catch (Exception e) {
            System.out.println("Failed to delete Service Model with exception " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}