package ITOM.Otel;

import com.fasterxml.jackson.databind.JsonNode;
import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.Discovery;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITOM.GrafanaDashboard.Dashboard_FeatureMethods;
import pages.ITOM.Intelligent_Integrations.IIFeatureMethods;
import util.*;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OtelTests extends Launcher {

    static SolqaLogger log = new SolqaLogger(OtelTests.class.getName());
    Portal_FeatureMethods portalFeatureMethods;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    Map<String, String> configProp = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    BhomFeatureMethods bhomFeatureMethods;
    IIFeatureMethods iiFeatureMethods;
    DashboadFeatureMethods helixDashboardFeatureMethods;
    CommonWaits commonWaits;
    String accessKey = SolQAConstants.API_ACCESS_KEY;
    String secreteKey = SolQAConstants.API_SECRET_KEY;
    String policyId = "";
    Discovery discovery;

    Dashboard_FeatureMethods dashboardFeatureMethods;

    @BeforeMethod(alwaysRun = true)
    public void preconditions() {
        WebDriver driver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),SolQAConstants.appITOM);
        setDriver(driver);
        bhomFeatureMethods = new BhomFeatureMethods();
        commonWaits = new CommonWaits();
        iiFeatureMethods = new IIFeatureMethods();
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        portalFeatureMethods = new Portal_FeatureMethods();
        helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
        discovery = new Discovery();
        dashboardFeatureMethods = new Dashboard_FeatureMethods();
    }


    @Test(description = "Data Injection via OpenTelemetry from ITSM Cluster into BMC Helix (Default Blueprint E2E)",groups={"otel"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-15286",Products = {Product.OTEL, Product.AiOPS, Product.Operations_Management, Product.Helix_Discovery})
    public void DRSQA_15286_Data_Injection_Via_Otel_From_ITSM_Default_Blueprint(ITestContext context) throws Exception {

        String blueprintName = "Default Blueprint for OTel Service";
        String serviceName = "Otel_Service_"+ RandomStringUtils.randomAlphanumeric(5);
        String alarmPolicyName = "Otel_RED_Alarm_Policy"+ RandomStringUtils.randomAlphanumeric(5);
        String OtelREDAlarmJsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Otel" + File.separator + "Otel_Red_Alarm_Policy.json";
        String jwtToken = new Events_API().getJwtToken(accessKey, secreteKey);

        log.info("---- Precheck 1: Verify Otel data is loading in Otel tile: First go to Otel tile. Check if data is loaded in the panel. ALso fetch namespace value ----------");
        portalFeatureMethods.crossLaunchProduct("ADE Helix Telemetry");
        helixDashboardFeatureMethods.refreshDashboard();
        String stackNameSpace = helixDashboardFeatureMethods.getOtelNameSpaceValue();
        log.info("Fetched Otel Namespace value: " + stackNameSpace);
        Assert.assertTrue(stackNameSpace != null && !stackNameSpace.isEmpty(), "Pre check failed: Otel Namespace value in Dashboard filter is empty.");
        Assert.assertTrue(helixDashboardFeatureMethods.isLatencyPanelDataPresent("Latency Trend"), "Pre check failed: No data present in Latency panel for Otel dashboard.");

        log.info("---- Precheck 2: Go to BHOM & search for devices. Search based on namespace & see that device is listed  ----------");
        portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        Assert.assertTrue(bhomFeatureMethods.searchAndCountSearchedDevices(stackNameSpace) >= 1, "Pre check failed: No device found with name ultron in BHOM.");

        log.info("---- Precheck 3: Verify OTel topology devices are present in Discovery ----------");
        Map<String, Object> ciId = discovery.fetchOtelDeviceTypes(stackNameSpace, jwtToken);
        int countSpan = discovery.countDevicesOfType(ciId, "OTel Span");
        int countHost = discovery.countDevicesOfType(ciId, "OTel Host");
        int countService = discovery.countDevicesOfType(ciId, "OTel Service");
        int countNamespace = discovery.countDevicesOfType(ciId, "OTel Service Namespace");
        log.info("Topology count for OTel Span: " + countSpan);
        log.info("Topology count for OTel Host: " + countHost);
        log.info("Topology count for OTel Service: " + countService);
        log.info("Topology count for OTel Service Namespace: " + countNamespace);
        Assert.assertTrue((countSpan > 0) || (countHost>0) || (countService>0) || (countNamespace>0), "No OTel topology devices found in Discovery.");

        log.info("---- Enable default Otel blueprint.  ----------");
        aiOpsFeatureMethods.enableBlueprint(blueprintName, accessKey, secreteKey);

        try{
            log.info("---- Create a service using this default Otel blueprint by passing namespace value.  ----------");
            JsonNode output = aiOpsFeatureMethods.createBlankServiceFromGivenBlueprint(serviceName, blueprintName, stackNameSpace, accessKey, secreteKey);
            log.info("Service created with ID: " + output.path("ids").path("$$$$${ko}").asText());

            log.info("---- Create OTel RED Alarm Policy ----------");
            policyId = aiOpsFeatureMethods.create_alarm_policy_from_json(OtelREDAlarmJsonPath, alarmPolicyName);

            log.info("---- Wait until 2+2=4 min for the Otel service to get impacted and situation to get generated. Atleast 2 events should get generated for a situation to generate.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(5))) {
                log.info("Otel Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. Check if Otel data collection is working fine.");
            }

            Map<String, String> healthScore = new Aiops().getHealthScore(serviceName, jwtToken);
            log.info("Waiting for situation creation until max 3 min...");
            if (!new Aiops().waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }
            log.info("Waiting additional time for situations to be fully populated with Incident ID's...");
            commonWaits.waitForSometime(commonWaits.moreDelay);

            log.info("---- Verify situation is created for Otel service with incident id ----------");
            JSONObject situationObject = new Aiops().getSituationsForService(serviceName, jwtToken);
            JSONArray eventsArray = situationObject.getJSONArray("eventList");
            int eventCount = eventsArray.length();
            Assert.assertTrue(eventCount >= 1, "No situations were seen created for Otel service: " + serviceName);
            log.info("Number of Situations seen created: " + eventCount);

            List<String> incidentIds = new ArrayList<>();
            for (int j = 0; j < eventsArray.length(); j++) {
                JSONObject eventObj = eventsArray.getJSONObject(j);
                if (eventObj.has("incident_id")) {
                    String incId = eventObj.getString("incident_id").trim();
                    if (!incId.isEmpty()) {
                        incidentIds.add(incId);
                    }
                }
            }
            Assert.assertTrue(incidentIds.size() >= 1, "No incident IDs found in the situations for Otel service: " + serviceName);
            log.info("Incident IDs: " + incidentIds);

            log.info("---- Test Completed ----------");
        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service and Alarm policy--------");
            try{
                aiOpsFeatureMethods.deleteService(serviceName);
                }
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            aiOpsFeatureMethods.delete_alarm_policy(policyId);
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "Data Injection via OpenTelemetry from ITSM Cluster into BMC Helix (Manual Blueprint E2E)",groups={"otel"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-15287",Products = {Product.OTEL, Product.AiOPS, Product.Operations_Management, Product.Helix_Discovery})
    public void DRSQA_15287_Data_Injection_Via_Otel_From_ITSM_Manual_Blueprint(ITestContext context) throws Exception {

        String blueprintName = "Manual Blueprint for OTel Service "+ RandomStringUtils.randomAlphanumeric(5);
        String serviceName = "Otel_Service_"+ RandomStringUtils.randomAlphanumeric(5);
        String alarmPolicyName = "Otel_RED_Alarm_Policy"+ RandomStringUtils.randomAlphanumeric(5);
        String OtelREDAlarmJsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Otel" + File.separator + "Otel_Red_Alarm_Policy.json";
        String OtelBlueprintDefJsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Otel" + File.separator + "Otel_manual_blueprint_def.json";

        String jwtToken = new Events_API().getJwtToken(accessKey, secreteKey);

        log.info("---- Precheck 1: Verify Otel data is loading in Otel tile: First go to Otel tile. Check if data is loaded in the panel. ALso fetch namespace value ----------");
        portalFeatureMethods.crossLaunchProduct("ADE Helix Telemetry");
        helixDashboardFeatureMethods.refreshDashboard();
        String stackNameSpace = helixDashboardFeatureMethods.getOtelNameSpaceValue();
        log.info("Fetched Otel Namespace value: " + stackNameSpace);
        Assert.assertTrue(stackNameSpace != null && !stackNameSpace.isEmpty(), "Pre check failed: Otel Namespace value in Dashboard filter is empty.");
        Assert.assertTrue(helixDashboardFeatureMethods.isLatencyPanelDataPresent("Latency Trend"), "Pre check failed: No data present in Latency panel for Otel dashboard.");

        log.info("---- Precheck 2: Go to BHOM & search for devices. Search based on namespace & see that device is listed  ----------");
        portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        Assert.assertTrue(bhomFeatureMethods.searchAndCountSearchedDevices(stackNameSpace) >= 1, "Pre check failed: No device found with name ultron in BHOM.");

        log.info("---- Precheck 3: Verify OTel topology devices are present in Discovery ----------");
        Map<String, Object> ciId = discovery.fetchOtelDeviceTypes(stackNameSpace, jwtToken);
        int countSpan = discovery.countDevicesOfType(ciId, "OTel Span");
        int countHost = discovery.countDevicesOfType(ciId, "OTel Host");
        int countService = discovery.countDevicesOfType(ciId, "OTel Service");
        int countNamespace = discovery.countDevicesOfType(ciId, "OTel Service Namespace");
        log.info("Topology count for OTel Span: " + countSpan);
        log.info("Topology count for OTel Host: " + countHost);
        log.info("Topology count for OTel Service: " + countService);
        log.info("Topology count for OTel Service Namespace: " + countNamespace);
        Assert.assertTrue((countSpan > 0) || (countHost>0) || (countService>0) || (countNamespace>0), "No OTel topology devices found in Discovery.");

        log.info("---- Create manual Otel blueprint.  ----------");
        String blueprintId = aiOpsFeatureMethods.createBlueprint(blueprintName, OtelBlueprintDefJsonPath, accessKey, secreteKey);

        try{

            log.info("---- Create a service using this default Otel blueprint by passing namespace value.  ----------");
            JsonNode output = aiOpsFeatureMethods.createBlankServiceFromGivenBlueprint(serviceName, blueprintName, stackNameSpace, accessKey, secreteKey);
            log.info("Service created with ID: " + output.path("ids").path("$$$$${ko}").asText());

            log.info("---- Create OTel RED Alarm Policy ----------");
            policyId = aiOpsFeatureMethods.create_alarm_policy_from_json(OtelREDAlarmJsonPath, alarmPolicyName);

            log.info("---- Wait until 3 min for the Otel service to get impacted. Atleast 2 events should get generated for a situation to generate.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(4))) {
                log.info("Otel Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. Check if Otel data collection is working fine.");
            }

            log.info("Waiting for situation creation...");
            Map<String, String> healthScore = new Aiops().getHealthScore(serviceName, jwtToken);
            if (!new Aiops().waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }

            log.info("Waiting additional time for situations to be fully populated with Incident ID's...");
            commonWaits.waitForSometime(commonWaits.minDelay);
            log.info("---- Verify situation is created for Otel service with incident id ----------");
            JSONObject situationObject = new Aiops().getSituationsForService(serviceName, jwtToken);
            JSONArray eventsArray = situationObject.getJSONArray("eventList");
            int eventCount = eventsArray.length();
            Assert.assertTrue(eventCount >= 1, "No situations were seen created for Otel service: " + serviceName);
            log.info("Number of Situations seen created: " + eventCount);

            List<String> incidentIds = new ArrayList<>();
            for (int j = 0; j < eventsArray.length(); j++) {
                JSONObject eventObj = eventsArray.getJSONObject(j);
                if (eventObj.has("incident_id")) {
                    String incId = eventObj.getString("incident_id").trim();
                    if (!incId.isEmpty()) {
                        incidentIds.add(incId);
                    }
                }
            }
            Assert.assertTrue(incidentIds.size() >= 1, "No incident IDs found in the situations for Otel service: " + serviceName);

            log.info("Incident IDs: " + incidentIds);
            log.info("---- Test Completed ----------");
        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service and Alarm policy--------");
            try{
                aiOpsFeatureMethods.deleteService(serviceName);
            }
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            aiOpsFeatureMethods.deleteBlueprint(blueprintId, accessKey, secreteKey);
            aiOpsFeatureMethods.delete_alarm_policy(policyId);
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "OTel-Driven K8S Data Injection in BMC Helix | Default Blueprint E2E",groups={"otel"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-14921",Products = {Product.OTEL, Product.AiOPS, Product.Operations_Management, Product.Helix_Discovery})
    public void DRSQA_14921_K8s_Data_Injection_Via_Otel_In_Helix_Default_Blueprint(ITestContext context) throws Exception {

        String blueprintName = "Default Blueprint for OTel Service";
        String serviceName = "Otel_Service_"+ RandomStringUtils.randomAlphanumeric(5);
        String alarmPolicyName = "Otel_RED_Alarm_Policy"+ RandomStringUtils.randomAlphanumeric(5);
        String OtelREDAlarmJsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Otel" + File.separator + "Otel_Red_Alarm_Policy.json";
        String jwtToken = new Events_API().getJwtToken(accessKey, secreteKey);

        log.info("---- Precheck 1: Verify Otel data is loading in Otel tile: First go to Otel tile. Check if data is loaded in the panel. ALso fetch namespace value ----------");
        portalFeatureMethods.crossLaunchProduct("ADE Helix Telemetry");
        helixDashboardFeatureMethods.refreshDashboard();
        String stackNameSpace = helixDashboardFeatureMethods.getOtelNameSpaceValue();
        log.info("Fetched Otel Namespace value: " + stackNameSpace);
        Assert.assertTrue(stackNameSpace != null && !stackNameSpace.isEmpty(), "Pre check failed: Otel Namespace value in Dashboard filter is empty.");
        Assert.assertTrue(helixDashboardFeatureMethods.isLatencyPanelDataPresent("Latency Trend"), "Pre check failed: No data present in Latency panel for Otel dashboard.");

        log.info("---- Precheck 2: Go to BHOM & search for devices. Search based on namespace & see that device is listed  ----------");
        portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        Assert.assertTrue(bhomFeatureMethods.searchAndCountSearchedDevices(stackNameSpace) >= 1, "Pre check failed: No device found with name ultron in BHOM.");

        log.info("---- Precheck 3: Verify OTel topology devices are present in Discovery ----------");
        Map<String, Object> ciId = discovery.fetchOtelDeviceTypes(stackNameSpace, jwtToken);
        int countSpan = discovery.countDevicesOfType(ciId, "OTel Span");
        int countHost = discovery.countDevicesOfType(ciId, "OTel Host");
        int countService = discovery.countDevicesOfType(ciId, "OTel Service");
        int countNamespace = discovery.countDevicesOfType(ciId, "OTel Service Namespace");
        log.info("Topology count for OTel Span: " + countSpan);
        log.info("Topology count for OTel Host: " + countHost);
        log.info("Topology count for OTel Service: " + countService);
        log.info("Topology count for OTel Service Namespace: " + countNamespace);
        Assert.assertTrue((countSpan > 0) || (countHost>0) || (countService>0) || (countNamespace>0), "No OTel topology devices found in Discovery.");

        log.info("---- Enable default Otel blueprint.  ----------");
        aiOpsFeatureMethods.enableBlueprint(blueprintName, accessKey, secreteKey);

        try{
            log.info("---- Create a service using this default Otel blueprint by passing namespace value.  ----------");
            JsonNode output = aiOpsFeatureMethods.createBlankServiceFromGivenBlueprint(serviceName, blueprintName, stackNameSpace, accessKey, secreteKey);
            log.info("Service created with ID: " + output.path("ids").path("$$$$${ko}").asText());

            log.info("---- Create OTel RED Alarm Policy ----------");
            policyId = aiOpsFeatureMethods.create_alarm_policy_from_json(OtelREDAlarmJsonPath, alarmPolicyName);

            log.info("---- Wait until 2+2=4 min for the Otel service to get impacted and situation to get generated. Atleast 2 events should get generated for a situation to generate.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(4))) {
                log.info("Otel Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. Check if Otel data collection is working fine.");
            }

            Map<String, String> healthScore = new Aiops().getHealthScore(serviceName, jwtToken);
            log.info("Waiting for situation creation...");
            if (!new Aiops().waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }
            log.info("Waiting additional time for situations to be fully populated with Incident ID's...");
            commonWaits.waitForSometime(commonWaits.minDelay);

            log.info("---- Verify situation is created for Otel service with incident id ----------");
            JSONObject situationObject = new Aiops().getSituationsForService(serviceName, jwtToken);
            JSONArray eventsArray = situationObject.getJSONArray("eventList");
            int eventCount = eventsArray.length();
            Assert.assertTrue(eventCount >= 1, "No situations were seen created for Otel service: " + serviceName);
            log.info("Number of Situations seen created: " + eventCount);

            List<String> incidentIds = new ArrayList<>();
            for (int j = 0; j < eventsArray.length(); j++) {
                JSONObject eventObj = eventsArray.getJSONObject(j);
                if (eventObj.has("incident_id")) {
                    String incId = eventObj.getString("incident_id").trim();
                    if (!incId.isEmpty()) {
                        incidentIds.add(incId);
                    }
                }
            }
            Assert.assertTrue(incidentIds.size() >= 1, "No incident IDs found in the situations for Otel service: " + serviceName);
            log.info("Incident IDs: " + incidentIds);

            log.info("---- Test Completed ----------");
        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service and Alarm policy--------");
            try{
                aiOpsFeatureMethods.deleteService(serviceName);
            }
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            aiOpsFeatureMethods.delete_alarm_policy(policyId);
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "OTel-Driven K8S Data Injection in BMC Helix Using Manual Blueprint (E2E)",groups={"otel"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-15257",Products = {Product.OTEL, Product.AiOPS, Product.Operations_Management, Product.Helix_Discovery})
    public void DRSQA_15257_K8s_Data_Injection_Via_Otel_IN_Helix_Manual_Blueprint(ITestContext context) throws Exception {

        String blueprintName = "Manual Blueprint for OTel Service "+ RandomStringUtils.randomAlphanumeric(5);
        String serviceName = "Otel_Service_"+ RandomStringUtils.randomAlphanumeric(5);
        String alarmPolicyName = "Otel_RED_Alarm_Policy"+ RandomStringUtils.randomAlphanumeric(5);
        String OtelREDAlarmJsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Otel" + File.separator + "Otel_Red_Alarm_Policy.json";
        String OtelBlueprintDefJsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Otel" + File.separator + "Otel_manual_blueprint_def.json";

        String jwtToken = new Events_API().getJwtToken(accessKey, secreteKey);

        log.info("---- Precheck 1: Verify Otel data is loading in Otel tile: First go to Otel tile. Check if data is loaded in the panel. ALso fetch namespace value ----------");
        portalFeatureMethods.crossLaunchProduct("ADE Helix Telemetry");
        helixDashboardFeatureMethods.refreshDashboard();
        String stackNameSpace = helixDashboardFeatureMethods.getOtelNameSpaceValue();
        log.info("Fetched Otel Namespace value: " + stackNameSpace);
        Assert.assertTrue(stackNameSpace != null && !stackNameSpace.isEmpty(), "Pre check failed: Otel Namespace value in Dashboard filter is empty.");
        Assert.assertTrue(helixDashboardFeatureMethods.isLatencyPanelDataPresent("Latency Trend"), "Pre check failed: No data present in Latency panel for Otel dashboard.");

        log.info("---- Precheck 2: Go to BHOM & search for devices. Search based on namespace & see that device is listed  ----------");
        portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        Assert.assertTrue(bhomFeatureMethods.searchAndCountSearchedDevices(stackNameSpace) >= 1, "Pre check failed: No device found with name ultron in BHOM.");

        log.info("---- Precheck 3: Verify OTel topology devices are present in Discovery ----------");
        Map<String, Object> ciId = discovery.fetchOtelDeviceTypes(stackNameSpace, jwtToken);
        int countSpan = discovery.countDevicesOfType(ciId, "OTel Span");
        int countHost = discovery.countDevicesOfType(ciId, "OTel Host");
        int countService = discovery.countDevicesOfType(ciId, "OTel Service");
        int countNamespace = discovery.countDevicesOfType(ciId, "OTel Service Namespace");
        log.info("Topology count for OTel Span: " + countSpan);
        log.info("Topology count for OTel Host: " + countHost);
        log.info("Topology count for OTel Service: " + countService);
        log.info("Topology count for OTel Service Namespace: " + countNamespace);
        Assert.assertTrue((countSpan > 0) || (countHost>0) || (countService>0) || (countNamespace>0), "No OTel topology devices found in Discovery.");

        log.info("---- Create manual Otel blueprint.  ----------");
        String blueprintId = aiOpsFeatureMethods.createBlueprint(blueprintName, OtelBlueprintDefJsonPath, accessKey, secreteKey);

        try{

            log.info("---- Create a service using this default Otel blueprint by passing namespace value.  ----------");
            JsonNode output = aiOpsFeatureMethods.createBlankServiceFromGivenBlueprint(serviceName, blueprintName, stackNameSpace, accessKey, secreteKey);
            log.info("Service created with ID: " + output.path("ids").path("$$$$${ko}").asText());

            log.info("---- Create OTel RED Alarm Policy ----------");
            policyId = aiOpsFeatureMethods.create_alarm_policy_from_json(OtelREDAlarmJsonPath, alarmPolicyName);

            log.info("---- Wait until 3 min for the Otel service to get impacted. Atleast 2 events should get generated for a situation to generate.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(4))) {
                log.info("Otel Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. Check if Otel data collection is working fine.");
            }

            log.info("Waiting for situation creation...");
            Map<String, String> healthScore = new Aiops().getHealthScore(serviceName, jwtToken);
            if (!new Aiops().waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }
            log.info("Waiting additional time for situations to be fully populated with Incident ID's...");
            commonWaits.waitForSometime(commonWaits.minDelay);

            log.info("---- Verify situation is created for Otel service with incident id ----------");
            JSONObject situationObject = new Aiops().getSituationsForService(serviceName, jwtToken);
            JSONArray eventsArray = situationObject.getJSONArray("eventList");
            int eventCount = eventsArray.length();
            Assert.assertTrue(eventCount >= 1, "No situations were seen created for Otel service: " + serviceName);
            log.info("Number of Situations seen created: " + eventCount);

            List<String> incidentIds = new ArrayList<>();
            for (int j = 0; j < eventsArray.length(); j++) {
                JSONObject eventObj = eventsArray.getJSONObject(j);
                if (eventObj.has("incident_id")) {
                    String incId = eventObj.getString("incident_id").trim();
                    if (!incId.isEmpty()) {
                        incidentIds.add(incId);
                    }
                }
            }
            Assert.assertTrue(incidentIds.size() >= 1, "No incident IDs found in the situations for Otel service: " + serviceName);

            log.info("Incident IDs: " + incidentIds);
            log.info("---- Test Completed ----------");
        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service and Alarm policy--------");
            try{
                aiOpsFeatureMethods.deleteService(serviceName);
            }
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            aiOpsFeatureMethods.deleteBlueprint(blueprintId, accessKey, secreteKey);
            aiOpsFeatureMethods.delete_alarm_policy(policyId);
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "Validate ITSM service Otel trace data in Helix Dashboard",groups={"otel"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-11760",Products = {Product.OTEL, Product.AiOPS, Product.Operations_Management, Product.Helix_Discovery})
    public void DRSQA_11760_validate_itsm_otel_data_in_dashboard(ITestContext context) throws Exception {


        log.info("---- Step 1: Verify Otel data is loading in Otel tile: First go to Otel tile. Check if data is loaded in the panel. ALso fetch namespace value ----------");
        portalFeatureMethods.crossLaunchProduct("ADE Helix Telemetry");
        helixDashboardFeatureMethods.refreshDashboard();
        String stackNameSpace = helixDashboardFeatureMethods.getOtelNameSpaceValue();
        log.info("Fetched Otel Namespace value: " + stackNameSpace);
        Assert.assertTrue(stackNameSpace != null && !stackNameSpace.isEmpty(), "Pre check failed: Otel Namespace value in Dashboard filter is empty.");
        Assert.assertTrue(helixDashboardFeatureMethods.isLatencyPanelDataPresent("Latency Trend"), "Pre check failed: No data present in Latency panel for Otel dashboard.");

        log.info("---- Step 2: Go to Dashboard and fetch all available business services ----------");
        List<String> services = helixDashboardFeatureMethods.getAllBusinessServices();

        for (String svc : services) {
            log.info("Selecting Business Service: " + svc);
                helixDashboardFeatureMethods.selectBusinessService(svc);
                helixDashboardFeatureMethods.refreshDashboard();
                dashboardFeatureMethods.verifyDashboardHasNoErrors();
                log.info("Selected Business Service: " + svc);
                break;
        }

    }

    @Test(description = "OTEL : E2E validation with ITSM instrumentation data",groups={"otel"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12471",Products = {Product.OTEL, Product.AiOPS, Product.Operations_Management, Product.Helix_Discovery})
    public void DRSQA_12471_OTEL_E2E_validation_with_ITSM_Instrumentation_Data(ITestContext context) throws Exception {

        String blueprintName = "Default Blueprint for OTel Service";
        String serviceName = "Otel_Service_"+ RandomStringUtils.randomAlphanumeric(5);
        String alarmPolicyName = "Otel_RED_Alarm_Policy"+ RandomStringUtils.randomAlphanumeric(5);
        String OtelREDAlarmJsonPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "Otel" + File.separator + "Otel_Red_Alarm_Policy.json";
        String jwtToken = new Events_API().getJwtToken(accessKey, secreteKey);

        log.info("---- Precheck 1: Verify Otel data is loading in Otel tile: First go to Otel tile. Check if data is loaded in the panel. ALso fetch namespace value ----------");
        portalFeatureMethods.crossLaunchProduct("ADE Helix Telemetry");
        helixDashboardFeatureMethods.refreshDashboard();
        String stackNameSpace = helixDashboardFeatureMethods.getOtelNameSpaceValue();
        log.info("Fetched Otel Namespace value: " + stackNameSpace);
        Assert.assertTrue(stackNameSpace != null && !stackNameSpace.isEmpty(), "Pre check failed: Otel Namespace value in Dashboard filter is empty.");
        Assert.assertTrue(helixDashboardFeatureMethods.isLatencyPanelDataPresent("Latency Trend"), "Pre check failed: No data present in Latency panel for Otel dashboard.");

        log.info("---- Precheck 2: Go to BHOM & search for devices. Search based on namespace & see that device is listed  ----------");
        portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        Assert.assertTrue(bhomFeatureMethods.searchAndCountSearchedDevices(stackNameSpace) >= 1, "Pre check failed: No device found with name ultron in BHOM.");

        log.info("---- Enable default Otel blueprint.  ----------");
        aiOpsFeatureMethods.enableBlueprint(blueprintName, accessKey, secreteKey);

        try{
            log.info("---- Create a service using this default Otel blueprint by passing namespace value.  ----------");
            JsonNode output = aiOpsFeatureMethods.createBlankServiceFromGivenBlueprint(serviceName, blueprintName, stackNameSpace, accessKey, secreteKey);
            log.info("Service created with ID: " + output.path("ids").path("$$$$${ko}").asText());

            log.info("---- Create OTel RED Alarm Policy ----------");
            policyId = aiOpsFeatureMethods.create_alarm_policy_from_json(OtelREDAlarmJsonPath, alarmPolicyName);

            log.info("---- Wait until 2+2=4 min for the Otel service to get impacted and situation to get generated. Atleast 2 events should get generated for a situation to generate.  ----------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(serviceName, 100, Duration.ofMinutes(4))) {
                log.info("Otel Service health score is impacted and below 100.");
            } else {
                throw new AssertionError("Service health score is still not impacted. Check if Otel data collection is working fine.");
            }

            Map<String, String> healthScore = new Aiops().getHealthScore(serviceName, jwtToken);
            log.info("Waiting for situation creation until max 3 min...");
            if (!new Aiops().waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
                Assert.fail("Situation did not get created within 3 minutes.");
            }
            log.info("Waiting additional time for situations to be fully populated with Incident ID's...");
            commonWaits.waitForSometime(commonWaits.moreDelay);

            log.info("---- Verify situation is created for Otel service with incident id ----------");
            JSONObject situationObject = new Aiops().getSituationsForService(serviceName, jwtToken);
            JSONArray eventsArray = situationObject.getJSONArray("eventList");
            int eventCount = eventsArray.length();
            Assert.assertTrue(eventCount >= 1, "No situations were seen created for Otel service: " + serviceName);
            log.info("Number of Situations seen created: " + eventCount);

            List<String> incidentIds = new ArrayList<>();
            for (int j = 0; j < eventsArray.length(); j++) {
                JSONObject eventObj = eventsArray.getJSONObject(j);
                if (eventObj.has("incident_id")) {
                    String incId = eventObj.getString("incident_id").trim();
                    if (!incId.isEmpty()) {
                        incidentIds.add(incId);
                    }
                }
            }
            Assert.assertTrue(incidentIds.size() >= 1, "No incident IDs found in the situations for Otel service: " + serviceName);
            log.info("Incident IDs: " + incidentIds);

            log.info("---- Test Completed ----------");
        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service and Alarm policy--------");
            try{
                aiOpsFeatureMethods.deleteService(serviceName);
            }
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            aiOpsFeatureMethods.delete_alarm_policy(policyId);
            portalFeatureMethods.closeDriver();
        }
    }


    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {

        if (portalFeatureMethods != null) {
            portalFeatureMethods.closeBrowser();
        }

        if (iiFeatureMethods != null) {
            iiFeatureMethods.closeBrowser();
        }

        if (bhomFeatureMethods != null) {
            bhomFeatureMethods.closeBrowser();
        }

        if (helixDashboardFeatureMethods != null) {
            helixDashboardFeatureMethods.closeBrowser();
        }

        if (aiOpsFeatureMethods != null) {
            aiOpsFeatureMethods.closeBrowser();
        }
    }

}