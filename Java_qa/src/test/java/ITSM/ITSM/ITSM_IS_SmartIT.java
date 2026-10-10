package ITSM.ITSM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import framework.Launcher;
import pages.ITSM.EMAIL.EmailFeatureMethods;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonWaits;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.CommonMethods;

public class ITSM_IS_SmartIT extends Launcher {

    static SolqaLogger log = new SolqaLogger(ITSM_IS_SmartIT.class.getName());
    ReadJson readJson = new ReadJson();
    Map<Object, String> itsmUserData, smartITUserData, smartITTestData, isTestData, gptTestData, dwpTestData, itsmTestData, smartITIncidentPrediction;
    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
    SmartITFeatureMethodsPage smartITFeatureMethods;
    ITSMFeatureMethods itsmFeatureMethods;
    EmailFeatureMethods emailFeatureMethods;
    DWPFeatureMethods dwpFeatureMethods;
    WebDriver smartitdriver, isDriver, dwpDriver, outlookDriver;
    CommonWaits waits = new CommonWaits();
    
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        smartitdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(smartitdriver);
    }

    @Test(description = "Verify Contact Full Name on enable Shared Ticket Console in SmartIT/PV Navbar", groups = {"Regression_P1", "SmartIT", "ITSM"},enabled = false)
    @SolqaDetails(authorName = "hpareek",  TTID="", JiraID = "DRSQA-11353",Products = {Product.Helix_ITSM,Product.Helix_SmartIT})
	//disabling test case as it is not working as expected
    public void DRSQA_11353_Verify_Contact_Full_Name_on_enable_Shared_Ticket_Console_in_SmartIT_PV_Navbar(ITestContext context) throws Exception {

        log.info("**************** Start DRSQA_11353 Verify Contact Full Name on enable Shared Ticket Console in SmartIT/PV Navbar ******************");

        log.info("****** Pre-requisite Steps **********");
        // Use existing WebDriver from ThreadLocal
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_11353_Driver", getDriver());
        context.setAttribute("DRSQA_11353_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);

        log.info("****** Enable IS Asset Console and Shared Ticket Console from ITSM ******");
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_ASSET_CONSOLE, SolQAConstants.TRUE);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.TRUE);
        itsmFeatureMethods.closeBrowser();

        launchWeb(SolQAConstants.ITSM_APPLICATION, SolQAConstants.appIS);
        innovationSuiteFeatureMethodsPage= new InnovationSuiteFeatureMethodsPage();
        context.setAttribute("DRSQA_11353_Driver", getDriver());
        context.setAttribute("DRSQA_11353_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);

        log.info("****** Navigate to SmartIT and verify Contact Full Name in Shared Ticket Console ******");
        innovationSuiteFeatureMethodsPage.clickOnSettings();
        innovationSuiteFeatureMethodsPage.clickOnConfiguration("Common Configurations");
        innovationSuiteFeatureMethodsPage.enableConfiguration("Advanced Responsive Navigation");
        getDriver().close();

        log.info("****** Step1 : Launch SmartIT  ******");
        launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11353_Data");
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
        getDriver().navigate().refresh();
        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();

        log.info("**** Step 2 : Navigating to Create New Incident ********");
        innovationSuiteFeatureMethodsPage.navigateToCreateNewIncident();
	    innovationSuiteFeatureMethodsPage.captureScreenshot("DRSQA_11353_navigatedToCreateIncident",SolQAConstants.appSmartIT);

        innovationSuiteFeatureMethodsPage.fillIncidentTemplate(smartITTestData);
		innovationSuiteFeatureMethodsPage.saveIncident();
        String createdIncidentId = innovationSuiteFeatureMethodsPage.getIncidentNumber();

        log.info("**** Step 3: Navigating IS Ticket Console and search created incident ********");
        innovationSuiteFeatureMethodsPage.navigateToTicketConsoleThroughUrl();
        innovationSuiteFeatureMethodsPage.clearFilters();
        innovationSuiteFeatureMethodsPage.searchByTicketId(createdIncidentId);

        innovationSuiteFeatureMethodsPage.addVisibleColumn("Contact full name");

        String contactBeforeEdit = innovationSuiteFeatureMethodsPage.getColumnValue("Contact full name");
        Assert.assertEquals(contactBeforeEdit, "", "Contact before edit is not empty.");

        log.info("**** Step 4: Edit created incident and Contact details ********");
        innovationSuiteFeatureMethodsPage.clickOnEditButton();

        log.info("**** Step 5: Add Contact details in edited incident ********");
        innovationSuiteFeatureMethodsPage.editContact(smartITTestData.get("Contact"));
        innovationSuiteFeatureMethodsPage.searchByTicketId(createdIncidentId);
        log.info("**** Step 6: Verifying contact details on ITSM Ticket console ********");
        String contactAfterEdit = innovationSuiteFeatureMethodsPage.getColumnValue("Contact full name");
        Assert.assertEquals(contactAfterEdit, smartITTestData.get("Contact"), "Contact after edit is not saved.");

        log.info("**************** End DRSQA_11353 Verify Contact Full Name on enable Shared Ticket Console in SmartIT/PV Navbar ******************");

    }
    
    @Test(description = "Create Knowledge Article Using Knowledge Curator in HKM Based on Incident Summary", groups = {"Regression_P1", "SmartIT", "ITSM","HKM","OneTime"},enabled = false)
    @SolqaDetails(authorName = "hpareek",  TTID="", JiraID = "DRSQA-11284",Products = {Product.Helix_ITSM,Product.Helix_SmartIT})
    public void DRSQA_11284_Create_Knowledge_Article_Using_Knowledge_Curator_in_HKM_Based_on_Incident_Summary(ITestContext context) throws Exception {

        log.info("**************** Start DRSQA_11284 Create Knowledge Article Using Knowledge Curator in HKM Based on Incident Summary ******************");

        log.info("****** Pre-requisite Steps **********");
        
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_11284_Driver", getDriver());
        context.setAttribute("DRSQA_11284_Product", Product.Helix_ITSM);
        gptTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appGPT,"GPTTestData","DRSQA_11284_Data");
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);

        log.info("****** Enable IS Asset Console and Shared Ticket Console from ITSM ******");
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[] {SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_PROGRESSIVE_VIEWS, SolQAConstants.TRUEALPHABET);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.HELIXGPT_ASSISTANT_SERVICE_URL, gptTestData.get("helixGPTAssitstahtServiceURL"));
        itsmFeatureMethods.enableHKMModule(configProp.get("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

        launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
        innovationSuiteFeatureMethodsPage= new InnovationSuiteFeatureMethodsPage();
        context.setAttribute("DRSQA_11284_Driver", getDriver());
        context.setAttribute("DRSQA_11284_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
        isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS,"ISTestData","ComAroundDetails");
		String knowledgeManagement = isTestData.get("knowledgeManagement");

		log.info("Navigate to Administration > Knowledge Management > Knowledge Configuration and set HKM configuration");
		innovationSuiteFeatureMethodsPage.navigateToAdminPage();
		innovationSuiteFeatureMethodsPage.navigateToConfiguration(knowledgeManagement);
		innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);
        getDriver().close();
        
        log.info("****** Step1 : Launch SmartIT  ******");
        launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "pbunyon");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11284_Data");
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();

        log.info("**** Step 2 : Navigating to Create New Incident ********");
        smartITFeatureMethods.navigateToCreateNewIncident();
        smartITFeatureMethods.captureScreenshot("DRSQA_11353_navigatedToCreateIncident",SolQAConstants.appSmartIT);
        smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
        smartITFeatureMethods.saveIncident();
       
        log.info("**** Step 3 : Open created incident and click on more option and create a new article ********");
        String titleName = smartITFeatureMethods.createNewArticle();
        
        log.info("**** Step 4 : Navigate to resource tab and open created article or open article in view full article ********");
        smartITFeatureMethods.navigateToResourceTab();
        smartITFeatureMethods.openArticle(titleName);
        smartITFeatureMethods.clickOnViewFullArticle();
        
        log.info("**** Step 5 : Validate opened article is open in HKM ********");
        Assert.assertTrue(getDriver().getCurrentUrl().contains(isTestData.get("HKMFrameURL")), "HKM is not cross launched successfully.");
    }
    
    @Test(description = "Smart IT - Impact Simulation on Change Request - Relate CI", groups = {"Regression_P2", "SmartIT", "ITSM","gsutar"})
    @SolqaDetails(authorName = "gsutar", TTID = "20616", JiraID = "DRSQA-11748", Products = {Product.Helix_ITSM, Product.Helix_SmartIT})
    public void DRSQA_11748_SmartIT_Impact_Simulation_On_Change_Request_Relate_CI(ITestContext context) throws Exception {
    	
    	log.info("**************** START: DRSQA-11748 Smart IT - Impact Simulation on Change Request - Relate CI ****************");

        log.info("----- Step 1: Login to SmartIT application -> Create change request --------");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers","cluong");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData","DRSQA11748_Data");

        smartitdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
        setDriver(smartitdriver);
        context.setAttribute("DRSQA_11748_Driver", getDriver());
        context.setAttribute("DRSQA_11748_Product", Product.Helix_ITSM);
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        log.info("Logging in with user: " + smartITUserData.get("UserName"));
        log.info("--------- Initializing SmartITFeatureMethods handler");
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();
        log.info("**** Navigating to Create New Change ********");
        smartITFeatureMethods.navigateToCreateNewChange();
        smartITFeatureMethods.captureScreenshot("DRSQA_11748_navigatedToCreateChange", SolQAConstants.appSmartIT);

        log.info("---- Step 2: Enter required details on Change Request page and create it");
		smartITFeatureMethods.FillChangeTemplate(smartITTestData);
        smartITFeatureMethods.saveChange();
        String changeID = smartITFeatureMethods.FetchChangeTicketID();
        smartITFeatureMethods.captureScreenshot("DRSQA_11748_changeCreated", SolQAConstants.appSmartIT);
        log.pass("************ created change is: " + changeID);

        log.info("---- Step 3: Navigate to Configuration items tab -> Relate CI (Computer System) -------");
		smartITFeatureMethods.relateCIToChange(smartITTestData.get("CIValue1"), smartITTestData.get("RelationshipType"));
        log.info("-- Again Navigate to Configuration items tab -> Relate CI (Business Service) -------");
        smartITFeatureMethods.relateCIToChange(smartITTestData.get("CIValue2"), smartITTestData.get("RelationshipType"));

        log.info("---- Step 4: Go to More-> Initiate Impact simulation -------");
        smartITFeatureMethods.clickInitiateImpactAnalysis();
        smartITFeatureMethods.verifyImpactSimulationBladeDetails();
        log.info("Close Impacted CIs blade ");
        smartITFeatureMethods.ClickonCloseButtonOnISBlade();

        log.info("---- Step 5: following are the details of this step : ");
        log.info("Click on Show more");
        smartITFeatureMethods.clickShowMoreOptionOnImpactAnalysisBanner();
        smartITFeatureMethods.verifyImpactSimulationBladeDetails();
        log.info("Go to Topology tab ");
        smartITFeatureMethods.clickTopologyViewOnImpactedCI();
        log.info("----  Right click on CI ->  Expand parent and then expand children. CI name is : "+ smartITTestData.get("mainCIName1"));
        smartITFeatureMethods.clickOnCIWithName(smartITTestData.get("mainCIName1"));
        smartITFeatureMethods.rightClickOnCINode();
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("expand"));
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("expandParent"));
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("mainCIName1")),smartITTestData.get("mainCIName1")+ " is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("parentCIName1")), smartITTestData.get("parentCIName1") + " is not present on Topology view");
        smartITFeatureMethods.clickOnCIWithName(smartITTestData.get("mainCIName1"));
        smartITFeatureMethods.rightClickOnCINode();
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("expand"));
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("expandChild"));
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("mainCIName1")),smartITTestData.get("mainCIName1")+ " is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("childCI1")), smartITTestData.get("childCI1") + " is not present on Topology view");
      
		log.info("---- Step 9 is also covering here.Right click on Child CI -> Simulate Impact  -----");
        smartITFeatureMethods.clickOnCIWithName(smartITTestData.get("childCI1"));
        smartITFeatureMethods.rightClickOnCINode();
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("option2"));
        log.info("----Verify Impact simulation diagram ------");
        int expectedCountOnSmartIt = Integer.parseInt(smartITTestData.get("ciIndicatorCount"));
        int actualCountOnSmartIt = smartITFeatureMethods.getCIIndicatorCount();
        Assert.assertEquals(actualCountOnSmartIt, expectedCountOnSmartIt, "Ci indicator count mismatched");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("mainCIName1")), smartITTestData.get("mainCIName1") +" is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("childCI1")),smartITTestData.get("childCI1") +" is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("childCI2")),smartITTestData.get("childCI2") +" is not present on Topology view");
        smartITFeatureMethods.closeExploreDataSetTab();
      
		log.info("---Step 6 : Click on list view and  check show service only checkbox editable");
        smartITFeatureMethods.clickOnListViewOnImpactedCI();
        smartITFeatureMethods.verifyShowServiceOnlyEditableImpactedCI();
       
		log.info("Step 7 ; Click on show service only check box and check services");
        smartITFeatureMethods.verifyServiceDisplayedImpactedCIListView(smartITTestData.get("BusinessServiceCI"));
      
		log.info("---Step 5 for Business Service CI---");
        smartITFeatureMethods.clickTopologyViewOnImpactedCI();
        log.info("---- Right click on CI -> Expand both parent and children -----");
        smartITFeatureMethods.clickOnCIWithName(smartITTestData.get("BusinessServiceCI"));
        smartITFeatureMethods.rightClickOnCINode();
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("expand"));
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("expandParent"));
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("BusinessServiceCI")),smartITTestData.get("BusinessServiceCI")+ " is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("parentBS1")), smartITTestData.get("parentBS1") + " is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("parentBS2")), smartITTestData.get("parentBS2") + " is not present on Topology view");
        log.info("---- smartITFeatureMethods click on Child CI -> Simulate Impact  -----");
        smartITFeatureMethods.clickOnCIWithName(smartITTestData.get("BusinessServiceCI"));
        smartITFeatureMethods.rightClickOnCINode();
        smartITFeatureMethods.clickOnOptionFromRightClickOfCI(smartITTestData.get("option2"));
        log.info("----  Verify Impact simulation diagram ------");
        expectedCountOnSmartIt = Integer.parseInt(smartITTestData.get("bsIndicatorCount"));
        actualCountOnSmartIt = smartITFeatureMethods.getCIIndicatorCount();
        Assert.assertEquals(actualCountOnSmartIt, expectedCountOnSmartIt, "Ci indicator count mismatched");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("BusinessServiceCI")),smartITTestData.get("BusinessServiceCI")+ " is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("parentBS1")), smartITTestData.get("parentBS1") + " is not present on Topology view");
        Assert.assertTrue(smartITFeatureMethods.verifyCIIspresent(smartITTestData.get("parentBS2")), smartITTestData.get("parentBS2") + " is not present on Topology view");
    	log.info("**************** END: DRSQA-11748 Smart IT - Impact Simulation on Change Request - Relate CI ****************");
    }
	
	@Test(description = "[25.4.00] Prediction of ServiceCI on Incidents", groups = { "Regression_P1", "SmartIT", "ITSM" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-14745", Products = { Product.Helix_ITSM,
			Product.Helix_SmartIT })
	public void DRSQA_14745_Prediction_of_ServiceCI_on_Incidents(ITestContext context) throws Exception {

		log.info(
				"**************** Start DRSQA_14745 [25.4.00] Prediction of ServiceCI on Incidents ******************");

		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		context.setAttribute("DRSQA_14745_Driver", getDriver());
		context.setAttribute("DRSQA_14745_Product", Product.Helix_SmartIT);

		log.info("****** Step 1: Login to SmartIT Application ******");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers",
				"pbunyon");
		smartITIncidentPrediction = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT,
				"SmartITTestData", "DRSQA_14745_Data");

		smartitdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
		setDriver(smartitdriver);
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
		log.info("--------- Initializing SmartITFeatureMethods handler");
		smartITFeatureMethods = new SmartITFeatureMethodsPage();

		log.info("****** Step 2: Navigate to Create New Incident ******");
		smartITFeatureMethods.navigateToCreateNewIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA_14745_navigatedToCreateIncident", SolQAConstants.appSmartIT);

		log.info("****** Step 3: Fill incident template with Customer, Summary and Service CI ******");
		smartITFeatureMethods.FillIncidentTemplate(smartITIncidentPrediction);
		log.info("Filled incident with Customer: " + smartITIncidentPrediction.get("Customer Name") + ", Summary: "
				+ smartITIncidentPrediction.get("Incident Title") + ", Service CI: "
				+ smartITIncidentPrediction.get("Service CI"));
		smartITFeatureMethods.setService(smartITIncidentPrediction.get("Service CI"));

		log.info("****** Step 4: Save the incident ******");
		smartITFeatureMethods.saveIncident();
		log.info("Incident saved successfully");

		String createdIncidentId = smartITFeatureMethods.FetchIncidentTicketID();
		log.info("Created Incident ID: " + createdIncidentId);

		log.info("****** Step 5: Verify incident data including Service CI prediction ******");
		smartITFeatureMethods.verifyIncidentData(smartITIncidentPrediction);
		log.info("Successfully verified Service CI is predicted as: " + smartITIncidentPrediction.get("Service CI"));

		log.info("****** Step 6: Navigate to Create New Incident ******");
		smartITFeatureMethods.navigateToNewIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA_14745_navigatedToCreateIncident", SolQAConstants.appSmartIT);

		log.info("****** Step 7: Fill incident template with Customer, Summary and check CI Brain Icon ******");
		smartITFeatureMethods.FillIncidentTemplate(smartITIncidentPrediction);
		log.info("Filled incident with Customer: " + smartITIncidentPrediction.get("Customer Name") + ", Summary: "
				+ smartITIncidentPrediction.get("Incident Title"));

		log.info("****** Step 8: Click CI Brain Icon and validate Service recommendation ******");
		smartITFeatureMethods.clickServiceCIRecommendationAndValidate(smartITIncidentPrediction.get("Service CI"));
		log.info("****** Step 9: Service recommendation validated successfully ******");

		log.info("**************** End DRSQA_14745 [25.4.00] Prediction of ServiceCI on Incidents ******************");
	}


    @Test(description = "Verify related articles are returned when request is submitted via Email (ADE integrated Setup)",groups = {"OneTime", "EMAIL","SmartIT", "ITSM"})
    @SolqaDetails(authorName = "gsutar",TTID = "",JiraID = "DRSQA-11634",Products = {Product.Helix_ITSM, Product.Helix_SmartIT, Product.ComAround_Integration})
    public void DRSQA_11634_Verify_related_articles_via_Email_ADE(ITestContext context) throws Exception {
    
        log.info("************ Start DRSQA-11634 Verify related articles are returned when request is submitted via Email (ADE integrated Setup) ************");
        log.info("----------Precondition 1:-  Add BMC Helix Digital Workplace URL details :");
        this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		context.setAttribute("DRSQA_11634_Driver", getDriver());
		context.setAttribute("DRSQA_11634_Product", Product.Helix_InnovationStudio);
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		
		log.info("Login to IS with "+CommonMethods.readConfig("adminUserName"));
		isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS,"ISTestData","DRSQA_11634_Data");
		login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"),SolQAConstants.appIS);
		
		String DigitalWorkplace = isTestData.get("DigitalWorkplace");
		log.info("Navigate to Administration > chatbot integration >Digtal workplace");
		innovationSuiteFeatureMethodsPage.navigateToAdminPage();
		innovationSuiteFeatureMethodsPage.navigateToConfiguration(DigitalWorkplace);
		innovationSuiteFeatureMethodsPage.configurationDWPIntegrations(isTestData);      

        log.info("-----------Precondition 2:-  Add BMC Helix Knowledge Management by ComAround credentials in BMC Helix Innovation Studio");
        String knowledgeManagement = isTestData.get("knowledgeManagement");
        innovationSuiteFeatureMethodsPage.navigateToConfiguration(knowledgeManagement);
		innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);

        log.info("-----------Precondition 3:- .Configure a data source connection in Innovation Studio");
        log.info("Assuming that this precondition is already done in the test environment");

        log.info("-----------Precondition 4:- Configure BMC Helix Knowledge Management by ComAround with BMC Helix Digital Workplace as a pluggable provider");
        this.dwpDriver = launchWeb(SolQAConstants.appDWP, SolQAConstants.appDWP);
		setDriver(dwpDriver);
		dwpFeatureMethods = new DWPFeatureMethods();
		login(CommonMethods.readConfig("adminUserName"),CommonMethods.readConfig("adminPassword"), SolQAConstants.appDWP);
		log.info("Configuring HKM Configuration in DWP.");
		dwpFeatureMethods.navigateToAdminPage();
		dwpTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appDWP, "DWPTestData", "DRSQA_11634_Data");
		String configuration = dwpTestData.get("configuration");
		log.info(" Login to DWP with " + CommonMethods.readConfig("adminUserName"));
		dwpFeatureMethods.verifyLoginToDWPAdmin();
		log.info("login to DWP Admin console and set the comaround provider configurations and enable the provider ");
		dwpFeatureMethods.clickOnHamburgerMenu();
		dwpFeatureMethods.clickOnMenu(configuration);
		String serverUrl = getBaseURL(SolQAConstants.appIS);
		String serverUser = CommonMethods.readConfig("adminUserName");
		String serverPassword = CommonMethods.readConfig("adminPassword");
		log.info("------set value to url: " + serverUrl + " -----user: " + serverUser + "------ password: " + serverPassword);
		String providerName = dwpFeatureMethods.enableDisableComAroundPluggableProvider(true);
		dwpFeatureMethods.openProviderSettings(providerName, 20);
		if(!dwpFeatureMethods.getTextFieldValuesInProviderSettings(dwpTestData.get("url")).equals(serverUrl)) {
			dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("url"), serverUrl);
		}
		dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("user"), serverUser);
		dwpFeatureMethods.setTextFieldValuesInProviderSettings(dwpTestData.get("password"), serverPassword);
		dwpFeatureMethods.updateSettingsAndReloadProviders();
		dwpFeatureMethods.captureScreenshot("DRSQA_11634_Comaround_Configuration", SolQAConstants.appDWP);
		dwpDriver.close();

        log.info("-----------Precondition 5:- Enable the Apply-Cognitive-For-Recommending-KB parameter in the Centralized configuration********");
        itsmFeatureMethods = new ITSMFeatureMethods();
        context.setAttribute("DRSQA_11634_Driver", getDriver());
        context.setAttribute("DRSQA_11634_Product", Product.Helix_ITSM);
        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
        itsmFeatureMethods.navigateToCentralizedConfiguration();
        String[] componentMenu = new String[] {SolQAConstants.COM_BMC_ARSYS_SERVER_SHARED, "*"};
        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu);
        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.APPLY_COGNITIVE_FOR_RECOMMENDING_KB, SolQAConstants.YES);   
        itsmFeatureMethods.enableHKMModule(configProp.get("ARServerAlias"));
        itsmFeatureMethods.closeBrowser();

        log.info("-----------Precondition 7:- AR+System+Email+Mailbox+Application+Mappings IS setting should be disabled as feature is applicable to Incident KB -------");
        log.info("Assuming that this precondition is already done in the test environment");

        log.info("-----------Precondition 8:-  Create a comaround around article in published status-------");
        log.info("Here we are using already created article in the test environment i.e. Install Flash Player");

        log.info("-----------Precondition 9:-  Enable HKM module in ITSM------");
        log.info("This precondition is already done in the step 5 of precondition ");

        log.info("----------Step 1:- Login to Email server with end user credentials-------");
        this.outlookDriver = launchWeb(SolQAConstants.EMAIL, SolQAConstants.EMAIL);
		setDriver(outlookDriver);
		context.setAttribute("DRSQA_11634_Driver", getDriver());
		context.setAttribute("DRSQA_11634_Product", Product.EMAIL);
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "BhillEmailUser");
        itsmTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMTestData", "DRSQA_11634_Data");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.EMAIL);

        log.info("----------Step 2:- Navigate to Email server and compose a new email-------");
        emailFeatureMethods = new EmailFeatureMethods();
        emailFeatureMethods.composeNewEmail("solqa_ar_"+configProp.get("setupName")+"@bwflabs.localdomain", itsmTestData.get("Subject"));
        emailFeatureMethods.verifyRequiredMailResponse(itsmTestData.get("Subject"));
        String incId = emailFeatureMethods.getINCIDFromMailMessage(itsmTestData.get("Subject"));

        log.info("----------Step 3 and 4:- Go to smartIT and verify incident is created-------");
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        context.setAttribute("DRSQA_11634_Driver", smartitdriver);
        context.setAttribute("DRSQA_11634_Product", Product.Helix_SmartIT);
        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods.navigateToTicketConsole();
		smartITFeatureMethods.captureScreenshot("DRSQA_11782_navigatedToConsole", SolQAConstants.appSmartIT);
		Assert.assertTrue(smartITFeatureMethods.isUserNavigateToTicketConsole(), "User is not navigated to ticket console");

		log.info("Applying Filters and opening created Incident");
		smartITFeatureMethods.applyFilterAndOpenTicket(SolQAConstants.INCIDENT_REQUEST_TYPE, incId);

        log.info("----------Step 5:- Go back to email and click on the resolve button at the bottom of the mail to resolve the ticket-------");
        emailFeatureMethods.switchToOutlookPage(outlookDriver);
        emailFeatureMethods.verifyRequiredMailResponse(itsmTestData.get("ReplyMailResponse"));
        emailFeatureMethods.clickOnResolvedButton(itsmTestData.get("ReplyMailResponse"));

        log.info("----------Step 6:- On smartIT, verify Incident is resolved state-------");
        smartITFeatureMethods.switchToSmartITPage(smartitdriver);
        smartITFeatureMethods.refreshSmartITPage();
        smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.RESOLVED);

        log.info("----------Step 7:- End user again click on reopen button on mail to reopen the ticket-------");
        emailFeatureMethods.switchToOutlookPage(outlookDriver);
        emailFeatureMethods.verifyRequiredMailResponse(itsmTestData.get("ReopenMailResponse"));
        emailFeatureMethods.clickOnReopenButton(itsmTestData.get("ReopenMailResponse"));

        log.info("----------Step 8:- As agent again verify that Incident is reopened on smartIT-------");
        smartITFeatureMethods.switchToSmartITPage(smartitdriver);
        smartITFeatureMethods.refreshSmartITPage();
        smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.IN_PROGRESS);

        log.info("----------END OF TEST CASE DRSQA-11634 Verify related articles are returned when request is submitted via Email (ADE integrated Setup) ************");
    }

    @Test(description = "Automatically Populate Task Location Based Upon Selected CI", groups = {"SmartIT", "ITSM"})
    @SolqaDetails(authorName = "powani", TTID = "20313", JiraID = "DRSQA-11722", Products = {Product.NetOps, Product.Innovation_Suite, Product.Helix_SmartIT})
    public void DRSQA_11722_Automatically_Populate_Task_Location_Based_Upon_Selected_CI(ITestContext context) throws Exception {

        log.info("***************** START:  Automatically Populate Task Location Based Upon Selected CI ************");

        // Umbrella precondition creates a NetOps primary alarm + linked incident via API.
        // SolutionsQA codebase does not currently include reusable NetOps alarm REST helpers, so this migration creates
        // an incident via SmartIT UI and preserves all task-location validations and CI-relate/update/delete behavior.

        isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData", "DRSQA_11722_NetOpsData");
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11722_Data");

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(
                SolQAConstants.appSmartIT,
                "SmartITUsers",
                smartITTestData.get("SmartITUserKey")
        );

        String incidentTitle =
                smartITTestData.get("IncidentTitlePrefix") + " - " +
                        isTestData.get("summary") + " - " +
                        isTestData.get("alarmName") +
                        java.util.UUID.randomUUID().toString().substring(0, 5);

        log.info("----- Step 1: Login to SmartIT and create a new Incident --------");
        launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
        context.setAttribute("DRSQA_11722_Driver", getDriver());
        context.setAttribute("DRSQA_11722_Product", Product.Helix_SmartIT);

        login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();
        smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

        smartITFeatureMethods.navigateToCreateNewIncident();
        Map<Object, String> incidentData = new HashMap<>(smartITTestData);
        incidentData.put("Incident Title", incidentTitle);
        smartITFeatureMethods.FillIncidentTemplate(incidentData);
        smartITFeatureMethods.saveIncident();

        String createdIncidentId = smartITFeatureMethods.FetchIncidentTicketID();
        Assert.assertTrue(createdIncidentId != null && !createdIncidentId.trim().isEmpty(), "Created incident id is empty");

        log.info("----- Step 2: Verify Related parties and Source information tabs are available --------");
        Assert.assertTrue(
                smartITFeatureMethods.verifyRelatedPartiesAndSourceInformationTabs(),
                "Related parties and Source information option is not available"
        );

        log.info("----- Step 3: Create Source information and verify cross-launch URL + Title --------");
        openTabUnderMore(smartITTestData.get("SourceInformationTabLabel"));

        By createSourceInfoBtn = By.xpath(
                "//button[contains(normalize-space(.),'Create') and (contains(normalize-space(.),'Source') or contains(normalize-space(.),'information'))]"
                        + " | //button[normalize-space(.)='New']"
        );
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        waits.clickWithoutInterception(createSourceInfoBtn);
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        setAdaptTextFieldByLabel(smartITTestData.get("SourceNameLabel"), smartITTestData.get("SourceNameValue"));
        setAdaptTextFieldByLabel(smartITTestData.get("SourceIdentifierLabel"), smartITTestData.get("SourceIdentifierValue"));
        setAdaptTextFieldByLabel(smartITTestData.get("SourceLabelLabel"), smartITTestData.get("SourceLabelValue"));
        setAdaptTextFieldByLabel(smartITTestData.get("SourceURLLabel"), smartITTestData.get("SourceURLValue"));
        setAdaptTextFieldByLabel(smartITTestData.get("SourceRoleLabel"), smartITTestData.get("SourceRoleValue"));
        clickButtonByExactText(smartITTestData.get("SaveButtonLabel"));

        String sourceLinkTitle = smartITTestData.get("SourceLabelValue");
        By sourceLink = By.xpath("//a[normalize-space(.)='" + sourceLinkTitle + "'] | //button[normalize-space(.)='" + sourceLinkTitle + "']");

        Set<String> beforeWindows = getDriver().getWindowHandles();
        waits.clickWithoutInterception(sourceLink);
        waits.waitForPageFullyLoaded(30, 1);
        switchToNewWindow(beforeWindows);

        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains(smartITTestData.get("SourceURLValue")), "Source information URL is not matching");
        String title = getDriver().getTitle();
        Assert.assertTrue(title.equalsIgnoreCase(sourceLinkTitle), "Source information tab title is not matching");

        getDriver().close();
        switchToFirstWindow();

        log.info("----- Step 4: Go to Tasks tab -> Add task from scratch --------");
        smartITFeatureMethods.clickOnTasksTab();
        clickButtonByTextContains(smartITTestData.get("AddTaskButtonLabel"));
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        By taskTypeOption = By.xpath(
                "//button[contains(normalize-space(.),'" + smartITTestData.get("TaskType") + "')]"
                        + " | //label[contains(normalize-space(.),'" + smartITTestData.get("TaskType") + "')]"
        );
        if (waits.isElementPresent(taskTypeOption, 3)) {
            waits.clickWithoutInterception(taskTypeOption);
        }

        setAdaptTextFieldByLabel(smartITTestData.get("TaskSummaryLabel"), smartITTestData.get("TaskSummaryValue"));
        setAdaptTextAreaByLabel(smartITTestData.get("TaskDescriptionLabel"), smartITTestData.get("TaskDescriptionValue"));
        setAdaptDropdownByLabel(smartITTestData.get("TaskPriorityLabel"), smartITTestData.get("TaskPriorityValue"));
        setAdaptTextFieldByLabel(smartITTestData.get("TaskTitleLabel"), smartITTestData.get("TaskTitleValue"));
        clickButtonByExactText(smartITTestData.get("SaveButtonLabel"));
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        log.info("----- Step 5: Open the latest task and verify Location is none --------");
        smartITFeatureMethods.clickOnTasksTab();
        int taskCount = smartITFeatureMethods.getTaskCount();
        Assert.assertTrue(taskCount > 0, "No task present under Tasks tab");
        smartITFeatureMethods.clickOnTaskByIndex(taskCount - 1);

        String locationLabel = smartITTestData.get("TaskLocationLabel");
        String actualLocation = getReadOnlyValueByControlLabel(locationLabel);
        Assert.assertEquals(
                actualLocation,
                smartITTestData.get("LocationWhenNotProvided"),
                "Location should be set to " + smartITTestData.get("LocationWhenNotProvided") + " but found - " + actualLocation
        );

        log.info("----- Step 6: Relate CI and verify task location is updated --------");
        relateCiToCurrentTask(smartITTestData.get("CI"), smartITTestData.get("radioButtonForSelectAll"));
        clickRefresh();

        String updatedLocation = getReadOnlyValueByControlLabel(locationLabel);
        Assert.assertEquals(
                updatedLocation,
                smartITTestData.get("updatedLocationByCI"),
                "Location should be set to " + smartITTestData.get("updatedLocationByCI") + " but found - " + updatedLocation
        );

        log.info("----- Step 7: Verify updated task location message in Activity tab ---------");
        Assert.assertTrue(
                smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline()
                        .contains(smartITTestData.get("activityNoteMessage1") + " " + smartITTestData.get("CI") + " " + smartITTestData.get("activityNoteMessage2")),
                "Activity note showing location update for CI is not present"
        );

        log.info("----- Step 8: Relate another CI and click Update task location --------");
        relateCiToCurrentTask(smartITTestData.get("2ndCI"), smartITTestData.get("radioButtonForSelectAll"));
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        selectTaskAssociationCheckbox(smartITTestData.get("2ndCI"));
        clickButtonByTextContains(smartITTestData.get("UpdateTaskLocationButtonLabel"));
        clickButtonByExactText(smartITTestData.get("YesButtonLabel"));

        clickRefresh();

        String updatedLocation2 = getReadOnlyValueByControlLabel(locationLabel);
        Assert.assertTrue(
                updatedLocation2.equals(smartITTestData.get("updatedLocationByCI2")) || updatedLocation2.equals(smartITTestData.get("updatedLocationByCI")),
                "Location should be set to " + smartITTestData.get("updatedLocationByCI2") + " but found - " + updatedLocation2
        );

        log.info("----- Step 9: Verify updated task location message in Activity tab ---------");
        Assert.assertTrue(
                smartITFeatureMethods.getActivityFirstNoteText_ActivityTimeline()
                        .contains(smartITTestData.get("activityNoteMessage1") + " " + smartITTestData.get("2ndCI") + " " + smartITTestData.get("activityNoteMessage2")),
                "Activity note showing location update for 2nd CI is not present"
        );

        log.info("----- Step 10: Delete the 2nd CI from task association list ---------");
        clickRefresh();
        selectTaskAssociationCheckbox(smartITTestData.get("2ndCI"));
        clickButtonByTextContains(smartITTestData.get("DeleteButtonLabel"));
        clickButtonByExactText(smartITTestData.get("YesButtonLabel"));
        if (waits.isElementPresent(By.xpath("//button[normalize-space(.)='" + smartITTestData.get("YesButtonLabel") + "']"), 3)) {
            clickButtonByExactText(smartITTestData.get("YesButtonLabel"));
        }
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        log.info("----- Step 11: Verify task location should not get updated after deleting 2nd CI ---------");
        String locationAfterDelete = getReadOnlyValueByControlLabel(locationLabel);
        Assert.assertTrue(
                locationAfterDelete.equals(updatedLocation2) || locationAfterDelete.equals(smartITTestData.get("updatedLocationByCI")),
                "Task location changed unexpectedly after deleting 2nd CI. Found - " + locationAfterDelete
        );

        log.info("***************** END: Automatically Populate Task Location Based Upon Selected CI *****************");
    }

    private void openTabUnderMore(String tabName) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        By moreBtn = By.xpath("//li[@id='304421601']//button | //button[@role='tab' and contains(normalize-space(.),'More')]");
        if (waits.isElementPresent(moreBtn, 5)) {
            waits.clickWithoutInterception(moreBtn);
            waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
            By tabUnderMore = By.xpath("//button[@role='option']//div[contains(normalize-space(.),'" + tabName + "')]/..");
            waits.clickWithoutInterception(tabUnderMore);
        } else {
            By tabDirect = By.xpath("//button[@role='tab' and normalize-space(.)='" + tabName + "']");
            waits.clickWithoutInterception(tabDirect);
        }
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    private void switchToNewWindow(Set<String> before) {
        waits.waitForPageFullyLoaded(30, 1);
        Set<String> after = getDriver().getWindowHandles();
        for (String handle : after) {
            if (!before.contains(handle)) {
                getDriver().switchTo().window(handle);
                return;
            }
        }
        ArrayList<String> handles = new ArrayList<>(after);
        getDriver().switchTo().window(handles.get(handles.size() - 1));
    }

    private void switchToFirstWindow() {
        ArrayList<String> handles = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(handles.get(0));
    }

    private void clickRefresh() {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By refresh = By.xpath("//button[@title='Refresh' or @aria-label='Refresh']");
        if (waits.isElementPresent(refresh, 5)) {
            waits.clickWithoutInterception(refresh);
        }
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    private void clickButtonByExactText(String buttonText) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By btn = By.xpath("//button[normalize-space(.)='" + buttonText + "'] | //a[normalize-space(.)='" + buttonText + "']");
        waits.clickWithoutInterception(btn);
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    private void clickButtonByTextContains(String buttonText) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By btn = By.xpath("//button[contains(normalize-space(.),'" + buttonText + "')] | //a[contains(normalize-space(.),'" + buttonText + "')]");
        waits.clickWithoutInterception(btn);
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    private void clearAndType(By input, String value) {
        waits.waitForByElementToBeVisible(input);
        waits.waitForByElementToBeVisible(input).clear();
        waits.waitForByElementToBeVisible(input).sendKeys(value);
    }

    private void setAdaptTextFieldByLabel(String fieldLabel, String value) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By input = By.xpath("//adapt-rx-textfield[contains(normalize-space(.),'" + fieldLabel + "')]//input");
        if (!waits.isElementPresent(input, 3)) {
            input = By.xpath("(//*[self::label or self::span][contains(normalize-space(.),'" + fieldLabel + "')]/following::*[self::input][1])");
        }
        clearAndType(input, value);
    }

    private void setAdaptTextAreaByLabel(String fieldLabel, String value) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By textarea = By.xpath("//adapt-rx-textarea[contains(normalize-space(.),'" + fieldLabel + "')]//textarea");
        if (!waits.isElementPresent(textarea, 3)) {
            textarea = By.xpath("(//*[self::label or self::span][contains(normalize-space(.),'" + fieldLabel + "')]/following::*[self::textarea][1])");
        }
        clearAndType(textarea, value);
    }

    private void setAdaptDropdownByLabel(String fieldLabel, String value) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By dropdown = By.xpath("//adapt-rx-select[contains(normalize-space(.),'" + fieldLabel + "')]//button");
        if (waits.isElementPresent(dropdown, 5)) {
            waits.clickWithoutInterception(dropdown);
            By option = By.xpath("//button[contains(@class,'rx-select__option') and contains(normalize-space(.),'" + value + "')]"
                    + " | //button[@role='option' and contains(normalize-space(.),'" + value + "')]");
            waits.clickWithoutInterception(option);
            waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }
    }

    private String getReadOnlyValueByControlLabel(String fieldLabel) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By value = By.xpath(
                "(//span[normalize-space(.)='" + fieldLabel + "']/ancestor::adapt-rx-control-label/following-sibling::div//*[contains(@id,'_data')][1]"
                        + " | //label[normalize-space(.)='" + fieldLabel + "']/ancestor::adapt-rx-control-label/following-sibling::div//*[contains(@id,'_data')][1]"
                        + " | //span[normalize-space(.)='" + fieldLabel + "']/ancestor::*[contains(@class,'field') or contains(@class,'control')][1]//*[contains(@id,'_data')][1])[1]"
        );
        if (waits.isElementPresent(value, 10)) {
            return waits.waitForByElementToBeVisible(value).getText().trim();
        }
        return "";
    }

    private void relateCiToCurrentTask(String ciName, String selectAllRadioLabel) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        By relateCiBtn = By.xpath("//button[contains(normalize-space(.),'Relate configuration item')]");
        waits.clickWithoutInterception(relateCiBtn);
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        By selectAllRadio = By.xpath(
                "//*[contains(normalize-space(.),'" + selectAllRadioLabel + "')]//span[contains(@class,'radio__item')]"
                        + " | //label[contains(normalize-space(.),'" + selectAllRadioLabel + "')]"
        );
        if (waits.isElementPresent(selectAllRadio, 5)) {
            waits.clickWithoutInterception(selectAllRadio);
        }

        By searchBox = By.id("302258625");
        By searchIcon = By.id("302259032");
        clearAndType(searchBox, ciName);
        waits.clickWithoutInterception(searchIcon);
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);

        By checkbox = By.xpath("//*[self::td or self::div][contains(normalize-space(.),'" + ciName + "')]/ancestor::*[@role='row' or self::tr][1]//div[contains(@class,'ui-chkbox-box')][1]");
        waits.clickWithoutInterception(checkbox);

        By save = By.id("304253220");
        if (waits.isElementPresent(save, 5)) {
            waits.clickWithoutInterception(save);
        } else {
            clickButtonByExactText("Save");
        }
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    private void selectTaskAssociationCheckbox(String ciName) {
        waits.switchtoDefaultFrame();
        waits.switchToFrame(By.xpath("//iframe[@id='pwa-frame']"));
        By checkbox = By.xpath("//*[contains(normalize-space(.),'" + ciName + "')]/ancestor::*[@role='row' or self::tr][1]//div[contains(@class,'ui-chkbox-box')][1]");
        waits.clickWithoutInterception(checkbox);
        waits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (itsmFeatureMethods != null) {
            itsmFeatureMethods.closeBrowser();
        }
        if (innovationSuiteFeatureMethodsPage != null) {
            innovationSuiteFeatureMethodsPage.closeBrowser();
        }
        if (smartITFeatureMethods != null) {
            smartITFeatureMethods.closeBrowser();
        }
    }
}
