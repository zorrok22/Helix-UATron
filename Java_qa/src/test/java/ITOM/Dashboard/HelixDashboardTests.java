package ITOM.Dashboard;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import framework.Launcher;
import pages.ITOM.Dashboard.DashboadFeatureMethods;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import util.Product;
import util.ReadJson;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;
import util.CommonMethods;

public class HelixDashboardTests extends Launcher {

	static SolqaLogger log = new SolqaLogger(HelixDashboardTests.class.getName());
	Portal_FeatureMethods portalFeatureMethods;
	Map<String, String> configProp = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
	DashboadFeatureMethods helixDashboardFeatureMethods;

	ReadJson readJson = new ReadJson();
	Map<Object, String> drsqa_12540_data, drsqa_12539_data, drsqa_12431_data, drsqa_17341_data;
	List<String> folderNames;
	String schedulerName;

    //previous owner: shudeshpa
	@Test(description = "Verify Dashboards audit trail for dashboard, schedule and folder",groups={"Regression", "HelixDashboard"})
	@SolqaDetails(authorName = "gsutar", JiraID = "DRSQA-12540",Products = {Product.Helix_DWP})
	public void DRSQA_12540_Verify_Dashboards_audit_trail_for_dashboard_schedule_and_folder(ITestContext context) throws IOException{

		log.info("------------Verify Dashboards audit trail for dashboard, schedule and folder-------------");

		log.info("---- Step1: Login to Helix Portal with User (admin role)and cross launch Helix Dashboard. ----------");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12540");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
		drsqa_12540_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12540_Data");
		folderNames = Arrays.asList(drsqa_12540_data.get("folderNames").split(","));

		log.info("---- Step2: Open Dashboards from Menu ----------");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

		log.info("---- Step3 and 4: Open BMC Helix Audit Dashboard dashboard ----");
		helixDashboardFeatureMethods.openMenu(drsqa_12540_data.get("menuItem"));
		helixDashboardFeatureMethods.openFoldersFromMenu(folderNames);

		for(int i=1;i<=9;i++) {
			log.info("Clearing updated filters for case "+i+"");
			helixDashboardFeatureMethods.clearAllFilters();

			log.info("Selecting required values for the filters for case "+i+"");
			helixDashboardFeatureMethods.selectValues(drsqa_12540_data.get("operation"), drsqa_12540_data.get("operation_case"+i+""));
			helixDashboardFeatureMethods.selectValues(drsqa_12540_data.get("resourceType"), drsqa_12540_data.get("resourceType_case"+i+""));
			helixDashboardFeatureMethods.selectValues(drsqa_12540_data.get("resourceName"), drsqa_12540_data.get("resourceName_case"+i+""));
			helixDashboardFeatureMethods.selectValues(drsqa_12540_data.get("application"), drsqa_12540_data.get("application_case"+i+""));
			helixDashboardFeatureMethods.selectValues(drsqa_12540_data.get("actor"), drsqa_12540_data.get("actor_case"+i+""));

			log.info("Verifying the selected values in the filters for case "+i+"");
			helixDashboardFeatureMethods.verifyVariableValue(drsqa_12540_data.get("operation"), drsqa_12540_data.get("operation_case"+i+""));
			helixDashboardFeatureMethods.verifyVariableValue(drsqa_12540_data.get("resourceType"), drsqa_12540_data.get("resourceType_case"+i+""));
			helixDashboardFeatureMethods.verifyVariableValue(drsqa_12540_data.get("resourceName"), drsqa_12540_data.get("resourceName_case"+i+""));
			helixDashboardFeatureMethods.verifyVariableValue(drsqa_12540_data.get("application"), drsqa_12540_data.get("application_case"+i+""));
			helixDashboardFeatureMethods.verifyVariableValue(drsqa_12540_data.get("actor"), drsqa_12540_data.get("actor_case"+i+""));
		}

	}

