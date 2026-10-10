package pages.ITSM.dwp;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class DWPAppointmentPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;

    String ANY_BUTTON_CONTAINS_TEXT = "//*[self::button or self::a][contains(normalize-space(.), '%s')]";
    String LOCATION_INPUT_CANDIDATES = "//*[contains(@data-ta,'location') or contains(@data-ta,'Location')]//input";
    String PICKER_ITEM_BUTTONS = "button.picker-item, button.picker-item.btn_clear-style, button.picker-item.btn_clear-style.btn-lg";
    String DAY_BUTTON_BY_NUMBER = "(//button[normalize-space()='%s' and not(@disabled) and not(contains(@class,'disabled'))])[1]";
    String TIME_SLOT_BUTTONS_XPATH = "//*[contains(@data-ta,'timeSlot') or contains(@data-ta,'timeslot') or contains(@class,'time-slot') or contains(@class,'timeslot')]//*[self::button or self::a][not(@disabled)]";
    String TEXTAREA_OR_INPUT_IN_APPOINTMENT = "//dwp-appointment | //*[contains(@class,'appointment') and (self::div or self::section)]//textarea[not(@disabled)]";

    public DWPAppointmentPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(DWPAppointmentPage.class.getName());
        commonWaits = new CommonWaits();
    }

    public DWPAppointmentPage addAppointmentTextIfPresent(String text) {
        if (text == null || text.trim().isEmpty()) {
            return this;
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        try {
            List<WebElement> fields = findElements(By.xpath(TEXTAREA_OR_INPUT_IN_APPOINTMENT));
            if (!fields.isEmpty()) {
                WebElement field = fields.get(0);
                commonWaits.waitForElementVisible(field).clear();
                field.sendKeys(text);
            }
        } catch (Exception e) {
            log.warn("Unable to add appointment text (best-effort): " + e.getMessage());
        }
        return this;
    }

    public DWPAppointmentPage chooseLocation(String locationName) {
        Assert.assertNotNull(locationName, "Appointment location is null");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        WebElement locationInput = null;
        List<WebElement> candidates = findElements(By.xpath(LOCATION_INPUT_CANDIDATES));
        if (!candidates.isEmpty()) {
            locationInput = candidates.get(0);
        }
        Assert.assertNotNull(locationInput, "Unable to find appointment location input");

        commonWaits.waitForElementVisible(locationInput).click();
        locationInput.sendKeys(locationName);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);

        // Reuse common picker-item CSS used across DWP
        List<WebElement> results = commonWaits.getElements(By.cssSelector(PICKER_ITEM_BUTTONS));
        Assert.assertFalse(results.isEmpty(), "No location results found for: " + locationName);
        results.get(0).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAppointmentPage chooseDayByDayOfMonth(String dayOfMonth) {
        Assert.assertNotNull(dayOfMonth, "Day-of-month is null");
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        commonWaits.clickWithoutInterception(By.xpath(String.format(DAY_BUTTON_BY_NUMBER, dayOfMonth)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAppointmentPage selectFirstTimeSlot() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        List<WebElement> slots = findElements(By.xpath(TIME_SLOT_BUTTONS_XPATH));
        Assert.assertFalse(slots.isEmpty(), "No appointment time slots found");
        commonWaits.clickWithoutInterception(slots.get(0));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }

    public DWPAppointmentPage clickSubmitAppointment() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        if (commonWaits.isElementPresent(By.xpath(String.format(ANY_BUTTON_CONTAINS_TEXT, "Submit appointment")))) {
            commonWaits.clickWithoutInterception(By.xpath(String.format(ANY_BUTTON_CONTAINS_TEXT, "Submit appointment")));
        } else if (commonWaits.isElementPresent(By.xpath(String.format(ANY_BUTTON_CONTAINS_TEXT, "Submit")))) {
            commonWaits.clickWithoutInterception(By.xpath(String.format(ANY_BUTTON_CONTAINS_TEXT, "Submit")));
        } else {
            Assert.fail("Submit appointment button not found");
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appDWP);
        return this;
    }
}