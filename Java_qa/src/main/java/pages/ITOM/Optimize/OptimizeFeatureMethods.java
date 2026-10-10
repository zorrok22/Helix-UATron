package pages.ITOM.Optimize;

import framework.Launcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.Assert;

import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Bhom.BhomPage;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import util.CommonWaits;
import util.CommonMethods;
import util.SolQAConstants;
import util.SolqaLogger;


public class OptimizeFeatureMethods extends Launcher {
    private static SolqaLogger log = new SolqaLogger(OptimizeFeatureMethods.class.getName());

    CommonWaits commonWaits;
	CommonMethods commonMethods;


    IntelligentAutomationPage iaPage;
    ServiceMonitoringPage smPage;
    BhomPage monitoringPage;

    OptimizePage optimizePage;
    public OptimizeFeatureMethods() {
        commonWaits=new CommonWaits();
        commonMethods=new CommonMethods();
        monitoringPage = new BhomPage();
        optimizePage = new OptimizePage(getDriver());
        smPage = new ServiceMonitoringPage(getDriver());
        iaPage = new IntelligentAutomationPage();
    }

    public void createEventFromOptimize(String uniqueEventMessage){
        iaPage.navigateToOptimizePage();
        iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
        optimizePage.clickOnWorkspaceTab();
        optimizePage.clickOnCreateEvent();
        optimizePage.createEvent(uniqueEventMessage);
        iaPage.closeWindow(SolQAConstants.HELIX_OPTIMIZE);
    }

    public void addBHOMETL(){
        iaPage.navigateToOptimizePage();
        iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
        optimizePage.clickOnAdministrationTab();
        optimizePage.addBHOMETL();
    }

    public void runBHOMETLAndVerifyStatus(){
        optimizePage.runBHOMETL();
    }

    public void deleteBHOMETL(){
        optimizePage.deleteBHOMETL();
    }

    public void VerifyEventFromOptimize(String uniqueEventMessage){
        iaPage.switchToWindow(SolQAConstants.BMC_HELIX_PORTAL);
        iaPage.navigateToOperationsManagementPage();
        monitoringPage.navigateToEventsTab();
        monitoringPage.searchEvent(uniqueEventMessage);
        Assert.assertEquals(monitoringPage.getEventCount(), 1, "Optimize event is not seen created.");
    }

    public void clickOnWorkspaceTab(){
        optimizePage.clickOnWorkspaceTab();
    }

    public void selectNavigationItem(String folderName) {
        optimizePage.selectNavigationItem(folderName);
    }

    public void selectWorkValue(String value) {
        optimizePage.selectWorkValue(value);
    }

    public void clickOnAdministrationTab() { optimizePage.clickOnAdministrationTab();}
    public void clickOnViewsTab() { optimizePage.clickOnViews();}
    public void clickOnMaintenanceTab() { optimizePage.clickOnMaintenanceTab();}
    public void clickOnAdditionalPackageTab() { optimizePage.clickOnAdditionalPackageTab();}
    public void reInstallExistingPackage(String packageName) { optimizePage.reInstallExistingPackage(packageName);}
    public void checkPackageReinstalled() { optimizePage.checkPackageReinstalled();}
    public void installPackage() { optimizePage.installPackage();}
    public void checkPackageinstalled() { optimizePage.checkPackageinstalled();}
    public void clickOnkubernetesView() { optimizePage.kubernetesView();}
    public void clickOncontainersView() { optimizePage.containersView();}
    public OptimizeFeatureMethods clickOnClustersView() { optimizePage.ClustersView();
    return this;
    }
    public void clickOnNodesView() { optimizePage.NodesView();}
    public void clickNamespacesView() { optimizePage.NamespacesView();}
    public void clickOnControllersView() { optimizePage.ControllersView();}
    public void clickOnPodsView() { optimizePage.PodsView();}
    public void clickOnRecommendationsView() {
        optimizePage.RecommendationsView();
        optimizePage.forecastedSaturationInfrastructureView();
        optimizePage.forecastedSaturationContainersView();
        optimizePage.oversubscribedClustersView();
        optimizePage.overallocatedContainersView();
    }
    public void clickOncapacityView() { optimizePage.capacityView();}
    public void clickOnCPUView() { optimizePage.CPUView();}
    public void clickOnmemoryView() { optimizePage.memoryView();}
    public void clickOnserversView() { optimizePage.serversView();}
    public void clickOnoverviewView() { optimizePage.overviewView();}
    public void clickOnserverName(String server) { optimizePage.serverNameView(server);}
    public void clickOnopenDashboard() { optimizePage.openDashboard();}
    public void clickOnshowinOperationsManagement() { optimizePage.showinOperationsManagement();}

