package ITOM.HelixRemediate;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import framework.BasePage;
import pages.ITOM.Intelligent_Automation.*;
import util.*;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.BHAC.BHACFeatureMethods;
import pages.ITOM.BHAC.BHACLandingPage.BHACTab;
import java.io.File;
import java.util.Map;

public class HelixRemediateTests extends BasePage {

    private static SolqaLogger log = new SolqaLogger(HelixRemediateTests.class.getName());
    public static String app = "ITOM";
    private static final String ADE_LOGIN_USER_KEY = "pbunyon";

    CommonWaits commonWaits;
    CommonMethods commonMethods;

    WebDriver driver;
    IAFeatureMethod IAFeatureMethod;
    AdePortalLandingPage adePortalLandingPagePage;
    BHACFeatureMethods bhacFeatureMethods;
    AIOpsFeatureMethods aiopsFeatureMethods;
    ReadJson readJson = new ReadJson();
    Map<Object, String> iaTestData;
    Map<Object, String> bhacTestData;

    public void doPreconditions() {
        driver = this.launchWeb(app, app);
        setDriver(driver);
        Map<Object, String> adeUserData = readJson.getInputValuesMapIncludingLabelField(app, "ITOM_User_Data", ADE_LOGIN_USER_KEY);
        login(adeUserData.get("UserName"), adeUserData.get("Password"), app);
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
        IAFeatureMethod = new IAFeatureMethod();
        adePortalLandingPagePage = new AdePortalLandingPage(driver);
        bhacFeatureMethods = new BHACFeatureMethods();
        aiopsFeatureMethods = new AIOpsFeatureMethods();
    }

    @Test(description = "[VulnOps]:Verify Sync Ansible actions in IA.",groups={"Regression","VulnOps","HelixRemediate"})
    @SolqaDetails(authorName = "hpareek",  JiraID = "DRSQA-17463", Products = {Product.intelligent_automation,Product.VulnOps})
    public void DRSQA_17463_VulnOps_Sync_Ansible_actions_in_IA(ITestContext context) throws Exception
    {

       log.info("------- START: DRSQA_17463_VulnOps_Sync_Ansible_actions_in_IA ------------");
       doPreconditions();
       context.setAttribute("IAFeatureMethod", IAFeatureMethod);
       iaTestData = readJson.getInputValuesMapIncludingLabelField(app, "BHACTestData", "DRSQA_17463_Test_Data");
       
       log.info("Step 1 : Navigate to Intelligent Automation and verify Page is open.");
       IAFeatureMethod.navigateToIA();
       
       log.info("Step 2 :   Navigate to Connectors.");
       IAFeatureMethod.navigateToConnectorPage();

       log.info("Step 3 : Check if Ansible connector is present.");
       Assert.assertTrue(IAFeatureMethod.isConnectorPresentByNameContains(iaTestData.get("ConnectorName")), "Connector is not present.");
    
       log.info("Step 4 : Click on Edit for Ansible connector.");
       IAFeatureMethod.clickEditConnector(iaTestData.get("ConnectorName"));

       log.info("Step 5 : Click on View actions button.");
       IAFeatureMethod.clickViewActions();

       log.info("Step 6 : Disable Search Online toggle.");
       IAFeatureMethod.setSearchOnlineToggle(false);
       
       log.info("Step 7 : Click on Sync Actions");
       IAFeatureMethod.clickSyncActionsLink();

        log.info("Step 8 : Verify sync was successfull.");
        Assert.assertTrue(IAFeatureMethod.isSyncSuccessfull(), "Sync was not successful.");

        log.info("Step 9 : Enable Search Online toggle.");
        IAFeatureMethod.setSearchOnlineToggle(true);

        log.info("Step 10 : Click on Sync Actions");
        IAFeatureMethod.clickSyncActionsLink();
 
        log.info("Step 11 : Verify sync was successfull.");
        Assert.assertTrue(IAFeatureMethod.isSyncSuccessfull(), "Sync was not successful.");

        log.info("------- END: DRSQA_17463_VulnOps_Sync_Ansible_actions_in_IA ------------");
    }

