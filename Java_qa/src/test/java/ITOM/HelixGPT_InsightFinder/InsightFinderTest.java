package ITOM.HelixGPT_InsightFinder;

import framework.Launcher;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.HelixGPT_InsightFinder.InsightFinderFeatureMethods;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsightFinderTest extends Launcher {

	static SolqaLogger log = new SolqaLogger(InsightFinderTest.class.getName());

	ReadJson readJson = new ReadJson();
	Portal_FeatureMethods portalFeatureMethods;
	InsightFinderFeatureMethods insightFinderFeatureMethods;
	InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	Map<Object, String> drsqa_15290_data, drsqa_15291_data, drsqa_15303_data, drsqa_15293_data, drsqa_15302_data,
			drsqa_13613_data, drsqa_13614_data, drsqa_15292_data, drsqa_13612_data, drsqa_16623_data, drsqa_15368_data,
			drsqa_17108_data;
	WebDriver isDriver = null;
	Boolean isSuccess;

	/**
	 * Get value from test data with SERVICE_NAME placeholder replaced
	 * 
	 * @param labelKey The label key from JSON (e.g., "question1",
	 *                 "expectedHeader1")
	 * @return The value with %SERVICE_NAME% replaced with actual serviceName
	 */
	private String getWithServiceName(String labelKey, Map<Object, String> data) {
		String serviceName = data.get("serviceName");
		String value = data.get(labelKey);
		if (value != null && serviceName != null) {
			return value.replace("%SERVICE_NAME%", serviceName);
		}
		return value;
	}

	private String getWithDeviceName(String labelKey, Map<Object, String> data) {
		String deviceName = configProp.get("PA_AGENT");
		String value = data.get(labelKey);
		if (value != null && deviceName != null) {
			return value.replace("%DEVICE_NAME%", deviceName);
		}
		return value;
	}

	public void doPreconditions(ITestContext context, String jiraID) {
		log.info("==== PRECONDITIONS: Starting test setup ====");
		log.info("---- Login to Portal with correct username and password ----");
		portalFeatureMethods = new Portal_FeatureMethods(context, jiraID);
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
		insightFinderFeatureMethods = new InsightFinderFeatureMethods();
		log.info("==== PRECONDITIONS: Setup completed successfully ====");
	}

	@Test(description = "Support Insight Finder for BWF")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15368", Products = { Product.Dashboards })
	public void DRSQA_15368_SupportInsightFinderForBWF(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-15368 ====");
		log.info("Objective: Support Insight Finder for BWF - Copy Insight Finder Supervisor Skill ID");

		drsqa_15368_data = readJson.getInputValuesMapIncludingLabelField(
				"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
				"DRSQA_15368_Data");

		log.info("Logging in to Innovation Suite");
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
		login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
		log.info("Logged in user: " + configProp.get("adminUserName"));
		innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
		innovationSuiteFeatureMethodsPage.verifyInnovationSuitePageLaunchedSuccessfully();

		log.info("Step 2 and step3: Navigate to the HelixGPT Agent Studio Page");
		String skillID = innovationSuiteFeatureMethodsPage.copyInsightFinderSupervisorSkillId();
		log.info("Insight Finder Supervisor Skill ID:" + skillID);
		isDriver.quit();

		log.info("---- Login to Portal with correct username and password ----");
		portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-15368");
		portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

		log.info("---- Navigate to BMC Helix Dashboards ----");
		portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

		log.info("---- Initialize InsightFinderFeatureMethods ----");
		insightFinderFeatureMethods = new InsightFinderFeatureMethods();

		log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
		insightFinderFeatureMethods.openSettingsPage();
		insightFinderFeatureMethods.updateInsightFinderSettings(skillID, true, true);

		log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
		insightFinderFeatureMethods.openInsightFinderPage();

		log.info("---- Ask question in Insight Finder input prompt ----");
		insightFinderFeatureMethods.askQuestion(drsqa_15368_data.get("question1"),
				drsqa_15368_data.get("expectedHeader1"), false);
		java.util.List<String> insightResultForQues_2 = insightFinderFeatureMethods.validateAndGetInsightResultTable();
		log.info("********" + insightResultForQues_2.toString());

	}

	@Test(description = "Insight Finder: Verify generate dashboards for Service Health")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15290", Products = { Product.Dashboards })
	public void DRSQA_15290_InsightFinderVerifyGenerateDashboard(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-15290 ====");
		log.info("Objective: Verify Insight Finder dashboard generation for Service Health");

		try {
			doPreconditions(context, "DRSQA-15290");

			drsqa_15290_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_15290_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String serviceName = drsqa_15290_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input  prompt ----");
			Boolean isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithServiceName("question1", drsqa_15290_data),
					getWithServiceName("expectedHeader1", drsqa_15290_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader1", drsqa_15290_data));
			}

			log.info("---- Ask next question in Insight Finder input  prompt on new topic ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithServiceName("question2", drsqa_15290_data),
					getWithServiceName("expectedHeader2", drsqa_15290_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader2", drsqa_15290_data));
			}

			log.info("---- Ask next question in Insight Finder input  prompt on new topic ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithServiceName("question3", drsqa_15290_data),
					getWithServiceName("expectedHeader3", drsqa_15290_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader3", drsqa_15290_data));
			}

			log.info("---- Ask next question in Insight Finder input  prompt on new topic ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultCount(
					getWithServiceName("question4", drsqa_15290_data),
					getWithServiceName("expectedHeader4", drsqa_15290_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader4", drsqa_15290_data));
			}

			log.info("---- Ask next question in Insight Finder input prompt on new topic ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultCount(
					getWithServiceName("question5", drsqa_15290_data),
					getWithServiceName("expectedHeader5", drsqa_15290_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader5", drsqa_15290_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithServiceName("dashboardTitle", drsqa_15290_data),
					getWithServiceName("dashboardDescription", drsqa_15290_data));

			insightFinderFeatureMethods
					.openSavedDashboardFolder(getWithServiceName("dashboardTitle", drsqa_15290_data));

			log.info("---- Validate Dashboard Folder Content with 5 panels ----");
			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithServiceName("dashboardTitle", drsqa_15290_data));

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify generate dashboards for Predictions")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15291", Products = { Product.Dashboards })
	public void DRSQA_15291_InsightFinderVerifyGenerateDashboard(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-15291 ====");
		log.info("Objective: Verify Insight Finder dashboard generate dashboards for Predictions");

		try {
			doPreconditions(context, "DRSQA-15291");

			drsqa_15291_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_15291_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String serviceName = drsqa_15291_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			Boolean isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultCount(
					getWithServiceName("question1", drsqa_15291_data),
					getWithServiceName("expectedHeader1", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader1", drsqa_15291_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question2", drsqa_15291_data),
					getWithServiceName("expectedHeader2", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader2", drsqa_15291_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question3", drsqa_15291_data),
					getWithServiceName("expectedHeader3", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader3", drsqa_15291_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultCount(
					getWithServiceName("question4", drsqa_15291_data),
					getWithServiceName("expectedHeader4", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader4", drsqa_15291_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question5", drsqa_15291_data),
					getWithServiceName("expectedHeader5", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader5", drsqa_15291_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question6", drsqa_15291_data),
					getWithServiceName("expectedHeader6", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader6", drsqa_15291_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question7", drsqa_15291_data),
					getWithServiceName("expectedHeader7", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader7", drsqa_15291_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question8", drsqa_15291_data),
					getWithServiceName("expectedHeader8", drsqa_15291_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader8", drsqa_15291_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithServiceName("dashboardTitle", drsqa_15291_data),
					getWithServiceName("dashboardDescription", drsqa_15291_data));

			insightFinderFeatureMethods
					.openSavedDashboardFolder(getWithServiceName("dashboardTitle", drsqa_15291_data));

			log.info("---- Validate Dashboard Folder Content with 8 panels ----");
			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithServiceName("dashboardTitle", drsqa_15291_data));

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify AIOps dashboard gets generated for Situations using Helix GPT")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15303", Products = { Product.Dashboards })
	public void DRSQA_15303_InsightFinderVerifyAiopsDashboard(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-15303 ====");
		log.info("Insight Finder: Verify AIOps dashboard gets generated for Situations using Helix GPT");

		try {
			doPreconditions(context, "DRSQA-15303");

			drsqa_15303_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_15303_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String serviceName = drsqa_15303_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question1", drsqa_15303_data),
					getWithServiceName("expectedHeader1", drsqa_15303_data), false);

			log.info(
					"---- Ask question in Insight Finder input prompt and validate timeColumnName values are within last 2 days----");
			isSuccess = insightFinderFeatureMethods.askQuestion(getWithServiceName("question2", drsqa_15303_data),
					getWithServiceName("expectedHeader2", drsqa_15303_data), false);
			if (isSuccess) {
				List<String> insightResultForQues_2 = insightFinderFeatureMethods.validateAndGetInsightResultTable();
				log.info("********" + insightResultForQues_2.toString());
				if (insightResultForQues_2 != null && !insightResultForQues_2.isEmpty()
						&& !(insightResultForQues_2.size() == 1
								&& insightResultForQues_2.get(0).equalsIgnoreCase("No data"))) {
					List<String> timeColumnValues = insightFinderFeatureMethods
							.extractAndValidateColumnDates(getWithServiceName("timeColumnName", drsqa_15303_data), 2);
					log.info("Values inside Creation Time Column" + timeColumnValues);
				}
				expectedHeaders.add(getWithServiceName("expectedHeader2", drsqa_15303_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithServiceName("dashboardTitle", drsqa_15303_data),
					getWithServiceName("dashboardDescription", drsqa_15303_data));

			insightFinderFeatureMethods
					.openSavedDashboardFolder(getWithServiceName("dashboardTitle", drsqa_15303_data));

			log.info("---- Validate Dashboard Folder Content with panels ----");
			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithServiceName("dashboardTitle", drsqa_15303_data));

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify generate dashboards for BWF")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15293", Products = { Product.Dashboards })
	public void DRSQA_15293_InsightFinderVerifyGenerateDashboardForBwf(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-15291 ====");
		log.info("Objective: Insight Finder: Verify generate dashboards for BWF");

		try {
			doPreconditions(context, "DRSQA-15293");

			drsqa_15293_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_15293_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String serviceName = drsqa_15293_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			Boolean isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultCount(
					getWithServiceName("question1", drsqa_15293_data),
					getWithServiceName("expectedHeader1", drsqa_15293_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader1", drsqa_15293_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question2", drsqa_15293_data),
					getWithServiceName("expectedHeader2", drsqa_15293_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader2", drsqa_15293_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question3", drsqa_15293_data),
					getWithServiceName("expectedHeader3", drsqa_15293_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader3", drsqa_15293_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question4", drsqa_15293_data),
					getWithServiceName("expectedHeader4", drsqa_15293_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader4", drsqa_15293_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question5", drsqa_15293_data),
					getWithServiceName("expectedHeader5", drsqa_15293_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader5", drsqa_15293_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question6", drsqa_15293_data),
					getWithServiceName("expectedHeader6", drsqa_15293_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader6", drsqa_15293_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question7", drsqa_15293_data),
					getWithServiceName("expectedHeader7", drsqa_15293_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader7", drsqa_15293_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithServiceName("dashboardTitle", drsqa_15293_data),
					getWithServiceName("dashboardDescription", drsqa_15293_data));

			insightFinderFeatureMethods
					.openSavedDashboardFolder(getWithServiceName("dashboardTitle", drsqa_15293_data));

			log.info("---- Validate Dashboard Folder Content with 8 panels ----");

			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithServiceName("dashboardTitle", drsqa_15293_data));

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify AIOps dashboard gets generated for Events using Helix GPT")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15302", Products = { Product.Dashboards })
	public void DRSQA_15302_InsightFinderVerifyAiopsDashboardForEvents(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-15302 ====");
		log.info(" Insight Finder: Verify AIOps dashboard gets generated for Events using Helix GPT");

		try {
			doPreconditions(context, "DRSQA-15302");

			drsqa_15302_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_15302_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String serviceName = drsqa_15302_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question1", drsqa_15302_data),
					getWithServiceName("expectedHeader1", drsqa_15302_data), false);

			log.info(
					"---- Ask question in Insight Finder input prompt and validate timeColumnName values are within 2 days ----");
			isSuccess = insightFinderFeatureMethods.askQuestion(getWithServiceName("question2", drsqa_15302_data),
					getWithServiceName("expectedHeader2", drsqa_15302_data), false);
			if (isSuccess) {
				List<String> insightResultForQues_2 = insightFinderFeatureMethods.validateAndGetInsightResultTable();
				log.info("********" + insightResultForQues_2.toString());
				if (insightResultForQues_2 != null && !insightResultForQues_2.isEmpty()
						&& !(insightResultForQues_2.size() == 1
								&& insightResultForQues_2.get(0).equalsIgnoreCase("No data"))) {
					List<String> timeColumnValues = insightFinderFeatureMethods
							.extractAndValidateColumnDates(getWithServiceName("timeColumnName", drsqa_15302_data), 2);
					log.info("Values inside Creation Time Column" + timeColumnValues);
				}
				expectedHeaders.add(getWithServiceName("expectedHeader2", drsqa_15302_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithServiceName("dashboardTitle", drsqa_15302_data),
					getWithServiceName("dashboardDescription", drsqa_15302_data));

			insightFinderFeatureMethods
					.openSavedDashboardFolder(getWithServiceName("dashboardTitle", drsqa_15302_data));

			log.info("---- Validate Dashboard Folder Content with panels ----");
			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithServiceName("dashboardTitle", drsqa_15302_data));

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestion(getWithServiceName("question1", drsqa_15302_data),
					getWithServiceName("expectedHeader1", drsqa_15302_data), true);
			if (isSuccess) {
				insightFinderFeatureMethods
						.removeQueryWithHeader(getWithServiceName("expectedHeader1", drsqa_15302_data));
			}

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify ITSM Dashboards gets generated for supported views using Helix GPT")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-13613", Products = { Product.Dashboards })
	public void DRSQA_13613_InsightFinderVerifyITSMDashboard(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-13613 ====");
		log.info("Insight Finder: Verify ITSM Dashboards gets generated for supported views using Helix GPT");

		try {
			doPreconditions(context, "DRSQA-13613");

			drsqa_13613_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_13613_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String serviceName = drsqa_13613_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			Boolean isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question1", drsqa_13613_data),
					getWithServiceName("expectedHeader1", drsqa_13613_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader1", drsqa_13613_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question2", drsqa_13613_data),
					getWithServiceName("expectedHeader2", drsqa_13613_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader2", drsqa_13613_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question3", drsqa_13613_data),
					getWithServiceName("expectedHeader3", drsqa_13613_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader3", drsqa_13613_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question4", drsqa_13613_data),
					getWithServiceName("expectedHeader4", drsqa_13613_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader4", drsqa_13613_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithServiceName("dashboardTitle", drsqa_13613_data),
					getWithServiceName("dashboardDescription", drsqa_13613_data));

			insightFinderFeatureMethods
					.openSavedDashboardFolder(getWithServiceName("dashboardTitle", drsqa_13613_data));

			log.info("---- Validate Dashboard Folder Content with panels ----");
			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithServiceName("dashboardTitle", drsqa_13613_data));

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestion(getWithServiceName("question1", drsqa_13613_data),
					getWithServiceName("expectedHeader1", drsqa_13613_data), true);
			if (isSuccess) {
				insightFinderFeatureMethods
						.removeQueryWithHeader(getWithServiceName("expectedHeader1", drsqa_13613_data));
			}

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify CMDB asset dashboards gets generated using Helix GPT")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-13614", Products = { Product.Dashboards })
	public void DRSQA_13614_InsightFinderVerifyCMDBDashboard(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-13614 ====");
		log.info("Insight Finder: Verify CMDB asset dashboards gets generated using Helix GPT");

		try {
			doPreconditions(context, "DRSQA-13614");

			drsqa_13614_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_13614_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String serviceName = drsqa_13614_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			Boolean isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question1", drsqa_13614_data),
					getWithServiceName("expectedHeader1", drsqa_13614_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader1", drsqa_13614_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithServiceName("question2", drsqa_13614_data),
					getWithServiceName("expectedHeader2", drsqa_13614_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader2", drsqa_13614_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question3", drsqa_13614_data),
					getWithServiceName("expectedHeader3", drsqa_13614_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader3", drsqa_13614_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithServiceName("question4", drsqa_13614_data),
					getWithServiceName("expectedHeader4", drsqa_13614_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader4", drsqa_13614_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question5", drsqa_13614_data),
					getWithServiceName("expectedHeader5", drsqa_13614_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithServiceName("expectedHeader5", drsqa_13614_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithServiceName("dashboardTitle", drsqa_13614_data),
					getWithServiceName("dashboardDescription", drsqa_13614_data));

			insightFinderFeatureMethods
					.openSavedDashboardFolder(getWithServiceName("dashboardTitle", drsqa_13614_data));

			log.info("---- Validate Dashboard Folder Content with panels ----");

			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithServiceName("dashboardTitle", drsqa_13614_data));

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestion(getWithServiceName("question1", drsqa_13614_data),
					getWithServiceName("expectedHeader1", drsqa_13614_data), true);
			if (isSuccess) {
				insightFinderFeatureMethods
						.removeQueryWithHeader(getWithServiceName("expectedHeader1", drsqa_13614_data));
			}

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify Telemetry events")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-15292", Products = { Product.Dashboards })
	public void DRSQA_15292_InsightFinderVerifyTelemetryEvents(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-15292 ====");
		log.info("Insight Finder: Verify Telemetry events");

		try {
			doPreconditions(context, "DRSQA-15292");

			drsqa_15292_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_15292_Data");

			String serviceName = drsqa_15292_data.get("serviceName");
			log.info("---- Using Service Name: " + serviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(
					getWithServiceName("question1", drsqa_15292_data),
					getWithServiceName("expectedHeader1", drsqa_15292_data), true);

			log.info("Verify tracking information in stream API payload");
			insightFinderFeatureMethods.verifyGenerateVisualizationTracking();

			log.info("Click on Last recommendation");
			insightFinderFeatureMethods.clickLastRecommendation();

			log.info("Verify tracking information in stream API payload");
			insightFinderFeatureMethods.verifyGenerateVisualizationTracking();

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}
	}

	@Test(description = "Insight Finder: Verify AIOps dashboard gets generated for Metrics using Helix GPT")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-13612", Products = { Product.Dashboards })
	public void DRSQA_13612_InsightFinderVerifyAiopsDashboardForMetrics(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-13612 ====");
		log.info("Insight Finder: Verify AIOps dashboard gets generated for Metrics using Helix GPT");

		try {
			doPreconditions(context, "DRSQA-13612");

			drsqa_13612_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_13612_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String deviceName = drsqa_13612_data.get("deviceName");
			log.info("---- Using Device Name: " + deviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			Boolean isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithDeviceName("question1", drsqa_13612_data),
					getWithDeviceName("expectedHeader1", drsqa_13612_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithDeviceName("expectedHeader1", drsqa_13612_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithDeviceName("question2", drsqa_13612_data),
					getWithDeviceName("expectedHeader2", drsqa_13612_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithDeviceName("expectedHeader2", drsqa_13612_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithDeviceName("question3", drsqa_13612_data),
					getWithDeviceName("expectedHeader3", drsqa_13612_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithDeviceName("expectedHeader3", drsqa_13612_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithDeviceName("dashboardTitle", drsqa_13612_data),
					getWithDeviceName("dashboardDescription", drsqa_13612_data));

			insightFinderFeatureMethods.openSavedDashboardFolder(getWithDeviceName("dashboardTitle", drsqa_13612_data));

			log.info("---- Validate Dashboard Folder Content with panels ----");
			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithDeviceName("dashboardTitle", drsqa_13612_data));

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestion(getWithDeviceName("question1", drsqa_13612_data),
					getWithDeviceName("expectedHeader1", drsqa_13612_data), true);
			if (isSuccess) {
				insightFinderFeatureMethods
						.removeQueryWithHeader(getWithDeviceName("expectedHeader1", drsqa_13612_data));
			}

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Verify AIOps dashboard gets generated for Processor Metrics using Helix GPT")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-16623", Products = { Product.Dashboards })
	public void DRSQA_16623_InsightFinderVerifyAiopsDashboardForProcessorMetrics(ITestContext context)
			throws IOException {

		log.info("==== TEST STARTED: DRSQA-16623 ====");
		log.info("Insight Finder: Verify AIOps dashboard gets generated for Processor Metrics using Helix GPT");

		try {
			doPreconditions(context, "DRSQA-16623");

			drsqa_16623_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_16623_Data");
			List<String> expectedHeaders = new ArrayList<>();

			String deviceName = drsqa_16623_data.get("deviceName");
			log.info("---- Using Device Name: " + deviceName + " ----");

			log.info("---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			Boolean isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithDeviceName("question1", drsqa_16623_data),
					getWithDeviceName("expectedHeader1", drsqa_16623_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithDeviceName("expectedHeader1", drsqa_16623_data));
			}

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestionAndValidateCanvas(
					getWithDeviceName("question2", drsqa_16623_data),
					getWithDeviceName("expectedHeader2", drsqa_16623_data), true);
			if (isSuccess) {
				expectedHeaders.add(getWithDeviceName("expectedHeader2", drsqa_16623_data));
			}

			insightFinderFeatureMethods.saveDashboard(getWithDeviceName("dashboardTitle", drsqa_16623_data),
					getWithDeviceName("dashboardDescription", drsqa_16623_data));

			insightFinderFeatureMethods.openSavedDashboardFolder(getWithDeviceName("dashboardTitle", drsqa_16623_data));

			log.info("---- Validate Dashboard Folder Content with panels ----");
			insightFinderFeatureMethods.ValidateDashboardFolderContent(expectedHeaders);

			insightFinderFeatureMethods.deleteDashboard(getWithDeviceName("dashboardTitle", drsqa_16623_data));

			log.info("---- Click on 'Helix GPT Insight Finder' menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("---- Ask question in Insight Finder input prompt ----");
			isSuccess = insightFinderFeatureMethods.askQuestion(getWithDeviceName("question1", drsqa_16623_data),
					getWithDeviceName("expectedHeader1", drsqa_16623_data), true);
			if (isSuccess) {
				insightFinderFeatureMethods
						.removeQueryWithHeader(getWithDeviceName("expectedHeader1", drsqa_16623_data));
			}

		} finally {
			log.info("---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@Test(description = "Insight Finder: Enhance ITSM semantic model - phase 2 and Insight Finder: Allow use of relative terms in prompts")
	@SolqaDetails(authorName = "mhedaoo", JiraID = "DRSQA-17108", Products = { Product.Dashboards })
	public void DRSQA_17108_InsightFinderEnhanceItsmSemanticModelPhase2(ITestContext context) throws IOException {

		log.info("==== TEST STARTED: DRSQA-17108 ====");
		log.info(
				"Insight Finder: Enhance ITSM semantic model - phase 2 and Insight Finder: Allow use of relative terms in prompts");

		try {
			log.info("[Step 1] ---- Preconditions: login to ADE platform and initialize Insight Finder ----");
			doPreconditions(context, "DRSQA-17108");

			log.info("[Step 2] ---- Load test data: DRSQA_17108_Data from InsightFinderTestData.json ----");
			drsqa_17108_data = readJson.getInputValuesMapIncludingLabelField(
					"ITOM" + File.separator + SolQAConstants.HELIX_GPT_INSIGHT_FINDER, "InsightFinderTestData",
					"DRSQA_17108_Data");

			log.info("[Step 3] ---- Navigate to BMC Helix Dashboards ----");
			portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);

			log.info("[Step 4] ---- Open Helix GPT Insight Finder from the menu ----");
			insightFinderFeatureMethods.openInsightFinderPage();

			log.info("[Step 5] ---- Ask question 1 and validate insight result table ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(drsqa_17108_data.get("question1"),
					drsqa_17108_data.get("expectedHeader1"), false);
			
			log.info("[Step 6] ---- Click last recommendation, submit, then validate insight result table ----");
			insightFinderFeatureMethods.clickLastRecommendation();
			List<String> insightResult = insightFinderFeatureMethods.validateAndGetInsightResultTable();
			log.info("********" + insightResult.toString());

			log.info("[Step 7] ---- Ask question 2 and validate insight result table ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(drsqa_17108_data.get("question2"),
					drsqa_17108_data.get("expectedHeader2"), true);

			log.info("[Step 8] ---- Ask question 3 and validate insight result table ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(drsqa_17108_data.get("question3"),
					drsqa_17108_data.get("expectedHeader3"), true);

			log.info("[Step 9] ---- Ask question 4 and validate insight result table ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(drsqa_17108_data.get("question4"),
					drsqa_17108_data.get("expectedHeader4"), true);

			log.info("[Step 10] ---- Ask question 5 and validate insight result table ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(drsqa_17108_data.get("question5"),
					drsqa_17108_data.get("expectedHeader5"), true);

			log.info("[Step 11] ---- Ask question 6 and validate insight result count ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultCount(drsqa_17108_data.get("question6"),
					drsqa_17108_data.get("expectedHeader6"), true);

			log.info("[Step 12] ---- Ask question 7 and validate insight result table ----");
			insightFinderFeatureMethods.askQuestionAndValidateInsightResultTable(drsqa_17108_data.get("question7"),
					drsqa_17108_data.get("expectedHeader7"), true);

		} finally {
			log.info("[Step 13] ---- Validate all soft assertions collected from InsightFinderPage ----");
			insightFinderFeatureMethods.assertAllSoftAssertions();
		}

	}

	@AfterMethod(alwaysRun = true)
	public void closeAllBrowsers() {
		log.info("---- Starting cleanup process ----");
		try {
			if (portalFeatureMethods != null) {
				portalFeatureMethods.closeBrowser();
			}
			if (insightFinderFeatureMethods != null) {
				insightFinderFeatureMethods.closeBrowser();
			}
			log.info("---- Cleanup completed successfully ----");
		} catch (Exception e) {
			log.error("Error during cleanup: " + e.getMessage(), e);
		}
	}
}