package pages.ITSM.HPM;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITSM.Api.API_CommonOperations;
import pages.ITSM.InnovationSuite.InnovationSuite;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class HPM_AppsCommonOperations extends InnovationSuite {

    SolqaLogger log;
    CommonWaits commonWaits;
    CommonMethods commonMethods;
    API_CommonOperations apiCommonOperations;

    public HPM_AppsCommonOperations() {
        log = new SolqaLogger(HPM_AppsCommonOperations.class.getName());
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
        apiCommonOperations = new API_CommonOperations();
    }

    String hpmRecordTypeDropdown = "//a[normalize-space(.)='%s' and contains(@href, 'Create')]";
    String textInputField = "(//adapt-rx-textfield[contains(normalize-space(.) ,'%s')]//input)";
    String ckeEditorField = "//label[text()='%s']/parent::*//*[contains(@aria-label, 'Rich text editor main editing area')]//p";
    String associatedRecordRadioBtn = "//adapt-rx-radiobutton//label";
    String dropdownField = "//adapt-rx-select[contains(. ,'%s')]//button";
    String dropdownOptions = "//button[@role='option' and normalize-space(.)='%s']";
    String counterFieldInput = "//adapt-rx-counter[contains(. ,'%s')]//input";
    String dateFieldInput = "//adapt-rx-datetime[contains(. ,'%s')]//input";
    String typeheadInput = "//adapt-rx-typeahead[contains(. ,'%s')]//input";
    String typeheadOption = "(//button[@role='option' and contains(. ,'%s')])[1]";
    String Tab = "//button[@role='tab' and contains(. ,'%s')]";

    @FindBy(xpath = "//button[normalize-space(.)='Create']")
    WebElement createHPMRecordButton;

    @FindBy(xpath = "//button[normalize-space(.)='Save']")
    WebElement saveHPMRecordButton;

    @FindBy(xpath = "//*[contains(@aria-label, 'Prefix Display ID')]")
    WebElement prefixDisplayIDField;

    @FindBy(xpath = "//*[contains(@aria-label, 'Display Id')]")
    WebElement displayIDField;

    @FindBy(xpath = "//label[contains(. ,'Portfolio')]/ancestor::div[contains(@class, 'rx-runtime-view-canvas-item-container-column')]/following-sibling::*//button[contains(. ,'Associate')]")
    WebElement associateRecordButton;

    @FindBy(xpath = "(//*[@rx-view-component-id='6cd8991e-1879-4bf3-82e1-779e279a966b']//button)[last()]")
    WebElement associateExternalEntityButton;

    @FindBy(xpath = "//input[@placeholder='Search']")
    WebElement searchInputBox;

    @FindBy(xpath = "//button[contains(@class, 'adapt-rx-search__search-button') and normalize-space(.)='Search']")
    WebElement searchButton;

    @FindBy(xpath = "//*[contains(@class, 'rx-ellipsis')]//a//span")
    WebElement searchedRecordLink;

    @FindBy(xpath = "//rx-runtime-view-modal[contains(. ,'Associate Portfolio')]//button[normalize-space()='Select']")
    WebElement associateSelectButton;

    @FindBy(xpath = "(//*[@rx-view-component-id='b4c1aa4a-8817-4c57-a5db-af3e92de9649']//button[normalize-space()='Associate'])[last()]")
    WebElement associateRecordBtnInProject;

    @FindBy(xpath = "//header[contains(. ,'Portfolio Management')]")
    WebElement hpmHeader;

    @FindBy(xpath = "(//*[@rx-view-component-id='e6bc1c77-53c0-4cf8-a28b-0805b9f48924']//button)[last()]")
    WebElement associateRecordCompactBtn;

    @FindBy(xpath = "(//*[@rx-view-component-id='7642ded8-b2ee-43c2-a8a7-aaa063f84ed6']//button[normalize-space()='Associate'])[last()]")
    WebElement associateRecordBtnInResourceRequest;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'External Entity')]")
    WebElement externalEntityBtn;

    @FindBy(xpath = "//*[@rx-view-component-id='bb5c1a3c-e09b-4230-bccc-96f6b7378089']//button[contains(normalize-space(.), 'Save')]")
    WebElement saveExternalEntityBtn;

    @FindBy(xpath = "//*[@rx-view-component-id='ed113d42-d361-424b-8169-3a19f258d5c6']//button[normalize-space()='Select']")
    WebElement selectEntityBtn;

    @FindBy(xpath = "//button[contains(. ,'Add Requirement')]")
    WebElement addRequirementBtn;

    @FindBy(xpath = "//*[@rx-view-component-id='6a909c84-6c75-4ec3-9f4b-ed688045cec9']//button[contains(.,'Save')]")
    WebElement saveRequirementBtn;

    @FindBy(xpath = "//tr[contains(. ,'REQM')]")
    WebElement requirementID;

    @FindBy(xpath = "//button[contains(. ,'Add Risk')]")
    WebElement addRiskBtn;

    @FindBy(xpath = "//*[@rx-view-component-id='138ac502-c76a-46f1-9883-072e339918e7']//button[contains(. ,'Save')]")
    WebElement saveRiskBtn;

    @FindBy(xpath = "//tr[contains(. ,'RISK')]")
    WebElement riskID;

    @FindBy(xpath = "//button[contains(. ,'Add Stakeholder')]")
    WebElement addStakeholderBtn;

    @FindBy(xpath = "//*[@rx-view-component-id='a38a3fe0-9880-422e-819f-ce204fa0c178']//button[contains(. ,'Save')]")
    WebElement saveStakeholderBtn;

    @FindBy(xpath = "//*[contains(@class, 'a-toast__summary')]")
    WebElement toastMessage;

    @FindBy(xpath = "//a[contains(normalize-space(.), 'Project Import')]")
    WebElement projectImportLink;

    final String FIELD_ID_SUMMARY = "8";
    final String FIELD_ID_DESCRIPTION = "536875001";

    @SuppressWarnings("unchecked")
    private static void putFieldInstanceValue(Map<String, Object> fieldInstances, String fieldId, String value) {
        Object nested = fieldInstances.get(fieldId);
        if (nested instanceof Map) {
            ((Map<String, Object>) nested).put("value", value);
        }
    }

    public HPM_AppsCommonOperations createPortfolioRecord(String jsonFilePath, String username, String password, String summary) throws IOException {
        final String API_ENDPOINT = SolQAConstants.RecordInstance_Endpoint;

        // Get SAML token
        String samlToken = apiCommonOperations.getSamlToken(
                CommonMethods.readConfig("RSSO_URL"),
                username, password,
                CommonMethods.readConfig("setupName"),
                CommonMethods.readConfig("IS_URL")
        );

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json;
        try {
            json = mapper.readValue(new File(jsonFilePath), new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            log.error("Failed to read or parse JSON file: " + e.getMessage());
            throw e;
        }

        Object rawFieldInstances = json.get("fieldInstances");
        if (rawFieldInstances == null || !(rawFieldInstances instanceof Map)) {
            throw new IllegalArgumentException("Missing 'fieldInstances' in JSON");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> fieldInstances = (Map<String, Object>) rawFieldInstances;

        putFieldInstanceValue(fieldInstances, FIELD_ID_SUMMARY, summary);
        putFieldInstanceValue(fieldInstances, FIELD_ID_DESCRIPTION, summary);

        String updatedJson = mapper.writeValueAsString(json);
        log.debug("Request Body: " + updatedJson);
        log.debug("Endpoint: " + configProp.get("IS_URL") + API_ENDPOINT);

        Response response = RestAssured.given()
                .baseUri(configProp.get("IS_URL"))
                .header(SolQAConstants.AUTHORIZATION_HEADER, "Bearer " + samlToken)
                .header(SolQAConstants.X_Requested_By, SolQAConstants.XMLHttpRequest)
                .header(SolQAConstants.Accept, "*/*")
                .header(SolQAConstants.Accept_Encoding, "gzip, deflate, br")
                .header(SolQAConstants.Connection, SolQAConstants.keep_alive)
                .contentType(ContentType.JSON)
                .body(updatedJson)
                .post(API_ENDPOINT);

        Assert.assertEquals(response.getStatusCode(), 201, "Expected response code 201");
        return this;
    }

    public HPM_AppsCommonOperations deletePortfolioRecord(String username, String password, String recordID) {
        // FIX: Remove the trailing slash from the template definition.
        final String API_ENDPOINT_TEMPLATE = "/api/rx/application/record/recordinstance/com.fusiongbs.agility:Portfolio";
        String isUrl = CommonMethods.readConfig("IS_URL");

        String samlToken = apiCommonOperations.getSamlToken(
                CommonMethods.readConfig("RSSO_URL"),
                username, password,
                CommonMethods.readConfig("setupName"),
                isUrl
        );

        // Concatenate the template, a single forward slash, and the recordID.
        String apiEndpoint = API_ENDPOINT_TEMPLATE + "/" + recordID;
        log.debug("Endpoint: " + isUrl + apiEndpoint); // This will now log the correct URL

        Response response = RestAssured.given()
                .baseUri(isUrl)
                .header(SolQAConstants.AUTHORIZATION_HEADER, "Bearer " + samlToken)
                .header(SolQAConstants.Accept, "*/*")
                .header(SolQAConstants.Accept_Encoding, "gzip, deflate, br")
                .header(SolQAConstants.Connection, SolQAConstants.keep_alive)
                .header(SolQAConstants.X_Requested_By, SolQAConstants.XMLHttpRequest)
                .delete(apiEndpoint); // Pass the corrected relative path

        if (response.getStatusCode() != 204) {
            log.error("Delete failed. Status code: " + response.getStatusCode() + ". Response Body: " + response.getBody().asString());
            throw new RuntimeException("Expected response code 204, got: " + response.getStatusCode());
        }
        log.debug("Portfolio record deleted successfully. Record ID: " + recordID);
        return this;
    }

    public String searchPortfolioRecord(String jsonFilePath, String username, String password, String portfolioSummary) throws IOException {
        final String API_ENDPOINT = "/api/rx/application/datapage";
        String isUrl = CommonMethods.readConfig("IS_URL");

        String samlToken = apiCommonOperations.getSamlToken(
                CommonMethods.readConfig("RSSO_URL"),
                username, password,
                CommonMethods.readConfig("setupName"),
                isUrl
        );

        String content = Files.readString(Paths.get(jsonFilePath));
        JSONObject jsonObject = new JSONObject(content);
        JSONObject valuesObject = jsonObject.getJSONObject("values");
        valuesObject.put("queryExpression", "('" + FIELD_ID_SUMMARY + "' LIKE \"%" + portfolioSummary + "%\")");

        log.debug("Endpoint: " + isUrl + API_ENDPOINT);

        Response response = RestAssured.given()
                .baseUri(isUrl)
                .header(SolQAConstants.Authorization, "Bearer " + samlToken)
                .header(SolQAConstants.X_Requested_By, SolQAConstants.XMLHttpRequest)
                .header(SolQAConstants.Accept, "*/*")
                .header(SolQAConstants.Accept_Encoding, "gzip, deflate, br")
                .header(SolQAConstants.Connection, SolQAConstants.keep_alive)
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post(API_ENDPOINT);

        if (response.getStatusCode() != 200) {
            log.error("Search failed. Status code: " + response.getStatusCode());
            throw new RuntimeException("Expected response code 200, got: " + response.getStatusCode());
        }

        log.debug("Response Body: " + response.getBody().asString());
        JSONObject responseJson = new JSONObject(response.getBody().asString());
        JSONArray dataArray = responseJson.getJSONArray("data");
        if (dataArray.length() == 0) {
            throw new RuntimeException("No portfolio records found.");
        }
        JSONObject firstRecord = dataArray.getJSONObject(0);
        return firstRecord.getString("179");
    }

    public String createRecordUsingAPI(String jsonFilePath, WebDriver driver) throws IOException {

        final String API_ENDPOINT = "/api/rx/application/record/recordinstance";
        // Get SAML token
        Map<String, String> cookieValues = apiCommonOperations.getSessionCookies(driver);
        String samlToken = cookieValues.get("saml_token");
        log.debug("SAML Token: " + samlToken);

        String content = Files.readString(Paths.get(jsonFilePath));
        JSONObject jsonObject = new JSONObject(content);

        Response response = RestAssured.given()
                .baseUri(configProp.get("IS_URL"))
                .header("Authorization", "Bearer " + samlToken)
                .header("X-Requested-By", "XMLHttpRequest")
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post(API_ENDPOINT);

        if (response.getStatusCode() != 201) {
            log.error("Error response: " + response.getBody().asString());
            throw new RuntimeException("Expected response code 201, got: " + response.getStatusCode());
        }

        // Extract record ID from Location header
        String locationHeader = response.getHeader("Location");
        if (locationHeader != null && !locationHeader.isEmpty()) {
            String recordId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
            log.debug("Created record ID: " + recordId);
            return recordId;
        } else {
            throw new RuntimeException("Location header not found in response");
        }
    }

    public HPM_AppsCommonOperations createRecordUsingAPI(String jsonFilePath, String username, String password) throws IOException {

        final String API_ENDPOINT = SolQAConstants.RecordInstance_Endpoint;

        // Get SAML token
        String samlToken = apiCommonOperations.getSamlToken(
                CommonMethods.readConfig("RSSO_URL"),
                username, password,
                CommonMethods.readConfig("setupName"),
                CommonMethods.readConfig("IS_URL")
        );

        String content = Files.readString(Paths.get(jsonFilePath));
        JSONObject jsonObject = new JSONObject(content);

        Response response = RestAssured.given()
                .baseUri(configProp.get("IS_URL"))
                .header(SolQAConstants.Authorization, "Bearer " + samlToken)
                .header(SolQAConstants.X_Requested_By, SolQAConstants.XMLHttpRequest)
                .header(SolQAConstants.Accept, "*/*")
                .header(SolQAConstants.Accept_Encoding, "gzip, deflate, br")
                .header(SolQAConstants.Connection, SolQAConstants.keep_alive)
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post(API_ENDPOINT);

        if (response.getStatusCode() != 201) {
            log.error("Error response: " + response.getBody().asString());
            throw new RuntimeException("Expected response code 201, got: " + response.getStatusCode());
        }
        return this;
    }

    public String searchRecord(String recordType, String jsonFilePath, String username, String password, String portfolioSummary) throws IOException {
        final String API_ENDPOINT = "/api/rx/application/datapage";
        String isUrl = CommonMethods.readConfig("IS_URL");

        String samlToken = apiCommonOperations.getSamlToken(
                CommonMethods.readConfig("RSSO_URL"),
                username, password,
                CommonMethods.readConfig("setupName"),
                isUrl
        );

        String content = Files.readString(Paths.get(jsonFilePath));
        JSONObject jsonObject = new JSONObject(content);
        JSONObject valuesObject = jsonObject.getJSONObject("values");
        valuesObject.put("queryExpression", "('" + FIELD_ID_SUMMARY + "' LIKE \"%" + portfolioSummary + "%\")");
        String recordForm = null;
        switch (recordType) {
            case SolQAConstants.Portfolio:
                recordForm = SolQAConstants.HPM_Portfolio_Record;
                break;
            case SolQAConstants.Idea:
                recordForm = SolQAConstants.HPM_Idea_Record;
                break;
            case SolQAConstants.Demand:
                recordForm = SolQAConstants.HPM_Demand_Record;
                break;
            case SolQAConstants.Project:
                recordForm = SolQAConstants.HPM_Project_Record;
                break;
            case SolQAConstants.Program:
                recordForm = SolQAConstants.HPM_Program_Record;
                break;
            default:
                throw new IllegalArgumentException("Invalid record type: " + recordType);
        }
        valuesObject.put("recorddefinition", recordForm);
        log.debug("Endpoint: " + isUrl + API_ENDPOINT);

        Response response = RestAssured.given()
                .baseUri(isUrl)
                .header(SolQAConstants.Authorization, "Bearer " + samlToken)
                .header(SolQAConstants.X_Requested_By, SolQAConstants.XMLHttpRequest)
                .header(SolQAConstants.Accept, "*/*")
                .header(SolQAConstants.Accept_Encoding, "gzip, deflate, br")
                .header(SolQAConstants.Connection, SolQAConstants.keep_alive)
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post(API_ENDPOINT);

        if (response.getStatusCode() != 200) {
            log.error("Search failed. Status code: " + response.getStatusCode());
            throw new RuntimeException("Expected response code 200, got: " + response.getStatusCode());
        }

        log.debug("Response Body: " + response.getBody().asString());
        JSONObject responseJson = new JSONObject(response.getBody().asString());
        JSONArray dataArray = responseJson.getJSONArray("data");
        if (dataArray.length() == 0) {
            throw new RuntimeException("No portfolio records found.");
        }
        JSONObject firstRecord = dataArray.getJSONObject(0);
        return firstRecord.getString("179");
    }

    public HPM_AppsCommonOperations deleteRecord(String recordType, String username, String password, String recordID) {
        // FIX: Remove the trailing slash from the template definition.
        final String API_ENDPOINT_TEMPLATE = SolQAConstants.RecordInstance_Endpoint + "/";
        String recordForm;
        switch (recordType) {
            case SolQAConstants.Portfolio:
                recordForm = SolQAConstants.HPM_Portfolio_Record;
                break;
            case SolQAConstants.Idea:
                recordForm = SolQAConstants.HPM_Idea_Record;
                break;
            case SolQAConstants.Demand:
                recordForm = SolQAConstants.HPM_Demand_Record;
                break;
            case SolQAConstants.Project:
                recordForm = SolQAConstants.HPM_Project_Record;
                break;
            case SolQAConstants.Program:
                recordForm = SolQAConstants.HPM_Program_Record;
                break;
            default:
                throw new IllegalArgumentException("Invalid record type: " + recordType);
        }
        String isUrl = CommonMethods.readConfig("IS_URL");

        String samlToken = apiCommonOperations.getSamlToken(
                CommonMethods.readConfig("RSSO_URL"),
                username, password,
                CommonMethods.readConfig("setupName"),
                isUrl
        );

        // Concatenate the template, a single forward slash, and the recordID.
        String apiEndpoint = API_ENDPOINT_TEMPLATE + recordForm + "/" + recordID;
        log.debug("Endpoint: " + isUrl + apiEndpoint); // This will now log the correct URL

        Response response = RestAssured.given()
                .baseUri(isUrl)
                .header(SolQAConstants.Authorization, "Bearer " + samlToken)
                .header(SolQAConstants.Accept, "*/*")
                .header(SolQAConstants.Accept_Encoding, "gzip, deflate, br")
                .header(SolQAConstants.Connection, SolQAConstants.keep_alive)
                .header(SolQAConstants.X_Requested_By, SolQAConstants.XMLHttpRequest)
                .delete(apiEndpoint); // Pass the corrected relative path

        if (response.getStatusCode() != 204) {
            log.error("Delete failed. Status code: " + response.getStatusCode() + ". Response Body: " + response.getBody().asString());
            throw new RuntimeException("Expected response code 204, got: " + response.getStatusCode());
        }
        log.debug("Portfolio record deleted successfully. Record ID: " + recordID);
        return this;
    }

    public void deleteRecord(String recordForm, WebDriver driver, String recordID) {
        // FIX: Remove the trailing slash from the template definition.
        final String API_ENDPOINT_TEMPLATE = "/api/rx/application/record/recordinstance/";
        String isUrl = CommonMethods.readConfig("IS_URL");

        Map<String, String> cookieValues = apiCommonOperations.getSessionCookies(driver);
        String samlToken = cookieValues.get("saml_token");
        log.debug("SAML Token: " + samlToken);

        // Concatenate the template, a single forward slash, and the recordID.
        String apiEndpoint = API_ENDPOINT_TEMPLATE + recordForm + "/" + recordID;
        log.debug("Endpoint: " + isUrl + apiEndpoint); // This will now log the correct URL

        Response response = RestAssured.given()
                .baseUri(isUrl)
                .header("Authorization", "Bearer " + samlToken)
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("X-Requested-By", "XMLHttpRequest")
                .delete(apiEndpoint); // Pass the corrected relative path

        if (response.getStatusCode() != 204) {
            log.error("Delete failed. Status code: " + response.getStatusCode() + ". Response Body: " + response.getBody().asString());
            throw new RuntimeException("Expected response code 204, got: " + response.getStatusCode());
        }
        log.debug("Portfolio record deleted successfully. Record ID: " + recordID);
    }

    public HPM_AppsCommonOperations navigateCreateHPMRecord(String HPMRecordType) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createHPMRecordButton, 20), "Create button is not visible on the page.");
        createHPMRecordButton.click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(hpmRecordTypeDropdown, HPMRecordType)), 20), "HPM Record type dropdown is not visible.");
        commonWaits.clickWithoutInterception(By.xpath(String.format(hpmRecordTypeDropdown, HPMRecordType)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHPM);
        return this;
    }

    public HPM_AppsCommonOperations setTextField(String fieldName, String value) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(textInputField + "[last()]", fieldName), 30), "Text input field with name '" + fieldName + "' is not visible.");
        Assert.assertTrue(commonWaits.waitForElementClickable(String.format(textInputField + "[last()]", fieldName), 30), "Text input field with name '" + fieldName + "' is not clickable.");
        commonWaits.keyClearAndType(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(textInputField + "[last()]", fieldName))), value);
        return this;
    }

    public HPM_AppsCommonOperations setCKE_EditorField(String fieldName, String value) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(ckeEditorField, fieldName), 20), "CKE editor field with name '" + fieldName + "' is not visible.");
        Assert.assertTrue(commonWaits.waitForElementClickable(String.format(ckeEditorField, fieldName), 15), "CKE editor field with name '" + fieldName + "' is not present or not clickable.");
        commonWaits.keyClearAndType(commonWaits.waitForByElementToBeVisible(By.xpath(String.format(ckeEditorField, fieldName))), value);
        return this;
    }

    public HPM_AppsCommonOperations setDropdownField(String fieldName, String option) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(dropdownField, fieldName), 20), "Dropdown field with name '" + fieldName + "' is not visible.");
        WebElement dropdown = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownField, fieldName)));
        dropdown.click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(dropdownOptions, option), 20), "Dropdown option '" + option + "' is not visible.");
        WebElement optionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropdownOptions, option)));
        optionElement.click();
        return this;
    }

    public HPM_AppsCommonOperations setCounterField(String fieldName, String value) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(counterFieldInput, fieldName), 20), "Counter field with name '" + fieldName + "' is not visible.");
        Assert.assertTrue(commonWaits.waitForElementClickable(String.format(counterFieldInput, fieldName), 10), "Counter field with name '" + fieldName + "' is not present.");
        WebElement counterField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(counterFieldInput, fieldName)));
        counterField.clear();
        counterField.sendKeys(value);
        return this;
    }

    public HPM_AppsCommonOperations setDateField(String fieldName, String dateValue, String dateFormat) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(dateFieldInput, fieldName), 20), "Date field with name '" + fieldName + "' is not visible.");
        Assert.assertTrue(commonWaits.waitForElementClickable(String.format(dateFieldInput, fieldName), 10), "Date field with name '" + fieldName + "' is not present.");
        WebElement dateField = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dateFieldInput, fieldName)));
        dateField.clear();
        dateField.sendKeys(getFutureDate(Integer.parseInt(dateValue), dateFormat));
        return this;
    }

    public HPM_AppsCommonOperations setTypeHead(String fieldName, String option) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(typeheadInput, fieldName)), 20), "Typehead field with name '" + fieldName + "' is not present.");
        WebElement input = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadInput, fieldName)));
        input.sendKeys(option);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(typeheadOption, option)), 20), "Typehead option '" + option + "' is not visible.");
        WebElement optionElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(typeheadOption, option)));
        optionElement.click();
        return this;
    }

    public void uploadFile(File file, WebElement webElement) {
        log.debug("Uploading file: " + file.getAbsolutePath());
        webElement.sendKeys(file.getAbsolutePath());
        commonWaits.waitForAngularRequestsToFinish();
    }

    public HPM_AppsCommonOperations fillHPMRecordTemplate(String recordType, Map<Object, String> isTestData) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHPM);
        switch (recordType) {

            case SolQAConstants.Portfolio:
                if (isTestData.get("Summary") != null) {
                    setTextField(SolQAConstants.SUMMARY_LABEL + "(required)", isTestData.get("Summary"));
                }
                if (isTestData.get("Description") != null) {
                    commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHPM);
                    setCKE_EditorField(SolQAConstants.DESCRIPTION, isTestData.get("Description"));
                }
                if (isTestData.get("Scope") != null) {
                    setDropdownField(SolQAConstants.Scope, isTestData.get("Scope"));
                }
                if (isTestData.get("Cost Center") != null) {
                    setDropdownField(SolQAConstants.Cost_Center, isTestData.get("Cost Center"));
                }
                if (isTestData.get("Idea Review Threshold") != null) {
                    setCounterField(SolQAConstants.Idea_Review_Threshold, isTestData.get("Idea Review Threshold"));
                }
                break;

            case SolQAConstants.Idea:
                if (isTestData.get("Summary") != null) {
                    setTextField(SolQAConstants.SUMMARY_LABEL + "(required)", isTestData.get("Summary"));
                }
                if (isTestData.get("Description") != null) {
                    setCKE_EditorField(SolQAConstants.DESCRIPTION, isTestData.get("Description"));
                }
                if (isTestData.get("Benefits") != null) {
                    setCKE_EditorField(SolQAConstants.Benefits, isTestData.get("Benefits"));
                }
                if (isTestData.get("Idea Problem Solved") != null) {
                    setCKE_EditorField(SolQAConstants.Problem_Solved, isTestData.get("Idea Problem Solved"));
                }
                if (isTestData.get("Associate Record") != null) {
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateRecordButton, 20), "Associate Record button is not visible.");
                    commonWaits.waitForElementVisible(associateRecordButton).click();
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(associatedRecordRadioBtn), 20), "Associated Record radio button is not visible.");
                    commonWaits.clickWithoutInterception(By.xpath(associatedRecordRadioBtn));
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateSelectButton, 20), "Associate Select button is not visible.");
                    commonWaits.clickWithoutInterception(associateSelectButton);
                }
                break;

            case SolQAConstants.Demand:
                if (isTestData.get("Summary") != null) {
                    setTextField(SolQAConstants.SUMMARY_LABEL + "(required)", isTestData.get("Summary"));
                }
                if (isTestData.get("Description") != null) {
                    setCKE_EditorField(SolQAConstants.DESCRIPTION, isTestData.get("Description"));
                }
                if (isTestData.get("Priority") != null) {
                    setDropdownField(SolQAConstants.PRIORITY, isTestData.get("Priority"));
                }
                if (isTestData.get("Type") != null) {
                    setDropdownField(SolQAConstants.TYPE, isTestData.get("Type"));
                }
                if (isTestData.get("Strategy") != null) {
                    setDropdownField(SolQAConstants.Strategy, isTestData.get("Strategy"));
                }
                if (isTestData.get("Cost Center") != null) {
                    setDropdownField(SolQAConstants.Cost_Center, isTestData.get("Cost Center"));
                }
                if (isTestData.get("Corporate Alignment") != null) {
                    setDropdownField(SolQAConstants.Corporate_Alignment, isTestData.get("Corporate Alignment"));
                }
                if (isTestData.get("Goal") != null) {
                    setTextField(SolQAConstants.Goal, isTestData.get("Goal"));
                }
                if (isTestData.get("Total Risk") != null) {
                    setCounterField(SolQAConstants.Total_Risk, isTestData.get("Total Risk"));
                }
                if (isTestData.get("Start Date") != null) {
                    setDateField(SolQAConstants.Start_Date, isTestData.get("Start Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("Target Date") != null) {
                    setDateField(SolQAConstants.Target_Date, isTestData.get("Target Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("Associate Record") != null) {
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateRecordButton, 20), "Associate Record button is not visible.");
                    commonWaits.waitForElementVisible(associateRecordButton).click();
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(associatedRecordRadioBtn), 20), "Associated Record radio button is not visible.");
                    commonWaits.clickWithoutInterception(By.xpath(associatedRecordRadioBtn));
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateSelectButton, 20), "Associate Select button is not visible.");
                    commonWaits.clickWithoutInterception(associateSelectButton);
                }
                break;

            case SolQAConstants.Project:
                if (isTestData.get("Summary") != null) {
                    setTextField(SolQAConstants.SUMMARY_LABEL + "(required)", isTestData.get("Summary"));
                }
                if (isTestData.get("Description") != null) {
                    setCKE_EditorField(SolQAConstants.DESCRIPTION, isTestData.get("Description"));
                }
                if (isTestData.get("Behaviour") != null) {
                    setDropdownField(SolQAConstants.Behavior, isTestData.get("Behaviour"));
                }
                if (isTestData.get("Priority") != null) {
                    setDropdownField(SolQAConstants.PRIORITY, isTestData.get("Priority"));
                }

                if (isTestData.get("Strategic") != null) {
                    setDropdownField(SolQAConstants.Strategic, isTestData.get("Strategic"));
                }
                if (isTestData.get("Cost Center") != null) {
                    setDropdownField(SolQAConstants.Cost_Center, isTestData.get("Cost Center"));
                }
                if (isTestData.get("Start Date") != null) {
                    setDateField(SolQAConstants.Start_Date, isTestData.get("Start Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("End Date") != null) {
                    setDateField(SolQAConstants.End_Date, isTestData.get("End Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("Target Date") != null) {
                    setDateField(SolQAConstants.Target_Date, isTestData.get("Target Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("Associate Record") != null) {
                    if (commonWaits.isElementPresent(associateRecordCompactBtn)) {
                        commonWaits.waitForElementVisible(associateRecordCompactBtn).click();
                        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateRecordBtnInProject, 20), "Associated Record radio button is not visible.");
                        commonWaits.clickWithoutInterception(associateRecordBtnInProject);
                    } else {
                        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateRecordButton, 20), "Associate Record button is not visible.");
                        commonWaits.clickWithoutInterception(associateRecordButton);
                    }
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(associatedRecordRadioBtn), 20), "Associated Record radio button is not visible.");
                    commonWaits.clickWithoutInterception(By.xpath(associatedRecordRadioBtn));
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateSelectButton, 20), "Associate Select button is not visible.");
                    commonWaits.clickWithoutInterception(associateSelectButton);
                }
                break;

            case SolQAConstants.Program:
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHPM);
                if (isTestData.get("Summary") != null) {
                    setTextField(SolQAConstants.SUMMARY_LABEL + "(required)", isTestData.get("Summary"));
                }
                if (isTestData.get("Description") != null) {
                    setCKE_EditorField(SolQAConstants.DESCRIPTION, isTestData.get("Description"));
                }
                if (isTestData.get("Cost Center") != null) {
                    setDropdownField(SolQAConstants.Cost_Center, isTestData.get("Cost Center"));
                }
                if (isTestData.get("Start Date") != null) {
                    setDateField(SolQAConstants.Start_Date, isTestData.get("Start Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("End Date") != null) {
                    setDateField(SolQAConstants.End_Date, isTestData.get("End Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("Target Date") != null) {
                    setDateField(SolQAConstants.Target_Date, isTestData.get("Target Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("Associate Record") != null) {
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateRecordButton, 20), "Associate Record button is not visible.");
                    commonWaits.waitForElementVisible(associateRecordButton).click();
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(associatedRecordRadioBtn), 20), "Associated Record radio button is not visible.");
                    commonWaits.clickWithoutInterception(By.xpath(associatedRecordRadioBtn));
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateSelectButton, 20), "Associate Select button is not visible.");
                    commonWaits.clickWithoutInterception(associateSelectButton);
                }
                break;

            case SolQAConstants.Resource_Request:
                if (isTestData.get("Request Summary") != null) {
                    setTextField(SolQAConstants.Request_Summary + "(required)", isTestData.get("Request Summary"));
                }
                if (isTestData.get("Description") != null) {
                    setCKE_EditorField(SolQAConstants.DESCRIPTION, isTestData.get("Description"));
                }
                if (isTestData.get("Request Type") != null) {
                    setDropdownField(SolQAConstants.Request_Type, isTestData.get("Request Type"));
                }
                if (isTestData.get("Priority") != null) {
                    setDropdownField(SolQAConstants.PRIORITY, isTestData.get("Priority"));
                }
                if (isTestData.get("Scope") != null) {
                    setDropdownField(SolQAConstants.Scope, isTestData.get("Scope"));
                }
                if (isTestData.get("Source Application") != null) {
                    setDropdownField(SolQAConstants.Source_Application, isTestData.get("Source Application"));
                }
                if (isTestData.get("Entity Type") != null) {
                    setDropdownField(SolQAConstants.Entity_Type, isTestData.get("Entity Type"));
                }
                if (isTestData.get("External Entity Reference") != null) {
                    if (commonWaits.isElementPresent(associateExternalEntityButton)) {
                        commonWaits.waitForElementVisible(associateExternalEntityButton).click();
                        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateRecordBtnInResourceRequest, 20), "Associated Record radio button is not visible.");
                        commonWaits.clickWithoutInterception(associateRecordBtnInResourceRequest);
                    } else {
                        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), associateRecordBtnInResourceRequest, 20), "Associate Record button is not visible.");
                        commonWaits.clickWithoutInterception(associateRecordBtnInResourceRequest);
                    }
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), externalEntityBtn, 20), "Associated Record radio button is not visible.");
                    commonWaits.clickWithoutInterception(externalEntityBtn);
                    setTextField(SolQAConstants.External_ID, isTestData.get("External Entity Reference External ID") + getCurrentDateTime("MM/dd/yyyy HH:mm:ss"));
                    setTextField(SolQAConstants.SUMMARY_LABEL, isTestData.get("External Entity Reference Summary"));
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), saveExternalEntityBtn, 20), "Save button is not visible on external entity form.");
                    commonWaits.clickWithoutInterception(saveExternalEntityBtn);
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(associatedRecordRadioBtn), 20), "Associated Record radio button is not visible.");
                    commonWaits.clickWithoutInterception(By.xpath(associatedRecordRadioBtn));
                    Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), selectEntityBtn, 20), "Select button is not visible on associate record popup.");
                    commonWaits.clickWithoutInterception(selectEntityBtn);
                }
                if (isTestData.get("Start Date") != null) {
                    setDateField(SolQAConstants.Start_Date, isTestData.get("Start Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("End Date") != null) {
                    setDateField(SolQAConstants.End_Date, isTestData.get("End Date"), SolQAConstants.SimpleDateFormat);
                }
                if (isTestData.get("Assignment Type Preference") != null) {
                    setDropdownField(SolQAConstants.Assignment_Type_Preference, isTestData.get("Assignment Type Preference"));
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid record type: " + recordType);
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHPM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), saveHPMRecordButton, 20), "Save button is not visible on the page.");
        Assert.assertTrue(commonWaits.waitForElementClickableAsBoolean(saveHPMRecordButton, 20), "Save button is not clickable, required fields might be pending.");
        commonWaits.waitForSometime(commonWaits.moreDelay);
        commonWaits.waitForElementClickable(saveHPMRecordButton).click();

        return this;
    }

    public String getHPMRecordTicketID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHPM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), prefixDisplayIDField, 30), "Display ID field is not visible on the page.");
        return commonWaits.waitForElementVisible(prefixDisplayIDField).getText().trim();
    }

    public String getResourceRequestTicketID() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHPM);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), displayIDField, 20), "Display ID field is not visible on the page.");
        return commonWaits.waitForElementVisible(displayIDField).getText().trim();
    }

    public HPM_AppsCommonOperations addRequirementToDemand(Map<Object, String> DemandTestData) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(Tab, SolQAConstants.Requirement)), 20), "Requirement tab is not visible.");
        WebElement requirementTab = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(Tab, SolQAConstants.Requirement)));
        if (requirementTab.getAttribute(SolQAConstants.ATTRIBUTE_CLASS).contains(SolQAConstants.ACTIVE_CLASS)) {
            log.debug("Requirements tab is already active");
        } else {
            requirementTab.click();
            commonWaits.waitForAngularRequestsToFinish();
        }
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addRequirementBtn, 20), "Add Requirement button is not visible on Requirement tab.");
        commonWaits.waitForElementVisible(addRequirementBtn).click();
        setTextField(SolQAConstants.SUMMARY_LABEL + "(required)", DemandTestData.get("Requirement Summary"));
        setCKE_EditorField(SolQAConstants.DESCRIPTION, DemandTestData.get("Requirement Description"));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), saveRequirementBtn, 20), "Save button is not visible on Requirement form.");
        commonWaits.waitForElementClickable(saveRequirementBtn, 10).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), requirementID, 5), "Requirement ID is not visible after saving requirement.");
        return this;
    }

    public HPM_AppsCommonOperations addRiskToDemand(Map<Object, String> DemandTestData) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(Tab, SolQAConstants.Risk)), 20), "Risk tab is not visible.");
        WebElement riskTab = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(Tab, SolQAConstants.Risk)));
        if (riskTab.getAttribute(SolQAConstants.ATTRIBUTE_CLASS).contains(SolQAConstants.ACTIVE_CLASS)) {
            log.debug("Risk tab is already active");
        } else {
            riskTab.click();
            commonWaits.waitForAngularRequestsToFinish();
        }
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addRiskBtn, 20), "Add Risk button is not visible on Risk tab.");
        commonWaits.waitForElementVisible(addRiskBtn).click();
        setTextField(SolQAConstants.SUMMARY_LABEL + "(required)", DemandTestData.get("Risk Summary"));
        setCKE_EditorField(SolQAConstants.DESCRIPTION, DemandTestData.get("Risk Description"));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), saveRiskBtn, 20), "Save button is not visible on Risk form.");
        commonWaits.waitForElementClickable(saveRiskBtn, 10).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), riskID, 5), "Risk ID is not visible after saving risk.");
        return this;
    }

    public HPM_AppsCommonOperations addStakeholderToDemand(Map<Object, String> DemandTestData) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(String.format(Tab, SolQAConstants.Stakeholder)), 20), "Stakeholder tab is not visible.");
        WebElement stakeholderTab = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(Tab, SolQAConstants.Stakeholder)));
        if (stakeholderTab.getAttribute(SolQAConstants.ATTRIBUTE_CLASS).contains(SolQAConstants.ACTIVE_CLASS)) {
            log.debug("Stakeholder tab is already active");
        } else {
            stakeholderTab.click();
            commonWaits.waitForAngularRequestsToFinish();
        }
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), addStakeholderBtn, 20), "Add Stakeholder button is not visible on Stakeholder tab.");
        commonWaits.waitForElementVisible(addStakeholderBtn).click();
        setTypeHead(SolQAConstants.Stakeholder, DemandTestData.get("Stakeholder"));
        setDropdownField(SolQAConstants.Main_Role + "(required)", DemandTestData.get("Main Role"));
        setDropdownField(SolQAConstants.Approval_Required + "(required)", DemandTestData.get("Approval Required"));
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), saveStakeholderBtn, 20), "Save button is not visible on Stakeholder form.");
        commonWaits.waitForElementClickable(saveStakeholderBtn, 10).click();
        Assert.assertTrue(commonWaits.waitForElementVisible(toastMessage, 3).getText().contains(SolQAConstants.Success), "Success toast message is not displayed after saving stakeholder.");
        return this;
    }

    public HPM_AppsCommonOperations searchProjectImportTicket(String importName) {
        commonWaits.waitForElementVisible(searchInputBox).clear();
        searchInputBox.sendKeys(importName);
        commonWaits.waitForElementVisible(searchButton).click();
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public String getProjectImportDisplayID() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), searchedRecordLink, 20), "Searched record link is not visible after searching for project import.");
        return commonWaits.waitForElementVisible(searchedRecordLink).getText().trim();
    }

    public HPM_AppsCommonOperations verifyHPMLogin() {
        commonWaits.waitForPageFullyLoaded(10, 2);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), hpmHeader, 30), "HPM header is not visible, login might have failed.");
        return this;
    }

    public HPM_AppsCommonOperations navigateProjectImport() {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), createHPMRecordButton, 20), "Create button is not visible on the page.");
        commonWaits.waitForElementVisible(createHPMRecordButton).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), projectImportLink, 20), "Project Import link is not visible on create record dropdown.");
        commonWaits.waitForElementVisible(projectImportLink).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appIS);
        return this;
    }

}