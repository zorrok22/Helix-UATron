package pages.ITSM.ITSM;

import framework.BasePage;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.util.Map;

public class DMTJob extends BasePage {
    public static final SolqaLogger log = new SolqaLogger(DMTJob.class.getName());
    CommonWaits commonWaits;

    public DMTJob() {
        commonWaits = new CommonWaits();
        PageFactory.initElements(getDriver(), this);
    }

    // String locators
    String TYPE_HEAD_INPUT = "(//label[contains(. ,'%s')]/following-sibling::textarea)[1]";
    String createStepsDropdownOption = "//table[@class='MenuTable']//td[text()='%s']";
    String jobTemplateName = "//*[text()='%s']/ancestor::tr";
    String FileUploadFrame = "//iframe[contains(@src, 'AttachmentPopup')]";
    String linkByText = "//a[text()='%s']";
    String DropDownField = "//label[contains(. ,'%s')]/parent::div//a";
    String DropDownFieldOption = "//td[@class='MenuEntryName' and text()='%s']";

    // WebElement locators
    @FindBy(xpath = "//*[@arid='304282010']//a")
    WebElement createStepsDropdown;
    @FindBy(xpath = "//tr[@id='Load_304276130']")
    WebElement loadStep;
    @FindBy(xpath = "(//label[contains(text(), 'Job ID')]/following-sibling::textarea)[1]")
    WebElement jobID;
    @FindBy(xpath = "//label[contains(text(), 'Job Result')]/parent::*//input")
    WebElement jobResult;
    @FindBy(xpath = "//input[@type='file']")
    WebElement fileUploadInput;
    @FindBy(xpath = "//a[@id='WIN_5_1003' and contains(. ,'Save')]")
    WebElement jobStepSaveButton;
    @FindBy(xpath = "(//label[contains(. ,'Status')]/parent::div[@arid='7']//input)[1]")
    WebElement statusField;
    @FindBy(xpath = "//*[@artxt='Refresh']")
    WebElement refreshButton;

    public String createAndExecuteDMTJob(Map<Object, String> jobData) {
        String loadStepName = jobData.get("LoadStepName") != null ? jobData.get("LoadStepName") : "Load CI";
        String fileCopyStepName = jobData.get("FileCopyStepName") != null ? jobData.get("FileCopyStepName") : "Data Load File Copy";

        commonWaits.clickByText(SolQAConstants.CREATE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        setTextAreaInput(SolQAConstants.JOB_NAME, jobData.get("JobName") + "_" + RandomStringUtils.randomAlphanumeric(5));
        setTextAreaInput(SolQAConstants.Job_Description_Label, jobData.get("JobDescription"));
        setDropDownField(SolQAConstants.Company_Label + "*", jobData.get("Company"));
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        commonWaits.waitForElementVisible(createStepsDropdown).click();
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), String.format(createStepsDropdownOption, jobData.get("TemplateType")), 10),
                "Create Steps dropdown option not found: " + jobData.get("TemplateType"));
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(createStepsDropdownOption, jobData.get("TemplateType")))).click();

        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(jobTemplateName, jobData.get("TemplateName")))).click();
        commonWaits.clickByText(SolQAConstants.SELECT);
        commonWaits.waitForExecutionComplete();
        String job_ID = commonWaits.waitForElementVisible(jobID).getAttribute("value").trim();
        commonWaits.clickWithoutInterception(loadStep);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(jobTemplateName, loadStepName))).click();
        commonWaits.clickByText(SolQAConstants.VIEW);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(jobTemplateName, fileCopyStepName))).click();
        commonWaits.clickByText(SolQAConstants.ADD);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        String PATH_TO_File = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appITSM + File.separator;
        String fileName = PATH_TO_File + jobData.get("AttachmentName");
        log.info("----File path:  " + fileName);
        commonWaits.switchToFrame(By.xpath(FileUploadFrame));
        fileUploadInput.sendKeys(fileName);
        commonWaits.clickByText(SolQAConstants.OK);
        commonWaits.waitForElementInvisible(fileUploadInput);
        commonWaits.switchtoDefaultFrame();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.scrollToBottomOfPage();
        commonWaits.clickWithoutInterception(jobStepSaveButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(linkByText, job_ID))).click();
        setDropDownField(SolQAConstants.STATUS + "*", SolQAConstants.BUILT);
        commonWaits.clickByText(SolQAConstants.SAVE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.clickByText(SolQAConstants.RUN);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForSometime(15000); // Wait for job to complete
        commonWaits.clickByText(SolQAConstants.CONTINUE);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        commonWaits.waitForElementVisible(refreshButton);
        commonWaits.clickWithoutInterception(refreshButton);
        int maxAttempts = 10;
        int attempt = 0;
        while (attempt < maxAttempts) {
            WebElement result = commonWaits.waitForElementVisible(jobResult);
            String title = result.getAttribute("title");
            if (title != null && !title.trim().isEmpty() && !title.trim().equalsIgnoreCase(SolQAConstants.SUCCESSFUL)) {
                // value found → exit loop
                break;
            }
            // value not present → refresh
            commonWaits.clickWithoutInterception(refreshButton);
            commonWaits.waitForSometime(10000); // small wait for data reload
            attempt++;
        }
        commonWaits.waitForElementVisible(jobResult);
        Actions actions = new Actions(getDriver());
        actions.moveToElement(jobResult).perform();
        Assert.assertFalse(jobResult.getAttribute("title").isEmpty(), "Job result is empty after maximum refresh attempts");
        return jobResult.getAttribute("title").trim();
    }

    public void setTextAreaInput(String labelName, String value) {
        try {
            WebElement webElement = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(TYPE_HEAD_INPUT, labelName)));
            webElement.click();
            webElement.sendKeys(value);
            log.info("Set text area input " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set text area input for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

    public void setDropDownField(String labelName, String value) {
        try {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownField, labelName))).click();
            commonWaits.waitForSometime(1000); // Wait for dropdown options to load
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(DropDownFieldOption, value))).click();
            log.info("Set dropdown field " + labelName + " to " + value);
        } catch (Exception e) {
            log.error("Failed to set dropdown field for " + labelName + ": " + e.getMessage());
            throw e;
        }
    }

}