	@Test(description = "Verify Dashboards audit trail for dashboard, schedule and folder",groups={"Regression", "HelixDashboard"})
	@SolqaDetails(authorName = "shudeshpa", JiraID = "DRSQA-12539",Products = {Product.Helix_DWP})
	public void DRSQA_12539_Verify_Dashboards_audit_trail_for_dashboard_schedule_and_folder(ITestContext context) throws IOException{

		log.info("------------Verify Dashboards audit trail for dashboard, schedule and folder-------------");

		log.info("---- Login to Portal with correct username and password ----------");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12539");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
		drsqa_12539_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12539_Data");
		schedulerName = uniquify(drsqa_12539_data.get("schedulerName"));

		log.info("---- Navigate to BMC Helix Dashboards ----------");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

		log.info("---- Open Folder from Dashboard ----");
		helixDashboardFeatureMethods.openMenu(drsqa_12539_data.get("menuItem"));

		helixDashboardFeatureMethods.clickOnNewScheduledReport();
		helixDashboardFeatureMethods.addScheduledReportDetails(schedulerName, drsqa_12539_data.get("schedulerDescription"), drsqa_12539_data.get("dashboardTitle"), drsqa_12539_data.get("recepientsIds"), drsqa_12539_data.get("recepientsBCCIDs"));

		helixDashboardFeatureMethods.openSchedulerReport(schedulerName);
		helixDashboardFeatureMethods.verifyBCCRecipient(drsqa_12539_data.get("recepientsBCCIDs"));

	}
	
