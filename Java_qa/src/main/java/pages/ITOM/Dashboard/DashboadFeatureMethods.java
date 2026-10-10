package pages.ITOM.Dashboard;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import framework.BasePage;
import util.SolqaLogger;

public class DashboadFeatureMethods extends BasePage {

    SolqaLogger log = new SolqaLogger(DashboadFeatureMethods.class.getName());
    DashboardPage dashboardPage;

    public DashboadFeatureMethods(WebDriver driver) {
        PageFactory.initElements(getDriver(),this);
        dashboardPage = new DashboardPage(getDriver());
    }

    public DashboadFeatureMethods() {
        PageFactory.initElements(getDriver(),this);
        dashboardPage = new DashboardPage(getDriver());
    }

    public void openMenu(String menu) {
        dashboardPage.openMenu(menu);
    }

    public void openFoldersFromMenu(List<String> folderName) {
        dashboardPage.openFoldersFromMenu(folderName);
    }

    public void openDashboard(String folderName) {
        dashboardPage.openDashboard(folderName);
    }

    public void selectValues(String key, String value) {
        dashboardPage.selectValues(key, value);
    }

    public boolean isLatencyPanelDataPresent(String title) {
        return dashboardPage.isLatencyPanelDataPresent(title);
    }

    public String getOtelNameSpaceValue() {
        return dashboardPage.getOtelNameSpaceValue();
    }

    public List<String> getAllBusinessServices() {
        return dashboardPage.getAllBusinessServices();
    }

    public DashboardPage selectBusinessService(String businessService) {
        dashboardPage.selectBusinessService(businessService);
        return dashboardPage;
    }

    public void verifyVariableValue(String key, String value) {
        dashboardPage.verifyVariableValue(key, value);
    }

    public void clearAllFilters() {
        dashboardPage.clearAllFilters();
    }

    public void createNewVisualizationDashboard(String dashboardName) {
        dashboardPage.createNewVisualizationDashboard(dashboardName);
    }
    public void verifySourceOptionPresent(String option) {
        org.testng.Assert.assertTrue(dashboardPage.isSourceOptionPresent(option),
                "Source option '" + option + "' is not available.");
    }

    public void enterQueryDetails(String key, String value) {
        dashboardPage.enterQueryDetails(key, value);
    }

    public void selectQueryDetails(String key, String value) {
        dashboardPage.selectQueryDetails(key, value);
    }

    public void selectVisualizationPlugin(String pluginName) {
        dashboardPage.selectVisualizationPlugin(pluginName);
    }

    public void configureCrossTab(String rowField, String columnField) {
        dashboardPage.configureCrossTab(rowField, columnField);
    }

    public boolean verifyCrossTabHeaders(String rowHeader, String columnHeader) {
        return dashboardPage.verifyCrossTabHeaders(rowHeader, columnHeader);
    }
    public void configureCombinationBarChart(String xAxisMetric, String xAxisLabel, String leftYAxisMetric, String leftYAxisChartType, String leftYAxisLabel) {
        dashboardPage.configureCombinationBarChart(xAxisMetric, xAxisLabel, leftYAxisMetric, leftYAxisChartType, leftYAxisLabel);
    }

    public boolean validateCombinationChartData(String expectedXAxisTitle, String expectedYAxisTitle) {
        return dashboardPage.validateCombinationChartData(expectedXAxisTitle, expectedYAxisTitle);
    }
    public void enterPanelDetails(String title, String description) {
        dashboardPage.enterPanelDetails(title);
        dashboardPage.enterPanelDescription(description);
    }

    public void enterDashboardDetails(String title, String description) {
        dashboardPage.enterDashboardDetails(title);
        dashboardPage.enterDashboardDescription(description);
    }

    public void selectTableData() {
        dashboardPage.selectTableData();
    }

    public void saveChanges() {
        dashboardPage.saveChanges();
    }

    public void saveDashboard() {
        dashboardPage.saveDashboard();
    }

    public void addChangeMessage(String message) {
        dashboardPage.addChangeMessage(message);
    }

    public void openDashboardSettings() {
        dashboardPage.openDashboardSettings();
    }

    public void addTags(String tags) {
        dashboardPage.addTags(tags);
    }

    public boolean verifyDashboardPresentWithTag(String dashboardName, String tag) {
        return dashboardPage.verifyDashboardPresentWithTag(dashboardName, tag);
    }

    public void clearETLFilters() {
        dashboardPage.clearETLFilters();
    }

