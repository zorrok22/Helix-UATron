package Apex_MAT;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.dwp.DWPFeatureMethods;
import util.CommonWaits;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
public class SmartIT_PV_Apex extends Launcher{

	SolqaLogger log = new SolqaLogger(SmartIT_PV_Apex.class.getName());
	ReadJson readJson = new ReadJson();
	SmartITFeatureMethodsPage smartITFeatureMethods;
	ITSMFeatureMethods itsmFeatureMethods;
	DWPFeatureMethods dwpFeatureMethods;

	Map<Object, String> smartITUserData,smartITTestData, smartITTestData_VerificationData, dwpUserData, itsmUserData;
	CommonWaits commonWaits = new CommonWaits();
	WebDriver smartITdriver, dwpDriver, itsmDriver;

	@Test(description = "[SmartIT] Creating Incident with PV")
	@SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12059", Products = {Product.Helix_ITSM, Product.SmartIT})
	public void DRSQA_12059_SmartIT_Creating_Incident_with_PV(ITestContext context) throws IOException {

		log.info("***** START : DRSQA_12059_SmartIT_Creating_Incident_with_PV *****");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITUsers", "Apex_AdminUser_Seth"); 
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_12059_Data_CreateData");
		smartITTestData_VerificationData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_12059_Data_VerificationData");

		log.info("Step 1 : Login to SmartIT PV");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_12059_Driver", getDriver());
        context.setAttribute("DRSQA_12059_Product", Product.Helix_SmartIT);
		
		login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("Step 2 : Go to More >> Create New >> Incident");
		smartITFeatureMethods.navigateToCreateNewIncident();
		smartITFeatureMethods.captureScreenshot("DRSQA_12059_navigatedToCreateIncident",SolQAConstants.appSmartIT);

		log.info("Step 3 : Fill all required fields and save the incident in In-Progress state.");
		smartITFeatureMethods.FillIncidentTemplate(smartITTestData);
		smartITFeatureMethods.clickAssigntoMeInIncidentCreatePage();
		smartITFeatureMethods.saveIncident();

		String incidentID = smartITFeatureMethods.FetchIncidentTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_12059_incidentCreated", SolQAConstants.appSmartIT);
		log.pass("************ created incident is: " + incidentID);

		log.info("**** Verifying Incident Data ********");
		smartITFeatureMethods.verifyIncidentData(smartITTestData_VerificationData);

		log.info("Step 4 : Change the status to Pending");
		smartITFeatureMethods.updateIncidentStatus(SolQAConstants.PENDING, SolQAConstants.FUTURE_ENHANCEMENT, "");
		smartITFeatureMethods.saveIncident();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.PENDING);
		smartITFeatureMethods.captureScreenshot("DRSQA_12059_statusChangedToPending", SolQAConstants.appSmartIT);

