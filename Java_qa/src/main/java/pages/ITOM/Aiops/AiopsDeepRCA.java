package pages.ITOM.Aiops;
import ai.onnxruntime.OrtException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pages.ITOM.Aiops.ADESituationHelixGPT;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Api.OpenAIQuery;
import pages.ITOM.Api.SemanticSimilarityONNX;
import pages.ITSM.Api.CMDBRestApi;
import pages.ITSM.Api.RemedyApiClient;
import util.*;
import io.restassured.http.Cookies;
import org.openqa.selenium.Cookie;
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
import org.json.JSONArray;
import org.json.JSONObject;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static pages.ITOM.Aiops.ADESituationHelixGPT.openSituationHelixGPT;

public class AiopsDeepRCA {
    private static final SolqaLogger log = new SolqaLogger(AiopsDeepRCA.class.getName());
    private static final Map<String, String> config = ReadPropertiesFile.readProperties(
        System.getProperty("user.dir") + File.separator + "properties.config");
    CommonMethods commonmethod = new CommonMethods();
    CommonWaits commonwait = new CommonWaits();
    Aiops aiopsApiCore = new Aiops();
    HMEvents hm_events = new HMEvents();
    private static String hmHostName = config.get("ADE_PLATFORM_URL");
    public static final String event_tag = "";
    public static final String logInsightEndPoint = "/logs-service/api/v1.0/logs/log-insights/summary/";
    public static final String aiopsRunRanker = "/situation-best-action-recommender/api/v1.0/generate_ranker";
    public static final String helixPrescanQueryendpoint = "/situation-best-action-recommender/api/v1.0/pre-canned-question";
    private static String submitter = null;
    private static String assignee_groups = null;
    private String bhomSendEvent = "/events-service/api/v1.0/events";
    private String bhomIdentifierbased = "msearchidquery.json";
    private String bapRecommedationEndPoint = "/situation-best-action-recommender/api/v1.0/bap/";
    private String helixGPTSummaryEndPoint = "/situation-detector/api/v1.2/situation/description/";
    private String helixGPTDeepRCAEndPoint = "/situation-best-action-recommender/api/v1.0/deep-rca/";
    private String helixGPTDeepRCAStatusEndPoint = "/situation-best-action-recommender/api/v1.0/deep-rca/status/";
    private String situationRCAEndPoint = "/service-management/api/v1.0/service/situation/get";
    private static final String CRAInsightEndPoint = "/change-risk-analysis/api/v1.1/insights/";
    private static final String CRAGPTInsightsEndPoint = "/change-risk-analysis/api/v1.0/gpt-insights/";
    private static final String CRAListEndPoint = "/service-management/api/v1.0/helixgpt/assistant/agents/CRA/invoke";
    private static final String HELIXGPTQueryEndPoint = "/situation-best-action-recommender/api/v1.0/natural-language-question";
    private static final ObjectMapper mapper = new ObjectMapper();
    private String resolutionfieldValue = "Reduce memory usage by stopping Patrol Agent. deleted log from patrol agent log folder. deleted history file from patrol Agent History folder. Stop the Patrol Agent running on different port. Clear swap using sudo swapoff -a, sudo swapon -a. Now Memory utilization on Linux system is below 30%";
    String[] OldfeatureList;
    String[] newfeatureList;
    String authtoken = null;
    String cmdbUrl = null;
    private String jwtToken = null;
    private String xsrfTokenValue = null;
    private Cookies restAssuredCookies = null;
    private static final String FEATURE_LIST_FILE = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "AIOPS" + File.separator + "deepRCAFeatureFlagLists.txt";
    private static ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();

    public AiopsDeepRCA() {
        loadFeatureLists();
    }

