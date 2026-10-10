package ITOM.Bhom;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITOM.Api.HMEvents;
import util.CommonMethods;
import util.CommonWaits;
import util.ExcelReaderForMLSituation;
import util.ReadPropertiesFile;
import util.SSHConnector;
import util.SolqaDetails;
import util.SolqaLogger;
import util.Product;

/**
 * Java translation of Robot "3rd-party-metrics-push" (non-bmc-metrics.robot).
 * Uses APIs where possible and SSH to trigger the remote Python metric push.
 */
public class NonBmcMetricsPushTest {
    private static final SolqaLogger log = new SolqaLogger(NonBmcMetricsPushTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    private static final int EXPECTED_EVENT_COUNT = 20;
    private static final int EXPECTED_DEVICE_COUNT = 2;
    private static final int SSH_CONFIG_TC = 13337;
    private static final String SSH_CONFIG_SHEET = "sshconfig";
    CommonWaits commonWait = new CommonWaits();
    CommonMethods commonmethod = new CommonMethods();
    private final Map<String, Object> sshConfig = loadSshConfig();
    private final String NONBMC_SSH_HOST = requireSshValue("ssh_host");
    private final String NONBMC_SSH_USER = requireSshValue("ssh_user");
    private final String NONBMC_SSH_PASSWORD = requireSshValue("ssh_password");
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();


    @Test(description = "Non-BMC Data : External Data ingestion (Time series metric data)", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "13337", Products = {Product.Service_Monitoring})
    public void DRSQA_13337_External_Data_ingestion_Time_series_metric_data() throws Exception {
        String PolicyPreFix = "nonbcm-";
        log.info("Starting 3rd-party-metrics-push test");

        String jwtToken = commonmethod.refreshJwtToken(null);
        log.info("JWT token generated");

        HMEvents hmEvents = new HMEvents();
        String apiKey = resolveApiKey(jwtToken, hmEvents);
        log.info("API key resolved");

        String appRnd = deriveAppRandom(apiKey);
        log.info("Derived app random value: " + appRnd);

        String hmHostname = config.get("ADE_PLATFORM_URL").replace("https://","");
        log.info("HM hostname: " + hmHostname);
        String result = hmHostname.split("[-.]")[0];
        String hmTenant = result + "." + appRnd;
        log.info("HM tenant: " + hmTenant);
        String hmUser = config.get("ADE_USERNAME");
        String hmPassword = config.get("ADE_PASSWORD");
        log.info("Delete existing events for device-" + appRnd);
        hmEvents.SearchanddeleteAlarmPolicies(PolicyPreFix, jwtToken);
        hmEvents.searchAndDeleteGroup(PolicyPreFix , jwtToken);

        log.info("Run the Python Script");
        runRemoteMetricScript(appRnd, apiKey, hmHostname, hmTenant, hmUser, hmPassword);
        log.info("Remote metric push script triggered");
        boolean devicesFound = false;
        for (int i = 0; i < 10; i++) {
            JSONArray devices = hmEvents.getBHOMDeviceList("device-" + appRnd, jwtToken);
            int deviceCount = devices != null ? devices.length() : 0;
            if (deviceCount > EXPECTED_DEVICE_COUNT) {
                log.info("Found " + deviceCount + " devices for device-" + appRnd);
                devicesFound = true;
                break;
            } else {
                log.info("Found " + deviceCount + " devices for device-" + appRnd + ", waiting for " + (EXPECTED_DEVICE_COUNT - deviceCount) + " more devices");
            }
            commonWait.waitForSometime(10000);
        }
        if (!devicesFound) {
            Assert.fail("Expected at least " + EXPECTED_DEVICE_COUNT + " devices for device-" + appRnd);
        }

        try{
            String AlarmPolicyFile = "CreatethirdPartyAlarmNagios.json";
            String jsonContent = hmEvents.readJSon(AlarmPolicyFile);
            jsonContent = jsonContent.replace("<Policy_Name>", PolicyPreFix + appRnd).replace("<Tenant_ID>",appRnd);
            boolean policyCreated = hmEvents.Create3rdPartyAlarmPolicy(jsonContent, jwtToken);
            if (!policyCreated) {
                Assert.fail("Failed to create patrol agent alarm policy for device-" + appRnd);
            }
            commonWait.waitForSometime(5000);
            boolean groupPolicyCreated = hmEvents.createGroupPolicy(PolicyPreFix + appRnd, "device", jwtToken);
            if (!groupPolicyCreated) {
                Assert.fail("Failed to create group policy for device-" + appRnd);
            }
            commonWait.waitForSometime(5000); // wait for system to accept group policy
            int EventCount = 0;
            for(int i = 0; i < 21; i++) {
                JSONObject groupPolicyDetails = hmEvents.getGroupPolicyDetails(PolicyPreFix + appRnd, jwtToken);
                if (groupPolicyDetails == null) {
                    Assert.fail("Failed to get group policy details for device-" + appRnd);
                    return;
                }

                EventCount = groupPolicyDetails.getJSONObject("tagDetails").optInt("totalEvents", 0);
                if (EventCount >= EXPECTED_EVENT_COUNT) {
                    log.info("generated Alarm " + EventCount);
                    break;
                }
                commonWait.waitForSometime(15000); // retry after every 15 sec
            }
            log.info("Total Alarm Count " + EventCount);
            if (EventCount < EXPECTED_EVENT_COUNT) {
                Assert.fail("Expected at least " + EXPECTED_EVENT_COUNT + " events for device-" + appRnd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally{
            log.info("Deleting group policy for device-" + appRnd);
            hmEvents.SearchanddeleteAlarmPolicies(PolicyPreFix, jwtToken);
            hmEvents.searchAndDeleteGroup(PolicyPreFix , jwtToken);
        }

    }


    private String resolveApiKey(String jwtToken, HMEvents hmEvents) {
        String configured = config.get("BHOM_API_KEY");
        if (configured != null && !configured.isEmpty()) {
            return configured;
        }
        String apiKey = hmEvents.getBhomApiKey(jwtToken);
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Unable to resolve API key");
        }
        return apiKey;
    }

    private String deriveAppRandom(String apiKey) {
        String base = apiKey.replaceFirst("::.*", "");
        return base.replace("-", "");
    }

    private Map<String, Object> loadSshConfig() {
        log.info("get the detail from resource file");
        try {
            List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(SSH_CONFIG_TC, SSH_CONFIG_SHEET);
            if (rows.isEmpty()) {
                throw new IllegalStateException("No SSH config found in sheet '" + SSH_CONFIG_SHEET + "' for TC " + SSH_CONFIG_TC);
            }
            return rows.get(0);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read SSH config from Excel sheet '" + SSH_CONFIG_SHEET + "'", e);
        }
    }

    private String requireSshValue(String key) {
        Object val = sshConfig.get(key);
        if (val == null || val.toString().trim().isEmpty()) {
            throw new IllegalStateException("Missing SSH config '" + key + "' in sheet '" + SSH_CONFIG_SHEET + "'");
        }
        return val.toString();
    }

    private void runRemoteMetricScript(String appRnd, String apiKey, String hmHostname, String hmTenant,
                                       String hmUser, String hmPassword) throws Exception {
        SSHConnector ssh = new SSHConnector();
        ssh.connect(NONBMC_SSH_HOST, NONBMC_SSH_USER, NONBMC_SSH_PASSWORD, 22);
        ssh.openChannel();

        List<String> commands = Arrays.asList(
                "ps -eaf | grep -i python | awk '{print $2}' | xargs -r kill -9",
                "cd /data1/hmmetric && cp non-bmc-metric_api.py non-bmc-metric.py",
                "cd /data1/hmmetric && sed -i \"s/device-ind-/device-" + appRnd + "/g\" non-bmc-metric.py",
                "cd /data1/hmmetric && sed -i \"s/App12/" + appRnd + "/g\" non-bmc-metric.py",
                "cd /data1/hmmetric && sed -i \"s/__USER_PASSWORD__/" + hmPassword + "/g\" non-bmc-metric.py",
                "cd /data1/hmmetric && sed -i \"s/adesolqa/" + hmUser + "/g\" non-bmc-metric.py",
                "cd /data1/hmmetric && sed -i \"s/replace_apikey_robot/" + apiKey + "/g\" non-bmc-metric.py",
                "cd /data1/hmmetric && rm -f nohup.out"
        );
        String runScript = "cd /data1/hmmetric && nohup python3 non-bmc-metric.py -n " + hmHostname + " -t " + hmTenant + " -o 3 -d 720 > nohup.out 2>&1 &";
        ssh.executeCommands(commands,60000);
        ssh.write(runScript);
        commonWait.waitForSometime(5000);
        ssh.write("\n");
        commonWait.waitForSometime(10000);
        String output = ssh.read(5);
        log.info(output);
        ssh.disconnect();
        log.info("SSH session closed");
    }


}

