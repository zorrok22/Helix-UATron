package ITOM.Multiservice;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Multiservice.ServiceMonitoringFeatureMethods;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import util.*;

public class MultiService_Situations extends Launcher {
    public static SolqaLogger log = new SolqaLogger(MultiService_Situations.class.getName());
    WebDriver adeDriver;
    CommonWaits commonWaits;
    ServiceMonitoringPage smPage;
    ServiceMonitoringFeatureMethods serviceMonitoringFeatureMethods;
    Portal_FeatureMethods portalFeatureMethods;
    ReadJson readJson;
    String multiServiceTestDataPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITOM + File.separator + "AIOPS" + File.separator + "MultiServiceTestData.json";
    
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        readJson = new ReadJson();
        commonWaits = new CommonWaits();
        serviceMonitoringFeatureMethods = new ServiceMonitoringFeatureMethods();
        portalFeatureMethods = new Portal_FeatureMethods();
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for Service model ( Hardware Container & Host)", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-13486", Products = {Product.ADE_Platform, Product.AiOPS})
    public void DRSQA_13486_Multi_Service_Situation_Use_case_for_Service_model_Hardware_Container_and_Host(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_13486_WebDriver", getDriver());
        context.setAttribute("DRSQA_13486_Product", Product.ADE_Platform);
        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_13486_TestData");
        String situationName = "Multi-Service - DRSQA_13486 Events";
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify Multi-service situation created");
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for Service model. (Host and VM )", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12268", Products = {Product.ADE_Platform, Product.AiOPS})
    public void DRSQA_12268_Multi_Service_Situation_Use_case_for_Service_model_Host_and_VM(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_12268_WebDriver", getDriver());
        context.setAttribute("DRSQA_12268_Product", Product.ADE_Platform);
        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_12268_TestData");
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify Multi-service situation created");
        String situationName = "Multi-Service - DRSQA_12268 Events";
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for Service model. (Network Device and Host)", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12270", Products = {Product.ADE_Platform, Product.AiOPS})
    public void DRSQA_12270_Multi_Service_Situation_Use_case_for_Service_model_Network_Device_and_Host(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_12270_WebDriver", getDriver());
        context.setAttribute("DRSQA_12270_Product", Product.ADE_Platform);
        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_12270_TestData");
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify Multi-service situation created");
        String situationName = "Multi-Service - DRSQA_12270 Events";
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for service model. (Host and Deployment)", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12271", Products = {Product.ADE_Platform, Product.AiOPS})
    public void DRSQA_12271_Multi_Service_Situation_Use_case_for_Service_model_Host_and_Deployment(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_12271_WebDriver", getDriver());
        context.setAttribute("DRSQA_12271_Product", Product.ADE_Platform);

        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_12271_TestData");
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify Multi-service situation created");
        String situationName = "Multi-Service - DRSQA_12271 Events";
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for Service model. (Hardware Container and Storage)", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12272", Products = {Product.ADE_Platform, Product.AiOPS})
    public void DRSQA_12272_Multi_Service_Situation_Use_case_for_Service_model_Hardware_Container_and_Storage(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_12272_WebDriver", getDriver());
        context.setAttribute("DRSQA_12272_Product", Product.ADE_Platform);
        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_12272_TestData");
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify Multi-service situation created");
        String situationName = "Multi-Service - DRSQA_12272 Events";
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for Service model. (Storage System and Storage Volume)", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12273", Products = {Product.ADE_Platform})
    public void DRSQA_12273_Multi_Service_Situation_Use_case_for_Service_model_Storage_System_and_Storage_Volume(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_12273_WebDriver", getDriver());
        context.setAttribute("DRSQA_12273_Product", Product.ADE_Platform);

        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_12273_TestData");
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify Multi-service situation created");
        String situationName = "Multi-Service - DRSQA_12273 Events";
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for Service model. (Storage System and Host))", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12274", Products = {Product.ADE_Platform, Product.AiOPS})
    public void DRSQA_12274_Multi_Service_Situation_Use_case_for_Service_model_Storage_System_and_Host(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_12274_WebDriver", getDriver());
        context.setAttribute("DRSQA_12274_Product", Product.ADE_Platform);
        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_12274_TestData");
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify Multi-service situation created");
        String situationName = "Multi-Service - DRSQA_12274 Events";
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @Test(singleThreaded = true, description = "Multi-Service Situation Use case for Service model. (Single Service Situation)", groups={"ITOM_BMCIT"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12269", Products = {Product.ADE_Platform, Product.AiOPS})
    public void DRSQA_12269_Multi_Service_Situation_Use_case_for_Service_model_Single_Service_Situation(ITestContext context) {

        log.info("Step 1 and Step 2 : Login to ADE portal and navigate to AiOps");
        context.setAttribute("DRSQA_12269_WebDriver", getDriver());
        context.setAttribute("DRSQA_12269_Product", Product.ADE_Platform);

        login(CommonMethods.readConfig("ADE_USERNAME"), CommonMethods.readConfig("ADE_PASSWORD"), SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        log.info("Step 2 : Cross launch to AiOps");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.MONITORING_TILE);

        log.info("Step 2 : navigate to Situations");
        serviceMonitoringFeatureMethods.navigateToSituations();

        log.info("Step 3: Inject Events in the stack using API.+");
        String JsonContent = readJson.readJsonInputArray(multiServiceTestDataPath, "DRSQA_12269_TestData");
        serviceMonitoringFeatureMethods.createSituationUsingAPI(JsonContent);

        log.info("verify situation created");
        String situationName = "isr-vcenter-snmp - DRSQA_12269 Events";
        try {
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Open");
            serviceMonitoringFeatureMethods.openSituation(situationName);
            serviceMonitoringFeatureMethods.verifySituationDetails();
        } finally {
            log.info("close the created situation");
            serviceMonitoringFeatureMethods.navigateToSituations();
            serviceMonitoringFeatureMethods.navigateToListView();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Open");
            serviceMonitoringFeatureMethods.searchSituation(situationName);
            serviceMonitoringFeatureMethods.closeSituation(situationName);
            serviceMonitoringFeatureMethods.refreshBrowser();
            serviceMonitoringFeatureMethods.removeExistingAdvancedFilters();
            serviceMonitoringFeatureMethods.setFilters("Status", "Closed");
            serviceMonitoringFeatureMethods.verifySituationStatus(situationName, "Closed");
        }
    }

    @AfterMethod
    public void closeBrowsers() {
        if (portalFeatureMethods != null) {
            portalFeatureMethods.closeBrowser();
        }
        if (serviceMonitoringFeatureMethods != null) {
            serviceMonitoringFeatureMethods.closeBrowser();
        }
    }
 }