    public void addHelixDashboardReportDetails(String name, String description, String dashboardName, List<String> reportFormat, String reportLevelValue) {
        optimizePage.addHelixDashboardReportDetails(name, description, dashboardName, reportFormat,  reportLevelValue);
    }

    public void verifyReportIsGenerated(String reportName, List<String> reportFormat) {
        optimizePage.verifyReportIsGenerated(reportName, reportFormat);
    }

    public void downloadAndVerifyReport(String reportName, List<String> reportFormat) {
        optimizePage.downloadAndVerifyReport(reportName, reportFormat);
    }

    public boolean compareFilesBinary(String filePath1, String filePath2) {
        return optimizePage.compareFilesBinary(filePath1, filePath2);
    }

    public void verifyBHCOPageLaunchedSuccessfully() {
        optimizePage.verifyBHCOPageLaunchedSuccessfully();
    }

    public void verifyTasksPresentInETLList(String taskName) {
        optimizePage.verifyTasksPresentInETLList(taskName);
    }

    public OptimizePage addNewRule(String ruleName, String templateGrp,String templateName, String actionToPerform){
    	iaPage.navigateToOptimizePage();
        iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
        optimizePage.clickOnAdministrationTab();
        optimizePage.addNewRule(ruleName, templateGrp, templateName, actionToPerform);
        return optimizePage;
    }
    
    public OptimizePage runRuleAndVerify(String ruleName){
        optimizePage.runRuleAndVerify(ruleName);
        return optimizePage;
    }
    
    public OptimizePage runSystemTaskAndWaitForRunFinish(String systemTaskName){
        optimizePage.runSystemTaskAndWaitForRunFinish(systemTaskName);
        return optimizePage;
    }
    
    public OptimizePage verifySaturationForecastAlertEventForHost(String host, String message){
        iaPage.switchToWindow(SolQAConstants.BMC_HELIX_PORTAL);
        iaPage.navigateToOperationsManagementPage();
        monitoringPage.navigateToEventsTab();
        commonWaits.waitForPageLoad(getDriver());
        
        String filterCriteria = String.format("( source_hostname equals %s )", host);
        monitoringPage.applyAdvanceFilterForEvent(filterCriteria);
        Assert.assertEquals(monitoringPage.getEventCount(), 1, "Saturation forecast alert is not seen created for host:" + host);
        monitoringPage.verifySaturationForecastAlertEventForHost(message);
        return optimizePage;
    }
    
    public OptimizePage closeOpenedEvent(){
        monitoringPage.closeEvent();
        return optimizePage;
    }
    
    public OptimizePage deleteRuleAndVerify(String ruleName){
    	iaPage.navigateToOptimizePage();
        iaPage.switchToWindow(SolQAConstants.HELIX_OPTIMIZE);
        optimizePage.clickOnAdministrationTab();
        optimizePage.deleteRuleAndVerify(ruleName);
        return optimizePage;
    }
       
    public void navigateToAllSystemsAndBusinessDrivers() {
        log.info("Navigating to Workspace -> All systems and business drivers");
        optimizePage.clickOnWorkspaceTab();
        optimizePage.clickOnAllSystemsAndBusinessDrivers();
    }

    public void clickOnUnassignedDomain() {
        log.info("Clicking on Unassigned domain");
        optimizePage.clickOnUnassignedDomain();
    }

    public void verifySortingFunctionalityForDateOfCreation() {
        log.info("Verifying sorting functionality for '" + SolQAConstants.DATE_OF_CREATION + "' column");
        optimizePage.verifySortingFunctionalityForColumn(SolQAConstants.DATE_OF_CREATION, 4);
    }

