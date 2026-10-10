package ITOM.Aiops;
import ai.onnxruntime.OrtException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.ADESituationHelixGPT;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.OpenAIQuery;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static pages.ITOM.Aiops.ADESituationHelixGPT.openSituationHelixGPT;

public class AiopsSituationBAPTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationBAPTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    CommonMethods commonmethod = new CommonMethods();
    Aiops aiopsApiCore = new Aiops();
    HMEvents hm_events = new HMEvents();
    AiopsDeepRCA aiopsDeepRCA = new AiopsDeepRCA();
    private String eventClass = "NAGIOS_EV1";
    private String location = "Pune";
    public static final String event_tag = "";
    public static final String logInsightEndPoint = "/logs-service/api/v1.0/logs/log-insights/summary/";
    public static final String aiopsRunRanker = "/situation-best-action-recommender/api/v1.0/generate_ranker";
    public static final String helixGPTQueryendpoint = "/situation-best-action-recommender/api/v1.0/pre-canned-question";
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();


    

    @Test(description = "[BAP]:Validate BAP summary with different event messages on respective Service Models part 1", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "13339", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_13339_Validate_BAP_summary_with_different_event_messages_on_respective_Service_Models_situation_creation_create(ITestContext context) throws Exception {
        int testcase_number = 1;
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String mlSheetName = "gpt_data";
        String jwtToken = commonmethod.refreshJwtToken(null);


        log.info("Close all open Event");

        log.info("CLose all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);

        log.info("Check if BAP feature is enabled");
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("BAP")){
            log.info("BAP feature is not enabled; skipping test as passed.");
            return;
        }

        if (!hm_events.checkIncidentCreationEnabled(jwtToken)) {
            log.error("IA PSR not configured. This TC cannot be run.");
            return;
        }

        log.info("Read the gpt_data");
        String[] excludeArray = {"TC", "Feature", "tenant_name", "column", "solution"};

        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, mlSheetName);

        Set<String> firstRowKeys = row_data.get(0).keySet();

        // Create a new set without the excluded keys
        Set<String> filteredKeys = firstRowKeys.stream()
                .filter(key -> !Arrays.asList(excludeArray).contains(key))
                .collect(Collectors.toSet());

        filteredKeys.removeIf(s -> s.contains("_"));

        aiopsDeepRCA.createBAPSituation("HARDWARE", row_data);
        
        List<CompletableFuture<Void>> futures = filteredKeys.stream()
                .map(key1 -> CompletableFuture.runAsync(() -> {
                    for (int j = 0; j < 5; j++) {
                        try {
                            aiopsDeepRCA.createBAPSituation(key1, row_data);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }))
                .collect(Collectors.toList());

        // Wait for all to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        log.info("Close all open Event");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("CLose all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        log.info("Run the Ranker");
        String[] arry = {"200", "{}"};
        logAnalyticsPage.Send_RestCall("POST", aiopsRunRanker, arry);

    }

    @Test(description = "log insight summary generation", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "13108", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_13108_log_insight_summary_generation(ITestContext context) throws Exception {
        int testcase_number = 5;
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String mlSheetName = "pca";
        LogAnalyticsPage loganalyticspage = new LogAnalyticsPage();
        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, mlSheetName);
        SemanticSimilarityONNX similarityCalculator = new SemanticSimilarityONNX();
        
        String jwtToken = commonmethod.refreshJwtToken(null);
        CommonWaits commonWait = new CommonWaits();

        log.info("Check if BAP feature is enabled");
        if(!aiopsDeepRCA.TenantFeatureStatusCheck("BAP")){
            log.info("BAP feature is not enabled; skipping test as passed.");
            return;
        }

        log.info("Check if DEEPRCA feature is enabled");
        if(aiopsDeepRCA.TenantFeatureStatusCheck("DEEPRCA")){
            log.info("DEEPRCA feature is enabled; skipping test as passed.");
            return;
        }

        boolean isProfileLevelToken = aiopsDeepRCA.getprofileleveltoken();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        
        String[] host_list = row_data.get(0).get("Impact Element").toString().split(",");
        String bussness_service = row_data.get(0).get("Business Name").toString();

        log.info("Close all open Event");
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("CLose all open event on Nagios class");
        hm_events.closeAllEventByClass(eventClass, jwtToken);

        Map<String, String> healthScore = aiopsApiCore.getHealthScore(bussness_service, jwtToken);
        log.info("Push Log to system");

        List<Map<String, Object>> log_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, "logs");
        StringBuilder log_send = new StringBuilder();
        for (int i = 1; i < 10; i += 1) {
            log_data = excelReaderForMLSituation.getTestDataFromExcel(i, "logs");
            String msg = log_data.get(0).get("message").toString();
            msg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
            log_send.append(" ").append(msg);
            log.info(msg);
            logAnalyticsPage.logPush(msg, host_list[0]);
        }
        long currenttime = hm_events.getBhomServerTime(jwtToken) / 1000;
        long starttime = currenttime - 43200;
        String currentTime = Instant.ofEpochSecond(currenttime).toString();
        String startTime = Instant.ofEpochSecond(starttime).toString();
        String query = "{\"bool\":{\"filter\":[{\"range\":{\"@timestamp\":{\"gte\":\"" + startTime + "\",\"lte\":\"" + currentTime + "\"}}}]}}";
        JSONObject logAn = logAnalyticsPage.logRetrieval(query, "15");
        Assert.assertNotNull(logAn);
        int totalrecord = logAn.getJSONArray("responses").getJSONObject(0).getJSONObject("_shards").getInt("total");
        if (totalrecord < 1) {
            Assert.fail("No Data push");
        }
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        log.info("Impact the Ci's");
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        String msg;
        int no_of_event_per_node = 1;
        int total_rec = host_list.length;
        for (String hostName : host_list) {
            msg = "Memory utilization of " + hostName + " exceed threshold of 80% " + randomStr;
            hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, jwtToken, hostName, msg, "MAJOR", location, event_tag);
        }

        if (!aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), jwtToken, 180000)) {
            Assert.fail("Situation did not get created within 3 minutes.");
        }
        commonWait.waitForSometime(15000);
        log.info("Get the situation identification");
        JSONObject situationObject = aiopsApiCore.getSituationsForService(bussness_service, jwtToken);
        String identifier = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        String sourceIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).getString("source_identifier");
        log.info("Get the situation details");
        //msg = openSituationHelixGPT(identifier);
        msg  = aiopsDeepRCA.getLogInsightSummaryData(sourceIdentifier);
        log.info("log Insight message " + msg);
        Assert.assertNotNull(msg);
        if (msg.contains("Insufficient")) {
            Assert.fail("No Insight found");
        }

        String llmResponse = OpenAIQuery.sendChatRequest("Provide me summary of below message \n" + log_send);
        double rate = similarityCalculator.calculateSemanticSimilarity(llmResponse, msg);
        log.info("rate calculation " +  rate);
        if (rate < 0.75) {
            log.info(llmResponse);
            log.info(msg);
            Assert.fail("Insight content not match");
        }

    }

    @Test(description = "[BAP]:Validate BAP summary with different event messages on respective Service Models part 1", groups = {"bap"})
    @SolqaDetails(authorName = "Haris", JiraID = "13339", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_13339_Validate_BAP_summary_with_different_event_messages_on_respective_Service_Models_situation_creation_validate(ITestContext context) throws Exception {
        HMEvents hm_events = new HMEvents();
        Aiops aiopsApiCore = new Aiops();
        int processCount = 0;
        SemanticSimilarityONNX similarityCalculator = new SemanticSimilarityONNX();
        int testcase_number = 1;
        String mlSheetName = "gpt_data";
        log.info("Read the gpt_data");
        boolean failureStatus = false;
        String jwtToken = commonmethod.refreshJwtToken(null);

        String[] featureList = {"gpt-situation-summary","gpt-situation-questions", "gpt-situation-best-recommendation-planner", "gpt-situation-code-wizard"};
        for(String feature: featureList){
            if(!aiopsApiCore.getTenantFeatureFlagStatus(feature, jwtToken)){
                log.info("Feature " + feature + " is not enabled; skipping test as passed.");
                return;
            }
        }

        String llmQuery = "provide me the recommendation for the below issue reported by <KINDTYPE> monitoring agent \n";
        String[] excludeArray = {"TC", "Feature", "tenant_name", "column", "solution"};
        String eventMessageFile = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "AIOPS" + File.separator + "event_message.txt";
        List<String> eventMessages = aiopsDeepRCA.readFileLines(eventMessageFile);

        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, mlSheetName);
        Set<String> firstRowKeys = row_data.get(0).keySet();
        Set<String> filteredKeys = firstRowKeys.stream()
                .filter(key -> !Arrays.asList(excludeArray).contains(key))
                .collect(Collectors.toSet());
        filteredKeys.removeIf(s -> s.contains("_"));

        log.info("get situation List");
        String filename = "msearchsituationqueryall.json";
        jwtToken = commonmethod.refreshJwtToken(jwtToken);
        long timediff = 96400000;
        JSONObject situationObject = hm_events.runMSearchQueryGetSituation("", jwtToken, filename, timediff);
        int totalRecord = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalRecord < 1) {
            Assert.fail("No record to validate");
        }

        log.info("Prepare Kind List");
        Map<String, String> kindList = new HashMap<>();
        for (String kindType : filteredKeys) {
            kindList.put(row_data.get(0).get(kindType).toString(), kindType);
        }

        boolean isProfileLevelToken = aiopsDeepRCA.setBrowserLevelProfile();
        if (!isProfileLevelToken) {
            Assert.fail("Get profile level token failed");
        }
        
        log.info("process each situation record");
        JSONArray situationRecords = situationObject.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
        for (Object situation : situationRecords) {
            JSONObject situationRecord = (JSONObject) situation;
            String class1 = situationRecord.getJSONObject("_source").getString("class");
            if (!(class1.contains("Situation"))) {
                continue;
            }
            String source_identifier = situationRecord.getJSONObject("_source").getString("source_identifier");
            String _identifier = situationRecord.getJSONObject("_source").getString("_identifier");
            String incident_id = situationRecord.getJSONObject("_source").getString("incident_id");
            String situationMessage = situationRecord.getJSONObject("_source").getString("msg");

            List<String> eventLists = aiopsApiCore.getEventIdsAssociatedWithSituation(_identifier, jwtToken);
            List<String> eventMessageList = new ArrayList<>();
            if ((eventLists.size() > 5) || (eventLists.isEmpty())) {
                continue;
            }

            JSONArray serviceNameArray = situationRecord.getJSONObject("_source").getJSONArray("_service_name");
            String[] serviceNames = IntStream.range(0, serviceNameArray.length())
                    .mapToObj(serviceNameArray::getString)
                    .toArray(String[]::new);

            boolean foundService = false;
            String foundServiceName = "";
            for (String sName : serviceNames) {
                if (kindList.containsKey(sName)) {
                    foundService = true;
                    foundServiceName = sName;
                    break;
                }
            }
            if (!foundService) {
                log.warn("None of the Service Names " + Arrays.toString(serviceNames) + " found in test data");
                continue;
            }
            String serviceName = foundServiceName;

            boolean foundInFile = false;
            String compareMessage;
            String subSituationMessage = situationMessage;
            if (subSituationMessage.startsWith("Multi-Service -")) {
                compareMessage = subSituationMessage.replaceFirst("^Multi-Service -\\s*", "");
            } else {
                compareMessage = subSituationMessage.replace(serviceNames[0], "");
            }
            for (String eventMessage : eventMessages) {
                if (compareMessage.contains(eventMessage)) {
                    foundInFile = true;
                    break;
                }
            }

            if (!foundInFile) {
                continue;
            }

            String kindType = kindList.get(serviceName);
            log.info("Event Summary");
            for (String eventId : eventLists) {
                String eventdetail = hm_events.eventDetails(eventId, jwtToken);
                JSONObject jsonObject = new JSONObject(eventdetail);
                String message = jsonObject.getJSONObject("response").getJSONObject("allslots").getString("msg");
                eventMessageList.add(message);
            }
            log.info("get validation Type");
            String joinedMessages = "";
            if (!eventMessageList.isEmpty()) {
                joinedMessages = String.join("\n", eventMessageList);
                log.info("Joined Event Messages:\n" + joinedMessages);
            } else {
                log.warn("eventMessageList is empty for situation: " + _identifier);
            }
            String tenantFeature = row_data.get(0).get("Feature").toString();
            llmQuery = llmQuery.replace("<KINDTYPE>", kindList.get(serviceName));
            llmQuery = llmQuery + row_data.get(0).get(kindType + "_column").toString();
            String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
            log.info("LLM Output " + llmOutput);
            log.info("get the BAP summary");

            List<String> BapSummary = aiopsDeepRCA.getSituationGPTSummary(source_identifier);
            log.info("BAP Summary " + (BapSummary != null && !BapSummary.isEmpty() ? BapSummary.get(0) : "null"));
            log.info("Find sematic Similarity");

            // Validate before calling similarityCalculator
            if (BapSummary == null || BapSummary.get(0) == null || BapSummary.get(0).length() < 5 || llmOutput == null || llmOutput.length() < 5) {
                Assert.fail("Summary GPT or LLM Output not available or too short");
            } else {
                double rate = similarityCalculator.calculateSemanticSimilarity(llmOutput, BapSummary.get(0));
                if (rate < 0.70) {
                    log.info("LLM Output: " + llmOutput);
                    log.info("BAP Summary: " + BapSummary.get(0));
                    log.error("BAP summary not matching for situation " + _identifier + " similarity rate " + rate);
                    failureStatus = true;
                }
            }

            String cmdbUrl = config.get("CMDBURL");
            String username = config.get("ADE_USERNAME");
            String password = config.get("ADE_PASSWORD");
            RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
            String authtoken = cmdbauthToken.getRemedyApiKey(username, password);
            CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);

            if (tenantFeature.contains("BAP")) {
                log.info("Validate Pre Scanned Query for 5why");

                String resolution = "";
                String prescanSummary = "";

                    if (incident_id.length() > 3) {
                        log.info("Get resolution field from incident");
                        resolution = cmdbrestapi.getIncidentFieldValue(incident_id, "Resolution");
                        prescanSummary = ADESituationHelixGPT.getSituationHelixGPTwhy5(_identifier);
                        if (prescanSummary.length() < 5) {
                            log.error("Prescanned query 5why failed for situation " + _identifier);
                            failureStatus = true;
                        } else {
                            // Validate before calling similarityCalculator
                            if (resolution != null && resolution.length() >= 5) {
                                double rate = similarityCalculator.calculateSemanticSimilarity(resolution, prescanSummary);
                                if (rate < 0.70) {
                                    log.info("Resolution Output: " + resolution);
                                    log.info("Prescanned Summary: " + prescanSummary);
                                    log.error("Prescanned query 5why not matching for situation " + _identifier + " similarity rate " + rate);
                                    failureStatus = true;
                                }
                            }

                        }
                }
                log.info("Validate Pre Scanned Query for remaining option");
                for (int i = 0; i < 4; i++) {
                    prescanSummary = aiopsDeepRCA.prescannQuery(_identifier, i);
                    if (prescanSummary.equals("Failed")) {
                        log.error("Prescanned query " + i + " failed for situation " + _identifier);
                        failureStatus = true;
                    }
                }
                log.info("Validate resolution information is present in BAP");
                List<String> BapTools = aiopsDeepRCA.getBapTools(source_identifier, _identifier);
                if (BapTools != null) {
                    String bapString = BapTools.get(1);
                    if (bapString == null || bapString.length() < 5) {
                        log.error("bapstring failed");
                    } else if (resolution != null && resolution.length() >= 5) {
                        double rate = similarityCalculator.calculateSemanticSimilarity(resolution, bapString);
                        if (rate < 0.50) {
                            log.info("Resolution Output: " + resolution);
                            log.info("bapString Summary: " + bapString);
                            log.error("bapString query not matching for situation " + _identifier + " similarity rate " + rate);
                            failureStatus = true;
                        }
                    }
                }

            } else {
                List<String> bapRempStep = aiopsDeepRCA.getRemediationData(_identifier);
                log.info("Combine the List into a single string");
                String combinedString = String.join("\n", bapRempStep);
                String similarFileJson = "msearchsimilarsituationquery.json";
                JSONObject situationrecord = hm_events.runMSearchQueryGetSituation(situationMessage, jwtToken, similarFileJson, 86400000);
                int similarrecord = situationrecord.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("total");
                if (similarrecord > 0) {
                    String resolution = cmdbrestapi.getIncidentFieldValue(incident_id, "Resolution");
                    if (incident_id.length() > 3 && resolution != null && resolution.length() >= 5 && combinedString.length() >= 5) {
                        double rate = similarityCalculator.calculateSemanticSimilarity(resolution, combinedString);
                        if (rate < 0.70) {
                            log.info("Resolution Output: " + resolution);
                            log.info("Remediation Summary: " + combinedString);
                            log.error("Prescanned query 5why not matching for situation " + _identifier + " similarity rate " + rate);
                            failureStatus = true;
                        }
                    }
                } else {
                    String llmmsg = "provide me the recommendation for the below issue reported by " + kindType + " monitoring agent\n";
                    llmmsg = llmmsg + joinedMessages;
                    llmOutput = OpenAIQuery.sendChatRequest(llmmsg);
                    if (llmOutput != null && llmOutput.length() >= 5 && combinedString.length() >= 5) {
                        double rate1 = similarityCalculator.calculateSemanticSimilarity(llmOutput, combinedString);
                        if (rate1 < 0.70) {
                            log.info("Resolution Output: " + llmOutput);
                            log.info("Remediation Summary: " + combinedString);
                            log.error("resolution does not matching for situation " + _identifier + " similarity rate " + rate1);
                            failureStatus = true;
                        }
                    }
                }
            }
            processCount += 1;

            if (processCount > 3){
                break;
            }
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
        }
        if (failureStatus) {
            Assert.fail("Test Failed please check log ");
        }
    }
}
