package pages.ITOM.GrafanaDashboard;

import framework.BasePage;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

import java.util.List;
import java.util.Map;

public class Dashboard_FeatureMethods extends BasePage {
    static SolqaLogger log = new SolqaLogger(Portal_FeatureMethods.class.getName());
    CommonWaits commonWaits;
    WebDriver driver;
    ReportingPage reportingPage;

    public Dashboard_FeatureMethods() {
        DOMConfigurator.configure("log4j_Config.xml");
        commonWaits = new CommonWaits();
        reportingPage = new ReportingPage();

    }
    public void verifyLoginToADEPlatform() {
        log.info("----Verify Login to ADE Portal");
        //platformLandingPage.verifyPlatformLandingPageOpened();
    }
    public void crossLaunchOperationManagement() {
        reportingPage.launch_OpManagement();
        log.info("-----cross launch Operation Management successfully");
        reportingPage.switchToWindow(SolQAConstants.BHOMTitle);
    }
    public void crossLaunchDashboard(){
        reportingPage.launch_Dashboard();
        log.info("-----cross launch Dashboard successfully");
        reportingPage.switchToWindow(SolQAConstants.DashboardTitle);
    }
    public void switchToDashboardWindow(){
        reportingPage.switchToWindow(SolQAConstants.DashboardTitle);
    }
    public void switchToOperationManagementWindow(){
        reportingPage.switchToWindow(SolQAConstants.BHOMTitle);
    }
    public void launchDashboardFromDevice(){
        reportingPage.launchDashboardFromDevice();
    }
    public void launchDashboardFromEvent(){
        reportingPage.launchDashboardFromEvent();
    }
    public void toggle(){
        reportingPage.toggle();
        log.info("-----Toggle dropdown open");
    }
    public void monitoringDevices(){
        reportingPage.monitoringDevices();
        log.info("Open one device");
        reportingPage.openOneDevice();
    }
    public void monitoringEvents(){
        reportingPage.monitoringEvents();
        log.info("Open one monitor Event");
        reportingPage.openOneEvent();
    }
    public void metricsDashboardloaded(){
        reportingPage.verifyMetricsDashboardLoaded();
   }
    public void EventDashboardloaded(){
        reportingPage.verifyEventDashboardLoaded();
    }
    public void openDashboard(String dashboardName, String folderName){
        reportingPage.openDashboard(dashboardName, folderName);
    }
    public void verifyDashboardLoaded(String dashboardName){
        Assert.assertTrue(reportingPage.verifyDashboardLoaded(dashboardName));
    }
    public void verifyFilter(String filterName){
        reportingPage.verifyFilterForDashboard(filterName);
    }
    public void verifyAllDashboardFilters(){
        reportingPage.verifyAllDashboardFilters();

    }
    public void verifyDashboardHasNoErrors(){
        Assert.assertTrue(reportingPage.verifyDashboardHasNoErrors());
    }
    public boolean isShareDashboard(){
        return reportingPage.isShareDashboard();
    }
    public boolean isExportDashboardAsFile(String fileType){
        return reportingPage.isExportDashboardAsFile(fileType);
    }
    public void selectLast90Days(){
        Assert.assertTrue(reportingPage.selectLast90Days(),"Failed to select Last 90 Days time range");
        log.info("Failed to select Last 90 Days time range");
    }
    public void selectLast5years(){
        Assert.assertTrue(reportingPage.selectLast5years(),"Failed to select Last 5 years time range");
        log.info("Failed to select Last 5 years time range");
    }
    public boolean hasEditControlsVisible(){
        return reportingPage.hasEditControls();
    }
    public boolean verifyDashboardHasErrors(){
        return reportingPage.verifyDashboardHasErrors();
    }
    public boolean drillThroughCasesByAssignedGroup(){
        return reportingPage.drillThroughCasesByAssignedGroup();
    }
    public void validateCategoryAndUsageColumnsWithData(){
        reportingPage.validateCategoryAndUsageColumnsWithData();
    }
    
    public void validateLicenseUsageDataInDashboard(){
        reportingPage.validateLicenseUsageDataInDashboard();
    }

    public Map<String, Double> getEventsWithoutNodeIdPieChartData() {
        return reportingPage.getEventsWithoutNodeIdPieChartData();
    }

    public Map<String, Double> getEventsWithNodeWithoutServicePieChartData() {
        return reportingPage.getEventsWithNodeWithoutServicePieChartData();
    }

    public List<String> getAllClassEventsWithoutNodeIdLegendItems() {
        return reportingPage.getAllClassEventsWithoutNodeIdLegendItems();
    }

    public List<Double> getAllClassEventsWithoutNodeIdPieChartPercentages() {
        return reportingPage.getAllClassEventsWithoutNodeIdPieChartPercentages();
    }

    public List<String> getAllClassEventsWithNodeWithoutServiceLegendItems() {
        return reportingPage.getAllClassEventsWithNodeWithoutServiceLegendItems();
    }

    public List<Double> getAllClassEventsWithNodeWithoutServicePieChartPercentages() {
        return reportingPage.getAllClassEventsWithNodeWithoutServicePieChartPercentages();
    }
}
