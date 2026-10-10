package ITOM.Aiops;

import framework.Launcher;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.BlackoutPolicies;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Intelligent_Automation.IAChangeBlackoutConfiguration;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.*;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AiopsWithIAChangeBlackout extends Launcher {
    private static final SolqaLogger log = new SolqaLogger(AiopsWithIAChangeBlackout.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    CommonMethods commonmethod = new CommonMethods();
    private static final String app = "ITOM";
    WebDriver driver;
    private CommonWaits commonwait = new CommonWaits();
    private final String loganalytics_app_xpath = "//div[@id=\"BMC Helix Log Analytics\"]";
    private static final String serviceName = "ML-HW-AIX";
    private static final String[] ciList = {"qa-host-02.example.com","qa-host-03.example.com"};
    private static final String changeHost = "qa-host-02.example.com";
    private static final String eventClass = "NAGIOS_EV1";
    private static final String location = "Pune";
    HMEvents hmEvents = new HMEvents();
    Aiops aiopsApiCore = new Aiops();
    String jwtToken = null;
    AiopsDeepRCA aiopsDeepRCA = new AiopsDeepRCA();

    SoftAssert softAssert = new SoftAssert();

    public static boolean checkStatusEfficient(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        int commaIndex = input.indexOf(',');
        String status;

        if (commaIndex != -1) {
            // Comma found, get the substring before it
            status = input.substring(0, commaIndex);
        } else {
            // No comma found, the whole string is the status
            status = input;
        }

        return "ACTIVE".equalsIgnoreCase(status.trim());
    }

    @Test(description = "Aiops Validate IA Blackout event is not considered for Situtation and also does not list it under Node/Service", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "DRSQA-12466/12968", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12466_Aiops_Validate_IA_Blackout_event_is_not_considered_for_Situtation_and_also_does_not_list_it_under_Node_Service(ITestContext context) throws Exception {
        String cmdbUrl = config.get("CMDBURL");
        String username = config.get("ADE_USERNAME");
        String password = config.get("ADE_PASSWORD");
        AiopsSituation aiopsSituation = new AiopsSituation();
        boolean changeEnabled = false;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        log.info("Check Incident is enabled");
        if (!hmEvents.checkIncidentCreationEnabled(jwtToken)) {
            Assert.fail("Incident creation is not enabled This TC cannot run");
        }

        try{
            driver = this.launchWeb(app, app);
            log.info("Launched web driver for app: " + app);

            // Initialize wait and commonWaits

            // Login to the system
            login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
            log.info("Logged in to app: " + app);

            // Navigate to Log Analytics (AiOps page)
            commonwait.waitForElementPresent(loganalytics_app_xpath, 15);
            log.info("Verified Log Analytics app tile is present");

            IAChangeBlackoutConfiguration iachangeblackoutConfiguration = new IAChangeBlackoutConfiguration();
            log.info("Initialized IAChangeBlackoutConfiguration");

            if (!iachangeblackoutConfiguration.getChangeStatus()){
                log.info("Change configuration not enabled, enabling now");
                iachangeblackoutConfiguration.deleteChangeConfig();
                iachangeblackoutConfiguration.configureChangeBlackout();
                changeEnabled = true;
                log.info("Change configuration enabled");
            }



            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            log.info("Refreshed JWT token for test flow");

            BlackoutPolicies blackoutPolicies = new BlackoutPolicies();
            log.info("Initialized HMEvents, Aiops, and BlackoutPolicies clients");

            hmEvents.closeAllEventByClass("NAGIOS_EV1", jwtToken);
            aiopsSituation.CloseAllEventOnService(serviceName);
            log.info("Closed all events for class NAGIOS_EV1");



            RemedyApiClient remedyApiClient = new RemedyApiClient(cmdbUrl);
            String authToken = remedyApiClient.getRemedyApiKey(username, password);
            CMDBRestApi cmdbRestApi = new CMDBRestApi(cmdbUrl, authToken);
            log.info("Initialized Remedy and CMDB clients");

            String changeSummary = "IA blackout validation change for " + changeHost + " " + RandomStringUtils.insecure().nextNumeric(5);
            String changeDescription = "IA blackout validation description for " + changeHost;
            Map<String, String> changeIdentifiers = cmdbRestApi.createChangeRequestUpdateStatus(
                    changeSummary, changeDescription, serviceName, ciList, username, "US Support 3");
            if (changeIdentifiers == null) {
                Assert.fail("Failed to create and progress change request for " + changeHost);
                return;
            }
            if(!aiopsDeepRCA.moveChangeTorequestStatus(changeIdentifiers,"Implementation In Progress")){
                Assert.fail("Failed to progress change request for service " + serviceName);
            }
            log.info("Created change " + changeIdentifiers.get("infrachangeid"));
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            String serviceKey = healthScore.get("key");
            log.info("Fetched initial health score and service key for " + serviceName);

            boolean timeframeActive = false;
            for (int attempt = 0; attempt < 25 && !timeframeActive; attempt++) {
                jwtToken = commonmethod.refreshJwtToken(jwtToken);
                JSONArray policies = blackoutPolicies.searchBlackoutPolicy(changeIdentifiers.get("infrachangeid"), jwtToken);
                log.info("Blackout policy search attempt " + (attempt + 1));
                if (policies != null && policies.length() > 0) {
                    for (int i = 0; i < policies.length(); i++) {
                        JSONObject policy = policies.getJSONObject(i);
                        JSONObject configurations = policy.optJSONObject("configurations");
                        String timeframeStatus = configurations != null ? configurations.optString("timeframeStatus", "") : "";
                        if (checkStatusEfficient(timeframeStatus)) {
                            timeframeActive = true;
                            break;
                        }
                        log.info("Policy still not Active " + timeframeStatus);
                    }
                }
                if (!timeframeActive) {
                    Thread.sleep(15000);
                    log.info("Waiting before next policy check");
                }
            }
            if (!timeframeActive) {
                Assert.fail("Blackout timeframe did not become ACTIVE for change " + changeIdentifiers.get("infrachangeid"));
            }
            log.info("Blackout timeframe is ACTIVE for change " + changeIdentifiers.get("infrachangeid"));

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            String firstMsg = "IA blackout validation event 1 " + RandomStringUtils.insecure().nextNumeric(4);
            hmEvents.streamRestApiEvents(eventClass, 1, jwtToken, changeHost, firstMsg, "MAJOR", location, "");
            String secondMsg = "IA blackout validation event 2 " + RandomStringUtils.insecure().nextNumeric(4);
            hmEvents.streamRestApiEvents(eventClass, 1, jwtToken, changeHost, secondMsg, "MAJOR", location, "");
            log.info("Impacted service " + serviceName + " via events on host " + changeHost);

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            softAssert.assertEquals(Integer.parseInt(healthScore.get("health")), 100,
                    "Service health degraded after blackout events");
            log.info("Validated service health remains 100 after blackout events");

            boolean situationCreated = aiopsApiCore.waitForSituationCreation(serviceKey, jwtToken, 90000);
            softAssert.assertFalse(situationCreated, "Situation created even though blackout should suppress events");
            log.info("Verified no situation created during blackout");

            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            hmEvents.closeAllEventByClass(eventClass, jwtToken);
            aiopsSituation.CloseAllEventOnService(serviceName);
            log.info("Closed events after verification");

            aiopsApiCore.waitForHealthScoreExpectedValue(100, serviceName, jwtToken);
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
            healthScore = aiopsApiCore.getHealthScore(serviceName, jwtToken);
            if(Integer.parseInt(healthScore.get("health")) != 100){
                log.error("Service health is not 100 after cleanup");
                softAssert.fail("Service health is not 100 after cleanup");
            }


            ZonedDateTime actualEndTimeUtc = ZonedDateTime.now(ZoneOffset.UTC);
            ZonedDateTime actualStartTimeUtc = actualEndTimeUtc.minusMinutes(45);
            DateTimeFormatter remedyTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.000+0000'");
            cmdbRestApi.moveChangeStatusComplete(
                    changeIdentifiers.get("infrachangeid"),
                    changeIdentifiers.get("RequestID"),
                    actualStartTimeUtc.format(remedyTimeFormatter),
                    actualEndTimeUtc.format(remedyTimeFormatter));
            log.info("Moved change " + changeIdentifiers.get("infrachangeid") + " to Completed status");
        }
        finally {
            if(changeEnabled){
                IAChangeBlackoutConfiguration iachangeblackoutConfiguration = new IAChangeBlackoutConfiguration();
                iachangeblackoutConfiguration.disablechangeConfig();
                log.info("Disabled change configuration");
            }
            if (driver != null) {
                driver.quit();
                log.info("WebDriver quit");
            }
        }
        softAssert.assertAll();
    }
}
