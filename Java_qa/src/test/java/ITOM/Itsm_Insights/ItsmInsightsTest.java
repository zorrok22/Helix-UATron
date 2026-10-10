package ITOM.Itsm_Insights;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Itsm_Insights.ITSMInsightsFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import util.*;
import java.io.File;
import java.util.Map;

public class ItsmInsightsTest extends Launcher {

    public static SolqaLogger log = new SolqaLogger(ItsmInsightsTest.class.getName());
    WebDriver adeDriver;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    ITSMInsightsFeatureMethods itsmInsightsFeatureMethods;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    Portal_FeatureMethods portalFeatureMethods;

    private Map<Object, String> itomUserData;
    private String userKey = "aananda"; // Default user key

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM.ITOM_Prerequisites.updateITOMUserDataFromExcel(config);
        userKey = "itomadminuser";
        log.info("ItsmInsightsTest will use user: " + itomUserData.get("UserName"));
    }

    @Test(description = "Create problem investigation for cluster from ITSM Insights application.", singleThreaded = true,
            dependsOnMethods = {"ITOM.Sanity_Prerequisites.sanityPrerequisites"}, groups = {"ITSMInsights"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12356", Products = {Product.ITSMInsight, Product.SmartIT})
    public void DRSQA_12356_Create_Problem_Investigation_For_Cluster(ITestContext context) throws Exception {
        // User data already loaded in @BeforeTest - just use it directly
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("==== TEST STARTED: DRSQA-12356 ====");
        log.info("Objective: Create problem investigation for cluster via repeated incident creation");

        try {
            log.info("Launching ITOM application");
            adeDriver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            setDriver(adeDriver);
            context.setAttribute("DRSQA_12356_Driver", getDriver());
            context.setAttribute("DRSQA_12356_Product", Product.Helix_DWP);
            log.info("Logging in to ITOM with username: " + username);
            login(username, password, SolQAConstants.appITOM);

            itsmInsightsFeatureMethods = new ITSMInsightsFeatureMethods();
            smartITFeatureMethods = new SmartITFeatureMethodsPage();
            portalFeatureMethods = new Portal_FeatureMethods();

            log.info("Verifying login to ITOM portal");
            portalFeatureMethods.verifyLogin();
            log.info("Cross launching to Helix ITSM");
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
            log.info("Verifying SmartIT page launched successfully");
            smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

            log.info("Navigating to Proactive Problem Management");
            smartITFeatureMethods.navigateToProactiveProblemManagement();
            log.info("Navigating to Manage Jobs in ITSM Insights");
            smartITFeatureMethods.navigateToManageJobsITSMInsights();

            String jobName = "Test_Solqa_One_Time_Job_" + RandomStringUtils.randomAlphabetic(5);
            log.info("Creating one-time job with name: " + jobName);
            smartITFeatureMethods.createOneTimeJob(jobName);
            log.info("Verifying job status for job: " + jobName);
            smartITFeatureMethods.verifyJobStatus(jobName);
            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-12356 ====");
        } catch (Exception e) {
            log.error("==== TEST FAILED: DRSQA-12356 ====");
            log.error("Failure reason: " + e.getMessage(), e);
            throw e;
        }
    }

    @Test(description = "[ITSM Insights][SmartIT][ITSM] Verify Problem co-ordinator is able to Create one-time job and verify clusters.", groups = {"Regression"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12339", Products = {Product.ITSMInsight, Product.SmartIT})
     public void DRSQA_12339_Verify_Problem_Coordinator_is_able_to_Create_one_time_job_and_verify_clusters(ITestContext context) throws Exception {

        log.info("********** START:- [ITSM Insights][SmartIT][ITSM] Verify Problem co-ordinator is able to Create one-time job and verify clusters. ***********");
        log.info("Objective: Verify Problem co-ordinator is able to Create one-time job and verify clusters.");
       
        log.info("--- Step 1: Navigate to Proactive Problem Management ---");
        log.info("Launching ITOM application");
        adeDriver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        context.setAttribute("DRSQA_12339_Driver", getDriver());
        context.setAttribute("DRSQA_12339_Product", Product.Helix_DWP);
        log.info("Logging in to ITOM with username: " + username);
        login(username, password, SolQAConstants.appITOM);
        itsmInsightsFeatureMethods = new ITSMInsightsFeatureMethods();
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        portalFeatureMethods = new Portal_FeatureMethods();
        log.info("Verifying login to ITOM portal");
        portalFeatureMethods.verifyLogin();
        log.info("Cross launching to Helix ITSM");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
        log.info("Verifying SmartIT page launched successfully");
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("Navigating to Proactive Problem Management");
        smartITFeatureMethods.navigateToProactiveProblemManagement();
        log.info("Navigating to Manage Jobs in ITSM Insights");
        smartITFeatureMethods.navigateToManageJobsITSMInsights();

        log.info("------- Step 2:Enter unique job name(e.g Sample_Job1) and select appropriate Algorithm from dropdown. -------");
        log.info("------- Step 3: Step3 is already covered in Step1 -------");
        log.info("------- Step 4: Select Date range (i.e how many data/tickets u want to fetch from ITSM) (e.g 1st Dec to 6th Dec) -------");
        log.info("------- Step 5: Keep Data set as show system fields only.-------");
        log.info("------- Step 6: Click on show icon to view preview of field values-------");
        log.info("------- Step 7:  in create Clusters section , select GroupBy: Assignee-company, Company, Status field and fourth value will not be selected.-------");
        log.info("------- Step 8: Click on Save Job button -------");
        String jobName = "Test_Solqa_One_Time_Job_" + RandomStringUtils.randomAlphabetic(5);
        log.info("Creating one-time job with name: " + jobName);
        smartITFeatureMethods.createOneTimeJob(jobName);
        
        log.info("------- Step 9: Wait for job run to be completed. It will take around 1 min.-------");
        smartITFeatureMethods.waitForOneMinuteJobToRun();
        log.info("Verifying job status for job: " + jobName);
        smartITFeatureMethods.verifyJobStatus(jobName);

        log.info("------- Step 10: Go to the Dashboard -> Select the successfully executed job from jobs dropdown list -> Go to List view tab. -------");
        log.info("Navigating to Proactive Problem Management");
        smartITFeatureMethods.navigateBackToProactiveProblemManagement();
        smartITFeatureMethods.verifyListViewContainsClusterandINCData();

        log.info("********** END:- [ITSM Insights][SmartIT][ITSM] Verify Problem co-ordinator is able to Create one-time job and verify clusters. ***********");
}

    @Test(description = "[ITSM Insights][SmartIT] Verify Relate to Problem option in drilldown view", groups = {"Regression"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12373", Products = {Product.ITSMInsight, Product.SmartIT})
     public void DRSQA_12373_Verify_Relate_to_Problem_option_in_drilldown_view(ITestContext context) throws Exception {

        log.info("********** START:- [ITSM Insights][SmartIT] Verify Relate to Problem option in drilldown view ***********");
       
        log.info("--- Step 1: Navigate to Proactive Problem Management ---");
        log.info("Launching ITOM application");
        adeDriver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        context.setAttribute("DRSQA_12373_Driver", getDriver());
        context.setAttribute("DRSQA_12373_Product", Product.Helix_DWP);
        log.info("Logging in to ITOM with username: " + username);
        login(username, password, SolQAConstants.appITOM);
        itsmInsightsFeatureMethods = new ITSMInsightsFeatureMethods();
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        portalFeatureMethods = new Portal_FeatureMethods();
        log.info("Verifying login to ITOM portal");
        portalFeatureMethods.verifyLogin();
        log.info("Cross launching to Helix ITSM");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
        log.info("Verifying SmartIT page launched successfully");
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("Navigating to Proactive Problem Management");
        smartITFeatureMethods.navigateToProactiveProblemManagement();

        log.info("------- Step 2: On the Dashboard page -> Select any cluster -> Click on 3 dots drilldown on top corner of the cluster map -------");
        smartITFeatureMethods.clickOn3DotsDrilldownOnTopCornerOfClusterMap();

        log.info("------- Step 3 and Step 4: Click on Relate to Problem -------");
        log.info("-------  Select Relate to existing problem-------");
        smartITFeatureMethods.selectClusterOption("Relate to existing problem");
        smartITFeatureMethods.relateToExistingProblemAndVerifySuccessToast();

        log.info("------- Step 5: Once the problem is related successfully, come back to the Cluster Heat Map and hover on the cluster -------");
        smartITFeatureMethods.clickOn3DotsDrilldownOnTopCornerOfClusterMap();

        log.info("------- Step 6: Try creating another problem for the same incident -------");
        smartITFeatureMethods.selectClusterOption("Create problem");
         String problemID = smartITFeatureMethods.createProblemAndGetProblemID();

        log.info("------- Step 7: Open the cluster to view its details -------");
         smartITFeatureMethods.switchToTabByIndex(2);
         smartITFeatureMethods.verifyClusterDetailsPageContainsProblemID();

        log.info("********** END:- [ITSM Insights][SmartIT] Verify Relate to Problem option in drilldown view ***********");
    }
      
    @Test(description = "[ITSM Insights][SmartIT][ITSM] Verify Problem co-ordinator is able to create recurrent job and verify clusters created for job.", groups = {"Regression"})
    @SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12338", Products = {Product.ITSMInsight, Product.SmartIT})
     public void DRSQA_12338_Verify_Problem_Coordinator_is_able_to_create_recurrent_job_and_verify_clusters_created_for_job(ITestContext context) throws Exception {

        log.info("********** START:- [ITSM Insights][SmartIT][ITSM] Verify Problem co-ordinator is able to create recurrent job and verify clusters created for job. ***********");
       
        log.info("--- Step 1: Login to SmartIT application with user as problem co-ordinator permission.---");
        log.info("Launching ITOM application");
        adeDriver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        context.setAttribute("DRSQA_12338_Driver", getDriver());
        context.setAttribute("DRSQA_12338_Product", Product.Helix_DWP);
        log.info("Logging in to ITOM with username: " + username);
        login(username, password, SolQAConstants.appITOM);
        itsmInsightsFeatureMethods = new ITSMInsightsFeatureMethods();
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        portalFeatureMethods = new Portal_FeatureMethods();
        log.info("Verifying login to ITOM portal");
        portalFeatureMethods.verifyLogin();
        log.info("Cross launching to Helix ITSM");
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
        log.info("Verifying SmartIT page launched successfully");
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        log.info("--- Step 2: Click on More dropdown from adapt-navigation -> Select workspaces -> Click on Proactive problem management.---");
        log.info("Navigating to Proactive Problem Management");
        smartITFeatureMethods.navigateToProactiveProblemManagement();
        log.info("Navigating to Manage Jobs in ITSM Insights");
        smartITFeatureMethods.navigateToManageJobsITSMInsights();

        log.info("------- Step 3: Click on Manage jobs option -> click on Create Recurrent job link. -------");
        log.info("------- Step 4: Enter unique job name(e.g Test_Job1) and select appropriate Algorithm from dropdown.-------");
        log.info("------- Step 5: Select job frequency as Daily and select start and end recurrnt job dates (e.g start date -today , end date - tomorrow)-------");
        log.info("------- Step 6: Select the loop back period (means how many data/tickets u want to fetch from ITSM) (e.g 1 month, 1 week)-------");
        log.info("------- Step 7: Click on show icon to view preview of field values-------");
        log.info("------- Step 8: in create Clusters section , select GroupBy: Assignee-company, Company, Status field and fourth value will not be selected.-------");
        log.info("------- Step 9: Click on Save Job button -------");
        String jobName = "Test_Solqa_One_Time_Job_" + RandomStringUtils.randomAlphabetic(5);
        log.info("Creating one-time job with name: " + jobName);
        smartITFeatureMethods.createOneTimeJob(jobName);
        
        log.info("------- Step 10: Wait for job run to be completed. It will take around 1 min.-------");
        smartITFeatureMethods.waitForOneMinuteJobToRun();
        log.info("Verifying job status for job: " + jobName);
        smartITFeatureMethods.verifyJobStatus(jobName);

        log.info("------- Step 11: Go to the Dashboard -> Select the successfully executed job from jobs dropdown list. -------");
        log.info("Navigating to Proactive Problem Management");
        smartITFeatureMethods.navigateBackToProactiveProblemManagement();
       
        log.info("------- Step 12: Click on 3 dots present on left side of any cluster to view cluster details with incidents.-------");
        smartITFeatureMethods.openTheFirstCluster();

        log.info("------- Step 13: Click on any Incident present in cluster. Verify the incident ID which is clicked from ITSM insights should be same as opened in SmartIT application.-------");
        smartITFeatureMethods.openIncidentFromClusterDetailsPageAndVerifyIncidentID();

        log.info("------- Step 14: Open Resources tab present on the incident ->Check for Explore Probable Causes button .-------");
        smartITFeatureMethods.checkForExploreProbableCausesButton();

        log.info("------- Step 15: Click on Explore probable causes button.-------");
        smartITFeatureMethods.clickOnExploreProbableCausesButtonAndVerifyCrossLaunch();

        log.info("********** END:- [ITSM Insights][SmartIT][ITSM] Verify Problem co-ordinator is able to create recurrent job and verify clusters created for job. ***********");
}

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (itsmInsightsFeatureMethods != null) {
                itsmInsightsFeatureMethods.closeBrowser();
            }
            if (smartITFeatureMethods != null) {
                smartITFeatureMethods.closeBrowser();
            }
            if (portalFeatureMethods != null) {
                portalFeatureMethods.closeBrowser();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }

}