    public void verifyDashboardTitle(String dashboardTitle) {
        dashboardPage.verifyDashboardTitle(dashboardTitle);
    }

    public void verifyPanels(String dashboardTitle) {
        dashboardPage.verifyPanels(dashboardTitle);
    }

    public void refreshDashboard() {
        dashboardPage.refreshDashboard();
    }

    public void openETLList() {
        dashboardPage.openETLList();
    }

    public void clickOnNewScheduledReport() {
        dashboardPage.clickOnNewScheduledReport();
    }

    public void addScheduledReportDetails(String schedulerName, String schedulerDescription, String dashboardName, String recepientsIds, String recepientsBCCIDs) {
        dashboardPage.addScheduledReportDetails(schedulerName, schedulerDescription, dashboardName, recepientsIds, recepientsBCCIDs);
    }

    public void openSchedulerReport(String schedulerName) {
        dashboardPage.openSchedulerReport(schedulerName);
    }

    public void verifyBCCRecipient(String bccRecipient) {
        dashboardPage.verifyBCCRecipient(bccRecipient);
    }

    public void verifyPanelContent() {
		dashboardPage.verifyNoDataIsNotPresent();
		dashboardPage.verifyPanelContentPresent();
    }

    public boolean isPanelPresent(String title) {
        return dashboardPage.isPanelPresent(title);
    }
    
    public void disableSearchBarMenu() {
    	dashboardPage.disableSearchBarMenu();
    }
    
    public void clickOnMediumAssignedCount() {
		dashboardPage.clickOnMediumAssignedCount();
	}
    
    public void clickOnFirstIncident() {
    	dashboardPage.clickOnFirstIncident();
    }

    public void verifyDashboardPageLaunchedSuccessfully() {
        dashboardPage.verifyDashboardPageLaunchedSuccessfully();
    }
    
    public DashboardPage verifyDashboardsInFolder(List<String> folderNames) {
        dashboardPage.verifyDashboardsInFolder(folderNames);
        return dashboardPage;
    }
    
	public DashboadFeatureMethods configureForecastQuery(String queryType, String source, String metric, String label,
			String labelValueFilter) {
		dashboardPage.configureForecastQuery(queryType, source, metric, label, labelValueFilter);
		return this;
	}

	public DashboadFeatureMethods verifyEditPanelPageOpened() {
		dashboardPage.verifyEditPanelPageOpened();
		return this;
	}

	public DashboadFeatureMethods verifyForecastAlertMessage(String expectedAlertText) {
		dashboardPage.verifyForecastAlertMessage(expectedAlertText);
		return this;
	}

	public DashboadFeatureMethods selectTimeRange(String timeRange) {
		dashboardPage.selectTimeRange(timeRange);
		return this;
	}

	public DashboadFeatureMethods verifyForecastGraphAndPredictionDisplayed() {
		dashboardPage.verifyForecastGraphAndPredictionDisplayed();
		return this;
	}

	public DashboadFeatureMethods verifyPanelTitleOnChart(String expectedTitle) {
		dashboardPage.verifyPanelTitleOnChart(expectedTitle);
		return this;
	}

	public DashboadFeatureMethods selectTooltipMode(String tooltipMode) {
		dashboardPage.selectTooltipMode(tooltipMode);
		return this;
	}

	public DashboadFeatureMethods hoverOnPanelChart() {
		dashboardPage.hoverOnPanelChart();
		return this;
	}

	public DashboadFeatureMethods verifyTooltipDisplayed() {
		dashboardPage.verifyTooltipDisplayed();
		return this;
	}

	public DashboadFeatureMethods clickAwayFromChart() {
		dashboardPage.clickAwayFromChart();
		return this;
	}

	public DashboadFeatureMethods verifyTooltipNotDisplayed() {
		dashboardPage.verifyTooltipNotDisplayed();
		return this;
	}

	public DashboadFeatureMethods toggleLegendVisibility() {
		dashboardPage.toggleLegendVisibility();
		return this;
	}

	public DashboadFeatureMethods verifyLegendNotVisible() {
		dashboardPage.verifyLegendNotVisible();
		return this;
	}

	public DashboadFeatureMethods selectAlgorithmType(String algorithmType) {
		dashboardPage.selectAlgorithmType(algorithmType);
		return this;
	}

	public DashboadFeatureMethods selectFuturePeriod(String futurePeriod) {
		dashboardPage.selectFuturePeriod(futurePeriod);
		return this;
	}

	public DashboadFeatureMethods verifyFuturePeriodSelected(String expectedFuturePeriod) {
		dashboardPage.verifyFuturePeriodSelected(expectedFuturePeriod);
		return this;
	}

