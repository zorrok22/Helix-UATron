package FSM;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.FSM.FSMFeatureMethodsPage;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class FSMTests extends Launcher {
	
    public static SolqaLogger log = new SolqaLogger(FSMTests.class.getName());
    ReadJson readJson = new ReadJson();
    FSMFeatureMethodsPage fsmFeatureMethodsPage;
    Map<Object, String> isUserData, isTestData;
    WebDriver isDriver;
    
    @BeforeMethod
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
    	isDriver = launchWeb(SolQAConstants.FIELD_SERVICE_MANAGEMENT, SolQAConstants.appIS);
        setDriver(isDriver);
    }

    @Test(description = "FSM - Work Order Assignment")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-15706",Products = {Product.Field_Service_Management})
    public void DRSQA_15706_FSM_Work_Order_Assignment(ITestContext context) throws IOException {
    	log.info("***** START : DRSQA_15706_FSM_Work_Order_Assignment *****");

		context.setAttribute("FSM_WorkOrderAssignments_WebDriver", getDriver());
		context.setAttribute("FSM_WorkOrderAssignments_Product", Product.Helix_InnovationStudio);

		isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appFSM + File.separator + SolQAConstants.appIS, "ISUsers", "drikusg");
		isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appFSM + File.separator + SolQAConstants.appIS, "ISTestData", "FSM_WorkOrderAssignments");
		login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
		fsmFeatureMethodsPage = new FSMFeatureMethodsPage();
		
		//Verify FSM Page is loaded
		fsmFeatureMethodsPage.verifyFSMPageLoaded();
		
		log.info("Step 1 : Login to Innovation Studio as a admin user & navigate to FSM Work Order Assignments page");
		fsmFeatureMethodsPage.navigateToDispatcherConsoleItem(isTestData.get("AssignmentType"));
		fsmFeatureMethodsPage.verifyPageIsNavigated(isTestData.get("AssignmentType"));
		
		log.info("Step 2 : Select Scheduling Options from Work Order Assignment page");
		fsmFeatureMethodsPage.selectSchedulingOptions(isTestData.get("SchedulingOption"));
		
		log.info("Step 3 : Select a Work Order in the list");
		fsmFeatureMethodsPage.selectFirstWorkOrderFromList();
		
		log.info("Step 4 : Scroll Down and select the Select Group option");
		fsmFeatureMethodsPage.clickOnSelectGroup();
		
		log.info("Step 5 : On the displayed dialogue select a Company and Support Group");
		fsmFeatureMethodsPage.selectCompanyAndSupportGroup(isTestData.get("companyName"),isTestData.get("supportGroup"));
		//Verify Support Group is selected
		fsmFeatureMethodsPage.verifySupportGroupIsSelected(isTestData.get("supportGroup"));
		
		log.info("Step 6 : Select the Show Map Option to display it and then Go Back");
		fsmFeatureMethodsPage.clickOnShowMap();
		//Verify Map is displayed
		fsmFeatureMethodsPage.verifyMapIsDisplayed();
		fsmFeatureMethodsPage.clickOnBackButton();
		
		log.info("Step 7 : Select the Show USer Route option and then Go Back");
		fsmFeatureMethodsPage.clickOnShowUserRoute();
		//Verify Route is displayed
		fsmFeatureMethodsPage.verifyRouteIsDisplayed();
		fsmFeatureMethodsPage.clickOnBackButton();
		
		log.info("Step 8 : Select the Inventory Search Option");
		fsmFeatureMethodsPage.clickOnInventorySearch();
		//Verify Inventory Search is displayed
		fsmFeatureMethodsPage.verifyInventorySearchIsDisplayed();
		
		log.info("Step 9 : On the Inventory Search screen select Inventory type and Select Inventory Location");
		fsmFeatureMethodsPage.selectInventoryTypeAndInventoryLocation(isTestData.get("inventoryType"),isTestData.get("inventoryLocation"));
		//Verify Inventory Type and Location is present
		fsmFeatureMethodsPage.verifyInventoryTypeAndLocationIsPresent(isTestData.get("inventoryType"),isTestData.get("inventoryLocation"));
		
		log.info("Step 10 : Select an Inventory Item.");
		fsmFeatureMethodsPage.selectFirstInventoryFromList();
		
		log.info("Step 11 : Select an item in the template list");
		fsmFeatureMethodsPage.selectWorkOrderTemplate(isTestData.get("workOrderTemplate"));
		
		log.info("Step 12 : select the Assign Work Order button");
		fsmFeatureMethodsPage.selectFirstWorkOrderFromList();
		fsmFeatureMethodsPage.clickOnAssignWorkOrder();
		
		log.info("Step 13 : Add duration on site and save the work order");
		fsmFeatureMethodsPage.addDurationOnSite(isTestData.get("durationTime"));
		
		log.info("***** END : DRSQA_15706_FSM_Work_Order_Assignment *****");	
    }
    
    @Test(description = "FSM - FSA Timelines")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-15707",Products = {Product.Field_Service_Management})
    public void DRSQA_15707_FSM_FSA_Timelines(ITestContext context) throws IOException {
    	
    	log.info("***** START : DRSQA_15707_FSM_FSA_Timelines *****");

		context.setAttribute("FSM_WorkOrderAssignments_WebDriver", getDriver());
		context.setAttribute("FSM_WorkOrderAssignments_Product", Product.Helix_InnovationStudio);

		isUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appFSM + File.separator + SolQAConstants.appIS, "ISUsers", "drikusg");
		isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appFSM + File.separator + SolQAConstants.appIS, "ISTestData", "FSM_Timeline");
		login(isUserData.get("UserName"), isUserData.get("Password"), SolQAConstants.appIS);
		fsmFeatureMethodsPage = new FSMFeatureMethodsPage();
		
		//Verify FSM Page is loaded
		fsmFeatureMethodsPage.verifyFSMPageLoaded();
		
		log.info("Step 1 : Login to Innovation Studio as a admin user & navigate to FSM Timelines page");
		fsmFeatureMethodsPage.navigateToDispatcherConsoleItem(isTestData.get("consoleType"));
		fsmFeatureMethodsPage.verifyPageIsNavigated(isTestData.get("consoleHeader"));
		
		log.info("Step 2 : Select a Timeline details from the list");
		fsmFeatureMethodsPage.selectTimelineSupportGroupAndRouteName(isTestData.get("supportGroup"),isTestData.get("routeName"));
		//Verify Timeline Support Group and Route Name
		fsmFeatureMethodsPage.verifyTimeineSupportGroupAndRouteName(isTestData.get("supportGroup"),isTestData.get("routeName"));

		log.info("Step 3 : Select a Work order object and drag it to a Field Agents corresponding Day and Time.");
		fsmFeatureMethodsPage.dragAndDropFirstUnscheduledWorkOrderCardToDate();

		log.info("Step 4 : Select the Inventory Search Option");
		fsmFeatureMethodsPage.clickOnInventorySearch();
		//Verify Inventory Search is displayed
		fsmFeatureMethodsPage.verifyInventorySearchIsDisplayed();
		
		log.info("Step 5 : On the Inventory Search screen select Inventory type and Select Inventory Location");
		fsmFeatureMethodsPage.selectInventoryTypeAndInventoryLocation(isTestData.get("inventoryType"),isTestData.get("inventoryLocation"));
		//Verify Inventory Type and Location is present
		fsmFeatureMethodsPage.verifyInventoryTypeAndLocationIsPresent(isTestData.get("inventoryType"),isTestData.get("inventoryLocation"));
		
		log.info("Step 6 : Select an Inventory Item.");
		fsmFeatureMethodsPage.selectFirstInventoryFromList();
		
		log.info("Step 7 : Select an item in the template list");
		fsmFeatureMethodsPage.selectWorkOrderTemplate(isTestData.get("workOrderTemplate"));
		
		log.info("Step 8 : The assigned Work Order will disappear from the timeline view as it is now assigned");
		fsmFeatureMethodsPage.verifyWOIsNotPresentInDatePanel();
    	
		log.info("***** END : DRSQA_15707_FSM_FSA_Timelines *****");	
    }    
    
    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        if (fsmFeatureMethodsPage != null) {
        	fsmFeatureMethodsPage.closeBrowser();
        }
    }

}