    @Test(description = "[VulnOps]:Map IA remediation to Vulnerability in BHAC..",groups={"Regression","VulnOps","HelixRemediate"})
    @SolqaDetails(authorName = "hpareek",  JiraID = "DRSQA-17464", Products = {Product.Helix_Remediate,Product.VulnOps})
    public void DRSQA_17464_VulnOps_Map_IA_remediation_to_Vulnerability_in_BHAC(ITestContext context) throws Exception
    {
       log.info("------- START: DRSQA_17464_VulnOps_Map_IA_remediation_to_Vulnerability_in_BHAC ------------");
       doPreconditions();
       bhacTestData = readJson.getInputValuesMapIncludingLabelField(app, "BHACTestData", "DRSQA_17464_Test_Data");
       
       log.info("Step 1 : Navigate to BHAC and verify Page is open.");
       adePortalLandingPagePage.navigateToAutomationConsolePage();
       commonMethods.switchToWindowByTitleWithRetry(SolQAConstants.BHAC);
      
       log.info("Step 2 : Navigate to Manage/Import.");
       bhacFeatureMethods.navigateToTab(BHACTab.MANAGE);

       String scanFilePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "HelixRemediate" + File.separator + bhacTestData.get("FileName");
       String scanFileName = new File(scanFilePath).getName();
       String scanFileVendor=bhacTestData.get("Vendor");
       log.info("Check if file is already imported in BHAC.");
       if(bhacFeatureMethods.isFileImportedInBHAC(scanFileName))
       {
        log.info("File is already imported in BHAC. Deleting it from BHAC.");
        bhacFeatureMethods.deleteImportedFile(scanFileName);
        log.info("Verifying file is no longer present in table with refresh retry logic.");
        Assert.assertTrue(bhacFeatureMethods.verifyFileNotInTableWithRetry(scanFileName), 
                "File '" + scanFileName + "' should not appear in the table after deletion.");
       }
       
       bhacFeatureMethods.clickImportButton();

       log.info("Step 3 : Select Qualys vendor from Import Scan modal.");
       bhacFeatureMethods.selectVendor(scanFileVendor);

       log.info("Step 4 : Select Operating System filter.");
       bhacFeatureMethods.selectOperatingSystem("Linux", "Others", "Windows");

       log.info("Step 5 : Select all Severity levels.");
        bhacFeatureMethods.selectAllSeverity();

       log.info("Step 5 : Upload scan file: "+scanFilePath);
       commonWaits.waitForSometime(commonWaits.moreDelay);
       commonMethods.uploadFileInWindowsOpenDialog(scanFilePath);

       log.info("Step 6 : Click Import button on modal.");
       bhacFeatureMethods.clickModalImportButton();

       log.info("Step 7 : Verify file is imported successfully.");
       Assert.assertTrue(bhacFeatureMethods.isFileImportedInBHAC(scanFileName), "File is not imported successfully.");

       log.info("Step 8 :Go to Risk/Vulnerabilities.");
       bhacFeatureMethods.navigateToTab(BHACTab.RISKS);

       log.info("Step 9 : Map vulnerability to IA remediation (search, Map, Map Remediation Content, Save).");
       Assert.assertTrue(
               bhacFeatureMethods.MapVulnerablityToIA(
                       bhacTestData.get("VulnerabilityName"),
                       bhacTestData.get("RemediationContent")),
               "Map vulnerability to IA remediation did not complete successfully.");

       log.info("Step 10 :Remediation button should be enabled.");
       Assert.assertTrue(bhacFeatureMethods.isRemediationButtonEnabled(), "Remediation button is not enabled.");
       log.info("------- END: DRSQA_17464_VulnOps_Map_IA_remediation_to_Vulnerability_in_BHAC ------------");
    }