	public DashboadFeatureMethods selectDataSource(String dataSource) {
		dashboardPage.selectDataSource(dataSource);
		return this;
	}

	public DashboadFeatureMethods selectQueryType(String queryType) {
		dashboardPage.selectQueryType(queryType);
		return this;
	}

	public DashboadFeatureMethods verifyAxisOptionVisible() {
		dashboardPage.verifyAxisOptionVisible();
		return this;
	}

	public DashboadFeatureMethods verifyThresholdsOptionVisible() {
		dashboardPage.verifyThresholdsOptionVisible();
		return this;
	}

	public DashboadFeatureMethods verifyTimeSeriesChartDisplayed() {
		dashboardPage.verifyTimeSeriesChartDisplayed();
		return this;
	}

	public DashboadFeatureMethods selectAxisPlacement(String placement) {
		dashboardPage.selectAxisPlacement(placement);
		return this;
	}

	public DashboadFeatureMethods verifyAxisPlacementShiftedToRight() {
		dashboardPage.verifyAxisPlacementShiftedToRight();
		return this;
	}

	public DashboadFeatureMethods setSoftMinMax(String softMin, String softMax) {
		dashboardPage.setSoftMinMax(softMin, softMax);
		return this;
	}

	public DashboadFeatureMethods verifySoftMinMaxInForecastGraph(String expectedSoftMin, String expectedSoftMax) {
		dashboardPage.verifySoftMinMaxInForecastGraph(expectedSoftMin, expectedSoftMax);
		return this;
	}

	public DashboadFeatureMethods clickAddThreshold() {
		dashboardPage.clickAddThreshold();
		return this;
	}

	public DashboadFeatureMethods verifyNewThresholdAddedWithDeleteOption() {
		dashboardPage.verifyNewThresholdAddedWithDeleteOption();
		return this;
	}

	public DashboadFeatureMethods clickApplyChanges() {
		dashboardPage.clickApplyChanges();
		return this;
	}

	public DashboadFeatureMethods saveDashboardWithName(String dashboardName) {
		dashboardPage.saveDashboardWithName(dashboardName);
		return this;
	}

	public DashboadFeatureMethods reopenDashboard(String menuItem, String dashboardName) {
		dashboardPage.reopenDashboard(menuItem, dashboardName);
		return this;
	}

	public DashboadFeatureMethods deleteDashboard(String dashboardName) {
		dashboardPage.deleteDashboard(dashboardName);
		return this;
	}

    public DashboadFeatureMethods navigateTo_HelixGPT_ITSM_Service_Collaborator_Value_Dashboard() {
        dashboardPage.navigateTo_HelixGPT_ITSM_Service_Collaborator_Value_Dashboard();
        return this;
    }

    public DashboadFeatureMethods selectRequiredTimePeriod(String timePeriod) {
        dashboardPage.selectRequiredTimePeriod(timePeriod);
        return this;
    }

    public String getTotalIncidentsCount() {
        return dashboardPage.getTotalIncidentsCount();
    }

    public String getMTTRValue() {
        return dashboardPage.getMTTRValue();
    }

    public String getAverageResponseTimeWithoutServiceCollaborator() {
        return dashboardPage.getAverageResponseTimeWithoutServiceCollaborator();
    }

    public String getAverageResponseTimeWithServiceCollaborator() {
        return dashboardPage.getAverageResponseTimeWithServiceCollaborator();
    }

    public String getServiceCollaboratorSkillsUsage() {
        return dashboardPage.getServiceCollaboratorSkillsUsage();
    }

    public boolean isMTTRTrendPresent() {
        return dashboardPage.isMTTRTrendPresent();
    }

    public String getNumberOfTotalIncidents() {
        return dashboardPage.getNumberOfTotalIncidents();
    }

    public String getNumberOfUniqueUsersInteractedWithServiceCollaborator() {
        return dashboardPage.getNumberOfUniqueUsersInteractedWithServiceCollaborator();
    }

    public boolean isSLABreachedTrendPresent() {
        return dashboardPage.isSLABreachedTrendPresent();
    }

    public DashboadFeatureMethods cancelChanges() {
        dashboardPage.cancelChanges();
        return this;
    }

    public DashboadFeatureMethods backToDashboard() {
        dashboardPage.backToDashboard();
        return this;
    }

    public DashboadFeatureMethods clickOnDashboardsLink() {
        dashboardPage.clickOnDashboardsLink();
        return this;
    }


}
