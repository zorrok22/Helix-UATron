package pages.ITOM.Intelligent_Integrations;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolqaLogger;

import java.util.List;
import java.util.Map;

public class SWPIntelligentIntegration extends BasePage {

    SolqaLogger log = new SolqaLogger(SWPIntelligentIntegration.class.getName());
    CommonWaits commonWaits;
    String appName = null;
    WebDriver driver;

    public String bmcHelixPage="(//header//nav//span/span)[2]";
    @FindBy(xpath = "//span[text()='Sources']/following-sibling::span//button[contains(@class, 'MuiButtonBase-root') and @aria-label='Add']")
    WebElement addSourceButton;
    @FindBy(xpath = "//input[@id='#/properties/accessKey-input']")
    WebElement accessKeyInput;
    @FindBy(xpath = "//input[@id='#/properties/secretKey-input']")
    WebElement secretKeyInput;
    @FindBy(xpath = "//input[@id='#/properties/region-input']")
    WebElement regionInput;
    @FindBy(xpath = "//input[@id='#/properties/host-input']")
    WebElement hostInput;

    @FindBy(xpath = "//input[@id='#/properties/workspaceId-input']")
    WebElement workspace;

    @FindBy(xpath = "//input[@id='#/properties/account-input']")
    WebElement accountInput;

    @FindBy(xpath = "//input[@id='#/properties/apiToken-input']")
    WebElement tokenInput;
    @FindBy(xpath = "//input[@id='#/properties/port-input']")
    WebElement portInput;
    @FindBy(xpath = "//*[@id='#/properties/apiKey-input']")
    WebElement apiKeyInput;

    @FindBy(xpath = "//input[@id='#/properties/applicationKey-input']")
    WebElement appKeyInput;

    @FindBy(xpath = "//input[@id='#/properties/accountId-input']")
    WebElement accountIdInput;
    @FindBy(xpath = "//input[@id='#/properties/graphqlApiKey-input']")
    WebElement graphqlApiKeyInput;
    @FindBy(xpath = "//*[text()='Validate and Create']/ancestor::button")
    WebElement validateAndCreateButton;
    @FindBy(xpath = "//*[text()='Save and Start Stream']/parent::button")
    WebElement saveAndStartStreamButton;
    @FindBy(xpath = "//input[@id='#/properties/username-input']")
    WebElement usernameInput;
    @FindBy(xpath = "//input[@id='#/properties/password-input']")
    WebElement passwordInput;
    @FindBy(xpath = "//a[@href='/swpui/streams']")
    WebElement streamsPageLink;
    @FindBy(xpath = "//a[@href='/swpui/channels']")
    WebElement connectorsPageLink;
    @FindBy(xpath = "//button[contains(@class,'MuiButton-textSecondary')]")
    List<WebElement> playButtons;
    @FindBy(xpath = "//div[contains(. ,'Streams')]//*[@aria-label='Search']")
    WebElement streamsSearchBox;
    @FindBy(xpath = "//*[text()='Edit']/parent::button")
    WebElement editButton;
    @FindBy(xpath = "//*[text()='Confirm Remove']/parent::button")
    WebElement confirmRemoveButton;
    @FindBy(xpath = "//*[text()='Save & Close']/ancestor::button")
    WebElement saveAndCloseButton;

    @FindBy(xpath = "//*[contains(text(),'Datadog Metrics')]/parent::div")
    WebElement datadogMetricsCard;

    @FindBy(xpath = "(//h6[contains(text(),'Stackify Metrics')]/parent::div)[3]")
    WebElement stackifyMetricsCard;

    @FindBy(xpath = "//*[contains(text(),'AppDynamics Events')]/parent::div")
    WebElement appDynamicsEventsCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Events')]/following::*/button/span[contains(text(),'Collector Configuration')])[1]")
    WebElement collectorConfigForEventsCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Events')]/following::*//p[contains(text(),'Select...')]/parent::div)[1]")
    WebElement AppSelectorForAppDynamicsEventsCard;