    @Test(description = "[VulnOps]:Create Operation in BHAC to Remediate Vulnerability via IA.",groups={"Regression","VulnOps","HelixRemediate"})
    @SolqaDetails(authorName = "hpareek",  JiraID = "DRSQA-17465", Products = {Product.Helix_Remediate,Product.VulnOps})
    public void DRSQA_17465_VulnOps_Create_Operation_in_BHAC_to_Remediate_Vulnerability_via_IA(ITestContext context) throws Exception
    {
       log.info("------- START: DRSQA_17465_VulnOps_Create_Operation_in_BHAC_to_Remediate_Vulnerability_via_IA ------------");
       doPreconditions();
       bhacTestData = readJson.getInputValuesMapIncludingLabelField(app, "BHACTestData", "DRSQA_17465_Test_Data");
       
       log.info("Step 1 : Navigate to BHAC and verify Page is open.");
       adePortalLandingPagePage.navigateToAutomationConsolePage();
       commonMethods.switchToWindowByTitleWithRetry(SolQAConstants.BHAC);
      
       log.info("Step 2 : Navigate to Operations.");
       bhacFeatureMethods.navigateToTab(BHACTab.OPERATIONS);

       log.info("Step 3 : Operations tab should open. It should list already created operations along with their status and other details.");
       Assert.assertTrue(bhacFeatureMethods.isOperationsListed(), "Operations are not listed.");
       
       log.info("Step 4 : Click on Create Operation button.");
       bhacFeatureMethods.clickCreateOperationButton();
       
       log.info("Step 5 : Verify Create Operation window should open.");
       Assert.assertTrue(bhacFeatureMethods.isCreateOperationWindowOpen(), "Create Operation window is not open.");

       log.info("Step 6 :Select IA as Remediation tool.Provide a name to the Operation and Provide some Description.Click on next");
       bhacFeatureMethods.selectIAAsRemediationTool();
       String operationName = bhacTestData.get("OperationName")+System.currentTimeMillis();
       bhacFeatureMethods.provideOperationName(operationName)
       .provideOperationDescription(bhacTestData.get("OperationDescription"))
       .clickNextButton();

       log.info("Step 7 : On Vulnerability Selections window.Seacrh for the Asset/Vulnerability.Select the Asset/Vulnerability and click on next.");
       bhacFeatureMethods.applyAdvancedFilterSelectOption("Asset",bhacTestData.get("AssetName"));
       bhacFeatureMethods.applyAdvancedFilterSelectOption("Vulnerability Name",bhacTestData.get("VulnerabilityName"));
       bhacFeatureMethods.clickNextButton();

       log.info("Step 8 : On Remediation Options screen open Configure Script parameters.Provide the following values and click on next");
       bhacFeatureMethods.openConfigureScriptParameters()
       .provideScriptParameter("sf_action", bhacTestData.get("SfAction"))
       .provideScriptParameter("software_name", bhacTestData.get("SoftwareName"))
       .saveScriptParameters()
       .clickNextButton();
       
       log.info("Step 9 : Select Execute now and save it.");
       bhacFeatureMethods.selectExecuteNow()
       .clickNextButton();

       log.info("Step 10 : click next button again on Change Approval management screen.");
       bhacFeatureMethods.clickNextButton();

       log.info("Step 11 : On Summary window click on Save button.");
       bhacFeatureMethods.clickSaveButton();
       
       log.info("Step 12 : Verify Operation is created successfully saved and should be in progress state.");
       Assert.assertTrue(bhacFeatureMethods.searchOperationAndVerifyStatus(operationName, "In Progress"), "Operation is not created successfully.");
       
       log.info("------- END: DRSQA_17465_VulnOps_Create_Operation_in_BHAC_to_Remediate_Vulnerability_via_IA ------------");
    }