    public void verifySortingFunctionalityForLastActivity() {
        log.info("Verifying sorting functionality for '" + SolQAConstants.LAST_ACTIVITY + "' column");
        optimizePage.verifySortingFunctionalityForColumn(SolQAConstants.LAST_ACTIVITY, 5);
    }
    
    public OptimizePage verifySortingFunctionalityForName() {
        log.info("Verifying sorting functionality for '" + SolQAConstants.EXPRESSION_FIELD_NAME + "' column");
        optimizePage.verifySortingFunctionalityForColumn(SolQAConstants.EXPRESSION_FIELD_NAME, 3);
        return optimizePage;
    }
    
    public OptimizePage validateFilterResultForColumn(String columnName, int columnIndex, String filterOperator, String filterInput) {
        log.info("Validating filtered result for '" + columnName + "' column" + "for operator :" + filterOperator + "with value : " + filterInput);
        optimizePage.validateFilterResultForColumn(columnName, columnIndex, filterOperator, filterInput);
        return optimizePage;
    }
    
    public OptimizePage verifyFilterFunctionalityForColumn(String columnName, int columnIndex, String filterOperator, String filterInput) {
        log.info("Verifying filter functionality for '" + columnName + "' column" + "for operator :" + filterOperator + "with value : " + filterInput);
        optimizePage.verifyFilterFunctionalityForColumn(columnName, columnIndex, filterOperator, filterInput);
        return optimizePage;
    }
    
