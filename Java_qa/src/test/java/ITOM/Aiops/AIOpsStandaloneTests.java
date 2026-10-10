package ITOM.Aiops;

import ITOM.Intelligent_Automation.IntelligentAutomationTest;
import framework.Launcher;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import util.*;
import java.io.File;
import java.time.Duration;
import java.util.*;

import ai.onnxruntime.OrtException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pages.ITOM.Aiops.ADESituationHelixGPT;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Aiops.AIOpsStandaloneMethods;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.OpenAIQuery;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import org.json.JSONArray;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.AiopsSituation;
import pages.ITOM.Api.HMEvents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static pages.ITOM.Aiops.ADESituationHelixGPT.openSituationHelixGPT;

public class AIOpsStandaloneTests extends Launcher {
    public static SolqaLogger log = new SolqaLogger(AIOpsStandaloneTests.class.getName());
    WebDriver driver;
    String eventClass = "NAGIOS_EV1";
    String jwtToken = null;
    int no_of_event_per_node = 1;
    HMEvents hm_events = null;
    Aiops aiopsApiCore = null;
    SoftAssert softAssert = null;
    CommonWaits commonwait = null;
    String location = "Pune";
    String event_tag = "";
    CommonMethods commonmethod = new CommonMethods();
    private ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();

    @Test(description = "Verify standalone events alongside Situations in context of services in RCA view")
    @SolqaDetails(authorName = "Haris", JiraID = "18970", Products = {Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void DRSQA_18970_Verify_standalone_events_alongside_Situations_context_services_RCA_view(ITestContext context ) throws Exception {

        log.info("------- START: DRSQA-18970 Verify standalone events alongside Situations in context of services in RCA view ------------");
        hm_events = new HMEvents();
        aiopsApiCore = new Aiops();
        softAssert = new SoftAssert();
        commonwait = new CommonWaits();
        String modelName = "ML-TSOM";
        log.info("Impact the Service by sending single event");
        int testcase_number = 2;
        String mlSheetName = "mlcheck";
        
        jwtToken = commonmethod.refreshJwtToken(jwtToken);

        log.info("Validate stand-alone-events enabled");
        if (!aiopsApiCore.getTenantFeatureFlagStatus("stand-alone-events",jwtToken)){
            log.info("stand-alone-events is not enabled This TC cannot run");
            return;
        }


        log.info("CLose all open event on Nagios class");
        hm_events.CloseAllOpenEvent(jwtToken);
        hm_events.closeAllEventByClass(eventClass, jwtToken);

        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number,mlSheetName);
        
        log.info("Check Service Model Exist");
        String[] bussness_service = row_data.get(0).get("Business Name").toString().split((","));
        
        aiopsApiCore.searchAndGetServiceDetails(bussness_service[0],jwtToken);
        
        log.info("Get init service health score");
        List<Map<String, String>> businessHealthList = new ArrayList<>();
        for(String service_name: bussness_service){
            Map<String, String> healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            aiopsApiCore.resetServiceHealthScore(healthScore.get("key"), jwtToken);
            commonwait.waitForSometime(5000);
            healthScore = aiopsApiCore.getHealthScore(service_name, jwtToken);
            log.info("Health Score for business service " + service_name + " Health Score " + healthScore.get("actualHealth"));
            businessHealthList.add(healthScore);
        }

        String[] ci_list = row_data.get(0).get("Impact Element").toString().split((","));
        String[] ci_priority = row_data.get(0).get("Impact Priority").toString().split((","));
        String[] ci_severity = row_data.get(0).get("Impact Severity").toString().split((","));
        String randomStr = RandomStringUtils.randomNumeric(7);
        String msg;
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        int no_of_event_per_node = (int)Double.parseDouble(row_data.get(0).get("No of Event per Node").toString());
        int total_rec = ci_list.length;
        for(int i =0;i< total_rec;i+= 1){
            msg = ci_list[i] + " Memory > 90% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, ci_list[i], msg, ci_severity[i], location, event_tag);
        }
        log.info("start timer to check how long it to take situation creation");
        log.info("Waiting for situation creation...");
        Map<String, String> healthScore = businessHealthList.get(0);
        for(int i =0;i<10;i++)
        {
            
            Map<String, String> bsHealthScore = aiopsApiCore.getHealthScore(bussness_service[0], jwtToken);
            log.info("Check if health score is change");
            if (Integer.parseInt(bsHealthScore.get("actualHealth")) < 100){
                break;
            }
        }
        log.info("Navigate to Servie and check Standalone");
        driver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        Launcher.login(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"), SolQAConstants.appITOM);
        setDriver(driver);
        AIOpsStandaloneMethods aiopsStandaloneMethods = new AIOpsStandaloneMethods();
        aiopsStandaloneMethods.waitforPlatformPageLoad();
        aiopsStandaloneMethods.setservice_url(healthScore.get("key"));
        if(!aiopsStandaloneMethods.navigateToServicesTab()){
            softAssert.fail("StandAlone Option not available");
        }

        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            softAssert.fail("Situation did not get created within 3 minutes.");
        }

        log.info("CLose all open event on Nagios class");
        hm_events.CloseAllOpenEvent(jwtToken);
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        softAssert.assertAll();

        
    }

}