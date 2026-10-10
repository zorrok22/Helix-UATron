package ITOM.Dashboard;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Optimize.OptimizeFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class HelixDashboard_ContinuousOptimization extends Launcher {

	static SolqaLogger log = new SolqaLogger(HelixDashboard_ContinuousOptimization.class.getName());
	ReadJson readJson = new ReadJson();
	Map<Object, String> dashboard_drsqa_12507_data, bhco_drsqa_12507_data, drsqa_12447_data, drsqa_12432_data;
	List<String> reportFormats, etlServiceNames, etlFolderNames;

	Portal_FeatureMethods portalFeatureMethods;
	DashboadFeatureMethods helixDashboardFeatureMethods ;
	OptimizeFeatureMethods optimizeFeatureMethods;

	String resourceFilePath = System.getProperty(USERDIR) + File.separator + "resources" + File.separator +"ITOM" + File.separator;
	String downloadedFilePath = getDefaultDownloadPath() + File.separator;
	String reportName;

	//previous owner: shudeshpa
	@Test(description = "Verify Create reports using Helix Dashboard Definitions",groups={"Regression"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12507",Products = {Product.Dashboards})
	public void DRSQA_12507_Verify_Create_reports_using_Helix_Dashboard_Definitions(ITestContext context) throws IOException{

		log.info("------------Verify Create reports using Helix Dashboard Definitions-------------");

		log.info("---- Login to Portal with correct username and password ----------");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12507");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		dashboard_drsqa_12507_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12507_Data");

		log.info("---- Navigate to BMC Helix Dashboards ----------");
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

		log.info("---- Verify " + dashboard_drsqa_12507_data.get("dashboardTitle") + " dashboard is present or not. ----");
		helixDashboardFeatureMethods.openMenu(dashboard_drsqa_12507_data.get("menuItem"));
         String dashboardTitle = dashboard_drsqa_12507_data.get("dashboardTitle")+RandomStringUtils.randomAlphanumeric(5);
			log.info("---- Creating a new Dashboard ----");
			helixDashboardFeatureMethods.openMenu(dashboard_drsqa_12507_data.get("menuItem"));
			helixDashboardFeatureMethods.createNewVisualizationDashboard(dashboard_drsqa_12507_data.get("helixDashboard"));
			helixDashboardFeatureMethods.enterQueryDetails("Query type", dashboard_drsqa_12507_data.get("queryType"));
			helixDashboardFeatureMethods.enterQueryDetails("Service", dashboard_drsqa_12507_data.get("Service"));
			helixDashboardFeatureMethods.selectQueryDetails("Datamarts browser", dashboard_drsqa_12507_data.get("datamartsBrowser"));
			helixDashboardFeatureMethods.selectTableData();

			log.info("---- Entering Panel Details ----");
			helixDashboardFeatureMethods.enterPanelDetails(dashboard_drsqa_12507_data.get("panelTitle"), dashboard_drsqa_12507_data.get("panelDescription"));

			log.info("---- Saving the Dashboard Changes ----");
			helixDashboardFeatureMethods.saveChanges();

			log.info("---- Entering Panel Details ----");
			helixDashboardFeatureMethods.enterDashboardDetails(dashboardTitle, dashboard_drsqa_12507_data.get("dashboardDescription"));

			log.info("---- Saving the Dashboard ----");
			helixDashboardFeatureMethods.saveDashboard();

			log.info("---- Opening Settings and Adding Tags ----");
			helixDashboardFeatureMethods.backToDashboard();
			helixDashboardFeatureMethods.openDashboardSettings();
			helixDashboardFeatureMethods.addTags("BHCO");
			helixDashboardFeatureMethods.saveChanges();
			helixDashboardFeatureMethods.addChangeMessage("Tags added in Dashboard");
			helixDashboardFeatureMethods.saveDashboard();

			log.info("---- Verifying the Dashboard is created successfully ----");
		
		log.info("---- Login to Portal with correct username and password ----------");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12507");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		bhco_drsqa_12507_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_OPTIMIZE, "BHCOTestData", "DRSQA_12507_Data");
		reportFormats = Arrays.asList(bhco_drsqa_12507_data.get("reportFormats").split(","));
		reportName = uniquify(bhco_drsqa_12507_data.get("reportName"));

		log.info("---- Navigate to BMC Helix Continuous Optimization ----------");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_OPTIMIZE);
		optimizeFeatureMethods = new OptimizeFeatureMethods();

		//Open Workspace Tab and select Domains, Services & Applications and serviceName
		optimizeFeatureMethods.clickOnWorkspaceTab();
		optimizeFeatureMethods.selectNavigationItem(bhco_drsqa_12507_data.get("serviceName"));

		// Select work and add Helix Dashboard report from the dropdown
		optimizeFeatureMethods.selectWorkValue("Add works...");
		optimizeFeatureMethods.selectWorkValue(bhco_drsqa_12507_data.get("addDashboardReport"));

		// Add Helix Dashboard report details
		log.info("---- Selecting the Dashboard Report Value ----");
		optimizeFeatureMethods.addHelixDashboardReportDetails(reportName, bhco_drsqa_12507_data.get("reportDescription"), dashboardTitle, reportFormats, bhco_drsqa_12507_data.get("reportLevelValue"));

		// Verify the report is generated and download it
		optimizeFeatureMethods.selectNavigationItem("Works");
		optimizeFeatureMethods.selectNavigationItem(reportName);
		optimizeFeatureMethods.selectWorkValue("Run report now");
		optimizeFeatureMethods.verifyReportIsGenerated(reportName, reportFormats);
		optimizeFeatureMethods.downloadAndVerifyReport(reportName, reportFormats);

		for (String format : reportFormats) {
			//removed PDF comparison code due to date-time in header section causing binary comparison failure
			 if (format.equalsIgnoreCase("XLS")) {
				boolean excelComparisonResult = optimizeFeatureMethods.compareFilesBinary(
						downloadedFilePath + bhco_drsqa_12507_data.get("serviceName") + "_" + reportName + ".xlsx",
						resourceFilePath + "Amazon Web Services_Automation Test Optimization Report.xlsx");
				Assert.assertTrue(excelComparisonResult, "Excel binary comparison failed. Please check downloaded report.");
			}
		}
	}


	//previous owner: shudeshpa
	@Test(description = "Verify ability to see ETL status and last counter on helix dashboard",groups={"Regression"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12447",Products = {Product.Dashboards})
	public void DRSQA_12447_Verify_ability_to_see_ETL_status_and_last_counter_on_helix_dashboard(ITestContext context) throws IOException{

		log.info("------------ Verify ability to see ETL status and last counter on helix dashboard -------------");

		log.info("---- Login to Portal with correct username and password ----------");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12540");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		drsqa_12447_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12447_Data");
		etlFolderNames = Arrays.asList(drsqa_12447_data.get("etlFolderNames").split(","));
		etlServiceNames = Arrays.asList(drsqa_12447_data.get("etlServiceNames").split(","));
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());

		log.info("---- Navigate to BMC Helix Dashboards ----------");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

		log.info("---- Open Folder from Dashboard ----");
		helixDashboardFeatureMethods.openMenu(drsqa_12447_data.get("menuItem"));
		helixDashboardFeatureMethods.openFoldersFromMenu(etlFolderNames);
		log.info("---- Verify Folder is opened ----");

		helixDashboardFeatureMethods.clearETLFilters();
		for(String etlServiceName : etlServiceNames) {
			helixDashboardFeatureMethods.selectValues("ETL", etlServiceName);
		}
		helixDashboardFeatureMethods.refreshDashboard();

		log.info("Selected Panels are present in the Dashboard");
		for(String etlServiceName : etlServiceNames) {
			helixDashboardFeatureMethods.verifyDashboardTitle(etlServiceName);
		}

		log.info("---- Verify ETL Details, Loaded Samples and last counter panels on helix dashboard ----");
		for(String etlServiceName : etlServiceNames) {
			helixDashboardFeatureMethods.verifyPanels(etlServiceName);
		}

		log.info("---- Verify ETL List is opened in cross launch ----");
		helixDashboardFeatureMethods.openETLList();
		optimizeFeatureMethods = new OptimizeFeatureMethods();
		optimizeFeatureMethods.verifyBHCOPageLaunchedSuccessfully();

		log.info("---- Verify Tasks are present in ETL List ----");
		for(String etlServiceName : etlServiceNames) {
			optimizeFeatureMethods.verifyTasksPresentInETLList(etlServiceName);
		}
	}

	//previous owner: shudeshpa
	@Test(description = "Verify Helix Dashboard for HCO Datamarts using plugin",groups={"Regression"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12432",Products = {Product.Dashboards})
	public void DRSQA_12432_Verify_Helix_Dashboard_for_HCO_Datamarts_using_plugin(ITestContext context) throws IOException{

		log.info("------------ Verify Helix Dashboard for HCO Datamarts using plugin -------------");

		log.info("---- Login to Portal with correct username and password ----------");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12432");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		drsqa_12432_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12432_Data");

		log.info("---- Navigate to BMC Helix Dashboards ----------");
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

		log.info("---- Verify " + drsqa_12432_data.get("dashboardTitle") + " dashboard is present or not. ----");
		helixDashboardFeatureMethods.openMenu(drsqa_12432_data.get("menuItem"));
		String dashboardTitle = drsqa_12432_data.get("dashboardTitle")+RandomStringUtils.randomAlphanumeric(5);
			log.info("---- Creating a new Dashboard ----");
			helixDashboardFeatureMethods.openMenu(drsqa_12432_data.get("menuItem"));
			helixDashboardFeatureMethods.createNewVisualizationDashboard(drsqa_12432_data.get("helixDashboard"));
			helixDashboardFeatureMethods.enterQueryDetails("Query type", drsqa_12432_data.get("queryType"));
			helixDashboardFeatureMethods.enterQueryDetails("Service", drsqa_12432_data.get("Service"));
			helixDashboardFeatureMethods.selectQueryDetails("Datamarts browser", drsqa_12432_data.get("datamartsBrowser"));
			helixDashboardFeatureMethods.selectTableData();

			log.info("---- Entering Panel Details ----");
			helixDashboardFeatureMethods.enterPanelDetails(drsqa_12432_data.get("panelTitle"), drsqa_12432_data.get("panelDescription"));

			log.info("---- Saving the Dashboard Changes ----");
			helixDashboardFeatureMethods.saveChanges();

			log.info("---- Entering Panel Details ----");
			helixDashboardFeatureMethods.enterDashboardDetails(dashboardTitle, drsqa_12432_data.get("dashboardDescription"));

			log.info("---- Saving the Dashboard ----");
			helixDashboardFeatureMethods.saveDashboard();

			log.info("---- Opening Settings and Adding Tags ----");
			helixDashboardFeatureMethods.backToDashboard();
			helixDashboardFeatureMethods.openDashboardSettings();
			helixDashboardFeatureMethods.addTags("BHCO");
			helixDashboardFeatureMethods.saveChanges();
			helixDashboardFeatureMethods.addChangeMessage("Tags added in Dashboard");
			helixDashboardFeatureMethods.saveDashboard();

			log.info("---- Verifying the Dashboard is created successfully ----");
			helixDashboardFeatureMethods.clickOnDashboardsLink();
		helixDashboardFeatureMethods.openDashboard(dashboardTitle);

		log.info("---- Verify Panel content is present and No Data is not present on opened dashboard ----");
		helixDashboardFeatureMethods.verifyPanelContent();
	}

	@AfterMethod(alwaysRun = true)
	public void quitdriver(){
		if (portalFeatureMethods != null){
			portalFeatureMethods.closeBrowser();}
		if (helixDashboardFeatureMethods != null){
			helixDashboardFeatureMethods.closeBrowser();}
		if (optimizeFeatureMethods != null){
			optimizeFeatureMethods.closeBrowser();}
	}

}