    @Test(description = "[VulnOps]:Create Operation in BHAC to Remediate Vulnerability via IA.",groups={"Regression","VulnOps","HelixRemediate"})
    @SolqaDetails(authorName = "hpareek",  JiraID = "DRSQA-17466", Products = {Product.Helix_Remediate,Product.VulnOps})
    public void DRSQA_17466_VulnOps_Create_Operation_in_BHAC_to_Remediate_Vulnerability_via_IA(ITestContext context) throws Exception    {
       log.info("------- START: DRSQA_17466_VulnOps_Create_Operation_in_BHAC_to_Remediate_Vulnerability_via_IA ------------");
       doPreconditions();
       bhacTestData = readJson.getInputValuesMapIncludingLabelField(app, "BHACTestData", "DRSQA_17466_Test_Data");
       
       log.info("Step 1 : Navigate to BHAC and verify Page is open.");
       adePortalLandingPagePage.navigateToAutomationConsolePage();
       commonMethods.switchToWindowByTitleWithRetry(SolQAConstants.BHAC);
      
       log.info("Step 2 : Navigate to Operations.");
       bhacFeatureMethods.navigateToTab(BHACTab.OPERATIONS);

       String operationName = bhacTestData.get("OperationName");
       log.info("Step 3 : Verify Operation is created successfully saved and should be in progress state.");
       Assert.assertTrue(bhacFeatureMethods.searchOperationAndVerifyStatus(operationName, "Success"), "Operation is not executed successfully.");
      
       log.info("Step 4 : Navigate to Risks.");
       bhacFeatureMethods.navigateToTab(BHACTab.RISKS);

       log.info("Step 5 : On Risk > Unique Vulnerabilities: search vulnerability, expand row, verify asset from test data is not under impacted assets.");
       Assert.assertFalse(
               bhacFeatureMethods.searchExpandVulnerabilityAndVerifyAssetPresent(
                       bhacTestData.get("VulnerabilityName"),
                       bhacTestData.get("AssetName")),
               "Asset '" + bhacTestData.get("AssetName")
                       + "' should not appear in impacted assets for vulnerability '" + bhacTestData.get("VulnerabilityName") + "'.");

       log.info("------- END: DRSQA_17466_VulnOps_Create_Operation_in_BHAC_to_Remediate_Vulnerability_via_IA ------------");
    }

    @Test(description = "[VulnOps]:Create Operation in BHAC to Remediate Vulnerability via IA.",groups={"Regression","VulnOps","HelixRemediate"})
    @SolqaDetails(authorName = "hpareek",  JiraID = "DRSQA-17467", Products = {Product.Helix_Remediate,Product.VulnOps})
    public void DRSQA_17467_VulnOps_Plan_remediation_via_IA_remediation_in_AIOps(ITestContext context) throws Exception
    {
       log.info("------- START: DRSQA_17467_VulnOps_Plan_remediation_via_IA_remediation_in_AIOps ------------");
       doPreconditions();
       bhacTestData = readJson.getInputValuesMapIncludingLabelField(app, "BHACTestData", "DRSQA_17467_Test_Data");
       
       log.info("Step 1 : Login to ADE Portal and launch Service Monitoring..");
       adePortalLandingPagePage.navigateToAIOPS();
       commonMethods.switchToWindowByTitleWithRetry(SolQAConstants.AIOPS);
      
       log.info("Step 2 : Go to Configurations tab.Click on Manage Product Features.");
       aiopsFeatureMethods.navigateToAIOpsTab(SolQAConstants.AIOPS_CONFIGURATION_TAB);
       aiopsFeatureMethods.navigateToConfigurationSubTab(SolQAConstants.AIOPS_PRODUCT_FEATURES_TAB);
       
       log.info("Step 3 : Enable Vulnerabilities feature..");
       aiopsFeatureMethods.enableFeature(bhacTestData.get("FeatureName"));

       log.info("Step 4 :Go to Risk/Vulnerabilities.");
       aiopsFeatureMethods.navigateToAIOpsTab(SolQAConstants.AIOPS_RISKS_TAB);

       log.info("Step 5 : Click on any Vulnerability.");
       aiopsFeatureMethods.navigateToRiskSubTab(SolQAConstants.AIOPS_VULNERABILITIES_TAB);

       log.info("Step 6 : Click on View all for vulnerabilities.");
       aiopsFeatureMethods.clickViewAllButton(SolQAConstants.AIOPS_VIEW_ALL_VULNERABILITIES);
       
       log.info("Step 7 : Search and open vulnerability.");
       aiopsFeatureMethods.searchAndOpenFirstVulnerability(bhacTestData.get("VulnerabilityName"));

       log.info("Step 8 : Select asset and plan automation.");
       String assetName = bhacTestData.getOrDefault("AssetName", "host-26");
       aiopsFeatureMethods.selectAssetAndPlanAutomation(assetName);

       commonMethods.switchToWindowByTitleWithRetry(SolQAConstants.BHAC);
       log.info("Step 9 : Verify Create Operation window should open.");
       Assert.assertTrue(bhacFeatureMethods.isCreateOperationWindowOpen(), "Create Operation window is not open.");

       log.info("Step 10 :Select IA as Remediation tool.Provide a name to the Operation and Provide some Description.Click on next");
       bhacFeatureMethods.selectIAAsRemediationTool();
       String operationName = bhacTestData.get("OperationName")+System.currentTimeMillis();
       bhacFeatureMethods.provideOperationName(operationName)
       .provideOperationDescription(bhacTestData.get("OperationDescription"))
       .clickNextButton();

       log.info("Step 11 : On Vulnerability Selections window.Seacrh for the Asset/Vulnerability.Select the Asset/Vulnerability and click on next.");
       bhacFeatureMethods.clickNextButton();

       log.info("Step 12 : On Remediation Options screen open Configure Script parameters.Provide the following values and click on next");
       bhacFeatureMethods.openConfigureScriptParameters()
       .provideScriptParameter("sf_action", bhacTestData.get("SfAction"))
       .provideScriptParameter("software_name", bhacTestData.get("SoftwareName"))
       .saveScriptParameters()
       .clickNextButton();
       
       log.info("Step 13 : Select Execute now and save it.");
       bhacFeatureMethods.selectExecuteNow()
       .clickNextButton();

       log.info("Step 14 : click next button again on Change Approval management screen.");
       bhacFeatureMethods.clickNextButton();

       log.info("Step 15 : On Summary window click on Save button.");
       bhacFeatureMethods.clickSaveButton();    
       
       log.info("Step 16 : Verify Operation is created successfully saved and should be in progress state.");
       Assert.assertTrue(bhacFeatureMethods.searchOperationAndVerifyStatus(operationName, "In Progress"), "Operation is not created successfully.");

       log.info("------- END: DRSQA_17467_VulnOps_Create_Operation_in_BHAC_to_Remediate_Vulnerability_via_IA ------------");
    }
    