    private void loadFeatureLists() {
        List<String> oldFeatures = new ArrayList<>();
        List<String> newFeatures = new ArrayList<>();
        File file = new File(FEATURE_LIST_FILE);
        if (!file.exists()) {
            log.warn("Feature list file not found at " + FEATURE_LIST_FILE + "; using defaults");
            Assert.fail("Failed to read feature list file: ");
            return;
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                if (trimmed.startsWith("OLDDEEPRCAFF=")) {
                    oldFeatures = parseFeatureLine(trimmed.substring(13));
                } else if (trimmed.startsWith("NEWDEEPRCAFF=")) {
                    newFeatures = parseFeatureLine(trimmed.substring(13));
                }
            }
            if (oldFeatures.isEmpty() || newFeatures.isEmpty()) {
                log.warn("Feature list entries missing or empty; using defaults");
                Assert.fail("Failed to read feature list file: ");
                return;
            }
            OldfeatureList = oldFeatures.toArray(new String[0]);
            newfeatureList = newFeatures.toArray(new String[0]);
            log.info("Loaded feature lists from file");
        } catch (IOException e) {
            log.error("Failed to read feature list file: " + e.getMessage());
            Assert.fail("Failed to read feature list file: " + e.getMessage());
        }
    }

    private List<String> parseFeatureLine(String line) {
        List<String> list = new ArrayList<>();
        for (String part : line.split(",")) {
            String val = part.trim();
            if (!val.isEmpty()) {
                list.add(val);
            }
        }
        return list;
    }


    /**
     * Sends log agent events equivalent to the provided bash script, using values
     * sourced from the ML Excel sheet. Falls back to the bash defaults when a column
     * is missing. Expected column names (optional): HOST, PORT, QUERY, LOG_SOURCE_HOST,
     * SOURCE_HOSTNAME, INTERVAL_SECONDS, TOTAL_DURATION_SECONDS, DISK_MIN, DISK_MAX,
     * MEM_MIN, MEM_MAX.
     */

    public List<String> sendLogAgentEventsFromExcel(int testcaseNumber, String mlSheetName, String hostname) throws Exception {
        int logpush_TC = 1;
        String CONFIG_SHEET = "LogConfiguration";
        try {
            List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(logpush_TC, CONFIG_SHEET);
            if (rows.isEmpty()) {
                throw new IllegalStateException("No Clickhouse config found in sheet '" + CONFIG_SHEET + "' for TC " + logpush_TC);
            }
            
            String config = rows.get(0).get("Setting").toString();
            if(config.equalsIgnoreCase("ClickHouse")) {
                return ClickhouseInsertData(testcaseNumber, mlSheetName, hostname);
            } else if(config.equalsIgnoreCase("LogAnalytics")) {
                return LogAnalyticsInsertData(testcaseNumber, mlSheetName, hostname);
            } else {
                log.error("Invalid config: " + config);
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read Clickhouse config from Excel sheet '" + CONFIG_SHEET + "'", e);
        }
    }

    public List<String> LogAnalyticsInsertData(int testcaseNumber, String mlSheetName, String hostname) throws Exception {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        
        List<Map<String, Object>> logdatas = excelReaderForMLSituation.getTestDataFromExcel(testcaseNumber, mlSheetName);
        List<String> log_messages = new ArrayList<>();
        StringBuilder log_send = new StringBuilder();
        for (Map<String, Object> log_data : logdatas) {
            String msg = log_data.get("message").toString();
            log_send.append(" ").append(msg);
            log_messages.add(msg);
            logAnalyticsPage.logPush(msg, hostname);
        }
        return log_messages;
        
    }

    public List<String> ClickhouseInsertData(int testcaseNumber, String mlSheetName, String hostname) throws Exception {
        Map<String, Object> clickhouseConfig = getClickhouseConfig();
        String host = getString(clickhouseConfig, "ssh_host", "129.213.105.84");
        int port = (int) Double.parseDouble(getString(clickhouseConfig, "port", "64226"));
        String query = getString(clickhouseConfig, "QUERY", "INSERT INTO log_analytics.log_1994262954 FORMAT JSONEachRow");
        String username = getString(clickhouseConfig, "ssh_user", "test");
        String password = getString(clickhouseConfig, "ssh_password", "test");
        
        

        List<Map<String, Object>> logdatas = excelReaderForMLSituation.getTestDataFromExcel(testcaseNumber, mlSheetName);
        List<String> log_messages = new ArrayList<>();
        StringBuilder log_send = new StringBuilder();
        for (Map<String, Object> log_data : logdatas) {
            String msg = log_data.get("message").toString();
            log_messages.add(msg);
            msg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
            log_send.append(" ").append(msg);
            log.info(msg);
            String ts = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            JSONObject payload = new JSONObject();
            payload.put("log_source_host", hostname);
            payload.put("message", msg);
            payload.put("source_hostname", hostname);
            //payload.put("ts", ts);
            String url = String.format("https://%s:%d", host, port);
            
            log.info("Log agent URL: " + url);
            for (int attempt = 0; attempt < 3; attempt++) {
                try {
                    // Prefer TLS 1.2: many internal services (e.g. ClickHouse HTTPS) do not negotiate TLS 1.3;
                    // forcing TLSv1.3 often yields "Remote host terminated the handshake".
                    RestAssuredConfig config = RestAssured.config()
                        .sslConfig(new SSLConfig()
                            .relaxedHTTPSValidation("TLSv1.2")
                            .allowAllHostnames());

                    Response response = RestAssured.given()
                        .config(config)
                        .auth()
                        .preemptive()
                        .basic(username,password)    // Equivalent to -u test:test
                        .contentType(ContentType.JSON)
                        .queryParam("query", query)
                        .body(payload.toString())        // Sends the string payload
                        .log().all()              // Equivalent to -v (logs request details)
                        .when()
                        .post(url)
                        .then()
                        .log().all()              // Logs response details
                        .extract().response();
                    log.info(String.format("[LOG AGENT] -> %s [HTTP %d]", msg, response.getStatusCode()));
                    log.info("Log agent response: " + response.getBody().asString());
                    if (response.getStatusCode() > 299) {
                        if (attempt < 2) {
                            log.warn("Log agent failed on attempt " + (attempt + 1) + "/3, retrying... Error: " + response.getStatusCode());
                            commonwait.waitForSometime(2000);
                        } else {
                            log.error("Log agent failed after 3 attempts: " + response.getStatusCode());
                            return new ArrayList<>();
                        }
                    }
                    commonwait.waitForSometime(2000);
                    break;
                } catch (Exception e) {
                    if (attempt < 2) {
                        log.warn("Log agent failed on attempt " + (attempt + 1) + "/3, retrying... Error: " + e.getMessage());
                        commonwait.waitForSometime(2000);
                    } else {
                        log.error("Log agent failed after 3 attempts: " + e.getMessage());
                        return new ArrayList<>();
                    }
                }
            }

        }

        return log_messages;
    }

    private String getString(Map<String, Object> row, String key, String defaultValue) {
        Object value = row.get(key);
        return value == null ? defaultValue : value.toString();
    }


    private Map<String, Object> getClickhouseConfig() {
        int CLICKHOUSE_CONFIG_TC = 12111;
        String CLICKHOUSE_CONFIG_SHEET = "clickhouseconfig";
        try {
            List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(CLICKHOUSE_CONFIG_TC, CLICKHOUSE_CONFIG_SHEET);
            if (rows.isEmpty()) {
                throw new IllegalStateException("No Clickhouse config found in sheet '" + CLICKHOUSE_CONFIG_SHEET + "' for TC " + CLICKHOUSE_CONFIG_TC);
            }
            return rows.get(0);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read Clickhouse config from Excel sheet '" + CLICKHOUSE_CONFIG_SHEET + "'", e);
        }
    }

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




    public List<String> readFileLines(String filePath) {
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
        String endPoint =  bapRecommedationEndPoint + "steps/" + _identifier;

        try {
            logAnalyticsPage.jsonObject = null;
            logAnalyticsPage.Send_RestCall("GET", endPoint, new String[]{"200"});
            if (logAnalyticsPage.jsonObject == null) {
                log.error("No response for remediation data for situation " + _identifier);
                return null;
            }
            JSONObject json = new JSONObject(logAnalyticsPage.jsonObject.toString());
            JSONArray stepSummary = json.getJSONArray("steps");
            int stepCnt = stepSummary.length();
            if (stepCnt < 1 || stepCnt > 9) {
                log.error("situation " + _identifier + " Error remediation step too less or more " + stepCnt);
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

    public List<String> getBapTools(String sourceIdentifier, String identifier) throws IOException, InterruptedException {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        List<String> expectedList = Arrays.asList(
                "metric tool",
                "log tool",
                "code tool",
                "document tool",
                "change tool"
        );

        List<String> returnList = new ArrayList<>();
        String endPoint = bapRecommedationEndPoint + sourceIdentifier;
        // Assuming LogAnalyticsPage.retryRestCall is available if needed
        // LogAnalyticsPage.retryRestCall = false; // Uncomment if such a field exists
        logAnalyticsPage.Send_RestCall("GET", endPoint, new String[]{"200"});
        // LogAnalyticsPage.retryRestCall = true; // Uncomment if such a field exists

        String bapMessage = "";
        if (logAnalyticsPage.jsonObject == null || logAnalyticsPage.response_code > 299) {
            return null;
        }

        JSONObject jsonBody = logAnalyticsPage.jsonObject;
        JSONArray bap = jsonBody.getJSONArray("best_action_plan");

        for (int i = 0; i < bap.length(); i++) {
            JSONObject data = bap.getJSONObject(i);
            String task = data.optString("task", "");
            bapMessage += " Task " + task;

            // Extract all "tool" fields from steps (recursive)
            List<String> tools = new ArrayList<>();
            List<String> actions = new ArrayList<>();
            if (data.has("steps")) {
                extractFieldsRecursive(data.get("steps"), "tool", tools);
                extractFieldsRecursive(data.get("steps"), "action", actions);
            }

            Set<String> expectedSet = new HashSet<>(expectedList);
            bapMessage += "\n tools Name ";
            for (String item : tools) {
                bapMessage += "\n" + item;
                if (!expectedSet.contains(item)) {
                    log.error("'" + item + "' not found " + sourceIdentifier);
                }
            }

            bapMessage += "\n Tools Action ";
            for (String item : actions) {
                bapMessage += "\n" + item;
            }
        }

        // Optionally log or use bapMessage as needed
        log.info(bapMessage);
        returnList.add(jsonBody.toString());
        returnList.add(bapMessage);

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
            System.out.println("File '" + filename + "' not found.");
        } catch (IOException e) {
            System.out.println("Error reading file '" + filename + "': " + e.getMessage());
        }

        return sections;
    }

    private String readRandomLine(String sectionName) {
        String filename = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "AIOPS" + File.separator + "event_message.txt";
        Map<String, List<String>> sections = parseNoneInitFile(filename);

        if (sections.isEmpty()) {
            return "No sections found.";
        }

        Random random = new Random();

        if (sectionName != null && !sectionName.isEmpty()) {
            if (sections.containsKey(sectionName)) {
                List<String> lines = sections.get(sectionName);
                if (!lines.isEmpty()) {
                    return lines.get(random.nextInt(lines.size()));
                } else {
                    return "Section '" + sectionName + "' is empty.";
                }
            } else {
                return "No";
            }
        } else {
            List<String> sectionNames = new ArrayList<>(sections.keySet());
            String randomSection = sectionNames.get(random.nextInt(sectionNames.size()));
            List<String> lines = sections.get(randomSection);
            String randomLine = lines.get(random.nextInt(lines.size()));
            return "Section: " + randomSection + "\nLine: " + randomLine;
        }
    }

    private String findReplaceValueMsg(String text, String newValue) {
        Pattern pattern = Pattern.compile("\\{\\w+\\}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return text.replaceAll("\\{\\w+\\}", newValue);
        } else {
            return text;
        }
    }

    public String createITSMChangeEvent(String serviceName, String title, String description) throws Exception {
        String jsonFile = "createchangeevent.json";
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String jsonContent = hm_events.readJSon(jsonFile);
        jsonContent = jsonContent.replace("<ServiceName>", serviceName);
        jsonContent = jsonContent.replace("<CHANGEID>", "CRQ000" + randomNumber);
        jsonContent = jsonContent.replace("<Summary>", title);
        jsonContent = jsonContent.replace("<Details>", description);
        long epochMillisUtcend = java.time.Instant.now().toEpochMilli() - 60*1000;
        long epochMillisUtcstart = java.time.Instant.now().toEpochMilli() - 60*60*1000;
        jsonContent = jsonContent.replace("<EPOCH_START>", String.valueOf(epochMillisUtcstart));
        jsonContent = jsonContent.replace("<EPOCH_END>", String.valueOf(epochMillisUtcend));
        String[] arry = {"200", jsonContent};
        
        String cmdbUrl = config.get("CMDBURL");
        String username = config.get("ADE_USERNAME");
        String password = config.get("ADE_PASSWORD");



        RemedyApiClient remedyApiClient = new RemedyApiClient(cmdbUrl);
            String authToken = remedyApiClient.getRemedyApiKey(username, password);
            CMDBRestApi cmdbRestApi = new CMDBRestApi(cmdbUrl, authToken);
            log.info("Initialized Remedy and CMDB clients");

        cmdbRestApi.setCreatebackdatatedChange(true);
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        Map<String, String> ciTopology = aiopsApiCore.aiopsGetCITopology(serviceName, this.jwtToken);
        List<String> hostCiIds = new ArrayList<>();
        for (Map.Entry<String, String> entry : ciTopology.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && key.endsWith("_kind") && "Host".equalsIgnoreCase(value)) {
                String baseName = key.substring(0, key.length() - "_kind".length());
                String ciId = ciTopology.get(baseName);
                if (ciId != null && !ciId.isEmpty()) {
                    hostCiIds.add(ciId);
                }
            }
        }
        String[] ciList = hostCiIds.toArray(new String[0]);

        Map<String, String> changeIdentifiers = cmdbRestApi.createChangeRequestUpdateStatus(
            title, description, serviceName, ciList, username, "US Support 3");
        log.info("jsonContent: " + jsonContent);

        if(changeIdentifiers == null){
            return null;
        }

        if(!moveChangeTorequestStatus(changeIdentifiers,"Implementation In Progress")){
            Assert.fail("Unable to update change status");
        }
        //logAnalyticsPage.Send_RestCall("POST", bhomSendEvent, arry);
        //logAnalyticsPage.jsonObject.getJSONArray("resourceId").getString(0)
        try {
            RemedyApiClient remedyStatusApiClient = new RemedyApiClient(cmdbUrl);
            String StatusauthToken = remedyStatusApiClient.getRemedyApiKey(changeIdentifiers.get("cabmanagerlogin"), "__USER_PASSWORD__");;
            CMDBRestApi cmdbStatusRestApi = new CMDBRestApi(cmdbUrl, StatusauthToken);
            log.info("Initialized Remedy and CMDB clients");
            log.info("changeIdentifiers: " + changeIdentifiers);
            ZonedDateTime actualStartTimeUtc = ZonedDateTime.now(ZoneOffset.UTC);
            ZonedDateTime actualEndTimeUtc = actualStartTimeUtc.plusMinutes(5);
            DateTimeFormatter remedyTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.000+0000'");
            cmdbStatusRestApi.moveChangeStatusComplete(
                    changeIdentifiers.get("infrachangeid"),
                    changeIdentifiers.get("RequestID"),
                    actualStartTimeUtc.format(remedyTimeFormatter),
                    actualEndTimeUtc.format(remedyTimeFormatter));
            log.info("Moved change " + changeIdentifiers.get("infrachangeid") + " to Completed status");
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        
        return changeIdentifiers.get("infrachangeid");
    }


    public String createChangeBhomEvent(String serviceName, String situationSummary, String featureType) throws Exception {
        String requestQuery = "Return your response in strict JSON format with the following keys: 'title', 'description'. Do not include any extra text outside the JSON.\n";
        String llmmsg = "Suggest a short summary for change request, which I can raise for the below issue reported by " + featureType + " monitoring.\n" + requestQuery;
        llmmsg = llmmsg + situationSummary;
        String llmOutput = OpenAIQuery.sendChatRequest(llmmsg);
        ObjectMapper mapper = new ObjectMapper();
        String title = "";
        String description = "";
        llmOutput = llmOutput.replace("```json", "");
        llmOutput = llmOutput.replace("```", "");
        try {
            JsonNode jsonNode = mapper.readTree(llmOutput);

            title = jsonNode.has("title") ? jsonNode.get("title").asText() : title;
            description = jsonNode.has("description") ? jsonNode.get("description").asText() : description;

            System.out.println("Title: " + title);
            System.out.println("Description: " + description);

        } catch (Exception e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
        }

        if (title.length() < 3) {
            return null;
        }
        
        String changeId = createITSMChangeEvent(serviceName, title, description);
        return changeId;
    }

    public void updateITSMResolutionField(String incidentID, String kindType, List<Map<String, Object>> row_data, List<String> msgList, String llmOutput) throws Exception {
        if (authtoken == null) {
            cmdbUrl = config.get("CMDBURL");
            String username = config.get("ADE_USERNAME");
            String password = config.get("ADE_PASSWORD");
            RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
            authtoken = cmdbauthToken.getRemedyApiKey(username, password);
        }
        String serviceName = kindType;
        CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);
        if (submitter == null) {
            assignee_groups = cmdbrestapi.getIncidentFieldValue(incidentID, "Assignee Groups");
            submitter = cmdbrestapi.getIncidentFieldValue(incidentID, "Submitter");
        }
        if (llmOutput == null) {
            if (row_data == null || row_data.isEmpty()) {
                throw new IllegalArgumentException("Row data is empty");
            }
            serviceName = row_data.get(0).get(kindType).toString();
            String llmmsg = row_data.get(0).get(kindType + "_solution").toString();
            llmmsg = llmmsg + "\n" + String.join("\n", msgList);
            log.info("LLM Message: " + llmmsg);
            llmOutput = OpenAIQuery.sendChatRequest(llmmsg);
        }
        String requestId = null;
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                requestId = cmdbrestapi.getFromIncidentInterfaceFieldValue(incidentID, "Request ID");
                break;
            } catch (Exception ex) {
                if (attempt == 0) {
                    log.warn("Failed to fetch Request ID on attempt 1, retrying... Error: " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
        }
        if (requestId == null) {
            throw new IllegalStateException("Unable to fetch Request ID for incident " + incidentID);
        }
        cmdbrestapi.updateResolutionField(requestId, serviceName, llmOutput);
    }

    public String getLogInsightSummaryData(String situationId) throws IOException, InterruptedException {
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        CommonWaits commonWait = new CommonWaits();
        String endpoint = logInsightEndPoint + situationId + "?idType=situation&forceRefresh=false";
        logAnalyticsPage.Send_RestCall("GET", endpoint, new String[]{"200"});
        String requestID = logAnalyticsPage.jsonObject.getString("requestId");
        for(int i = 0;i<20;i = i + 1){
            endpoint = logInsightEndPoint + requestID + "?idType=request&forceRefresh=false";
            logAnalyticsPage.Send_RestCall("GET", endpoint, new String[]{"200"});
            String status = logAnalyticsPage.jsonObject.getString("status");
            if (status.compareToIgnoreCase("SUCCESS") == 0){

                return logAnalyticsPage.jsonObject.getJSONArray("cards").getJSONObject(0).getString("summary");
            }
            commonWait.waitForSometime(5000);
        }
        return null;
    }

    public void createBAPSituation(String kindType, List<Map<String, Object>> row_data) throws Exception, InterruptedException {
        List<String> changeEventList = new ArrayList<String>();
        Aiops aiopsApiCore = new Aiops();
        CommonWaits commonWait = new CommonWaits();
        JSONObject serviceObject = null;
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        String location = "Pune";
        int no_of_event_per_node = 1;
        String eventClass = "NAGIOS_EV1";
        HMEvents hm_events = new HMEvents();
        if (!aiopsApiCore.enableMLSituationsFeature(this.jwtToken)) {
            Assert.fail("Failed to enable ml-situations feature.");
        }
        List<String> msgList = new ArrayList<String>();
        List<String> eventList = new ArrayList<String>();
        String[] hostList = row_data.get(0).get(kindType + "_column").toString().split(",");
        String serviceName = row_data.get(0).get(kindType).toString();
        try {
            aiopsApiCore.searchAndGetServiceDetails(serviceName, this.jwtToken);
        } catch (Exception e) {
            return;
        }
        log.info("Kind Type: " + kindType + " Service Name " + serviceName + " host List: " + Arrays.toString(hostList));
        Map<String, String> healthScore = aiopsApiCore.getHealthScore(serviceName, this.jwtToken);
        if (hostList.length < 1) {
            return;
        }
        boolean createChange = true;

        for (String hostName : hostList) {
            String msg = readRandomLine(kindType);
            msg = findReplaceValueMsg(msg, hostName);
            if (msg.length() < 5) {
                continue;
            }
            log.info(msg);
            msgList.add(msg);
            log.info("Create a Change Event");
            if (createChange) {
                createChange = false;
                String event_id = createChangeBhomEvent(serviceName, msg, kindType);
                if (event_id != null) {
                    changeEventList.add(event_id);
                }
            }
            String responseString = hm_events.streamRestApiEvents(eventClass, no_of_event_per_node, this.jwtToken, hostName, msg, "CRITICAL", location, event_tag);
            JSONObject eventid = new JSONObject(responseString);
            String sendEventId = eventid.getJSONObject("0").getJSONArray("resourceId").getString(0);
            eventList.add(sendEventId);

            this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        }
        log.info("Waiting for situation");
        aiopsApiCore.waitForSituationCreationMsearch(healthScore.get("key"), this.jwtToken, 180000);
        commonWait.waitForSometime(15000);
        log.info("wait for incident creation max for 1 min");
        String incidentId = "";
        for (int j = 0; j < 6; j += 1) {
            JSONObject situationObject = aiopsApiCore.getSituationsForService(serviceName, this.jwtToken);
            String situationIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).optString("_identifier", "");
            if (situationIdentifier.length() < 3) {
                commonWait.waitForSometime(10000);
                continue;
            }
            incidentId = situationObject.getJSONArray("eventList").getJSONObject(0).getString("incident_id");
            if (incidentId.length() < 3) {
                commonWait.waitForSometime(10000);
            } else {
                break;
            }
        }
        if (incidentId.length() > 3) {
            updateITSMResolutionField(incidentId, kindType, row_data, msgList, null);
        }

        for (String eventid : eventList) {
            hm_events.closeEventApi(eventid, "Close Event", this.jwtToken);
        }

        for (String eventid : changeEventList) {
            hm_events.closeEventApi(eventid, "Close Event", this.jwtToken);
        }
        changeEventList.clear();
        eventList.clear();
        JSONObject situationObject = aiopsApiCore.getSituationsForService(serviceName, this.jwtToken);
        String situationIdentifier = situationObject.getJSONArray("eventList").getJSONObject(0).getString("_identifier");
        hm_events.closeEventApi(situationIdentifier, "Close Event", this.jwtToken);

    }

    public boolean triggerDeepRCA(String situationIdentifier) throws Exception {
        RestAssured.baseURI = hmHostName;
        String endpoint = helixGPTDeepRCAEndPoint + situationIdentifier + "?force=false";
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .body("{}")
                .post(endpoint);
        log.info(resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Trigger deep RCA failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }
    public JSONObject getDeepRCAResult(String situationIdentifier) throws Exception {
        String endpoint = helixGPTDeepRCAStatusEndPoint + situationIdentifier + "?dataformat=dd/MM/yyyy&timeformat=HH:mm&timezone=America/Los_Angeles";
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .get(endpoint);
        log.info("Get deep RCA result: " + resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get deep RCA result failed: " + resp.getStatusCode());
            return null;
        }
        String jsonString = getValidJsonOrNull(resp.getBody().asString());
        if (jsonString == null) {
            log.error("Get deep RCA result failed: Invalid JSON");
            return null;
        }
        return new JSONObject(jsonString);
    }


    public boolean getprofileleveltoken() throws Exception{
        LogAnalyticsPage loganalyticspage = new LogAnalyticsPage();
        WebDriver driver = null;
        try{
            driver = loganalyticspage.setuserAccessSecretKey();
        }catch(Exception e){
            log.error("Get profile level token failed: " + e.getMessage());
            return false;
        }
        finally {
            if (driver != null) {
                driver.quit();
                log.info("WebDriver quit");
            }
        }
        this.jwtToken = commonmethod.refreshJwtToken(null);
        return true;
    }

    public static String getValidJsonOrNull(String jsonString) {
        try {
            // readTree validates the structure of the JSON string
            mapper.readTree(jsonString);
            if(jsonString.length() < 3) {
                log.error("Get valid JSON failed: JSON is empty");
                return null;
            }
            return jsonString;
        } catch (JsonProcessingException | IllegalArgumentException e) {
            // Returns null if the string is not valid JSON
            return null;
        }
    }

    public JSONObject updateDeepRCA(String situationIdentifier) throws Exception {
        String endpoint = "/situation-detector/api/v1.2/situation/description/" + situationIdentifier + "?dataformat=dd/MM/yyyy&timeformat=HH:mm&timezone=Africa/Abidjan";
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer "+this.jwtToken)
            .header("X-XSRF-TOKEN", xsrfTokenValue)
            .cookies(this.restAssuredCookies)
            .when()
            .get(endpoint);
        log.info("Update deep RCA: " + resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Update deep RCA failed: " + resp.getStatusCode());
            return null;
        }
        String jsonString = getValidJsonOrNull(resp.getBody().asString());
        if (jsonString == null) {
            log.error("Update deep RCA failed: Invalid JSON");
            return null;
        }
        return new JSONObject(jsonString);
    }

    public JSONObject getSituationRCA(String situationIdentifier, String jwtToken) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("situationRawId", situationIdentifier);
        payload.put("maxNodes", 10);
        RestAssured.baseURI = hmHostName;
        Response resp = null;
        for(int i = 0; i < 3; i++) {
            resp = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer "+ jwtToken)
                    .body(payload.toString())
                    .post(situationRCAEndPoint);
            log.info("Get situation RCA: " + resp.getBody().asString());
        
            if (resp.getStatusCode() > 299) {
                log.info("Get situation RCA failed: " + resp.getStatusCode());
                commonwait.waitForSometime(10000);
                continue;
            }else{
                break;
            }
        }
        if (resp.getStatusCode() > 299) {
            log.error("Get situation RCA failed: " + resp.getStatusCode());
            return null;
        }
        String jsonString = getValidJsonOrNull(resp.getBody().asString());
        if (jsonString == null) {
            log.error("Get situation RCA failed: Invalid JSON");
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public JSONObject triggerandreturnDeepRCAResult(String situationIdentifier) throws Exception {
        boolean isDeepRCATriggered = triggerDeepRCA(situationIdentifier);
        if (!isDeepRCATriggered) {
            Assert.fail("Deep RCA trigger failed");
        }
        commonwait.waitForSometime(10000);
        JSONObject deepRCAResult = null;
        for (int i = 0; i < 21; i++) {
            deepRCAResult = getDeepRCAResult(situationIdentifier);
            if (deepRCAResult == null) {
                commonwait.waitForSometime(10000);
                continue;
            }
            JSONArray deepRCAStatus = deepRCAResult.getJSONArray("actions");
            String state = deepRCAResult.getString("state");
            log.info("Deep RCA state: " + state);
            
            
            // Require all expected phases to be present and none pending
            if (deepRCAStatus.length() < 3) {
                commonwait.waitForSometime(10000);
                continue;
            }

            boolean hasPending = false;
            for( Object action : deepRCAStatus) {
                JSONObject actionObject = (JSONObject) action;
                String actionStatus = actionObject.getString("phase");
                if ("PENDING".equalsIgnoreCase(actionStatus)) {
                    hasPending = true;
                    break;
                }
            }
            if (!("READY".equalsIgnoreCase(state) || "COMPLETED".equalsIgnoreCase(state))) {
                commonwait.waitForSometime(10000);
                continue;
            }
            if (!hasPending) {
                return deepRCAResult;
            }
            commonwait.waitForSometime(10000);
        }
        Assert.fail("Deep RCA actions are not completed");
        return deepRCAResult; // will be null if no successful poll occurred
    }

    public JSONObject getDeepRCASituationSummary(String situationIdentifier) throws Exception {
        String endpoint = helixGPTSummaryEndPoint + situationIdentifier + "?dataformat=dd/MM/yyyy&timeformat=HH:mm&timezone=Africa/Abidjan";
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .get(endpoint);
        log.info("Get deep RCA situation summary: " + resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get deep RCA situation summary failed: " + resp.getStatusCode());
            return null;
        }
        String jsonString = getValidJsonOrNull(resp.getBody().asString());
        if (jsonString == null) {
            log.error("Get deep RCA situation summary failed: Invalid JSON");
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public boolean updatesimilarsituationIncidentwithResolution(String situationIdentifier, String serviceName) throws Exception {
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        String similarsituationList = "msearchquerysimilarSituationlistwithIncident.json";
        long similarSituationTimeDiff = 3;
        String cmdbUrl = config.get("CMDBURL");
        String username = config.get("ADE_USERNAME");
        String password = config.get("ADE_PASSWORD");
        RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
        String authtoken = cmdbauthToken.getRemedyApiKey(username, password);
        CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);

        JSONObject msquery = hm_events.runMSearchQueryGetSituation(situationIdentifier, this.jwtToken,similarsituationList,similarSituationTimeDiff);
        int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalrecord == 0){
            return false;
        }
        for (int i = 0; i < totalrecord; i++) {
            String incidentid = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getJSONObject("_source").getString("incident_id");
            if (incidentid.length() < 3) {
                continue;
            }
            
            if (submitter == null) {
                assignee_groups = cmdbrestapi.getIncidentFieldValue(incidentid, "Assignee Groups");
                submitter = cmdbrestapi.getIncidentFieldValue(incidentid, "Submitter");
            }
            
            String requestId = cmdbrestapi.getFromIncidentInterfaceFieldValue(incidentid, "Request ID");
            String incidentStatus = cmdbrestapi.getIncidentFieldValue(incidentid, "Status");
            if (requestId == null || incidentStatus == null) {
                continue;
            }

            if (incidentStatus.equalsIgnoreCase("Closed")) {
                continue;
            }
            cmdbrestapi.updateResolutionField(requestId, serviceName, resolutionfieldValue);
        }
        return true;
    }
    
    public JSONObject getDeepRCABapTools(String sourceIdentifier, String identifier) throws IOException, InterruptedException {
        RestAssured.baseURI = hmHostName;
        String endPoint = bapRecommedationEndPoint + sourceIdentifier + "?dataformat=dd/MM/yyyy&timeformat=HH:mm&timezone=Africa/Abidjan";
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .get(endPoint);
        log.info("Get deep RCA BAP tools: " + resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Get deep RCA BAP tools failed: " + resp.getStatusCode());
            return null;
        }
        String jsonString = getValidJsonOrNull(resp.getBody().asString());
        if (jsonString == null) {
            log.error("Get deep RCA BAP tools failed: Invalid JSON");
            return null;
        }
        log.info("Get deep RCA BAP tools: " + jsonString);
        return new JSONObject(jsonString);
    }

    public String getResolutionfieldValue() {
        return resolutionfieldValue;
    }

    public Map<String, String> getSituationIDAndSourceID(String serviceKey) throws Exception {
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        String situationjsonfile = "msearchsituationquery.json";
        long situationTimeDiff = 300000;
        String situationIdentifier = "";
        String sourceIdentifier = "";
        JSONObject msquery = hm_events.runMSearchQueryGetSituation(serviceKey, this.jwtToken, situationjsonfile, situationTimeDiff);
        if (msquery == null){
            return null;
        }
        int totalrecord = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (totalrecord > 0) {
            JSONArray jsonarray = msquery.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
            for (Object jsonobj: jsonarray) {
                JSONObject sit_obj =  (JSONObject)jsonobj;
                String EventClass = sit_obj.getJSONObject("_source").getString("class");
                if(!EventClass.equals("Situation")){
                    continue;
                }
                String status = sit_obj.getJSONObject("_source").getString("status");
                if (!status.equals("CLOSED")){
                    log.info("Service is impacted and situation is created, exiting loop.");
                    situationIdentifier = sit_obj.getJSONObject("_source").getString("_identifier");
                    sourceIdentifier = sit_obj.getJSONObject("_source").getString("source_identifier");
                    break;
                }
            }
        }


        if (situationIdentifier.isEmpty() || sourceIdentifier.isEmpty()) {
            return null;
        }
        Map<String, String> situationIDAndSourceID = new HashMap<>();
        situationIDAndSourceID.put("situationIdentifier", situationIdentifier);
        situationIDAndSourceID.put("sourceIdentifier", sourceIdentifier);
        return situationIDAndSourceID;
    }

    public boolean isDeepRCAFeatureEnabled() {
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        boolean oldFeatureSetEnabled = true;
        for (String Oldfeature : OldfeatureList) {
            if (!aiopsApiCore.getTenantFeatureFlagStatus(Oldfeature, this.jwtToken)) {
                oldFeatureSetEnabled = false;
                break;
            }
        }
        boolean newFeatureSetEnabled = true;
        for (String Newfeature : newfeatureList) {
            if (!aiopsApiCore.getTenantFeatureFlagStatus(Newfeature, this.jwtToken)) {
                newFeatureSetEnabled = false;
                break;
            }
        }
        if (!oldFeatureSetEnabled && !newFeatureSetEnabled) {
            log.info("Required feature sets are not fully enabled; skipping test as passed.");
            return false;
        }

        return true;
    }

    public double getDeepRCAScore(String llmQuery, String AiopsSummary) {
        SemanticSimilarityONNX similarityCalculator = null;
        double rate = 0.0;

        try {
            DeepEval deepEval = new DeepEval();
            deepEval.getDeepEvalScore("DeepRCA summary check", llmQuery, AiopsSummary, AiopsSummary);
            rate = Double.parseDouble(deepEval.getRelevancyScore());
            return rate;
        } catch (AssertionError e) {
            rate = -1.0;
            log.error("DeepEval assertion failed: " + e.getMessage());
        } catch (Exception e) {
            rate = -1.0;
            log.error("DeepEval score failed: " + e.getMessage());
        }
        log.info("DeepEval score failed, using semantic similarity");    
        try {
            similarityCalculator = new SemanticSimilarityONNX();
            String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
            rate = similarityCalculator.calculateSemanticSimilarity(AiopsSummary, llmOutput);
            log.info("Tool output: " + AiopsSummary);
            log.info("BAP Summary Input: " + llmQuery);
        } catch (OrtException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error("Semantic similarity failed: " + e.getMessage());
            return -1.0;
        }
          
        return rate;
    }


    public double getDeepEvalRCAScore(String llmQuery, String AiopsSummary, int type) {
        //type = 0: DeepEval, 1: Semantic Similarity
        //type = 1: DeepEval with Hallucination check
        SemanticSimilarityONNX similarityCalculator = null;
        double rate = 0.0;

        try {
            DeepEval deepEval = new DeepEval();
            deepEval.getDeepEvalScore("DeepRCA summary check", llmQuery, AiopsSummary, AiopsSummary);
            rate = Double.parseDouble(deepEval.getRelevancyScore());
            return rate;
        } catch (AssertionError e) {
            rate = -1.0;
            log.error("DeepEval assertion failed: " + e.getMessage());
        } catch (Exception e) {
            rate = -1.0;
            log.error("DeepEval score failed: " + e.getMessage());
        }
        log.info("DeepEval score failed, using semantic similarity");    
        try {
            similarityCalculator = new SemanticSimilarityONNX();
            if(type == 1) {
                String llmOutput = OpenAIQuery.sendChatRequest(llmQuery);
                rate = similarityCalculator.calculateSemanticSimilarity(AiopsSummary, llmOutput);
            } else {
                rate = similarityCalculator.calculateSemanticSimilarity(AiopsSummary, llmQuery);
            }
            log.info("Tool output: " + AiopsSummary);
            log.info("BAP Summary Input: " + llmQuery);
        } catch (OrtException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error("Semantic similarity failed: " + e.getMessage());
            return -1.0;
        }
          
        return rate;
    }


    public boolean  runSituationRanker() throws Exception {
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        String endpoint = "/situation-best-action-recommender/api/v1.0/generate_ranker";
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("Content-Type", "application/json")
                .body("{}")
                .post(endpoint);
        log.info("Run situation ranker: " + resp.getBody().asString());
        if (resp.getStatusCode() > 299) {
            log.error("Run situation ranker failed: " + resp.getStatusCode());
            return false;
        }
        return true;
    }

    public String getIncidentRequestId(String incidentId) throws Exception {
        String requestId = null;
        if (authtoken == null) {
            cmdbUrl = config.get("CMDBURL");
            String username = config.get("ADE_USERNAME");
            String password = config.get("ADE_PASSWORD");
            RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
            authtoken = cmdbauthToken.getRemedyApiKey(username, password);
        }
        CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);
        requestId = cmdbrestapi.getFromIncidentInterfaceFieldValue(incidentId, "Request ID");
        if (requestId == null) {
            throw new IllegalStateException("Unable to fetch Request ID for incident " + incidentId);
        }
        return requestId;
    }

    public boolean updateIncidentTicketField(String requestId, String fieldname, String fieldvalue) throws Exception {
        if (authtoken == null) {
            cmdbUrl = config.get("CMDBURL");
            String username = config.get("ADE_USERNAME");
            String password = config.get("ADE_PASSWORD");
            RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
            authtoken = cmdbauthToken.getRemedyApiKey(username, password);
        }

        CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);
        if (!cmdbrestapi.updateIncidentTicketField(requestId, fieldname, fieldvalue)) {
            return false;
        }
        return true;
    }

    public boolean updateIncidentWorkLog(String incidentId, String title, String detail) throws Exception {
        if (authtoken == null) {
            cmdbUrl = config.get("CMDBURL");
            String username = config.get("ADE_USERNAME");
            String password = config.get("ADE_PASSWORD");
            RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
            authtoken = cmdbauthToken.getRemedyApiKey(username, password);
        }

        CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);
        if (submitter == null) {
            assignee_groups = cmdbrestapi.getIncidentFieldValue(incidentId, "Assignee Groups");
            submitter = cmdbrestapi.getIncidentFieldValue(incidentId, "Submitter");
        }
        
        if (!cmdbrestapi.updateWorkLog(incidentId, submitter, assignee_groups, title, detail)) {
            return false;
        }
        return true;
    }

    public boolean TenantFeatureStatusCheck(String featureflag_name){
        final int testcase = 0;
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        final String featureflagSheet = "featureflag";
        try {
            List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(testcase, featureflagSheet);
            int maxRow = (int) Float.parseFloat(rows.get(0).get("Feature Name").toString());
            if (maxRow <= 0) {
                return false;
            }

            boolean matchedFeatureflagNameRow = false;
            for (int i = 1; i <= maxRow; i++) {
                List<Map<String, Object>> row = excelReaderForMLSituation.getTestDataFromExcel(i, featureflagSheet);
                if (row == null) {
                    continue;
                }
                Object nameObj = row.get(0).get("Feature Name");
                if (nameObj == null) {
                    continue;
                }
                if (!featureflag_name.trim().equalsIgnoreCase(String.valueOf(nameObj).trim())) {
                    continue;
                }
                matchedFeatureflagNameRow = true;
                Object ff1 = row.get(0).get("Feature Flag 1");
                if (ff1 == null) {
                    return false;
                }
                for (String part : String.valueOf(ff1).split(",")) {
                    String flag = part.trim();
                    if (flag.isEmpty()) {
                        continue;
                    }
                    if (!aiopsApiCore.getTenantFeatureFlagStatus(flag, this.jwtToken)) {
                        return false;
                    }
                }
            }
            if (!matchedFeatureflagNameRow) {
                return false;
            }
            return true;
        } catch (IOException e) {
            log.warn("Feature flag Excel read failed, using direct API name match: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Feature flag sheet or columns missing: " + e.getMessage());
        }
        return false;
    }

    /** Counts Situation hits whose {@code incident_id} equals the given value (for uniqueness checks). */
    public int countSituationHitsWithIncidentId(JSONArray hitsArray, String incidentId) {
        if (incidentId == null || incidentId.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < hitsArray.length(); i++) {
            JSONObject hit = hitsArray.getJSONObject(i).getJSONObject("_source");
            if (!"Situation".equalsIgnoreCase(hit.optString("class", ""))) {
                continue;
            }
            if (incidentId.equals(hit.optString("incident_id", ""))) {
                count++;
            }
        }
        return count;
    }
    public JSONObject getCRAInsightDetails(String changeId) throws IOException, InterruptedException {
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .when()
                .get(CRAInsightEndPoint + changeId);
        int statusCode = resp.getStatusCode();
        if (statusCode > 299) {
            log.error("Get CRA insight details failed: " + statusCode);
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public Map.Entry<Integer, JSONObject> getCRAGPTInsights(String changeId) throws IOException, InterruptedException {
        RestAssured.baseURI = hmHostName;
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .when()
                .get(CRAGPTInsightsEndPoint + changeId);
        int statusCode = resp.getStatusCode();
        if (statusCode > 299) {
            log.error("Get CRA GPT insights failed: " + statusCode);
            return new AbstractMap.SimpleEntry<>(statusCode, null);
        }
        return new AbstractMap.SimpleEntry<>(statusCode, new JSONObject(resp.getBody().asString()));
    }

    public JSONObject getCRAList() throws IOException, InterruptedException {
        String ChangeListFileName = "getchangerisklist.json";
        RestAssured.baseURI = hmHostName;
        String body = aiopsApiCore.readJSon(ChangeListFileName);
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body("{\"parameters\":{\"method\":\"get_change_status_options\"}}")
                .when()
                .post(CRAListEndPoint);
        log.info("Get CRA list response: " + resp.getStatusCode() + " " + resp.getBody().asString());
        resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(body)
                .when()
                .post(CRAListEndPoint);
        log.info("Get CRA list response: " + resp.getStatusCode() + " " + resp.getBody().asString());
        int statusCode = resp.getStatusCode();
        if (statusCode > 299) {
            log.error("Get CRA list failed: " + statusCode);
            return null;
        }   
        return new JSONObject(resp.getBody().asString());
    }

    public void setxsrftoken(Set<Cookie> seleniumCookies){
        for (Cookie cookie : seleniumCookies) {
            if (cookie.getName().equals("XSRF-TOKEN")) {
                this.xsrfTokenValue = cookie.getValue();
                // We found the token, no need to continue looping
                break;
            }
        }
        for (Cookie cookie : seleniumCookies) {
            if (cookie.getName().equals("helix_jwt_token")) {
                this.jwtToken = cookie.getValue();
                log.info("JWT token found and stored.");
                // We found the token, no need to continue looping
                break;
            }
        }

    }

    public void setrestassuredcookies(Set<Cookie> seleniumCookies){
        List<io.restassured.http.Cookie> list = new ArrayList<>();
        for (Cookie seleniumCookie : seleniumCookies) {
            io.restassured.http.Cookie build = new io.restassured.http.Cookie.Builder(
                    seleniumCookie.getName(),
                    seleniumCookie.getValue())
                    .build();
            list.add(build);
        }
        this.restAssuredCookies = new Cookies(
                list
        );
    }

    public boolean setBrowserLevelProfile(){
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        logAnalyticsPage.auth_type = 3;
        try {
            logAnalyticsPage.getloganalyticscookies();
            this.setrestassuredcookies(LogAnalyticsPage.seleniumCookies);
            this.setxsrftoken(LogAnalyticsPage.seleniumCookies);
            return true;
        } catch (InterruptedException e) {
            log.error("Set browser profile failed: " + e.getMessage());
            return false;
        }
    }


    public boolean updateIncidentwithResolution(String situationIdentifier, String serviceName) throws Exception {
        this.jwtToken = commonmethod.refreshJwtToken(this.jwtToken);
        String similarsituationList = "msearchquerysimilarSituationlistwithIncident.json";
        long similarSituationTimeDiff = 3;
        String cmdbUrl = config.get("CMDBURL");
        String username = config.get("ADE_USERNAME");
        String password = config.get("ADE_PASSWORD");
        RemedyApiClient cmdbauthToken = new RemedyApiClient(cmdbUrl);
        String authtoken = cmdbauthToken.getRemedyApiKey(username, password);
        CMDBRestApi cmdbrestapi = new CMDBRestApi(cmdbUrl, authtoken);

        String eventDetails = hm_events.eventDetails(situationIdentifier, this.jwtToken);
        JSONObject eventDetailsJson = new JSONObject(eventDetails);
        String incidentid = eventDetailsJson.getJSONObject("response").getJSONObject("allslots").getString("incident_id");
        if (incidentid.length() < 3) {
            return false;
        }
            
        if (submitter == null) {
            assignee_groups = cmdbrestapi.getIncidentFieldValue(incidentid, "Assignee Groups");
            submitter = cmdbrestapi.getIncidentFieldValue(incidentid, "Submitter");
        }
        
        String requestId = cmdbrestapi.getFromIncidentInterfaceFieldValue(incidentid, "Request ID");
        String incidentStatus = cmdbrestapi.getIncidentFieldValue(incidentid, "Status");
        if (requestId == null || incidentStatus == null) {
            return false;
        }

        if (incidentStatus.equalsIgnoreCase("Closed")) {
            return false;
        }
        cmdbrestapi.updateResolutionField(requestId, serviceName, resolutionfieldValue);
        
        return true;
    }

    public boolean moveChangeTorequestStatus(Map<String, String> changeIdSummary, String requestedStatus) throws Exception
    {
        String[] validChangeStatusSequence = {"Draft", "Request For Authorization", "Request For Change", "Planning In Progress", "Scheduled For Review", "Scheduled For Approval", "Scheduled", "Implementation In Progress"};
        String cmdbUrl = config.get("CMDBURL");
        RemedyApiClient remedyStatusApiClient = new RemedyApiClient(cmdbUrl);
        String StatusauthToken = remedyStatusApiClient.getRemedyApiKey(changeIdSummary.get("cabmanagerlogin"), "__USER_PASSWORD__");;
        CMDBRestApi cmdbStatusRestApi = new CMDBRestApi(cmdbUrl, StatusauthToken);

        for(int i =0;i<20;i ++) {
            String currentChangeStatus = cmdbStatusRestApi.getCurrentChangeStatus(changeIdSummary.get("RequestID"));
            log.info("Attempt " + i + " status=" + currentChangeStatus);
            if(currentChangeStatus != null && currentChangeStatus.equals(requestedStatus)) {
                log.info("Requested status reached");
                return true;
            }
            int currentIndex = Arrays.asList(validChangeStatusSequence).indexOf(currentChangeStatus);
            if (currentIndex == -1 || currentIndex + 1 >= validChangeStatusSequence.length) {
                log.info("Unable to progress change status from " + currentChangeStatus);
                return false;
            }
            String nextStatus = validChangeStatusSequence[currentIndex + 1];
            log.info("Advancing to " + nextStatus);
            cmdbStatusRestApi.updateChangeRequestStatus(changeIdSummary.get("RequestID"), nextStatus, changeIdSummary.get("infrachangeid"));
            commonwait.waitForSometime(10000);
        }
        return true;
    }

    public JSONObject getHelixGPTQuery(String query) throws IOException, InterruptedException {
        RestAssured.baseURI = hmHostName;
        String askHelixGTPFile = "askhelixgpt.json";
        String jsoncontent = aiopsApiCore.readJSon(askHelixGTPFile);
        String randomnumber = String.valueOf(ThreadLocalRandom.current().nextLong(1000000000000L, 9999999999999L));
        jsoncontent = jsoncontent.replace("<query>", query);
        jsoncontent = jsoncontent.replace("<randomnumber>", randomnumber);
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsoncontent)
                .when()
                .post(HELIXGPTQueryEndPoint);
        int statusCode = resp.getStatusCode();
        log.info("Get Helix GPT query response: " + statusCode + " " + resp.getBody().asString());
        if (statusCode > 299) {
            log.error("Get Helix GPT query failed: " + statusCode);
            return null;
        }
        return new JSONObject(resp.getBody().asString());
    }

    public String getHelixGPTQueryResponse(String query) throws IOException, InterruptedException {
        JSONObject helixGPTQueryResponse = getHelixGPTQuery(query);
        if (helixGPTQueryResponse == null) {
            return null;
        }
        log.info("Helix GPT query response: " + helixGPTQueryResponse.toString());
        String helixGPTQueryResponseText = helixGPTQueryResponse.getString("answer");
        JSONObject helixGPTQueryResponseData = new JSONObject(helixGPTQueryResponseText);
        JSONArray helixGPTQueryResponseDataArray = helixGPTQueryResponseData.getJSONArray("messages");
        String contents = null;
        for(Object message : helixGPTQueryResponseDataArray){
            JSONObject messageObject = (JSONObject) message;
            String type = messageObject.getString("type");
            if(type.equalsIgnoreCase("human")){
                continue;
            }

            String content = messageObject.getString("content");
            if(content.length() < 15){
                log.info("Assistant message: " + contents);
                continue;
            }
            contents = content;
            break;
        }
        return contents;
    }

    public String prescannQuery(String _identifier, int type) {
        // List of types as in the Python code
        RestAssured.baseURI = hmHostName;
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String[] typeList = {
                "similar-situation-pattern",
                "change-window",
                "service-health-impact",
                "remediation-team",
                "five-why"
        };

        if (type < 0 || type >= typeList.length) {
            log.error("Invalid type index for prescannQuery: " + type + ". Valid range is 0 to " + (typeList.length - 1));
            return null;
        }

        String jsonfile = "helixgptquerypre.json";
        String jsoncontent = aiopsApiCore.readJSon(jsonfile);
        jsoncontent = jsoncontent.replace("<typeList>", typeList[type]).replace("<_identifier>", _identifier);
        Response resp = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+this.jwtToken)
                .header("X-XSRF-TOKEN", xsrfTokenValue)
                .cookies(this.restAssuredCookies)
                .body(jsoncontent)
                .when()
                .post(helixPrescanQueryendpoint);
        int statusCode = resp.getStatusCode();
        log.info("Get Helix GPT query response: " + statusCode + " " + resp.getBody().asString());
        if (statusCode > 299) {
            log.error("Get Helix GPT query failed: " + statusCode);
            return null;
        }
        String responseBody = resp.getBody().asString();
        JSONObject responseJson = new JSONObject(responseBody);
        String rescanSummary = responseJson.optString("answer", "");
        if (rescanSummary == null || rescanSummary.contains("Sorry")) {
            return null;
        } else {
            return rescanSummary;
        }
        
    }
}