    @FindBy(xpath = "//*[contains(text(),'AppDynamics Health')]/parent::div")
    WebElement appDynamicsHealthCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Health')]/following::*/button/span[contains(text(),'Collector Configuration')])[1]")
    WebElement collectorConfigForHealthCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Health')]/following::*//p[contains(text(),'Select...')]/parent::div)[1]")
    WebElement AppSelectorForAppDynamicsHealthCard;



    @FindBy(xpath = "//*[contains(text(),'AppDynamics Metrics')]/parent::div")
    WebElement appDynamicsMetricsCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Metrics')]/following::*/button/span[contains(text(),'Collector Configuration')])[1]")
    WebElement collectorConfigForMetricCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Metrics')]/following::*//p[contains(text(),'Select...')]/parent::div)[1]")
    WebElement AppSelectorForAppDynamicsMetricsCard;

    @FindBy(xpath = "//*[contains(text(),'AppDynamics Topology')]/parent::div")
    WebElement appDynamicsTopologyCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Topology')]/following::*/button/span[contains(text(),'Collector Configuration')])[1]")
    WebElement collectorConfigForTopolocyCard;

    @FindBy(xpath = "(//*[contains(text(),'AppDynamics Topology')]/following::*//p[contains(text(),'Select...')]/parent::div)[1]")
    WebElement AppSelectorForAppDynamicsTopologyCard;


    @FindBy(xpath = "//p[contains(text(),'Select...')]/parent::div")
    WebElement selectMetricsDropdown;

    @FindBy(xpath = "//div[@class='MuiCardContent-root']")
    WebElement MultiCardRoot;

    @FindBy(xpath = "//input[@id='#/properties/selectors-input']")
    WebElement selectorsInput;

    String createCollectorsButton = "//*[text()='Create Collectors']/parent::button";
    String createDistributorsButton = "//*[text()='Create Distributors']/parent::button";
    String connectorGearIcon = "//*[contains(text(), '%s')]//ancestor::div[@class='MuiCardHeader-root']//*[@title='Configure Mediator']/ancestor::button";
    String removeConnectorButton = "//*[contains(text(),'%s')]/parent::div/preceding-sibling::div//button";
    String allowUnsignedCertificates = "//label[@id='#/properties/allowUnsignedCertificate']";
    String usesHttps = "//label[@id='#/properties/usesHttps']";

    public SWPIntelligentIntegration(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        commonWaits = new CommonWaits();
    }