    @Test(description = "[VulnOps]:Create Operation in BHAC to Remediate Vulnerability via IA.",groups={"Regression","VulnOps","HelixRemediate"})
    @SolqaDetails(authorName = "hpareek",  JiraID = "DRSQA-17468", Products = {Product.Helix_Remediate,Product.VulnOps})
    public void DRSQA_17468_VulnOps_Remediate_Vulnerability_via_IA_remediation_in_AIOps(ITestContext context) throws Exception
    {
       log.info("------- START: DRSQA_17468_VulnOps_Remediate_Vulnerability_via_IA_remediation_in_AIOps ------------");
       doPreconditions();
       bhacTestData = readJson.getInputValuesMapIncludingLabelField(app, "BHACTestData", "DRSQA_17468_Test_Data");
       
       log.info("Step 1 : Navigate to BHAC and verify Page is open.");
       adePortalLandingPagePage.navigateToAutomationConsolePage();
       commonMethods.switchToWindowByTitleWithRetry(SolQAConstants.BHAC);
      
       log.info("Step 2 : Navigate to Operations.");
       bhacFeatureMethods.navigateToTab(BHACTab.OPERATIONS);

       String operationName = bhacTestData.get("OperationName");
       log.info("Step 3 : Verify Operation is created successfully saved and should be in progress state.");
       Assert.assertTrue(bhacFeatureMethods.searchOperationAndVerifyStatus(operationName, "Success"), "Operation is not executed successfully.");
      
       log.info("Step 4 : Navigate to Risks.");
       bhacFeatureMethods.navigateToTab(BHACTab.RISKS);

       log.info("Step 5 : On Risk > Unique Vulnerabilities: search vulnerability, expand row, verify asset from test data is not under impacted assets.");
       Assert.assertFalse(
               bhacFeatureMethods.searchExpandVulnerabilityAndVerifyAssetPresent(
                       bhacTestData.get("VulnerabilityName"),
                       bhacTestData.get("AssetName")),
               "Asset '" + bhacTestData.get("AssetName")
                       + "' should not appear in impacted assets for vulnerability '" + bhacTestData.get("VulnerabilityName") + "'.");

       log.info("------- END: DRSQA_17468_VulnOps_Remediate_Vulnerability_via_IA_remediation_in_AIOps ------------");
    }

    @AfterMethod(alwaysRun = true)
    public void quitdriver(){
        if (IAFeatureMethod != null){   
            IAFeatureMethod.closeBrowser(); //close IA browser
            if (bhacFeatureMethods != null){
                bhacFeatureMethods.closeBrowser(); //close BHAC browser
            }
        }
    }
}
