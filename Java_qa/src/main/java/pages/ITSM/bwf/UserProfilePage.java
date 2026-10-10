package pages.ITSM.bwf;

import framework.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class UserProfilePage extends BasePage {

    public static final SolqaLogger log = new SolqaLogger(UserProfilePage.class.getName());
    CommonWaits commonWaits;

    public UserProfilePage() {
        PageFactory.initElements(getDriver(), this);
        commonWaits=new CommonWaits();
    }

    @FindBy(xpath = "//adapt-profile//button[contains(@class, 'a-profile__menu')]")
    WebElement userProfileMenuButton;
    @FindBy(xpath = "//a[contains(normalize-space(.), 'My Profile')]")
    WebElement myProfileMenuItem;
    @FindBy(xpath = "(//*[contains(@class, 'person-name')])[1]")
    WebElement userNameElement;
    @FindBy(xpath = "//button[contains(@class, 'd-icon-angle_left') and @aria-label='Activity']")
    WebElement activityExpandButton;

    public UserProfilePage openUserProfilePage() {
        commonWaits.waitForElementVisible(userProfileMenuButton).click();
        commonWaits.waitForElementVisible(myProfileMenuItem).click();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }

    public String getPersonNameFromProfilePage() {
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    	commonWaits.waitForPageFullyLoaded(30, 3);
        String personName = userNameElement.getText().trim();
        log.info("User name from Profile Page: " + personName);
        return personName;
    }

    public UserProfilePage expandActivityOnProfilePage() {
    	commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    	commonWaits.waitForPageFullyLoaded(30, 3);
    	
        if (commonWaits.isElementPresent(activityExpandButton)) {
            commonWaits.waitForElementVisible(activityExpandButton).click();
        }
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        return this;
    }
}
