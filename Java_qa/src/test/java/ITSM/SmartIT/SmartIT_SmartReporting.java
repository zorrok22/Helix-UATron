package ITSM.SmartIT;

import java.io.File;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;

import framework.Launcher;
import pages.ITSM.SmartIT.SmartITFeatureMethodsPage;
import pages.ITSM.SmartReporting.SmartReportingFeatureMethods;
import util.CommonWaits;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.UserUpdater;
import util.WebDriverManager;
import util.Listener;

public class SmartIT_SmartReporting extends Launcher {

	static SolqaLogger log = new SolqaLogger(SmartIT_SmartReporting.class.getName());
	CommonWaits commonWaits = new CommonWaits();

	ReadJson readJson = new ReadJson();
	Map<Object, String> smartITUserData,smartITTestData;
	SmartITFeatureMethodsPage smartITFeatureMethodsPage;
	SmartReportingFeatureMethods smartReportingFeatureMethods;
	WebDriver smartITdriver;
	
	
	 UserUpdater smtitUsers;
	    String pathToXls,pathToJson;
	    int counter;
	    String appName="SmartIT";
		
		
		
		
		 @BeforeTest(alwaysRun=true)
		    public void setupUsers() {
		    	counter = Integer.parseInt(configProp.get("runCounter"));
		        if(counter > 0){
		            // Load the JSON file once
		        	smtitUsers = new UserUpdater();
		            String filename = "SmartITUsers";
		                        
		            String excelFilePath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + appName +File.separator + filename +".xlsx";
		            String jsonFilePath = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + appName +File.separator + filename +".json";
		            
		           
		            
		            log.info("Setting up users with runCounter: " + counter);
		            log.info("Excel file path: " + excelFilePath);
		            log.info("JSON file path: " + jsonFilePath);
		            
		            // Load the JSON file
		            smtitUsers.loadJsonFile(jsonFilePath);
		            
		            // Read the specific row based on runCounter
		            // Note: row index 1 corresponds to first data row (after header)
		            // So if runCounter is 3, we read row 3 from Excel
		            smtitUsers.updateMultiUserRuntimeUsersFromExcelRow(excelFilePath, counter);
		            
		            log.info("Updated SmartITUsers.json for run counter: " + counter + " using row: " + counter);
		            
		            // Verify the update by reading the JSON back
		            try {
		                ReadJson verifyJson = new ReadJson();
		                
		                // Verify bmcopssupportagent
		                Map<Object, String> supportAgentData = verifyJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "bmcopssupportagent");
		                log.info("Verification - bmcopssupportagent keys: " + supportAgentData.keySet());
		                log.info("Verification - bmcopssupportagent Username: " + supportAgentData.get("UserName"));
		                log.info("Verification - bmcopssupportagent Password: " + supportAgentData.get("Password"));
		                
		                // Verify bmcopsenduser
		                Map<Object, String> endUserData = verifyJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers", "bmcopsenduser");
		                log.info("Verification - bmcopsenduser keys: " + endUserData.keySet());
		                log.info("Verification - bmcopsenduser Username: " + endUserData.get("UserName"));
		                log.info("Verification - bmcopsenduser Password: " + endUserData.get("Password"));
		                
		            } catch (Exception e) {
		                log.error("Error verifying JSON update: " + e.getMessage());
		            }
		        } else {
		            //No change required
		            log.info("runCounter is 0, no user updates required");
		        }
		    }

	@BeforeMethod (alwaysRun = true)
	public void setup() {
		// Use ThreadLocal WebDriver management - only create one WebDriver per thread
		smartITdriver = launchWeb(SolQAConstants.ITSM_SMARTIT_COMPONENT, SolQAConstants.appSmartIT);
		setDriver(smartITdriver);
	}

	@Test(description = "Incident Cross Launch from Reporting Console to Smart IT console",enabled=false, groups = {"MAT", "SmartIT", "SmartReporting"})
	@SolqaDetails(authorName = "shudeshp",  JiraID = "DRSQA-11806",Products = {Product.SmartIT,Product.Helix_SmartReporting})
	public void DRSQA_11806_IncidentCrossLaunch_SmartIT(ITestContext context) throws Exception {

		log.info("**************** Start DRSQA_11806_IncidentCrossLaunch_SmartIT ******************");
		context.setAttribute("DRSQA_11806_Driver", getDriver());
		context.setAttribute("DRSQA_11806_Product", Product.Helix_SmartIT);

		log.info("******Step 1: Login to Smart_IT**********");
		// Use existing WebDriver from ThreadLocal
		smartITFeatureMethodsPage = new SmartITFeatureMethodsPage();

		smartITTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITTestData", "DRSQA_11806_data");
		smartITUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appSmartIT, "SmartITUsers","bmcopssupportagent");
		login(smartITUserData.get("UserName"), smartITUserData.get("Password"), SolQAConstants.appSmartIT);

		log.info("******Step 2: Nevigate to smart reporting **********");
		log.info("---- Go to More -> reports -----");
		smartITFeatureMethodsPage.goToReportsTab();
		smartReportingFeatureMethods = new SmartReportingFeatureMethods();
		smartReportingFeatureMethods.verifyNavigationToSmartReporting();
		smartReportingFeatureMethods.captureScreenshot("DRSQA_11806_smartReportingLAnding", SolQAConstants.appSmartIT);

		log.info("**************** Step 3: Navigating to Incident management report ******************");
		smartReportingFeatureMethods.waitForSmartReportingSpinnersToDisappear();
		smartReportingFeatureMethods.searchFolder(smartITTestData.get("FolderName"));
		smartReportingFeatureMethods.clickOnFolder(smartITTestData.get("FolderName"));
		smartReportingFeatureMethods.clickOnSubFolder(smartITTestData.get("SubFolderName"));

		log.info("******Step 4:Double click on Incident details **********");
		smartReportingFeatureMethods.clickContentDashBoardLinkForVerifyingDetails(smartITTestData.get("DashboardLink"));
		smartReportingFeatureMethods.enterDateAndclickOnGo();
		smartReportingFeatureMethods.captureScreenshot("DRSQA_11806_searchResults", SolQAConstants.appSmartIT);

		log.info("******Step 5: Open and verify incident open**********");
		smartReportingFeatureMethods.captureScreenshot("DRSQA_11806_incidentOpened", SolQAConstants.appSmartIT);
		smartReportingFeatureMethods.openFirstIncidentAndVerifyDetails();

		log.info("**************** End DRSQA_11806_IncidentCrossLaunch_MT ******************");

	}
	
	@AfterMethod(alwaysRun = true)
	public void cleanup() {
		// Use ThreadLocal cleanup to prevent memory leaks
		if (smartITFeatureMethodsPage != null) {
			smartITFeatureMethodsPage.closeBrowser();
		}
		if (smartReportingFeatureMethods != null) {
			smartReportingFeatureMethods.closeBrowser();
		}
		WebDriverManager.cleanupDriver();
		Listener.cleanupThreadLocal();
	}
}

