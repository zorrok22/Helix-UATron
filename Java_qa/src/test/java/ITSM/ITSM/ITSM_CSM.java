package ITSM.ITSM;

import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import framework.BasePage;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITSM.InnovationSuite.CSMBundle;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import org.openqa.selenium.WebDriver;

public class ITSM_CSM extends BasePage {

    static SolqaLogger log = new SolqaLogger(ITSM_CSM.class.getName());
    ReadJson readJson = new ReadJson();
    Map<Object, String> ITSM_Data;
    ITSMFeatureMethods itsmFeatureMethods;
    DashboadFeatureMethods helixDashboardFeatureMethods;
    WebDriver ITSMdriver;
    CSMBundle csmBundle;  
    List<String> folderNames;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;

    @Test(description = "Authorized User Login and Session Initiation for Resolver Workspace", groups = {"PartnerApps", "Innovation_Suite", "ITSM"})
    @SolqaDetails(authorName = "hpareek", JiraID = "DRSQA-12547",Products = {Product.Innovation_Suite})
    public void DRSQA_12547_Authorized_User_Login_and_Session_Initiation_for_Resolver_Workspace(ITestContext context) {

        log.info("**************** Start DRSQA_12547 Authorized User Login and Session Initiation for Resolver Workspace ******************");

        log.info("****** Step 1: Login with incorrect credentials **********");
        // Use existing WebDriver from ThreadLocal
        ITSMdriver = launchWeb(SolQAConstants.CSM_BUNDLE, SolQAConstants.appIS);
        setDriver(ITSMdriver);
        
        // Initialize page objects AFTER driver is set up
        csmBundle = new CSMBundle();
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
        
        ITSM_Data = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "CSMUsers");

        context.setAttribute("DRSQA_12547_Driver", getDriver());
        context.setAttribute("DRSQA_12547_Product", Product.Innovation_Suite);
        login(ITSM_Data.get("IncorrectUserName"), ITSM_Data.get("IncorrectPassword"), SolQAConstants.appIS);
        log.info("****** Step 2: Verify login failed **********");
        Assert.assertTrue(innovationSuiteFeatureMethodsPage.verifyCSMLoginFailed(), "Login failed as expected");
        
        log.info("****** Step 3: Login with correct credentials ******");
        login(ITSM_Data.get("CSMUserName"), ITSM_Data.get("CSMPassword"), SolQAConstants.appIS);
        log.info("****** Step 4: Verify login success **********");
        innovationSuiteFeatureMethodsPage.verifyCSMLoginSuccess();
        log.info("**************** End DRSQA_12547 Authorized User Login and Session Initiation for Resolver Workspace ******************");

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (helixDashboardFeatureMethods != null) {
            helixDashboardFeatureMethods.closeBrowser();
        }
    }
}