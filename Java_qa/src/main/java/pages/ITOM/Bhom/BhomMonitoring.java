package pages.ITOM.Bhom;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import com.jcraft.jsch.JSchException;

import pages.ITOM.Api.HMEvents;
import util.CommonMethods;
import util.CommonWaits;
import util.ExcelReaderForMLSituation;
import util.ReadPropertiesFile;
import util.SSHConnector;
import util.SolqaLogger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BhomMonitoring {
    static SolqaLogger log = new SolqaLogger(BhomMonitoring.class.getName());
    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static String hmUrl = config.get("ADE_PLATFORM_URL");
    CommonWaits commonWaits=new CommonWaits();
    CommonMethods commonMethods=new CommonMethods();
    private static final String SSH_CONFIG_SHEET = "sshconfig";
    private static final int SSH_CONFIG_TC = 11000;  // for CPU Utilization test
    private String remotehostname = null;
    private String remoteusername = null;
    private String remotepassword = null;
    private HMEvents hmEvents = new HMEvents();
    private static final String ALARM_POLICY = "/tsws/monitoring/api/v1.0/alarm/policies";
    private long MetricLatestTime = 0;
    private static ExcelReaderForMLSituation excelReaderForML = new ExcelReaderForMLSituation();

    public BhomMonitoring setRemoteHostname() throws IOException {
        List<Map<String, Object>> rows = excelReaderForML.getTestDataFromExcel(SSH_CONFIG_TC, SSH_CONFIG_SHEET);
            if (rows.isEmpty()) {
                throw new IllegalStateException("No SSH config found in sheet '" + SSH_CONFIG_SHEET + "' for TC " + SSH_CONFIG_TC);
            }
        this.remotehostname = (String) rows.get(0).get("ssh_host");
        if (remotehostname == null || remotehostname.trim().isEmpty()) {
            throw new IllegalStateException("Missing SSH config 'ssh_host' in sheet '" + SSH_CONFIG_SHEET + "' for TC " + SSH_CONFIG_TC);
        }
        this.remoteusername = (String) rows.get(0).get("ssh_user");
        if (remoteusername == null || remoteusername.trim().isEmpty()) {
            throw new IllegalStateException("Missing SSH config 'ssh_user' in sheet '" + SSH_CONFIG_SHEET + "' for TC " + SSH_CONFIG_TC);
        }
        this.remotepassword = (String) rows.get(0).get("ssh_password");
        if (remotepassword == null || remotepassword.trim().isEmpty()) {
            throw new IllegalStateException("Missing SSH config 'ssh_password' in sheet '" + SSH_CONFIG_SHEET + "' for TC " + SSH_CONFIG_TC);
        }
        return this;
    }

    public boolean increaseCPUUtilization() throws IOException, JSchException{
        log.info("Increasing CPU utilization on remote host: " + this.remotehostname);
        List<String> commands = Arrays.asList("yes > /dev/null &");
        
        SSHConnector sshConnector = new SSHConnector();
        sshConnector.connect(this.remotehostname, this.remoteusername, this.remotepassword, 22);
        sshConnector.openChannel();
        sshConnector.executeCommands(commands, 15000);
        sshConnector.closeChannel();
        sshConnector.disconnect();
        return true;
    }
    
    public boolean decreaseCPUUtilization() throws IOException, JSchException{
        log.info("Decreasing CPU utilization on remote host: " + this.remotehostname);
        List<String> commands = Arrays.asList("killall yes");
        SSHConnector sshConnector = new SSHConnector();
        sshConnector.connect(this.remotehostname, this.remoteusername, this.remotepassword, 22);
        sshConnector.openChannel();
        sshConnector.executeCommands(commands, 15000);
        sshConnector.closeChannel();
        sshConnector.disconnect();
        return true;
    }

    public boolean createMultipleAlarms(String PoliceName, List<String> entityTypeId, List<String> metricId, List<String> label, List<String> systemType, List<String> unit, List<Integer> threshold,List<String> severity, int executionOrder, String type, String devicename,  String helixJwtToken){
        log.info("Creating multiple alarms policy: " + PoliceName + " type: " + type + " executionOrder: " + executionOrder);
        String jsonFileName = "createMultiAlarmPolicy.json";
        String jsonContentOriginal = hmEvents.readJSon(jsonFileName);
        JSONObject AlarmPolicyjsonContent = new JSONObject();
        Random random = new Random();
        AlarmPolicyjsonContent.put("policyName", PoliceName + "-" + random.nextInt(1000));
        AlarmPolicyjsonContent.put("enabled", true);
        AlarmPolicyjsonContent.put("description", PoliceName + "-" + random.nextInt(1000));
        AlarmPolicyjsonContent.put("executionOrder", executionOrder);
        if(type.equals("MULTI")){
            AlarmPolicyjsonContent.put("type", "MULTI");
            AlarmPolicyjsonContent.put("evaluationCriteria", "device_host_name starts-with '" + devicename + "'");
        } else {
            AlarmPolicyjsonContent.put("type", "ALL");
            AlarmPolicyjsonContent.put("evaluationCriteria", "");
        }
        JSONArray entityThresholds = new JSONArray();

        

        for(int i = 0; i < entityTypeId.size(); i++){
            String jsonContent = jsonContentOriginal;
            String path = hmEvents.getAlarmPolicyPath(systemType.get(i), entityTypeId.get(i), metricId.get(i), helixJwtToken);
            if(path == null){
                log.error("Failed to get path for " + systemType + " " + entityTypeId + " " + metricId);
                return false;
            }
            jsonContent = jsonContent.replace("<entityTypeId>", entityTypeId.get(i));
            jsonContent = jsonContent.replace("<metricId>", metricId.get(i));
            jsonContent = jsonContent.replace("<label>", label.get(i));
            jsonContent = jsonContent.replace("<systemType>", systemType.get(i));
            jsonContent = jsonContent.replace("<unit>", unit.get(i));
            jsonContent = jsonContent.replace("<threshold>", threshold.get(i).toString());
            jsonContent = jsonContent.replace("<severity>", severity.get(i));
            jsonContent = jsonContent.replace("<path>", path);
            entityThresholds.put(new JSONObject(jsonContent));
        }
        AlarmPolicyjsonContent.put("entityThresholds", entityThresholds);
        log.info("Create Multiple Alarms Payload: " + AlarmPolicyjsonContent.toString());
        RestAssured.baseURI = hmUrl;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(AlarmPolicyjsonContent.toString())
                .post(ALARM_POLICY);
        log.info("Create Multiple Alarms Response: " + resp.getBody().asString());
        if(resp.getStatusCode() > 299){
            log.error("Fail to create Alarm Policy " + AlarmPolicyjsonContent.toString());
            Assert.fail("Fail to create Alarm Policy " + AlarmPolicyjsonContent.toString());
        }
        return true;
        
    }

    public JSONObject SearchMonioringmetrics(String deviceID, String providerId, String searchString, String helixJwtToken){
        RestAssured.baseURI = hmUrl;
        String endPoint = "/tsws/monitoring/api/v1.0/monitor/search?withAgentActions=true";
        String searchMatricsJsonFile = "SearchDeviceMetrics.json";
        String searchMatricsJsonContent = hmEvents.readJSon(searchMatricsJsonFile);
        searchMatricsJsonContent = searchMatricsJsonContent.replace("<sourceId>", deviceID);
        searchMatricsJsonContent = searchMatricsJsonContent.replace("<ProviderId>", providerId);
        searchMatricsJsonContent = searchMatricsJsonContent.replace("<searchString>", searchString);
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(searchMatricsJsonContent)
                .post(endPoint);
        if(resp.getStatusCode() > 299){
            log.error("Failed to search monitoring metrics for " + deviceID + " " + providerId + " " + searchString + " status: " + resp.getStatusCode());
            Assert.fail("Failed to search monitoring metrics for " + deviceID + " " + providerId + " " + searchString);
        }
        return new JSONObject(resp.getBody().asString());
    }

    public Map<String, String> getDeviceMetricId(String deviceName, String SearchMetricName, String ExpectedtypeId, String helixJwtToken){
        JSONArray deviceIdList = hmEvents.getBHOMDeviceList(deviceName, helixJwtToken);
        if(deviceIdList == null){
            log.info("Failed to get device ID list for " + deviceName);
            return null;
        }

        for (int idx = 0; idx < deviceIdList.length(); idx++) {
            String serverId = deviceIdList.getJSONObject(idx).getString("serverId");
            String deviceid = deviceIdList.getJSONObject(idx).getString("id");

            JSONObject searchMatrics = SearchMonioringmetrics(deviceid, serverId, SearchMetricName, helixJwtToken);
            if(searchMatrics == null){
                log.info("Failed to search monitoring metrics for " + deviceid + " " + SearchMetricName);
                continue;
            }
            JSONArray monitorArray = searchMatrics.getJSONObject("_object").optJSONArray("monitorSummaryTOsList");
            if(monitorArray == null || monitorArray.length() == 0){
                log.info("No monitoring metrics found for device " + deviceid + "; trying next device if available");
                continue;
            }
            Map<String, String> metricIdMap = new HashMap<>();
            metricIdMap.put("serverId", serverId);
            metricIdMap.put("deviceid", deviceid);
            for(Object monitor : monitorArray){
                JSONObject monitorObject = (JSONObject) monitor;
                String id = monitorObject.optString("id", "");
                String typeId = monitorObject.optString("typeId", "");
                if(typeId.equals(ExpectedtypeId)){
                    metricIdMap.put("monitorId", id);
                    return metricIdMap;
                }
            }
            log.info("Expected typeId " + ExpectedtypeId + " not found for device " + deviceid + "; trying next device if available");
        }
        log.error("Failed to find monitorId for device " + deviceName + " with type " + ExpectedtypeId);
        return null;
    }

    public JSONObject getDeviceMetricvalue(String moTypeId, String monitorId, String serverId, String attributeId, String helixJwtToken){
        String endPoint = "/tsws/monitoring/api/v1.0/graph/data?istextparam=false";
        String getDeviceMetricvalueJsonFile = "GetDeviceMetricvalue.json";
        long timediff = 14400;
        long endTime = (long)(hmEvents.getBhomServerTime(helixJwtToken) / 1000);
        long startTime = endTime - timediff;

        String getDeviceMetricvalueJsonContent = hmEvents.readJSon(getDeviceMetricvalueJsonFile);
        getDeviceMetricvalueJsonContent = getDeviceMetricvalueJsonContent.replace("<monitorTypeId>", moTypeId);
        getDeviceMetricvalueJsonContent = getDeviceMetricvalueJsonContent.replace("<monitorId>", monitorId);
        getDeviceMetricvalueJsonContent = getDeviceMetricvalueJsonContent.replace("<serverId>", serverId);
        getDeviceMetricvalueJsonContent = getDeviceMetricvalueJsonContent.replace("<moTypeId>", moTypeId);
        getDeviceMetricvalueJsonContent = getDeviceMetricvalueJsonContent.replace("<attributeId>", attributeId);
        getDeviceMetricvalueJsonContent = getDeviceMetricvalueJsonContent.replace("<startTime>", String.valueOf(startTime));
        getDeviceMetricvalueJsonContent = getDeviceMetricvalueJsonContent.replace("<endTime>", String.valueOf(endTime));
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(getDeviceMetricvalueJsonContent)
                .post(endPoint);
        if(resp.getStatusCode() > 299){
            log.error("Failed to get device metric value for " + moTypeId + " " + monitorId + " " + serverId + " " + attributeId + " status: " + resp.getStatusCode());
            Assert.fail("Failed to get device metric value for " + moTypeId + " " + monitorId + " " + serverId + " " + attributeId);
        }
        return new JSONObject(resp.getBody().asString());
    }

    public double extractLatestMetricValue(JSONObject metricValue) {
        //PA push data every 5 min for CPU and Memory utilization
        // So loop wait max 5 min to get the latest data
        JSONArray graphdatas = metricValue.getJSONObject("_object").getJSONArray("dataSeries");
        if (graphdatas.length() < 1) {
            log.error("No data series found");
            return -1.0;
        }
        JSONArray graphdataList = graphdatas.getJSONObject(0).getJSONArray("graphDataPoints");
        if (graphdataList.length() < 1) {
            log.error("No data points found");
            return -1.0;
        }
        double latestvalue = 0.0;
        long latesttimestamp = 0;
        for (Object graphdata : graphdataList) {
            JSONObject graphdataObject = (JSONObject) graphdata;
            long time = graphdataObject.getLong("xDataValue");
            double value = graphdataObject.getDouble("yDataValue");
            if (time > latesttimestamp) {
                latestvalue = value;
                latesttimestamp = time;
            }
        }
        if(latesttimestamp > MetricLatestTime){
            MetricLatestTime = latesttimestamp;
            log.info("Latest metric value: " + latestvalue + " at time: " + latesttimestamp);
            return latestvalue;
        }
        log.error("No change in data");
        return -2.0; // no change in Data
        
    }

    public double getDeviceLatestMetricValue(String moTypeId, String monitorId, String serverId, String attributeId, String helixJwtToken){
        for(int i = 0; i < 25; i++){
            JSONObject metricValue = getDeviceMetricvalue(moTypeId, monitorId, serverId, attributeId, helixJwtToken);
            if(metricValue == null){
                log.error("Failed to get device metric value for " + moTypeId + " " + monitorId + " " + serverId + " " + attributeId);
                return -1.0;
            }
            double currentValue = extractLatestMetricValue(metricValue);
            if(currentValue > 0){
                return currentValue;
            }
            if(currentValue == -1.0){
                log.error("No data found for " + moTypeId + " " + monitorId + " " + serverId + " " + attributeId);
                return -1.0;
            }
            commonWaits.waitForSometime(15000);
        }
        log.error("Failed to get device latest metric value for " + moTypeId + " " + monitorId + " " + serverId + " " + attributeId);
        return -1.0;
    }

    
    public JSONObject getAlarmsSeverityAndHost(String expectedSeverity, String expectedHost, String helixJwtToken){
        String msearchQueryFile = "msearchqueryonOpenClass.json";
        String className = "ALARM";
        JSONObject events_Resp = hmEvents.runMSearchQueryGetSituation(className, helixJwtToken, msearchQueryFile, 36000);
        JSONArray hitsobject = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
        if (hitsobject.length() < 1) {
            log.error("No hits found, waiting before next attempt");
            return null;
        }
        for (Object obj : hitsobject) {
            JSONObject event = (JSONObject) obj;
            String severity = event.getJSONObject("_source").getString("severity");
            String source_host = event.getJSONObject("_source").getString("source_hostname");
            if(severity.equals(expectedSeverity) && source_host.equals(expectedHost)){
                log.info("Found alarm: " + event.getJSONObject("_source").getString("_operations") + " for " + expectedHost);
                return event;
            }
        }
        
        return null;
    }

    public String exportAlarmPolicy(String helixJwtToken){
        String endPoint = "/alarm-service/api/v1.0/alarm_policies/export";
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "text/csv;charset=utf-8")
                .get(endPoint);
        String responseBody = resp.getBody().asString();
        log.info("Export alarm policy response: " + (responseBody.length() > 200 ? responseBody.substring(0, 200) : responseBody));
        if(resp.getStatusCode() > 299){
            log.error("Failed to export alarm policy status: " + resp.getStatusCode());
            Assert.fail("Failed to export alarm policy");
        }
        return responseBody;
    }

    public JSONObject getAlarmPolicyList(String helixJwtToken){
        String ALARM_POLICY_SEARCH = "/tsws/monitoring/api/v1.0/alarm/policies/search";
        RestAssured.baseURI = hmUrl;
        String searchString = "";
        JSONObject searchBody = new JSONObject()
                .put("recordsPerPage", 100)
                .put("pageIndex", 1)
                .put("searchString", searchString)
                .put("sortCriteria", new JSONObject().put("attribute", "modifiedTime").put("order", "DESC"));

        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+ helixJwtToken)
                .header("Content-Type", "application/json")
                .body(searchBody.toString())
                .post(ALARM_POLICY_SEARCH);

        if (resp.getStatusCode() != 200) {
            log.info("Alarm policy search failed: " + resp.getStatusCode());
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public boolean createConditionAlarmsPolicy(String PolicyName, String entityTypeId, String metricId, String label, String systemType, String unit, String condition,String severity, String startRange, String endRange, String helixJwtToken){
        log.info("Creating multiple alarms policy: " + PolicyName );
        String jsonFileName = "createAlarmPolicyRange.json";
        String jsonContent = hmEvents.readJSon(jsonFileName);
        String path = hmEvents.getAlarmPolicyPath(systemType, entityTypeId, metricId, helixJwtToken);
        if(path == null){
            log.error("Failed to get path for " + systemType + " " + entityTypeId + " " + metricId);
            return false;
        }
        
        jsonContent = jsonContent.replace("<entityTypeId>", entityTypeId);
        jsonContent = jsonContent.replace("<metricId>", metricId);
        jsonContent = jsonContent.replace("<label>", label);
        jsonContent = jsonContent.replace("<unit>", unit);
        jsonContent = jsonContent.replace("<path>", path);
        jsonContent = jsonContent.replace("<condition>", condition);
        jsonContent = jsonContent.replace("<startRange>", startRange);
        jsonContent = jsonContent.replace("<endRange>", endRange);
        jsonContent = jsonContent.replace("<severity>", severity);
        jsonContent = jsonContent.replace("<PolicyName>", PolicyName);
        RestAssured.baseURI = hmUrl;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+helixJwtToken)
                .header("Content-Type", "application/json")
                .body(jsonContent)
                .post(ALARM_POLICY);
        log.info("Create Multiple Alarms Response: " + resp.getBody().asString());
        if(resp.getStatusCode() > 299){
            log.error("Fail to create Alarm Policy " + PolicyName);
            Assert.fail("Fail to create Alarm Policy " + PolicyName);
        }
        return true;
        
    }
}