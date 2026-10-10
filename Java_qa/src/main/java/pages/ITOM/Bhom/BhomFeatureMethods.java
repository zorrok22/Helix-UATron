package pages.ITOM.Bhom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.Launcher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import pages.ITOM.Aiops.AIOpsPage;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import pages.ITOM.Platform.PlatformLandingPage;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import pages.ITOM.Api.MailsacApi;

public class BhomFeatureMethods extends Launcher {
    public static SolqaLogger log = new SolqaLogger(BhomFeatureMethods.class.getName());

    CommonWaits commonWaits;
    pages.ITOM.Intelligent_Automation.IntelligentAutomationPage iaPage;
    ServiceMonitoringPage smPage;
    pages.ITOM.Bhom.BhomPage monitoringPage;
    pages.ITOM.Platform.PlatformLandingPage platformLandingPage;
    Events_API eventsAPI;
    AIOpsPage aiopsPage;
    public static String AIOpsTitle=SolQAConstants.AIOpsTitle;

    Map<String, String> filters = new HashMap<>();
    HMEvents eventsApi = new HMEvents();
    HMEvents hmEvents = new HMEvents();
    MailsacApi mailsacApi;

    public BhomFeatureMethods() {
        commonWaits = new CommonWaits();
        monitoringPage = new BhomPage();
        smPage = new ServiceMonitoringPage(getDriver());
        iaPage = new IntelligentAutomationPage();
        aiopsPage = new AIOpsPage();
        eventsAPI = new Events_API();
        platformLandingPage = new PlatformLandingPage(getDriver());
    }

    public String getIncidentID() {
        return monitoringPage.getIncidentID();
    }

    public void verifyLogin() {
        monitoringPage.verifyBHOMLaunchedSuccessfully("BMC Helix Operations Management");
    }

    public String create_correlation_event_policy(String JsonPath, String uniqueEventMessage, String cluster_CI) {
        return smPage.create_correlation_event_policy(JsonPath, uniqueEventMessage, cluster_CI);
    }

    public int searchAndCountSearchedDevices(String deviceName) {
        return monitoringPage.searchAndCountSearchedDevice(deviceName);
    }

    public void deactivate_event_policy(String policyID) {
        smPage.deactivate_event_policy(policyID);
    }

    public void delete_event_policy(String policyID) {
        smPage.delete_event_policy(policyID);
    }

    public String create_group(String jsonFilePath, String sGroupName, String hostName, String groupid, String publish) {
        return smPage.create_group(jsonFilePath,sGroupName,sGroupName,hostName,groupid,publish);
    }

    public void update_group(String jsonFilePath, String sGroupName, String hostName, String groupid, String publish) {
        smPage.update_group(jsonFilePath,sGroupName,sGroupName,hostName,groupid,publish);
    }

    public void delete_group(String groupID){
        smPage.delete_group(groupID);
    }

    public boolean searchAndDeleteGroup(String groupName) {
        return hmEvents.searchAndDeleteGroup(groupName, smPage.getJwtToken());
    }

    public void checkForGroupStatusUpdate(){
        monitoringPage.refreshBrowser();
        commonWaits.waitForPageLoad(getDriver());
        monitoringPage.navigateToGroupTab();
    }

    public String navigateToAgentsAndGetCommonHostnameSubstring() {
        return monitoringPage.navigateToAgentsAndGetCommonHostnameSubstring();
    }

    public BhomFeatureMethods navigateToGroups() {
        monitoringPage.navigateToGroupTab();
		return this;
    }

    public String createGroupWithEntityAndEvent(String groupNamePrefix, String entityFieldName, String entityOperator, String entityHostName, String eventFieldName, String eventOperator, String eventClassName) {
        return monitoringPage.createGroupWithEntityAndEvent(groupNamePrefix, entityFieldName, entityOperator, entityHostName, eventFieldName, eventOperator, eventClassName);
    }

    public boolean isNotificationPresent() {
        return monitoringPage.isNotificationPresent();
    }

    public String getNotificationMessage() {
        return monitoringPage.getNotificationMessage();
    }

    public String getPreviewDevices() {
        return monitoringPage.getPreviewDevices();
    }

    public String getPreviewEvents() {
        return monitoringPage.getPreviewEvents();
    }

