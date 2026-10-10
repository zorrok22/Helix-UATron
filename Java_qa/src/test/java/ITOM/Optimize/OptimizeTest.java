package ITOM.Optimize;

import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Optimize.OptimizeFeatureMethods;
import util.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OptimizeTest extends Launcher {
    public static SolqaLogger log = new SolqaLogger(OptimizeTest.class.getName());
    WebDriver adeDriver;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    AIOpsFeatureMethods aiopsFeatureMethods;
    Portal_FeatureMethods portalFeatureMethods;
    Map<Object, String> optimizeServiceNameData;
    OptimizeFeatureMethods optimizeFeatureMethods;
    CommonWaits commonWaits;
    Map<Object, String> itomUserData, optimizeSaturationForecastAlertData, filterNameWorkspaceData, filterLastActivityWorkspaceData, filterCreationDateWorkspaceData, filterCreationDateAndLastActivityWorkspaceData;
    String userKey = "aananda"; // Default user key
    ReadJson readJson;
    String past30DaysDate, past20DaysDate, past25DaysDate;

    @BeforeTest(alwaysRun = true)
    public void setupITOMUsers() {
        // Single line - gets user data for both runCounter=0 (properties.config) and >0 (Excel)
        itomUserData = ITOM.ITOM_Prerequisites.updateITOMUserDataFromExcel(config);
        userKey = "itomadminuser";
        log.info("OptimizeTest will use user: " + itomUserData.get("UserName"));
    }

    public void doPreconditions(String username, String password) {
        adeDriver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        login(username, password, SolQAConstants.appITOM);
        portalFeatureMethods = new Portal_FeatureMethods();
        optimizeFeatureMethods = new OptimizeFeatureMethods();
        commonWaits = new CommonWaits();
    }

    @Test(description = "Create/View/Search/Edit Optimize Events.", singleThreaded = true, groups = {"Optimize"})
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12429", Products = {Product.Helix_Optimize, Product.ADE_OperationsManagement})
    public void DRSQA_12429_Create_View_Search_Event(ITestContext context) {

        // User data already loaded in @BeforeTest - just use it directly

        String username = itomUserData.get("UserName");
        String password = itomUserData.get("Password");
        log.info("Running test with user: " + username + " (UserKey: " + userKey + ")");

        log.info("Starting Create/View/Search/Edit Optimize Events test.");
        adeDriver = launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        context.setAttribute("DRSQA_12429_Driver", adeDriver);
        context.setAttribute("DRSQA_12429_Product", Product.ADE_Platform);

        portalFeatureMethods = new Portal_FeatureMethods();
        optimizeFeatureMethods = new OptimizeFeatureMethods();
        commonWaits = new CommonWaits();

        login(username, password, SolQAConstants.appITOM);
        portalFeatureMethods.verifyLogin();

        String uniqueEventMessage = commonWaits.generateRandomString();
        log.info("Go to Optimize and create Event. View and event generated in Optimize");
        optimizeFeatureMethods.createEventFromOptimize(uniqueEventMessage);
        log.info("Go to BHOM. Search for the optimize Event: " + uniqueEventMessage);
        optimizeFeatureMethods.VerifyEventFromOptimize(uniqueEventMessage);
    }

    @Test(description = "BHOM ETL: Create and execute BHOM ETL. https://jira.example.com/browse/DRSQA-12417 ", groups = {"Regression"},enabled=false)
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12417", Products = {Product.Helix_Optimize, Product.ADE_OperationsManagement})
    public void DRSQA_12417_BHOM_ETL_create_and_execute(ITestContext context) {
        log.info("Starting BHOM ETL: Create and execute BHOM ETL.");
        doPreconditions(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
        log.info("Go to Optimize and create BHOM ETL. ");
        optimizeFeatureMethods.addBHOMETL();

        log.info("Execute the BHOM ETL created. & see that it gets executed successfully. " );
        optimizeFeatureMethods.runBHOMETLAndVerifyStatus();

        log.info("Delete the BHOM ETL created.");
        optimizeFeatureMethods.deleteBHOMETL();

    }
    
    @Test(description = "Create events for saturation forecasting alerts", groups = {"Regression"})
    @SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-12440", Products = {Product.Helix_Optimize, Product.ADE_OperationsManagement})
    public void DRSQA_12440_Create_Event_For_Saturation_Forecasting_Alerts() {
        log.info("==== TEST STARTED: DRSQA-12440 ====");
        log.info("Objective: Create events for saturation forecasting alerts");
        
        try {
        	doPreconditions(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
        	
            readJson = new ReadJson();
            optimizeSaturationForecastAlertData = readJson.getInputValuesMapIncludingLabelField(
                "ITOM" + File.separator + SolQAConstants.HELIX_OPTIMIZE, 
                "OptimizeTestData", 
                "DRSQA_12440_Data");
            
            log.info("---- Go to Optimize and add new Rule ----");
            String uniqueRuleName = "Rule" + commonWaits.generateRandomString();
            optimizeFeatureMethods.addNewRule(
                uniqueRuleName, 
                optimizeSaturationForecastAlertData.get("templateGroup"), 
                optimizeSaturationForecastAlertData.get("templateName"), 
                optimizeSaturationForecastAlertData.get("actionToPerform"));
            
            log.info("---- Run rule and verify it gets executed successfully ----");
            optimizeFeatureMethods.runRuleAndVerify(uniqueRuleName);
            
            log.info("---- Go to System tasks, search for Optimizer - daily and run it ----");
            optimizeFeatureMethods.runSystemTaskAndWaitForRunFinish(optimizeSaturationForecastAlertData.get("systemTaskName"));
            
            log.info("---- Go to BHOM and verify events details for saturation forecasting alerts ----");
            optimizeFeatureMethods.verifySaturationForecastAlertEventForHost(
            		optimizeSaturationForecastAlertData.get("hostName"), 
            		optimizeSaturationForecastAlertData.get("eventMessage"));
         
            log.info("---- Close opened saturation forecasting alert ----");
            optimizeFeatureMethods.closeOpenedEvent();
            
            log.info("---- Delete the rule created for saturation forecasting alerts ----");
            optimizeFeatureMethods.deleteRuleAndVerify(uniqueRuleName);
            
            log.info("==== TEST COMPLETED SUCCESSFULLY: DRSQA-12440 ====");
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage(), e);
            throw new AssertionError("Failed to execute test case: " + e);
        }
    }

    @Test(description = "Verify support of sorting in Unassigned domain in Workspace", groups = {"Regression"})
    @SolqaDetails(authorName = "sasatark", JiraID = "DRSQA-16325", Products = {Product.Helix_Optimize})
    public void verify_Optimize_Workspace_Sorting_Functionality(ITestContext context) {
        // Set test context
        context.setAttribute("TestCaseName", "DRSQA-16325");
        
        log.info("========== Starting Optimize Workspace Sorting Functionality Test ==========");
        
        // Step 1: Login to Helix Portal and cross launch Optimize
        log.info("Step 1: Login to Helix Portal and cross launch Optimize");
        doPreconditions(configProp.get("adminUserName"), configProp.get("adminPassword"));

        IntelligentAutomationPage iaPage = new IntelligentAutomationPage();
        iaPage.navigateToOptimizePage();
        iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
        optimizeFeatureMethods = new OptimizeFeatureMethods();
        
        // Step 2: Go to "Workspace", scroll down and click on "All systems and business drivers"
        log.info("Step 2: Navigate to Workspace -> All systems and business drivers");
        optimizeFeatureMethods.navigateToAllSystemsAndBusinessDrivers();
        
        // Step 3: Click on "Unassigned" domain
        log.info("Step 3: Click on 'Unassigned' domain");
        optimizeFeatureMethods.clickOnUnassignedDomain();
        
        // Steps 4-8: Verify sorting functionality for "Date of creation" column
        log.info("Steps 4-8: Verifying sorting functionality for 'Date of creation' column");
        optimizeFeatureMethods.verifySortingFunctionalityForDateOfCreation();
        
        // Step 9: Repeat for "Last Activity" column
        log.info("Step 9: Verifying sorting functionality for 'Last Activity' column");
        optimizeFeatureMethods.verifySortingFunctionalityForLastActivity();
        
        log.info("========== Completed Optimize Workspace Sorting Functionality Test ==========");
    }
    
	@Test(description = "Verify support of filtering 'Name' in Unassigned domain in Workspace", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-16581", Products = { Product.Helix_Optimize })
	public void verify_support_filtering_name_in_unassigned_domain(ITestContext context) {
		// Set test context
		context.setAttribute("TestCaseName", "DRSQA-16581");

		// Load user data from JSON
		readJson = new ReadJson();
		filterNameWorkspaceData = readJson.getInputValuesMapIncludingLabelField(
				"ITOM" + File.separator + SolQAConstants.HELIX_OPTIMIZE, "OptimizeTestData", "DRSQA_16581_Data");

		log.info("========== Starting Optimize Workspace 'Name' Filter Functionality Test ==========");

		// Step 1: Login to Helix Portal and cross launch Optimize
		log.info("Step 1: Login to Helix Portal and cross launch Optimize");
		doPreconditions(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
		
		IntelligentAutomationPage iaPage = new IntelligentAutomationPage();
		iaPage.navigateToOptimizePage();
		iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		// Step 2: Go to "Workspace", scroll down and click on "All systems and business drivers"
		log.info("Step 2: Navigate to Workspace -> All systems and business drivers");
		optimizeFeatureMethods.navigateToAllSystemsAndBusinessDrivers();

		// Step 3: Click on "Unassigned" domain
		log.info("Step 3: Click on 'Unassigned' domain");
		optimizeFeatureMethods.clickOnUnassignedDomain();

		// Step 4-7: Apply filter's for 'Name' column
		String commonSubstringInColumn = optimizeFeatureMethods
				.findCommonPatternInList(Integer.valueOf(filterNameWorkspaceData.get("columnIndex")));
		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterNameWorkspaceData.get("columnName"),
				Integer.valueOf(filterNameWorkspaceData.get("columnIndex")),
				filterNameWorkspaceData.get("filterOperator1"), commonSubstringInColumn);

		String searchColumnElement = optimizeFeatureMethods
				.getElementFromColumn(Integer.valueOf(filterNameWorkspaceData.get("columnIndex")));
		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterNameWorkspaceData.get("columnName"),
				Integer.valueOf(filterNameWorkspaceData.get("columnIndex")),
				filterNameWorkspaceData.get("filterOperator2"), searchColumnElement);

		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterNameWorkspaceData.get("columnName"),
				Integer.valueOf(filterNameWorkspaceData.get("columnIndex")),
				filterNameWorkspaceData.get("filterOperator3"), searchColumnElement);

		// Steps 8: Sort the filtered list
		log.info("Steps 4-8: Verifying sorting functionality for 'Date of creation' column");
		optimizeFeatureMethods.verifySortingFunctionalityForName();

		log.info("========== Completed Optimize Workspace 'Name' Filter Functionality Test ==========");
	}

	@Test(description = "Verify support of filtering 'last activity' in Unassigned domain in Workspace", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-16580", Products = { Product.Helix_Optimize })
	public void verify_support_filtering_last_activity_in_unassigned_domain(ITestContext context) {
		// Set test context
		context.setAttribute("TestCaseName", "DRSQA-16580");

		// Load user data from JSON
		readJson = new ReadJson();
		filterLastActivityWorkspaceData = readJson.getInputValuesMapIncludingLabelField(
				"ITOM" + File.separator + SolQAConstants.HELIX_OPTIMIZE, "OptimizeTestData", "DRSQA_16580_Data");

		log.info("========== Starting Optimize Workspace 'last activity' Filter Functionality Test ==========");

		// Step 1: Login to Helix Portal and cross launch Optimize
		log.info("Step 1: Login to Helix Portal and cross launch Optimize");
		doPreconditions(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		IntelligentAutomationPage iaPage = new IntelligentAutomationPage();
		iaPage.navigateToOptimizePage();
		iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		// Step 2: Go to "Workspace", scroll down and click on "All systems and business drivers"
		log.info("Step 2: Navigate to Workspace -> All systems and business drivers");
		optimizeFeatureMethods.navigateToAllSystemsAndBusinessDrivers();

		// Step 3: Click on "Unassigned" domain
		log.info("Step 3: Click on 'Unassigned' domain");
		optimizeFeatureMethods.clickOnUnassignedDomain();

		// Step 4-7: Apply filter's for 'last activity' column
		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterLastActivityWorkspaceData.get("columnName"),
				Integer.valueOf(filterLastActivityWorkspaceData.get("columnIndex")),
				filterLastActivityWorkspaceData.get("filterOperator1"), optimizeFeatureMethods.getDateMinusDays(10));

		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterLastActivityWorkspaceData.get("columnName"),
				Integer.valueOf(filterLastActivityWorkspaceData.get("columnIndex")),
				filterLastActivityWorkspaceData.get("filterOperator2"), optimizeFeatureMethods.getDateMinusDays(10));

		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterLastActivityWorkspaceData.get("columnName"),
				Integer.valueOf(filterLastActivityWorkspaceData.get("columnIndex")),
				filterLastActivityWorkspaceData.get("filterOperator3"), null);

		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterLastActivityWorkspaceData.get("columnName"),
				Integer.valueOf(filterLastActivityWorkspaceData.get("columnIndex")),
				filterLastActivityWorkspaceData.get("filterOperator4"), null);

		// Steps 8: Sort the filtered list
		log.info("Steps 4-8: Verifying sorting functionality for 'last activity' column");
		optimizeFeatureMethods.verifySortingFunctionalityForLastActivity();

		log.info("========== Completed Optimize Workspace 'last activity' Filter Functionality Test ==========");
	}

	@Test(description = "Verify support of filtering 'Date of creation'  in Unassigned domain in Workspace", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-16547", Products = { Product.Helix_Optimize })
	public void verify_support_filtering_date_of_creation_in_unassigned_domain(ITestContext context) {
		// Set test context
		context.setAttribute("TestCaseName", "DRSQA-16547");

		// Load user data from JSON
		readJson = new ReadJson();
		filterCreationDateWorkspaceData = readJson.getInputValuesMapIncludingLabelField(
				"ITOM" + File.separator + SolQAConstants.HELIX_OPTIMIZE, "OptimizeTestData", "DRSQA_16547_Data");

		log.info("========== Starting Optimize Workspace 'Date of creation' Filter Functionality Test ==========");

		// Step 1: Login to Helix Portal and cross launch Optimize
		log.info("Step 1: Login to Helix Portal and cross launch Optimize");
		doPreconditions(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		IntelligentAutomationPage iaPage = new IntelligentAutomationPage();
		iaPage.navigateToOptimizePage();
		iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		// Step 2: Go to "Workspace", scroll down and click on "All systems and business drivers"
		log.info("Step 2: Navigate to Workspace -> All systems and business drivers");
		optimizeFeatureMethods.navigateToAllSystemsAndBusinessDrivers();

		// Step 3: Click on "Unassigned" domain
		log.info("Step 3: Click on 'Unassigned' domain");
		optimizeFeatureMethods.clickOnUnassignedDomain();

		// Step 4-7: Apply filter's for 'Date of creation' column
		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterCreationDateWorkspaceData.get("columnName"),
				Integer.valueOf(filterCreationDateWorkspaceData.get("columnIndex")),
				filterCreationDateWorkspaceData.get("filterOperator1"), optimizeFeatureMethods.getDateMinusDays(5));

		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(filterCreationDateWorkspaceData.get("columnName"),
				Integer.valueOf(filterCreationDateWorkspaceData.get("columnIndex")),
				filterCreationDateWorkspaceData.get("filterOperator2"), optimizeFeatureMethods.getDateMinusDays(20));

		// Steps 8: Sort the filtered list
		log.info("Steps 4-8: Verifying sorting functionality for 'last activity' column");
		optimizeFeatureMethods.verifySortingFunctionalityForDateOfCreation();

		log.info("========== Completed Optimize Workspace 'Date of creation' Filter Functionality Test ==========");
	}

	@Test(description = "Verify filter entities by both 'Date of creation' and 'Last Activity'  in Unassigned domain in Workspace", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-16594", Products = { Product.Helix_Optimize })
	public void verify_support_filtering_creation_date_and_last_activity_in_unassigned_domain(ITestContext context) {
		// Set test context
		context.setAttribute("TestCaseName", "DRSQA-16594");

		// Load user data from JSON
		readJson = new ReadJson();
		filterCreationDateAndLastActivityWorkspaceData = readJson.getInputValuesMapIncludingLabelField(
				"ITOM" + File.separator + SolQAConstants.HELIX_OPTIMIZE, "OptimizeTestData", "DRSQA_16594_Data");

		log.info(
				"========== Starting Optimize Workspace 'Date of creation' and 'Last Activity' Filter Functionality Test ==========");

		// Step 1: Login to Helix Portal and cross launch Optimize
		log.info("Step 1: Login to Helix Portal and cross launch Optimize");
		doPreconditions(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		IntelligentAutomationPage iaPage = new IntelligentAutomationPage();
		iaPage.navigateToOptimizePage();
		iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		// Step 2: Go to "Workspace", scroll down and click on "All systems and business drivers"
		log.info("Step 2: Navigate to Workspace -> All systems and business drivers");
		optimizeFeatureMethods.navigateToAllSystemsAndBusinessDrivers();

		// Step 3: Click on "Unassigned" domain
		log.info("Step 3: Click on 'Unassigned' domain");
		optimizeFeatureMethods.clickOnUnassignedDomain();

		// Get past date from current date in 'dd/mm/yyy' format
		past30DaysDate = optimizeFeatureMethods.getDateMinusDays(30);
		past20DaysDate = optimizeFeatureMethods.getDateMinusDays(20);
		past25DaysDate = optimizeFeatureMethods.getDateMinusDays(25);

		// Step 5-6: Apply filter's for 'Date of creation' and choose "<" then insert a
		// date as 30 days previous to current date
		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(
				filterCreationDateAndLastActivityWorkspaceData.get("columnName1"),
				Integer.valueOf(filterCreationDateAndLastActivityWorkspaceData.get("columnIndex1")),
				filterCreationDateAndLastActivityWorkspaceData.get("filterOperator1"), past30DaysDate);

		// Step 7: Apply filter's for 'Last Activity' column and choose "<" then insert
		// a date as 20 days previous to current date
		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(
				filterCreationDateAndLastActivityWorkspaceData.get("columnName2"),
				Integer.valueOf(filterCreationDateAndLastActivityWorkspaceData.get("columnIndex2")),
				filterCreationDateAndLastActivityWorkspaceData.get("filterOperator2"), past20DaysDate);
		// Validate that filtered result 'Date of creation' < than date as 30 days
		// previous to current date
		optimizeFeatureMethods.validateFilterResultForColumn(
				filterCreationDateAndLastActivityWorkspaceData.get("columnName1"),
				Integer.valueOf(filterCreationDateAndLastActivityWorkspaceData.get("columnIndex1")),
				filterCreationDateAndLastActivityWorkspaceData.get("filterOperator1"), past30DaysDate);

		// Step 8: Apply filter's for 'Last Activity' column and choose ">" then insert
		// a date as 25 days previous to current date
		optimizeFeatureMethods.verifyFilterFunctionalityForColumn(
				filterCreationDateAndLastActivityWorkspaceData.get("columnName2"),
				Integer.valueOf(filterCreationDateAndLastActivityWorkspaceData.get("columnIndex2")),
				filterCreationDateAndLastActivityWorkspaceData.get("filterOperator3"), past25DaysDate);
		// Validate that filtered result 'Date of creation' < than date as 30 days
		// previous to current date
		optimizeFeatureMethods.validateFilterResultForColumn(
				filterCreationDateAndLastActivityWorkspaceData.get("columnName1"),
				Integer.valueOf(filterCreationDateAndLastActivityWorkspaceData.get("columnIndex1")),
				filterCreationDateAndLastActivityWorkspaceData.get("filterOperator1"), past30DaysDate);

		log.info(
				"========== Completed Optimize Workspace 'Date of creation' and 'Last Activity' Filter Functionality Test ==========");
	}
	
	@Test(description = "Optimize-AIOps:Show capacity-related service risk predictions in Service Monitoring UI (AIOps UI)", groups = {
	"Regression" })
	@SolqaDetails(authorName = "dpatare", JiraID = "DRSQA-14912", Products = { Product.Helix_Optimize })
	public void OptimizeAIOps_Show_capacity_related_service_risk_predictions_in_Service_Monitoring_UI(
			ITestContext context) {
		// Set test context
		context.setAttribute("TestCaseName", "DRSQA-14912");

		// Load user data from JSON
		readJson = new ReadJson();
		String username = configProp.get("ADE_USERNAME");
		String password = configProp.get("ADE_PASSWORD");

		log.info(
				"========== Starting Show capacity-related service risk predictions in Service Monitoring UI ==========");

		// Step 1: Login to Helix Portal and cross launch Optimize

		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-14912");
		aiopsFeatureMethods = new AIOpsFeatureMethods();
		log.info("Step 1: Get Test Data");
		readJson = new ReadJson();
		optimizeServiceNameData = readJson.getInputValuesMapIncludingLabelField(
				SolQAConstants.appITOM + File.separator + SolQAConstants.HELIX_OPTIMIZE, "OptimizeTestData",
				"DRSQA_14912_testData");

		log.info("Step 2: Login to Helix Portal and cross launch Optimize");
		portalFeatureMethods.loginToADEPlatform(username, password);

		log.info("Step 3: Cross Launch HELIX OPTIMIZE");

		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		log.info("Step 4: Get Risk Count");
		String serviceRisk = optimizeFeatureMethods.getServiceRiskByName(optimizeServiceNameData.get("serviceName"));

		optimizeFeatureMethods.closeWindow(SolQAConstants.HELIX_OPTIMIZE);
		log.info("Step 5: Cross Lauch AIOPS");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.AIOPS);
		aiopsFeatureMethods.navigateToServicesTab();

		log.info("Step 6: Create Service if not Exist");
		aiopsFeatureMethods.createDuplicateService(optimizeServiceNameData.get("serviceName"),
				optimizeServiceNameData.get("CIname"));
		log.info("Step 7: Verify Risk count");
		aiopsFeatureMethods.verifyRiskCountOfService(optimizeServiceNameData.get("serviceName"), serviceRisk);
		log.info("Step 8: Delete Created Service");
		aiopsFeatureMethods.deleteServiceUsingUI(optimizeServiceNameData.get("serviceName"));

	}

	@Test(description = "Optimize : Verify file system details columns added on Virtual Machines All VMs page on vSphere view", groups = {
			"Regression_P3" })
	@SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17387", Products = { Product.Helix_Optimize })
	public void DRSQA_17387_verify_file_system_columns_on_vsphere_vms_page(ITestContext context) {
		context.setAttribute("DRSQA_17387_Driver", getDriver());
		context.setAttribute("DRSQA_17387_Product", Product.Helix_Optimize);

		readJson = new ReadJson();
		Map<Object, String> vSphereVMColumnsData = readJson.getInputValuesMapIncludingLabelField(
				"ITOM" + File.separator + SolQAConstants.HELIX_OPTIMIZE, "OptimizeTestData", "DRSQA_17387_Data");

		log.info("========== Starting DRSQA-17387: Verify file system details columns on vSphere VMs page ==========");

		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17387");
		//Login to ADE Platform
		log.info("Step 1: Login to Helix Dashboard using admin user");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("Step 2: Cross Launch HELIX OPTIMIZE");

		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		log.info("Step 3: Navigate to Views -> Virtualization -> vSphere -> Virtual Machines");
		optimizeFeatureMethods.navigateToVSphereVirtualMachines();

		log.info("Step 4: Verify file system columns are present in the VM table");
		List<String> columnNames = Arrays.asList(vSphereVMColumnsData.get("column1"),
				vSphereVMColumnsData.get("column2"), vSphereVMColumnsData.get("column3"));
		optimizeFeatureMethods.verifyFileSystemColumnsOnVMPage(columnNames);

		log.info("Step 5: Validate that All VMs table has rows more than 0");
		optimizeFeatureMethods.verifyVMTableHasRows();

		log.info("Step 6: Click on any VM to open VM details");
		optimizeFeatureMethods.clickOnFirstVMInTable();

		log.info("Step 7: Verify graphs are displayed on VM details page");
		List<String> graphTitles = Arrays.asList(vSphereVMColumnsData.get("graph1"), vSphereVMColumnsData.get("graph2"),
				vSphereVMColumnsData.get("graph3"), vSphereVMColumnsData.get("graph4"));
		optimizeFeatureMethods.verifyVMDetailsGraphsPresent(graphTitles);

		log.info(
				"Step 8: Click Related Information link 'File System Profile' under File System/Datastore Utilization graph");
		optimizeFeatureMethods.clickChartRelatedInfoLink(vSphereVMColumnsData.get("relatedInfoLink"));

		log.info("Step 9: Verify File System Profile page launched and graphs are displayed with data");
		optimizeFeatureMethods.verifyProfileTabSelected(vSphereVMColumnsData.get("relatedInfoLink"));
		optimizeFeatureMethods.verifySidebarItemActive(vSphereVMColumnsData.get("dataExplorerSidebarItem"));
		List<String> fsProfileGraphTitles = Arrays.asList(vSphereVMColumnsData.get("fsProfileGraph1"),
				vSphereVMColumnsData.get("fsProfileGraph2"), vSphereVMColumnsData.get("fsProfileGraph3"),
				vSphereVMColumnsData.get("fsProfileGraph4"));
		optimizeFeatureMethods.verifyGraphsPresentWithData(fsProfileGraphTitles);

		log.info("========== Completed DRSQA-17387: Verify file system details columns on vSphere VMs page ==========");
	}

	@Test(description = "[ADE Optimize] Verify Re-install packages in optimize", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17181", Products = { Product.Helix_Optimize })
	public void DRSQA17181_Verify_Re_install_packages_in_optimize(ITestContext context) {
		String packageName = SolQAConstants.AWS_Package;
		log.info("[ADE Optimize] Verify Re-install packages in optimize");

		// Step 1: Login to Helix Portal and cross launch Optimize

		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17181");
		//Login to ADE Platform
		log.info("Step 1: Login to Helix Dashboard using admin user");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("Step 2: Cross Launch HELIX OPTIMIZE");

		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		log.info("Step 3: Navigate to Administration tab");
		optimizeFeatureMethods.clickOnAdministrationTab();

		log.info("Step 4: Navigate to Maintenance tab");
		optimizeFeatureMethods.clickOnMaintenanceTab();

		log.info("Step 5: Navigate to AdditionalPackageTab tab");
		//optimizeFeatureMethods.clickOnAdditionalPackageTab();

		log.info("Step 6: Re-install existing package");
		optimizeFeatureMethods.reInstallExistingPackage(packageName);

		log.info("Step 7: check package is re-installed for selected view");
		optimizeFeatureMethods.checkPackageReinstalled();

		log.info("[ADE Optimize] Verify Re-install packages in optimize");


	}


	@Test(description = "[ADE Optimize] Verify Install various packages in optimize", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-17180", Products = { Product.Helix_Optimize })
	public void DRSQA17180_Verify_install_packages_in_optimize(ITestContext context) {
		log.info("[ADE Optimize] Verify install packages in optimize");

		// Step 1: Login to Helix Portal and cross launch Optimize

		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17180");
		//Login to ADE Platform
		log.info("Step 1: Login to Helix Dashboard using admin user");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("Step 2: Cross Launch HELIX OPTIMIZE");

		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		log.info("Step 3: Navigate to Administration tab");
		optimizeFeatureMethods.clickOnAdministrationTab();

		log.info("Step 4: Navigate to Maintenance tab");
		optimizeFeatureMethods.clickOnMaintenanceTab();

		log.info("Step 5: Navigate to AdditionalPackageTab tab");
		//optimizeFeatureMethods.clickOnAdditionalPackageTab();

		log.info("Step 6: install package");
		optimizeFeatureMethods.installPackage();

		log.info("Step 7: check package is installed for selected view");
		optimizeFeatureMethods.checkPackageinstalled();

		log.info("[ADE Optimize] Verify install package in optimize");



	}


	@Test(description = "Verify the K8S Table data is available", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12359", Products = { Product.Helix_Optimize })
	public void DRSQA12359_Verify_K8S_Table_data_is_available(ITestContext context) {
		log.info("Verify the K8S Table data is available");

		// Step 1: Login to Helix Portal and cross launch Optimize

		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12359");
		//Login to ADE Platform
		log.info("Step 1: Login to Helix Dashboard using admin user");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("Step 2: Cross Launch HELIX OPTIMIZE");

		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		log.info("Step 3: Verify Optimize page launched successfully");
		optimizeFeatureMethods.verifyBHCOPageLaunchedSuccessfully();

		log.info("Step 4: Navigate to kubernetesView tab");
		optimizeFeatureMethods.navigateTokubernetes();

		log.info("Step 5: Navigate to ClustersView tab");
		optimizeFeatureMethods.clickOnClustersView();
		log.info("Step 6: Validate that All clusters table has rows more than 0");
		optimizeFeatureMethods.verifyVMTableHasRows();

		log.info("Step 7: Navigate to NodesView tab");
		optimizeFeatureMethods.clickOnNodesView();

		log.info("Step 8: Navigate to kubernetesVie tab");
		optimizeFeatureMethods.clickNamespacesView();

		log.info("Step 9: Navigate to ControllersView tab");
		optimizeFeatureMethods.clickOnControllersView();

		log.info("Step 10: Navigate to PodsView tab");
		optimizeFeatureMethods.clickOnPodsView();

		log.info("Step 11: Navigate to capacityView tab");
		optimizeFeatureMethods.clickOncapacityView();

		log.info("Step 12: Navigate to CPUView tab");
		optimizeFeatureMethods.clickOnCPUView();

		log.info("Step 13: Navigate to memoryView tab");
		optimizeFeatureMethods.clickOnmemoryView();

		log.info("Step 14: Navigate to RecommendationsView tab");
		optimizeFeatureMethods.clickOnRecommendationsView();

		log.info("========== completed Verification of the K8S Table data is available ==========");

	}

	@Test(description = "Cross launch to BMC Helix Operation Management and BMC Helix Dashboard - OOTB view", groups = {
			"Regression" })
	@SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12428", Products = { Product.Helix_Optimize })
	public void DRSQA12428_Cross_launch_to_BMC_Helix_Operation_Management_and_BMC_Helix_Dashboard(ITestContext context) {
		log.info("Cross launch to BMC Helix Operation Management and BMC Helix Dashboard - OOTB view");

		// Step 1: Login to Helix Portal and cross launch Optimize
		String serverName = SolQAConstants.PA_AGENT;
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12428");
		//Login to ADE Platform
		log.info("Step 1: Login to Helix Dashboard using admin user");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("Step 2: Cross Launch HELIX OPTIMIZE");

		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		log.info("Step 3: Verify Optimize page launched successfully");
		optimizeFeatureMethods.verifyBHCOPageLaunchedSuccessfully();

		log.info("Step 6: Navigate to serveroverView tab");
		optimizeFeatureMethods.navigateToServersOverview();

		log.info("Step 7: Open server");
		optimizeFeatureMethods.clickOnserverName(serverName);

		log.info("Step 8: Cross launch dashboard");
		optimizeFeatureMethods.clickOnopenDashboard();

		log.info("Step 9: cross launch operationmanagement");
		optimizeFeatureMethods.clickOnshowinOperationsManagement();

		log.info("========== completed Cross launch to BMC Helix Operation Management and BMC Helix Dashboard - OOTB view ==========");

	}

	@Test(description = "Verify vSphere view for Infrastructure, Virtual machines and Data Explorer", groups = {
			"Regression_P3" })
	@SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-12397", Products = { Product.Helix_Optimize })
	public void DRSQA_12397_verify_vSphere_view_for_Infrastructure_Virtualmachines_and_Data_Explorer(ITestContext context) {
		context.setAttribute("DRSQA_12397_Driver", getDriver());
		context.setAttribute("DRSQA_12397_Product", Product.Helix_Optimize);

		readJson = new ReadJson();
		Map<Object, String> vSphereVMColumnsData = readJson.getInputValuesMapIncludingLabelField(
				"ITOM" + File.separator + SolQAConstants.HELIX_OPTIMIZE, "OptimizeTestData", "DRSQA_12397_Data");

		log.info("========== Starting DRSQA-12397: Verify vSphere view for Infrastructure, Virtual machines and Data Explorer ==========");

		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12397");
		//Login to ADE Platform
		log.info("Step 1: Login to Helix Dashboard using admin user");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("Step 2: Cross Launch HELIX OPTIMIZE");

		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		log.info("Step 3: Navigate to Views -> Virtualization -> vSphere -> Virtual Machines");
		optimizeFeatureMethods.navigateToVSphereVirtualMachines();

		log.info("Step 4: Validate that All VMs table has rows more than 0");
		optimizeFeatureMethods.verifyVMTableHasRows();

		log.info("Step 5: Click on any VM to open VM details");
		optimizeFeatureMethods.clickOnFirstVMInTable();

		log.info("Step 6: Verify graphs are displayed on VM details page");
		List<String> graphTitles = Arrays.asList(vSphereVMColumnsData.get("graph1"), vSphereVMColumnsData.get("graph2"),
				vSphereVMColumnsData.get("graph3"), vSphereVMColumnsData.get("graph4"));
		optimizeFeatureMethods.verifyVMDetailsGraphsPresent(graphTitles);

		log.info("Step 7: Navigate to Views -> Virtualization -> vSphere -> Infrasture");
		optimizeFeatureMethods.navigateToVSphereInfrastructure();

		log.info("Step 8: Validate that All Infrasture table has rows more than 0");
		optimizeFeatureMethods.verifyVMTableHasRows();

		log.info("Step 9: Click on any Infrasture to open Infrasture details");
		optimizeFeatureMethods.clickOnFirstVMInTable();

		log.info("Step 10: Verify graphs are displayed on VM details page");
		List<String> graphTitles1 = Arrays.asList(vSphereVMColumnsData.get("graph1"), vSphereVMColumnsData.get("graph2"));
		optimizeFeatureMethods.verifyVMDetailsGraphsPresent(graphTitles1);

		log.info("Step 11: Navigate to Views -> Virtualization -> vSphere -> Data explorer");
		optimizeFeatureMethods.navigateToVSphereDataexplorer();

		log.info("========== Completed DRSQA-12397: Verify vSphere view for Infrastructure, Virtual machines and Data Explorer ==========");
	}

    @Test(description = "AWS ETL: Create and execute AWS ETL", groups = {"Regression" })
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-14121", Products = { Product.Helix_Optimize })
    public void DRSQA_14121_Create_and_execute_AWS_ETL(ITestContext context) {
        context.setAttribute("DRSQA_14121_Driver", getDriver());
        context.setAttribute("DRSQA_14121_Product", Product.Helix_Optimize);


        log.info("========== Starting DRSQA-14121: Create and execute AWS ETL ==========");

        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-14121");
        //Login to ADE Platform
        log.info("Step 1: Login to Helix Dashboard using admin user");
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("Step 2: Cross Launch HELIX OPTIMIZE");

        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
        optimizeFeatureMethods = new OptimizeFeatureMethods();

        log.info("Step 3: Navigate to Views -> Cloud -> AWS -> EC2 Instances");
        optimizeFeatureMethods.navigateToAWS();

        log.info("Step 4: click on EC2 Instances");
        optimizeFeatureMethods.viewEc2Instances();

        log.info("Step 5: Validate that All VMs table has rows more than 0");
        optimizeFeatureMethods.verifyVMTableHasRows();

        log.info("Step 6: Click on any ec2instance to open ec2instance details");
        optimizeFeatureMethods.viewEc2Instance();

        optimizeFeatureMethods.navigateToAWS();

        log.info("Step 7: click on EC2 Idle Instances");
        optimizeFeatureMethods.viewEC2IdleInstances();

        log.info("Step 8: Validate that All VMs table has rows more than 0");
        optimizeFeatureMethods.verifyVMTableHasRows();

        log.info("Step 9: Click on any idle ec2instance to open ec2instance details");
        optimizeFeatureMethods.viewEC2IdleInstance();

        optimizeFeatureMethods.navigateToAWS();

        log.info("Step 10: click on EBSVolumes");
        optimizeFeatureMethods.viewEBSVolumes();

        log.info("Step 11: Validate that All VMs table has rows more than 0");
        optimizeFeatureMethods.verifyVMTableHasRows();

        log.info("Step 12: click on Recommendations");
        optimizeFeatureMethods.viewRecommendations();

        log.info("Step 13: Validate that All VMs table has rows more than 0");
        optimizeFeatureMethods.verifyVMTableHasRows();


        log.info("========== Completed DRSQA-14121: Create and execute AWS ETL ==========");
    }



    @Test(description = "Azure ETL: Create and execute Azure ETL with filtering of Virtual Desktop Infrastructure.", groups = { "Regression" })
    @SolqaDetails(authorName = "mmadanka", JiraID = "DRSQA-13610", Products = { Product.Helix_Optimize })
    public void DRSQA13610_Create_and_execute_Azure_ETL_with_filtering(ITestContext context) {
        log.info("Azure ETL: Create and execute Azure ETL with filtering of Virtual Desktop Infrastructure");

        // Step 1: Login to Helix Portal and cross launch Optimize

        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-13610");
        //Login to ADE Platform
        log.info("Step 1: Login to Helix Dashboard using admin user");
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        log.info("Step 2: Cross Launch HELIX OPTIMIZE");

        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
        optimizeFeatureMethods = new OptimizeFeatureMethods();

        log.info("Step 3: Verify Optimize page launched successfully");
        optimizeFeatureMethods.verifyBHCOPageLaunchedSuccessfully();

        log.info("Step 4: Navigate to Azure view tab");
        optimizeFeatureMethods.navigateToAzure();

        log.info("Step 5: click virtual machines");
        optimizeFeatureMethods.viewazuretabVirtualMachines();

        log.info("Step 6: Validate that All VMs table has rows more than 0");
        optimizeFeatureMethods.verifyVMTableHasRows();

        log.info("Step 7: click on virtual machine");
        optimizeFeatureMethods.viewazureVirtualmachine();

        optimizeFeatureMethods.navigateToAzure();

        log.info("Step 8: click idle virtual machines");
        optimizeFeatureMethods.viewazuretabIdleVirtualMachines();

        log.info("Step 9: click on recommendations");
        optimizeFeatureMethods.viewAzureRecommendations();

        log.info("========== Completed: Create and execute Azure ETL with filtering of Virtual Desktop Infrastructure ==========");

    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        // Use ThreadLocal cleanup to prevent memory leaks
        try {
            if (optimizeFeatureMethods != null) {
                optimizeFeatureMethods.closeBrowser();
            }
            if (aiopsFeatureMethods != null) {
                aiopsFeatureMethods.closeBrowser();
            }
            if (portalFeatureMethods != null) {
                portalFeatureMethods.closeBrowser();
            }
        } catch (Exception e) {
            log.error("Error during cleanup: " + e.getMessage());
        }
    }
}
