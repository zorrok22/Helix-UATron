package pages.ITOM.LogAnalytics;

import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import pages.ITOM.Api.HMEvents;
import util.ReadPropertiesFile;
import org.json.JSONObject;
import java.time.*;
import java.time.format.DateTimeFormatter;
import framework.Launcher;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.net.URISyntaxException;

public class LogAnalyticsPage {
    public JSONObject jsonObject = null;
    public int auth_type = 0; //0 jwt, 1 log analytics 2 IA 3 Aiops

    private static final SolqaLogger log = new SolqaLogger(LogAnalyticsPage.class.getName());
    static int return_status = 0;
    private String index;
    private String tagsField;
    private static String browser_cookies = null;
    public int response_code = 200;
    public String auth_token = null;
    static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    public static String timeZone = "GMT";
    public static Launcher  launch_driver = new Launcher();
    private static final CommonWaits commonwait = new CommonWaits();
    private static final String base_url =  config.get("ADE_PLATFORM_URL");
    private static final String loganalytics_url =  base_url + "/loganalytics/#";
    private static final String aiops_url =  base_url + "/aiops/#";
    private static final String ia_url =  base_url + "/intelligent-automation/#";
    private static final String loganalytics_app_xpath = "//div[@id=\"BMC Helix Log Analytics\"]";
    private static final String loganalytic_discovery_xpath = "//*[@title='Discover']";
    private static final String loganalytic_iframe_xpath = "//*[@id='iframe_kibana']";
    private static final String loganalsytic_oepnsearch_timeformat = "yyyy-MM-dd'T'HH:mm:ss'.000Z'";
    private static final String loganalsytic_timezone = "GMT";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    String app = SolQAConstants.appITOM;
    public String[] OpensearchData_es_endPoint = {"/logexplorer/internal/search/es", "/logexplorer/internal/search/opensearch", "/logexplorer/internal/search/opensearch-with-long-numerals"};
    public String IndexNumber_endPoint = "/logexplorer/api/saved_objects/_find?fields=title&per_page=10000&type=index-pattern";
    public String KibanaType_endPoint = "/logexplorer/api/core/capabilities";
    public static WebDriver driver = null;
    public static final String platformjwtloginendpoint = "/ims/api/v1/access_keys/login";
    private static final String BASE_URL = config.get("ADE_PLATFORM_URL");
    public String ACCESS_KEY = resolveAccessKey(false);
    public String ACCESS_SECRET_KEY = resolveSecretKey(false);
    private static final String TenantKeyStore = "tenant-keys.json";
    private static final String logPushEndPoint =  "/log-service/api/v1.0/logs";
    private static final String  logRetrievalEndPoint = "/logs-service/api/v1.0/logs/msearch";
    private static final String updateDescriptionField = "//*[@rx-id=\"access-key-description\"]//textarea";
    private static final String getTenantApiName = "//input[contains(@name,'tenantApiName')]";
    private static final String addKeyButton = "//span[contains(.,'Add key')]/../../button";
    private static final String selectAuthPeriodselection =  "//button[@placeholder='Please select']";
    private static final String neverExpireOption = "//button[@id='rx-select-4_popup-4']";
    private static final String confirmButtonxpath = "//button[contains(@rx-id,'confirm')]";
    private static final String accessKeyCopyXpath = "//button[@rx-id='access-key-copy']";
    private static final String myAccessKeyTable = "//div[@class=\"my-keys\"]//div[contains(@class,'body')]";
    private String username = config.get("ADE_USERNAME");
    private String password = config.get("ADE_PASSWORD");

