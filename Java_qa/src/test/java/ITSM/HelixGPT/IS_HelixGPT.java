package ITSM.HelixGPT;

import java.io.File;
import java.util.Map;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import framework.Launcher;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import org.openqa.selenium.WebDriver;

public class IS_HelixGPT extends Launcher {

	static SolqaLogger log = new SolqaLogger(IS_HelixGPT.class.getName());
	ReadJson readJson = new ReadJson();
	Map<Object, String> ISTestData_12799, ISTestData_14851, ISTestData_12292;
	Map<Object, String> ISUsersData;

	InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	WebDriver itsmDriver, isDriver;
	String recordName = "student";
	String recordId;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		itsmDriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
		setDriver(itsmDriver);
	}

	@Test(description = "Create Record Definition Using BMC Helix GPT-Phase1", groups = { "Innovation_Suite","Custom_Application", "HelixGPT", "Regression_P3" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-12799", Products = { Product.Innovation_Suite,Product.HelixGPT })
	public void DRSQA_12799_Create_Record_Definition_In_Application_In_Innovation_Studio(ITestContext context) throws InterruptedException {

		log.info(
				"**************** Start DRSQA-12799: Create Record Definition Using BMC Helix GPT-Phase1 ******************");

		log.info("Step 1: Login to Innovation Studio with admin user");
		context.setAttribute("DRSQA_12799_Driver", getDriver());
		context.setAttribute("DRSQA_12799_Product", Product.Helix_Platform);

		ISTestData_12799 = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData",
				"DRSQA_12799Data");

		log.info("Launching Innovation Suite");
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

		log.info("Step 2: Create a custom application");
		innovationSuiteFeatureMethodsPage.createCustomApplication(ISTestData_12799.get("applicationName"),
				ISTestData_12799.get("applicationShortName"), ISTestData_12799.get("applicationGroupId"));
		log.info("Custom application created successfully");

		log.info("Step 2: Navigate to 'SOLQA_Application' bundle");
		innovationSuiteFeatureMethodsPage.navigateToBundle(ISTestData_12799.get("applicationName"));
		log.info("Navigated to Automated Asset Assignment bundle");

		log.info("Step 3: Create a New Process with 'Create Record: Student' via HelixGPT");
		innovationSuiteFeatureMethodsPage.createDefinitionViaHelixGPT(ISTestData_12799.get("processType1"),
				ISTestData_12799.get("processDetails1"), ISTestData_12799.get("expectedResponse1"));
		log.info("Step 3: Record definition 'student' via HelixGPT got created");

		innovationSuiteFeatureMethodsPage.clickRefreshButton();

		innovationSuiteFeatureMethodsPage.validateRecordFields(ISTestData_12799.get("recordName1"));

		log.info("Step 4: Create a New Process with 'Create Record: Student' via HelixGPT");
		innovationSuiteFeatureMethodsPage.createDefinitionViaHelixGPT(ISTestData_12799.get("processType2"),
				ISTestData_12799.get("processDetails2"), ISTestData_12799.get("expectedResponse2"));
		log.info("Step 4: Record definition 'student' via HelixGPT got created");

		log.info("Step 5: Create a New Process with 'Create Record: employee' with custome fields via HelixGPT");
		innovationSuiteFeatureMethodsPage.createDefinitionViaHelixGPT(ISTestData_12799.get("processType3"),
				ISTestData_12799.get("processDetails3"), ISTestData_12799.get("expectedResponse3"));
		log.info("Step 5: Record definition 'employee' via HelixGPT got created");

		innovationSuiteFeatureMethodsPage.clickRefreshButton();

		innovationSuiteFeatureMethodsPage.validateRecordFields(ISTestData_12799.get("recordName2"),
				ISTestData_12799.get("customField1"), ISTestData_12799.get("customField2"),
				ISTestData_12799.get("customField3"));

		log.info(
				"**************** End DRSQA-12799: Create Record Definition Using BMC Helix GPT-Phase1 ******************");
	}

	@Test(description = "[25.4.00] BMC HelixGPT- Process Designer Productization-Phase 1",  groups = {
			"Innovation_Suite", "Regression_P3",
			"HelixGPT" }, dependsOnMethods = "DRSQA_12799_Create_Record_Definition_In_Application_In_Innovation_Studio")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-14851", Products = { Product.Innovation_Suite,
			Product.HelixGPT })
	public void DRSQA_14851_Validate_Process_Designer_Production_Phase1_Functionality(ITestContext context)
			throws InterruptedException {

		log.info(
				"**************** Start DRSQA-14851: Validate BMC HelixGPT- Process Designer Productization-Phase 1 Functionality ******************");

		log.info("Step 1: Login to Innovation Studio with admin user");
		context.setAttribute("DRSQA_14851_Driver", getDriver());
		context.setAttribute("DRSQA_14851_Product", Product.Helix_Platform);

		ISTestData_14851 = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData",
				"DRSQA_14851Data");
		ISTestData_12799 = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS, "ISTestData",
				"DRSQA_12799Data");

		log.info("Launching Innovation Suite");
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

		log.info("Step 2: Navigate to 'solqatesting' bundle");
		innovationSuiteFeatureMethodsPage.navigateToBundle(ISTestData_14851.get("applicationName"));
		log.info("Navigated to Automated Asset Assignment bundle");

		log.info("Step 3: Click on Processes tab");
		innovationSuiteFeatureMethodsPage.clickProcessesTab();
		log.info("Processes tab clicked successfully");

		log.info("Step 4: Create a New Process with 'Create Record' via HelixGPT");
		innovationSuiteFeatureMethodsPage.createDefinitionViaHelixGPT(ISTestData_14851.get("processType1"),
				ISTestData_14851.get("processDetails1"), ISTestData_14851.get("expectedResponse1"));
		innovationSuiteFeatureMethodsPage.enableProcess(ISTestData_14851.get("processName1"));
		log.info("Step 4: Process with 'Create Record' via HelixGPT got created and enabled successfully ");

		log.info("Step 5: Create a New Process with 'Create Update' via HelixGPT");
		innovationSuiteFeatureMethodsPage.createDefinitionViaHelixGPT(ISTestData_14851.get("processType2"),
				ISTestData_14851.get("processDetails2"), ISTestData_14851.get("expectedResponse2"));
		innovationSuiteFeatureMethodsPage.enableProcess(ISTestData_14851.get("processName2"));
		log.info("Step 5: Process with 'Create Update' via HelixGPT got created and enabled successfully");

		log.info("Step 7: Create a New Process with 'Get Record' via HelixGPT");
		innovationSuiteFeatureMethodsPage.createDefinitionViaHelixGPT(ISTestData_14851.get("processType4"),
				ISTestData_14851.get("processDetails4"), ISTestData_14851.get("expectedResponse4"));
		innovationSuiteFeatureMethodsPage.enableProcess(ISTestData_14851.get("processName4"));
		log.info("Step 7: Process with 'Get Record' via HelixGPT got created and enabled successfully");

		log.info("Step 8: Create a New Process with 'Delete Record' via HelixGPT");
		innovationSuiteFeatureMethodsPage.createDefinitionViaHelixGPT(ISTestData_14851.get("processType3"),
				ISTestData_14851.get("processDetails3"), ISTestData_14851.get("expectedResponse3"));
		innovationSuiteFeatureMethodsPage.enableProcess(ISTestData_14851.get("processName3"));
		log.info("Step 8: Process with 'Delete Record' via HelixGPT got created and enabled successfully");

		log.info("Run Process");
		innovationSuiteFeatureMethodsPage.runCreateRecordProcess(recordName, ISTestData_14851.get("processName1"));
		log.info("Run Process Successful");

		recordId = innovationSuiteFeatureMethodsPage.getFirstRecordId(recordName);

		log.info("Run Process");
		innovationSuiteFeatureMethodsPage.runUpdateRecordProcess(recordName, ISTestData_14851.get("processName2"),
				recordId);
		log.info("Run Process Successful");

		log.info("Run Process");
		innovationSuiteFeatureMethodsPage.runGetRecordProcess(ISTestData_14851.get("processName4"), recordId);
		log.info("Run Process Successful");

		log.info("Run Process");
		innovationSuiteFeatureMethodsPage.runDeleteRecordProcess(recordName, ISTestData_14851.get("processName3"),
				recordId);
		log.info("Run Process Successful");

		log.info("Deleting created Records");
		innovationSuiteFeatureMethodsPage.deleteRecords(ISTestData_12799.get("recordName1"));
		innovationSuiteFeatureMethodsPage.deleteRecords(ISTestData_12799.get("recordName2"));

		log.info("Deleting created Processe");
		innovationSuiteFeatureMethodsPage.deleteProcesses(ISTestData_14851.get("processName1"));
		innovationSuiteFeatureMethodsPage.deleteProcesses(ISTestData_14851.get("processName2"));
		innovationSuiteFeatureMethodsPage.deleteProcesses(ISTestData_14851.get("processName3"));
		innovationSuiteFeatureMethodsPage.deleteProcesses(ISTestData_14851.get("processName4"));

		log.info(
				"**************** End DRSQA-14851: BMC HelixGPT- Process Designer Productization-Phase 1 ******************");
	}

	@Test(description = "Retrieving data from attachments in HelixGPT Agent Studio", groups = { "HelixGPT", "IS", "AgentStudio" })
	@SolqaDetails(authorName = "mhedaoo", TTID = "", JiraID = "DRSQA-12292", Products = { Product.HelixGPT, Product.Helix_ITSM })
	public void DRSQA_12292_RetrievingDataFromAttachments(ITestContext context) throws Exception {

		log.info("------------START: DRSQA-12292: Retrieving data from attachments----------");

		log.info("---------Step1: Login to IS as pbunyon / __USER_PASSWORD__------------");
		isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
		login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appIS);
		context.setAttribute("DRSQA_12292_Driver", getDriver());
		context.setAttribute("DRSQA_12292_Product", Product.Helix_ITSM);

		log.info("---------Step2: Initialize feature methods and navigate to HelixGPT Agent Studio------------");
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();
		innovationSuiteFeatureMethodsPage.navigateToBundle(SolQAConstants.HELIXGPT_AGENT_STUDIO);

		log.info("---------Step3-14: Create Record Definition with Attachment Field------------");
		Map<Object, String> testData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "DRSQA_12292_Data");
		String recordName = testData.get("field");
		String fieldName = testData.get("field");
		String fieldId = innovationSuiteFeatureMethodsPage.createRecordDefinitionWithAttachmentField(recordName, fieldName, "DRSQA_12292");
		log.info("Record Definition created with Field ID: " + fieldId);

		log.info("---------Step15-21: Upload Attachment Instance------------");
		// File will be loaded from resources/HelixGPT/ directory
		File attachmentFile = new File(testData.get("attachmentFileName"));
		String description = testData.get("attachmentDescription");
		String status = testData.get("attachmentStatus");
		innovationSuiteFeatureMethodsPage.uploadAttachmentInstance(recordName, fieldName, attachmentFile, description, status, "DRSQA_12292");
		log.info("Attachment instance uploaded successfully");

		log.info("---------Step22-31: Create Connection Record------------");
		String connectionRecordDefinitionName = testData.get("connectionRecordDefinitionName");
		String connectionStatus = testData.get("connectionStatus");
		String connectionName = testData.get("connectionName");
		String connectionRecordDefinition = testData.get("connectionRecordDefinition");
		String connectionDataSourceId = testData.get("connectionDataSourceId");

		String connectionRecordId = innovationSuiteFeatureMethodsPage.createConnectionRecord(
			connectionRecordDefinitionName,
			fieldId,
			connectionStatus,
			connectionName,
			connectionRecordDefinition,
			connectionDataSourceId
		);
		Assert.assertFalse(connectionRecordId.isEmpty(), "Connection Record ID is empty");
		log.info("Connection Record created successfully with ID: " + connectionRecordId);

		log.info("---------Step32-41: Create Data Connection Job------------");
		String dataConnectionJobRecordDefinitionName = testData.get("dataConnectionJobRecordDefinitionName");
		String dataConnectionJobDescription = testData.get("dataConnectionJobDescription");
		String dataConnectionJobStatus = testData.get("dataConnectionJobStatus");
		String dataConnectionJobType = testData.get("dataConnectionJobType");
		String dataConnectionJobDatasource = testData.get("dataConnectionJobDatasource");

		String dataConnectionJobId = innovationSuiteFeatureMethodsPage.createDataConnectionJob(
			dataConnectionJobRecordDefinitionName,
			dataConnectionJobDescription,
			dataConnectionJobStatus,
			dataConnectionJobType,
			connectionRecordId,
			true, // executeNow
			dataConnectionJobDatasource,
			"DRSQA_12292"
		);
		Assert.assertFalse(dataConnectionJobId.isEmpty(), "Data Connection Job ID is empty");
		log.info("Data Connection Job created successfully with ID: " + dataConnectionJobId);

		log.info("---------Step42: Login to HelixGPT chatbot as rjames / __USER_PASSWORD__------------");
		context.setAttribute("DRSQA_12292_Driver", getDriver());
		context.setAttribute("DRSQA_12292_Product", Product.HelixGPT);
		ISUsersData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appITSM, "ITSMUsers", "pbunyon");
		ISTestData_12292 = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appHelixGPT, "HelixGPTAgentStudioTestData", "DRSQA_12292_Data");
		log.info("Launching HelixGPT Chatbot");
		WebDriver helixGPTDriver = launchWeb(SolQAConstants.appHelixGPT, SolQAConstants.appHelixGPT);
		setDriver(helixGPTDriver);
		login(ISUsersData.get("UserName"), ISUsersData.get("Password"), SolQAConstants.appHelixGPT);
		log.info("Logged in to HelixGPT chatbot as user: " + ISUsersData.get("UserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyLoginToHelixGPTBot();

		log.info("---------Step43: Search for query from test data------------");
		String searchQuery = ISTestData_12292.get("input");
		log.info("Search Query: " + searchQuery);
		innovationSuiteFeatureMethodsPage.sendMessage(searchQuery);

		log.info("---------Step44: Verify the response from HelixGPT------------");
		String gptResponse = innovationSuiteFeatureMethodsPage.getAllGPTResponseString();
		log.info("HelixGPT Response: " + gptResponse);

		log.info("---------Step45: Verify response contains expected content using DeepEval------------");
		String context_text = ISTestData_12292.get("Context");
		String expectedResponse = ISTestData_12292.get("ExpectedResponse");
		innovationSuiteFeatureMethodsPage.verifyHelixGPTResult(context_text, searchQuery, expectedResponse, gptResponse);
		log.info("DeepEval verification completed successfully");

		log.info("------------END: DRSQA-12292: Retrieving data from attachments completed successfully----------");
		log.info("HelixGPT chatbot verification completed successfully");
	}

	
	@AfterMethod(alwaysRun = true)
	public void cleanup() {
		// Use ThreadLocal cleanup to prevent memory leaks
		if (innovationSuiteFeatureMethodsPage != null) {
			innovationSuiteFeatureMethodsPage.closeBrowser();
		}
		if (innovationSuiteFeatureMethodsPage != null) {
			innovationSuiteFeatureMethodsPage.closeBrowser();
		}	
	}
}