    public SWPIntelligentIntegration(){
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    public void verifyIntelligentIntegrationPageLaunchedSuccessfully(){
        appName = commonWaits.waitForByElementToBeVisible(By.xpath(bmcHelixPage)).getText().trim();
        log.info("----- Opened application name - "+ appName);
        Assert.assertEquals(appName, "Intelligent Integrations", "Intelligent Integrations application is not launched successfully");
    }

    public void addAndConfigureConnector(Map<Object, String> connectorData) {
        String connectorName = connectorData.get("connectorName");
        if (connectorName == null) {
            log.error("Connector name is missing in connectorData");
            throw new IllegalArgumentException("Connector name is required");
        }
        log.info("Adding and configuring connector: " + connectorName);
        clickOnAddConnector();
        selectConnectorType(connectorName);

        switch (connectorName) {
            case "AWS CloudWatch":
                fillInput(accessKeyInput, connectorData.get("accessKey"));
                fillInput(secretKeyInput, connectorData.get("secretKey"));
                fillInput(regionInput, connectorData.get("region"));
                break;
            case "Icinga":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(portInput, connectorData.get("port"));
                fillInput(usernameInput, connectorData.get("userName"));
                fillInput(passwordInput, connectorData.get("password"));
                toggleCheckbox(allowUnsignedCertificates);
                break;
            case "New Relic":
                fillInput(apiKeyInput, connectorData.get("accessKey"));
                fillInput(accountIdInput, connectorData.get("accountId"));
                fillInput(graphqlApiKeyInput, connectorData.get("graphqlApiKey"));
                break;
            case "SolarWinds NPM":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(portInput, connectorData.get("port"));
                fillInput(usernameInput, connectorData.get("userName"));
                fillInput(passwordInput, connectorData.get("password"));
                toggleCheckbox(allowUnsignedCertificates);
                toggleCheckbox(usesHttps);
                break;
            case "Splunk Enterprise":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(portInput, connectorData.get("port"));
                fillInput(usernameInput, connectorData.get("userName"));
                fillInput(passwordInput, connectorData.get("password"));
                toggleCheckbox(usesHttps);
                break;
            case "Stackify Metrics":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(portInput, connectorData.get("port"));
                fillInput(apiKeyInput, connectorData.get("stackifyKey"));
                toggleCheckbox(usesHttps);
                break;
            case "VMware vROps":
            case "Zabbix":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(portInput, connectorData.get("port"));
                fillInput(usernameInput, connectorData.get("userName"));
                fillInput(passwordInput, connectorData.get("password"));
                break;

            case "Dynatrace":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(tokenInput, connectorData.get("token"));
                toggleCheckbox(allowUnsignedCertificates);
                break;

            case "Datadog":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(tokenInput, connectorData.get("token"));
                fillInput(appKeyInput, connectorData.get("appKey"));
                break;
            case "AppDynamics":
                fillInput(hostInput, connectorData.get("host"));
                fillInput(accountInput, connectorData.get("accountName"));
                fillInput(usernameInput, connectorData.get("username"));
                fillInput(passwordInput, connectorData.get("password"));
                break;

            default:
                log.warn("Unknown connector: " + connectorName);
        }

        clickValidateAndCreate();
        configureCollectorsAndDistributors();
        clickSaveAndStartStreaming();

        Actions actions = new Actions(driver);
        switch (connectorName) {
            case "Datadog":
                clickGearIcon(connectorName);
                commonWaits.waitForElementVisible(datadogMetricsCard).click();
                commonWaits.waitForElementVisible(selectMetricsDropdown).click();
                actions.sendKeys(Keys.ENTER).perform();
                fillInput(selectorsInput, connectorData.get("deviceName"));
                saveConnectorConfiguration();
                break;
            case "Stackify Metrics":
                clickGearIcon(connectorName);
                commonWaits.waitForElementVisible(stackifyMetricsCard).click();
                commonWaits.waitForElementVisible(selectMetricsDropdown).click();
                actions.sendKeys(Keys.ENTER).perform();
                saveConnectorConfiguration();
                break;
            case "AppDynamics":
                clickGearIcon(connectorName);
                commonWaits.waitForElementVisible(appDynamicsEventsCard).click();
                commonWaits.waitForElementVisible(collectorConfigForEventsCard).click();
                commonWaits.waitForElementVisible(AppSelectorForAppDynamicsEventsCard).click();
                actions.sendKeys(Keys.ENTER).perform();
                commonWaits.waitForElementVisible(appDynamicsEventsCard).click();

                commonWaits.waitForSometime(commonWaits.minDelay);
                commonWaits.waitForElementVisible(appDynamicsHealthCard).click();
                commonWaits.waitForElementVisible(collectorConfigForHealthCard).click();
                commonWaits.waitForElementVisible(AppSelectorForAppDynamicsHealthCard).click();
                actions.sendKeys(Keys.ENTER).perform();
                commonWaits.waitForElementVisible(appDynamicsHealthCard).click();

                commonWaits.waitForSometime(commonWaits.minDelay);
                commonWaits.waitForElementVisible(appDynamicsMetricsCard).click();
                commonWaits.waitForElementVisible(collectorConfigForMetricCard).click();
                commonWaits.waitForElementVisible(AppSelectorForAppDynamicsMetricsCard).click();
                commonWaits.waitForSometime(commonWaits.minDelay);
                actions.sendKeys(connectorData.get("ciName")+Keys.ENTER).perform();
                commonWaits.waitForElementVisible(appDynamicsMetricsCard).click();

                commonWaits.waitForSometime(commonWaits.minDelay);
                commonWaits.waitForElementVisible(appDynamicsTopologyCard).click();
                commonWaits.waitForElementVisible(collectorConfigForTopolocyCard).click();
                commonWaits.waitForElementVisible(AppSelectorForAppDynamicsTopologyCard).click();
                actions.sendKeys(Keys.ENTER).perform();
                commonWaits.waitForElementVisible(appDynamicsTopologyCard).click();
                saveConnectorConfiguration();
                break;

            default:
                log.info("No additional configuration needed for connector: " + connectorName);
        }

    }

    public void clickGearIcon(String connectorName) {
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectorGearIcon, connectorName))).click();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }

    public void saveConnectorConfiguration() {
        //commonWaits.clickWithoutInterception(saveAndCloseButton);
        commonWaits.waitForElementClickable(saveAndCloseButton).click();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }

    public void fillInput(WebElement element, String value) {
        if (value != null) {
            commonWaits.waitForElementVisible(element);
            commonWaits.clearTextFromElement(element);
            element.sendKeys(value);
        } else {
            log.warn("Value for input field is null");
        }
    }

    public void toggleCheckbox(String xpath) {
        WebElement checkbox = commonWaits.waitForByElementToBeVisible(By.xpath(xpath + "/span"));
        if (!checkbox.getAttribute("class").contains("Mui-checked")) {
            commonWaits.waitForByElementToBeVisible(By.xpath(xpath)).click();
        }
    }

    public void clickValidateAndCreate() {
        // Implementation to click Validate and Create button
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForSometime(3000);
        commonWaits.waitForElementVisible(validateAndCreateButton).click();
        commonWaits.waitForElementInvisible_withFluentWait(driver, createCollectorsButton, 10);
    }

    public void configureCollectorsAndDistributors() {
        // Implementation to configure collectors with default values
        commonWaits.waitForByElementToBeVisible(By.xpath(createCollectorsButton)).click();
        commonWaits.waitForElementInvisible_withFluentWait(driver, createDistributorsButton, 10);
        commonWaits.waitForByElementToBeVisible(By.xpath(createDistributorsButton)).click();
    }

    public void clickSaveAndStartStreaming() {
        // Implementation to click Save and Start Streaming button
        commonWaits.waitForElementClickable(saveAndStartStreamButton).click();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }

    public void clickOnAddConnector() {
        // Implementation to click on the + button next to Sources
        commonWaits.waitForElementVisible(addSourceButton).click();
    }

    public void navigateToStreamsPage(){
        //commonWaits.clickWithoutInterception(streamsPageLink);
        commonWaits.waitForElementVisible(streamsPageLink).click();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }

    public void navigateToConnectorsPage(){
        commonWaits.waitForElementVisible(connectorsPageLink).click();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }


    public void searchStream(String streamName){
        commonWaits.waitForElementVisible(streamsSearchBox).sendKeys(streamName);
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }

    public void startStream(){
        for (WebElement playButton : playButtons){
            if (playButton.isDisplayed()){
                commonWaits.waitForElementClickable(playButton).click();
            }
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
        }
    }

    public void removeConnector(String connectorName){
        commonWaits.waitForElementVisible(editButton).click();
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(removeConnectorButton, connectorName))).click();
        commonWaits.waitForElementVisible(confirmRemoveButton).click();
        commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForAllSpinnersToHide("ITOM");
    }

    public void selectConnectorType(String connectorName) {
        String connectorXPath = "//li[contains(@class, 'MuiImageListItem-root') and contains(. ,'%s')]";
        if (connectorName.contains(" ")){
            connectorXPath = "//li[contains(@class, 'MuiImageListItem-root') and contains(. ,'%s') and contains(. ,'%s')]";
            String[] parts = connectorName.split(" ", 2);
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectorXPath, parts[0], parts[1]))).click();
        } else {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(connectorXPath, connectorName))).click();
        }
    }


}