    private String resolveAccessKey(boolean fallback) {
        String key = "";
        if(!fallback){
            key = SolQAConstants.API_ACCESS_KEY;
            if (isUsableKey(key)) {
                return key;
            }

            key = config.get("ACCESS_KEY");
            if (isUsableKey(key)) {
                return key;
            }
        } 

        String tenantUrl = config.getOrDefault("ADE_PLATFORM_URL", "defaultTenant");
        String host = "";
        try {
            host = new URI(tenantUrl).getHost();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String[] parts = host.split("\\.");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 2; i++) {
            if (i > 0) sb.append('.');
            sb.append(parts[i]);
        }
        String tenantName = sb.toString();
        TenantKeyPair storedKeys = loadTenantKeys(tenantName);
        if (storedKeys != null && isUsableKey(storedKeys.accessKey)) {
            if (isUsableKey(storedKeys.secretKey)) {
                ACCESS_SECRET_KEY = storedKeys.secretKey;
            }
            return storedKeys.accessKey;
        }

        try {
            String keyDetails = getUserApiKey();
            if (keyDetails != null) {
                String[] parsed = parseKeyDetails(keyDetails);
                if (parsed.length > 0 && isUsableKey(parsed[0])) {
                    String access = parsed[0];
                    String secret = parsed.length > 1 ? parsed[1] : null;
                    if (isUsableKey(secret)) {
                        ACCESS_SECRET_KEY = secret;
                    }
                    storeTenantKeys(tenantName, access, secret);
                    return access;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch access key via UI: " + e.getMessage());
        }

        return key;
    }

    private String resolveSecretKey(boolean fallback) {
        String key = "";
        if(!fallback) {
            key = SolQAConstants.API_SECRET_KEY;
            if (isUsableKey(key)) {
                return key;
            }

            key = config.get("SECRET_KEY");
            if (isUsableKey(key)) {
                return key;
            }
        }

        String tenantUrl = config.getOrDefault("ADE_PLATFORM_URL", "defaultTenant");
        String host = "";
        try {
            host = new URI(tenantUrl).getHost();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String[] parts = host.split("\\.");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 2; i++) {
            if (i > 0) sb.append('.');
            sb.append(parts[i]);
        }
        String tenantName = sb.toString();

        TenantKeyPair storedKeys = loadTenantKeys(tenantName);
        if (storedKeys != null && isUsableKey(storedKeys.secretKey)) {
            if (isUsableKey(storedKeys.accessKey) && !isUsableKey(ACCESS_KEY)) {
                ACCESS_KEY = storedKeys.accessKey;
            }
            return storedKeys.secretKey;
        }

        return key;
    }
    private static final String myAccessRow = myAccessKeyTable + "/div[@class='row ng-star-inserted']";
    private static final String deleteAccessXpath = "//button[contains(.,' Delete ')]";
    private static final String deleteAccessKeyXpath = "//button[contains(.,'Delete key')]";
    private static final String editAccessXpath = "//button[contains(.,' Edit ')]";
    public static Set<Cookie> seleniumCookies = null;

    @AfterMethod(alwaysRun = true)
    public static void tearDown() {
        if(driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static String readJSon(String jsonFileName){
        try { //C:\GitHub\solqaautomation\SolutionsQA_Automation\Java_qa\resources\ITOM\LA\logpush.json
            String jsonContent;
            String jsonFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "LA"+File.separator+jsonFileName;
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            return jsonContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getloganalyticscookies() throws InterruptedException {
        String adePageURL = "";
        String waitElement = "";
        switch (auth_type) {
            case 1:
                adePageURL = loganalytics_url;
                waitElement = loganalytic_discovery_xpath;
                break;
            case 2:
                adePageURL = ia_url;
                waitElement = "//span[contains(.,'Automations Policies')]";
                break;
            case 3:
                adePageURL = aiops_url;
                waitElement = "//span[@id='Total Events']";
                break;
        }

        try {
            // Create launcher instance and launch browser
            driver = launch_driver.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
            Launcher.setDriver(driver);
            // Initialize wait and commonWaits

            // Login to the system
            Launcher.login(username, password, SolQAConstants.appITOM);

            // Navigate to Log Analytics (AiOps page)
            commonwait.waitForSometime(20000);
            commonwait.scrollToSpecificElement(loganalytics_app_xpath);
            commonwait.waitForElementPresent(loganalytics_app_xpath, 5);
            driver.get(adePageURL);
            commonwait.waitForPageLoad(driver);

            try {
                if (auth_type == 1) {
                    WebElement frame = commonwait.waitForByElementToBeVisible(By.id(loganalytic_iframe_xpath));
                    driver.switchTo().frame(frame);
                }
                commonwait.waitForElementPresent(waitElement, 15);
            } catch (Exception ignored) {
            }
            // Retrieve cookies
            seleniumCookies = driver.manage().getCookies();
            // Format cookies as a string for REST API header
            browser_cookies = seleniumCookies.stream()
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.joining("; "));

            System.out.println("Retrieved Cookies: " + browser_cookies);
        }finally{
            tearDown();
        }
    }

    public void generateJWTToken() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("access_key", ACCESS_KEY);
            requestBody.put("access_secret_key", ACCESS_SECRET_KEY);

            io.restassured.response.Response response = RestAssured.given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(requestBody.toString())
                    .post(platformjwtloginendpoint);

            if (response.getStatusCode() != 200) {
                // Retry once with freshly resolved keys (force re-fetch / UI fetch)
                ACCESS_KEY = resolveAccessKey(true);
                ACCESS_SECRET_KEY = resolveSecretKey(true);
                requestBody.put("access_key", ACCESS_KEY);
                requestBody.put("access_secret_key", ACCESS_SECRET_KEY);
                response = RestAssured.given()
                        .baseUri(BASE_URL)
                        .contentType(ContentType.JSON)
                        .body(requestBody.toString())
                        .post(platformjwtloginendpoint);
            }

            Assert.assertEquals(response.getStatusCode(), 200, "Failed to generate JWT token");

            // Extract JWT token from response
            auth_token = response.jsonPath().getString("json_web_token");
            Assert.assertNotNull(auth_token, "JWT token is null");

            // Print token for verification (remove in production)
            System.out.println("Generated JWT Token: " + auth_token);

        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            throw e;
        }
    }

    public void Send_RestCall(String request_type,String end_point,String[] stringArray) throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .build();
        String url =  config.get("ADE_PLATFORM_URL") + end_point;

        if ((auth_token == null) && (auth_type == 0)) {
            System.out.println("Inside jwt token");
            generateJWTToken();
        }

        if ((browser_cookies == null)&& (auth_type > 0)){
            System.out.println("Inside Cokkies");
            getloganalyticscookies();
        }

        RequestBody body = null;
        Request request = null;
        boolean retry_flag = true;
        String jsonInputString = "";


        if (stringArray.length > 1){
            //jsonInputString =  new Gson().toJson(stringArray[1]);
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            body = RequestBody.create(stringArray[1], JSON);
        }
        if (stringArray.length > 2){
            retry_flag = false;
        }

        int maxRetries = 4;
        for (int i = 0; i< maxRetries; i++) {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json");
            if (auth_type > 0) {
                builder.header("Cookie", browser_cookies);
                builder.header("kbn-xsrf", "true");
                builder.header("Accept", "*/*");
                builder.header("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");
                builder.header("Accept-Encoding", "gzip, deflate, br");
                builder.header("osd-xsrf", "true");
            } else {
                builder.header("Authorization", "Bearer " + auth_token);
            }

            if (Objects.equals(request_type, "POST")) {
                assert body != null;
                System.out.println(body.toString());
                builder.post(body);
            } else if (Objects.equals(request_type, "GET")) {
                builder.get();
            } else if (Objects.equals(request_type, "PUT")) {
                assert body != null;
                System.out.println(body.toString());
                builder.put(body);
            }
            else if (Objects.equals(request_type, "PATCH")) {
                assert body != null;
                System.out.println(body.toString());
                builder.patch(body);
            }
            else {
                System.out.println("Invalid parameter pass");
            }
            request = builder.build();
            try {
                System.out.println(url);
                Response response = client.newCall(request).execute();
                response_code = response.code();
                System.out.println(response_code);
                if (response.body() != null) {
                    String restapi_response_body = String.valueOf(response.body().string());
                    System.out.println(restapi_response_body);
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(restapi_response_body);
                        return_status = 0;
                        return;
                    }
                }
                if (response.code() < 300){ return;}
                if (response.code() == 401) {
                    commonwait.waitForSometime(2000);
                    if (auth_type == 0) {
                        generateJWTToken();
                    } else if (auth_type == 1){
                        getloganalyticscookies();
                    }
                    continue;
                }else{
                    if (retry_flag){
                        commonwait.waitForSometime(5000);
                        continue;
                    }
                    return_status = 1;
                    return;
                }
            } catch (Exception e) {
                System.out.println("Exception occurred: " + e.getMessage() + ". Retrying...");
                commonwait.waitForSometime(2000);
            }
        }
        return_status = 1;

    }

    public String getIndexNumber() throws IOException, InterruptedException {
        String[] arry = {"200"};
        Send_RestCall("GET", IndexNumber_endPoint, arry);
        JSONArray recordList = jsonObject.getJSONArray("saved_objects");
        System.out.println(recordList);
        for (int i = 0; i < recordList.length(); i++)  {
            JSONObject json_ind = recordList.getJSONObject(i);
            String title =  json_ind.getJSONObject("attributes").getString("title");
            if (!title.contains("ml")) {
                return title;
            }
        }
        return null;
    }

    public String getKibanaType(String defaultTitle) throws IOException, InterruptedException {
        String data_path = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + app +File.separator + File.separator + "LA" +File.separator + "kibanatype.json";
        JsonNode jsonNode = objectMapper.readTree(new File(data_path));
        String data_body = jsonNode.toString();
        String[] arry = {"200",data_body};
        Send_RestCall("POST", KibanaType_endPoint, arry);
        JSONObject recordList = jsonObject.getJSONObject("navLinks");
        System.out.println(recordList.toString());
        boolean value = recordList.getBoolean("opensearch-dashboards");
        if (value) {
            return "@opensearch-dashboards-highlighted-field@";
        } else {
            return "@kibana-highlighted-field@";
        }
    }

    public JSONObject getOpensearchData(String query, String duration) throws IOException, InterruptedException {
        if (index == null || index.length() < 2) {
            index = getIndexNumber();
        }
        if (tagsField == null || tagsField.length() < 2) {
            tagsField = getKibanaType(index);
        }

        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(loganalsytic_timezone));
        String endTime = currentTime.format(DateTimeFormatter.ofPattern(loganalsytic_oepnsearch_timeformat));
        ZonedDateTime startTime = currentTime.minusHours(Long.parseLong(duration));
        String startTimeString = startTime.format(DateTimeFormatter.ofPattern(loganalsytic_oepnsearch_timeformat));
        String data_path = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + app +File.separator + File.separator + "LA" +File.separator + "Opensearch.json";
        String data_body = Files.readString(Paths.get(data_path));
        String newQuery = query.replace("${end_time}", endTime).replace("${start_time}", startTimeString);
        String tags_field = "@kibana-highlighted-field@";
        String perf = String.valueOf(currentTime.toInstant().toEpochMilli());
        data_body = data_body.replace("${index}",index).replace("${timeZone}",timeZone).replace("${newQuery}",newQuery).replace("${preference}",perf);
        String body = data_body.replace("${tags_field}",tags_field);
        String[] arry = {"200",body,"1"};
        System.out.println(body);
        Send_RestCall("POST", OpensearchData_es_endPoint[0], arry);
        if (response_code < 300) {
            return jsonObject;
        }

        // Try another endpoint if the first one fails

        tags_field = "@opensearch-dashboards-highlighted-field@";
        String body_new = data_body.replace("${tags_field}",tags_field);
        arry[1] = body_new;
        for (String ep : OpensearchData_es_endPoint) {
            System.out.println(arry[1]);
            Send_RestCall("POST", ep, arry);
            if (response_code < 300) {
                return jsonObject;
            }
        }
        return null;
    }

