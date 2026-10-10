package ITSM.DWP;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITSM.DWPC.DWPCFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonWaits;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.CommonMethods;

public class DWP_DWPC extends Launcher {

    SolqaLogger log = new SolqaLogger(DWP_DWPC.class.getName());
    ReadJson readJson = new ReadJson();
    DWPFeatureMethods dwpFeatureMethod, dwpFeatureMethod2;
    DWPCFeatureMethods dwpcFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods;
    Map<Object, String> dwpcUserData, dwpcTestData, dwpUserData, dwpTestData, itsmUserData;
    WebDriver DWPCdriver, dwpDriver, dwpDriver2, itsmDriver;
    CommonWaits commonWaits;

    public DWP_DWPC() {
        dwpcFeatureMethods = new DWPCFeatureMethods();
        commonWaits = new CommonWaits();
        log.info("*********** beforeClass in DWP_DWPC ********************");
    }

    //previous owner:- Preethi 
    @Test(description = "[DWP][DWPC]Allow user to use rich text in ticket Comments", groups = {"Regression_P3", "DWP", "DWPC", "gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "9779", JiraID = "DRSQA-11624", Products = {Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_11624_AllowUserToUseRichText_InTicketComments(ITestContext context) {
        log.info("--------- DRSQA-11624: AllowUserToUseRichText_InTicketComments *** START");

        log.info("Step 1: Login to DWP as end user");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bmcopsenduser");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11624_RequestDetails");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11624_Driver", getDriver());
        context.setAttribute("DRSQA_11624_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();

        log.info("Step 2: Open Catalog and submit service request");
        String serviceName = dwpTestData.get("SERVICE_NAME");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(serviceName);
        } else {
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.clickOnRequestNow(serviceName);
        }
        dwpFeatureMethod.verifySrdTitle(serviceName);
        dwpFeatureMethod.clickOnSubmitRequest();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
        log.info("Submitted request id: " + requestId);

        log.info("Step 3: Open submitted request and add rich-text comment with link + attachment");
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);

        String rtfText = dwpTestData.get("RTF_TEXT");
        String linkText = dwpTestData.get("LINK_TEXT");
        String linkUrl = dwpTestData.get("LINK_URL");

        File attachment = CommonMethods.createTextFile("DRSQA_11624_RichTextComment_Attachment");
        dwpFeatureMethod.addRichTextCommentWithLinkAndAttachment(rtfText, linkUrl, linkText, attachment);

        log.info("Step 4: Verify comment contains link and attachment is present");
        dwpFeatureMethod.verifyAttachmentsSoftly(linkText, attachment);

