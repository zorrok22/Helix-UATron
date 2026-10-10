package pages.ITSM.SmartIT;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class SmartITCalender extends BasePage {

    public static final SolqaLogger log = new SolqaLogger(SmartITCalender.class.getName());
    
    CommonWaits commonWaits;
    String smartIT = "SmartIT";

    public SmartITCalender() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }
    //WebElements locators
    @FindBy(xpath = "//span[contains(.,'Today')]/../../button")
    WebElement todayButton;
    @FindBy(xpath = "//span[contains(.,'Search')]/../input")
    WebElement searchInput;
    @FindBy(xpath = "//button[contains(@class,'search-button')]")
    WebElement searchButton;
    @FindBy(xpath = "//div[@dir='ltr']//ul/li[1]")
    WebElement firstChangeListContent;
    @FindBy(xpath = "//span[contains(.,'Service:')]/a")
    WebElement serviceLink;
    @FindBy(xpath = "//span[contains(.,'Current service health status')]/..//div/span")
    WebElement currentServiceHealthStatus;
    @FindBy(xpath = "//span[contains(.,'Current open situations')]/../span[contains(@class,'bold')]")
    WebElement currentOpenSituations;
    @FindBy(xpath = "//span[contains(.,'Past situations')]/../button/span")
    WebElement pastSituations;
    @FindBy(xpath = "//span[contains(.,'Completed changes')]/../span[contains(@class,'bold')]")
    WebElement completedChanges;
    @FindBy(xpath = "//span[contains(.,'Work orders')]/../span[contains(@class,'bold')]")
    WebElement workOrders;
    @FindBy(xpath = "//span[contains(.,'Incidents')]/../span[contains(@class,'bold')]")
    WebElement incidents;
    @FindBy(xpath = "//span[contains(.,'View full ticket')]")
    WebElement viewFullTicketLink;
    @FindBy(xpath = "//div[@class='app-title' and contains(.,'BMC Helix ITSM')]")
    WebElement helixITSMLink;
    @FindBy(xpath = "//*[@role='menuitem']/..")
    WebElement popupCRMwindows;


    public boolean waitForHelixITSMLinkToBeVisible() {
        try {
            log.info("Waiting for helix ITSM link to be visible in SmartIT Calender");
            CommonWaits.fluentWait(helixITSMLink);
            commonWaits.waitForElementVisible(helixITSMLink);
            return true;
        } catch (Exception e) {
            log.error("Failed to wait for helix ITSM link to be visible in SmartIT Calender: " + e.getMessage());
            return false;
        }
    }

    public boolean searchChange(String changeName) {
        try {
            CommonWaits.fluentWait(todayButton);
            commonWaits.waitForElementVisible(todayButton);
            todayButton.click();
            searchInput.sendKeys(changeName);
            commonWaits.waitForElementVisible(searchButton);
            searchButton.click();
            
            
        } catch (Exception e) {
            log.error("Failed to search change: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean popupCRMwindows() {
        try {
            CommonWaits.fluentWait(firstChangeListContent);
            commonWaits.waitForElementVisible(firstChangeListContent);
            firstChangeListContent.click();
            commonWaits.waitForElementVisible(popupCRMwindows);
            popupCRMwindows.click();
            CommonWaits.fluentWait(popupCRMwindows);
            commonWaits.waitForElementVisible(viewFullTicketLink);
            return true;
        } catch (Exception e) {
            log.error("Failed to popup CRM windows: " + e.getMessage());
            return false;
        }
    }

    public boolean loadSmartITCalender(String calenderURL) {
        try {
            getDriver().get(calenderURL);
            CommonWaits.fluentWait(todayButton);
            commonWaits.waitForElementVisible(todayButton, 60);
        } catch (Exception e) {
            log.error("Failed to load smart IT calender: " + e.getMessage());
            return false;
        }
        return true;
    }

    public String getServiceName() {
        try {
            commonWaits.waitForElementVisible(serviceLink);
            return serviceLink.getText();
        } catch (Exception e) {
            log.error("Failed to get service name: " + e.getMessage());
            return null;
        }
    }

    public String getCurrentServiceHealthStatus() {
        try {
            commonWaits.waitForElementVisible(currentServiceHealthStatus);
            return currentServiceHealthStatus.getText();
        } catch (Exception e) {
            log.error("Failed to get current service health status: " + e.getMessage());
            return null;
        }
    }

    public String getCurrentOpenSituations() {
        try {
            commonWaits.waitForElementVisible(currentOpenSituations);
            return currentOpenSituations.getText();
        } catch (Exception e) {
            log.error("Failed to get current open situations: " + e.getMessage());
            return null;
        }
    }

    public String getPastSituations() {
        try {
            commonWaits.waitForElementVisible(pastSituations);
            return pastSituations.getText();
        } catch (Exception e) {
            log.error("Failed to get past situations: " + e.getMessage());
            return null;
        }
    }

    public String getCompletedChanges() {
        try {
            commonWaits.waitForElementVisible(completedChanges);
            return completedChanges.getText();
        } catch (Exception e) {
            log.error("Failed to get completed changes: " + e.getMessage());
            return null;
        }
    }

    public String getWorkOrders() {
        try {
            commonWaits.waitForElementVisible(workOrders);
            return workOrders.getText();
        } catch (Exception e) {
            log.error("Failed to get work orders: " + e.getMessage());
            return null;
        }
    }

    public String getIncidents() {
        try {
            commonWaits.waitForElementVisible(incidents);
            return incidents.getText();
        } catch (Exception e) {
            log.error("Failed to get incidents: " + e.getMessage());
            return null;
        }
    }
}