    public void logPush(String msg, String hostName) throws IOException, InterruptedException {
        HMEvents eventApi = new HMEvents();
        if ((auth_token == null) && (auth_type == 0)) {
            generateJWTToken();
        }
        String jwtToken = auth_token;
        String jsonFile = "logpush.json";
        Long currenttime = eventApi.getBhomServerTime(jwtToken);
        //currenttime = currenttime - (19800 * 1000);
        String isoFormat = Instant.ofEpochMilli(currenttime)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String jsonContent = readJSon(jsonFile);
        String apiBody = jsonContent.replace("<HOST_NAME>", hostName).replace("<EPOCH_TIME>", String.valueOf(currenttime)).replace("<TIME_STAMP>", isoFormat);
        apiBody = apiBody.replace("<MSG>", msg);
        String[] arry = {"200", apiBody};
        Send_RestCall("POST", logPushEndPoint, arry);
    }

    public JSONObject logRetrieval(String query, String duration) throws IOException, InterruptedException {
        String jsonFile = "logretrieval.json";
        String jsonContent = readJSon(jsonFile);
        System.out.println(query);
        String apiBody = jsonContent.replace("<QUERY>", query);
        System.out.println(apiBody);
        String[] arry = {"200", apiBody};
        Send_RestCall("POST", logRetrievalEndPoint, arry);
        if (response_code < 300) {
            return jsonObject;
        }
        return null;
    }

