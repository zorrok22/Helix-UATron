package ITOM.Aiops;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.Api.OpenAIQuery;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.*;
import java.io.*;
import java.util.*;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;

public class AiopsSituationBARTest {
    private static final SolqaLogger log = new SolqaLogger(AiopsSituationBARTest.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
            System.getProperty("user.dir") + File.separator + "properties.config");
    CommonMethods commonmethod = new CommonMethods();
    Aiops aiopsApiCore = new Aiops();
    HMEvents hm_events = new HMEvents();
    public static final String event_tag = "";
    public static final String logInsightEndPoint = "/logs-service/api/v1.0/logs/log-insights/summary/";
    public static final String aiopsRunRanker = "/situation-best-action-recommender/api/v1.0/generate_ranker";
    public static final String helixGPTQueryendpoint = "/situation-best-action-recommender/api/v1.0/pre-canned-question";
    private String barRecommedationEndPoint = "/situation-best-action-recommender/api/v1.0/bar/";
    private String helixGPTSummaryEndPoint = "/situation-detector/api/v1.2/situation/description/";
    private SoftAssert softAssert = null;
    private ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();


    // Helper method to recursively extract fields from nested JSON
    private void extractFieldsRecursive(Object node, String field, List<String> result) {
        if (node instanceof JSONObject) {
            JSONObject obj = (JSONObject) node;
            if (obj.has(field)) {
                result.add(obj.getString(field));
            }
            for (String key : obj.keySet()) {
                extractFieldsRecursive(obj.get(key), field, result);
            }
        } else if (node instanceof JSONArray) {
            JSONArray arr = (JSONArray) node;
            for (int i = 0; i < arr.length(); i++) {
                extractFieldsRecursive(arr.get(i), field, result);
            }
        }
    }



