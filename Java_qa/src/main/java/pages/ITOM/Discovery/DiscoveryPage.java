package pages.ITOM.Discovery;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscoveryPage extends BasePage {

    WebDriver driver;
    static SolqaLogger log = new SolqaLogger(DiscoveryPage.class.getName());
    CommonWaits commonWaits=new CommonWaits();
    String appName = null;

    protected static Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") +
			File.separator + "properties.config");


    @FindBy(xpath = "//input[@id='_tw_searchvalue' and @role='search']")
    WebElement txtBox_searchGrid;


    @FindBy(xpath = "//a[contains(. ,'Admin')]")
    public WebElement adminSetting;

    @FindBy(xpath = "//a[@href='/ui/SetupHome']")
    public WebElement discoverySettings;

    @FindBy(xpath = "//a[@href='/ui/SetupSyncTargetOverview']")
    public WebElement CmdbSync;

    @FindBy(xpath = "//a[contains(@href,'SetupSyncTargetStatus')]")
    public WebElement existingCMDBConnections;

    @FindBy(xpath = "//a[@href='SetupSyncTargetAdd']")
    public WebElement addCMDBConnection;

    @FindBy(xpath = "//input[@id='name']")
    public WebElement name;

    @FindBy(xpath = "//input[@id='api_choice_servicenow']")
    public WebElement radioServiceNow;

    @FindBy(xpath = "//input[@id='endpoint']")
    public WebElement endpoint;

    @FindBy(xpath = "//input[@id='username']")
    public WebElement username;

    @FindBy(xpath = "//input[@id='password']")
    public WebElement password;

    @FindBy(xpath = "//input[@id='client_id']")
    public WebElement client_id;

    @FindBy(xpath = "//input[@id='client_secret']")
    public WebElement client_secret;

    @FindBy(xpath = "//input[@id='source_name']")
    public WebElement source_name;

    @FindBy(xpath = "//select[@id='datamodel']")
    public WebElement datamodel;

    @FindBy(xpath = "//option[contains(text(),'ServiceNow')]")
    public WebElement snow_value;

    @FindBy(xpath = "//a[contains(@href,'SetupSyncTargetResync')]")
    public WebElement syncTab;

    @FindBy(xpath = "//input[@id='complete_resync']")
    public WebElement completeResync;

    @FindBy(xpath = "//input[@id='incremental_resync']")
    public WebElement incrementalSync;

    @FindBy(xpath = "//input[@id='start_button']")
    public WebElement startSync;

    @FindBy(xpath = "//div[@id='resync_status']/h3")
    public WebElement syncStatus;

    @FindBy(xpath = "//input[@value='Add']")
    public WebElement addCMDB;

    @FindBy(xpath = "//button[contains(text(),'Advanced')]")
    public WebElement discovery_Advancedbutton;

    @FindBy(xpath = "//a[contains(@id,'proceed-link')]")
    public WebElement discovery_proceedLink;

    @FindBy(xpath = "//button[@title='Model things so that they are discovered in future']")
    public WebElement modelMenu;

    @FindBy(xpath = "//a[@title='Model Services and Applications']")
    public WebElement servicesApplicationSubMenu;

    @FindBy(xpath = "//button[@title='Create a new Model or Import definitions']")
    public WebElement createImportButton;

    @FindBy(xpath = "//a[@title='Create from Blueprint']")
    public WebElement createWithBlueprints;

    @FindBy(xpath = "//input[@id='manageBlueprintDefinitions']")
    public WebElement manageBlueprintDefinition;

    @FindBy(xpath = "//lib-modelling-view-mode//div[contains(text(),'Published')]")
    public WebElement publishedModel;

    @FindBy(xpath ="//button[@title='Search Options']")
    WebElement loc_disc_search_options_btn;

    @FindBy(xpath = "//select[@name='_tw_searchtype']")
    WebElement loc_dic_search_type_list;

    @FindBy(xpath = "//option[@value='exact']")
    WebElement loc_dic_search_type_exact_opt;
    @FindBy(xpath = "//option[@value='word']")
    WebElement loc_dic_search_type_word_opt;

    @FindBy(xpath = "//input[@id='_tw_search']")
    WebElement loc_disc_search_btn;

    @FindBy(xpath = "(//tr[@title='Click to view the Clusters Found'])[1]")
    WebElement searchedClusters;

    @FindBy(xpath = "(//tr[@title='Click to view the Business Services Found'])[1]")
    WebElement searchedService;


    String fileType = "input[type='file']";
    public String discoveryPage="//a[@role='menuitem']/img[contains(@src,'company')]/following-sibling::span";
    String adminChannelSetting = "//h3[contains(. ,'%s')]/parent::div//li[contains(. ,'%s')]";
    String verifySetting = "//*[@id='breadcrumb']//*[contains(. ,'%s')]";
    String CIandRelationshipData = "(//td[text()='%s']//following::td)[1]";
    String firstResult = "//tbody[@id='resultTBodyId']//tr/td[2]";
    String showCurrentViz = "//input[@title='Show current visualization']";
    String toggle = "//button[@aria-label='Toggle size' and contains(@class, 'd-icon-plus')]";
    String app_modelling="//button[contains(@title,'Start Service / Application Modeling')]";
    String bus_serv = "//div[text()[contains(., ' Business Service')]]";
    String serv_name = "//input[@data-testid='inline-editor-0_Textfield']";
    String save_publish = "//input[@value='Save & Publish']";
    String confirm = "//button[text()[contains(., 'Yes')]]";
    String inline_viz = "//span[contains(@class, 'InlineVisualizationTitle')]";
    String clusterName = "//tbody[@id='resultTBodyId']//tr/td[2]";
    String showVisualization = "//input[@title='Show current visualization']";
    String plusIcon = "//button[@aria-label='Toggle size' and contains(@class, 'd-icon-plus')]";
    String loc_disc_search_no_results="//div[@id='searchContent']/p[@class='noResultsFound']";
    String searchedServices="//tr[@title='Click to view the Business Services Found']";
    String node= "//button[@id='nodeActionsDropdownLink']";
    String deleteModel = "//li/a[@id='delete_model']";
    String confirmYes = "//button[@id='confirm-collection-yes-button']";
    String cmdbTile = "//a[contains(@href,'SetupSyncTargetStatus')]/parent::h4/a[normalize-space()='%s']";
    String cmdbTileAction = "/parent::h4/following-sibling::div";
    String cmdbTileEditAction = "/..//a[contains(text(),'Edit')]";
    String resyncStatus = "//div[@id='resync_status']/h3";

    public DiscoveryPage() {
        this.driver = getDriver() ;
        PageFactory.initElements(driver, this);
        commonWaits = new CommonWaits();
    }

    public void verifyDiscoveryPageLaunchedSuccessfully() {
        if (commonWaits.isElementPresent(discovery_Advancedbutton))
            discovery_Advancedbutton.click();
        if (commonWaits.isElementPresent(discovery_proceedLink))
            discovery_proceedLink.click();
        appName = driver.findElement(By.xpath(discoveryPage)).getText().trim();
        log.info("----- Opened application name - " + appName);
        Assert.assertEquals(appName, "Discovery", "Discovery application is not launched successfully");
    }

    public void Search_cluster_In_Discovery(String clusterName){
        commonWaits.clickWithoutInterception(txtBox_searchGrid);
        txtBox_searchGrid.clear();
        txtBox_searchGrid.sendKeys(clusterName);
        commonWaits.waitForElementVisible(loc_disc_search_options_btn).click();
        commonWaits.waitForElementVisible(loc_dic_search_type_list, 3);
        commonWaits.waitForElementVisible(loc_dic_search_type_list).click();
        commonWaits.waitForElementVisible(loc_dic_search_type_word_opt, 3);
        commonWaits.waitForElementVisible(loc_dic_search_type_word_opt).click();
        commonWaits.waitForElementVisible(loc_disc_search_btn).click();
        commonWaits.waitForPageFullyLoaded(10,2);
        commonWaits.waitForAngularRequestsToFinish();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),searchedClusters,10), "Cluster with name "+clusterName+" is not found in Discovery.");
    }

    public void searchServiceInDiscoveryUsingUI(String clusterName){
        commonWaits.clickWithoutInterception(txtBox_searchGrid);
        txtBox_searchGrid.clear();
        txtBox_searchGrid.sendKeys(clusterName);
        commonWaits.waitForElementVisible(loc_disc_search_options_btn).click();
        commonWaits.waitForElementVisible(loc_dic_search_type_list, 3);
        commonWaits.waitForElementVisible(loc_dic_search_type_list).click();
        commonWaits.waitForElementVisible(loc_dic_search_type_word_opt, 3);
        commonWaits.waitForElementVisible(loc_dic_search_type_word_opt).click();
        commonWaits.waitForElementVisible(loc_disc_search_btn).click();
        commonWaits.waitForPageFullyLoaded(10,2);
        commonWaits.waitForAngularRequestsToFinish();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(),searchedService,10), "Cluster with name "+clusterName+" is not found in Discovery.");
    }

    public void Create_Model_And_Publish(String serviceName){
        commonWaits.clickWithoutInterception(searchedClusters);
        if(commonWaits.isElementPresent(clusterName)){
            findElement(By.xpath(clusterName)).click();
        }
        if(commonWaits.isElementPresent(showVisualization)){
            findElement(By.xpath(showVisualization)).click();
        }
        if(commonWaits.isElementPresent(plusIcon)){
            findElement(By.xpath(plusIcon)).click();
        }
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(app_modelling))).click();
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(bus_serv))).click();
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(serv_name)));
        driver.findElement(By.xpath(serv_name)).sendKeys(serviceName);
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(save_publish))).click();
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(confirm))).click();
        commonWaits.waitForElementVisible(publishedModel);
    }

    public void delete_service_model(String serviceName){
        searchServiceInDiscoveryUsingUI(serviceName);
        commonWaits.waitForSometime(4000);
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(searchedServices))).click();
        commonWaits.waitForSometime(4000);
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(node))).click();
        commonWaits.waitForSometime(4000);
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(deleteModel))).click();
        commonWaits.waitForElementClickable(driver.findElement(By.xpath(confirmYes))).click();
    }

    public void clickOnModel() {
        commonWaits.waitForElementClickable(modelMenu);
        clickByAction(modelMenu);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickOnServicesAndApplication() {
        commonWaits.waitForElementClickable(servicesApplicationSubMenu);
        clickByAction(servicesApplicationSubMenu);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void goToDiscoverySettings(){
        commonWaits.waitForElementVisible(discoverySettings).click();
        commonWaits.waitForElementVisible(CmdbSync).click();
        commonWaits.waitForElementVisible(addCMDBConnection);
        commonWaits.waitForPageReadyState(getDriver());
    }

    public void createServiceNowCMDBConnection(String connectionName, String endPoint, String userName, String passwordvalue, String clientID, String clientSecret){
        if (!isElementPresent(By.xpath(String.format(cmdbTile,connectionName)))){
            commonWaits.waitForElementVisible(addCMDBConnection).click();
            commonWaits.waitForElementVisible(name).sendKeys(connectionName);
            commonWaits.waitForElementVisible(radioServiceNow).click();
            commonWaits.waitForElementVisible(endpoint).sendKeys(endPoint);
            commonWaits.waitForElementVisible(username).sendKeys(userName);
            commonWaits.waitForElementVisible(password).sendKeys(passwordvalue);
            commonWaits.waitForElementVisible(client_id).sendKeys(clientID);
            commonWaits.waitForElementVisible(client_secret).sendKeys(clientSecret);
            commonWaits.waitForElementVisible(source_name).clear();
            commonWaits.waitForElementVisible(source_name).sendKeys(SolQAConstants.SNOW);
            commonWaits.waitForElementVisible(datamodel).click();
            commonWaits.clickWithoutInterception(snow_value);
            commonWaits.clickWithoutInterception(addCMDB);
            commonWaits.waitForPageReadyState(getDriver());
        }{
            log.info("Service Now CMDB conenction is already existing. No need to add new.");
        }
    }

    public void syncServiceNowCMDB(String connectionName){
        commonWaits.waitForElementVisible(getDriver().findElement(By.xpath(String.format(cmdbTile,connectionName))));
        if (isElementPresent(By.xpath(String.format(cmdbTile,connectionName)))){
            String xpath1 = String.format(cmdbTile,connectionName);
            String xpath2 = String.format(cmdbTileAction,connectionName);
            commonWaits.waitForElementVisible(getDriver().findElement(By.xpath(xpath1+xpath2))).click();
            commonWaits.waitForElementVisible(getDriver().findElement(By.xpath(xpath1+xpath2+cmdbTileEditAction))).click();
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForElementVisible(syncTab).click();
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForElementVisible(incrementalSync).click();
            commonWaits.waitForElementVisible(startSync).click();
            commonWaits.waitForPageReadyState(getDriver());
            commonWaits.waitForElementVisible(syncStatus);
            log.info("Wait until CMDB Sync is completed");
            boolean flag=true;
            while (flag){
                commonWaits.waitForStableElement(By.xpath(resyncStatus));
                if (!commonWaits.getTextSafely(By.xpath(resyncStatus)).trim().contains("Running")){
                    log.info("Wait for sometime. ");
                    commonWaits.waitForSometime(commonWaits.moreDelay);
                    flag=false;
                }
            }
            Assert.assertTrue(commonWaits.waitForElementVisible(findElement(By.xpath(resyncStatus))).getText().trim().contains("Completed"),"CMDB Sync has failed. Status seen is: "+ syncStatus.getText());

        }else{
            log.info("No Connector with this name is found: "+connectionName);
        }
    }

    public void clickOnManageBlueprintDefinition() {
        commonWaits.waitForElementClickable(manageBlueprintDefinition);
        clickByAction(manageBlueprintDefinition);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void importBlueprint(String filePath) {
        WebElement file_input = findElement(By.cssSelector(fileType));
        file_input.sendKeys(filePath);
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(handles.get(1));
    }

    public void clickOnCreateAndImport() {
        commonWaits.waitForElementClickable(createImportButton);
        clickByAction(createImportButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }

    public void clickOnCreateWithBlueprintButton() {
        commonWaits.waitForElementClickable(createWithBlueprints);
        clickByAction(createWithBlueprints);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
    }
}