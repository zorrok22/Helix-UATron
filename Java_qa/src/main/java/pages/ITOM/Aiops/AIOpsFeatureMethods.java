package pages.ITOM.Aiops;

import com.fasterxml.jackson.databind.JsonNode;
import framework.BasePage;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.Discovery;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import pages.ITOM.Bhom.BhomPage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIOpsFeatureMethods extends BasePage {
    public static SolqaLogger log = new SolqaLogger(AIOpsFeatureMethods.class.getName());
    AdePortalLandingPage adePortalLandingPagePage;
    AiopsServicePage aiopsServicePage;
    ServiceMonitoringPage serviceMonitoringPage;
    Discovery discovery;
    AIOpsPage aiOpsPage;
    CommonWaits commonWaits;
    WebDriver driver;
    Events_API eventsAPI;
    HMEvents hmEvents;
    SituationsPage situationsPage;

    IntelligentAutomationPage iaPage;
    ServiceMonitoringPage smPage;

    public  AIOpsFeatureMethods() {
        this.driver = getDriver();
        PageFactory.initElements(this.driver,this);
        adePortalLandingPagePage = new AdePortalLandingPage(this.driver);
        aiopsServicePage = new AiopsServicePage();
        aiOpsPage = new AIOpsPage();
        serviceMonitoringPage = new ServiceMonitoringPage(this.driver);
        discovery = new Discovery();
        commonWaits = new CommonWaits();
        eventsAPI = new Events_API();
        iaPage = new IntelligentAutomationPage();
        smPage = new ServiceMonitoringPage(getDriver());

        hmEvents = new HMEvents();
        situationsPage = new SituationsPage();
    }

    public void verifyLoginToADEPlatform() {
        adePortalLandingPagePage.verifyLoginToADEPlatform();
    }

    public AIOpsPage navigateToAIOps() {
        adePortalLandingPagePage.navigateToAIOPS();
        return new AIOpsPage();
    }

    public AIOpsPage switchToSmartIt(){
        adePortalLandingPagePage.switchToSmartIt();
        return new AIOpsPage();
    }

 public void navigateToAiopsApplicationAndClickOnServicesTab() {
        log.info("Navigate to AIOps page");
        AIOpsPage aiOpsPage = navigateToAIOps();
        log.info("Enable IA permission");
        aiOpsPage.configureIAPermission();
        log.info("Navigate to Services tab");
        aiOpsPage.navigateToEntitles();
    }

    public void navigateToServicesTab() {
        log.info("Navigate to Services tab");
        aiOpsPage.navigateToEntitles();
    }

    public void verifyServiceIsCreated(String serviceName) {
        aiOpsPage.verifyService(serviceName);
    }

    public void createServiceUsingUI(String serviceName, String ciName) {
        aiOpsPage.createServiceUsingUI(serviceName, ciName);
    }

    public void verifyEventOptionsForFirstEvent() {
        aiOpsPage.verifyEventOptions();
    }

    public void navigateToSituationPageAndVerifySituationOption (String situation) {
        log.info("Navigate to Entitles tab");
        SituationsPage situationsPage = aiOpsPage.navigateToSituationsPage();

        situationsPage.verifySituationsPageOpened();
        log.info("On Situation Page open Situation " +  situation + "- verify all situation Options");
        situationsPage.verifySituationOptions();

        situationsPage.searchAndOpenSituation(situation);
        log.info("On Edit Situation Page - verify all event Options");
        situationsPage.verifyEventOptionsOnEditSituation();
    }

    public void navigateToSituationPage() {
        log.info("Navigate to Situations tab");
        SituationsPage situationsPage = aiOpsPage.navigateToSituationsPage();
        situationsPage.verifySituationsPageOpened();
    }

    public void apply30DaysFilter() {
        log.info("Applying 30 days filter" );
        aiOpsPage.showLast30DaysData();
    }

    public void openSimilarSituation() {
        log.info("Open Similar situation option");
        aiOpsPage.openSimilarSituation();
    }

    public void verifyHelixGPTMessage(String context, String expected) {
        log.info("Verify Similar situation is opened");
        aiOpsPage.verifyHelixGPTMessage(context,expected);
    }

    public void verifyChangeBAP(String expectedRotCauseSubText, String expectedChangeToolText) {
        situationsPage.verifyChangeBAP(expectedRotCauseSubText, expectedChangeToolText);
    }

    public void verifyBAPWithNoRecommendations(String expectedRecommendationSubSTring){
        situationsPage.verifyBAPWithRecommendations(expectedRecommendationSubSTring);
    }

    public AIOpsFeatureMethods switchToListViewAndSearchSituation(String situationName) {
        situationsPage.switchToListViewAndSearchSituation(situationName);
        return this;
    }

    public AIOpsFeatureMethods searchSituationOnPage(String situationName) {
        situationsPage.searchSituationOnPage(situationName);
        return this;
    }

    public AIOpsFeatureMethods verifyDeepRCAColumnOnSituationsPage() {
        situationsPage.verifyDeepRCAColumnPresent();
        return this;
    }

    public String getDeepRCAStatusForSituation(String situationId) {
        return situationsPage.getDeepRCAStatusForSituation(situationId);
    }

    public AIOpsFeatureMethods openSituationFromHierarchyView(String situationName) {
        situationsPage.openSituationFromHierarchyView(situationName);
        return this;
    }

    public AIOpsFeatureMethods verifyDeepRCATriggeredAutomatically() {
        situationsPage.verifyDeepRCATriggeredAutomatically();
        return this;
    }

    public AIOpsFeatureMethods verifyDeepRCAProgressStatusFlow(String[] expectedStatuses) {
        situationsPage.verifyDeepRCAProgressStatusFlow(expectedStatuses);
        return this;
    }

    public AIOpsFeatureMethods waitForDeepRCAStatusForSituation(String situationId, String... expectedStatuses) {
        situationsPage.waitForDeepRCAStatusForSituation(situationId, expectedStatuses);
        return this;
    }

    public AIOpsFeatureMethods verifyDeepRCACompletedInPanel(String expectedStatusText) {
        situationsPage.verifyDeepRCACompletedInPanel(expectedStatusText);
        return this;
    }

    public AIOpsFeatureMethods verifyDeepRCAStatusShownForOldSituations(String currentSituationId) {
        situationsPage.verifyDeepRCAStatusShownForOldSituations(currentSituationId);
        return this;
    }

    public static List<String> extractResourceIds(String response) {
        List<String> ids = new java.util.ArrayList<>();
        try {
            JSONObject json = new JSONObject(response);
            for (String key : json.keySet()) {
                JSONObject inner = json.getJSONObject(key);
                if (inner.has("resourceId")) {
                    JSONArray resourceIds = inner.getJSONArray("resourceId");
                    for (int i = 0; i < resourceIds.length(); i++) {
                        ids.add(resourceIds.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            log.info("Could not extract resourceIds from response: " + e.getMessage());
        }
        return ids;
    }

    public void searchAndOpenSituation(String situation) {
        situationsPage.searchAndOpenSituation(situation);
    }

    public void searchSituationAndOpenIncidentAssociated(String situation, String incidentId) {
        situationsPage.searchSituationAndOpenIncidentAssociated(situation, incidentId);
    }

    public void verifyMultiServiceSituation(List <String> serviceNames, List <String> eventMessages) {
        situationsPage.verifyMultiServiceSituation(serviceNames, eventMessages);
    }

    public void openFirstSimilarSituationWithIncident() {
        situationsPage.openFirstSimilarSituationWithIncident();
    }

    public void verifyHelixGptMenus() {
        situationsPage.verifyHelixGptMenus();
    }

    public void verify5Whys() throws InterruptedException {
        situationsPage.verify5Whys();
    }

    public void captureScreenshot(String ssName, String app){
        try {
            commonWaits.captureScreenshot(ssName, app);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void verifyServiceImpactedAndHealthScoreUpdated(String serviceName){
        aiOpsPage.verifyServiceImpactedAndHealthScoreUpdated(serviceName);
    }

    public int getTotalEventsCount() {
        return aiOpsPage.getTotalEventsCount();
    }

    public void openEventDetailsAndCrossLaunchEventInBHOMApplication(){
        log.info("Open event and check all required details");
        aiOpsPage.clickOnEventFirstEvent();
        String eventName = aiOpsPage.verifyEventEditDetails();

        log.info("open event in BHOM page and checked all required details");
        BhomPage bhomPage = aiOpsPage.clickOnMoreDetailsLink();
        bhomPage.verifyEventEditDetails(eventName);
        bhomPage.verifyEventTabNames();
    }

    public String crossLaunchImpactingEvent(){
        aiOpsPage.openImpactedEvents();
        String eventName = aiOpsPage.openFirstEventFromEventsPanel();
        aiOpsPage.crossLaunchEventFromEventDetailsPanel();
        return eventName;
    }

    public void runAutomationPolicyAndCheckStatus(String is_PolicyName) throws IOException {
        aiOpsPage.selectMessageForRunAutomation(is_PolicyName);
        log.info("Verify that Policy status is successful in AIOps");
        aiOpsPage.refreshBrowserOnEditServicePage();
        Assert.assertEquals(aiOpsPage.getAutomationPolicyStatus(is_PolicyName), "successful");
        commonWaits.captureScreenshot("DRSQA_12379_AutomationPolicyExecutionStatus", SolQAConstants.appITOM);
    }

    public void openService(String serviceName) {
        log.info("Opening service: " + serviceName);
        aiOpsPage.openService(serviceName);
    }

    public void createGenNextSvcWithMetric(String serviceName, String paAgent){
        iaPage.navigateToMonitoringPage();
        iaPage.switchToWindow(SolQAConstants.AIOpsTitle);
        log.info("Navigate to Entitles tab");
        aiOpsPage.navigateToEntitles();
        aiOpsPage.createGenNextSvcWithCI(serviceName, paAgent);
    }

    public void openAiOps(){
        iaPage.navigateToMonitoringPage();
        iaPage.switchToWindow(SolQAConstants.AIOpsTitle);
        log.info("Navigate to Entitles tab");
        aiOpsPage.navigateToEntitles();
    }


    public int openServiceAndcheckHealthScore(String serviceName){
        aiOpsPage.navigateToEntitles();
        aiOpsPage.openService(serviceName);
        aiOpsPage.waitForEditOptionToLoad();
        return aiOpsPage.getServiceHealthScore(serviceName);
    }

    public void AddMemoryHealthIndicator(String svcName){
        aiOpsPage.navigateToEntitles();
        aiOpsPage.openService(svcName);
        aiOpsPage.AddMemoryMetricToGenNextSvc(svcName);
    }

    public void removeMemoryHealthIndicator(String svcName){
        aiOpsPage.navigateToEntitles();
        aiOpsPage.openService(svcName);
        aiOpsPage.waitForEditOptionToLoad();
        aiOpsPage.removeMetric("Memory");
    }





    public String create_alarm_policy_for_pa(String CPUAlarmJsonPath, String ALARM_POLICY_CPU, String PA_AGENT){
        return smPage.create_alarm_policy_for_pa(CPUAlarmJsonPath, ALARM_POLICY_CPU, PA_AGENT);
    }

    public String create_alarm_policy_from_json(String jsonPath, String policyName) throws IOException {
        return smPage.create_alarm_policy_from_json(jsonPath, policyName);
    }

    public void delete_alarm_policy(String policyID){
        smPage.delete_alarm_policy(policyID);
    }

    public int getHealthScore() {
        return aiOpsPage.getHealthScore();
    }

    public void selectTopologyTabOnserviceDetailsPage(String topologyTab){
        aiopsServicePage.aiopsSelectTabOnServiceDetailsPage(topologyTab, true);
    }

    public void verifyAiopsCiTopologyVerifyDevLicenses(){
        aiopsServicePage.aiopsCiTopologyVerifyDevLicenses();
    }

    public void createServiceFromAIOPS(String serviceName, String cisFilename, String blockToProcess, boolean includeRelated, String randomStr) throws Exception {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        aiOpsPage.clickOnNewService();
        aiOpsPage.clickOnNewBusinessService(serviceName);
        aiOpsPage.aiopsDefineserviceAddStaticContentByInputfile(cisFilename, blockToProcess, includeRelated, randomStr);
    }

    public void setHealthIndicatorToServiceAndAddAlarm(String serviceName, String metricType, String metricName, String metricNameSearch, String ciName, String alarmName, String threshold, String duration){
        aiOpsPage.addHealthIndicator();
        aiOpsPage.aiopsNewServiceClickServiceNodeByName(serviceName);
        aiOpsPage.selectMetricScope(SolQAConstants.metricScope);
        aiOpsPage.aiopsDefineHealthIndicatorAddMetric(metricType, metricNameSearch, ciName, metricName);
        aiOpsPage.addAlarmPolicy();
        aiOpsPage.fillPolicyName(alarmName + RandomStringUtils.randomAlphanumeric(5));
        aiOpsPage.setAlarmGenerationConditions(threshold,duration);
        new CreateServicePage().blueprintClickSavencloseService();
        new CreateServicePage().aiopsDefineserviceClickSavencloseService();
    }

    public String VerifyAiopsCiTopologyNodePresentByKindBeforeAnyEventSentToService(String kind, String node_name, Map<Object, String> aiopsTestData){
        selectTopologyTabOnserviceDetailsPage(aiopsTestData.get("Topology"));
        verifyAiopsCiTopologyVerifyDevLicenses();
        aiopsServicePage.aiopsCiTopologyNodePresentByKind(kind, true);
        aiopsServicePage.aiopsCiTopologyClickHierarchicAction(aiopsTestData.get("Hierarchic"), aiopsTestData.get("LeftToRight"));
        aiopsServicePage.aiopsCITopologyClickNodeExpandCollapse(kind, true);
        aiopsServicePage.aiopsCiTopologyResizeView(aiopsTestData.get("maximize"));
        aiopsServicePage.aiopsCiTopologyCiTopologyBtn(aiopsTestData.get("zoomfit"));
        aiopsServicePage.aiopsCITopologyClickOnNodeByKind(node_name);
        aiopsServicePage.getNodesSelectedTabEventsCount(aiopsTestData.get("Events"));
        Map<String, String> node_details_conf = aiopsServicePage.aiopsPcaGetConfigurationFromNodeDetails();
        String ciName = node_details_conf.get(aiopsTestData.get("Host"));
        aiopsServicePage.aiopsPcaCloseNodeDetailsPage();
        aiopsServicePage.aiopsCiTopologyResizeView(aiopsTestData.get("restore"));
        return ciName;
    }

    public void VerifyAiopsCiTopologyNodePresentByKindAfterEventSentToService(String kind, String node_name, String topology_tab, int noOfEvents, Map<Object, String> aiopsTestData){
        selectTopologyTabOnserviceDetailsPage(aiopsTestData.get("Topology"));
        int count = aiopsServicePage.aiopsCiTopologyGetCausalNodeCountByKind(kind, true);
        selectTopologyTabOnserviceDetailsPage(topology_tab);
        Assert.assertTrue(count == 1, "Event count is not matching");
        aiopsServicePage.aiopsCITopologyClickNodeExpandCollapse(kind, true);
        aiopsServicePage.aiopsCiTopologyResizeView(aiopsTestData.get("maximize"));
        aiopsServicePage.aiopsCiTopologyCiTopologyBtn(aiopsTestData.get("zoomfit"));
        aiopsServicePage.aiopsCITopologyClickOnNodeByKind(node_name);
        int event_count_new = aiopsServicePage.getNodesSelectedTabEventsCount(aiopsTestData.get("Events"));
        Assert.assertTrue(noOfEvents == event_count_new, "after zoom , On Edit Node Event page event count is not matching");
        aiopsServicePage.aiopsPcaCloseNodeDetailsPage();
        aiopsServicePage.aiopsCiTopologyResizeView(aiopsTestData.get("restore"));
        selectTopologyTabOnserviceDetailsPage(topology_tab);
        aiopsServicePage.aiopsCiTopologyResizeView(aiopsTestData.get("maximize"));
        aiopsServicePage.aiopsCiTopologyCiTopologyBtn(aiopsTestData.get("zoomfit"));
        aiopsServicePage.aiopsCiTopologyResizeView(aiopsTestData.get("restore"));
    }

    public int sendEventForTopology(String node_name) throws Exception {
        log.info("-----Send Event to the node-----");
        String event_msg =  RandomStringUtils.randomAlphanumeric(5) + SolQAConstants.eventName;
        int noOfEvents = 3;
        String token = serviceMonitoringPage.getJwtToken();
        hmEvents.streamRestApiEvents(SolQAConstants.evClass, noOfEvents, token, node_name, event_msg, SolQAConstants.severityCritical, SolQAConstants.location, "");
        return noOfEvents;
    }

    public boolean aiopsCiTopologyNodePresentByKindAndType(String kindAndType) {
        log.info("Verifying topology aggregation button is active (CIs grouped by kind and type)");
        boolean isActive = aiopsServicePage.aiopsCiTopologyNodePresentByKindAndType(kindAndType, true);
        if (isActive) {
            log.info("Topology aggregation is active - CIs are grouped by kind and type");
        } else {
            log.info("Topology aggregation is NOT active");
        }
        return isActive;
    }

    public String getServiceIdByNameAPI(String serviceName) {
        String token = serviceMonitoringPage.getJwtToken();
        String serviceId = discovery.fetchModelKey(serviceName, token);
        return serviceId;
    }

    public String deleteService(String serviceName) {
        String token = serviceMonitoringPage.getJwtToken();
        String serviceKey = discovery.fetchModelKey(serviceName, token);
        String responseCode = discovery.deleteService(serviceKey, token);
        return responseCode;
    }

    public void closeEvent(String node_name, String note, String accessKey, String secretKey) throws Exception {
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        Map<String, String> filters = new HashMap<>();
        filters.put("class", SolQAConstants.evClass);
        filters.put("source_hostname", node_name);
        List<String> event_ids = hmEvents.getEventIdentifierApi(filters, helixJwtToken);
        for(String item: event_ids) {
            hmEvents.closeEventApi(item, note, helixJwtToken);
        }
    }

    public void aiopsServiceSaveAndCloseButton() throws Exception {
        log.info("----click on save and close button--------------");
        CreateServicePage aiOpsCIService = new CreateServicePage();
        aiOpsCIService.aiopsBlueprintClickSaveBtn();
        aiOpsCIService.aiopsBlueprintClickCloseBtn();
    }


    public JsonNode createBlankServiceFromGivenBlueprint(String serviceName, String bluePrintName, String value, String accessKey, String secretKey){
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        JsonNode response = new Aiops().createServiceFromGivenBlueprint(serviceName, bluePrintName, value, helixJwtToken);
        return response;
    }


    public void enableBlueprint(String bluePrintName, String accessKey, String secretKey){
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        new Aiops().enableBlueprint(bluePrintName, helixJwtToken);
    }

    public String createBlueprint(String blueprintName, String blueprintDefFile, String accessKey, String secretKey){
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        String blueprintId = new Aiops().aiopsCreateK8sBlueprintApi(blueprintName, blueprintDefFile, helixJwtToken);
        return blueprintId;
    }


    public String[] getBlueprintID(String blueprintName, String accessKey, String secretKey){
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        String[] blueprintId = new Aiops().getBlueprintID(blueprintName,helixJwtToken);
        return blueprintId;
    }
    public void navigateToConfigurations(){
        log.info("----click on Configuration Tab--------------");
    aiOpsPage.navigateToConfigurations();
}



    public String exportBlueprintsFromAiops(String blueprintName, String downloadDir){
        aiOpsPage.navigateToConfigurations();
        AiopsServiceBlueprintPage blueprintPage = aiOpsPage.navigateToConfigurationsManageServiceBlueprints();
        blueprintPage.aiopsConfigSearchBlueprintByColumnAndValue(SolQAConstants.colName, blueprintName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        blueprintPage.aiopsConfigBlueprintAction(blueprintName, SolQAConstants.export);
        String jsonFileName = blueprintName + SolQAConstants.blueprintVersion;
        String filePath = downloadDir + File.separator + jsonFileName;
        return filePath;
    }

    public boolean deleteBlueprint(String blueprintId, String accessKey, String secretKey){
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        boolean status = new Aiops().aiopsDeleteBlueprintByIdApi(blueprintId, helixJwtToken);
        return status;
    }


    public AIOpsPage activate_ml_situations_flag(){
        iaPage.switchToWindow(SolQAConstants.AIOpsTitle);
        aiOpsPage.enableMLBasedSituationsFlag();
        return aiOpsPage;
    }

    public AIOpsPage deactivate_ml_situations_flag(){
        iaPage.switchToWindow(SolQAConstants.AIOpsTitle);
        aiOpsPage.disableMLBasedSituationsFlag();
        return aiOpsPage;
    }

    public void verifyBlueprintImportedSuccessfully(String blueprintName) {
        AiopsServiceBlueprintPage blueprintPage = new AiopsServiceBlueprintPage();
        blueprintPage.aiopsConfigClickShowHideBlueprint();
        blueprintPage.aiopsConfigSearchBlueprintByColumnAndValue(SolQAConstants.colName, blueprintName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
        String name = blueprintPage.aiopsConfigBlueprintAction(blueprintName, SolQAConstants.enableOption);
        Assert.assertEquals(name, blueprintName, "Import Blueprint have some issue, import failed.");
    }

    public void createServiceFromAPI(String serviceName, String ciName, String accessKey, String secretKey) {
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        Aiops aiops = new Aiops();
        aiops.createBlankService(serviceName, helixJwtToken);
        String serviceId = getServiceIdByNameAPI(serviceName);
        Map<String, Object> ciId = new Discovery().fetchCiIdForLatestImport(ciName, SolQAConstants.VCENTER, helixJwtToken);
        String id = ciId.get("id").toString();
        aiops.addStaticCisToServiceModel(serviceId, id, helixJwtToken, "");
    }

    public void createServiceFromAPI(String serviceName, String ciName, String connectorType, String accessKey, String secretKey) {
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        Aiops aiops = new Aiops();
        aiops.createBlankService(serviceName, helixJwtToken);
        String serviceId = getServiceIdByNameAPI(serviceName);
        Map<String, Object> ciId = new Discovery().fetchCiIdForLatestImport(ciName, connectorType, helixJwtToken);
        String id = ciId.get("id").toString();
        aiops.addStaticCisToServiceModel(serviceId, id, helixJwtToken, "");
    }

    public List<String> getServicesFromAPIWithRelatedCis(String ciName, String accessKey, String secretKey) {
        return discovery.getServicesFromAPIWithRelatedCis(ciName, accessKey, secretKey);
    }


    public int getServiceHealthScore(String serviceName) {
        String token = serviceMonitoringPage.getJwtToken();
        Aiops aiopsApiCore = new Aiops();
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, token);
        return Integer.parseInt(healthScore.get("health"));
    }

    public void verifyCreateIncidentOptionInAiopsService(String serviceName){
        iaPage.navigateToMonitoringPage();
        iaPage.switchToWindow(SolQAConstants.AIOpsTitle);
        log.info("Verifying Create Incident option is present in AIOps Service");
        aiOpsPage.navigateToEntitles();
        aiOpsPage.openService(serviceName);
        Assert.assertTrue(aiOpsPage.selectCreateIncidentActionForFirstEventInAiopsService(), "Create Incident option is not present in AIOps Service.");
    }


    public boolean waitUntilScoreBelowThreshold(String serviceName, int threshold, Duration timeout)
            throws InterruptedException {

        Instant endTime = Instant.now().plus(timeout);
        int score;

        do {
            // Fetch the latest score
            score = getServiceHealthScore(serviceName);

            log.info("Current health score: " + score);

            // Break early if condition met
            if (score < threshold) {
                log.info("✅ Score dropped below " + threshold + ": " + score);
                return true;
            }

            // Wait before checking again
            Thread.sleep(5000); // poll every 5 seconds

        } while (Instant.now().isBefore(endTime));

        log.info("⚠️ Timeout reached. Last known score: " + score);
        return false;
    }

    public int sendSingleEventWithTagExcludeFromSituation(String eventName, String node_name,String accessKey, String secretKey) throws Exception {
        log.info("-----Send Event to the node with tag ExcludeFromSituation-----");
        int noOfEvents = 1;
        String token = serviceMonitoringPage.getJwtToken();
        hmEvents.streamRestApiEvents(SolQAConstants.evClass, noOfEvents, token, node_name, eventName, SolQAConstants.severityCritical, SolQAConstants.location, SolQAConstants.EXCLUDE_FROM_SITUATION);
        String helixJwtToken = eventsAPI.getJwtToken(accessKey, secretKey);
        Map<String, String> filters = new HashMap<>();
        filters.put(SolQAConstants.ATTRIBUTE_CLASS, SolQAConstants.evClass);
        filters.put(SolQAConstants.EVENT_HOST, node_name);
        List<String> event_ids = hmEvents.getEventIdentifierApi(filters, helixJwtToken);

        return noOfEvents;
    }

    public int sendSingleEvent(String eventName, String node_name) throws Exception {
        log.info("-----Send normal Event to the node-----");
        int noOfEvents = 1;
        String token = serviceMonitoringPage.getJwtToken();
        String id = hmEvents.streamRestApiEvents(SolQAConstants.EVENT_CLASS_VALUE, noOfEvents, token, node_name, eventName, SolQAConstants.severityCritical, SolQAConstants.location, "");
        return noOfEvents;
    }

    public void openSituationAndVerifyExpectedEventsToFormSituation(String serviceName, String event1, String event2, String event3){
        String token = serviceMonitoringPage.getJwtToken();
        boolean flag = new Aiops().waitForSituationCreation(serviceName, token, 9000);
        JSONObject situationOnject = new Aiops().getSituationsForService(serviceName, token);
        JSONArray arr = situationOnject.getJSONArray("eventList");
        JSONObject jObj = arr.getJSONObject(0);
        String situationName = jObj.getString("msg");
        if(flag) {
            SituationsPage situationsPage = aiOpsPage.clickOnSituation(situationName);
            situationsPage.clickOnEventTabInSituation();
            List<String> actualEventList = situationsPage.getEventMessagesFromSituation();
            Assert.assertTrue(!actualEventList.contains(event1), "Excluded event is present in situation");
            Assert.assertTrue(actualEventList.contains(event2), "event is not present to form situation");
            Assert.assertTrue(actualEventList.contains(event3), "event is not present to form situation");
        }
    }
    public void addHealthIndicatorToService(String metricType, String metricName) {
        aiOpsPage.enableEditServiceMode();
        aiOpsPage.addHealthIndicatorToService(metricType, metricName);
    }

    public void deleteHealthIndicatorToService(String metricName){
        aiOpsPage.enableEditServiceMode();
        aiOpsPage.deleteHealthIndicator(metricName);
    }

    public void deleteServiceUsingUI(String serviceName){
        aiOpsPage.openService(serviceName);
        aiOpsPage.deleteServiceUsingUI();
    }

    public List<String> getAgentHostnames() throws IOException {
        return eventsAPI.getAgentHostnames();
    }
    
    public AIOpsPage searchSituationAndOpenAssociatedIncident(String situation) {
    	situationsPage.searchAndOpenSituation(situation);
    	aiOpsPage = situationsPage.openIncidentAssociated();
        return aiOpsPage;
    }

    public void closeBrowser() {
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

	public void createDuplicateService(String serviceName,String CIname) {
		// TODO Auto-generated method stub
		  log.info("Create Serivce if not exist"); 
		aiOpsPage.createDuplicateService(serviceName,CIname);
		 navigateToServicesTab();
		  commonWaits.waitForAllSpinnersToHide(SolQAConstants.AIOPS);
	}

	public void verifyRiskCountOfService(String servicename,String riskcount) {
		// TODO Auto-generated method stub
		  log.info("Verify Risk count"); 
		  navigateToServicesTab();
		  openService(servicename);
		  commonWaits.waitForAllSpinnersToHide(SolQAConstants.AIOPS);
		  String countResult = aiOpsPage.getRiskCount();
		  navigateToServicesTab();
		  commonWaits.waitForAllSpinnersToHide(SolQAConstants.AIOPS);
		  Assert.assertTrue(riskcount.equalsIgnoreCase( countResult), "Future Risk count for Newly Created service not matching");
		
	}

    public Map<String, Integer> getEventClassCounts(JSONArray hitsArray, boolean filterNodeId, boolean filterService) {
        Map<String, Integer> classCountMap = new HashMap<>();
        int totalCount = 0;

        log.info("Processing " + hitsArray.length() + " events with filters - filterNodeId: " + filterNodeId + ", filterService: " + filterService);

        for (int i = 0; i < hitsArray.length(); i++) {
            try {
                JSONObject hit = hitsArray.getJSONObject(i).getJSONObject("_source");
                String classVal = hit.optString("class", "");
                String nodeId = hit.optString("_node_id", "");
                String serviceId = hit.optString("_service_id", "");

                if ("Situation".equalsIgnoreCase(classVal) ||
                    "INCIDENT_INFO".equalsIgnoreCase(classVal) ||
                    "Change".equalsIgnoreCase(classVal) ||
                    "Prediction".equalsIgnoreCase(classVal)) {
                    continue;
                }

                boolean hasNodeId = nodeId != null && nodeId.length() > 3;
                boolean hasServiceId = serviceId != null && serviceId.length() > 3;

                if (filterNodeId && hasNodeId) {
                    continue;
                }

                if (filterService) {
                    if (!hasNodeId || hasServiceId) {
                        continue;
                    }
                }

                classCountMap.put(classVal, classCountMap.getOrDefault(classVal, 0) + 1);
                totalCount++;

            } catch (Exception e) {
                log.warn("Error processing hit at index " + i + ": " + e.getMessage());
            }
        }

        log.info("Total events matching filter criteria: " + totalCount);
        log.info("Class counts: " + classCountMap);
        return classCountMap;
    }

    public Map<String, Integer> getEventSourceHostnameCounts(JSONArray hitsArray, boolean filterNodeId, boolean filterService) {
        Map<String, Integer> hostnameCountMap = new HashMap<>();
        int totalCount = 0;

        log.info("Processing " + hitsArray.length() + " events by source_hostname - filterNodeId: " + filterNodeId + ", filterService: " + filterService);

        for (int i = 0; i < hitsArray.length(); i++) {
            try {
                JSONObject hit = hitsArray.getJSONObject(i).getJSONObject("_source");
                String classVal = hit.optString("class", "");
                String sourceHostname = hit.optString("source_hostname", "");
                String nodeId = hit.optString("_node_id", "");
                String serviceId = hit.optString("_service_id", "");

                if ("Situation".equalsIgnoreCase(classVal) ||
                    "INCIDENT_INFO".equalsIgnoreCase(classVal) ||
                    "Change".equalsIgnoreCase(classVal) ||
                    "Prediction".equalsIgnoreCase(classVal)) {
                    continue;
                }

                boolean hasNodeId = nodeId != null && nodeId.length() > 3;
                boolean hasServiceId = serviceId != null && serviceId.length() > 3;

                if (filterNodeId && hasNodeId) {
                    continue;
                }

                if (filterService) {
                    if (!hasNodeId || hasServiceId) {
                        continue;
                    }
                }

                if (sourceHostname == null || sourceHostname.trim().isEmpty()) {
                    sourceHostname = "(empty)";
                }

                hostnameCountMap.put(sourceHostname, hostnameCountMap.getOrDefault(sourceHostname, 0) + 1);
                totalCount++;

            } catch (Exception e) {
                log.warn("Error processing hit at index " + i + ": " + e.getMessage());
            }
        }

        log.info("Total events matching filter criteria: " + totalCount);
        log.info("Source hostname counts (" + hostnameCountMap.size() + " unique): " + hostnameCountMap);
        return hostnameCountMap;
    }

    public Map<String, Double> calculateClassPercentages(Map<String, Integer> classCountMap) {
        Map<String, Double> percentageMap = new HashMap<>();
        int total = classCountMap.values().stream().mapToInt(Integer::intValue).sum();

        if (total == 0) {
            log.warn("Total count is 0, cannot calculate percentages");
            return percentageMap;
        }

        for (Map.Entry<String, Integer> entry : classCountMap.entrySet()) {
            double percentage = Math.round(((double) entry.getValue() / total) * 100.0 * 10.0) / 10.0;
            percentageMap.put(entry.getKey(), percentage);
            log.info("Class: " + entry.getKey() + ", Count: " + entry.getValue() + ", Percentage: " + percentage + "%");
        }

        return percentageMap;
    }    

    public AIOpsFeatureMethods configureIAPermission() {
        aiOpsPage.configureIAPermission();
        return this;
    }

    public AIOpsFeatureMethods navigateToEntitles() {
        aiOpsPage.navigateToEntitles();
        return this;
    }

    public AIOpsFeatureMethods selectMessageForRunAutomation(String policy_Name) {
        aiOpsPage.selectMessageForRunAutomation(policy_Name);
        return this;
    }

    public String getAutomationPolicyStatus(String policy_Name) {
        return aiOpsPage.getAutomationPolicyStatus(policy_Name);
    }

    public AIOpsFeatureMethods navigateToAIOpsTab(String tabName) {
        log.info("Navigating to AIOps tab: " + tabName);
        aiOpsPage.navigateToAIOpsTab(tabName);
        return this;
    }

    /**
     * Navigate to AIOps configuration sub-tabs like Connectors, SLA, Product Features
     * @param subTabName the sub-tab name to navigate to (use constants from SolQAConstants)
     * @return AIOpsFeatureMethods instance for method chaining
     */
    public AIOpsFeatureMethods navigateToConfigurationSubTab(String subTabName) {
        log.info("Navigating to AIOps Configuration sub-tab: " + subTabName);
        aiOpsPage.navigateToConfigurationSubTab(subTabName);
        return this;
    }

    /**
     * Navigate to AIOps Risk sub-tabs like Vulnerabilities and Change Risk Advisor
     * @param riskSubTabName the risk sub-tab name to navigate to (use constants from SolQAConstants)
     * @return AIOpsFeatureMethods instance for method chaining
     */
    public AIOpsFeatureMethods navigateToRiskSubTab(String riskSubTabName) {
        log.info("Navigating to AIOps Risk sub-tab: " + riskSubTabName);
        aiOpsPage.navigateToRiskSubTab(riskSubTabName);
        return this;
    }

    /**
     * Enable a product feature if it's not already enabled
     * @param featureName the name of the feature to enable (e.g., "Intelligent Automation")
     * @return AIOpsFeatureMethods instance for method chaining
     */
    public AIOpsFeatureMethods enableFeature(String featureName) {
        log.info("Enabling feature: " + featureName);
        aiOpsPage.enableFeature(featureName);
        return this;
    }

    public AIOpsFeatureMethods clickViewAllButton(String viewType) {
        log.info("Clicking on AIOps View All button for view type: " + viewType);
        aiOpsPage.clickViewAllButton(viewType);
        return this;
    }

    public AIOpsFeatureMethods searchVulnerabilities(String searchTerm) {
        log.info("Searching for vulnerabilities with term: " + searchTerm);
        aiOpsPage.searchVulnerabilities(searchTerm);
        return this;
    }

    public AIOpsFeatureMethods clickVulnerabilityByName(String vulnerabilityName) {
        log.info("Clicking on vulnerability: " + vulnerabilityName);
        aiOpsPage.clickVulnerabilityByName(vulnerabilityName);
        return this;
    }

    public AIOpsFeatureMethods clickFirstVulnerabilityFromResults() {
        log.info("Clicking on first vulnerability from search results");
        aiOpsPage.clickFirstVulnerabilityFromResults();
        return this;
    }

    public AIOpsFeatureMethods searchAndOpenVulnerability(String searchTerm, String vulnerabilityName) {
        log.info("Searching and opening vulnerability - Search term: " + searchTerm + ", Vulnerability: " + vulnerabilityName);
        aiOpsPage.searchVulnerabilities(searchTerm);
        aiOpsPage.clickVulnerabilityByName(vulnerabilityName);
        return this;
    }

    public AIOpsFeatureMethods searchAndOpenFirstVulnerability(String searchTerm) {
        log.info("Searching and opening first vulnerability with term: " + searchTerm);
        aiOpsPage.searchVulnerabilities(searchTerm);
        aiOpsPage.clickFirstVulnerabilityFromResults();
        return this;
    }

    public boolean isVulnerabilitySearchResultDisplayed(String vulnerabilityName) {
        log.info("Checking if vulnerability search result is displayed: " + vulnerabilityName);
        return aiOpsPage.isVulnerabilitySearchResultDisplayed(vulnerabilityName);
    }

    public AIOpsFeatureMethods selectAssetByName(String assetName) {
        log.info("Selecting asset by name: " + assetName);
        aiOpsPage.selectAssetByName(assetName);
        return this;
    }

    public AIOpsFeatureMethods clickAssetActionMenu(String assetName) {
        log.info("Clicking action menu for asset: " + assetName);
        aiOpsPage.clickAssetActionMenu(assetName);
        return this;
    }

    public AIOpsFeatureMethods clickPlanAutomation() {
        log.info("Clicking on Plan Automation from dropdown menu");
        aiOpsPage.clickPlanAutomation();
        return this;
    }

    public AIOpsFeatureMethods clickUpdateStatus() {
        log.info("Clicking on Update Status from dropdown menu");
        aiOpsPage.clickUpdateStatus();
        return this;
    }

    public AIOpsFeatureMethods selectAssetAndPlanAutomation(String assetName) {
        log.info("Selecting asset and opening Plan Automation for: " + assetName);
        aiOpsPage.selectAssetAndPlanAutomation(assetName);
        return this;
    }

    public AIOpsFeatureMethods selectAssetAndUpdateStatus(String assetName) {
        log.info("Selecting asset and opening Update Status for: " + assetName);
        aiOpsPage.selectAssetAndUpdateStatus(assetName);
        return this;
    }

    public boolean isAssetDisplayedInTable(String assetName) {
        log.info("Checking if asset is displayed in table: " + assetName);
        return aiOpsPage.isAssetDisplayedInTable(assetName);
    }
}