    public static String createAccessKey(String name) throws IOException, UnsupportedFlavorException {


        commonwait.pureSafeClick(addKeyButton);
        WebElement inputtext = commonwait.waitForByElementToBeVisible(By.xpath(getTenantApiName));
        inputtext.sendKeys(name);
        WebElement descrptioninputtext = commonwait.waitForByElementToBeVisible(By.xpath(updateDescriptionField));
        descrptioninputtext.sendKeys(name);
        commonwait.pureSafeClick(selectAuthPeriodselection);
        commonwait.waitForSometime(1000);
        commonwait.pureSafeClick(neverExpireOption);
        commonwait.waitForSometime(1000);
        commonwait.pureSafeClick(confirmButtonxpath);
        commonwait.waitForSometime(1000);
        commonwait.pureSafeClick(accessKeyCopyXpath);
        Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        commonwait.waitForSometime(1000);
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String keyvalue = contents.getTransferData(DataFlavor.stringFlavor).toString();
            commonwait.clearTextFromElement(descrptioninputtext);
            descrptioninputtext.sendKeys(keyvalue);
            return keyvalue;
        }
        return null;

    }

    public static void deleteAccessKey(String name){
        String apiKeyDelete = "//span[contains(.,'" + name + "')]";
        int rowCount = driver.findElements(By.xpath(apiKeyDelete)).size();
        if (rowCount < 1){
            return;
        }
        for (int i = 1; i < 3; i = i + 1){
            String deleteApikeyEntry = myAccessRow + "[" + Integer.toString(i) + "]";
            int entryFound = driver.findElements(By.xpath(deleteApikeyEntry + apiKeyDelete)).size();
            if (entryFound == 0){
                continue;
            }
            commonwait.pureSafeClick(deleteApikeyEntry + "//button");
            commonwait.waitForSometime(1000);
            commonwait.pureSafeClick(deleteAccessXpath);
            commonwait.waitForSometime(1000);
            commonwait.pureSafeClick(deleteAccessKeyXpath);
            commonwait.waitForSometime(5000);
        }

    }


    public static String editAccessKey(String name){
        String apiKeyDelete = "//span[contains(.,'" + name + "')]";
        String closeEditOption = "//i[contains(@class,'cross')]";
        String getAccessKeyValue = "//*[@rx-id='edit-access-key-description-field']//textarea";
        int rowCount = driver.findElements(By.xpath(apiKeyDelete)).size();
        if (rowCount < 1){
            return null;
        }
        for (int i = 1; i < 3; i = i + 1){
            String editApikeyEntry = myAccessRow + "[" + Integer.toString(i) + "]";
            int entryFound = driver.findElements(By.xpath(editApikeyEntry + apiKeyDelete)).size();
            if (entryFound == 0){
                continue;
            }
            commonwait.pureSafeClick(editApikeyEntry + "//button");
            commonwait.waitForSometime(1000);
            commonwait.pureSafeClick(editAccessXpath);
            commonwait.waitForSometime(1000);
            WebElement textarea = commonwait.waitForByElementToBeVisible(By.xpath(getAccessKeyValue));
            String accessKey = textarea.getAttribute("value");
            commonwait.pureSafeClick(closeEditOption);
            return accessKey;
        }
        return null;
    }

    public String getUserApiKey() throws IOException, UnsupportedFlavorException {
        String adePageURL = base_url + "/admin/#/editUserProfile";
        String myAccessEntry = "//strong[contains(.,'My access keys')]";
        String myAccessKeyTable = "//div[@class=\"my-keys\"]//div[contains(@class,'body')]";
        String myAccessRow = myAccessKeyTable + "/div[@class='row ng-star-inserted']";
        String keyName = "BhomApiKey";
        String expectedNameList[] = {"BhomApiKey", "Automation Key"};
        // Create launcher instance and launch browser
        driver = launch_driver.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        Launcher.setDriver(driver);
        // Initialize wait and commonWaits

        // Login to the system
        Launcher.login(username, password, SolQAConstants.appITOM);

        // Navigate to Log Analytics (AiOps page)
        commonwait.waitForElementPresent(loganalytics_app_xpath, 15);
        driver.get(adePageURL);
        commonwait.waitForSometime(5000);
        driver.navigate().refresh();
        commonwait.waitForSometime(5000);
        commonwait.waitForPageLoad(driver);
        commonwait.waitForByElementToBeVisible(By.xpath(myAccessEntry));
        WebElement webelement = commonwait.waitForByElementToBeVisible(By.xpath(myAccessKeyTable));
        //deleteAccessKey(keyName);
        int rowCount = driver.findElements(By.xpath(myAccessRow)).size();
        if (rowCount == 0){
            return createAccessKey(keyName);
        }
        String accesskey = null;
        for (String accessName: expectedNameList){
            accesskey = editAccessKey(accessName);
            if(accesskey != null){
                return accesskey;
            }
        }
        if (rowCount < 2){
            return createAccessKey(keyName);
        }
        return null;
    }

    // Helper method to remove empty strings from an array
    private static String[] removeEmptyStrings(String[] array) {
        return Arrays.stream(array)
                .filter(s -> s != null && !s.isEmpty())
                .toArray(String[]::new);
    }

    private static String[] parseKeyDetails(String keyDetails) {
        String cleaned = keyDetails == null ? "" : keyDetails.replace("Key details:", "");
        String[] parts = cleaned.split("::|,|\\s+");
        return removeEmptyStrings(parts);
    }

    private static boolean isUsableKey(String key) {
        return key != null && key.trim().length() >= 20;
    }

    private static TenantKeyPair loadTenantKeys(String tenantName) {
        String fileName = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + TenantKeyStore;
        try {
            if (!Files.exists(Paths.get(fileName))) {
                return null;
            }
            Map<String, TenantKeyPair> allTenants = objectMapper.readValue(
                    Files.readAllBytes(Paths.get(fileName)),
                    objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, TenantKeyPair.class)
            );
            return allTenants.get(tenantName);
        } catch (Exception e) {
            log.warn("Unable to read tenant keys from store: " + e.getMessage());
            return null;
        }
    }

    private static void storeTenantKeys(String tenantName, String accessKey, String secretKey) {
        try {
            Map<String, TenantKeyPair> allTenants = new HashMap<>();
            String fileName = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + TenantKeyStore;
            if (Files.exists(Paths.get(fileName))) {
                allTenants = objectMapper.readValue(
                        Files.readAllBytes(Paths.get(fileName)),
                        objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, TenantKeyPair.class)
                );
            }
            allTenants.put(tenantName, new TenantKeyPair(accessKey, secretKey));
            objectMapper.writeValue(Paths.get(fileName).toFile(), allTenants);
        } catch (Exception e) {
            log.warn("Unable to store tenant keys to file: " + e.getMessage());
        }
    }

    private static class TenantKeyPair {
        public String accessKey;
        public String secretKey;

        public TenantKeyPair() {}

        public TenantKeyPair(String accessKey, String secretKey) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
        }
    }

    public WebDriver setuserAccessSecretKey()throws Exception{
        String key = "";


        key = getUserApiKey();
        System.out.println("Retrieved user API key " +  key);


        Assert.assertNotNull(key, "Unable to get the key");
        key = key.replace("Key details:","");
        System.out.println("Clipboard content: " + key);
        // Split the string based on '::' and ',' and store the result in an array
        String[] parts = key.split("::|,|\\s+");

        // Remove empty strings from the array
        String[] filteredParts = removeEmptyStrings(parts);

        // Assign the first part to one variable and the second part to another variable
        String accessKey = filteredParts[0];
        String secretKey = filteredParts[1];

        System.out.println("First part: " + accessKey);
        System.out.println("Second part: " + secretKey);
        ACCESS_KEY = accessKey;
        ACCESS_SECRET_KEY = secretKey;
        generateJWTToken();
        return driver;
    }


    public void setadeuserandpassword(String _username, String _password) {
        username = _username;
        password = _password;
    }
}
