package pages.ITSM.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import util.ReadJson;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Author: Daniel
 * This Class will be used to maintain all ITSM related API calls
 */


public class Itsm_Helper {

    RemedyApiClient remedyApiClient;
    API_CommonOperations apiCommonOperations;
    ReadJson readJson = new ReadJson();
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    String INCIDENT_CREATE_ENDPOINT = "/api/arsys/v1/entry/HPD:IncidentInterface_Create";

    String USER_CREATE_ENDPOINT = "api/arsys/v1/entry/CTM:People";
    String INCIDENT_GET_ENDPOINT = "api/arsys/v1/entry/HPD:Help Desk"; //IncidentInterface

    String BMC_BASE_Element_URL = "api/arsys/v1/entry/BMC.CORE:BMC_BaseElement";
    String BMC_BUSINESS_SERVICE_URL = "api/arsys/v1/entry/BMC.CORE:BMC_BusinessService";
    String CHANGE_REQUEST_GET_ENDPOINT = "api/arsys/v1/entry/CHG:Infrastucture Change";
    String DATA_WIZARD_ENDPOINT = "api/arsys/v1/entry/DMT:SYS:DataWizardQueue";
    String USER_ADD_PERMISSION = "api/arsys/v1/entry/CTM:People Permission Groups";
    String GROUP_UPDATE_ENDPOINT = "api/arsys/v1/entry/CTM%3ASupport%20Group%20Association";
    String ADDM_JOB_URL="api/cmdb/v1.0/re/jobs?offset=0&limit=500&job_name=BMC%20ADDM%20-%20Identification%2C%20Merge%20and%20Purge";
    String NETOPS_ALARM_ENDPOINT = "/api/rx/application/record/recordinstance";
    String COMMAND="api/cmdb/v1.0/command";
    String finalJson = "",remedyAPIKey = "";
    String jobEndpoint = "/api/cmdb/v1.0/re/jobs";

    static SolqaLogger log = new SolqaLogger(Itsm_Helper.class.getName());

    public Itsm_Helper(String itsmRestApiUrl, String remedyAdminUser, String remedyAdminPassword){
        remedyApiClient = new RemedyApiClient(itsmRestApiUrl);
        apiCommonOperations = new API_CommonOperations();
        try {
            remedyAPIKey = remedyApiClient.getRemedyApiKey(remedyAdminUser, remedyAdminPassword);
        }catch (Exception e){
            log.error("Failed to generate Remedy API key: "+ e.getMessage());
        }
    }

    public String createUserInCTMForm(String json) throws Exception{
        String peopleID = "";
        String personId="";
        String BASE_URL = config.get("CMDBURL");
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .contentType(ContentType.JSON)
                .body(json)
                .post(USER_CREATE_ENDPOINT);

        Assert.assertEquals(response1.getStatusCode(), 201,
                "Failed to create User. Response: " + response1.body().asString());

        peopleID = response1.getHeader("Location");
        if (peopleID == null || peopleID.isEmpty()) {
            throw new Exception("Failed to retrieve Person ID from response headers.");
        }else{
            String[] parts = peopleID.split("/");
            personId = parts[parts.length - 1];
        }
        return personId;
    }

    public String updateLoginIDInDataWizard(String oldLoginID, String newLoginID) throws Exception{
        String peopleID = "";
        String personId="";
        String BASE_URL = config.get("CMDBURL");

        Map<String, String> update = Map.of(
                "LoginID_OriVal", oldLoginID,
                "LoginID_NewVal", newLoginID
        );

        String json = buildPayload(update);

        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .contentType(ContentType.JSON)
                .body(json)
                .post(DATA_WIZARD_ENDPOINT);

        Assert.assertEquals(response1.getStatusCode(), 201,
                "Failed to create entry in data wizard. Response: " + response1.body().asString());

        peopleID = response1.getHeader("Location");
        if (peopleID == null || peopleID.isEmpty()) {
            throw new Exception("Failed to retrieve Person ID from response headers.");
        }else{
            String[] parts = peopleID.split("/");
            personId = parts[parts.length - 1];
        }
        return personId;
    }