    public BhomPage navigateToOperationsManagementPage(){
        iaPage.navigateToOperationsManagementPage();
        return monitoringPage;
    }

    public String get_group_published_status(String groupName){
        monitoringPage.navigateToGroupTab();
        return monitoringPage.searchForGroupAndRetrieveItsPublishedStatus(groupName);
    }

    public Boolean checkIfEventGroupIsListed(String groupName){
        return monitoringPage.checkIfGroupIsListed(groupName);
    }

    public BhomFeatureMethods openGroupAndVerifyDetails(String groupName, String expectedDeviceCount, String expectedEventCount, String entityHostName, String eventClassName) {
        monitoringPage.openGroupAndVerifyDetails(groupName, expectedDeviceCount, expectedEventCount, entityHostName, eventClassName);
		return this;
    }

    public void send_events(String uniqueEventMessage, String cluster_CI){
        smPage.send_events(uniqueEventMessage, cluster_CI);
    }

    public void send_IA_policy_event(String hostName, String uniqueMessage) throws IOException {
        smPage.send_IA_policy_event(hostName, uniqueMessage);
    }

    public void send_events_with_object(String uniqueEventMessage, String cluster_CI){
        smPage.send_events_with_object(uniqueEventMessage, cluster_CI);

    }

    public void send_events_with_class(String className, String uniqueEventMessage, String cluster_CI){
        smPage.send_events_with_class(className, uniqueEventMessage, cluster_CI);
    }
    public void send_events_with_details(String uniqueMessage, String hostName, String details){
        smPage.send_events_with_details(uniqueMessage, hostName, details);
    }

    public void send_events_with_json(String jsonFilePath){
        try{
        smPage.send_events_with_json(jsonFilePath);}
        catch(IOException e){
            log.error("Error sending events with JSON file: " + e.getMessage());
            throw new RuntimeException("Failed to send events with JSON file, "+jsonFilePath, e);
        }
    }

    public int gotoServicesPageAndRetrieveHealthScore(String serviceName) throws Exception{
        int score = 0;
        iaPage.navigateToMonitoringPage();
        iaPage.switchToWindow(AIOpsTitle);
        log.info("Navigate to Entitles tab");
        aiopsPage.navigateToEntitles();
        aiopsPage.openService(serviceName);
        score = aiopsPage.getServiceHealthScore(serviceName);
        log.info("Health Score is: "+String.valueOf(score));
        return score;
    }

    public void searchForCorrelatedEvent(String uniqueEventMessage){
        iaPage.navigateToOperationsManagementPage();
        monitoringPage.navigateToEventsTab();
        monitoringPage.searchEvent("corr_" + uniqueEventMessage);
        Assert.assertEquals(monitoringPage.getEventCount(), 1, "Correlated event is not seen created.");
        monitoringPage.viewRelatedEventDetails();
        Assert.assertEquals(monitoringPage.getEventCount(), 4, "Correct number of events are not seen added under correlated event.");
        monitoringPage.GoBackFromEventDetails();
    }

    public void searchForCorrelatedEventPostDeactivation(String uniqueEventMessage){
        monitoringPage.navigateToEventsTab();
        monitoringPage.searchEvent("corr_" + uniqueEventMessage);
        Assert.assertEquals(monitoringPage.getEventCount(), 1, "Older Correlated event is present.");
        monitoringPage.viewRelatedEventDetails();
        Assert.assertEquals(monitoringPage.getEventCount(), 4, "Correct number of events are not seen added under correlated event.");
        monitoringPage.GoBackFromEventDetails();
    }

    public void searchForEventGenerated(String uniqueEventMessage){
        iaPage.navigateToOperationsManagementPage();
        monitoringPage.navigateToEventsTab();
        monitoringPage.searchEvent(uniqueEventMessage);
    }

    public void searchForEventGeneratedWithDetails(String uniqueEventMessage){
        iaPage.navigateToOperationsManagementPage();
        monitoringPage.navigateToEventsTab();
        monitoringPage.searchEventwithDetails(uniqueEventMessage);
    }

    public void acknowledgeEvent(String uniqueEventMessage){
        log.info("Acknowledging event: " + uniqueEventMessage);
        monitoringPage.acknowledgeEvent(uniqueEventMessage);
        log.info("Event acknowledged successfully");
    }