    /**
     * Gets a date in dd/MM/yyyy format by subtracting specified days from current date
     * @param days Number of days to subtract from current date (use positive number for past dates)
     * @return Date string in dd/MM/yyyy format
     */
    public String getDateMinusDays(int days) {
        log.info("Calculating date " + days + " days before current date");
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(System.currentTimeMillis() - days * 24L * 60 * 60 * 1000));
        log.info("Date calculated: " + date);
        return date;
    }
    
    public String findCommonPatternInList(int columnIndex) {
    	List<String> rowValues = optimizePage.getAllValuesForColumnByIndex(columnIndex); 
        String commonPattern = commonMethods.findCommonPatternInList(rowValues, 3);
        return commonPattern;
    }
    
    public String getElementFromColumn(int columnIndex) {
    	List<String> listValues = optimizePage.getAllValuesForColumnByIndex(columnIndex); 
    	if (listValues == null || listValues.isEmpty()) {
            log.warn("List is null or empty. Returning empty string.");
            return "";
        }
        String firstValue = listValues.get(0);
        log.info("First value from list: '" + firstValue + "'");
        
        return firstValue != null ? firstValue : "";
    }
    
	public OptimizeFeatureMethods navigateToBussinesService() {
		// TODO Auto-generated method stub
		  log.info("Navigating to Views -> Select Bussiness Service");
	        optimizePage.ClickOnBussinesService();
	        return this;
		
	}

	public String getServiceRiskByName(String inputServiceName) {
		// TODO Auto-generated method stub
		navigateToBussinesService();
		String risk = optimizePage.getServiceRiskByName(inputServiceName);
		  log.info("Return Risk count"); 
		return risk;
	}

	public void closeWindow(String helixOptimize) {
		// TODO Auto-generated method stub
		
		iaPage.closeWindow(helixOptimize);
		iaPage.switchToWindow(SolQAConstants.BMC_HELIX_PORTAL);
		
	}
    public OptimizeFeatureMethods navigateToVSphereVirtualMachines() {
        log.info("Navigating to vSphere Virtual Machines page");
        optimizePage.navigateToVSphereVirtualMachines();
        return this;
    }

    public OptimizeFeatureMethods navigateToVSphereInfrastructure() {
        log.info("Navigating to vSphere Infrastrure page");
        optimizePage.navigateToVSphereInfrastructure();
        return this;
    }
    public OptimizeFeatureMethods navigateToVSphereDataexplorer() {
        log.info("Navigating to vSphere Data explorer page");
        optimizePage.navigateToVSphereDataexplorer();
        return this;
    }
    public OptimizeFeatureMethods navigateToAWS() {
        log.info("Navigating to AWS page");
        optimizePage.navigateToAWS();
        return this;
    }
    public OptimizeFeatureMethods viewEc2Instances() {
        log.info("Navigating to AWS page");
        optimizePage.viewEc2Instances();
        return this;
    }
    public OptimizeFeatureMethods viewEc2Instance() {
        log.info("Navigating to AWS page");
        optimizePage.viewEc2Instance();
        return this;
    }
    public OptimizeFeatureMethods viewEC2IdleInstances() {
        log.info("Navigating to AWS page");
        optimizePage.viewEC2IdleInstances();
        return this;
    }
    public OptimizeFeatureMethods viewEC2IdleInstance() {
        log.info("Navigating to AWS page");
        optimizePage.viewEC2IdleInstance();
        return this;
    }
    public OptimizeFeatureMethods viewEBSVolumes() {
        log.info("Navigating to AWS page");
        optimizePage.viewEBSVolumes();
        return this;
    }
    public OptimizeFeatureMethods viewRecommendations() {
        log.info("Navigating to AWS page");
        optimizePage.viewRecommendations();
        return this;
    }

    public OptimizeFeatureMethods navigateToAzure() {
        log.info("Navigating to Azure page");
        optimizePage.navigateToAzure();
        return this;
    }
    public OptimizeFeatureMethods viewazuretabVirtualMachines() {
        log.info("Navigating to Azure Virtual Machines page");
        optimizePage.viewtabVirtualMachines();
        return this;
    }
    public OptimizeFeatureMethods viewazureVirtualmachine() {
        log.info("Navigating to Azure Virtual Machine page");
        optimizePage.viewazureVirtualmachine();
        return this;
    }
    public OptimizeFeatureMethods viewazuretabIdleVirtualMachines() {
        log.info("Navigating to Azure Idle Virtual Machines page");
        optimizePage.viewazuretabIdleVirtualMachines();
        return this;
    }
    public OptimizeFeatureMethods viewAzureRecommendations() {
        log.info("Navigating to Azur Recommendations page");
        optimizePage.viewAzureRecommendations();
        return this;
    }
    public OptimizeFeatureMethods navigateTokubernetes() {
        log.info("Navigating to vSphere Data explorer page");
        optimizePage.navigateTokubernetes();
        return this;
    }
    public OptimizeFeatureMethods navigateToServersOverview() {
        log.info("Navigating to vSphere Data explorer page");
        optimizePage.navigateToServersOverview();
        return this;
    }

    public OptimizeFeatureMethods verifyFileSystemColumnsOnVMPage(List<String> columnNames) {
        log.info("Verifying file system columns on VM page");
        optimizePage.verifyTableColumnHeadersPresent(columnNames);
        return this;
    }

    public OptimizeFeatureMethods verifyVMTableHasRows() {
        log.info("Verifying All VMs table has rows more than 0");
        optimizePage.verifyVMTableHasRows();
        return this;
    }

    public OptimizeFeatureMethods clickOnFirstVMInTable() {
        log.info("Clicking on first VM to open VM details");
        optimizePage.clickOnFirstVMInTable();
        return this;
    }

    public OptimizeFeatureMethods verifyVMDetailsGraphsPresent(List<String> graphTitles) {
        log.info("Verifying graphs on VM details page");
        optimizePage.verifyVMDetailsGraphsPresent(graphTitles);
        return this;
    }

    public OptimizeFeatureMethods clickChartRelatedInfoLink(String linkTitle) {
        log.info("Clicking chart Related Information link: " + linkTitle);
        optimizePage.clickChartRelatedInfoLink(linkTitle);
        return this;
    }

    public OptimizeFeatureMethods verifyProfileTabSelected(String tabName) {
        log.info("Verifying profile tab selected: " + tabName);
        optimizePage.verifyProfileTabSelected(tabName);
        return this;
    }

    public OptimizeFeatureMethods verifySidebarItemActive(String automationId) {
        log.info("Verifying sidebar item active: " + automationId);
        optimizePage.verifySidebarItemActive(automationId);
        return this;
    }

    public OptimizeFeatureMethods verifyGraphsPresentWithData(List<String> graphTitles) {
        log.info("Verifying graphs present with data on profile page");
        optimizePage.verifyGraphsPresentWithData(graphTitles);
        return this;
    }

	

}