    public void updateUserInCTMForm(String peopleID,String json) {
        String BASE_URL = config.get("CMDBURL");

        if (peopleID == null || peopleID=="") {
            log.error("Person ID is null or empty. Cannot update user.");
            }else {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "AR-JWT " + remedyAPIKey)
                    .contentType(ContentType.JSON)
                    .body(json)
                    .put(USER_CREATE_ENDPOINT + "/" + peopleID);

            Assert.assertEquals(response1.getStatusCode(), 204,
                    "Failed to update User. Response: " + response1.body().asString());
        }
    }

    /**
     * Reads the "Internet E-mail" value from a JSON response string.
     *
     * @param jsonResponse the JSON string from the REST API response
     * @return the Internet E-mail value if present, otherwise null
     */
    public String getEmailFromResponse(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Parse the JSON string
            JsonNode rootNode = mapper.readTree(jsonResponse);

            // Navigate to values → Internet E-mail
            JsonNode valuesNode = rootNode.path("values");
            JsonNode emailNode = valuesNode.path("Internet E-mail");

            if (!emailNode.isMissingNode() && !emailNode.isNull()) {
                return emailNode.asText();
            } else {
                return null;  // key not found or value is null
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getEmailOfUserInCTMForm(String peopleID) {
        String email = "";
        if (peopleID == null || peopleID=="") {
            log.error("Person ID is null or empty. Cannot update user.");
        }else {
            String responseBody = getUserInCTMForm(peopleID);
            email = getEmailFromResponse(responseBody);
        }
        return email;
    }

    public String getUserInCTMForm(String peopleID) {
        String BASE_URL = config.get("CMDBURL");

        if (peopleID == null || peopleID=="") {
            log.error("Person ID is null or empty. Cannot update user.");
        }else {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "AR-JWT " + remedyAPIKey)
                    .contentType(ContentType.JSON)
                    .get(USER_CREATE_ENDPOINT + "/" + peopleID);

            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to get User details. Response: " + response1.body().asString());
            return response1.getBody().asString();
        }
        return null;
    }

    public String getStatus(String jsonPayload) {
        log.info("Retrieve ITSM Incident status from JSON Payload: " + jsonPayload);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonPayload);

            // Navigate: entries → [0] → values → Status
            return root.path("entries")
                    .get(0)
                    .path("values")
                    .path("Status")
                    .asText(null);     // return null if missing

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getIncidentInHelpDeskForm(String incidentID) {
        log.info("Retrieve ITSM Incident details for Incident ID: " + incidentID);
        String BASE_URL = config.get("CMDBURL");
        if (incidentID == null || incidentID.trim().isEmpty()) {
            log.error("Person ID is null or empty. Cannot update user.");
        }else {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "AR-JWT " + remedyAPIKey)
                    .contentType(ContentType.JSON)
                    .get(INCIDENT_GET_ENDPOINT + "/" + "?q='Incident Number'=\"" + incidentID + "\"");

            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to get Incident details. Response: " + response1.body().asString());
            return response1.getBody().asString();
        }
        return null;
    }

    public String getListofCIsSyncedInCMDB(String searchString) {
        log.info("Retrieve list of CIs from CMDB matching search string: " + searchString);
        String BASE_URL = config.get("CMDBURL");
        String query = "?q='Name' LIKE \"%" + searchString + "%\" AND 'DatasetId' = \"BMC.ASSET\"";

        if (searchString == null || searchString.trim().isEmpty()) {
            log.error("Search string cant be null or empty. Cannot proceed.");
        }else {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "AR-JWT " + remedyAPIKey)
                    .contentType(ContentType.JSON)
                    .get(BMC_BASE_Element_URL + "/" + query);

            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to get Incident details. Response: " + response1.body().asString());
            return response1.getBody().asString();
        }
        return null;
    }

    public String getListofServicesSyncedInCMDB(String searchString) {
        log.info("Retrieve list of Services from CMDB matching search string: " + searchString);
        String BASE_URL = config.get("CMDBURL");
        String query = "?q='Name' LIKE \"%" + searchString + "%\" AND 'DatasetId' = \"BMC.ASSET\"";

        if (searchString == null || searchString.trim().isEmpty()) {
            log.error("Search string cant be null or empty. Cannot proceed.");
        }else {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "AR-JWT " + remedyAPIKey)
                    .contentType(ContentType.JSON)
                    .get(BMC_BUSINESS_SERVICE_URL + "/" + query);

            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to get list of service details. Response: " + response1.body().asString());
            return response1.getBody().asString();
        }
        return null;
    }

    public String getCRDetailsInInfraChangeForm(String crID) {
        log.info("Retrieve ITSM Change Request details for CR ID: " + crID);
        String BASE_URL = config.get("CMDBURL");

        if (crID == null || crID=="") {
            log.error("Person ID is null or empty. Cannot update user.");
        }else {
            Response response1 = RestAssured.given()
                    .baseUri(BASE_URL)
                    .header("Authorization", "AR-JWT " + remedyAPIKey)
                    .contentType(ContentType.JSON)
                    .get(CHANGE_REQUEST_GET_ENDPOINT + "/" + crID);

            Assert.assertEquals(response1.getStatusCode(), 200,
                    "Failed to get Change request details. Response: " + response1.body().asString());
            return response1.getBody().asString();
        }
        return null;
    }

    public void deleteUserInCTMForm(String peopleID) throws Exception{

        Map<String, String> deleteUser = Map.of(
                "Profile Status", "Delete"
        );

        String deletePayload = buildPayload(deleteUser);

        updateUserInCTMForm(peopleID, deletePayload);

        String BASE_URL = config.get("CMDBURL");
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .contentType(ContentType.JSON)
                .delete(USER_CREATE_ENDPOINT+"/"+peopleID);
        try {
            Assert.assertEquals(response1.getStatusCode(), 204,
                    "Failed to delete User. Response: " + response1.body().asString());
        }catch (AssertionError e){
            log.error("Failed to delete User. Response: " + response1.body().asString());
        }
    }

    public void deletePermissionFromForm(String ID) {

        String BASE_URL = config.get("CMDBURL");
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .contentType(ContentType.JSON)
                .delete(USER_ADD_PERMISSION+"/"+ID);
        try {
            Assert.assertEquals(response1.getStatusCode(), 204,
                    "Failed to delete Permission. Response: " + response1.body().asString());
        }catch (AssertionError e){
            log.error("Failed to delete Permission. Response: " + response1.body().asString());
        }
    }

    public void updateLicenseForUserInCTMForm(String peopleID, String license) throws Exception{

        Map<String, String> updateLicenseUser = Map.of(
                "License Type", license
        );

        String deleteLicense = buildPayload(updateLicenseUser);

        updateUserInCTMForm(peopleID, deleteLicense);
    }

    public void updateLoginNameForUserInCTMForm(String peopleID, String newLoginName) throws Exception{

        Map<String, String> updateLicenseUser = Map.of(
                "First Name", newLoginName
        );

        String update = buildPayload(updateLicenseUser);
        updateUserInCTMForm(peopleID, update);
    }

    public void updateLoginIDForUserInCTMForm(String peopleID, String newLoginID) throws Exception{

        Map<String, String> updateLicenseUser = Map.of(
                "Remedy Login ID", newLoginID
        );

        String update = buildPayload(updateLicenseUser);
        updateUserInCTMForm(peopleID, update);
    }

    public void updateEmailForUserInCTMForm(String peopleID, String newEmail) throws Exception{
        Map<String, String> updateLicenseUser = Map.of(
                "Internet E-mail", newEmail
        );
        String deleteLicense = buildPayload(updateLicenseUser);
        updateUserInCTMForm(peopleID, deleteLicense);
    }

    public String addPermissionsToUserInCTMForm(String remedyLoginId, String personId, String inputGroup) throws Exception{
        String BASE_URL = config.get("CMDBURL");
        PermissionGroupInfo info = PermissionGroupInfo.fromGroupName(inputGroup);
        String content = "";

        // Create JSON structure
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ObjectNode values = mapper.createObjectNode();

        values.put("Remedy Login ID", remedyLoginId);
        values.put("Person ID", personId);
        values.put("Permission Group", info.getGroupName());
        values.put("Permission Group ID", info.getGroupId());
        values.put("Permission Tag Name", info.getTagName());
        values.put("Product Name", info.getProductName());
        values.put("Status", "Enabled");

        root.set("values", values);
        content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .contentType(ContentType.JSON)
                .body(content)
                .post(USER_ADD_PERMISSION);

        String groupID = "", permisisonID = "";
        groupID = response1.getHeader("Location");
        if (groupID == null || groupID.isEmpty()) {
            throw new Exception("Failed to retrieve group ID from response headers.");
        }else{
            String[] parts = groupID.split("/");
            permisisonID = parts[parts.length - 1];
        }

        Assert.assertEquals(response1.getStatusCode(), 201,
                "Failed to add permissions to User. Response: " + response1.body().asString());

        return permisisonID;
    }

    public void createIncidentForCluster(String jsonFilePath, String clusterDes){
        String BASE_URL = config.get("CMDBURL");
        try {
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            finalJson = content
                    .replace("__description__", clusterDes);
        }catch (IOException e){
            log.error("Error reading JSON file: " + e.getMessage());
        }
        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .contentType(ContentType.JSON)
                .body(finalJson)
                .post(INCIDENT_CREATE_ENDPOINT);

        Assert.assertEquals(response1.getStatusCode(), 201,
                "Failed to create Incident. Response: " + response1.body().asString());

    }

    public String getJobIDForBMC_ADDM(){
        String BASE_URL = config.get("CMDBURL");
        String jobId = "";
        String url = BASE_URL+jobEndpoint;
        String ADDM_JOB_NAME = SolQAConstants.ADDM_JOB_NAME;

        Response response1 = RestAssured.given()
                .baseUri(url)
                .queryParam("offset", 0)
                .queryParam("limit", 500)
                .queryParam("job_name", ADDM_JOB_NAME)
                .header("Accept","application/json")
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .header("Content-Type", "application/json")
                .body("{\"stringToSearch\":\"\",\"fieldToSearch\":\"\",\"withCount\":true,\"agentFilterCriteria\":\"\", \"directFilterCriteria\":null}")
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .extract()
                    .response();;

        log.info("Response Status: " + response1.getStatusCode());
        log.info("Response Body:" + response1.getBody().toString());
        Assert.assertEquals(response1.getStatusCode(), 200,
                "Failed to get ADDM JOb id . Response: " + response1.body().asString());

        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        JSONArray jobsInfoArray = objJsonObject.getJSONArray("jobs_info");

        if (jobsInfoArray.length() > 0) {
            JSONObject firstJob = jobsInfoArray.getJSONObject(0);
            jobId = firstJob.getString("job_id");
            log.info("ADDM Job ID: " + jobId);
        }else{
            log.error("Unable to fetch ADDM Job id.");
        }
        return jobId;
    }

    public String runADDMJob(){

        String jobDefinitionId = getJobIDForBMC_ADDM();
        JSONObject payload = new JSONObject();
        payload.put("command", "START_RE_JOB");
        payload.put("job_definition_id", jobDefinitionId);

        String jsonPayload = payload.toString();

        String BASE_URL = config.get("CMDBURL");
        String url = BASE_URL;

        Response response1 = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "AR-JWT " +remedyAPIKey)
                .header("Accept","application/json")
                .header("Content-Type", "application/json")
                .body(jsonPayload)
                .when()
                .post(COMMAND)
                .then()
                .statusCode(202)
                .extract()
                .response();;

        //Assert.assertEquals(response1.getStatusCode(), 200,
        //        "Unable to run ADDM Reconcilation Job . Response: " + response1.body().asString());
        String output = response1.getBody().asString();
        JSONObject objJsonObject = new JSONObject(output);
        String job_run_id = objJsonObject.getString("job_run_id");
        log.info("Job Run ID: " + job_run_id);
        return job_run_id;
    }

    /**
     * Gets JWT token from the API using provided credentials.
     * Simple POST request with form data - matches Postman behavior
     */
    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return null;
        }

        String trimmedUrl = baseUrl.trim();
        if (trimmedUrl.isEmpty()) {
            return trimmedUrl;
        }

        int hashIndex = trimmedUrl.indexOf('#');
        if (hashIndex >= 0) {
            trimmedUrl = trimmedUrl.substring(0, hashIndex);
        }

        try {
            URI uri = new URI(trimmedUrl);
            if (uri.getScheme() != null && uri.getHost() != null) {
                StringBuilder normalized = new StringBuilder();
                normalized.append(uri.getScheme()).append("://").append(uri.getHost());
                if (uri.getPort() != -1) {
                    normalized.append(":").append(uri.getPort());
                }
                trimmedUrl = normalized.toString();
            }
        } catch (Exception e) {
            // fallback to string trimming for malformed URLs
            int schemeIndex = trimmedUrl.indexOf("://");
            if (schemeIndex >= 0) {
                int firstPathOrQueryIndex = trimmedUrl.indexOf('/', schemeIndex + 3);
                if (firstPathOrQueryIndex == -1) {
                    firstPathOrQueryIndex = trimmedUrl.indexOf('?', schemeIndex + 3);
                } else {
                    int queryIndex = trimmedUrl.indexOf('?', schemeIndex + 3);
                    if (queryIndex >= 0 && queryIndex < firstPathOrQueryIndex) {
                        firstPathOrQueryIndex = queryIndex;
                    }
                }
                if (firstPathOrQueryIndex >= 0) {
                    trimmedUrl = trimmedUrl.substring(0, firstPathOrQueryIndex);
                }
            }
        }

        while (trimmedUrl.endsWith("/")) {
            trimmedUrl = trimmedUrl.substring(0, trimmedUrl.length() - 1);
        }
        return trimmedUrl;
    }

    public String getJwtToken(String baseUrl, String username, String password) throws IOException {
        String endpoint = baseUrl + SolQAConstants.JWT_LOGIN_ENDPOINT;
        log.info("Requesting JWT token from: " + endpoint);
        
        // Simple form data - no encoding
        String formData = "username=" + username + "&password=" + password;
        
        // Make POST request using RestAssured
        Response response = RestAssured.given()
                .baseUri(endpoint)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(formData)
                .post();
        
        log.info("Response code: " + response.getStatusCode());
        
        if (response.getStatusCode() != 200) {
            throw new IOException("Failed to get JWT token. Status: " + response.getStatusCode());
        }
        
        String token = response.getBody().asString();
        log.info("JWT token received successfully.");
        return token;
    }

    public Itsm_Helper createIncidentViaPlatformAPI(String jsonFilePath, String dataKey, String username, String password) throws IOException {
            
            // Get JWT token
            String normalizedBaseUrl = normalizeBaseUrl(config.get("CMDB_URL"));

            String jwtToken = getJwtToken(
                        normalizedBaseUrl,
                        username, password);

            String content = readJson.readJsonInputArray(jsonFilePath, dataKey);
            JSONArray jsonArray = new JSONArray(content);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            log.info("Request Body: " + jsonObject.toString());
            
            Response response = RestAssured.given()
                    .baseUri(normalizedBaseUrl)
                    .header(SolQAConstants.AUTHORIZATION_HEADER, "AR-JWT " + jwtToken)
                    .header(SolQAConstants.CONTENT_TYPE_HEADER, SolQAConstants.APPLICATION_JSON)
                    .body(jsonObject.toString())
                    .post(INCIDENT_CREATE_ENDPOINT);
            log.info("Response Status: " + response.getStatusCode());
            log.info("Response Body:" + response.getBody().asString());
            Assert.assertEquals(response.getStatusCode(), 201,
                    "Failed to create Incident. Response: " + response.body().asString());
        return this;
    }

    public Itsm_Helper createAlarmsViaNetOps(String jsonFilePath, String dataKey, String username, String password) throws IOException {
        String jwtToken = getJwtToken(
                config.get("IS_URL"),
                username, password);
        String content = readJson.readJsonInputArray(jsonFilePath, dataKey);
        JSONArray jsonArray = new JSONArray(content);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        log.info("Request Body: " + jsonObject.toString());
        
        Response response = RestAssured.given()
                .baseUri(config.get("IS_URL"))
                .header(SolQAConstants.AUTHORIZATION_HEADER, "AR-JWT " + jwtToken)
                .header(SolQAConstants.CONTENT_TYPE_HEADER, SolQAConstants.APPLICATION_JSON)
                .header("X-Requested-By", "XMLHttpRequest")
                .body(jsonObject.toString())
                .post(NETOPS_ALARM_ENDPOINT);
        String responseBody = response.getBody().asString();
        log.info("Response Status: " + response.getStatusCode());
        log.info("Response Body:" + responseBody);
        Assert.assertTrue(String.valueOf(response.getStatusCode()).startsWith("20"),
                "Failed to create Alarm. Status Code: " + response.getStatusCode() + ", Response: " + responseBody);
        return this;
    }

    /**
     * Build a JSON payload dynamically based on the provided key-value map
     * @param valuesMap - Map containing keys and values for the payload
     * @return JSON string
     * @throws Exception
     */
    public static String buildPayload(Map<String, String> valuesMap) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ObjectNode valuesNode = mapper.createObjectNode();

        // Populate "values" node dynamically
        for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
            valuesNode.put(entry.getKey(), entry.getValue());
        }

        root.set("values", valuesNode);

        // Return formatted JSON string
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

}
