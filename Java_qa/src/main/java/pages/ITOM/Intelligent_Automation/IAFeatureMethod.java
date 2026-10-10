package pages.ITOM.Intelligent_Automation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.Launcher;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.ITOM.Aiops.AIOpsPage;
import pages.ITOM.Bhom.BhomPage;
import pages.ITOM.Discovery.DiscoveryPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import pages.ITSM.Api.Itsm_Helper;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IAFeatureMethod extends Launcher {

    public static SolqaLogger log = new SolqaLogger(IAFeatureMethod.class.getName());

    IntelligentAutomationPage iaPage;
    PoliciesPage policiesPage;
    HistoryPage historyPage;
    AIOpsPage aiOpsPage;
    CommonWaits commonWaits;
    DiscoveryPage discoveryPage;
    ServiceMonitoringPage smPage;
    BhomPage monitoringPage;
    WebDriver driver;

    public String portal="BMC Helix Portal";
    public String discovery="Helix Discovery";
    public static String IATile="Intelligent Automation";
    public static final String currentDir = System.getProperty("user.dir");

    String baseJSONFilePath = currentDir + File.separator + "resources" + File.separator + "ITOM" + File.separator + "IA";
    String isEventJsonFilePath = baseJSONFilePath + File.separator + "eventprereqjs.json";
    String eventJsonFilePathVariable = baseJSONFilePath + File.separator + "eventTypeJsonVariableTc.json";
    String eventJsonFilePathforEnumSupport = baseJSONFilePath + File.separator + "eventTypeforEnumSupport.json";
    public static String Generic_Connector_Name = "Generic REST Connector";

    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    public static String ISSearchAction="Case - Get Case Details";
    public static String IS_ConnectorName="IScloud";

    public static String ISConnectorName="IS";
    String eventMessage = "alpha";

    SNOWLaunchPage snowLaunchPage;
    public IAFeatureMethod(){
        driver = getDriver();
        commonWaits=new CommonWaits();
        iaPage = new IntelligentAutomationPage();
        policiesPage = new PoliciesPage();
        discoveryPage = new DiscoveryPage();
        monitoringPage = new BhomPage();
        aiOpsPage = new AIOpsPage();
        historyPage = new HistoryPage();
        smPage = new ServiceMonitoringPage(getDriver());
        snowLaunchPage = new SNOWLaunchPage();
    }

    public WebDriver getSNOWDriver(){
        return driver;
    }



    public void verifyLogin() {
        iaPage.verifyIntelligentAutomationPageLaunchedSuccessfully();
    }

    /**
     * Create service in AIOPs for IA.
     */
    public void CreatePlainServiceInAIOps(String AIOpsService,String cluster_CI){

        DiscoveryPage discoveryPage = iaPage.navigateToDiscoveryPage();
        iaPage.switchToWindow(discovery);
        discoveryPage.verifyDiscoveryPageLaunchedSuccessfully();
        discoveryPage.Search_cluster_In_Discovery(cluster_CI);
        discoveryPage.Create_Model_And_Publish(AIOpsService);
    }

    public void create_IA_policy_using_IS_connector(String policyName) throws IOException{
        smPage.create_IA_policy_using_IS_connector(policyName);
    }

    public void create_IA_Automatic_policy_using_IS_connector(String policyName) throws IOException{
        smPage.create_IA_Automatic_policy_using_IS_connector(policyName);
    }
    
    public void configure_IS_connector(String connectorName) throws Exception{
        smPage.configure_IS_connector(connectorName);
    }

    public void update_IA_policy_status_via_api(String policyName, String status) throws IOException{
        smPage.update_IA_policy_status(policyName, status);
    }

    public void delete_IA_policy_via_api(String policyName) throws IOException{
        smPage.delete_IA_policy(policyName);
    }

    /**
     * Method to delete service model created post test execution.
     */
    public void DeleteServiceInAIOps(String AIOpsService){
        iaPage.navigateToDiscoveryPage();
        iaPage.switchToWindow(discovery);
        discoveryPage.delete_service_model(AIOpsService);
        iaPage.closeWindow(discovery);
        iaPage.switchToWindow(portal);
    }

    public void Configure_IA_policy_with_Innovation_Studio_API(String IS_PolicyName) throws Exception
    {
        try
        {
            log.info("Delete Policy ----> " + IS_PolicyName);
            update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
            delete_IA_policy_via_api(IS_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + IS_PolicyName);
        }
        create_IA_policy_using_IS_connector(IS_PolicyName);
        log.info("------- END: Automation Pre-requisite IA poilicy ------------");
    }

    public void Configure_IA_Automatic_policy_with_Innovation_Studio_API(String IS_PolicyName) throws Exception
    {
        try
        {
            log.info("Delete Policy ----> " + IS_PolicyName);
            update_IA_policy_status_via_api(IS_PolicyName,"DRAFT");
            delete_IA_policy_via_api(IS_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + IS_PolicyName);
        }
        create_IA_Automatic_policy_using_IS_connector(IS_PolicyName);
        update_IA_policy_status_via_api(IS_PolicyName,"PUBLISHED");
        log.info("------- END: Automation Pre-requisite IA poilicy ------------");
    }

    public void Configure_IA_policy_with_Innovation_Studio(String IS_PolicyName) throws Exception {
        log.info("------- START: Create Automation Pre-requisite IA poilicy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("Navigate to Connector page");
        iaPage.navigateToConnectorPage();
        log.info("Delete Existing IScloud connector");
        iaPage.deleteConnector(IS_ConnectorName);
        log.info("Configure IScloud connector");
        configureInnovationStudioCloudConnector(IS_ConnectorName);
        log.info("delete existing policy with same name");
        try {
            log.info("Navigate to Policy Page ");
            log.info("Delete Policy ----> " + IS_PolicyName);
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(IS_PolicyName);
            iaPage.deletePolicy(IS_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + IS_PolicyName);
        }
        log.info("Navigate to Policy tab");

        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(IS_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(isEventJsonFilePath);

        log.info("Set Trigger Condition");
        iaPage.setTriggerCondition();

        log.info("Select ISConnector");
        this.iaPage.selectConnector(ISConnectorName);

        log.info("Check Sync Functionality");
        this.iaPage.syncAction();

        log.info("Select Action");
        this.iaPage.selectAction(ISSearchAction);
        log.info("Disable change Management Slider");
        this.iaPage.configureChangeManagementSlider(false);

        log.info("Publish and Save Policy");
        policiesPage.SavePolicy();
        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");

        log.info("------- END: Automation Pre-requisite IA poilicy ------------");
    }

    public void Configure_RESTAPI_policy_with_Transformations(String Rest_Plugin_PolicyName, String Rest_Plugin_connectorName, String transFormerJson) throws Exception {
        String default_Rest_Action = SolQAConstants.DEFAULT_REST_API_ACTION;

        log.info("------- START: Create Rest API IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("delete existing policy with same name");
        try
        {
            log.info("Navigate to Policy Page ");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(Rest_Plugin_PolicyName);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);
            log.info("Delete Policy ----> " + Rest_Plugin_PolicyName);
            iaPage.deletePolicy(Rest_Plugin_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + Rest_Plugin_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(Rest_Plugin_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(isEventJsonFilePath);

        log.info("Set Trigger Condition");
        iaPage.setTriggerCondition();

        log.info("Select Rest API Connector");
        this.iaPage.selectConnector(Rest_Plugin_connectorName);

        log.info("Select Action");
        this.iaPage.selectAction(default_Rest_Action);
        log.info("Disable change Management Slider");
        this.iaPage.configureRESTActionWithTransformations(transFormerJson);
        this.iaPage.configureChangeManagementSlider(false);
        log.info("Publish and Save Policy");
        policiesPage.publishAndSavePolicy();
        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");
        log.info("------- END: REST API IA policy ------------");
    }


    public void Configure_RESTAPI_policy_with_Date_Transformations(String Rest_Plugin_PolicyName, String Rest_Plugin_connectorName, String transFormerJson, int iDays) throws Exception {
        String default_Rest_Action = SolQAConstants.DEFAULT_REST_API_ACTION;

        log.info("------- START: Create Rest API IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("delete existing policy with same name");
        try
        {
            log.info("Navigate to Policy Page ");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(Rest_Plugin_PolicyName);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);
            log.info("Delete Policy ----> " + Rest_Plugin_PolicyName);
            iaPage.deletePolicy(Rest_Plugin_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + Rest_Plugin_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(Rest_Plugin_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(isEventJsonFilePath);

        log.info("Set Trigger Condition");
        iaPage.setTriggerCondition();

        log.info("Select Rest API Connector");
        this.iaPage.selectConnector(Rest_Plugin_connectorName);

        log.info("Select Action");
        this.iaPage.selectAction(default_Rest_Action);

        log.info("Disable change Management Slider");
        this.iaPage.configureRESTActionWithDateTransformations(transFormerJson, iDays);
        this.iaPage.configureChangeManagementSlider(false);

        log.info("Publish and Save Policy");
        policiesPage.publishAndSavePolicy();

        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");
        log.info("------- END: REST API IA policy ------------");
    }


    public void Configure_RESTAPI_policy_with_Array_Transformations(String Rest_Plugin_PolicyName, String Rest_Plugin_connectorName, String eventJson, String transFormerJson, int iIndex) throws Exception {
        String default_Rest_Action = SolQAConstants.DEFAULT_REST_API_ACTION;

        log.info("------- START: Create Rest API IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();

        log.info("delete existing policy with same name");
        try
        {
            log.info("Navigate to Policy Page ");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(Rest_Plugin_PolicyName);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);
            log.info("Delete Policy ----> " + Rest_Plugin_PolicyName);
            iaPage.deletePolicy(Rest_Plugin_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + Rest_Plugin_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(Rest_Plugin_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEventWithJsonString(eventJson);
        log.info("Set Trigger Condition");
        iaPage.setEventDetailsTriggerCondition(eventMessage);

        log.info("Select Rest API Connector");
        this.iaPage.selectConnector(Rest_Plugin_connectorName);

        log.info("Select Action");
        this.iaPage.selectAction(default_Rest_Action);

        log.info("Disable change Management Slider");
        this.iaPage.configureRESTActionWithArrayTransformations(transFormerJson, iIndex);

        this.iaPage.configureChangeManagementSlider(false);

        log.info("Publish and Save Policy");
        policiesPage.SavePolicy();

        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");
        log.info("------- END: REST API IA policy ------------");
    }


    public void Configure_RESTAPI_policy_with_Editor_Enum_Transformations(String Rest_Plugin_PolicyName, String Rest_Plugin_connectorName, String transFormerJson) throws Exception {
        String default_Rest_Action = SolQAConstants.DEFAULT_REST_API_ACTION;

        log.info("------- START: Create Rest API IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("delete existing policy with same name");
        try
        {
            log.info("Navigate to Policy Page ");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(Rest_Plugin_PolicyName);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);
            log.info("Delete Policy ----> " + Rest_Plugin_PolicyName);
            iaPage.deletePolicy(Rest_Plugin_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + Rest_Plugin_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(Rest_Plugin_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(eventJsonFilePathforEnumSupport);

        log.info("Set Trigger Condition");
        iaPage.setSourceHostnameTriggerCondition();

        log.info("Select Rest API Connector");
        this.iaPage.selectConnector(Rest_Plugin_connectorName);

        log.info("Select Action");
        this.iaPage.selectAction(default_Rest_Action);

        log.info("Disable change Management Slider");
        this.iaPage.configureRESTActionWithEditorEnumTransformations(transFormerJson);
        this.iaPage.configureChangeManagementSlider(false);

        log.info("Publish and Save Policy");
        policiesPage.publishAndSavePolicy();

        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");
        log.info("------- END: REST API IA policy ------------");
    }


    public void Configure_RESTAPI_policy_with_Variable_Enum_Transformations(String Rest_Plugin_PolicyName, String Rest_Plugin_connectorName, String transFormerJson, String  variableName) throws Exception {
        String default_Rest_Action = SolQAConstants.DEFAULT_REST_API_ACTION;

        log.info("------- START: Create Rest API IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("delete existing policy with same name");
        try
        {
            log.info("Navigate to Policy Page ");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(Rest_Plugin_PolicyName);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);
            log.info("Delete Policy ----> " + Rest_Plugin_PolicyName);
            iaPage.deletePolicy(Rest_Plugin_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + Rest_Plugin_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(Rest_Plugin_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(eventJsonFilePathforEnumSupport);

        log.info("Set Trigger Condition");
        iaPage.setSourceHostnameTriggerCondition();

        log.info("Add Variable with value");
        iaPage.configureRESTActionWithVariableEnumTransformations(variableName);

        log.info("Select Rest API Connector");
        this.iaPage.selectConnector(Rest_Plugin_connectorName);

        log.info("Select Action");
        this.iaPage.selectAction(default_Rest_Action);
        this.iaPage.configureRestActionsWithoutTransformation(transFormerJson);

        this.iaPage.configureChangeManagementSlider(false);

        log.info("Publish and Save Policy");
        policiesPage.publishAndSavePolicy();

        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");
        log.info("------- END: REST API IA policy ------------");
    }

    public void edit_RESTAPI_policy_with_Transformation_functions(String IS_PolicyName, String sFunctionToBeUpdated) throws Exception {
        log.info("------- START: Edit IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();

        log.info("Navigate to Policy Page ");
        iaPage.navigateToPoliciesPage();

        log.info("Search and Edit Policy.");
        policiesPage.searchPolicy(IS_PolicyName, policiesPage.rowCount);
        policiesPage.editIAPolicy();

        log.info("Edit Transformations for REST API Action");
        this.iaPage.editRESTActionWithTransformations(sFunctionToBeUpdated);

        log.info("Just Save Policy");
        policiesPage.editAndSavePolicy();

        log.info("Policy has been Update successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");

        log.info("------- END: Edit IA policy ------------");
    }


    public void delete_transformations_from_RESTAPI_policy(String IS_PolicyName) throws Exception {
        log.info("------- START: Delete transformations from IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("Navigate to Policy Page ");
        iaPage.navigateToPoliciesPage();

        policiesPage.searchPolicy(IS_PolicyName, policiesPage.rowCount);
        policiesPage.editIAPolicy();

        log.info("Delete Transformations for REST API Action");
        this.iaPage.deleteTransformations();

        log.info("Just Save Policy");
        policiesPage.editAndSavePolicy();

        log.info("Policy has been Update successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");

        log.info("------- END: Delete transformations from IA policy ------------");
    }


    public void VerifyManualPolicyExecutionInHistoryPage(String policyName, String expectedStatus) throws Exception {
        iaPage.navigateToAutomationPage().verifyTitle();
        iaPage.switchToWindow(IATile);
        log.info("Navigate to History Page");
        iaPage.navigateToHistoryPage();
        log.info("Search for Policy in History Page");
        historyPage.searchPolicy(policyName);
        log.info("Verify the Policy Execution Status in History Page");
        historyPage.verifyFirstFilterResult(policiesPage.manualText, expectedStatus);
    }



    public void navigateToValueDashboard(){
        iaPage.navigateToValueDashboard();
    }

    public void validateValueDashboard(){
        iaPage.validateValueDashboard();
    }


    public void openFirstResultAndCheckJsonField(String jsonField){
        historyPage.openFirstResultAndCheckJsonField(jsonField);
    }

    public void configureInnovationStudioCloudConnector(String connectorName) {
        String endPointURL = config.get("CMDBURL");

        iaPage.navigateToConnectorPage();
        iaPage.enterInnovationStudioConnectorDetails(connectorName,endPointURL,config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"));
        Assert.assertEquals(iaPage.getConnectorStatus(connectorName), SolQAConstants.CONNECTED);
    }

    public void wakeUpSNOWFromHibernation(){
        SNOWLaunchPage snowLaunchPage = new SNOWLaunchPage();
        snowLaunchPage.openSNOWPage();
        snowLaunchPage.loginAndWakeUpSNOWInstance(config.get("SNOW_EmailID"), config.get("SNOW_Email_Password"));
        snowLaunchPage.quitSNOWPage();
    }


    public IntelligentAutomationPage enableIncidentNoiseReduction(){
        iaPage.enableIncidentNoiseReduction();
        return iaPage;
    }


    public void createSNOWCMDBIfNotPresent(String connectionName){
        iaPage.navigateToDiscoveryPage();
        iaPage.switchToWindow(discovery);
        discoveryPage.goToDiscoverySettings();
        discoveryPage.createServiceNowCMDBConnection(connectionName,config.get("SNOW_URL"),config.get("SNOW_USERNAME"),config.get("SNOW_PASSWORD"),config.get("SNOW_CLIENT_ID"),config.get("SNOW_SECRET"));
        iaPage.closeWindow(discovery);
        commonWaits.waitForTabToLoadFully(SolQAConstants.BMC_HELIX_PORTAL);
    }


    public boolean IsINREnabled(){
        log.info("Navigate to Solutions page and check if INR is enabled");
        iaPage.navigateToSolutionsPage();
        return iaPage.isINREnabled();
    }

    public void disablePSRSolution(){
        log.info("Navigate to Solutions page and disable PSR solution if enabled");
        iaPage.navigateToSolutionsPage();
        iaPage.disablePSRSolution();
    }

    public void enablePSRSolution(){
        log.info("Navigate to Solutions page and disable PSR solution if enabled");
        iaPage.navigateToSolutionsPage();
        iaPage.enablePSRSolution();
    }

    public void closeIATab(){
        iaPage.closeWindow(IATile);
    }


    public void disableAndDeleteAllEarlierPSRSolutions() throws Exception{
        iaPage.switchToWindow(portal);
        iaPage.navigateToAutomationPage().verifyTitle();
        iaPage.switchToWindow(IATile);
        log.info("Navigate to Solutions page");
        iaPage.navigateToSolutionsPage();
        log.info("disable PSR solution if any");
        iaPage.disablePSRSolution();
        log.info("delete PSR Solution if any");
        iaPage.disablePSRTMFSolution();
        iaPage.deletePSRSolution();
    }

    public void configureSnowInstanceOnPSRTMF(Boolean isTMF,Boolean enableINR){
        iaPage.configureSnowInstanceOnPSRTMF(isTMF,config.get("SNOW_URL"),config.get("SNOW_USERNAME"),config.get("SNOW_PASSWORD"),config.get("SNOW_GrantType"),config.get("SNOW_CLIENT_ID"),config.get("SNOW_SECRET"),"",enableINR);
    }

    public void configureSnowInstanceOnPSRTMFWithCase(Boolean isTMF,Boolean enableINR){
        iaPage.configureSnowInstanceOnPSRTMF(isTMF,config.get("SNOW_URL"),config.get("SNOW_USERNAME"),config.get("SNOW_PASSWORD"),config.get("SNOW_GrantType"),config.get("SNOW_CLIENT_ID"),config.get("SNOW_SECRET"),"Case",enableINR);
    }
    public void switchToPortalFromDiscovery(){
        iaPage.closeWindow(discovery);
        iaPage.switchToWindow(portal);
    }

    public void syncServiceNowCMDB(String SolQA_Snow_Cmdb){
        discoveryPage.goToDiscoverySettings();
        discoveryPage.syncServiceNowCMDB(SolQA_Snow_Cmdb);
    }

    public void runADDMJob(){
        Itsm_Helper itsm_helper = new Itsm_Helper(config.get("CMDBURL"), config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"));
        itsm_helper.runADDMJob();
    }

    public void switchtoIA() throws InterruptedException{
        iaPage.navigateToAutomationPage().verifyTitle();
        iaPage.switchToWindow(IATile);
        commonWaits.waitForPageLoad(driver);
    }

    public HistoryPage checkIAPolicyInHistoryIsSuccessful(String Snow_PolicyName, String expectedMode){
        iaPage.switchToWindow(IATile);
        iaPage.navigateToHistoryPage();
        historyPage.searchPolicy(Snow_PolicyName);
        historyPage.verifyFirstFilterResult(expectedMode, policiesPage.successText);
        return historyPage;
    }

    public HistoryPage checkIAPolicyInHistoryIsWaiting(String PolicyName, String expectedMode){
        iaPage.switchToWindow(IATile);
        iaPage.navigateToHistoryPage();
        historyPage.searchPolicy(PolicyName);
        historyPage.verifyFirstFilterResult(expectedMode, "WAITING");
        return historyPage;
    }

    public String getChangeRequestIDFromHistory(){
        return historyPage.fetchChangeTicketIDFromFirstRow();
    }


    public void switchToBHOM(){
        iaPage.switchToWindow(portal);
        iaPage.navigateToOperationsManagementPage();
    }

    public void openIncidentInSNOWAndVerify(String snowUserName, String snowUserPassword, String uniqueEventMessage,String status,String CI,String serviceName){
        SNOWLaunchPage snowLaunchPage1 = new SNOWLaunchPage();
        try{
        snowLaunchPage1.openSNOWPage();
        snowLaunchPage1.loginToSNOW(snowUserName, snowUserPassword);
        snowLaunchPage1.openListView();
        snowLaunchPage1.searchIncidentThroughMessage(uniqueEventMessage);
        snowLaunchPage1.openIncidentAndVerify(uniqueEventMessage,status,CI,serviceName);
        }finally
        {
        snowLaunchPage1.quitSNOWPage();
        }
    }


    public SNOWLaunchPage openSNOWAndReturnDriver(){
        log.info("******* Login to Snow Instance ***********");
        snowLaunchPage.openSNOWPage();
        snowLaunchPage.loginToSNOW(config.get("SNOW_USERNAME"), config.get("SNOW_PASSWORD"));
        snowLaunchPage.verifyLoginToSNOW();
        return snowLaunchPage;
    }

    public void openCaseInSNOWAndVerify(String exactMessage,String shortDescription){
        log.info("******** Search for case generated for event -> get all details of case ************");
        snowLaunchPage.searchCaseThroughMessage(exactMessage);
        snowLaunchPage.openCaseAndVerify(shortDescription);
    }


    public void connect_GenericRestAPI_Connector() throws Exception {
        log.info("------- START: Create Connector: Generic rest API for Pre-requisite IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("Navigate to Connector page");
        iaPage.navigateToConnectorPage();
        if(!(iaPage.isConnectorPresent(SolQAConstants.REST_API_CONNECTOR_NAME))) {
            iaPage.configureOnPremConnector(Generic_Connector_Name, SolQAConstants.REST_API_CONNECTOR_NAME);
        }
        log.info("------- END: Create Connector: Generic rest API for Pre-requisite IA policy ------------");
    }

    public void configure_IA_policy_with_GenericsRestConnector_UsingHardcodedValueVariable(String iS_PolicyName, String variableName) throws Exception {
        iaPage.navigateToAutomationPage().verifyTitle();
        try
        {
            log.info("Navigate to Policy Page");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(iS_PolicyName);
            log.info("Delete Policy ----> " + iS_PolicyName);
            iaPage.deletePolicy(iS_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + iS_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(iS_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(eventJsonFilePathVariable);

        log.info("Set Trigger Condition");
        iaPage.setTriggerCondition();

        log.info("Add Variable with value");
        iaPage.clickAddVariable();
        iaPage.fillVariable(variableName);
        iaPage.clickVariableArrowBtn(variableName);
        iaPage.expandVariable(variableName);
        iaPage.fillVariableValue("2");
        iaPage.clickSaveVariableButton();

        log.info("Select ISConnector");
        this.iaPage.selectConnector(SolQAConstants.REST_API_CONNECTOR_NAME);

        log.info("Select Action");
        this.iaPage.selectAction(SolQAConstants.DEFAULT_REST_API_ACTION);

        log.info("fill API Path");
        iaPage.FillApiPath(SolQAConstants.IA_API_ENDPOINT);

        log.info("Select Query for default Param and add kay and Value");
        iaPage.selectQueryParam();
        iaPage.fillKey1(SolQAConstants.IA_API_QUERY_PARAM);
        iaPage.fillValue1(SolQAConstants.IA_VARIABLE_CONDITION + variableName);

        log.info("Select header for default Param and add kay and Value");
        iaPage.clickAddKey();
        iaPage.selectHeaderParam();
        iaPage.fillKey2(SolQAConstants.X_API_KEY);
        iaPage.fillValue2(SolQAConstants.REQRES_KEY_VALUE);

        log.info("Save the configurations");
        iaPage.clickDoneButton();

        log.info("Disable change Management Slider");
        this.iaPage.configureChangeManagementSlider(false);

        log.info("Test Policy");
        policiesPage.testPolicyAndVerifyDetails();
        policiesPage.SavePolicy();
        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        log.info("------- END: Automation Pre-requisite IA policy ------------");
    }

    public String configure_IA_policy_with_GenericsRestConnector(String iS_PolicyName, String eventMessage, String variableName) throws Exception {
        iaPage.navigateToAutomationPage().verifyTitle();
        try {
            log.info("Navigate to Policy Page ");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(iS_PolicyName);
            log.info("Delete Policy ----> " + iS_PolicyName);
            iaPage.deletePolicy(iS_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + iS_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(iS_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(eventJsonFilePathVariable);

        log.info("Set Trigger Condition");
        iaPage.setTriggerConditionForMessage(eventMessage);

        log.info("Add Variable with value");
        iaPage.clickAddVariable();
        iaPage.fillVariable(variableName);
        iaPage.clickVariableArrowBtn(variableName);
        iaPage.expandVariable(variableName);
        iaPage.fillVariableValueBySelectingDropdownValue(SolQAConstants.IA_VARIABLE_VALUE);
        iaPage.clickSaveButton();

        log.info("Select ISConnector");
        this.iaPage.selectConnector(SolQAConstants.REST_API_CONNECTOR_NAME);

        log.info("Select Action");
        this.iaPage.selectAction(SolQAConstants.DEFAULT_REST_API_ACTION);

        log.info("fill API Path");
        iaPage.FillApiPath(SolQAConstants.IA_API_ENDPOINT);

        log.info("Select Query for default Param and add kay and Value");
        iaPage.selectQueryParam();
        iaPage.fillKey1(SolQAConstants.IA_API_QUERY_PARAM);
        iaPage.fillValue1(SolQAConstants.IA_VARIABLE_CONDITION + variableName);

        log.info("Select header for default Param and add kay and Value");
        iaPage.clickAddKey();
        iaPage.selectHeaderParam();
        iaPage.fillKey2(SolQAConstants.X_API_KEY);
        iaPage.fillValue2(SolQAConstants.REQRES_KEY_VALUE);

        log.info("Save the configurations");
        iaPage.clickDoneButton();

        log.info("Disable change Management Slider");
        this.iaPage.configureChangeManagementSlider(false);

        log.info("Save and publish Policy");
        policiesPage.SavePolicy();
        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        return PoliciesPage.finalpolicyName;
    }

    public void verifyPolicyExecution(String PolicyID, String ManualOrAuto, String SuccessOrFailure){
        commonWaits.switchToTabByTitle(SolQAConstants.IA);
        iaPage.navigateToHistoryPage();
        historyPage.verifyfilterResultForID(PolicyID, ManualOrAuto, SuccessOrFailure);
    }

    public void delete_IA_Policy(String IS_PolicyName) throws Exception{
        log.info("Move Policy to Draft status");
        iaPage.navigateToAutomationPage();
        iaPage.navigateToPoliciesPage();
        try {
            policiesPage.searchPolicy(IS_PolicyName, policiesPage.rowCount);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);

            log.info("Delete Policy in IA");
            iaPage.switchToWindow(IATile);
            iaPage.navigateToPoliciesPage();
            policiesPage.searchPolicy(IS_PolicyName, policiesPage.rowCount);
            iaPage.deletePolicy(IS_PolicyName);
        }catch(Exception e){
            log.info(" There is no policy to delete with name " + IS_PolicyName);
        }
    }

    public void deletePolicy(String PolicyName){
        log.info("Move Policy to Draft status");
        commonWaits.switchToTabByTitle(SolQAConstants.IA);
        iaPage.navigateToPoliciesPage();
        policiesPage.searchPolicy(PolicyName, policiesPage.rowCount);
        policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);

        log.info("Delete Policy in IA");
        commonWaits.switchToTabByTitle(SolQAConstants.IA);
        iaPage.navigateToPoliciesPage();
        policiesPage.searchPolicy(PolicyName, policiesPage.rowCount);
        iaPage.deletePolicy(PolicyName);
    }

    public void configureGenRestwithAPIKeysTSOCloudConnector(String connectorName) {
        String TSOendPointURL = config.get("TSO_ENDPOINT_URL");
        String TSOloginURL = config.get("TSO_LOGIN_URL");
        String Authentication_Type = config.get("Authentication_Type");
        String Key_Name = config.get("Key_Name");
        String API_Key = config.get("API_Key");
        String Location = config.get("Location");
        String Content_Type = config.get("Content_Type");
        String TSO_Username = config.get("TSO_username");
        String TSO_Password = config.get("TSO_password");
        String usernamekey = "username";
        String passwordkey = "password";
        iaPage.navigateToConnectorPage();
        iaPage.enterGenRestwithAPIKeysTSOCloudConnectorDetails(connectorName,TSOendPointURL,TSOloginURL,Key_Name,API_Key,Content_Type,usernamekey,passwordkey,TSO_Username,TSO_Password);
        Assert.assertEquals(iaPage.getConnectorStatus(connectorName), SolQAConstants.CONNECTED);
    }
    public void Configure_GenRestwithAPIKeys_TSO_Cloud_Connector(String Rest_Plugin_connectorName) throws Exception{

        iaPage.navigateToAutomationPage().verifyTitle();
        iaPage.switchToWindow(IATile);
        log.info("Navigate to Connector page");
        iaPage.navigateToConnectorPage();
        log.info("Delete Existing GenRestwithAPIKeysTSO connector");
        iaPage.deleteConnector(Rest_Plugin_connectorName);
        log.info("Configure GenRestwithAPIKeysTSO connector");
        configureGenRestwithAPIKeysTSOCloudConnector(Rest_Plugin_connectorName);

    }



    public void Configure_GENREST_APIKey_Cloud_TSO_policy(String Rest_Plugin_PolicyName, String Rest_Plugin_connectorName) throws Exception {
        String default_Rest_Action = SolQAConstants.DEFAULT_REST_API_ACTION;

        log.info("------- START: Create Rest API IA policy ------------");
        iaPage.navigateToAutomationPage().verifyTitle();
        log.info("delete existing policy with same name");
        try
        {
            log.info("Navigate to Policy Page ");
            iaPage.navigateToPoliciesPage();
            iaPage.searchPolicy(Rest_Plugin_PolicyName);
            policiesPage.actionDropDownOperation(policiesPage.unpublish, policiesPage.policyStatusLoc, policiesPage.draftText);
            log.info("Delete Policy ----> " + Rest_Plugin_PolicyName);
            iaPage.deletePolicy(Rest_Plugin_PolicyName);
        }catch (Exception e) {
            log.info(" There is no policy to delete with name " + Rest_Plugin_PolicyName);
        }
        log.info("Navigate to Policy tab");
        iaPage.navigateToPoliciesPage();

        log.info("Navigate to Create Automation Policy page");
        policiesPage.navigateToCreatePoliciesPage();

        log.info("Set Policy Name and Policy Description");
        policiesPage.setPolicyNameAndDescription(Rest_Plugin_PolicyName);

        log.info("Set Manual Execution Mode");
        this.iaPage.setExecutionMode(true);

        log.info("Set Event json");
        iaPage.setUserDefinedTagEvent(isEventJsonFilePath);

        log.info("Set Trigger Condition");
        iaPage.setTriggerCondition();

        log.info("Select Rest API Connector");
        this.iaPage.selectConnector(Rest_Plugin_connectorName);

        log.info("Select Action");
        this.iaPage.selectAction(default_Rest_Action);

        log.info("fill API Path");
        iaPage.FillApiPath(SolQAConstants.IA_GENREST_API_TSO_ENDPOINT);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        log.info("Save the configurations");
        iaPage.clickDoneButton();

        log.info("Disable change Management Slider");
        this.iaPage.configureChangeManagementSlider(false);
        log.info("Publish and Save Policy");
        policiesPage.SavePolicy();
        log.info("Policy has been created successfully with name "+ PoliciesPage.finalpolicyName);
        iaPage.closeWindow(IATile);
        iaPage.switchToWindow("BMC Helix Portal");
        log.info("------- END: REST API IA policy ------------");
    }

    public String getHPDCI(String jsonPayload) {
        log.info("Extracting HPD_CI value from JSON payload");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonPayload);

            // Navigate: entries → [0] → values → HPD_CI
            JsonNode hpdCiNode = root.path("entries")
                    .get(0)
                    .path("values")
                    .path(SolQAConstants.HPD_CI);

            return hpdCiNode.isMissingNode() ? null : hpdCiNode.asText(null);

        } catch (Exception e) {
            log.error("Error parsing JSON payload: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String fetchIncidentStatusValue(String jsonPayload) {
        log.info("Extracting Status value from JSON payload");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonPayload);

            // Navigate: entries → [0] → values → HPD_CI
            JsonNode hpdCiNode = root.path("entries")
                    .get(0)
                    .path("values")
                    .path("Status");

            return hpdCiNode.isMissingNode() ? null : hpdCiNode.asText(null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getIncidentStatus(String IncidentID){
        log.info("Getting status for Incident ID: " + IncidentID);
        Itsm_Helper itsm_helper = new Itsm_Helper(config.get("CMDBURL"), config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"));
        String incidentDetails = itsm_helper.getIncidentInHelpDeskForm(IncidentID);
        String sIncident = fetchIncidentStatusValue(incidentDetails);
        Assert.assertNotNull(sIncident, "No value for status found in paylaod for incident ID: " + IncidentID);
        return sIncident;
    }

    public void VerifyCINameInIncident(String sCMDBUrl, String sUserName, String sPassword, String IncidentID){
        log.info("Verifying CI Details in Incident: " + IncidentID);
        Itsm_Helper itsm_helper = new Itsm_Helper(sCMDBUrl, sUserName, sPassword);
        String incidentDetails = itsm_helper.getIncidentInHelpDeskForm(IncidentID);
        String sCIDetails = getHPDCI(incidentDetails);
        Assert.assertNotNull(sCIDetails, "CI Details not found in Incident: " + IncidentID);
    }

    public List<String> getComputerSystemNames(String jsonPayload) {
        List<String> names = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonPayload);

            for (JsonNode entry : root.path("entries")) {
                JsonNode values = entry.path("values");

                if ("BMC_COMPUTERSYSTEM".equals(values.path("ClassId").asText())) {
                    String name = values.path("Name").asText(null);
                    if (name != null && !name.isEmpty()) {
                        names.add(name);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }

        return names;
    }

    public List<String> getCIWhichIsSyncedInCMDB(String sCMDBUrl, String sUserName, String sPassword, String searchString){
        Itsm_Helper itsm_helper = new Itsm_Helper(sCMDBUrl, sUserName, sPassword);
        String ciDetails = itsm_helper.getListofCIsSyncedInCMDB(searchString);
        List<String> ciNames = getComputerSystemNames(ciDetails);
        return ciNames;
    }

    public boolean IsThisServiceSyncedInCMDB(String sCMDBUrl, String sUserName, String sPassword, String searchString) throws IOException{
        Itsm_Helper itsm_helper = new Itsm_Helper(sCMDBUrl, sUserName, sPassword);
        String serviceDetails = itsm_helper.getListofServicesSyncedInCMDB(searchString);
        System.out.println("Service Details JSON: " + serviceDetails);
        String actualName = "";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(serviceDetails);

        JsonNode entriesNode = rootNode.path("entries");
        Assert.assertTrue(entriesNode.isArray() && entriesNode.size() > 0,
                "Entries array is empty or missing");

        JsonNode nameNode =
                entriesNode.get(0)
                        .path("values")
                        .path("Name");

        Assert.assertFalse(nameNode.isMissingNode(),
                "'Name' field is missing in payload");
        if (nameNode.asText().isEmpty()) {
            log.error("'Name' field is empty in payload");
            return false;
        }else {
            actualName = nameNode.asText();
        }
        if (actualName.contains(searchString)){
            log.info("Service found in CMDB with name: " + actualName);
            return true;
        } else {
            log.info("Service not found in CMDB with name: " + searchString);
        }
        return false;

    }

    public IntelligentAutomationPage navigateToIA() throws Exception{
        iaPage.navigateToAutomationPage().verifyTitle();
        return iaPage;
    }

    public IntelligentAutomationPage switchToPortal(){
        iaPage.switchToWindow(portal);
        return iaPage;
    }

    public IAFeatureMethod switchToWindow(String windowName) {
        iaPage.switchToWindow(windowName);
        return this;
    }

    public IAFeatureMethod refreshBrowser(){
        iaPage.refreshBrowser();
        return this;
    }

    public IAFeatureMethod navigateToHistoryPage() {
        iaPage.navigateToHistoryPage();
        return this;
    }

    public IAFeatureMethod searchPolicy(String finalPolicyName) {
        historyPage.searchPolicy(finalPolicyName);
        return this;
    }

    public IAFeatureMethod verifyFirstFilterResult(String executionModeText, String statusText){
        historyPage.verifyFirstFilterResult(executionModeText, statusText);
        return this;
    }

    // Wrapper methods for connector and sync operations
    public void navigateToConnectorPage() {
        iaPage.navigateToConnectorPage();
    }
    
    public boolean isConnectorPresentByNameContains(String connectorName) {
        return iaPage.isConnectorPresentByNameContains(connectorName);
    }
    
    public IAFeatureMethod clickEditConnector(String connectorName) {
        iaPage.clickEditConnector(connectorName);
        return this;
    }
    
    public IAFeatureMethod clickViewActions() {
        iaPage.clickViewActions();
        return this;
    }
    
    public IAFeatureMethod setSearchOnlineToggle(boolean enable) {
        iaPage.setSearchOnlineToggle(enable);
        return this;
    }
    
    public IAFeatureMethod clickSyncActionsLink() {
        iaPage.clickSyncActionsLink();
        return this;
    }
    
    public boolean isSyncSuccessfull() {
        return iaPage.isSyncSuccessfull();
    }

}