    private List<String> readFileLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.warn("Could not read file: " + filePath + " - " + e.getMessage());
        }
        return lines;
    }

    public List<String> getRemediationData(String _identifier) {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String endPoint =  barRecommedationEndPoint + "steps/" + _identifier;

        try {
            logAnalyticsPage.Send_RestCall("GET", endPoint, new String[]{"200"});
            if (logAnalyticsPage.jsonObject == null) {
                softAssert.fail("No response for remediation data for situation " + _identifier);
                return null;
            }
            JSONObject json = logAnalyticsPage.jsonObject;
            JSONArray stepSummary = json.getJSONArray("steps");
            int stepCnt = stepSummary.length();
            if (stepCnt < 1 || stepCnt > 9) {
                softAssert.fail("situation " + _identifier + " Error remediation step too less or more " + stepCnt);
                return null;
            }
            List<String> details = new ArrayList<>();
            for (int i = 0; i < stepSummary.length(); i++) {
                JSONObject step = stepSummary.getJSONObject(i);
                details.add(step.optString("details", ""));
            }
            return details;
        } catch (Exception error) {
            log.info(error.getMessage());
            return null;
        }
    }

    public List<String> getSituationGPTSummary(String identifier) {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String endPoint = helixGPTSummaryEndPoint + identifier;
        try {
            // Assuming bhomapu is an instance with similar methods as in Python
            logAnalyticsPage.Send_RestCall("GET", endPoint, new String[]{"200"});
            if (logAnalyticsPage.jsonObject == null || logAnalyticsPage.jsonObject.length() < 3) {
                return null;
            }
            JSONObject json = logAnalyticsPage.jsonObject;
            String situationSummary = json.optString("situation_summary", null);
            String rootCauseClassification = json.optString("root_cause_classification", null);
            String rootCauseSummary = json.optString("root_cause_summary", null);

            if (logAnalyticsPage.response_code != 200) {
                return null;
            }

            return Arrays.asList(situationSummary, rootCauseClassification, rootCauseSummary);
        } catch (Exception error) {
            log.info(error.getMessage());
            return null;
        }
    }

    public String prescannQuery(String _identifier, int type) {
        // List of types as in the Python code
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String[] typeList = {
                "similar-situation-pattern",
                "change-window",
                "service-health-impact",
                "remediation-team",
                "five-why"
        };


        String jsonfile = "helixgptquerypre.json";
        String jsonconent = aiopsApiCore.readJSon(jsonfile);
        jsonconent = jsonconent.replace("<typeList>",typeList[type]).replace("<_identifier>",_identifier);
        try {
            // Assuming LogAnalyticsPage.Send_RestCall sets LogAnalyticsPage.jsonBody as response JSON string
            logAnalyticsPage.Send_RestCall("POST", helixGPTQueryendpoint, new String[]{"200", jsonconent});
            JSONObject json = new JSONObject(logAnalyticsPage.jsonObject);
            String rescanSummary = json.optString("answer", "");
            if (rescanSummary.contains("Sorry")) {
                return "Failed";
            } else {
                return rescanSummary;
            }
        } catch (Exception e) {
            log.info("prescannQuery error: " + e.getMessage());
            return "Failed";
        }
    }

    public List<String> getBarTools(String sourceIdentifier, String identifier) throws IOException, InterruptedException {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        List<String> expectedList = Arrays.asList(
                "metric tool",
                "log tool",
                "code tool",
                "document tool",
                "change tool"
        );
        List<String> returnList = new ArrayList<>();
        String endPoint = barRecommedationEndPoint + sourceIdentifier;
        // Assuming LogAnalyticsPage.retryRestCall is available if needed
        // LogAnalyticsPage.retryRestCall = false; // Uncomment if such a field exists
        logAnalyticsPage.Send_RestCall("GET", endPoint, new String[]{"200"});
        // LogAnalyticsPage.retryRestCall = true; // Uncomment if such a field exists

        String barMessage = "";
        if (logAnalyticsPage.jsonObject == null || logAnalyticsPage.response_code > 299) {
            return null;
        }

        JSONObject jsonBody = logAnalyticsPage.jsonObject;
        JSONArray bar = jsonBody.getJSONArray("best_action_plan");

        for (int i = 0; i < bar.length(); i++) {
            JSONObject data = bar.getJSONObject(i);
            String task = data.optString("task", "");
            barMessage += " Task " + task;

            // Extract all "tool" fields from steps (recursive)
            List<String> tools = new ArrayList<>();
            List<String> actions = new ArrayList<>();
            if (data.has("steps")) {
                extractFieldsRecursive(data.get("steps"), "tool", tools);
                extractFieldsRecursive(data.get("steps"), "action", actions);
            }

            Set<String> expectedSet = new HashSet<>(expectedList);
            barMessage += "\n tools Name ";
            for (String item : tools) {
                barMessage += "\n" + item;
                if (!expectedSet.contains(item)) {
                    log.error("'" + item + "' not found " + sourceIdentifier);
                }
            }

            barMessage += "\n Tools Action ";
            for (String item : actions) {
                barMessage += "\n" + item;
            }
        }

        // Optionally log or use barMessage as needed
        log.info(barMessage);
        returnList.add(jsonBody.toString());
        returnList.add(barMessage);

        return returnList;
    }

    private Map<String, List<String>> parseNoneInitFile(String filename) {
        Map<String, List<String>> sections = new HashMap<>();
        String currentSection = null;
        Random random = new Random();

        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = file.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    String sectionName = line.substring(1, line.length() - 1);
                    sections.put(sectionName, new ArrayList<>());
                    currentSection = sectionName;
                } else if (!line.isEmpty() && currentSection != null) {
                    sections.get(currentSection).add(line);
                }
            }
        } catch (FileNotFoundException e) {
            log.info("File '" + filename + "' not found.");
        } catch (IOException e) {
            log.info("Error reading file '" + filename + "': " + e.getMessage());
        }

        return sections;
    }


    @Test(description = "[BAR]:Verify Best action recommendations and remediation for ML situations", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12476", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12476_Verify_Best_action_recommendations_and_remediation_for_ML_situations(ITestContext context) throws Exception {
        this.softAssert = null;
        this.softAssert = new SoftAssert();
        HMEvents hm_events = new HMEvents();
        Aiops aiopsApiCore = new Aiops();
        int processCount = 0;
        SemanticSimilarityONNX similarityCalculator = new SemanticSimilarityONNX();
        int testcase_number = 1;
        String jwtToken = commonmethod.refreshJwtToken(null);
        String mlSheetName = "gpt_data";
        log.info("Read the gpt_data");

        
        
        
        if(!aiopsApiCore.getTenantFeatureFlagStatus("BAR", jwtToken)){
            log.info("Feature BAR is not enabled; skipping test as passed.");
            return;
        }
        

        if(aiopsApiCore.getTenantFeatureFlagStatus("BAP", jwtToken)){
            log.info("BAP is enabled; skipping test as passed.");
            return;
        }

        String llmQuery = "provide me the recommendation for the below issue reported by <KINDTYPE> monitoring agent \n";
        String[] excludeArray = {"TC", "Feature", "tenant_name", "column", "solution"};
        String eventMessageFile = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "AIOPS" + File.separator + "event_message.txt";
        List<String> eventMessages = readFileLines(eventMessageFile);

        List<Map<String, Object>> row_data = excelReaderForMLSituation.getTestDataFromExcel(testcase_number, mlSheetName);
        Set<String> firstRowKeys = row_data.get(0).keySet();
        Set<String> filteredKeys = firstRowKeys.stream()
                .filter(key -> !Arrays.asList(excludeArray).contains(key))
                .collect(Collectors.toSet());
        filteredKeys.removeIf(s -> s.contains("_"));

        log.info("get situation List");
        String filename = "msearchsituationqueryall.json";
        
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
            log.info("get the BAR summary");

            List<String> BarSummary = getSituationGPTSummary(source_identifier);
            log.info("BAR Summary " + (BarSummary != null && !BarSummary.isEmpty() ? BarSummary.get(0) : "null"));
            log.info("Find sematic Similarity");

            // Validate before calling similarityCalculator
            if (BarSummary == null || BarSummary.get(0) == null || BarSummary.get(0).length() < 5 || llmOutput == null || llmOutput.length() < 5) {
                Assert.fail("Summary GPT or LLM Output not available or too short");
            } else {
                double rate = similarityCalculator.calculateSemanticSimilarity(llmOutput, BarSummary.get(0));
                if (rate < 0.70) {
                    log.info("LLM Output: " + llmOutput);
                    log.info("BAR Summary: " + BarSummary.get(0));
                    softAssert.fail("BAR summary not matching for situation " + _identifier + " similarity rate " + rate);

                }
            }

            String cmdbUrl = config.get("CMDBURL");
            String username = config.get("ADE_USERNAME");
            String password = config.get("ADE_PASSWORD");
            RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
            String authtoken = cmdbauthToken.getRemedyApiKey(username, password);
            CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);


            log.info("Validate bar recommendation");
            String barllmQuery = "provide me the recommendation step for the below issue reported by <KINDTYPE> monitoring agent \n";
            List<String> barMessage = getRemediationData(_identifier);
            if(barMessage == null){
                softAssert.fail("Fail to get the Bar message");
                continue;
            }
            barllmQuery = barllmQuery.replace("<KINDTYPE>", kindList.get(serviceName));
            barllmQuery = barllmQuery + row_data.get(0).get(kindType + "_column").toString();
            barllmQuery = OpenAIQuery.sendChatRequest(barllmQuery);
            if (barllmQuery != null && barllmQuery.length() >= 5) {
                String barString = String.join(" \n ", barMessage);
                double rate = similarityCalculator.calculateSemanticSimilarity(barllmQuery, barString);
                if (rate < 0.70) {
                    log.info("Resolution Output: " + barllmQuery);
                    log.info("barString Summary: " + barString);
                    softAssert.fail("barString query not matching for situation " + _identifier + " similarity rate " + rate);

                }
            }

            String resolution = "";
            String prescanSummary = "";


            log.info("Validate Pre Scanned Query for remaining option");
            for (int i = 0; i < 4; i++) {
                prescanSummary = prescannQuery(_identifier, i);
                if (prescanSummary.equals("Failed")) {
                    softAssert.fail("Prescanned query " + i + " failed for situation " + _identifier);

                }
            }
            log.info("Validate resolution information is present in BAR");
            List<String> BarTools = getBarTools(source_identifier, _identifier);
            if (BarTools != null) {
                String barString = BarTools.get(1);
                if (barString == null || barString.length() < 5) {
                    log.error("barstring failed");
                } else if (resolution != null && resolution.length() >= 5) {
                    double rate = similarityCalculator.calculateSemanticSimilarity(resolution, barString);
                    if (rate < 0.50) {
                        log.info("Resolution Output: " + resolution);
                        log.info("barString Summary: " + barString);
                        softAssert.fail("barString query not matching for situation " + _identifier + " similarity rate " + rate);

                    }
                }
            }


            processCount += 1;

            if (processCount > 3){
                break;
            }
            jwtToken = commonmethod.refreshJwtToken(jwtToken);
        }
        softAssert.assertAll();
    }
}
