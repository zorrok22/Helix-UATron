package ITOM.GrafanaDashboard;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.GrafanaDashboard.Dashboard_FeatureMethods;
import pages.ITOM.GrafanaDashboard.ReportingPage;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.File;

public class GrafanaUI_ADE extends Launcher {
    static SolqaLogger log = new SolqaLogger(GrafanaUI_ADE.class.getName());
    ReadJson readJson = new ReadJson();
    Map<Object, String> drsqa_12353_data, drsqa_12361_data, drsqa_12362_data, drsqa_12350_data, drsqa_12343_data, drsqa_12377_data, drsqa_12406_data, drsqa_12414_data, dashboardServiceMonitoringData, bhomUsageReportingData;
    Dashboard_FeatureMethods dashboardFeatureMethods;
    Portal_FeatureMethods portalFeatureMethods;
    ReportingPage reportingPage;
    DashboadFeatureMethods helixDashboardFeatureMethods;
    BWFFeatureMethods bwfFeatureMethods;
    List<String> expectedFolderNames;

    @Test(description = "[ADE Monitor, Dashboard Reporting] : Cross launch to Metric Dashboard from Helix Monitor Console",groups={"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12321", Products = {Product.Dashboards})
    public void DRSQA_12321_VerifyCrossLaunchFromADEMonitor(ITestContext context) throws IOException {

        try {
            log.info("[ADE Monitor, Dashboard Reporting] : Cross launch to Metric Dashboard from Helix Monitor Console Started.");
            log.info("Login to ADE and cross launch BMC Helix Operations Management");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12321");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Navigate to BMC Helix Operations Management ------");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            log.info("Go to Monitoring dropdown");
            dashboardFeatureMethods.toggle();
            log.info("Go to Monitoring Devices");
            dashboardFeatureMethods.monitoringDevices();
            dashboardFeatureMethods.metricsDashboardloaded();
            log.info("[ADE Monitor, Dashboard Reporting] : Cross launch to Metric Dashboard from Helix Monitor Console END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[ADE Monitor, Dashboard Reporting] : Cross launch to Event Dashboard from Helix Monitor Console",groups={"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12322", Products = {Product.Dashboards})
    public void DRSQA_12322_VerifyCrossLaunchEventDashboardFromADEMonitor(ITestContext context) throws IOException {

        try {
            log.info("[ADE Monitor, Dashboard Reporting] : Cross launch to Event Dashboard from Helix Monitor Console Started.");
            log.info("Login to ADE and cross launch BMC Helix Operations Management");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12322");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Navigate to BMC Helix Operations Management ------");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            log.info("Go to Monitoring dropdown");
            dashboardFeatureMethods.toggle();
            log.info("Go to Monitoring Events");
            dashboardFeatureMethods.monitoringEvents();
            dashboardFeatureMethods.EventDashboardloaded();
            log.info("[ADE Monitor, Dashboard Reporting] : Cross launch to Event Dashboard from Helix Monitor Console END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform]Verify OOTB Dashboard for 'Printable Incident Details'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12406", Products = {Product.Dashboards})
    public void DRSQA_12406_Printable_Incident_Details_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform]Verify OOTB Dashboard for 'Printable Incident Details'");
        String dashboardName = SolQAConstants.Printable_Incident_dashboard;

        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open Tabular KPI Dashboard");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12406");
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ------");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Opening Printable Incident Details dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixITSMfolderName);
            log.info("---- Verify All filters in Printable Incident Details dashboard ----");
            log.info("Select Last 90 days time range");
            dashboardFeatureMethods.selectLast90Days();
            dashboardFeatureMethods.verifyAllDashboardFilters();
            log.info("[Helix Dashboards],[Helix ITSM] : Verify OOTB Dashboard for 'Printable Incident Details END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [BWF] [Converged Platform]Verify OOTB Dashboard for 'Printable Case Summary Details'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12414", Products = {Product.Dashboards})
    public void DRSQA_12414_Printable_Case_Summary_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform]Verify OOTB Dashboard for 'Printable Case Details'");
        String dashboardName = SolQAConstants.Printable_Case_dashboard;

        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open Printable Case Details");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12414");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            log.info("----- Opening Printable Case Summary Details dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixBusinessWorkflowsFolder);
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();
            log.info("---- Verify All filters in Printable Case Summary Details dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();
            log.info("[Helix Dashboards],[Helix ITSM] : Verify OOTB Dashboard for 'Printable Case Summary' END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards]Verify AIOps Value dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-15301", Products = {Product.Dashboards})
    public void DRSQA_15301_Verify_AIOps_Value_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Aiops] Verify OOTB Dashboard for 'AIOps Value Dashboard'");
        String dashboardName = SolQAConstants.AIOps_Value_Dashboard;
        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open AIOps Value Dashboard");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15301");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            log.info("----- Opening AIOps Value Dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixServiceMonitoringFolder);
            log.info("Select Last 90 Days time range");
            dashboardFeatureMethods.selectLast90Days();
            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("---- Verify All filters inAIOps Value Dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();
            log.info("Verify Share Dashboard functionality");
            dashboardFeatureMethods.isShareDashboard();
            log.info("[Helix Dashboards] [Aiops] Verify OOTB Dashboard for 'AIOps Value Dashboard' END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "Verify Helix Automation Console Dashboard consumed BMC Helix datasource", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-15300", Products = {Product.Dashboards})
    public void DRSQA_15300_Verify_Helix_Automation_Console_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards] Verify Helix Automation Console Dashboard");
        String dashboardName = SolQAConstants.BMC_Helix_Automation_and_Compliance;

        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open Helix Automation Console Dashboard");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15300");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            log.info("----- Opening Helix Automation Console Dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixITSMfolderName);
            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("---- Verify All filters in Helix Automation Console Dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();
            log.info("Verify Share Dashboard functionality");
            dashboardFeatureMethods.isShareDashboard();
            log.info("[Helix Dashboards] : Verify Helix Automation Console Dashboard END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "Verify ITSM Health Check Dashboard", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-15298", Products = {Product.Dashboards})
    public void DRSQA_15298_Verify_ITSM_Health_Check_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards] Verify ITSM Health Check Dashboard");
        String dashboardName = SolQAConstants.ITSM_Health_Check_Dashboard;

        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open ITSM Health Check Dashboard");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15298");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            log.info("----- Opening Verify ITSM Health Check Dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixITSMfolderName);
            log.info("Select Last 90 Days time range");
            dashboardFeatureMethods.selectLast90Days();
            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("---- Verify All filters in ITSM Health Check Dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();
            log.info("Verify Share Dashboard functionality");
            dashboardFeatureMethods.isShareDashboard();
            log.info("[Helix Dashboards] : Verify Helix Automation Console Dashboard END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "Verify DWP Net Promoter Score Dashboard", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-15299", Products = {Product.Dashboards})
    public void DRSQA_15299_Verify_DWP_Net_Promoter_Score_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards] Verify DWP Net Promoter Score Dashboard");
        String dashboardName = SolQAConstants.DWP_Net_Promoter_Score_Report;

        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open DWP Net Promoter Score Dashboard");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15299");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to DWP Net Promoter Score Report Dashboard ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            log.info("----- Opening Verify DWP Net Promoter Score Report Dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixITSMfolderName);
            log.info("Select Last 90 Days time range");
            dashboardFeatureMethods.selectLast90Days();
            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("---- Verify All filters in DWP Net Promoter Score Report Dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();
            log.info("Verify Share Dashboard functionality");
            dashboardFeatureMethods.isShareDashboard();
            log.info("[Helix Dashboards] : Verify DWP Net Promoter Score Report Dashboard END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform]Verify OOTB Dashboard for 'Printable Change Details'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", TTID = "9750", JiraID = "DRSQA-12405", Products = {Product.Dashboards})
    public void DRSQA12405_Printable_Change_Details_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform]Verify OOTB Dashboard for 'Printable Change Details'");
        String dashboardName = SolQAConstants.Printable_Change_dashboard;
        SoftAssert softassert = new SoftAssert();

        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open Printable Change Details dashboard");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12405");
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ------");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("Opening Printable Change Details dashboard");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixITSMfolderName);
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();
            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("Verify All filters in Printable Change Details dashboard");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            String fileExtn = "pdf";
            String folderName = SolQAConstants.HelixITSMfolderName;
            String fileName = folderName + dashboardName + " Preview." + fileExtn;
            File downloadDir = new File(System.getProperty("user.home") + File.separator + "Downloads");
            File fileToDelete = new File(downloadDir, fileName);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }

            log.info("Export Dashboard as PDF");
            Assert.assertTrue(dashboardFeatureMethods.isExportDashboardAsFile("PDF"), "File download failed..");

            String expectedFileName = dashboardName + " Preview." + fileExtn;
            File downloadedFile = new File(downloadDir, expectedFileName);
            org.testng.Assert.assertTrue(downloadedFile.exists(), "File not found in download directory.");

            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
            softassert.assertAll();
            log.info("[Helix Dashboards] [Helix ITSM] : Verify OOTB Dashboard for 'Printable Change Details' END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    
    @Test(description = "Verify new Dashboard folder named - 'Service Monitoring' created OOTB", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15762", Products = {Product.Dashboards})
    public void DRSQA_15762_Service_Monitoring_Dashboard(ITestContext context) {

        log.info("Verify new Dashboard folder named - 'Service Monitoring' created OOTB");

        try {
            log.info("Login to Helix Portal with User (Admin)");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15762");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            helixDashboardFeatureMethods = new DashboadFeatureMethods();
            readJson = new ReadJson();
            dashboardServiceMonitoringData = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_15762_Data");
            expectedFolderNames = Arrays.asList(dashboardServiceMonitoringData.get("expectedFolderNames").split(","));
    		
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            log.info("----- Opening 'Service Monitoring' dashboard ----");
            dashboardFeatureMethods.openDashboard(SolQAConstants.HelixServiceMonitoringFolder, null);
            
            log.info("----- Verify Aiops related dashboards are present in the folder ----");
            helixDashboardFeatureMethods.verifyDashboardsInFolder(expectedFolderNames);
            
			
            log.info("Verify new Dashboard folder named - 'Service Monitoring' created OOTB END");

        } catch (AssertionError e) {
            throw new AssertionError("Failed to execute test case" + e);
        } catch (Exception e) {
            throw new AssertionError("Failed to execute test case" + e);
        }
    }

    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform]Verify OOTB Dashboard for 'Printable Asset Details'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", TTID = "9750", JiraID = "DRSQA-12411", Products = {Product.Dashboards})
    public void DRSQA12411_Printable_Asset_Details_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform]Verify OOTB Dashboard for 'Printable Asset Details'");
        String dashboardName = SolQAConstants.Printable_Asset_dashboard;
        //SoftAssert softassert = new SoftAssert();

        try {
            log.info("Login to Helix Portal with User (Admin) which is sync-ed from Converged platform and Open Printable Asset Details dashboard");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12411");
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Dashboards ------");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            log.info("Opening Printable Asset Details dashboard");
            dashboardFeatureMethods.openDashboard(dashboardName, SolQAConstants.HelixITSMfolderName);
            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("Verify All filters in Printable Asset Details dashboard");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            //softassert.assertAll();
            log.info("[Helix Dashboards] [Helix ITSM] : Verify OOTB Dashboard for 'Printable Asset Details' END.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] [Helix Optimize] Verify new query type Metric introduced for Optimize", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12361", Products = {Product.Dashboards})
    public void DRSQA12361_verify_metric_query_type_for_optimize(ITestContext context) throws IOException {

        log.info("[Helix Dashboards] [Helix Optimize] Verify new query type Metric introduced for Optimize");
        String dashboardName = "Custom_Metric_SolQA_" + System.currentTimeMillis();

        try {
            //Initialize test components
            log.info("Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12361");
            //Login to ADE Platform
            log.info("Login to Helix Dashboard using admin user");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            drsqa_12361_data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITOM + File.separator + SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12361_Data");

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());

            // STEP 4: Create new visualization dashboard and verify Metric query type Source list
            log.info("---- Create new visualization dashboard and verify Metric query type Source list ----");
            helixDashboardFeatureMethods.openMenu(drsqa_12361_data.get("menuItem"));
            helixDashboardFeatureMethods.createNewVisualizationDashboard(dashboardName);
            helixDashboardFeatureMethods.enterQueryDetails("Data source", drsqa_12361_data.get("dataSource"));
            helixDashboardFeatureMethods.enterQueryDetails("Query type", drsqa_12361_data.get("queryType"));
            helixDashboardFeatureMethods.verifySourceOptionPresent(drsqa_12361_data.get("sourceOption"));
            log.info("[Helix Dashboards] [Helix Optimize] Metric query type validation completed.");
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12361 - Verify new query type Metric introduced for Optimize verified successfully");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] [Helix Chatbot] [Converged Platform] Verify OOTB dashboard for 'Chatbot Overview'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12382", Products = {Product.Dashboards})
    public void DRSQA12382_verify_chatbot_overview_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix Chatbot] [Converged Platform] Verify OOTB dashboard for 'Chatbot Overview'");
        String dashboardName = SolQAConstants.Chatbot_Overview_Dashboard;
        String folderName = SolQAConstants.Chatbot_Dashboard_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12382 - Verify Chatbot Overview Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12382");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Chatbot Overview dashboard
            log.info("STEP 4: Opening 'Chatbot Overview' dashboard from 'Helix Chatbot' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Chatbot Overview dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);
            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();
            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the chatbot related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("Verify All filters in Chatbot Overview dashboard");
            dashboardFeatureMethods.verifyAllDashboardFilters();
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12382 - Chatbot Overview Dashboard verified successfully");
            log.info("All chatbot panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] [Helix Chatbot] [Converged Platform] Verify OOTB dashboard for 'Chatbot Dashboard'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12351", Products = {Product.Dashboards})
    public void DRSQA12351_verify_chatbot_dashboard_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix Chatbot] [Converged Platform] Verify OOTB dashboard for 'Chatbot Dashboard'");
        String dashboardName = SolQAConstants.Chatbot_Dashboard;
        String folderName = SolQAConstants.Chatbot_Dashboard_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12351 - Verify Chatbot Dashboards Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12351");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Chatbot Dashboard dashboard
            log.info("STEP 4: Opening 'Chatbot Dashboard' dashboard from 'Helix Chatbot' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Chatbot Dashboard dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);
            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();
            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the chatbot related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12351 - Chatbot Dashboard Dashboard verified successfully");
            log.info("All chatbot panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] Verify Event analysis by tags dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", TTID = "8709", JiraID = "DRSQA-12418", Products = {Product.Dashboards})
    public void DRSQA12418_Event_analysis_by_tags_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] Verify Event analysis by tags dashboard");
        String dashboardName = SolQAConstants.Event_analysis_by_tags;
        String folderName = SolQAConstants.BHOMTitle;
        SoftAssert softassert = new SoftAssert();

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12418 - Verify Event Analysis by Tags Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12418");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with Admin user");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Event analysis by tags dashboard
            log.info("STEP 4: Opening 'Event analysis by tags' dashboard from 'Operations Management' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Event analysis by tags dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify dashboard has no errors on any panels
            log.info("STEP 6: Verify dashboard has no errors on any panels");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify Tag keys filter is present and functional
            log.info("STEP 7: Verify all filters is present and functional");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            // Assert all soft assertions
            softassert.assertAll();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12418 - Event Analysis by Tags Dashboard verified successfully");
            log.info("Dashboard panels have no errors and Tag keys filter validated successfully");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboard] [Converge Platform] Helix dashboard should get cross launch from Innovation Studio Apps", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12387", Products = {Product.Dashboards})
    public void DRSQA12387_HelixDashboard_should_get_CrossLaunched_From_InnovationStudioApps(ITestContext context) throws IOException {
        String dashboardName = SolQAConstants.BWF_dashboard;

        try {
            log.info("[Helix Dashboard] [Converge Platform] Helix dashboard should get cross launch from Innovation Studio Apps");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12387");
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            log.info("---- Navigate to BMC Helix Business Workflows ------");
            portalFeatureMethods.crossLaunchProduct("BMC Helix Business Workflows");
            bwfFeatureMethods = new BWFFeatureMethods();
            bwfFeatureMethods.verifyLoginToBWF();
            log.info("---- Cross launch Helix Dashboards from BWF application launcher ----");
            bwfFeatureMethods.crossLaunchApplication(SolQAConstants.HELIX_DASHBOARD);

            CommonWaits commonWaits = new CommonWaits();
            commonWaits.switchToLastWindow();

            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);
            log.info("[Helix Dashboard] [Converge Platform] Helix dashboard cross launch verification completed.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards]Verify the Windows Servers Dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12393", Products = {Product.Dashboards})
    public void DRSQA12393_Verify_Windows_Server_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards]Verify the Windows Servers Dashboard");
        String dashboardName = SolQAConstants.Windows_Server_Metrics;
        String folderName = SolQAConstants.BHOMTitle;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12393 - Verify the Windows Servers Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12393");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Windows Servers Dashboard
            log.info("STEP 4: Opening 'Windows Servers' dashboard from 'Operations Management' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Windows Servers dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify Windows Servers dashboard has no errors in its panels
            log.info("STEP 6: Verify Windows Servers dashboard filter has no errors in its panels");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in Windows Servers dashboard
            log.info("STEP 7: Verify all filters present in Windows Servers dashboard");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("[Helix Dashboards]: Verify the Windows Servers Dashboard END.");
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12393 - Windows Servers Dashboard verified successfully");
            log.info("Windows Servers Dashboard, all panels have no errors, and all filters validated successfully");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }



    @Test(description = "[Helix Dashboards]Verify the Linux Servers Dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12394", Products = {Product.Dashboards})
    public void DRSQA12394_Verify_Linux_Server_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards]Verify the Linux Servers Dashboard");
        String dashboardName = SolQAConstants.Linux_Server_Metrics;
        String folderName = SolQAConstants.BHOMTitle;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12394 - Verify the Linux Servers Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12394");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Dashboard using admin user");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Linux Servers Dashboard
            log.info("---- Opening Linux Servers dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Linux Servers dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify Linux Servers dashboard has no errors in its panels
            log.info("STEP 6: Verify Linux Servers has no errors in its panels");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in Linux Servers dashboard
            log.info("STEP 7: Verify all filters present in Linux Servers dashboard");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("[Helix Dashboards]: Verify the Linux Servers Dashboard END.");
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12394 - Linux Servers Dashboard verified successfully");
            log.info("Linux Servers Dashboard, all panels have no errors, and all filters validated successfully");
            log.info("======================================================================");

        }catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards]Verify the Dynamic Metrics dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12392", Products = {Product.Dashboards})
    public void DRSQA12392_Verify_DynamicMetric_dashboard(ITestContext context) {

        log.info("[Helix Dashboards]Verify the Dynamic Metrics dashboard");
        String dashboardName = SolQAConstants.Dynamic_Metrics_Analysis;
        String folderName = SolQAConstants.BHOMTitle;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12392 - Verify Dynamic Metrics Analysis Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12392");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Dashboard using admin user");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Dynamic Metrics Analysis dashboard
            log.info("STEP 4: Opening 'Dynamic Metrics Analysis' dashboard from 'Operations Management' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Dynamic Metrics Analysis dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify dynamic Metric dashboard has no errors in its panels
            log.info("STEP 6: Verify dynamic Metric filter has no errors in its panels");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in dynamic Metric dashboard
            log.info("STEP 7: Verify all filters present in dynamic Metric dashboard");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("[Helix Dashboards]: Verify the Dynamic Metrics dashboard END.");

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12392 - Dynamic Metrics Analysis Dashboard verified successfully");
            log.info("Dynamic metrics loaded, all panels have no errors, and all filters validated successfully");
            log.info("======================================================================");

        }catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards]Verify the SQL metrics Dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12395", Products = {Product.Dashboards})
    public void DRSQA12395_Verify_SQL_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards]Verify the SQL Metrics Dashboard");
        String dashboardName = SolQAConstants.Microsoft_SQL_Database_Metrics;
        String folderName = SolQAConstants.BHOMTitle;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12395 - Verify the SQL Metrics Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12395");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Microsoft SQL Database Metrics dashboard
            log.info("---- Opening Microsoft SQL Database Metrics dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Microsoft SQL Database Metrics dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            log.info("Select Last 90 Days time range");
            dashboardFeatureMethods.selectLast90Days();

            // STEP 6: Verify Microsoft SQL Database Metrics dashboard has no errors in its panels
            log.info("---- Verify panels have no errors ----");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in Microsoft SQL Database Metrics dashboard
            log.info("---- Verify all filters in Microsoft SQL Database Metrics dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("[Helix Dashboards]: Verify the SQL Metrics Dashboard END.");
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12395 - SQL Metrics Dashboard verified successfully");
            log.info("SQL metrics loaded, all panels have no errors, and all filters validated successfully");
            log.info("======================================================================");

        }catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards]Verify the ORACLE metrics Dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12396", Products = {Product.Dashboards})
    public void DRSQA12396_Verify_the_ORACLE_metrics_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards]Verify the ORACLE Metrics Dashboard");
        String dashboardName = SolQAConstants.Oracle_Database_Metrics;
        String folderName = SolQAConstants.BHOMTitle;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12396 - Verify the ORACLE Metrics Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12396");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the ORACLE Database Metrics dashboard
            log.info("---- Opening ORACLE Database Metrics dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify ORACLE Metrics dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            log.info("Select Last 90 Days time range");
            dashboardFeatureMethods.selectLast90Days();

            // STEP 6: Verify ORACLE Database Metrics dashboard has no errors in its panels
            log.info("---- Verify panels have no errors ----");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in ORACLE Database Metrics dashboard
            log.info("---- Verify all filters in ORACLE Database Metrics dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("[Helix Dashboards]: Verify the ORACLE Metrics Dashboard END.");
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12396 - ORACLE Metrics Dashboard verified successfully");
            log.info("ORACLE metrics loaded, all panels have no errors, and all filters validated successfully");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards]Verify the Tabular KPI Report Dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12391", Products = {Product.Dashboards})
    public void DRSQA12391_Verify_TabularKPI_Report_dashboard(ITestContext context) {

        log.info("[Helix Dashboards]Verify the Tabular KPI Report Dashboard.");
        String dashboardName = SolQAConstants.Tabular_KPI_Report;
        String folderName = SolQAConstants.BHOMTitle;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-12391 - Verify the Tabular KPI Report Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12391");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("---- Login to Portal with correct username and password -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));


            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Tabular KPI Report Metrics dashboard
            log.info("---- Opening Tabular KPI Report dashboard ----");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Tabular KPI Report dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify ORACLE Database Metrics dashboard has no errors in its panels
            log.info("---- Verify Tabular KPI Report dashboard has no errors ----");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in ORACLE Database Metrics dashboard
            log.info("---- Verify all filters in Tabular KPI Report dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("[Helix Dashboards]: Verify the Tabular KPI Report Dashboard END.");
            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-12391 - Tabular KPI Report Dashboard verified successfully");
            log.info("Tabular KPI Report loaded, all panels have no errors, and all filters validated successfully");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform] User with default 'Reporting Viewer' permissions but without authorization to incidents logs in to ADE and runs 'Incident Dashboard'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12362", Products = {Product.Dashboards})
    public void DRSQA12362_verify_with_User_not_authorised_for_Incident_dashboard(ITestContext context) throws IOException {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform] User with default 'Reporting Viewer' permissions but without authorization to incidents logs in to ADE and tries to run 'Incident Dashboard'");
        String dashboardName = SolQAConstants.Incident_dashboard;
        String folderName = SolQAConstants.HelixITSMfolderName;
        SoftAssert softAssert = new SoftAssert();

        try {
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12362");
            drsqa_12362_data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "ppanUser");

            log.info("---- Login to Portal with Reporting Viewer user without incident permissions -----");
            portalFeatureMethods.loginToADEPlatform(drsqa_12362_data.get("UserName").trim(), drsqa_12362_data.get("Password"));

            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            log.info("Opening Incident Dashboard for unauthorized viewer");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            log.info("Verify Incident Dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            log.info("Select Last 5 Years time range");
            dashboardFeatureMethods.selectLast5years();

            log.info("Validate Reporting Viewer permissions for editing controls");
            softAssert.assertFalse(dashboardFeatureMethods.hasEditControlsVisible(), "Editing controls (Add panel/Save/Settings) should not be available for unauthorized Reporting Viewer role.");

            log.info("Validate dashboard panels show errors due to missing incident authorization");
            //softAssert.assertTrue(dashboardFeatureMethods.verifyDashboardHasErrors(), "Incident Dashboard panels should show errors for unauthorized viewer.");

            softAssert.assertAll();
            log.info("[Helix Dashboards] [Helix ITSM] Incident Dashboard validation for unauthorized Reporting Viewer completed.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform] User with default 'Reporting Viewer' permissions logs in to ADE and runs 'Incident Dashboard'", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12350", Products = {Product.Dashboards})
    public void DRSQA12350_verify_Incident_dashboard_using_Reporting_viewer(ITestContext context) throws IOException {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform]User with default 'Reporting Viewer' permissions logs in to ADE and tries to run 'Incident Dashboard'");
        String dashboardName = SolQAConstants.Incident_dashboard;
        String folderName = SolQAConstants.HelixITSMfolderName;
        SoftAssert softAssert = new SoftAssert();

        try {
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12350");
            drsqa_12350_data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "ppanUser");

            log.info("---- Login to Portal with Reporting Viewer user -----");
            portalFeatureMethods.loginToADEPlatform(drsqa_12350_data.get("UserName").trim(), drsqa_12350_data.get("Password"));

            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            log.info("Opening Incident Dashboard");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            log.info("Verify Incident Dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            log.info("Select Last 5 Years time range");
            dashboardFeatureMethods.selectLast5years();

            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("Verify all filters in Incident Dashboard");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("Validate Reporting Viewer permissions for editing controls");
            softAssert.assertFalse(dashboardFeatureMethods.hasEditControlsVisible(), "Editing controls (Add panel/Save/Settings) should not be available for Reporting Viewer role.");

            softAssert.assertAll();
            log.info("[Helix Dashboards] [Helix ITSM] Incident Dashboard validation for Reporting Viewer completed.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix BWF] [Converged Platform] User with default 'Reporting Viewer' permissions logs in to ADE and tries to run BWF dashboard", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", TTID = "7321", JiraID = "DRSQA-12349", Products = {Product.Dashboards})
    public void DRSQA12349_verify_BWF_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix BWF] [Converged Platform] User with default 'Reporting Viewer' permissions logs in to ADE and tries to run BWF dashboard");
        String dashboardName = SolQAConstants.BWF_dashboard;
        String folderName = SolQAConstants.HelixBusinessWorkflowsFolder;
        SoftAssert softAssert = new SoftAssert();

        try {
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12349");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            Map<Object, String> drsqa_12349_data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "opsSupportAgent");

            log.info("---- Login to Portal with Reporting Viewer user -----");
            portalFeatureMethods.loginToADEPlatform(drsqa_12349_data.get("UserName").trim(), drsqa_12349_data.get("Password"));

            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            log.info("Opening Business Workflows Dashboard");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            log.info("Verify Business Workflows Dashboard loaded successfully");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            log.info("Select Last 90 Days time range");
            dashboardFeatureMethods.selectLast90Days();

            log.info("Verify dashboard has no errors");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("Validate drill-through from 'Cases by assigned group' panel to case details");
            softAssert.assertTrue(dashboardFeatureMethods.drillThroughCasesByAssignedGroup(),
                    "Case details did not open from 'Cases by assigned group' panel.");

            softAssert.assertAll();
            log.info("[Helix Dashboards] [Helix BWF] Business Workflows Dashboard validation for Reporting Viewer completed.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] [Helix ITSM] User with Report Editorship creates custom ITSM dashboard", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12353", Products = {Product.Dashboards})
    public void DRSQA12353_User_with_Report_Editorship_creates_custom_ITSM_dashboard(ITestContext context) throws IOException {

        log.info("[Helix Dashboards] [Helix ITSM] User with Report Editorship creates custom ITSM dashboard 'My Incident Details'");
        String dashboardName = "Custom_ITSM_SolQA_" + System.currentTimeMillis();
        SoftAssert softAssert = new SoftAssert();

        try {
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12353");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            drsqa_12353_data = readJson.getInputValuesMapIncludingLabelField( SolQAConstants.appITOM + File.separator + SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12353_Data");

            log.info("---- Login with Reporting Editor user -----");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            log.info("---- Navigate to BMC Helix Dashboards ----");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
            helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());

            log.info("---- Create new custom ITSM dashboard ----");
            helixDashboardFeatureMethods.openMenu(drsqa_12353_data.get("menuItem"));
            helixDashboardFeatureMethods.createNewVisualizationDashboard(dashboardName);

            log.info("Select query type as Service Management");
            helixDashboardFeatureMethods.enterQueryDetails("Query type", drsqa_12353_data.get("queryType"));
            helixDashboardFeatureMethods.selectTableData();

            log.info("Enter panel title and description");
            helixDashboardFeatureMethods.enterPanelDetails(drsqa_12353_data.get("panelTitle"), drsqa_12353_data.get("panelDescription"));
            helixDashboardFeatureMethods.saveChanges();

            log.info("Enter dashboard details and save");
            helixDashboardFeatureMethods.enterDashboardDetails(dashboardName, drsqa_12353_data.get("dashboardDescription"));
            helixDashboardFeatureMethods.saveDashboard();

            log.info("Validate custom dashboard and editor capabilities");
            helixDashboardFeatureMethods.verifyDashboardPageLaunchedSuccessfully();
            softAssert.assertTrue(helixDashboardFeatureMethods.isPanelPresent(drsqa_12353_data.get("panelTitle")),
                    "Custom ITSM panel is not present on the dashboard.");
            softAssert.assertTrue(dashboardFeatureMethods.hasEditControlsVisible(),
                    "Reporting Editor should have edit controls visible on custom dashboard.");

            softAssert.assertAll();
            log.info("[Helix Dashboards] [Helix ITSM] Custom ITSM dashboard creation and validation completed.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "Verify custom grafana report created using 'cross tab panel' plugin for Events data", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", TTID = "3829", JiraID = "DRSQA-12343", Products = {Product.Dashboards})
    public void DRSQA12343_verify_Events_report_with_CrossTabPanel(ITestContext context) {

        log.info("Verify custom grafana report created using 'cross tab panel' plugin for Events data");
        String dashboardName = "Custom_Dashboards_SolQA_" + System.currentTimeMillis();

        try {
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12343");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());

            log.info("Login to Helix Portal with admin user and launch Dashboards");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            drsqa_12343_data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITOM + File.separator + SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12343_Data");

            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            log.info("Create dashboard and configure cross tab panel for events data");
            helixDashboardFeatureMethods.openMenu(drsqa_12343_data.get("menuItem"));
            helixDashboardFeatureMethods.createNewVisualizationDashboard(dashboardName);
            helixDashboardFeatureMethods.selectVisualizationPlugin("Helix Cross-tab");
            helixDashboardFeatureMethods.enterQueryDetails("Data source", drsqa_12343_data.get("dataSource"));
            helixDashboardFeatureMethods.enterQueryDetails("Query type", drsqa_12343_data.get("queryType"));
            helixDashboardFeatureMethods.configureCrossTab(drsqa_12343_data.get("rowField"), drsqa_12343_data.get("columnField"));
            helixDashboardFeatureMethods.enterPanelDetails(drsqa_12343_data.get("panelTitle"), drsqa_12343_data.get("panelDescription"));
            helixDashboardFeatureMethods.saveChanges();
            helixDashboardFeatureMethods.enterDashboardDetails(dashboardName, drsqa_12343_data.get("panelDescription"));
            helixDashboardFeatureMethods.saveDashboard();

            log.info("Validate cross tab panel displays expected headers");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            Assert.assertTrue(helixDashboardFeatureMethods.verifyCrossTabHeaders(drsqa_12343_data.get("rowField"), drsqa_12343_data.get("columnField")),
                    "Cross tab panel does not show expected row/column headers.");
            log.info("Cross tab panel validation completed for dashboard: " + dashboardName);

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "[Helix Dashboards] Verify Combination-Chart Panel Plug-in.", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", TTID = "8652", JiraID = "DRSQA-12377", Products = {Product.Dashboards})
    public void DRSQA12377_VerifyCombinationOfBarChart(ITestContext context) {

        log.info("[Helix Dashboards] Verify Combination-Chart Panel Plug-in.");
        String dashboardName = "AutomationDashboard_" + System.currentTimeMillis();

        try {
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12377");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();
            helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());

            log.info("Login to Helix Dashboard using admin user");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            drsqa_12377_data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITOM + File.separator + SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12377_Data");

            log.info("Cross launch BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            log.info("Create dashboard using BMC Combination Chart plugin");
            helixDashboardFeatureMethods.openMenu(drsqa_12377_data.get("menuItem"));
            helixDashboardFeatureMethods.createNewVisualizationDashboard(dashboardName);
            helixDashboardFeatureMethods.selectVisualizationPlugin("Helix Combination Chart");
            helixDashboardFeatureMethods.enterQueryDetails("Data source", drsqa_12377_data.get("dataSource"));
            helixDashboardFeatureMethods.enterQueryDetails("Query type", drsqa_12377_data.get("queryType"));
            helixDashboardFeatureMethods.configureCombinationBarChart("severity", drsqa_12377_data.get("xAxisTitle"), "Count", drsqa_12377_data.get("chartType"), drsqa_12377_data.get("yAxisTitle"));
            helixDashboardFeatureMethods.enterPanelDetails(drsqa_12377_data.get("panelTitle"), "Combination chart for event severity");
            helixDashboardFeatureMethods.saveChanges();
            helixDashboardFeatureMethods.enterDashboardDetails(dashboardName, drsqa_12377_data.get("panelTitle"));
            helixDashboardFeatureMethods.saveDashboard();
            dashboardFeatureMethods.selectLast5years();
            dashboardFeatureMethods.verifyDashboardHasNoErrors();
            Assert.assertTrue(helixDashboardFeatureMethods.validateCombinationChartData(drsqa_12377_data.get("xAxisTitle"), drsqa_12377_data.get("yAxisTitle")),
                    "Combination chart axis titles or data labels are missing.");
            log.info("[Helix Dashboards] Combination chart plugin validation completed.");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

	@Test(description = "[License Utility] Validate BHOM License Usage dashboards", groups = {"Regression", "HelixDashboard"})
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-16700", Products = { Product.Dashboards })
	public void DRSQA_16700_Validate_BHOM_License_Usage_Dashboards(ITestContext context) {

		log.info("Starting Test: [License Utility] Validate BHOM License Usage dashboards");

		try {
			log.info("Login to Helix Portal with User (Admin)");
			portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-16700");
			dashboardFeatureMethods = new Dashboard_FeatureMethods();
			helixDashboardFeatureMethods = new DashboadFeatureMethods();
			readJson = new ReadJson();
			bhomUsageReportingData = readJson.getInputValuesMapIncludingLabelField(
					SolQAConstants.appITOM + File.separator + SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData",
					"DRSQA_16700_Data");
			expectedFolderNames = Arrays.asList(bhomUsageReportingData.get("expectedDashboardNames").split(","));
			log.info("Expected Folder Count: " + expectedFolderNames.size());

			log.info("---- Login to Portal with correct username and password -----");
			portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
			log.info("----- Go to Dashboard -> BHOM Usage Reporting ----");
            helixDashboardFeatureMethods.openMenu(bhomUsageReportingData.get("menuItem"));
            helixDashboardFeatureMethods.openDashboard(bhomUsageReportingData.get("BhomfolderName"));
			//dashboardFeatureMethods.openDashboard(bhomUsageReportingData.get("BhomfolderName"), null);

			log.info("----- Verify BHOM Usage dashboards are present in the folder ----");
			helixDashboardFeatureMethods.verifyDashboardsInFolder(expectedFolderNames);

			log.info("----- Click on BHOM Usage Reporting Dashboard ----");
			dashboardFeatureMethods.openDashboard("BHOM Usage Reporting Dashboard", "BHOM Usage Reporting");

			log.info("----- Verify BHOM Usage Reporting Dashboard loaded successfully ----");
			dashboardFeatureMethods.verifyDashboardLoaded(bhomUsageReportingData.get("Dashboard_1"));

			log.info("----- Verify dashboard has no errors ----");
			dashboardFeatureMethods.verifyDashboardHasNoErrors();

			log.info(
					"----- Verify dashboard shows category and usage info for AIOPS and Event Management categories ----");
			dashboardFeatureMethods.validateCategoryAndUsageColumnsWithData();

			log.info("----- Validate additional BHOM Usage dashboards with Category and Usage data ----");
			List<String> additionalDashboards = Arrays.asList(bhomUsageReportingData.get("Dashboard_2"),
					bhomUsageReportingData.get("Dashboard_3"), bhomUsageReportingData.get("Dashboard_4"));

			for (String dashboardName : additionalDashboards) {
				log.info("----- Click on " + dashboardName + " ----");
				dashboardFeatureMethods.openDashboard(dashboardName, null);

				log.info("----- Verify " + dashboardName + " loaded successfully ----");
				dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

				log.info("----- Verify dashboard has no errors ----");
				dashboardFeatureMethods.verifyDashboardHasNoErrors();

				log.info("----- Verify dashboard shows category and usage info ----");
				dashboardFeatureMethods.validateLicenseUsageDataInDashboard();
			}

			log.info("Test execution completed for [License Utility] Validate BHOM License Usage dashboards END");

		} catch (AssertionError e) {
			throw new AssertionError("Failed to execute test case" + e);
		} catch (Exception e) {
			throw new AssertionError("Failed to execute test case" + e);
		}
	}
    @Test(description = "[Helix Dashboards] [Helix Chatbot] [Converged Platform] Verify OOTB dashboard for Chatbot User", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17533", Products = {Product.Dashboards})
    public void DRSQA17533_verify_chatbot_user_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix Chatbot] [Converged Platform] Verify OOTB dashboard for 'Chatbot User'");
        String dashboardName = SolQAConstants.Chatbot_User;
        String folderName = SolQAConstants.Chatbot_Dashboard_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17533 - Verify Chatbot User Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17533");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Chatbot User dashboard
            log.info("STEP 4: Opening 'Chatbot User' dashboard from 'Helix Chatbot' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Chatbot User dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the chatbot related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17533 - Chatbot User Dashboard verified successfully");
            log.info("All chatbot panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for Live Chat Customer Experience", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17595", Products = {Product.Dashboards})
    public void DRSQA17595_Live_Chat_Customer_Experience_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for 'Live Chat Customer Experience'");
        String dashboardName = SolQAConstants.Live_Chat_Customer_Experience;
        String folderName = SolQAConstants.Live_Chat_Dashboard_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17595 - Verify Live Chat Customer Experience Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17595");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Live Chat Customer Experience dashboard
            log.info("STEP 4: Opening 'Live Chat Customer Experience' dashboard from 'Helix Live Chat' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Live Chat Customer Experience dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Live Chat Customer Experience related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 8: Verify all filters present in Live Chat Customer Experience dashboard
            log.info("---- Verify all filters in Live Chat Customer Experience dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17595 - Live Chat Customer Experience Dashboard verified successfully");
            log.info("All Live Chat panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for Live Chat Historic Data", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17597", Products = {Product.Dashboards})
    public void DRSQA17597_Live_Chat_Historic_Data_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for 'Live Chat Historic Data'");
        String dashboardName = SolQAConstants.Live_Chat_Historic_Data;
        String folderName = SolQAConstants.Live_Chat_Dashboard_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17597 - Verify Live Chat Historic Data Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17597");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Live Chat Historic Data dashboard
            log.info("STEP 4: Opening 'Live Chat Historic Data' dashboard from 'Helix Live Chat' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Live Chat Historic Data dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Live Chat Historic Data related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 8: Verify all filters present in Live Chat Customer Experience dashboard
            log.info("---- Verify all filters in Live Chat Historic Data dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17597 - Live Chat Historic Data Dashboard verified successfully");
            log.info("All Live Chat panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for Live Chat Team & Agent Performance", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17596", Products = {Product.Dashboards})
    public void DRSQA17596_Live_Chat_Team_Agent_Performance_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for 'Live Chat Team & Agent Performance'");
        String dashboardName = SolQAConstants.Live_Chat_Team_Agent_Performance;
        String folderName = SolQAConstants.Live_Chat_Dashboard_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17596 - Verify Live Chat Team & Agent Performance Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17596");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Live Chat Team & Agent Performance dashboard
            log.info("STEP 4: Opening 'Live Chat Team & Agent Performance' dashboard from 'Helix Live Chat' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Live Chat Team & Agent Performance dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Live Chat Team & Agent Performance related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 8: Verify all filters present in Live Chat Team & Agent Performance dashboard
            log.info("---- Verify all filters in Live Chat Team & Agent Performance dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17596 - Live Chat Team & Agent Performance Dashboard verified successfully");
            log.info("All Live Chat panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }


    @Test(description = "[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for Live Chat Usage & Value", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17598", Products = {Product.Dashboards})
    public void DRSQA17598_Live_Chat_Usage_Value_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix Live Chat] [Converged Platform] Verify OOTB dashboard for 'Live Chat Usage & Value'");
        String dashboardName = SolQAConstants.Live_Chat_Usage_Value;
        String folderName = SolQAConstants.Live_Chat_Dashboard_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17598 - Verify Live Chat Usage & Value Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17598");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Live Chat Usage & Value dashboard
            log.info("STEP 4: Opening 'Live Chat Usage & Value' dashboard from 'Helix Live Chat' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Live Chat Usage & Value dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Live Chat Usage & Value related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 8: Verify all filters present in Live Chat Usage & Value dashboard
            log.info("---- Verify all filters in Live Chat Usage & Value dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17598 - Live Chat Usage & Value Dashboard verified successfully");
            log.info("All Live Chat panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform] Verify OOTB dashboard for Asset Incidents", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17712", Products = {Product.Dashboards})
    public void DRSQA17712_Asset_Incidents_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform] Verify OOTB dashboard for 'Asset Incidents'");
        String dashboardName = SolQAConstants.Asset_Incidents;
        String folderName = SolQAConstants.HelixITSMfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17712 - Verify Asset Incidents Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17712");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Asset Incidents dashboard
            log.info("STEP 4: Opening 'Asset Incidents' dashboard from 'Helix ITSM' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Asset Incidents dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Asset Incidents related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17712 - Asset Incidents Dashboard verified successfully");
            log.info("All Asset Incidents Dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform] Verify OOTB dashboard for Changes by Assets", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17713", Products = {Product.Dashboards})
    public void DRSQA17713_Changes_by_Assets_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform] Verify OOTB dashboard for 'Changes by Assets'");
        String dashboardName = SolQAConstants.Asset_Changes;
        String folderName = SolQAConstants.HelixITSMfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17713 - Verify Changes by Assets Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17713");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Changes by Assets dashboard
            log.info("STEP 4: Opening 'Changes by Assets' dashboard from 'Helix ITSM' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Changes by Assets dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Changes by Assets related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17713 - Changes by Assets Dashboard verified successfully");
            log.info("All Changes by Assets Dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [Helix ITSM] [Converged Platform] Verify OOTB dashboard for Problems by Asset", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17714", Products = {Product.Dashboards})
    public void DRSQA17714_Problems_by_Asset_dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [Helix ITSM] [Converged Platform] Verify OOTB dashboard for 'Problems by Asset'");
        String dashboardName = SolQAConstants.Asset_Problems;
        String folderName = SolQAConstants.HelixITSMfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17714 - Verify Problems by Asset Dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17714");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the Problems by Asset dashboard
            log.info("STEP 4: Opening 'Problems by Asset' dashboard from 'Helix ITSM' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Problems by Asset dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Problems by Asset related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17714 - Problems by Asset Dashboard verified successfully");
            log.info("All Problems by Asset Dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "[Helix Dashboards] [HelixGPT] Verify OOTB dashboard for Service Collaborator Value Dashboard for BWF", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17153", Products = {Product.Dashboards})
    public void DRSQA17153_Service_Collaborator_Value_Dashboard(ITestContext context) {

        log.info("[Helix Dashboards] [HelixGPT] [Converged Platform] Verify OOTB dashboard for 'Service Collaborator Value Dashboard for BWF'");
        String dashboardName = SolQAConstants.Service_Collaborator_Value_Dashboard;
        String folderName = SolQAConstants.HelixGPT_Folder;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-17153 - Verify Service Collaborator Value Dashboard for BWF");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17153");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            log.info("---- Navigate to BMC Helix Business Workflows ------");

            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BMC_HELIX_BUSINESS_WORKFLOWS);
            bwfFeatureMethods = new BWFFeatureMethods();
            bwfFeatureMethods.verifyLoginToBWF();

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("---- Cross launch Helix Dashboards from BWF application launcher ----");
            bwfFeatureMethods.crossLaunchApplication(SolQAConstants.HELIX_DASHBOARD);

            CommonWaits commonWaits = new CommonWaits();
            commonWaits.switchToLastWindow();

            // STEP 4: Open Service Collaborator Value Dashboard
            log.info("STEP 4: Opening 'Service Collaborator Value Dashboard' dashboard from 'HelixGPT' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify Service Collaborator Value Dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 5 years
            log.info("STEP 6: change time range to 5 years");
            log.info("Select Last 5 years time range");
            dashboardFeatureMethods.selectLast5years();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Service Collaborator Value Dashboard related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 8: Verify all filters present in Service Collaborator Value Dashboard
            log.info("---- Verify all filters in Service Collaborator Value Dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-17153 - Service Collaborator Value Dashboard verified successfully");
            log.info("All Service Collaborator Value Dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }
    @Test(description = "Verify OOTB Change Calendar dashboard", groups = {"Regression", "CP", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-18161", Products = {Product.Dashboards})
    public void DRSQA18161_OOTB_Change_Calendar_dashboard(ITestContext context) {

        log.info("OOTB Change Calendar dashboard");
        String dashboardName = SolQAConstants.Change_Calendar_dashboard;
        String folderName = SolQAConstants.HelixITSMfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-18161 - Verify OOTB Change Calendar dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18161");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the OOTB Change Calendar dashboard
            log.info("STEP 4: Opening 'OOTB Change Calendar' dashboard from 'Helix ITSM' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify OOTB Change Calendar dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify dashboard has no errors on any panels
            log.info("STEP 6: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All the Change Calendar related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in OOTB Change Calendar dashboard
            log.info("---- Verify all filters in OOTB Change Calendar dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-18161 - OOTB Change Calendar dashboard verified successfully");
            log.info("All Change Calendar dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }


    @Test(description = "Verify OOTB Dashboard for BMC Helix Group wise and Role wise users list", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-18243", Products = {Product.Dashboards})
    public void DRSQA18243_OOTB_BMC_Helix_Group_wise_and_Role_wise_users_list_dashboard(ITestContext context) {

        log.info("Verify OOTB BMC Helix Group wise and Role wise users list dashboard");
        String dashboardName = SolQAConstants.BMC_Helix_Group_wise_and_Role_wise_users_list;
        String folderName = SolQAConstants.HelixCommonServicesfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-18243 - BMC Helix Group wise and Role wise users list");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18243");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the OOTB BMC Helix Group wise and Role wise users list dashboard
            log.info("STEP 4: Opening 'OOTB BMC Helix Group wise and Role wise users list' dashboard from 'Helix Common Services' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify OOTB BMC Helix Group wise and Role wise users list dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: change time range to 90 days
            log.info("STEP 6: change time range to  90 days");
            log.info("Select Last  90 days time range");
            dashboardFeatureMethods.selectLast90Days();

            // STEP 7: Verify dashboard has no errors on any panels
            log.info("STEP 7: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All BMC Helix Group wise and Role wise users list dashboard related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 8: Verify all filters present in OOTB BMC Helix Group wise and Role wise users list dashboard
            log.info("---- Verify all filters in OOTB BMC Helix Group wise and Role wise users list dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-18243 - OOTB BMC Helix Group wise and Role wise users list dashboard verified successfully");
            log.info("All BMC Helix Group wise and Role wise users list dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "Verify OOTB Storage dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-18230", Products = {Product.Dashboards})
    public void DRSQA18230_OOTB_Storage_dashboard(ITestContext context) {

        log.info("Verify OOTB Storage dashboard");
        String dashboardName = SolQAConstants.Storage_dashboard;
        String folderName = SolQAConstants.LicenseUsageDashboardsfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-18230 - Verify OOTB Storage dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18230");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the OOTB Storage dashboard
            log.info("STEP 4: Opening 'OOTB Storage' dashboard from 'License Usage Dashboards' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify OOTB Storage dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify dashboard has no errors on any panels
            log.info("STEP 6: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All Storage dashboard related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-18230 - Verify OOTB Storage dashboard verified successfully");
            log.info("All Storage dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "Verify OOTB License utilization dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-18229", Products = {Product.Dashboards})
    public void DRSQA18229_OOTB_License_utilization_dashboard(ITestContext context) {

        log.info("Verify OOTB License utilization Dashboard");
        String dashboardName = SolQAConstants.License_utilization_dashboard;
        String folderName = SolQAConstants.LicenseUsageDashboardsfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-18229 - Verify OOTB License utilization dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18229");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the OOTB License utilization dashboard
            log.info("STEP 4: Opening 'OOTB License utilization' dashboard from 'License Usage Dashboards' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify OOTB License utilization dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify dashboard has no errors on any panels
            log.info("STEP 6: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All OOTB License utilization dashboard related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            // STEP 7: Verify all filters present in License utilization dashboard
            log.info("---- Verify all filters in OOTB License utilization dashboard ----");
            dashboardFeatureMethods.verifyAllDashboardFilters();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-18229 - Verify OOTB License utilization dashboard verified successfully");
            log.info("All License utilization dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @Test(description = "Verify OOTB Discovery dashboard", groups = {"Regression", "HelixDashboard"})
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-18228", Products = {Product.Dashboards})
    public void DRSQA18228_OOTB_Discovery_dashboard(ITestContext context) {

        log.info("Verify OOTB Discovery dashboard");
        String dashboardName = SolQAConstants.Discovery_dashboard;
        String folderName = SolQAConstants.LicenseUsageDashboardsfolderName;

        try {
            log.info("======================================================================");
            log.info("TEST: DRSQA-18228 - Verify OOTB Discovery dashboard");
            log.info("======================================================================");

            // STEP 1: Initialize test components
            log.info("STEP 1: Initialize Portal and Dashboard Feature Methods");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-18228");
            dashboardFeatureMethods = new Dashboard_FeatureMethods();

            // STEP 2: Login to ADE Platform
            log.info("STEP 2: Login to Helix Portal with User which is sync-ed from Converged platform");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

            // STEP 3: Cross-launch to Helix Dashboard
            log.info("STEP 3: Cross-launch to BMC Helix Dashboards");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

            // STEP 4: Open the OOTB Discovery dashboard
            log.info("STEP 4: Opening 'OOTB Discovery' dashboard from 'License Usage Dashboards' folder");
            dashboardFeatureMethods.openDashboard(dashboardName, folderName);

            // STEP 5: Verify dashboard loaded successfully
            log.info("STEP 5: Verify OOTB Discovery dashboard loaded successfully");
            log.info("Dashboard title should be visible in top left corner");
            dashboardFeatureMethods.verifyDashboardLoaded(dashboardName);

            // STEP 6: Verify dashboard has no errors on any panels
            log.info("STEP 6: Verify Dashboard should get loaded without any errors on any of the panels");
            log.info("All OOTB Discovery dashboard related data should be loaded in all panels as expected");
            dashboardFeatureMethods.verifyDashboardHasNoErrors();

            log.info("======================================================================");
            log.info("TEST PASSED: DRSQA-18228 - Verify OOTB Discovery dashboard verified successfully");
            log.info("All Discovery dashboard panels loaded without errors");
            log.info("======================================================================");

        } catch (AssertionError e) {
            log.error("Test failed with assertion error: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        } catch (Exception e) {
            log.error("Test failed with exception: " + e.getMessage());
            throw new AssertionError("Failed to execute test case: " + e.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
       log.info("---- Starting cleanup process ----");
		try {
			if (portalFeatureMethods != null) {
				portalFeatureMethods.closeBrowser();
			}
			if (dashboardFeatureMethods != null) {
				dashboardFeatureMethods.closeBrowser();
			}
			if (helixDashboardFeatureMethods != null) {
				helixDashboardFeatureMethods.closeBrowser();
			}
			log.info("---- Cleanup completed successfully ----");
		} catch (Exception e) {
			log.error("Error during cleanup: " + e.getMessage(), e);
		}
    }

}