		log.info("Step 5 : Change the status to Resolved");
		smartITFeatureMethods.updateIncidentStatus(SolQAConstants.RESOLVED, SolQAConstants.AUTOMATED_RESOLUTION_REPORTED, SolQAConstants.RESOLVED);
		smartITFeatureMethods.saveIncident();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.RESOLVED);
		smartITFeatureMethods.captureScreenshot("DRSQA_12059_statusChangedToResolved", SolQAConstants.appSmartIT);

		log.info("Step 6 : Status changed to Closed");
		smartITFeatureMethods.updateIncidentStatus(SolQAConstants.STATUS_CLOSED, SolQAConstants.INFRASTRUCTURE_CHANGE_CREATED, SolQAConstants.SUCCESSFUL);
		smartITFeatureMethods.saveIncident();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.STATUS_CLOSED);
		smartITFeatureMethods.captureScreenshot("DRSQA_12059_statusChangedToClosed", SolQAConstants.appSmartIT);

		log.info("***** END : DRSQA_12059_SmartIT_Creating_Incident_with_PV *****");
	}

	@Test(description = "[SmartIT] Creating Workorder with PV")
	@SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12060", Products = {Product.Helix_ITSM, Product.SmartIT})
	public void DRSQA_12060_SmartIT_Creating_Workorder_with_PV(ITestContext context) throws IOException {

		log.info("***** START : DRSQA_12060_SmartIT_Creating_Workorder_with_PV *****");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITUsers", "Apex_AdminUser_Seth"); 
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_12060_Data");

		log.info("Step 1 : Login to SmartIT PV");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_12060_Driver", getDriver());
        context.setAttribute("DRSQA_12060_Product", Product.Helix_SmartIT);
        
		login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
		smartITFeatureMethods.verifySmartITPageLaunchedSuccessfully();

		log.info("Step 2 : Go to More >> Create New >> WorkOrder");
		smartITFeatureMethods.navigateToCreateNewWorkorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_12060_navigatedToWorkorder",SolQAConstants.appSmartIT);

		log.info("Step 3 : Fill all required fields and save the Workorder in In-Progress state.");
		smartITFeatureMethods.FillWorkorderTemplate(smartITTestData);  
		smartITFeatureMethods.saveWorkorder();
		String workorderID = smartITFeatureMethods.FetchWorkorderTicketID();
		smartITFeatureMethods.captureScreenshot("DRSQA_12060_workorderCreated", SolQAConstants.appSmartIT);
		log.pass("************ created workorder is: " + workorderID);

		log.info("Step 4 : Change the status to Waiting Approval");
		smartITFeatureMethods.updateWorkorderStatus(SolQAConstants.WAITING_APPROVAL);
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.WAITING_APPROVAL);
		smartITFeatureMethods.captureScreenshot("DRSQA_12060_statusChangedToWaitingApproval", SolQAConstants.appSmartIT);

		log.info("Step 5 : Change the status to Planning");
		smartITFeatureMethods.updateWorkorderStatus(SolQAConstants.PLANNING);
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.PLANNING);
		smartITFeatureMethods.captureScreenshot("DRSQA_12060_statusChangedToPlanning", SolQAConstants.appSmartIT);

		log.info("Step 6 : Change the status to In-Progress");
		smartITFeatureMethods.updateWorkorderStatus(SolQAConstants.IN_PROGRESS);
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.IN_PROGRESS);
		smartITFeatureMethods.captureScreenshot("DRSQA_12060_statusChangedToInProgress", SolQAConstants.appSmartIT);

		log.info("Step 7 : Change the status to Completed");
		smartITFeatureMethods.updateWorkorderStatus(SolQAConstants.COMPLETED_STATUS);
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.COMPLETED_STATUS);
		smartITFeatureMethods.captureScreenshot("DRSQA_12060_statusChangedToCompleted", SolQAConstants.appSmartIT);

		log.info("Step 8 : Change status to Closed");
		smartITFeatureMethods.updateWorkorderStatus(SolQAConstants.STATUS_CLOSED);
		smartITFeatureMethods.saveWorkorder();
		smartITFeatureMethods.verifyStatusViewPage(SolQAConstants.STATUS_CLOSED);
		smartITFeatureMethods.captureScreenshot("DRSQA_12060_statusChangedToClosed", SolQAConstants.appSmartIT);

		log.info("***** END : DRSQA_12060_SmartIT_Creating_Workorder_with_PV *****");
	}
	
	@Test(description = "[SmartIT] Creating Change with PV")
    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12061", Products = {Product.Helix_ITSM, Product.SmartIT})
    public void DRSQA_12061_Create_Change_With_PV_In_SmartIT(ITestContext context) throws IOException {
        
        log.info("***** START : DRSQA_12061_Create_Change_With_PV_In_SmartIT *****");

        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITUsers", "Apex_AdminUser_Seth"); 
        smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITTestData","DRSQA_12061_Data");
        
        // Use existing WebDriver from ThreadLocal
        smartITdriver = launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_12061_Driver", getDriver());
        context.setAttribute("DRSQA_12061_Product", Product.Helix_SmartIT);
		
		log.info("Step 1: Login to SmartIT PV");
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
        smartITFeatureMethods = new SmartITFeatureMethodsPage();

        log.info("Step 2: Go to More >> Create New >> Change");
        smartITFeatureMethods.navigateToCreateNewChange();
        smartITFeatureMethods.captureScreenshot("DRSQA_12061_navigatedToCreateChange",SolQAConstants.appSmartIT);
        
        log.info("Step 3: Fill all required fields and save the change");
        smartITFeatureMethods.FillChangeTemplate(smartITTestData);
        smartITFeatureMethods.clickAssigntoMeInCreateChangePage();
        smartITFeatureMethods.saveChange();
        
        String changeID = smartITFeatureMethods.FetchChangeTicketID();
        smartITFeatureMethods.captureScreenshot("DRSQA_12061_changeCreated", SolQAConstants.appSmartIT);
        Assert.assertNotNull(changeID, "Change ID should not be null");
        
        log.info("Step 4 : Change the status to Request For Authorization");
        smartITFeatureMethods.apexUpdateChangeStatus(SolQAConstants.REQUEST_FOR_AUTHORIZATION);
        smartITFeatureMethods.verifyChangeStatus(SolQAConstants.REQUEST_FOR_AUTHORIZATION);
        
        log.info("Step 5 : Click on show Approver and verify approval request is sent to Max");
        smartITFeatureMethods.verifyApproverFromList(smartITTestData.get("ApproverName"));
        smartITFeatureMethods.closeBrowser();

        log.info("Step 6: Login to ITSM with Approver1");
        log.info("Approve the change request by first approver");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12061_Driver", getDriver());
        context.setAttribute("DRSQA_12061_Product", Product.Helix_SmartIT);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_Approver_Max");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

        log.info("Step 7 : Go to quick links >> Approval center and appove the change request.");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        log.info("Approve the change request");
        itsmFeatureMethods.approveChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12061_ITSM_ChangeApproved", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();
        
        log.info("Step 8: Login to ITSM with Approver2");
        log.info("Approve the change request by second approver");
        itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
        setDriver(itsmDriver);
        context.setAttribute("DRSQA_12061_Driver", getDriver());
        context.setAttribute("DRSQA_12061_Product", Product.Helix_SmartIT);

        readJson = new ReadJson();
        itsmFeatureMethods = new ITSMFeatureMethods();
        itsmUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appITSM, "ITSMUsers", "Apex_Prisha");
        login(itsmUserData.get("UserName"), itsmUserData.get("Password"), SolQAConstants.appITSM);

        log.info("Step 9 : Go to quick links >> Approval center and appove the change request.");
        itsmFeatureMethods.navigateToPageFromApplication(SolQAConstants.QUICK_LINKS, SolQAConstants.APPROVAL_CENTRAL);
        log.info("Approve the change request");
        itsmFeatureMethods.approveChange(changeID);
        itsmFeatureMethods.captureScreenshot("DRSQA_12061_ITSM_ChangeApproved", SolQAConstants.appITSM);
        itsmFeatureMethods.closeBrowser();
        
        log.info("Step 10: Again login to SmartIT with bmcopssupport agent");
        smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITUsers", "Apex_AdminUser_Seth");         
        // Use existing WebDriver from ThreadLocal
        smartITdriver = launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_12061_Driver", getDriver());
        context.setAttribute("DRSQA_12061_Product", Product.Helix_SmartIT);
        
		smartITFeatureMethods = new SmartITFeatureMethodsPage();
        login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
        smartITFeatureMethods.navigateToTicketConsole();
        smartITFeatureMethods.applyFilterAndSearchTicketApex(SolQAConstants.CHANGE_REQUEST_TYPE, changeID);
        
        log.info("Step 11 : Verify the status to Planning in-progress");
        smartITFeatureMethods.verifyChangeStatus(SolQAConstants.PLANNING_IN_PROGRESS);
        
        log.info("Step 12 : Agent changes the status to Scheduled for Review and updates the schedule dates");
        smartITFeatureMethods.clickOnChangeEditButton();
        smartITFeatureMethods.addScheduledStartAndEndDate(1, 3);
        smartITFeatureMethods.addActualStartAndEndDate(1, 3);
        smartITFeatureMethods.clickOnSaveChangeButton();
        smartITFeatureMethods.apexUpdateChangeStatus(SolQAConstants.SCHEDULED_FOR_REVIEW);
        smartITFeatureMethods.verifyChangeStatus(SolQAConstants.SCHEDULED_FOR_APPROVAL);
        
        log.info("Step 13 : Click on show approval and approve all pending approvals");
        smartITFeatureMethods.approveRequestFromList();
        smartITFeatureMethods.verifyChangeStatus(SolQAConstants.SCHEDULED);
        
        log.info("Step 14 : Agent changes the status to Implementation in Progress");
        smartITFeatureMethods.apexUpdateChangeStatus(SolQAConstants.IMPLEMENTATION_IN_PROGRESS);
        smartITFeatureMethods.verifyChangeStatus(SolQAConstants.IMPLEMENTATION_IN_PROGRESS);
        
        log.info("Step 15 : Change status to Completed");
        smartITFeatureMethods.apexUpdateChangeStatus(SolQAConstants.COMPLETED_STATUS);
        
        log.info("Step 16 : Click on show approval and approve all pending approvals");
        smartITFeatureMethods.approveRequestFromList();
        smartITFeatureMethods.verifyChangeStatus(SolQAConstants.COMPLETED_STATUS);
        
        log.info("Step 17 : Agent changes the status to close.");
        smartITFeatureMethods.apexUpdateChangeStatus(SolQAConstants.STATUS_CLOSED);
        smartITFeatureMethods.verifyChangeStatus(SolQAConstants.STATUS_CLOSED);
        
		log.info("***** END: DRSQA_12061_Create_Change_With_PV_In_SmartIT *****");
        
	}

	@Test(description = "[SmartIT] Smart Recorder PV Test [Mail Service]")
	@SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12062", Products = {Product.Helix_ITSM, Product.SmartIT})
	public void DRSQA_12062_SmartIT_Smart_Recorder_PV_Test_Mail_Service(ITestContext context) throws Exception {

		log.info("***** START : DRSQA_12062_SmartIT_Smart_Recorder_PV_Test_Mail_Service ******");

		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITUsers", "Apex_SupportAgent_Seth");
		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_12062_Data");

		log.info("Step 1 : Login to SmartIT PV");
		smartITdriver = launchWeb(SolQAConstants.appSmartIT,SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
		context.setAttribute("DRSQA_12062_Driver", getDriver());
        context.setAttribute("DRSQA_12062_Product", Product.Helix_SmartIT);
        
		login(smartITUserData.get("UserName"),smartITUserData.get("Password"),SolQAConstants.appSmartIT);
		smartITFeatureMethods = new SmartITFeatureMethodsPage();


		log.info("Step 2 : Click on SmartRecorder and type @Alex Mail Service");
		smartITFeatureMethods.navigateToSmartRecorder();
		smartITFeatureMethods.captureScreenshot("DRSQA_12062_navigatedToSmartRecorder",SolQAConstants.appSmartIT);
		smartITFeatureMethods.selectCustomerOrAssetInSmartRecorder(smartITTestData.get("CustomerUser"));
		smartITFeatureMethods.enterStringInSmartRecorder(smartITTestData.get("DWPServiceName"));
		smartITFeatureMethods.captureScreenshot("DRSQA_12062_selectedCustomerInSmartRecorder",SolQAConstants.appSmartIT);

		log.info("Step 3 : Select Mail Service and click on create service request.");
		smartITFeatureMethods.selectRecommendedDWPTemplate(smartITTestData);
		smartITFeatureMethods.clickCreateServiceRequestButton();
		smartITFeatureMethods.fillMailSeriveTemplateDetails(smartITTestData);
		smartITFeatureMethods.captureScreenshot("DRSQA_12062_DWPServiceRequestInSmartIT",SolQAConstants.appSmartIT);

		smartITFeatureMethods.clickSubmitRequestButton();
		smartITFeatureMethods.captureScreenshot("DRSQA_12062_ServiceRequestSubmittedInSmartIT",SolQAConstants.appSmartIT);

		String dwpRequestID = smartITFeatureMethods.FetchDWPRequestID();
		smartITFeatureMethods.captureScreenshot("DRSQA_12062_DWPRequestID", SolQAConstants.appSmartIT);

		log.info("Step 4 : Verify request is created with correct customer name and summary");
		smartITFeatureMethods.closeBrowser();


		dwpUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appAPEX + File.separator + SolQAConstants.appDWP, "DWPUsers", "Apex_AdminUser_Alex");

		log.info("Step 5 : Login to DWP");
		dwpDriver = launchWeb(SolQAConstants.appDWP,SolQAConstants.appDWP);
		setDriver(dwpDriver);
		context.setAttribute("DRSQA_12062_Driver", getDriver());
        context.setAttribute("DRSQA_12062_Product", Product.Helix_DWP);
        
		login(dwpUserData.get("UserName"),dwpUserData.get("Password"),SolQAConstants.appDWP);
		dwpFeatureMethods = new DWPFeatureMethods();

		log.info("Step 6 : Open the request created in step 4 from my activity");
		dwpFeatureMethods.openActivityPage();
		dwpFeatureMethods.openSrmRequestByRequestID(dwpRequestID);

		log.info("Step 7 : Verify Enduser is able to open service request and verify request status and its summary");
		dwpFeatureMethods.verifySrdTitle(smartITTestData.get("ServiceName"));
		dwpFeatureMethods.captureScreenshot("DRSQA_12062_CreatedRequestIDInDWPPage", SolQAConstants.appDWP);

		log.info("***** END : DRSQA_12062_SmartIT_Smart_Recorder_PV_Test_Mail_Service ******");
	}

	@AfterMethod(alwaysRun = true)
	public void cleanup() {
		// Use ThreadLocal cleanup to prevent memory leaks
		if (smartITFeatureMethods != null) {
			smartITFeatureMethods.closeBrowser();
		}
		if (itsmFeatureMethods != null) {
			itsmFeatureMethods.closeBrowser();
		}
		if (dwpFeatureMethods != null) {
			dwpFeatureMethods.closeBrowser();
		}
	}
}