	@Test(description = "Verify On-demand forecasting of any time series data in Helix Dashboard", groups={"Regression", "HelixDashboard"})
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-12431", Products = {Product.Dashboards})
	public void DRSQA_12431_Verify_OnDemand_Forecasting_TimeSeries_In_Helix_Dashboard(ITestContext context) throws IOException {

		log.info("------------Verify On-demand forecasting of any time series data in Helix Dashboard-------------");

		log.info("---- Step 1: Login to Portal with correct username and password ----");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12431");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
		drsqa_12431_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_12431_Data");

		log.info("---- Step 2: Navigate to BMC Helix Dashboards ----");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
		helixDashboardFeatureMethods.verifyDashboardPageLaunchedSuccessfully();

		log.info("---- Step 3: Open Dashboards menu ----");
		helixDashboardFeatureMethods.openMenu(drsqa_12431_data.get("menuItem"));

		log.info("---- Step 4: Creating a new Dashboard ----");
		helixDashboardFeatureMethods.createNewVisualizationDashboard(drsqa_12431_data.get("helixDashboard"));
		helixDashboardFeatureMethods.verifyEditPanelPageOpened();

		log.info("---- Step 5: Change visualization to Helix Time Series Forecast ----");
		helixDashboardFeatureMethods.selectVisualizationPlugin(drsqa_12431_data.get("visualizationPlugin"));

		log.info("---- Step 6: Verify forecast alert message ----");
		helixDashboardFeatureMethods.verifyForecastAlertMessage(drsqa_12431_data.get("forecastAlertText"));

		log.info("---- Step 7: Change time range to Last 7 days ----");
		helixDashboardFeatureMethods.selectTimeRange(drsqa_12431_data.get("timeRange1"));

		log.info("---- Step 8: Verify forecast graph is displayed ----");
		helixDashboardFeatureMethods.verifyForecastGraphAndPredictionDisplayed();

		log.info("---- Step 9: Configure forecast query via Metrics browser ----");
		helixDashboardFeatureMethods.configureForecastQuery(
				drsqa_12431_data.get("queryType"),
				drsqa_12431_data.get("source"),
				drsqa_12431_data.get("metric"),
				drsqa_12431_data.get("labelName"),
				drsqa_12431_data.get("labelValueFilter"));

		log.info("---- Step 10: Verify forecast graph is displayed ----");
		helixDashboardFeatureMethods.verifyForecastGraphAndPredictionDisplayed();

		log.info("---- Step 11: Change time range to Last 30 days ----");
		helixDashboardFeatureMethods.selectTimeRange(drsqa_12431_data.get("timeRange2"));

		log.info("---- Step 12: Verify forecast graph is displayed ----");
		helixDashboardFeatureMethods.verifyForecastGraphAndPredictionDisplayed();

		log.info("---- Step 13: Enter panel title and description ----");
		CommonMethods commonMethods = new CommonMethods();
		String panelTitleText = drsqa_12431_data.get("panelTitlePrefix") + commonMethods.randomString(5);
		log.info("Generated panel title: " + panelTitleText);
		helixDashboardFeatureMethods.enterPanelDetails(panelTitleText, panelTitleText);

		log.info("---- Step 14: Verify panel title changed on the chart ----");
		helixDashboardFeatureMethods.verifyPanelTitleOnChart(panelTitleText);

		log.info("---- Step 15: Hover on panel chart and verify tooltip is displayed (Single mode) ----");
		helixDashboardFeatureMethods.hoverOnPanelChart();
		helixDashboardFeatureMethods.verifyTooltipDisplayed();

		log.info("---- Step 16: Click away from chart to reset tooltip state ----");
		helixDashboardFeatureMethods.clickAwayFromChart();

		log.info("---- Step 17: Change tooltip mode to Hidden ----");
		helixDashboardFeatureMethods.selectTooltipMode(drsqa_12431_data.get("tooltipMode"));

		log.info("---- Step 18: Hover on panel chart and verify tooltip is NOT displayed (Hidden mode) ----");
		helixDashboardFeatureMethods.hoverOnPanelChart();
		helixDashboardFeatureMethods.verifyTooltipNotDisplayed();

		log.info("---- Step 19: Toggle Legend visibility from true to false ----");
		helixDashboardFeatureMethods.toggleLegendVisibility();

		log.info("---- Step 20: Verify Legend label is not visible in the chart panel ----");
		helixDashboardFeatureMethods.verifyLegendNotVisible();

		log.info("---- Step 21: Toggle Legend visibility from false to true ----");
		helixDashboardFeatureMethods.toggleLegendVisibility();

		log.info("---- Step 22: Change Future Period from 1 week to 2 weeks ----");
		helixDashboardFeatureMethods.selectFuturePeriod(drsqa_12431_data.get("futurePeriod"));

		log.info("---- Step 23: Verify Future Period selected value is 2 weeks ----");
		helixDashboardFeatureMethods.verifyFuturePeriodSelected(drsqa_12431_data.get("futurePeriod"));

		log.info("---- Step 24: Verify forecast graph and prediction displayed after future period change ----");
		helixDashboardFeatureMethods.verifyForecastGraphAndPredictionDisplayed();

		log.info("---- Step 25: Change Algorithm Type from AUTOMATIC to CUBIC ----");
		helixDashboardFeatureMethods.selectAlgorithmType(drsqa_12431_data.get("algorithmType"));

		log.info("---- Step 26: Verify forecast graph and prediction displayed after algorithm change ----");
		helixDashboardFeatureMethods.verifyForecastGraphAndPredictionDisplayed();

	}

