package ITOM.Aiops;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import util.*;
import java.io.File;
import java.util.Map;

public class AIOpsTest extends Launcher {
    public static SolqaLogger log = new SolqaLogger(AIOpsTest.class.getName());
    WebDriver driver;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    String app = "ITOM";
    String serviceName;
    String cluster_CI = config.get("CLUSTER_CI");
    String PA_AGENT = config.get("PA_AGENT");
    String accessKey = SolQAConstants.API_ACCESS_KEY;
    String secreteKey = SolQAConstants.API_SECRET_KEY;
    ReadJson readJson = new ReadJson();
    Map<Object, String> aiopsTestData;
    CommonWaits commonWaits;
    IAFeatureMethod iaFeatureMethod;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    String ALARM_POLICY_CPU="Alarm_CPU_Auto";
    String ALARM_POLICY_Memory = "Alarm_Memory_Auto";
    String cpuPolicyId = "";
    String memoryPolicyId = "";

    public void doPreconditions() {
        driver = this.launchWeb(app, app);
        setDriver(driver);
        commonWaits=new CommonWaits();
        iaFeatureMethod = new IAFeatureMethod();
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        readJson =new ReadJson();
    }

    //Disabling this test for now, as Adarsh created its duplicate in other class
    @Test(description = "[NextGen]: Validate the impact on Service when health indicators are updated for a service. https://jira.example.com/browse/DRSQA-12454", groups = {"Regression", "PMDemo", "PreProd"},enabled=false)
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12454", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12454_Validate_impact_on_service_when_health_Indicators_are_Updated(ITestContext context) throws Exception{
        log.info("------- START: DRSQA_12454 [NextGen]: Validate the impact on Service when health indicators are updated for a service. ------------");
        doPreconditions();
        String CPUAlarmJsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + app +File.separator + "CPU_Alarm_Policy.json";
        String MemoryAlarmJsonPath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + app +File.separator + "Memory_Alarm_Policy.json";
        int score = 0;

        serviceName = commonWaits.generateRandomString();
        log.info("*** Service Name: " +serviceName);

        login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);

        try{
            log.info("Go to AiOPs. Create Service With a proper Patrol agent which has CPU & Memory metrics");
            aiOpsFeatureMethods.createGenNextSvcWithMetric(serviceName, PA_AGENT);

            log.info("Create Alarm Policies for CPU");
            cpuPolicyId = aiOpsFeatureMethods.create_alarm_policy_for_pa(CPUAlarmJsonPath, ALARM_POLICY_CPU, PA_AGENT);
            log.info("##### CPU Alarm policy ID: " + cpuPolicyId);

            log.info("Create Alarm Policies for Memory");
            memoryPolicyId = aiOpsFeatureMethods.create_alarm_policy_for_pa(MemoryAlarmJsonPath, ALARM_POLICY_Memory, PA_AGENT);
            log.info("##### Memory Alarm policy ID: " + memoryPolicyId);

            log.info("Go to services and check Health score for the service.");
            score = aiOpsFeatureMethods.openServiceAndcheckHealthScore(serviceName);
            log.info("Score should be 92. Actual: " +String.valueOf(score));
            Assert.assertTrue(score == 92, "Service is not seen impacted with expected score as 92. Actual Health Score is: " + String.valueOf(score));

            log.info("Add Health Indicator for Memory");
            aiOpsFeatureMethods.AddMemoryHealthIndicator(serviceName);

            log.info("Go to services and check Health score for the service.");
            score = aiOpsFeatureMethods.openServiceAndcheckHealthScore(serviceName);
            log.info("Score should be 84. Actual: " +String.valueOf(score));
            Assert.assertTrue(score == 84, "Service is not seen impacted with expected score as 84. Actual Health Score is: " + String.valueOf(score));

            log.info("Remove Health Indicator for Memory");
            aiOpsFeatureMethods.removeMemoryHealthIndicator(serviceName);

            log.info("Go to services and check Health score for the service.");
            score = aiOpsFeatureMethods.openServiceAndcheckHealthScore(serviceName);
            log.info("Score should be 92. Actual: " +String.valueOf(score));
            Assert.assertTrue(score == 92, "Service is not seen impacted with expected score as 92. Actual Health Score is: " + String.valueOf(score));

        }
        finally {
            log.info("Finally Go to BHOM and delete alarm policies.");
            aiOpsFeatureMethods.deleteService(serviceName);
            aiOpsFeatureMethods.delete_alarm_policy(cpuPolicyId);
            aiOpsFeatureMethods.delete_alarm_policy(memoryPolicyId);
            aiOpsFeatureMethods.closeBrowser();
        }

    }

    //test case moved to AiopsSituationEricssonTest

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        if (iaFeatureMethod != null) {
            iaFeatureMethod.closeBrowser();
        }
        if (aiOpsFeatureMethods != null) {
            aiOpsFeatureMethods.closeBrowser();
        }
    }
}
