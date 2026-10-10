package pages.ITSM.SmartIT;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.util.Map;

public class KnownError extends SmartITCommonOperations {

    static SolqaLogger log = new SolqaLogger(KnownError.class.getName());
    CommonWaits commonWaits;

    public KnownError() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    @FindBy(xpath = "//*[@id='ar1000000979_data']")
    WebElement knownErrorIDField;

    public KnownError createKnownError(Map<Object, String> data) {
        waitForCreateAndViewPageLoaded();
        if (data == null || data.isEmpty()) {
            return this;
        }

        if (data.get("Summary") != null) {
            setTextField("Summary", data.get("Summary"));
        }
        if (data.get("Description") != null) {
            setTextAreaField("Description", data.get("Description"));
        }
        if (data.get("Impact") != null) {
            setDropdownField("Impact", data.get("Impact"));
        }
        if (data.get("Urgency") != null) {
            setDropdownField("Urgency", data.get("Urgency"));
        }
        if (data.get("TargetDate") != null) {
            setDateField("Target date", getFutureDateTime(Integer.parseInt(data.get("TargetDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        clickSave();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        handlePageLoadWaitAlertIfPresent();
        return this;
    }

    public String getKnownErrorID() {
        return commonWaits.waitForElementVisible(knownErrorIDField).getText().trim();
    }

}