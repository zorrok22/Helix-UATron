package pages.ITSM.SmartIT;

import java.io.File;
import java.util.Map;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonMethods;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class Broadcast extends SmartITCommonOperations {

    static SolqaLogger log = new SolqaLogger(Broadcast.class.getName());
    CommonWaits commonWaits;
    CommonMethods commonMethods;

    public Broadcast() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
        commonMethods = new CommonMethods();
    }

    @FindBy(xpath = "//input[@type='file']")
    WebElement uploadFileInput;

    public void createBroadcast(Map<Object, String> data) {
        log.info("Creating Broadcast with provided data");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        switchToPWAFrame();

        if (data.get("Audience") != null) {
            setTypeaheadField("Audience", data.get("Audience"));
        }
        if (data.get("Headline") != null) {
            setTextField("Headline", data.get("Headline"));
        }
        if (data.get("Message") != null) {
            setTextAreaField("Message", data.get("Message"));
        }
        if (data.get("Type") != null) {
            setDropdownField("Type", data.get("Type"));
        }
        if (data.get("Priority") != null) {
            setDropdownField("Priority", data.get("Priority"));
        }
        if (data.get("Visibility") != null) {
            setDropdownField("Visibility", data.get("Visibility"));
        }
        if (data.get("attachment") != null) {
            File file = new File(data.get("attachment"));
            commonMethods.attachFile(file, uploadFileInput);
        }
        if (data.get("BroadcastStartDate") != null) {
            setDateField("Broadcast start", getFutureDateTime(Integer.parseInt(data.get("BroadcastStartDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (data.get("BroadcastEndDate") != null) {
            setDateField("Broadcast end", getFutureDateTime(Integer.parseInt(data.get("BroadcastEndDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        log.info("Saving Broadcast");
        clickSave();
        handlePageLoadWaitAlertIfPresent();
    }
}