	@Test(description = "Verify Axis and Thresholds appear in panel option and chart", groups={"Regression", "HelixDashboard"})
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-17341", Products = {Product.Dashboards})
	public void DRSQA_17341_Verify_Axis_And_Thresholds_Appear_In_Panel_Option_And_Chart(ITestContext context) throws IOException {

		log.info("------------Verify Axis and Thresholds appear in panel option and chart-------------");

		log.info("---- Step 1: Login to Portal with correct username and password ----");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-17341");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
		helixDashboardFeatureMethods = new DashboadFeatureMethods(getDriver());
		drsqa_17341_data = readJson.getInputValuesMapIncludingLabelField("ITOM"+ File.separator +SolQAConstants.HELIX_DASHBOARD, "HelixDashboardTestData", "DRSQA_17341_Data");

		log.info("---- Step 2: Navigate to BMC Helix Dashboards ----");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
		helixDashboardFeatureMethods.verifyDashboardPageLaunchedSuccessfully();

		log.info("---- Step 3: Open Dashboards menu ----");
		helixDashboardFeatureMethods.openMenu(drsqa_17341_data.get("menuItem"));

		log.info("---- Step 4: Navigate to New Dashboard and Add visualization ----");
		helixDashboardFeatureMethods.createNewVisualizationDashboard(drsqa_17341_data.get("helixDashboard"));
		helixDashboardFeatureMethods.verifyEditPanelPageOpened();

		log.info("---- Step 5: Select Data source as Grafana ----");
		helixDashboardFeatureMethods.selectDataSource(drsqa_17341_data.get("dataSource"));

		log.info("---- Step 6: Select Query type as Random Walk ----");
		helixDashboardFeatureMethods.selectQueryType(drsqa_17341_data.get("queryType"));

		log.info("---- Step 7: Verify Time Series chart is displayed ----");
		helixDashboardFeatureMethods.verifyTimeSeriesChartDisplayed();

		log.info("---- Step 8: Change visualization to BMC Time Series Forecast ----");
		helixDashboardFeatureMethods.selectVisualizationPlugin(drsqa_17341_data.get("visualizationPlugin"));

		log.info("---- Step 9: Verify forecast alert message ----");
		helixDashboardFeatureMethods.verifyForecastAlertMessage(drsqa_17341_data.get("forecastAlertText"));

		log.info("---- Step 10: Verify Axis option appears in panel options ----");
		helixDashboardFeatureMethods.verifyAxisOptionVisible();

		log.info("---- Step 11: Verify Thresholds option appears in panel options ----");
		helixDashboardFeatureMethods.verifyThresholdsOptionVisible();

		log.info("---- Step 12: Change time range to Last 30 days ----");
		helixDashboardFeatureMethods.selectTimeRange(drsqa_17341_data.get("timeRange"));

		log.info("---- Step 13: Verify forecast graph is displayed ----");
		helixDashboardFeatureMethods.verifyForecastGraphAndPredictionDisplayed();

		log.info("---- Step 14: Change Axis Placement from Auto to Right ----");
		helixDashboardFeatureMethods.selectAxisPlacement(drsqa_17341_data.get("axisPlacement"));

		log.info("---- Step 15: Verify axis placement shifted to right in forecast graph ----");
		helixDashboardFeatureMethods.verifyAxisPlacementShiftedToRight();

		log.info("---- Step 16: Set Soft min to " + drsqa_17341_data.get("softMin") + " and Soft max to " + drsqa_17341_data.get("softMax") + " ----");
		helixDashboardFeatureMethods.setSoftMinMax(drsqa_17341_data.get("softMin"), drsqa_17341_data.get("softMax"));

		log.info("---- Step 17: Verify Soft min and Soft max values reflected in forecast graph ----");
		helixDashboardFeatureMethods.verifySoftMinMaxInForecastGraph(drsqa_17341_data.get("softMin"), drsqa_17341_data.get("softMax"));

		log.info("---- Step 18: Click on Add threshold button ----");
		helixDashboardFeatureMethods.clickAddThreshold();

		log.info("---- Step 19: Verify new threshold is added with delete option ----");
		helixDashboardFeatureMethods.verifyNewThresholdAddedWithDeleteOption();

		log.info("---- Step 20: Click Apply to go back to dashboard ----");
		helixDashboardFeatureMethods.clickApplyChanges();

		log.info("---- Step 21: Save dashboard with name ----");
		CommonMethods commonMethods = new CommonMethods();
		String dashboardName = drsqa_17341_data.get("dashboardNamePrefix") + commonMethods.randomString(5);
		log.info("Generated dashboard name: " + dashboardName);
		helixDashboardFeatureMethods.saveDashboardWithName(dashboardName);

		log.info("---- Step 22: Reopen the saved dashboard ----");
		helixDashboardFeatureMethods.reopenDashboard(drsqa_17341_data.get("menuItem"), dashboardName);

		log.info("---- Step 23: Delete the saved dashboard ----");
		helixDashboardFeatureMethods.openMenu(drsqa_17341_data.get("menuItem"));
		helixDashboardFeatureMethods.deleteDashboard(dashboardName);

	}
}