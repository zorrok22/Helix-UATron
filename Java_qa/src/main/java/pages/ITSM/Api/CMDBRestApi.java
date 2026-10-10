package pages.ITSM.Api;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import util.CommonWaits;
import util.SolqaLogger;

import static io.restassured.RestAssured.given;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CMDBRestApi {
    private static final SolqaLogger log = new SolqaLogger(CMDBRestApi.class.getName());
    private String itsmRestApiUrl;
    private String authToken;
    public String wortLogUpdateEndPoint = "/api/arsys/v1/entry/HPD:WorkLog";
    public String incidentHelpDeskEndPoint = "/api/arsys/v1/entry/HPD:Help Desk";
    public String incidentIncidentInterfaceEndPoint = "/api/arsys/v1/entry/HPD:IncidentInterface";
    private boolean createbackdatatedChange = false;

    public CMDBRestApi(String itsmRestApiUrl, String authToken) {
        this.itsmRestApiUrl = itsmRestApiUrl;
        this.authToken = authToken;
    }

    private String readJSon(String jsonFileName){
        try {
            String jsonContent;
            String jsonFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "CMDB" + File.separator + jsonFileName;
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            return jsonContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a field value (e.g., Description) from an incident using a search URL.
     */
    public String getIncidentFieldValue(String IncidentID, String descriptionKey) throws Exception {
        String url = incidentHelpDeskEndPoint + "?q='Incident Number' = \"" + IncidentID + "\"\n";
        RestAssured.baseURI = this.itsmRestApiUrl;

        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .when()
                .get(url);
        System.out.println(response.getBody().toString());
        if (response.getStatusCode() != 200) {
            log.info("Failed to retrieve events. Response Code: " + response.getStatusCode());
            return null;
        }

        String jsonData = response.getBody().asString();
        log.info(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        JSONObject valuesObject = null;
        if(servicesNode.has("entries") && servicesNode.getJSONArray("entries").length() > 0){
            valuesObject = servicesNode.getJSONArray("entries")
                .getJSONObject(0)
                .getJSONObject("values");
        }else{
            valuesObject = servicesNode;
        }
        
        String values = "";
        if (valuesObject.has(descriptionKey) && !valuesObject.isNull(descriptionKey)) {
            values = valuesObject.getString(descriptionKey);
        } else {
            log.info(descriptionKey + " is null or not present");
            // Handle the null case: use default value, skip, log, etc.
            values = null;
        }
        return values;
    }

    public String getFromIncidentInterfaceFieldValue(String IncidentID, String descriptionKey) throws Exception {
        RestAssured.baseURI = this.itsmRestApiUrl;
        String url = incidentIncidentInterfaceEndPoint + "?q='Incident Number' = \"" + IncidentID + "\"\n";

        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .when()
                .get(url);
        System.out.println(response.getBody().toString());
        if (response.getStatusCode() != 200) {
            System.err.println("Failed to retrieve events. Response Code: " + response.getStatusCode());
            return null;
        }

        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject root = new JSONObject(jsonData);
        JSONArray entries = root.optJSONArray("entries");
        if (entries == null || entries.length() == 0) {
            log.info("No entries found for incident " + IncidentID);
            return null;
        }
        JSONObject entry = entries.optJSONObject(0);
        if (entry == null) {
            log.info("First entry is null for incident " + IncidentID);
            return null;
        }
        JSONObject values = entry.optJSONObject("values");
        if (values == null) {
            log.info("Values object missing for incident " + IncidentID);
            return null;
        }
        log.info("Values object: " + values.toString());
        if (!values.has(descriptionKey) || values.isNull(descriptionKey)) {
            log.info("Key '" + descriptionKey + "' missing or null in incident " + IncidentID);
            return null;
        }
        return values.optString(descriptionKey, null);
    }

    public String getReconciliationIdentity(String classType, String name){
        RestAssured.baseURI = this.itsmRestApiUrl;
        String url = "/api/cmdb/v1/instance/BMC.ASSET/BMC.CORE/" + classType + "?q=%27Name%27%20%3D%20%22" + name + "%22";

        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .when()
                .get(url);
        System.out.println(response.getBody().toString());
        if (response.getStatusCode() != 200) {
            System.err.println("Failed to retrieve events. Response Code: " + response.getStatusCode());
            return null;
        }
        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        JSONArray instances = servicesNode.optJSONArray("instances");
        if (instances != null && instances.length() > 0) {
            JSONObject firstInstance = instances.optJSONObject(0);
            if (firstInstance != null) {
                JSONObject attributes = firstInstance.optJSONObject("attributes");
                if (attributes != null) {
                    String reconciliationIdentity = attributes.optString("ReconciliationIdentity", null);
                    if (reconciliationIdentity != null && !reconciliationIdentity.trim().isEmpty()) {
                        // Field exists and has a non-null, non-empty value
                        return reconciliationIdentity;
                        // Proceed with your logic here
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void updateResolutionField(String Incident_id, String serviceName, String details) throws JsonProcessingException {
        String class_type = "BMC_BusinessService";
        log.info("updateResolutionField called. Incident_id=" + Incident_id + ", serviceName=" + serviceName);
        String resolutionUpdateEndPoint = incidentIncidentInterfaceEndPoint + "/" + Incident_id;
        log.info("resolutionUpdateEndPoint=" + resolutionUpdateEndPoint);
        String reconid = getReconciliationIdentity(class_type, serviceName);
        log.info("reconid=" + reconid);
        String jsonFile = "updateIncidentresolution.json";
        String jsonContent = readJSon(jsonFile);
        log.info("Loaded json template for updateResolutionField");
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("Base URI set for updateResolutionField: " + RestAssured.baseURI);

        if (reconid == null){
            log.info("reconid is null, removing ServiceCI fields");
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = (ObjectNode) mapper.readTree(jsonContent);
            JsonNode values = root.get("values");
            ((ObjectNode) values).remove("ServiceCI");
            ((ObjectNode) values).remove("ServiceCI_ReconID");
            ((ObjectNode) values).remove("Status");
            ((ObjectNode) values).remove("Status_Reason");
            jsonContent =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        }else {
            log.info("reconid present, replacing placeholders");
            jsonContent = jsonContent.replace("<RECON_ID>", reconid).replace("<Service_Name>", serviceName);
        }
        jsonContent = jsonContent.replace("<RESOLUTION>", details);
        log.info("Final payload for updateResolutionField: " + jsonContent);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .put(resolutionUpdateEndPoint);
        log.info("updateResolutionField response body=" + response.getBody().asString() + " response status code=" + response.getStatusCode());
        if (response.getStatusCode() > 299) {
            log.error("Failed to update. Response Code: " + response.getStatusCode());
        }
    }

    public boolean updateWorkLog(String IncidentID, String submitter, String assignGroup,  String title, String detail) throws JsonProcessingException {

        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("updateWorkLog base URI=" + RestAssured.baseURI);
        String jsonFile = "updateIncidentworklog.json";
        String jsonContent = readJSon(jsonFile);
        log.info("Loaded updateWorkLog template");
        jsonContent = jsonContent.replace("<Submitter>", submitter);
        log.info("Replaced Submitter with " + submitter);
        jsonContent = jsonContent.replace("<Assignee Groups>", assignGroup);
        log.info("Replaced Assignee Group with " + assignGroup);
        jsonContent = jsonContent.replace("<Detail Description>", detail);
        log.info("Replaced Detail Description");
        jsonContent = jsonContent.replace("<Short Description>", title);
        log.info("Replaced Short Description with " + title);
        jsonContent = jsonContent.replace("<incident_id>", IncidentID);
        log.info("Replaced Incident ID with " + IncidentID);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(wortLogUpdateEndPoint);
        log.info("updateWorkLog response code=" + response.getStatusCode() + "Response " + response.getBody().asString());
        if (response.getStatusCode() > 299) {
            log.error("Failed to create worklog. Response Code: " + response.getStatusCode());
            return false;
        }
        return true;
    }

    public boolean createChangeRequest(String Summary, String Description) throws JsonProcessingException {
        String changeRequestEndPoint = "/api/arsys/v1/entry/CHG:ChangeInterface_Create";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("createChangeRequest endpoint=" + changeRequestEndPoint + ", Summary=" + Summary);
        String jsonFile = "createChangeRequest.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<summary_Description>", Summary);
        log.info("Inserted summary text");
        jsonContent = jsonContent.replace("<Detailed_Description>", Description);
        log.info("Inserted detailed description");

        Instant tim_for = Instant.now();
        System.out.println("Initial tim_for (Instant): " + tim_for);
        if(createbackdatatedChange){
            tim_for = tim_for.minus(Duration.ofHours(6));
        }

        tim_for = tim_for.plus(Duration.ofMinutes(5));
        System.out.println("tim_for after subtracting 1 hour: " + tim_for);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        // To format an Instant, we first associate it with a time-zone (UTC).
        ZonedDateTime startZonedTime = tim_for.atZone(ZoneOffset.UTC);
        String s_tim = startZonedTime.format(formatter);
        System.out.println("Formatted s_tim: " + s_tim);

        tim_for = tim_for.plus(Duration.ofMinutes(10));
        System.out.println("tim_for after adding 5 minutes: " + tim_for);

        ZonedDateTime endZonedTime = tim_for.atZone(ZoneOffset.UTC);
        String e_tim = endZonedTime.format(formatter);
        System.out.println("Formatted e_tim: " + e_tim);

        System.out.println("\n--- Final Results ---");
        System.out.println("s_tim = " + s_tim);
        System.out.println("e_tim = " + e_tim);
        jsonContent = jsonContent.replace("<Scheduled Start Date>", s_tim);
        jsonContent = jsonContent.replace("<Scheduled End Date>", e_tim);
        log.info("Injected schedule window into payload");
        System.out.println(jsonContent);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(changeRequestEndPoint);
        log.info("createChangeRequest response=" + response.getStatusCode());
        if (response.getStatusCode() > 299) {
            log.error("Failed to create change request. Response Code: " + response.getStatusCode());
            log.error(response.getBody().asString());
            return false;
        }
        return true;
    }

    public Map<String, String> getChangeIdBasedOnSummary(String summary)
    {
        String changeRequestEndPoint = "/api/arsys/v1/entry/CHG:ChangeInterface?q='Description' = \"" + summary + "\"";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("getChangeIdBasedOnSummary called for summary=" + summary);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .get(changeRequestEndPoint);

        if (response.getStatusCode() > 299) {
            log.error("Failed to get change id based on summary. Response Code: " + response.getStatusCode());
            return Collections.emptyMap();
        }
        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        JSONArray entries = servicesNode.optJSONArray("entries");
        if (entries == null || entries.length() == 0) {
            log.info("No change entries found for summary " + summary);
            return Collections.emptyMap();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        JSONObject latestEntry = null;
        Instant latestSubmitDate = null;

        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.optJSONObject(i);
            if (entry == null) {
                log.info("Skipping null entry at index " + i);
                continue;
            }
            JSONObject values = entry.optJSONObject("values");
            if (values == null) {
                log.info("Skipping entry " + i + " because values is null");
                continue;
            }
            String submitDateStr = values.optString("Submit Date", null);
            Instant submitInstant = parseSubmitDate(submitDateStr, formatter);
            if (latestEntry == null) {
                log.info("Setting initial latest entry using submit date " + submitDateStr);
                latestEntry = entry;
                latestSubmitDate = submitInstant;
                continue;
            }
            if (submitInstant == null) {
                log.info("Submit date parse failed for entry " + i);
                continue;
            }
            if (latestSubmitDate == null || submitInstant.isAfter(latestSubmitDate)) {
                log.info("Found newer submit date " + submitDateStr + " replacing previous");
                latestEntry = entry;
                latestSubmitDate = submitInstant;
            }
        }

        if (latestEntry == null) {
            log.info("No latest entry determined");
            return Collections.emptyMap();
        }

        JSONObject latestValues = latestEntry.optJSONObject("values");
        if (latestValues == null) {
            log.info("Latest entry missing values object");
            return Collections.emptyMap();
        }

        Map<String, String> changeIdentifiers = new HashMap<>();
        changeIdentifiers.put("RequestID", latestValues.optString("Request ID", ""));
        changeIdentifiers.put("infrachangeid", latestValues.optString("Infrastructure Change ID", ""));
        changeIdentifiers.put("cabmanagerlogin", latestValues.optString("CAB Manager Login", ""));
        log.info("Resolved change identifiers: " + changeIdentifiers);
        return changeIdentifiers;
    }

    private Instant parseSubmitDate(String dateValue, DateTimeFormatter formatter) {
        if (dateValue == null || dateValue.isEmpty()) {
            log.info("parseSubmitDate: empty date value");
            return null;
        }
        try {
            return ZonedDateTime.parse(dateValue, formatter).toInstant();
        } catch (DateTimeParseException parseException) {
            log.info("Failed ZonedDateTime parse for " + dateValue + ", trying Instant.parse");
            try {
                return Instant.parse(dateValue);
            } catch (Exception ignored) {
                System.err.println("Unable to parse Submit Date: " + dateValue);
                return null;
            }
        }
    }

    public String getSupportGroupID(String supportGroupName) {
        String supportGroupEndPoint = "api/arsys/v1/entry/CTM:Support Group?q='Support Group Name'== \"" + supportGroupName + "\"";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("getSupportGroupID called for " + supportGroupName);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .get(supportGroupEndPoint);
        if (response.getStatusCode() > 299) {
            log.error("Failed to get support group id. Response Code: " + response.getStatusCode());
            return null;
        }
        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        String supportGroupId = servicesNode.getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("Support Group ID");
        log.info("Support Group ID resolved: " + supportGroupId);
        return supportGroupId;
    }

    public String getCompanyPersonID(String loginID) {
        String companyPersonEndPoint = "api/arsys/v1/entry/CTM:People?q='Remedy Login ID'==\"" + loginID + "\"";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("getCompanyPersonID called for login " + loginID);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .get(companyPersonEndPoint);
        if (response.getStatusCode() > 299) {
            log.error(response.getBody().asString());
            log.error("Failed to get support group id. Response Code: " + response.getStatusCode());
            return null;
        }
        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        String personId = servicesNode.getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("Person ID");
        log.info("Person ID resolved: " + personId);
        return personId;
    }

    public Map<String, String> getServiceReconID(String serviceName) {
        String serviceReconIDEndPoint = "api/cmdb/v1/instance/BMC.ASSET/BMC.CORE/BMC_BusinessService?q='Name' = \"" + serviceName + "\"";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("getServiceReconID called for service " + serviceName);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .get(serviceReconIDEndPoint);
        if (response.getStatusCode() > 299) {
            log.error(response.getBody().asString());
            log.error("Failed to get service recon id. Response Code: " + response.getStatusCode());
            return null;
        }
        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        String instanceID = servicesNode.getJSONArray("instances").getJSONObject(0).getJSONObject("attributes").getString("InstanceId");
        String name = servicesNode.getJSONArray("instances").getJSONObject(0).getJSONObject("attributes").getString("Name");
        String reconID = servicesNode.getJSONArray("instances").getJSONObject(0).getJSONObject("attributes").getString("ReconciliationIdentity");
        log.info("Service recon resolved: instanceID=" + instanceID + ", name=" + name + ", reconID=" + reconID);
        return Map.of("instanceID", instanceID, "name", name, "reconID", reconID);
    }

    public void updateChangeImpactedService(String requestId,String personID, String groupID, Map<String, String> serviceReconID) throws JsonProcessingException {
        String changeRequestEndPoint = "/api/arsys/v1/entry/CHG:ChangeInterface/" + requestId;
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("updateChangeImpactedService requestId=" + requestId + ", personID=" + personID + ", groupID=" + groupID);
        String jsonFile = "updateChangeImpactedService.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<ServiceCI>", serviceReconID.get("name"));
        jsonContent = jsonContent.replace("<ServiceCI_ReconID>", serviceReconID.get("reconID"));
        jsonContent = jsonContent.replace("<PersonID>", personID);
        jsonContent = jsonContent.replace("<GroupID>", groupID);
        log.info("updateChangeImpactedService payload prepared");
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .put(changeRequestEndPoint);
        log.info("updateChangeImpactedService response code=" + response.getStatusCode());
        if (response.getStatusCode() > 299) {
            log.error(response.getBody().asString());
            log.error("Failed to update change impacted service. Response Code: " + response.getStatusCode());
        }
    }

    public Map<String, String> getCIReconID(String ciName) {
        String ciReconIDEndPoint = "api/cmdb/v1/instance/BMC.ASSET/BMC.CORE/BMC_ComputerSystem?q='HostName' = \"" + ciName + "\"";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("getCIReconID called for CI " + ciName);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .get(ciReconIDEndPoint);
        if (response.getStatusCode() > 299) {
            log.error(response.getBody().asString());
            log.error("Failed to get ci recon id. Response Code: " + response.getStatusCode());
            return null;
        }
        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        String instanceID = servicesNode.getJSONArray("instances").getJSONObject(0).getJSONObject("attributes").getString("InstanceId");
        String name = servicesNode.getJSONArray("instances").getJSONObject(0).getJSONObject("attributes").getString("Name");
        String reconID = servicesNode.getJSONArray("instances").getJSONObject(0).getJSONObject("attributes").getString("ReconciliationIdentity");
        String classid = servicesNode.getJSONArray("instances").getJSONObject(0).getJSONObject("attributes").getString("ClassId");
        log.info("CI recon resolved: instanceID=" + instanceID + ", classid=" + classid);
        return Map.of("instanceID", instanceID, "name", name, "reconID", reconID, "classid", classid);
    }

    public void associatedCItoChangerequest(String infrachangeid, Map<String, String> ciReconID) throws JsonProcessingException {
        String associatedCItoChangerequestEndPoint = "api/arsys/v1/entry/CHG:Associations";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("associatedCItoChangerequest infrachangeid=" + infrachangeid);
        String jsonFile = "createchangeAssociateForm.json";
        String jsonContent = readJSon(jsonFile);
        String formName = "AST:ComputerSystem";
        if(ciReconID.get("classid").equals("BMC_BUSINESSSERVICE")) {
            formName = "AST:BusinessService";
        }
        jsonContent = jsonContent.replace("<infra_change_id>", infrachangeid);
        jsonContent = jsonContent.replace("<form_name>", formName);
        jsonContent = jsonContent.replace("<name>", ciReconID.get("name"));
        jsonContent = jsonContent.replace("<recon_id>", ciReconID.get("reconID"));
        log.info("associatedCItoChangerequest payload ready" + jsonContent);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(associatedCItoChangerequestEndPoint);
        log.info("associatedCItoChangerequest response code=" + response.getStatusCode());
        if (response.getStatusCode() > 299) {
            log.error(response.getBody().asString());
            log.error("Failed to associated ci to change request. Response Code: " + response.getStatusCode());
        }
    }

    public boolean CreatchangeCiAssocaiteForm(String infrachangeid, String Summary, Map<String, String> ciReconID) throws JsonProcessingException {
        String creatchangeCiAssocaiteFormEndPoint = "api/arsys/v1/entry/CHG:Change-CI-Associations";
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("CreatchangeCiAssocaiteForm infrachangeid=" + infrachangeid + ", summary=" + Summary);
        String jsonFile = "createchangeCiAssociationForm.json";
        String jsonContent = readJSon(jsonFile);
        String formName = "AST:ComputerSystem";
        if(ciReconID.get("classid").equals("BMC_BUSINESSSERVICE")) {
            formName = "AST:BusinessService";
        }
        jsonContent = jsonContent.replace("<infra_change_id>", infrachangeid);
        jsonContent = jsonContent.replace("<form_name>", formName);
        jsonContent = jsonContent.replace("<name>", ciReconID.get("name"));
        jsonContent = jsonContent.replace("<CI_ReconID>", ciReconID.get("reconID"));
        jsonContent = jsonContent.replace("<classid>", ciReconID.get("classid"));
        jsonContent = jsonContent.replace("<instanceid>", ciReconID.get("instanceID"));
        jsonContent = jsonContent.replace("<Summary>", Summary);
        log.info("CreatchangeCiAssocaiteForm payload ready" + jsonContent);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(creatchangeCiAssocaiteFormEndPoint);
        log.info("CreatchangeCiAssocaiteForm response code=" + response.getStatusCode());
        if (response.getStatusCode() > 299) {
            log.error(response.getBody().asString());
            log.error("Failed to create change ci association form. Response Code: " + response.getStatusCode());
            return false;
        }
        return true;
    }


    public String getCurrentChangeStatus(String requestId) {
        String getCurrentChangeStatusEndPoint = "api/arsys/v1/entry/CHG:ChangeInterface/" + requestId;
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("getCurrentChangeStatus requestId=" + requestId);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .get(getCurrentChangeStatusEndPoint);
        if (response.getStatusCode() > 299) {
            log.error("Failed to get current change status. Response Code: " + response.getStatusCode());
            return null;
        }
        String jsonData = response.getBody().asString();
        System.out.println(jsonData);
        JSONObject servicesNode = new JSONObject(jsonData);
        String status = servicesNode.getJSONObject("values").getString("Change Request Status");
        log.info("Current change status for " + requestId + " = " + status);
        return status;
    }

    public boolean updateChangeRequestStatus(String requestId, String ChangeRequestStatus, String InfrastructureChangeID) throws JsonProcessingException {
        String updateChangeRequestStatusEndPoint = "api/arsys/v1/entry/CHG:ChangeInterface/" + requestId;
        RestAssured.baseURI = this.itsmRestApiUrl;
        log.info("updateChangeRequestStatus requestId=" + requestId + ", status=" + ChangeRequestStatus);
        String jsonFile = "updateChangeRequestStatus.json";
        String jsonContent = readJSon(jsonFile);
        jsonContent = jsonContent.replace("<ChangeRequestStatus>", ChangeRequestStatus);
        jsonContent = jsonContent.replace("<infrastructure_change_id>", InfrastructureChangeID);
        jsonContent = jsonContent.replace("<Status Reason>", "Change Request Status updated to " + ChangeRequestStatus);
        log.info("updateChangeRequestStatus payload=" + jsonContent);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .put(updateChangeRequestStatusEndPoint);
        log.info("updateChangeRequestStatus response=" + response.getStatusCode()) ;
        if (response.getStatusCode() > 299) {
            log.info("updateChangeRequestStatus response=" + response.getStatusCode() + " " + response.getBody().asString());
            log.error("Failed to update change request status. Response Code: " + response.getStatusCode());
            return false;
        }
        return true;
    }

    public Map<String, String> createChangeRequestUpdateStatus(String Summary, String Description, String ServiceName, String[] CIList, String loginid, String companyid) throws JsonProcessingException {
        String[] validChangeStatusSequence = {"Draft", "Request For Authorization", "Request For Change", "Planning In Progress", "Scheduled For Review", "Scheduled For Approval", "Scheduled", "Implementation In Progress"};
        CommonWaits commonWait = new CommonWaits();
        log.info("createChangeRequestUpdateStatus summary=" + Summary );

        Map<String, String> serviceReconID = getServiceReconID(ServiceName);
        String personID = getCompanyPersonID(loginid);
        String groupID = getSupportGroupID(companyid);

        if(!createChangeRequest(Summary, Description)){
            log.error("Change creation failed");
            return null;
        }
        Map<String, String> changeIdSummary = getChangeIdBasedOnSummary(Summary);
        log.info("Change identifiers resolved: " + changeIdSummary + ", personID=" + personID + ", groupID=" + groupID);

        updateChangeImpactedService(changeIdSummary.get("RequestID"), personID, groupID, serviceReconID);
        for(String ciName : CIList) {
            Map<String, String> ciReconID = getCIReconID(ciName);
            log.info("Associating CI " + ciName);
            associatedCItoChangerequest(changeIdSummary.get("infrachangeid"), ciReconID);
            CreatchangeCiAssocaiteForm(changeIdSummary.get("infrachangeid"), Summary, ciReconID);
        }

        return changeIdSummary;
    }

    public Map<String, String> createChangeRequestUpdateStatus(String Summary, String Description, String requestedStatus, String loginid, String companyid) throws JsonProcessingException {
        RemedyApiClient remedyApiClient = new RemedyApiClient(this.itsmRestApiUrl);
        log.info("Create a Change without associating it to any service or CI");
        String[] validChangeStatusSequence = {"Draft", "Request For Authorization", "Request For Change", "Planning In Progress", "Scheduled For Review", "Scheduled For Approval", "Scheduled", "Implementation In Progress"};
        CommonWaits commonWait = new CommonWaits();
        log.info("createChangeRequestUpdateStatus summary=" + Summary + ", requestedStatus=" + requestedStatus);


        if(!createChangeRequest(Summary, Description)){
            log.error("Change creation failed");
            return null;
        }
        Map<String, String> changeIdSummary = getChangeIdBasedOnSummary(Summary);
        log.info("Change identifiers resolved: " + changeIdSummary);

        log.info("CAB Manager Login: " + changeIdSummary.get("cabmanagerlogin"));
        if(changeIdSummary.get("cabmanagerlogin") != null && !changeIdSummary.get("cabmanagerlogin").isEmpty()){
            log.info("CAB Manager Login is not null and not empty");
            try {
                this.authToken = remedyApiClient.getRemedyApiKey(changeIdSummary.get("cabmanagerlogin"), "__USER_PASSWORD__");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for(int i =0;i<20;i ++) {
            String currentChangeStatus = getCurrentChangeStatus(changeIdSummary.get("RequestID"));
            log.info("Attempt " + i + " status=" + currentChangeStatus);
            if(currentChangeStatus != null && currentChangeStatus.equals(requestedStatus)) {
                log.info("Requested status reached");
                return changeIdSummary;
            }
            int currentIndex = Arrays.asList(validChangeStatusSequence).indexOf(currentChangeStatus);
            if (currentIndex == -1 || currentIndex + 1 >= validChangeStatusSequence.length) {
                throw new IllegalStateException("Unable to progress change status from " + currentChangeStatus);
            }
            String nextStatus = validChangeStatusSequence[currentIndex + 1];
            log.info("Advancing to " + nextStatus);
            updateChangeRequestStatus(changeIdSummary.get("RequestID"), nextStatus, changeIdSummary.get("infrachangeid"));
            commonWait.waitForSometime(10000);
        }
        log.info("Returning changeIdSummary after max attempts");
        return changeIdSummary;
    }

    public boolean moveChangeStatusComplete(String infraChangeId, String requestId, String actualStartDate, String actualEndDate) {
        log.info("moveChangeStatusComplete requestId=" + requestId + ", infraChangeId=" + infraChangeId);
        RestAssured.baseURI = this.itsmRestApiUrl;

        JSONObject values = new JSONObject();
        values.put("Change Request Status", "Completed");
        values.put("Infrastructure Change ID", infraChangeId);
        values.put("Status Reason", "Final Review Complete");
        values.put("Actual Start Date", actualStartDate);
        values.put("Actual End Date", actualEndDate);

        JSONObject payload = new JSONObject();
        payload.put("values", values);

        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(payload.toString())
                .put("/api/arsys/v1/entry/CHG:ChangeInterface/" + requestId);

        log.info("moveChangeStatusComplete response=" + response.getStatusCode());
        log.info(response.getBody().asString());
        if (response.getStatusCode() > 299) {
            log.error("Failed to move change to Completed. Response Code: " + response.getStatusCode());
            return false;
        }
        return true;
    }

    public boolean moveChangeStatusCancelled(String infraChangeId, String requestId) {
        log.info("moveChangeStatusCancelled requestId=" + requestId + ", infraChangeId=" + infraChangeId);
        RestAssured.baseURI = this.itsmRestApiUrl;
        JSONObject values = new JSONObject();
        values.put("Change Request Status", "Cancelled");
        values.put("Infrastructure Change ID", infraChangeId);
        values.put("Status Reason", "No Longer Required");
        JSONObject payload = new JSONObject();
        payload.put("values", values);

        Response cancelResponse = RestAssured.given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(payload.toString())
                .put("/api/arsys/v1/entry/CHG:ChangeInterface/" + requestId);
        log.info("moveChangeStatusCancelled response=" + cancelResponse.getStatusCode());
        if (cancelResponse.getStatusCode() > 299) {
            log.error("Failed to cancel change. Response: " + cancelResponse.getStatusCode() + " " + cancelResponse.getBody().asString());
            return false;
        }
        return true;

    }
    public void setCreatebackdatatedChange(boolean createbackdatatedChange) {
        this.createbackdatatedChange = createbackdatatedChange;
    }

    public boolean updateIncidentTicketField(String incidentId, String fieldname, String fieldvalue) {
        log.info("updateIncidentTicket incidentId=" + incidentId + ", fieldname=" + fieldname + ", fieldvalue=" + fieldvalue);
        RestAssured.baseURI = this.itsmRestApiUrl;
        JSONObject values = new JSONObject();
        values.put(fieldname, fieldvalue);
        JSONObject payload = new JSONObject();
        payload.put("values", values);
        Response response = given()
                .header("Authorization", "AR-JWT " + this.authToken)
                .header("Content-Type", "application/json")
                .body(payload.toString())
                .put("/api/arsys/v1/entry/HPD:IncidentInterface/" + incidentId);
        log.info("updateIncidentTicket response=" + response.getStatusCode());
        if (response.getStatusCode() > 299) {
            log.error("Failed to update incident ticket. Response Code: " + response.getStatusCode());
            return false;
        }
        return true;
    }

}