    public void assignEvent(String uniqueEventMessage){
        log.info("Assigning event: " + uniqueEventMessage);
        monitoringPage.assignEvent(uniqueEventMessage);
        log.info("Event assigned successfully");
    }

    public void closeEvent(String uniqueEventMessage){
        log.info("Closing event: " + uniqueEventMessage);
        monitoringPage.closeEvent(uniqueEventMessage);
        log.info("Event closed successfully");
    }

    public void verifyTriggerAutomation(String uniqueEventMessage){
        log.info("Check if the automation status is successfully or not. ");
        monitoringPage.verifyTriggerAutomation(uniqueEventMessage);
    }

    public void closeAllBlackoutEvents() {
        log.info("Closing all blackout events");
        monitoringPage.navigateToEventsTab();
        monitoringPage.closeAllBlackoutEvents();
        monitoringPage.refreshEvents();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void closeAllEventsForService(String serviceName) {
        log.info("Closing all events for Service: " + serviceName);
        monitoringPage.navigateToEventsTab();
        monitoringPage.closeAllOpenEventsForService(serviceName);
        monitoringPage.refreshEvents();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void injectEventForBlackoutPolicy(String jsonFilePath, String hostName, String msg) throws IOException {
        log.info("Creating blackout policy events for host: " + hostName);
        eventsAPI.injectEventForBlackoutPolicy(jsonFilePath, hostName, msg);
    }

    public void verifyBlackoutPolicyCreatedEnabledAndNotExpired(String policyName) {
        log.info("Verifying blackout policy is created and enabled");
        monitoringPage.navigateToBlackoutPolicies();
        monitoringPage.searchBlackoutPolicy(policyName);
        monitoringPage.verifyBlackoutPolicyCreatedAndEnabled();
        monitoringPage.verifyBlackoutPolicyNotExpired();
    }

    public void verifyOpenEventsCreatedForHost(String hostName) {
        log.info("Verifying events are created for host: " + hostName);
        monitoringPage.navigateToEventsTab();
        monitoringPage.refreshEvents();
        monitoringPage.verifyOpenEventsCreatedForHost(hostName);
    }

    public void verifyEventsInBlackoutState(String hostName) {
        log.info("Verifying events in blackout state for host: " + hostName);
        monitoringPage.navigateToEventsTab();
        commonWaits.waitUntilNextTwoMinutes();
        monitoringPage.refreshEvents();
        Assert.assertTrue(monitoringPage.verifyEventsInBlackoutState(hostName), "Events not in blackout state for host: " + hostName);
    }

    public void verifyEventsStatus(String eventName, String eventStatus) {
        monitoringPage.navigateToEventsTab();
        commonWaits.waitUntilNextTwoMinutes();
        monitoringPage.refreshEvents();
        Assert.assertEquals(monitoringPage.getEventStatus(eventName), eventStatus, "Event status does not match expected value.");
    }

    public void waitForPolicyToActivate(){
        commonWaits.waitUntilNextTwoMinutes();
    }

    public void createBlackoutPolicy(String jsonFilePath, String hostName, String policyName, String blackoutExistingEvents, String resetStatusOnBlackoutEnd) throws IOException {
        log.info("Creating blackout policy for host: " + hostName);
        eventsAPI.createBlackoutPolicy(jsonFilePath, hostName, policyName, blackoutExistingEvents, resetStatusOnBlackoutEnd);
        log.info("Blackout policy created successfully for host: " + hostName);
    }

    public void disableBlackoutPolicy(String hostName) {
        log.info("Disabling blackout policy for host: " + hostName);
        commonWaits.switchToTabByTitle(SolQAConstants.BHOM);
        monitoringPage.disableBlackoutPolicy(hostName);
    }

    public void searchAndOpenEventsByMessage(String uniqueEventMessage) throws Exception{
        monitoringPage.navigateToEventsTab();
        monitoringPage.searchAndOpenEventsByMessage(uniqueEventMessage);
    }
    
    public void searchAndOpenNonInformationEventByMessage(String uniqueEventMessage) throws Exception {
        monitoringPage.navigateToEventsTab();
        monitoringPage.searchAndOpenNonInformationEventByMessage(uniqueEventMessage);
    }

    public String getEventMessage(){
        return monitoringPage.getHeaderMessageValue();
    }

    public String getSNOWIncidentValue(){
        return monitoringPage.getSNOWIncidentValue();
    }

    public String getIncidentValue(){
        return monitoringPage.getIncidentValue();
    }

    public BhomPage closeEventsWithMsgAndCI(String uniqueMsg, String hostname) throws Exception{
        filters.put(SolQAConstants.EVENT_CLASS, SolQAConstants.EVENT_CLASS_VALUE);
        if (hostname != "") {
            filters.put(SolQAConstants.EVENT_HOST, hostname);
        }
        if (uniqueMsg != "") {
            filters.put(SolQAConstants.EVENT_MSG, uniqueMsg);
        }
        ServiceMonitoringPage smpage = new ServiceMonitoringPage(getDriver());
        List<String> event_ids = eventsApi.getEventIdentifierApi(filters, smpage.getJwtToken());
        for (String event_id: event_ids){
            hmEvents.closeEventApi(event_id,"Closing event",smpage.getJwtToken()) ;
        }
        return monitoringPage;
    }


    public String getServiceNameFromPayload(String jsonPayload) {
        return monitoringPage.getServiceNameFromPayload(jsonPayload);
    }



    public boolean isServiceNamePresentInEvent(String sUniqueMsg) throws Exception{
        filters.put(SolQAConstants.EVENT_CLASS, "Event");
        filters.put(SolQAConstants.EVENT_MSG, sUniqueMsg);
        ServiceMonitoringPage smpage = new ServiceMonitoringPage(getDriver());
        List<String> event_ids = eventsApi.getEventIdentifierApi(filters, smpage.getJwtToken());
        String eventDetails = "";
        for (String event_id: event_ids){
            eventDetails = eventsApi.eventDetails(event_id, smpage.getJwtToken());
            String serviceName =  getServiceNameFromPayload(eventDetails);
            if (serviceName != null){
                log.info("Service Name associated for L3 event with message "+sUniqueMsg+" is: "+serviceName);
                return true;
            }
        }
        return false;
    }


    public int getIIEventsFromToday(String classType) throws Exception{
        filters.put(SolQAConstants.EVENT_CLASS, classType);
        ServiceMonitoringPage smpage = new ServiceMonitoringPage(getDriver());
        List<String> event_ids = eventsApi.getEventIdentifierApi(filters, smpage.getJwtToken());
        String eventDetails = "";
        int i = 0;
        for (String event_id: event_ids){
            String eventMessage = null;
            eventDetails = eventsApi.eventDetails(event_id, smpage.getJwtToken());
            eventMessage = getEventDetailsIfModifiedToday(eventDetails);
            if (eventMessage != null){
                i++;
            }
        }
        log.info("Total events synced today with class type "+classType+" are: "+String.valueOf(i));
        return i;
    }

    public String getEventDetailsIfModifiedToday(String jsonText) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonText);

        // Navigate to response → allslots
        JsonNode allSlots = root.path("response").path("allslots");

        // Extract fields
        String modifiedTimeStr = allSlots.path("_modified_time").asText();
        String details = allSlots.path("msg").asText();

        if (modifiedTimeStr.isEmpty() || details.isEmpty()) {
            System.out.println("⚠️ Missing required fields in JSON.");
            return null;
        }

        // Convert modified time (epoch millis) to local date
        long modifiedMillis = Long.parseLong(modifiedTimeStr);
        LocalDate modifiedDate = Instant.ofEpochMilli(modifiedMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate today = LocalDate.now();

        // Check if modified today
        if (modifiedDate.equals(today)) {
            return details;
        }

        return null;
    }

    public String getEventDetailsIfModifiedRecently(String jsonText, int iDays) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonText);