        log.info("--------- DRSQA-11624: AllowUserToUseRichText_InTicketComments *** END");
    }

    @Test(description = "Surveys – Enable surveys to display NPS scores.", groups = {"Regression_P3", "DWPC", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", TTID = "", JiraID = "DRSQA-15148", Products = {Product.Helix_DWPC})
    public void DRSQA_15148_Enable_Surveys_To_Display_NPS_Scores(ITestContext context) throws Exception {

        log.info("--------- DRSQA-15148: Enable_Surveys_To_Display_NPS_Scores *** START");

        log.info("DRSQA-15148_Pre-requisite Steps: START *********");

        log.info("Step 1: Load DWPC user data and launch application");
        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(DWPCdriver);
        context.setAttribute("DRSQA_15148_Driver", getDriver());
        context.setAttribute("DRSQA_15148_Product", Product.Helix_DWPC);
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        
        log.info("Step 2: Load survey test data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_15148_Survey");
        
        log.info("Step 3: Verify Post-request survey settings with NPS rating scale");
        dwpcFeatureMethods.verifyPostRequestSurveySettings(dwpcTestData.get("RATING_SCALE"));
        dwpcFeatureMethods.captureScreenshot("DRSQA_15148_PostRequestSurveySettingsVerified", SolQAConstants.appDWPC);
        log.pass("Successfully verified and configured Post-request survey settings with NPS rating scale");

        log.info("Step 4: Navigate to Surveys page and verify");
        dwpcFeatureMethods.openSurveysPage();
        dwpcFeatureMethods.verifySurveysPageLaunchedSuccessfully();

        log.info("Step 5: Create new survey, validate NPS score fields and save");
        String surveyName = dwpcFeatureMethods.createNewSurvey(dwpcTestData.get("SURVEY_NAME"));
        log.info("Survey name is: " + surveyName);
        dwpcFeatureMethods.captureScreenshot("DRSQA_15148_SurveyCreatedAndSaved", SolQAConstants.appDWPC);
        log.pass("Successfully created, validated and saved survey with NPS scores display capability");

        log.info("Step 6: Navigate back to Dashboard page");
        dwpcFeatureMethods.navigateBackToDashboard();

        log.info("DRSQA-15148_Pre-requisite Steps: END *********");

        log.info("Step 7: Login to DWP with Fritz end user");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_15148_Data");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_15148_Driver", getDriver());
        context.setAttribute("DRSQA_15148_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");

        log.info("Step 8: Open Catalog Page and search for service");
        String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
        } else {
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
        }

        log.info("Step 9: Request a service: " + SERVICE_NAME);
        dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);

        log.info("Step 10: Add machine name");
        String MACHINE_NAME = dwpTestData.get("MACHINE_NAME");
        dwpFeatureMethod.addMachineNameInService(MACHINE_NAME);

        log.info("Step 11: Select software required");
        String SOFTWARE_LABEL = dwpTestData.get("SOFTWARE_LABEL");
        String SOFTWARE_NAME = dwpTestData.get("SOFTWARE_NAME");
        dwpFeatureMethod.selectSoftwareRequired(SOFTWARE_LABEL, SOFTWARE_NAME);

        log.info("Step 12: Submit the service request");
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_15148_ServiceRequestCreated", SolQAConstants.appDWP);
        String requestID = dwpFeatureMethod.getSubmittedRequestIDFromPastEvents(SERVICE_NAME);
        log.info("Service Request created ID: " + requestID);

        log.info("Step 13: Open submitted request in past events");
        dwpFeatureMethod.openSubmittedRequestInPastEvents(requestID);
        dwpFeatureMethod.captureScreenshot("DRSQA_15148_RequestOpenedInPastEvents", SolQAConstants.appDWP);
        log.pass("Successfully created service request with ID: " + requestID);

        log.info("Step 14: Select NPS rating and submit");
        String NPS_RATING = dwpTestData.get("NPS_RATING");
        dwpFeatureMethod.selectNPSRating(NPS_RATING);
        dwpFeatureMethod.captureScreenshot("DRSQA_15148_NPSRatingSelected", SolQAConstants.appDWP);
        log.pass("Successfully selected NPS rating: " + NPS_RATING);

        log.info("Step 15: Switch back to DWPC driver");
        setDriver(DWPCdriver);
        context.setAttribute("DRSQA_15148_Driver", getDriver());
        context.setAttribute("DRSQA_15148_Product", Product.Helix_DWPC);
        log.info("Switched to DWPC driver successfully");

        log.info("Step 16: Change rating scale to Customer Satisfaction (CSAT) - 5 point");
        String NEW_RATING_SCALE = dwpcTestData.get("NEW_RATING_SCALE");
        dwpcFeatureMethods.openPostRequestSurveySettings();
        dwpcFeatureMethods.selectRatingScale(NEW_RATING_SCALE);
        dwpcFeatureMethods.handleChangeScaleWarningPopup();
        dwpcFeatureMethods.closeSettings();
        dwpcFeatureMethods.captureScreenshot("DRSQA_15148_RatingScaleChangedToCSAT", SolQAConstants.appDWPC);
        log.pass("Successfully changed rating scale to: " + NEW_RATING_SCALE);

        log.info("--------- DRSQA-15148: Enable_Surveys_To_Display_NPS_Scores *** END");
    }

    @Test(description = "Configure Send Http Request to return more than 2000 characters error message", groups = {"Regression_P3", "DWPC"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-12266", Products = {Product.Helix_DWPC})
    public void DRSQA_12266_Configure_Send_Http_Request_To_Return_More_Than_2000_Characters_ErrorMessage(ITestContext context) throws Exception {

        log.info("--------- DRSQA-12266: Configure_Send_Http_Request_To_Return_More_Than_2000_Characters_ErrorMessage *** START");

        log.info("DRSQA-12266_Pre-requisite Steps: START *********");

        log.info("Step 1: Load DWPC user data and launch application");
        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(DWPCdriver);
        context.setAttribute("DRSQA_12266_Driver", getDriver());
        context.setAttribute("DRSQA_12266_Product", Product.Helix_DWPC);
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();

        log.info("Step 2: Load connector test data");
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_12266_Data");

        log.info("Step 3: Navigate to Connectors page and verify if connector exists delete it .");
        dwpcFeatureMethods.navigateToSubmenu("Services", "Connectors");
        dwpcFeatureMethods.verifyConnectorManagementPageLoaded();
        dwpcFeatureMethods.deleteConnectorIfExists(dwpcTestData.get("CONNECTION_NAME"));
        log.info("Step 4: Create new connector");
        dwpcFeatureMethods.createNewConnector(dwpcTestData.get("CONNECTION_NAME"), dwpcTestData.get("CONNECTOR_TYPE"));
        dwpcFeatureMethods.captureScreenshot("DRSQA_12266_ConnectorCreated", SolQAConstants.appDWPC);

        log.info("Step 5: Navigate to Services page");
        dwpcFeatureMethods.navigateToSubmenu("Services", "Services");
        dwpcFeatureMethods.verifyServicesPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        log.info("Step 6: Import service if not found");
        dwpcFeatureMethods.importServiceIfNotFound(dwpcTestData.get("SERVICE_NAME"), "RESTService");
        dwpcFeatureMethods.captureScreenshot("DRSQA_12266_ServiceSearched", SolQAConstants.appDWPC);

        dwpcFeatureMethods.closeBrowser();
        log.info("Step 7: Login to DWP with bhill end user");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_12266_Driver", getDriver());
        context.setAttribute("DRSQA_12266_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        log.info("Login to DWP with " + dwpUserData.get("UserName") + " and verify its logged in successfully");

        log.info("Step 8: Open Catalog Page and search for service and request the service");
        String SERVICE_NAME = dwpcTestData.get("SERVICE_NAME");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
        } else {
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            dwpFeatureMethod.clickOnRequestNow(SERVICE_NAME);
        }
        log.info("Step 9: Submit the service request");
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.openActivityPage();
        dwpFeatureMethod.captureScreenshot("DRSQA_12266_ServiceRequestCreated", SolQAConstants.appDWP);
        String requestID = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(SERVICE_NAME);
        log.info("Service Request created ID: " + requestID);
        dwpFeatureMethod.openSrmRequestByRequestID(requestID);
        Assert.assertTrue(dwpFeatureMethod.verifyStatusOfService("Fault"), "Status is not Fault");
        log.info("--------- DRSQA-12266: Configure_Send_Http_Request_To_Return_More_Than_2000_Characters_ErrorMessage *** END");
    }

    @Test(description = "DWP - Asset Mgmt : End to End Asset service mapping", groups = {"Regression_P1", "DWP", "DWPC", "hpareek"},singleThreaded = true)
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-15391", Products = {Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_15391_DWP_Asset_Mgmt_End_to_End_Asset_service_mapping(ITestContext context) throws IOException, InterruptedException {

        log.info("***** START: DRSQA_15391_DWP_Asset_Mgmt_End_to_End_Asset_service_mapping *****");

        String originalRequestLifecycleValue = null;

        try {
            // ==================== PRECONDITIONS ====================
            log.info("PRECONDITION: Configure Request Lifecycle to 2 days for immediate closure");

            dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_15391_Data");
            DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(DWPCdriver);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWP);
            dwpcFeatureMethods = new DWPCFeatureMethods();

            log.info("Navigate to Catalog Settings > Request Lifecycle");
            dwpcFeatureMethods.openRequestLifeCycleSettings();

            log.info("Store original Request Lifecycle value");
            originalRequestLifecycleValue = dwpcFeatureMethods.getRequestLifecycleValue();
            log.info("Original Request Lifecycle value: " + originalRequestLifecycleValue);

             log.info("Set Request Lifecycle to 2 days");
             dwpcFeatureMethods.setRequestLifecycleValueDays(dwpTestData.get("REQUEST_LIFECYCLE_DAYS"));
             dwpcFeatureMethods.captureScreenshot("DRSQA_15391_RequestLifecycleSet", SolQAConstants.appDWP);

            int randomFourDigits = ThreadLocalRandom.current().nextInt(1000, 10000);
            String serviceActionName = dwpTestData.get("SERVICE_ACTION_NAME") + randomFourDigits;
            try {
                log.info("Create Service action and map with service");
                dwpcFeatureMethods.createServiceActionAndMapWithService(serviceActionName, dwpTestData.get("SERVICE_NAME"));

                log.info("Create Asset Group");

                dwpcFeatureMethods.createAssetGroup(dwpTestData.get("ASSET_GROUP_NAME"), dwpTestData.get("SERVICE_NAME"));

                log.info("Logout from admin session");
                dwpcFeatureMethods.closeBrowser();
            } catch (Exception e) {
                log.error("DRSQA-15391 precondition (service action / asset group / admin logout) skipped due to: " + e.getMessage(), e);
            }

            // ==================== TEST EXECUTION ====================
            log.info("Step 1: Login to DWP as end user and request service having Asset service mapping configured");
            context.setAttribute("DRSQA_15391_Driver", getDriver());
            context.setAttribute("DRSQA_15391_Product", Product.Helix_DWP);

            DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(DWPCdriver);
            dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "pbunyon");
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWP);        
            dwpFeatureMethod = new DWPFeatureMethods();

            String SERVICE_NAME = dwpTestData.get("SERVICE_NAME");
            log.info("Requesting service: " + SERVICE_NAME);

            dwpFeatureMethod.openCatalogPage();
            dwpFeatureMethod.openAllItemsPage();
            dwpFeatureMethod.searchAndOpenService(SERVICE_NAME);
            dwpFeatureMethod.verifySrdTitle(SERVICE_NAME);

            log.info("Fill service request details");
            dwpFeatureMethod.selectSoftwareRequired(dwpTestData.get("SOFTWARE_LABEL"), dwpTestData.get("SOFTWARE_NAME"));
            dwpFeatureMethod.addMachineNameInService(dwpTestData.get("MACHINE_NAME"));

            log.info("Submit the service request");
            dwpFeatureMethod.clickOnSubmitRequest();
            dwpFeatureMethod.captureScreenshot("DRSQA_15391_ServiceRequestSubmitted", SolQAConstants.appDWP);

            String requestID = dwpFeatureMethod.getSubmittedRequestIDFromPastEvents(SERVICE_NAME);
            Assert.assertFalse(requestID.isEmpty(), "Service Request ID should not be empty");
            log.info("Service Request ID created: " + requestID);

            log.info("Wait for service request to reach Closed status");
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSubmittedRequestInPastEvents(requestID);
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.STATUS_CLOSED);
            dwpFeatureMethod.captureScreenshot("DRSQA_15391_ServiceRequestClosedStatus", SolQAConstants.appDWP);
            dwpFeatureMethod.clickOnBackButton();

            log.info("Step 2: Navigate to My Stuff page");
            dwpFeatureMethod.navigateToMyStuffPage();
            dwpFeatureMethod.captureScreenshot("DRSQA_15391_MyStuffPageOpened", SolQAConstants.appDWP);

            log.info("Verify service request appeared in Asset Group/bucket");
            String suffixSA = dwpTestData.get("SUFFIX_SA");
            String suffixSA1 = dwpTestData.get("SUFFIX_SA1");
            
            // Try with suffixSA first, if not found then try with suffixSA1
            boolean serviceRequestFound = false;
            try {
                log.info("Attempting to verify service request with suffix: " + suffixSA);
                dwpFeatureMethod.verifyServiceRequestInMyStuff(SERVICE_NAME, suffixSA);
                serviceRequestFound = true;
                log.info("Service request found successfully with suffix: " + suffixSA);
            } catch (AssertionError | Exception e) {
                log.info("Service request not found with suffix: " + suffixSA + ". Trying with suffix: " + suffixSA1);
                try {
                    dwpFeatureMethod.verifyServiceRequestInMyStuff(SERVICE_NAME, suffixSA1);
                    serviceRequestFound = true;
                    log.info("Service request found successfully with suffix: " + suffixSA1);
                } catch (AssertionError | Exception e2) {
                    log.error("Service request not found with either suffix: " + suffixSA + " or " + suffixSA1);
                    throw new AssertionError("Service request not found in My Stuff page with either suffix: " + suffixSA + " or " + suffixSA1, e2);
                }
            }
            
            if (serviceRequestFound) {
                dwpFeatureMethod.captureScreenshot("DRSQA_15391_ServiceRequestInMyStuff", SolQAConstants.appDWP);
            }
        
            log.info("Step 3: Click on service carousel on which you want to perform Day2 actions");
            log.info("Step 4: Click on button which Action you want to perform on service.");
            dwpFeatureMethod.clickOnServiceCarouselInMyStuff(SERVICE_NAME, serviceActionName);
            dwpFeatureMethod.captureScreenshot("DRSQA_15391_ServiceCarouselClicked", SolQAConstants.appDWP);

            log.info("Step 5: Write down the Answers and submit the request");
            dwpFeatureMethod.clickOnSubmitRequest();
            dwpFeatureMethod.captureScreenshot("DRSQA_15391_Day2ActionRequestSubmitted", SolQAConstants.appDWP);
            log.info("**************** End DRSQA-15391: Verify Day2 actions on service request from My Stuff page ******************");

        } finally {
            // ==================== CLEANUP ====================
            log.info("CLEANUP: Reverting Request Lifecycle settings to original value");
            try {
                if (originalRequestLifecycleValue != null && dwpFeatureMethod != null) {
                    DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
                    setDriver(DWPCdriver);
                    login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWP);
                    dwpcFeatureMethods = new DWPCFeatureMethods();
                    dwpcFeatureMethods.openRequestLifeCycleSettings();
                    dwpcFeatureMethods.setRequestLifecycleValueDays(originalRequestLifecycleValue);
                    dwpcFeatureMethods.closeSettings();
                    log.info("Request Lifecycle reverted to: " + originalRequestLifecycleValue);
                    dwpFeatureMethod.captureScreenshot("DRSQA_15391_RequestLifecycleReverted", SolQAConstants.appDWP);
                }
            } catch (Exception e) {
                log.error("Error during cleanup: " + e.getMessage());
            }
        }

        log.info("***** END: DRSQA_15391_DWP_Asset_Mgmt_End_to_End_Asset_service_mapping *****");
    }

    @Test(description = "Map fulfillment priority in DWPC Requests", groups = {"Regression_P1"}, singleThreaded = true)
    @SolqaDetails(authorName = "aramulwa", TTID = "20312", JiraID = "DRSQA-11721", Products = {Product.Helix_DWP, Product.Helix_ITSM, Product.Helix_DWPC})
    public void DRSQA_11721_MapFulfillmentPriorityInDWPCRequests(ITestContext context) {
        log.info("--------- DRSQA-11721: Map fulfillment priority in DWPC Requests *** START");

        String requestId;
        String fulfillmentId;
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPTestData, "DRSQA_11721_Data");

        try {
            log.info("Precondition: Enable Request priority toggle in DWPC settings");
            dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCUsers, "Liam_User");
            DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(DWPCdriver);
            context.setAttribute("DRSQA_11721_Driver", DWPCdriver);
            context.setAttribute("DRSQA_11721_Product", Product.Helix_DWPC);
            login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
            dwpcFeatureMethods = new DWPCFeatureMethods();
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
            dwpcFeatureMethods.enableRequestPriority(true);
            dwpcFeatureMethods.closeBrowser();

            log.info("Step 1: Login to DWP and raise SB approval service request");
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "Fritz");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11721_Driver", dwpDriver);
            context.setAttribute("DRSQA_11721_Product", Product.Helix_DWP);
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.verifyDWPHomePageOpened();
            String serviceName = dwpTestData.get("serviceName");
            dwpFeatureMethod.openCatalogPage();
            if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
                dwpFeatureMethod.openAllItemsPage();
            }
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("summaryLabel"), dwpTestData.get("summaryValue"));
            dwpFeatureMethod.clickOnSubmitRequest();
            dwpFeatureMethod.openActivityPage();
            requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
            dwpFeatureMethod.openSrmRequestByRequestID(requestId);

            log.info("Step 2: Approver approves the request");
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "friedaUser");
            dwpDriver2 = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver2);
            context.setAttribute("DRSQA_11721_Driver", dwpDriver2);
            context.setAttribute("DRSQA_11721_Product", Product.Helix_DWP);
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod2 = new DWPFeatureMethods();
            dwpFeatureMethod2.verifyDWPHomePageOpened();
            dwpFeatureMethod2.openActivityPage();
            dwpFeatureMethod2.openSrmRequestByRequestID(requestId);
            dwpFeatureMethod2.clickApproveRequestInEventDetails();
            dwpFeatureMethod2.closeBrowser();

            log.info("Step 3-4: Requester verifies approval and Request priority on DWP");
            setDriver(dwpDriver);
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.openSrmRequestByRequestID(requestId);
            fulfillmentId = dwpFeatureMethod.getFulfillmentID();
            dwpFeatureMethod.verifyApprovalStatusFromDetails(SolQAConstants.IN_PROGRESS);
            dwpFeatureMethod.verifyHeadlineItemValueWithRetry(SolQAConstants.Request_Priority_Label, SolQAConstants.P4_Request_Priority, 300);

            log.info("Step 5: Verify priority in DWPC reports");
            DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(DWPCdriver);
            context.setAttribute("DRSQA_11721_Driver", DWPCdriver);
            context.setAttribute("DRSQA_11721_Product", Product.Helix_DWPC);
            dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, SolQAConstants.DWPCUsers, "Liam_User");
            login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
            dwpcFeatureMethods = new DWPCFeatureMethods();
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
            String dwpcPriorityAfterApproval = dwpcFeatureMethods.getServiceRequestPriorityFromReports(
                    dwpTestData.get("dwpcReportsMenu"),
                    dwpTestData.get("dwpcServiceRequestsSubMenu"),
                    requestId,
                    dwpTestData.get("dwpcPriorityColumn"));
            Assert.assertEquals(dwpcPriorityAfterApproval.toUpperCase(), dwpTestData.get("dwpcExpectedPriorityAfterApproval"), "DWP and DWPC request priority are not matched after approval");

            log.info("Step 6-7: Update Work Order priority in ITSM for fulfillment: " + fulfillmentId);
            itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
            setDriver(itsmDriver);
            context.setAttribute("DRSQA_11721_Driver", itsmDriver);
            context.setAttribute("DRSQA_11721_Product", Product.Helix_ITSM);
            itsmFeatureMethods = new ITSMFeatureMethods();
            itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, SolQAConstants.ITSMUsers, "pbunyon");
            login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);
            itsmFeatureMethods.verifyITSMLogin();
            itsmFeatureMethods.navigateMenu(SolQAConstants.SERVICE_REQUEST_MANAGEMENT, SolQAConstants.SEARCH_WORK_ORDER);
            itsmFeatureMethods.searchWorkOrder(fulfillmentId);
            itsmFeatureMethods.updateWorkOrderPriority(dwpTestData.get("itsmUpdatedWorkOrderPriority"));
            itsmFeatureMethods.closeBrowser();

            log.info("Step 8: Verify updated Request priority in DWP and DWPC");
            setDriver(dwpDriver);
            dwpFeatureMethod.refreshBrowser();
            dwpFeatureMethod.verifyHeadlineItemValueWithRetry(dwpTestData.get("requestPriorityLabel"), dwpTestData.get("expectedPriorityAfterWOUpdate"), 300);
            dwpFeatureMethod.closeBrowser();

            setDriver(DWPCdriver);
            String dwpcPriorityAfterUpdate = dwpcFeatureMethods.getServiceRequestPriorityFromReports(dwpTestData.get("dwpcReportsMenu"), dwpTestData.get("dwpcServiceRequestsSubMenu"), requestId, dwpTestData.get("dwpcPriorityColumn"));
            Assert.assertEquals(dwpcPriorityAfterUpdate.toUpperCase(), dwpTestData.get("dwpcExpectedPriorityAfterWOUpdate").toUpperCase(), "DWP and DWPC request priority are not matched after ITSM update");
            dwpcFeatureMethods.closeBrowser();
            log.info("--------- DRSQA-11721: Map fulfillment priority in DWPC Requests *** END");

        } finally {
            try {
                log.info("Cleanup: Disable Request priority toggle in DWPC settings");
                dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
                DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
                setDriver(DWPCdriver);
                login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
                dwpcFeatureMethods = new DWPCFeatureMethods();
                dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
                dwpcFeatureMethods.enableRequestPriority(false);
            } catch (Exception e) {
                log.warn("Failed to disable Request priority toggle in DWPC during cleanup: " + e.getMessage());
            } finally {
                if (dwpcFeatureMethods != null) {
                    dwpcFeatureMethods.closeBrowser();
                }
            }
        }
    }

    @Test(description = "Ability to retry failed service requests", groups = {"Regression_P2", "DWP"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-11578", Products = {Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_11578_RetryFailedServiceRequest(ITestContext context) throws Exception {
        log.info("--------- DRSQA-11578: RetryFailedServiceRequest *** START");

        DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(DWPCdriver);
        context.setAttribute("DRSQA-11578_Driver", DWPCdriver);
        context.setAttribute("DRSQA-11578_Product", Product.Helix_DWPC);
        log.info("Step 1: Login to DWPC and update work order template with wrong expression to make request fail");

        log.info("1.1: Login to DWPC with admin user: " + configProp.get("adminUserName"));
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();

        log.info("1.2: Navigate To Services Page ");
        dwpcFeatureMethods.navigateToSubmenu("Services", "Services");

        log.info("1.3: verify SB_Approval service is available or not, if not available import the service");
        dwpcFeatureMethods.importServiceIfNotFound("SB_Approval service", "SB_ApprovalService");

        log.info("1.4: Search and Open the service SB_Approval service");
        dwpcFeatureMethods.openServiceOption("SB_Approval service");

        log.info("1.5: Update service workflow with wrong expression to make the request fail");
        dwpcFeatureMethods.updateWorkOrderTemplateExpression("Install Projector_Wrong");
        dwpcFeatureMethods.closeBrowser();

        try {
            log.info("Step 2: Login to DWP as requester and submit SB Approval service");
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");
            dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11578_Data");
            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11578_DWP_Requester_Driver", dwpDriver);
            context.setAttribute("DRSQA_11578_DWP_Product", Product.Helix_DWP);

            log.info("2.1: Login to DWP with user: " + dwpUserData.get("UserName"));
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

            dwpFeatureMethod = new DWPFeatureMethods();
            String serviceName = dwpTestData.get("serviceName");

            log.info("2.2: Open Catalog page, search and submit service request for: " + serviceName);
            dwpFeatureMethod.openCatalogPage();
            if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
                dwpFeatureMethod.openAllItemsPage();
            }
            dwpFeatureMethod.searchAndOpenService(serviceName);
            dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("summaryLabel"), dwpTestData.get("summaryValue"));
            dwpFeatureMethod.clickOnSubmitRequest();
            dwpFeatureMethod.captureScreenshot("DRSQA_11578_requestSubmitted", SolQAConstants.appDWP);

            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.refreshBrowser();
            String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceName);
            Assert.assertNotNull(requestId, "Request ID is null");
            Assert.assertFalse(requestId.trim().isEmpty(), "Service request is not created in DWP");
            log.info("2.3: Submitted Request ID: " + requestId);

            log.info("2.4: Open submitted request and verify the request is in waiting approval status");
            dwpFeatureMethod.openSrmRequestByRequestID(requestId);
            dwpFeatureMethod.verifyServiceRequestStatus(dwpTestData.get("waitingApprovalStatus"));

            log.info("Step 3: Login to DWP as approver and approve request");
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "fcatUser");
            dwpDriver2 = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver2);
            context.setAttribute("DRSQA-11578_Driver", dwpDriver2);
            context.setAttribute("DRSQA-11578_Product", Product.Helix_DWP);
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

            dwpFeatureMethod2 = new DWPFeatureMethods();
            dwpFeatureMethod2.openActivityPage();
            dwpFeatureMethod2.openSrmRequestByRequestID(requestId);
            dwpFeatureMethod2.clickApproveRequestInEventDetails();

            log.info("Step 4: Verify request status changes to Fault after approval");
            setDriver(dwpDriver);
            dwpFeatureMethod = new DWPFeatureMethods();
            dwpFeatureMethod.refreshBrowser();
            dwpFeatureMethod.verifyServiceRequestStatus("Fault");
            dwpFeatureMethod.captureScreenshot("DRSQA_11578_requestInProgress", SolQAConstants.appDWP);

            log.info("Step 5: Login to DWPC and fix workflow expression, then retry failed service process");
            DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(DWPCdriver);
            context.setAttribute("DRSQA-11578_Driver", DWPCdriver);
            context.setAttribute("DRSQA-11578_Product", Product.Helix_DWPC);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWPC);
            dwpcFeatureMethods = new DWPCFeatureMethods();
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
            dwpcFeatureMethods.navigateToSubmenu("Services", "Services");
            dwpcFeatureMethods.openServiceOption("SB_Approval service");
            dwpcFeatureMethods.updateWorkOrderTemplateExpression("Install Projector");
            dwpcFeatureMethods.refreshBrowser();
            dwpcFeatureMethods.openServiceRequestsReports();
            dwpcFeatureMethods.restartFailedServiceProcess(requestId);
            dwpcFeatureMethods.closeBrowser();

            log.info("Step 6: Re-approve retried request and verify status moves to In Progress");
            setDriver(dwpDriver2);
            dwpFeatureMethod.refreshBrowser();
            dwpFeatureMethod2.openActivityPage();
            dwpFeatureMethod2.openSrmRequestByRequestID(requestId);
            dwpFeatureMethod2.clickApproveRequestInEventDetails();
            dwpFeatureMethod2.closeBrowser();

            setDriver(dwpDriver);
            dwpFeatureMethod.openActivityPage();
            dwpFeatureMethod.refreshBrowser();
            dwpFeatureMethod.openSrmRequestByRequestID(requestId);
            dwpFeatureMethod.verifyServiceRequestStatus("In Progress");
            dwpFeatureMethod.closeBrowser();
        } finally {
            log.info("Cleanup: START - Restore default work order template expression in DWPC");
            DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(DWPCdriver);
            context.setAttribute("DRSQA-11578_Driver", DWPCdriver);
            context.setAttribute("DRSQA-11578_Product", Product.Helix_DWPC);
            login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appDWPC);
            dwpcFeatureMethods = new DWPCFeatureMethods();
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
            dwpcFeatureMethods.navigateToSubmenu("Services", "Services");
            dwpcFeatureMethods.openServiceOption("SB_Approval service");
            dwpcFeatureMethods.updateWorkOrderTemplateExpression("Install Projector");
            log.info("Cleanup: Restored work order template expression to Install Projector");
            dwpcFeatureMethods.closeBrowser();
        }
        log.info("--------- DRSQA-11578: RetryFailedServiceRequest *** END");

    }

    @Test(description = "DWPA Collaboration - allow collaborators to add other collaborators.", groups = {"Regression_P1"})
    @SolqaDetails(authorName = "aramulwa", TTID = "20686", JiraID = "DRSQA-11772", Products = {Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_11772_Allow_Collaborators_To_Add_Other_Collaborators(ITestContext context) {

        String requestId;

        try {
            log.info("-------------------- Preconditions in DWPC: Enable Collaborator delegation --------------------");
            dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11772_service");
            dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");

            DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
            setDriver(DWPCdriver);
            context.setAttribute("DRSQA_11772_DWPC_Driver", getDriver());
            context.setAttribute("DRSQA_11772_Product", Product.Helix_DWPC);

            login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
            dwpcFeatureMethods = new DWPCFeatureMethods();
            dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();

            dwpcFeatureMethods.setCollaborationDelegation(dwpcTestData.get("serviceName"), dwpcTestData.get("ServiceOptionName"), true);
            dwpcFeatureMethods.closeBrowser();

            log.info("-------------------- DWP: Raise request as end user and add collaborator + delegate collaborators --------------------");
            dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11772_Data");
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "bhillUser");

            dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver);
            context.setAttribute("DRSQA_11772_DWP_Driver", getDriver());
            context.setAttribute("DRSQA_11772_Product", Product.Helix_DWP);

            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod = new DWPFeatureMethods();

            String serviceRequest = dwpTestData.get("serviceName");
            dwpFeatureMethod.openCatalogPage();
            if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
                dwpFeatureMethod.openAllItemsPage();
            }
            dwpFeatureMethod.searchAndOpenService(serviceRequest);
            dwpFeatureMethod.fillTextFieldByLabel(SolQAConstants.APPROVAL_SUMMARY, dwpTestData.get("ApprovalSummary"));
            dwpFeatureMethod.clickOnSubmitRequest();

            dwpFeatureMethod.openActivityPage();
            requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceRequest);
            dwpFeatureMethod.openSrmRequestByRequestID(requestId);
            dwpFeatureMethod.verifyServiceRequestStatus(SolQAConstants.WAITING_APPROVAL);

            Assert.assertFalse(requestId.isEmpty(), "SR is not created");
            log.info("----------------- DRSQA-11772 created ID: " + requestId);

            dwpFeatureMethod.addRemoveCollaborators_DWP(dwpTestData.get("collaboratorName1"));
            dwpFeatureMethod.clickDelegationCollaboratorsOption();
            dwpFeatureMethod.addDelegationCollaborator(dwpTestData.get("delegationCollaboratorName"));
            dwpFeatureMethod.verifySelectedDelegationCollaborator(dwpTestData.get("delegationCollaboratorName"));
            dwpFeatureMethod.closeBrowser();

            log.info("-------------------- DWP: Login as collaborator (no delegation rights) and verify cannot update collaborators --------------------");
            dwpDriver2 = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver2);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "MosesUser");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod2 = new DWPFeatureMethods();
            dwpFeatureMethod2.openActivityPage();
            dwpFeatureMethod2.openSrmRequestByRequestID(requestId);
            log.info("---- Collaborator user is unable to add or update the collaborators list ------------");
            Assert.assertFalse(dwpFeatureMethod2.isCollaboratorOptionAvailable(), "Collaborator dropdown option is available");
            dwpFeatureMethod2.closeBrowser();

            log.info("-------------------- DWP: Login as delegated collaborator and verify can add other collaborator --------------------");
            dwpDriver2 = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
            setDriver(dwpDriver2);
            dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
            login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
            dwpFeatureMethod2 = new DWPFeatureMethods();
            dwpFeatureMethod2.openActivityPage();
            dwpFeatureMethod2.verifyRequestDisplaysInActiveEventsWithStatus(requestId, dwpTestData.get("sharedByText"));
            dwpFeatureMethod2.openSrmRequestByRequestID(requestId);

            log.info("---- Collaborator user is able to add or update the collaborators list (delegation enabled) ------------");
            Assert.assertTrue(dwpFeatureMethod2.isCollaboratorOptionAvailable(), "Collaborator dropdown option is not available");
            dwpFeatureMethod2.addRemoveCollaborators_DWP(dwpTestData.get("collaboratorName2"));
            dwpFeatureMethod2.verifySelectedCollaboratorInDetailsPage(dwpTestData.get("collaboratorName2"));
            dwpFeatureMethod2.closeBrowser();

        } finally {
            // -------------------- Cleanup in DWPC: Disable Collaborator delegation --------------------
            try {
                dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11772_service");
                dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");

                DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
                setDriver(DWPCdriver);
                login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
                dwpcFeatureMethods = new DWPCFeatureMethods();
                dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
                dwpcFeatureMethods.setCollaborationDelegation(dwpcTestData.get("serviceName"), dwpcTestData.get("ServiceOptionName"), false);
            } finally {
                if (dwpcFeatureMethods != null) {
                    dwpcFeatureMethods.closeBrowser();
                }
            }
        }
    }

    //Previous owner aramulwa
    @Test(description = "Catalog Request selection from Service Health Actions.", groups = {"Regression_P1", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", JiraID = "DRSQA-11656", Products = {Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_11656_Catalog_Request_selection_from_Service_Health_Actions(ITestContext context) throws Exception {

        log.info("--------- DRSQA-11656: Catalog Request selection from Service Health Actions *** START");

        // Load product-scoped test data
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11656_service");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11656_service");

        // Make names unique to avoid collisions in shared tenants
        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String actionName = dwpTestData.get("ServiceActionNamePrefix") + suffix;
        String serviceNameInAvailability = dwpTestData.get("ServiceNameInAvailabilityPrefix") + suffix;

        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(DWPCdriver);
        context.setAttribute("DRSQA_11656_DWPC_Driver", getDriver());
        context.setAttribute("DRSQA_11656_DWPC_Product", Product.Helix_DWPC);
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        dwpcFeatureMethods.importServiceIfNotFound(dwpcTestData.get("serviceName"), dwpcTestData.get("ImportServiceName"));
        dwpcFeatureMethods.importServiceIfNotFound(dwpcTestData.get("BundleName"), dwpcTestData.get("ImportBundleName"));
        dwpcFeatureMethods.closeBrowser();

        // -------------------- Step 1: Login to DWP Admin and enable Service Health + create Service Action + Service Availability --------------------
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "liamUser");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11656_DWP_Admin_Driver", getDriver());
        context.setAttribute("DRSQA_11656_DWP_Admin_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.verifyLoginToDWPAdmin();
        dwpFeatureMethod.clickOnHamburgerMenu();

        // Enable Service Health feature (Feature Settings)
        dwpFeatureMethod.clickOnMenu(dwpTestData.get("AdminMenuConfiguration"));
        dwpFeatureMethod.clickOnSubMenu("Application Features");
        dwpFeatureMethod.enableFeatureSettingByName(dwpTestData.get("FeatureSettingName"), true);

        // Create Service Action (Catalog request)
        dwpFeatureMethod.clickOnHamburgerMenu();
        dwpFeatureMethod.clickOnMenu(dwpTestData.get("AdminMenuServices"));
        dwpFeatureMethod.clickOnSubMenu(dwpTestData.get("AdminSubMenuServiceActions"));
        dwpFeatureMethod.createServiceAction_CatalogRequest(dwpTestData.get("ServiceActionType"), actionName, dwpTestData.get("RequestValue"));

        // Create Service Availability and attach the action
        dwpFeatureMethod.clickOnSubMenu(dwpTestData.get("AdminSubMenuServiceAvailability"));
        dwpFeatureMethod.createServiceAvailabilityWithAction(serviceNameInAvailability, actionName);
        dwpFeatureMethod.closeBrowser();

        // -------------------- Step 2: Login to DWP as end user and submit request from Service Health action --------------------
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPUsers", "Fritz");
        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11656_DWP_EndUser_Driver", getDriver());
        context.setAttribute("DRSQA_11656_DWP_EndUser_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();

        dwpFeatureMethod.openMyStuffPage()
                .openServiceHealthTab(dwpTestData.get("MyStuffServiceHealthTab"))
                .removeAllFiltersOnServiceHealthIfPresent()
                .searchServiceHealthItem(serviceNameInAvailability)
                .openServiceHealthActionFromKebab(serviceNameInAvailability, actionName);

        dwpFeatureMethod.fillTextAreaByLabel(dwpTestData.get("TextAreaQuestionLabel"), dwpTestData.get("Summary"));
        dwpFeatureMethod.fillDropdownField(dwpTestData.get("DropdownQuestionLabel"), dwpTestData.get("DropdownAnswer"));
        dwpFeatureMethod.clickOnSubmitRequest();
        dwpFeatureMethod.captureScreenshot("DRSQA_11656_Submit_Request", SolQAConstants.appDWP);

        // -------------------- Step 3: Verify request is created and contains the Service Availability name in comments/details --------------------
        dwpFeatureMethod.openActivityPage();
        String requestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(dwpTestData.get("RequestValue"));
        Assert.assertTrue(requestId != null && !requestId.trim().isEmpty(), "Request ID is not created from Service Health action");
        dwpFeatureMethod.openSrmRequestByRequestID(requestId);
        dwpFeatureMethod.verifyComment(serviceNameInAvailability);

        dwpFeatureMethod.closeBrowser();
        log.info("--------- DRSQA-11656: Catalog Request selection from Service Health Actions *** END");
    }

    //previous owner preethi
    @Test(description = "Request 360: Ability to select OOTB and custom fields from PWA views in DWP Request details.", groups = {"Regression_P1", "DWP", "DWPC", "sonalsha"})
    @SolqaDetails(authorName = "sonalsha", TTID = "20295", JiraID = "DRSQA-11714", Products = {Product.Helix_DWP, Product.Helix_DWPC})
    public void DRSQA_11714_Request_360_Ability_To_Select_OOTB_Custom_Fields_In_DWP(ITestContext context) {

        log.info("-------- START: DRSQA_11714 - Request 360 additional fields in DWP request details --------");

        // Optional (commented): DWPC service availability precondition — enable when running full DWPC + DWP flow
        dwpcTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCTestData", "DRSQA_11714_service_info");
        String serviceName = dwpcTestData.get("serviceName");
        dwpcUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWPC, "DWPCUsers", "Liam_User");
        DWPCdriver = launchWeb(SolQAConstants.appDWPC, SolQAConstants.appDWPC);
        setDriver(DWPCdriver);
        context.setAttribute("DRSQA_11714_DWPC_Driver", getDriver());
        context.setAttribute("DRSQA_11714_DWPC_Product", Product.Helix_DWPC);
        login(dwpcUserData.get("UserName"), dwpcUserData.get("Password"), SolQAConstants.appDWPC);
        dwpcFeatureMethods = new DWPCFeatureMethods();
        dwpcFeatureMethods.verifyDWPCPageLaunchedSuccessfully();
        dwpcFeatureMethods.openServicesPage();
        Assert.assertTrue(dwpcFeatureMethods.isServicePresent(serviceName), "Service not present in DWPC: " + serviceName);
        dwpcFeatureMethods.closeBrowser();

        log.info("DRSQA_11714 - Step 1/18: Load test data, launch DWP, sign in as DWP admin");
        dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11714_TestData");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "liamUser");

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11714_DWP_Admin_Driver", getDriver());
        context.setAttribute("DRSQA_11714_DWP_Admin_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

        log.info("DRSQA_11714 - Step 2/18: Open Administration > Configuration > Fulfillment Details");
        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.verifyDWPHomePageOpened();
        dwpFeatureMethod.navigateToAdminPage();
        dwpFeatureMethod.navigateToConfiguration();

        dwpFeatureMethod.clickOnSubMenu(SolQAConstants.Fulfillment_Details);

        log.info("DRSQA_11714 - Step 3/18: Enable fulfillment details switches (My Activity + Request Details view)");
        dwpFeatureMethod.enableFulfillmentDetailsSwitches();

        log.info("DRSQA_11714 - Step 4/18: Change requests — select all additional fields and verify max-fields error");
        dwpFeatureMethod.selectAllValuesAdditionalFields(SolQAConstants.CHANGE_REQUEST_TYPE);
        dwpFeatureMethod.verifyAdditionalFieldLimitError(SolQAConstants.CHANGE_REQUEST_TYPE);

        log.info("DRSQA_11714 - Step 5/18: Incident — select all additional fields and verify max-fields error");
        dwpFeatureMethod.selectAllValuesAdditionalFields(SolQAConstants.INCIDENT_REQUEST_TYPE);
        dwpFeatureMethod.verifyAdditionalFieldLimitError(SolQAConstants.INCIDENT_REQUEST_TYPE);

        log.info("DRSQA_11714 - Step 6/18: Work order — select all additional fields and verify max-fields error");
        dwpFeatureMethod.selectAllValuesAdditionalFields(SolQAConstants.WORKORDER_REQUEST_TYPE);
        dwpFeatureMethod.verifyAdditionalFieldLimitError(SolQAConstants.WORKORDER_REQUEST_TYPE);

        log.info("DRSQA_11714 - Step 7/18: Change requests — add limited additional fields from test data");
        dwpFeatureMethod.selectAdditionalFieldsFulfillmentDetails(
                SolQAConstants.CHANGE_REQUEST_TYPE, dwpTestData.get("FulfillmentDetailsLimitedFieldsChange"));

        log.info("DRSQA_11714 - Step 8/18: Incident — add limited additional fields from test data");
        dwpFeatureMethod.selectAdditionalFieldsFulfillmentDetails(
                SolQAConstants.INCIDENT_REQUEST_TYPE, dwpTestData.get("FulfillmentDetailsLimitedFieldsIncident"));

        log.info("DRSQA_11714 - Step 9/18: Work order — add limited additional fields from test data");
        dwpFeatureMethod.selectAdditionalFieldsFulfillmentDetails(
                SolQAConstants.WORKORDER_REQUEST_TYPE, dwpTestData.get("FulfillmentDetailsLimitedFieldsWorkOrder"));

        log.info("DRSQA_11714 - Step 10/18: Save fulfillment details settings");
        dwpFeatureMethod.saveFulfillmentDetailsSettings();

        log.info("DRSQA_11714 - Step 11/18: Close DWP admin session");
        dwpFeatureMethod.closeBrowser();

        log.info("DRSQA_11714 - Step 12/18: Launch DWP and sign in as end user (requester)");
        dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, SolQAConstants.DWPUsers, "bhillUser");

        dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
        setDriver(dwpDriver);
        context.setAttribute("DRSQA_11714_DWP_Requester_Driver", getDriver());
        context.setAttribute("DRSQA_11714_DWP_Requester_Product", Product.Helix_DWP);
        login(dwpUserData.get("UserName"), dwpUserData.get("Password"), SolQAConstants.appDWP);

        dwpFeatureMethod = new DWPFeatureMethods();
        dwpFeatureMethod.closeBurgerMenuIfPresentDWP();
        dwpFeatureMethod.verifyLogin();            

        String serviceRequestName = dwpTestData.get("ServiceRequestName");
        dwpFeatureMethod.openCatalogPage();
        if (dwpFeatureMethod.isShowAllItemsLinkVisible()) {
            dwpFeatureMethod.openAllItemsPage();
        }
        dwpFeatureMethod.searchAndOpenService(serviceRequestName);
        dwpFeatureMethod.verifySrdTitle(serviceRequestName);
        dwpFeatureMethod.fillTextFieldByLabel(dwpTestData.get("SummaryLabel"), dwpTestData.get("SummaryValue"));
        dwpFeatureMethod.clickOnSubmitRequest();

        log.info("DRSQA_11714 - Step 13/18: Open My Activity and read submitted request ID");
        dwpFeatureMethod.openActivityPage();
        String dwpRequestId = dwpFeatureMethod.getSubmittedRequestIDFromActiveEvents(serviceRequestName);
        Assert.assertNotNull(dwpRequestId, "DWP Request ID is null");
        Assert.assertFalse(dwpRequestId.trim().isEmpty(), "DWP Request ID is not created");

        log.info("DRSQA_11714 - Step 14/18: Open request details and wait for Fulfillment section");
        dwpFeatureMethod.openSrmRequestByRequestID(dwpRequestId);
        dwpFeatureMethod.waitForFulfillmentSectionToDisplay();

        log.info("DRSQA_11714 - Step 15/18: Change (CRQ) — fulfillment details, summary, and configured additional fields");
        dwpFeatureMethod.openFulfillmentDetailsByPrefix("CRQ");
        dwpFeatureMethod.verifyFulfillmentDetailsSummaryNotBlank();
        dwpFeatureMethod.verifyFulfillmentDetailsFields(dwpTestData.get("FulfillmentDetailsLimitedFieldsChange"));
        dwpFeatureMethod.closeFulfillmentDetailsModalIfPresent();

        log.info("DRSQA_11714 - Step 16/18: Incident (INC) — fulfillment details, summary, and configured additional fields");
        dwpFeatureMethod.openFulfillmentDetailsByPrefix("INC");
        dwpFeatureMethod.verifyFulfillmentDetailsSummaryNotBlank();
        dwpFeatureMethod.verifyFulfillmentDetailsFields(dwpTestData.get("FulfillmentDetailsLimitedFieldsIncident"));
        dwpFeatureMethod.closeFulfillmentDetailsModalIfPresent();

        log.info("DRSQA_11714 - Step 17/18: Work order (WO) — fulfillment details, summary, and configured additional fields");
        dwpFeatureMethod.openFulfillmentDetailsByPrefix("WO");
        dwpFeatureMethod.verifyFulfillmentDetailsSummaryNotBlank();
        dwpFeatureMethod.verifyFulfillmentDetailsFields(dwpTestData.get("FulfillmentDetailsLimitedFieldsWorkOrder"));
        dwpFeatureMethod.closeFulfillmentDetailsModalIfPresent();

        log.info("DRSQA_11714 - Step 18/18: Close DWP requester session");
        dwpFeatureMethod.closeBrowser();
        log.info("-------- END: DRSQA_11714 - Request 360 additional fields in DWP request details --------");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (dwpcFeatureMethods != null) {
            dwpcFeatureMethods.closeBrowser();
        }
        if (dwpFeatureMethod != null) {
            dwpFeatureMethod.closeBrowser();
        }
        if (dwpFeatureMethod2 != null) {
            dwpFeatureMethod2.closeBrowser();
        }
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
    }

}