        // Navigate to response → allslots
        JsonNode allSlots = root.path("response").path("allslots");

        // Extract modified_time and details
        String modifiedTimeStr = allSlots.path("_modified_time").asText();
        String details = allSlots.path("msg").asText();

        if (modifiedTimeStr.isEmpty() || details.isEmpty()) {
            System.out.println("Missing required fields in JSON.");
            return null;
        }

        // Parse modified time (given in milliseconds as a string)
        long modifiedMillis = Long.parseLong(modifiedTimeStr);
        Instant modifiedInstant = Instant.ofEpochMilli(modifiedMillis);
        Instant now = Instant.now();

        long daysBetween = ChronoUnit.DAYS.between(modifiedInstant, now);

        // Return details only if modified within 2 days
        if ( daysBetween<= iDays) {
            return details;
        }
        return null;
    }

    public void advancedFilter(String criteria){
        smPage.advancedFilter(criteria);
    }

    public void exportEventAndVerifyFields(String key, String value) throws IOException, ParseException {
        String jsonFileName = "mcevent.json";
        String filePath = new Launcher().getDownloadDirectory() + File.separator + jsonFileName;
        if (Files.deleteIfExists(Paths.get(filePath))){
            System.out.println("Existing file deleted successfully: " + filePath);
        } else {
            System.out.println("File did not exist, no deletion performed: " + filePath);
        }
        smPage.exportEvent();
        String myJson = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
        JSONArray myJsonArray = new JSONArray(myJson);
        JSONObject jsonObject = myJsonArray.getJSONObject(0);
        Assert.assertEquals(jsonObject.get(key), value, jsonObject.get(key) + ":" + value + " field is not available in exported json.");
    }

    public void closeEventsByIdApi(String id, String closureMessage) throws JsonProcessingException {
        hmEvents.closeEventApi(id, closureMessage, smPage.getJwtToken());
    }

    public void verifyEventEditDetails(String eventName) {
        monitoringPage.verifyEventEditDetails(eventName);
    }

    public void verifyEventTabNames() {
        monitoringPage.verifyEventTabNames();
    }

    public void navigateToAlarmPolicies() {
        monitoringPage.navigateToAlarmPolicies();
    }

    public BhomFeatureMethods  deleteAlarmPolicy(String policyName) {
        Assert.assertTrue(monitoringPage.searchAlarmPolicy(policyName), "Alarm policy is not available");
        monitoringPage.deletePolicy(policyName);
		return this;
    }

    public void createAlarmPolicy(Map<Object, String> testData) {
        log.info("Searching is alarm policy already exists: " + testData.get("policyName"));
        boolean isPolicyExists = monitoringPage.searchAlarmPolicy(testData.get("policyName"));
        if(isPolicyExists) {
            log.info("Alarm policy already exists: " + testData.get("policyName"));
            return;
        }
        else{
            log.info("Alarm policy does not exist: " + testData.get("policyName"));
            log.info("Creating alarm policy: " + testData.get("policyName"));
            monitoringPage.createAlarmPolicy(testData);
        }
    }


    public boolean isCreateIncidentOptionPresent(){
        return monitoringPage.isCreateIncidentOptionPresent();
    }

    public BhomPage clickCreateIncident(){
        monitoringPage.clickCreateIncident();
        return monitoringPage;
    }
	public void selectMultipleEvent() {
		// TODO Auto-generated method stub		
		commonWaits.waitForPageLoad(getCurrentDriver());
		Integer incidentcountBeforeEvent = monitoringPage.getIncidentCount();
		Integer incidentcountAfterEvent = monitoringPage.selectMultipleEvent();	
	
		  log.info("Incident Count After"+incidentcountAfterEvent);
          Assert.assertTrue(incidentcountAfterEvent==incidentcountBeforeEvent+2, "Incident count is not incremented");
	    
	}

	
	public int getEventCount() {
		log.info("Getting count of elements for xpath: ");

		return monitoringPage.getEventCount();
	}
	
	public void navigateToAuthProfile() {
		log.info("Navigate to Administration -> Authorization Profiles");
        platformLandingPage.navigateToPage("Administration", "Authorization Profiles");
        commonWaits.waitForPageLoad(getDriver());
        log.info("Navigated to Authorization Profile page");
        
        log.info("Verify Auth Profile Page is displayed");
        monitoringPage.validateAuthProfilePageisOpened();
	}
	
	/**
	 * Method to click on a link by name on Authorization Profile page
	 * @param linkName - Name of the link to click (e.g., "Operator")
	 */
	public void clickOnAuthProfileLink(String linkName) {
		log.info("Clicking on Authorization Profile link: " + linkName);
		monitoringPage.clickOnAuthProfileLink(linkName);
	}
	
	/**
	 * Method to create Authorization Profile with 5 digit random string appended
	 * @param authProfileBaseName - Base name for the authorization profile
	 * @param userGroupName - User group name to be selected
	 * @return String - Full authorization profile name created
	 */
	public String createAuthProfile(String authProfileBaseName, String userGroupName) {
		log.info("Creating Authorization Profile: " + authProfileBaseName + " with user group: " + userGroupName);
		return monitoringPage.CreateAuthProfile(authProfileBaseName, userGroupName);
	}
	
	/**
	 * Method to edit Authorization Profile
	 * @param authProfileName - Name of the authorization profile to edit
	 */
	public void editAuthProfile(String authProfileName) {
		log.info("Editing Authorization Profile: " + authProfileName);
		monitoringPage.editAuthProfile(authProfileName);
	}
	
	/**
	 * Method to delete Authorization Profile
	 * @param authProfileName - Name of the authorization profile to delete
	 */
	public void deleteAuthProfile(String authProfileName) {
		log.info("Deleting Authorization Profile: " + authProfileName);
		monitoringPage.deleteAuthProfile(authProfileName);
	}

    public BhomFeatureMethods selectMessageForCreateAutomation() {
        monitoringPage.selectMessageForCreateAutomation();
        return this;
    }
    
    public BhomFeatureMethods navigateToEventPolicies() {
        monitoringPage.navigateToEventPolicies();
		return this;
    }

    public String createSuppressionEventPolicy(String policyNamePrefix, String eventFieldName, String eventOperator, String eventValue, String policyConfiguration) {
        return monitoringPage.createSuppressionEventPolicy(policyNamePrefix, eventFieldName, eventOperator, eventValue, policyConfiguration);
    }

    public boolean isEventPolicySuccessToastDisplayed() {
        return monitoringPage.isEventPolicySuccessToastDisplayed();
    }

    public String createNotificationEventPolicy(String policyNamePrefix, String eventFieldName,
            String eventOperator, String eventValue, String policyConfiguration,
            String notificationSendTo, String notificationSubject, String notificationMessage,
            String severityOperator, String severityValue) {
        return monitoringPage.createNotificationEventPolicy(policyNamePrefix, eventFieldName,
                eventOperator, eventValue, policyConfiguration, notificationSendTo,
                notificationSubject, notificationMessage, severityOperator, severityValue);
    }

    public BhomFeatureMethods searchAndDeleteEventPolicy(String policyName) {
        hmEvents.SearchanddeleteEventPolicies(policyName, smPage.getJwtToken());
		return this;
    }

    public String sendEvent(String evClass, String sourceHostname,
                            String msg, String severity, String location, String tag) {
    	int eventCount = 1;
    	String token = smPage.getJwtToken();
    	return hmEvents.streamRestApiEvents(evClass, eventCount, token, sourceHostname, msg, severity, location, tag);
    }

    public BhomFeatureMethods navigateToEventsTab() {
        monitoringPage.navigateToEventsTab();
		return this;
    }

    public boolean waitForEventOnEventsPage(String eventMessage) {
        return monitoringPage.waitForEventOnEventsPage(eventMessage, 5);
    }

    public boolean isEventAbsentOnEventsPage(String eventMessage) {
        return monitoringPage.isEventAbsentOnEventsPage(eventMessage);
    }

    public String generateRandomSuffix(int length) {
        return monitoringPage.generateRandomString().substring(0, length);
    }

    public BhomFeatureMethods closeEventsByMessageApi(String eventMessage) throws Exception {
        return closeEventsByMessageApi(eventMessage, smPage.getJwtToken());
    }

    public BhomFeatureMethods closeEventsByMessageApi(String eventMessage, String jwtToken) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(SolQAConstants.EVENT_MSG, eventMessage);
        params.put("status", "OPEN");
        List<String> eventIds = hmEvents.getEventIdentifierApi(params, jwtToken);
        for (String eventId : eventIds) {
            if (eventId != null && !eventId.isEmpty()) {
                hmEvents.closeEventApi(eventId, "Closing event via automation", jwtToken);
            }
        }
		return this;
    }

    public BhomFeatureMethods initMailsacApi(String apiKey) {
        this.mailsacApi = new MailsacApi(apiKey);
		return this;
    }

    public boolean validateNotificationEmail(String emailAddress, String expectedSubjectContent,
            String expectedBodyContent, int maxAttempts) {
        java.util.List<org.json.JSONObject> matchedMessages = mailsacApi.getMessagesBySubject(
                emailAddress, expectedSubjectContent, maxAttempts);
        if (matchedMessages.isEmpty()) {
            log.info("No email found with subject containing: " + expectedSubjectContent);
            return false;
        }
        org.json.JSONObject message = matchedMessages.get(0);
        String subject = message.optString("subject", "");
        log.info("Email subject: " + subject);

        String messageId = message.optString("_id", "");
        String body = mailsacApi.getMessageText(emailAddress, messageId);
        log.info("Email body: " + body);

        boolean subjectMatch = subject.contains(expectedSubjectContent);
        boolean bodyMatch = body.contains(expectedBodyContent);
        log.info("Subject validation (contains '" + expectedSubjectContent + "'): " + subjectMatch);
        log.info("Body validation (contains '" + expectedBodyContent + "'): " + bodyMatch);
        return subjectMatch && bodyMatch;
    }

    public BhomFeatureMethods deleteMailsacMessages(String emailAddress) {
        mailsacApi.deleteAllMessages(emailAddress);
		return this;
    }

    public BhomFeatureMethods navigateToDevices() {
        monitoringPage.navigateToDevices();
        return this;
    }

    public String getAllDevicesCount() {
        return monitoringPage.getAllDevicesCount();
    }

    public BhomFeatureMethods selectEventFiltersBasedOnCount(String devicesStatus) {
        monitoringPage.selectEventFiltersBasedOnCount(devicesStatus);
        return this;
    }

    public BhomFeatureMethods openDevice(String deviceName) {
        monitoringPage.openDevice(deviceName);
        return this;
    }

    public String copyDeviceUrl(String Device1, String Device2, String Device3, String Device4) {
        return monitoringPage.copyDeviceUrl(Device1, Device2, Device3, Device4);
    }

    public BhomFeatureMethods pasteDeviceUrlAndVerifyUserHaveNoAccess(String deviceUrl) {
        monitoringPage.pasteDeviceUrlAndVerifyUserHaveNoAccess(deviceUrl);
        return this;
    }

    public BhomFeatureMethods createNewUserGroup(String groupName, String userName) {
        platformLandingPage.createNewUserGroup(groupName, userName);
        return this;
    }

    public BhomFeatureMethods addNewRole(String roleName) {
        platformLandingPage.addNewRole(roleName);
        return this;
    }

    public BhomFeatureMethods clickOnSelectButton(String buttonName) {
        platformLandingPage.clickOnSelectButton(buttonName);
        return this;
    }

    public BhomFeatureMethods addPermissionToRole(String permissionName, String SubExpandName, String SubCollapseName) {
        log.info("Adding permission " + permissionName );
        clickOnSelectButton("Permissions");
        platformLandingPage.addPermissionToRole(permissionName, SubExpandName, SubCollapseName);
        return this;
    }

    public BhomFeatureMethods removeAllGroupsFromUser(String userName) {
        platformLandingPage.removeAllGroupsFromUser(userName);
        return this;
    }

    public BhomFeatureMethods addGroupToRole(String groupName) {
        platformLandingPage.addGroupToRole(groupName);
        return this;
    }

    public BhomFeatureMethods addUsersToRole(String userName) {
        platformLandingPage.addUsersToRole(userName);
        return this;
    }

    public BhomFeatureMethods navigateToHomePage() {
        platformLandingPage.navigateToHomePage();
        return this;
    }

    public BhomFeatureMethods createAuthProfile(String authProfileName) {
        monitoringPage.createAuthProfile(authProfileName);
        return this;
    }

    public BhomFeatureMethods addAssociatedUserGroup(String userGroupName) {
        monitoringPage.addAssociatedUserGroup(userGroupName);
        return this;
    }

    public BhomFeatureMethods switchToObjectsTab() {
        monitoringPage.switchToObjectsTab();
        return this;
    }

    public BhomFeatureMethods switchToDevicesTab() {
        monitoringPage.switchToDevicesTab();
        return this;
    }

    public BhomFeatureMethods addFirstFourDevices() {
        monitoringPage.addFirstFourDevices();
        return this;
    }

    public List<String> getNameOfAddedDevices() {
        return monitoringPage.getNameOfAddedDevices();
    }

    public BhomFeatureMethods saveAuthProfile() {
        monitoringPage.saveAuthProfile();
        return this;
    }

    public List<String> getNameOfAddedDevicesInRestrictedUser() {
        return monitoringPage.getNameOfAddedDevicesInRestrictedUser();
    }

}
	
