package pages.ITSM.EMAIL;

import org.openqa.selenium.WebDriver;

public class EmailFeatureMethods {


    OutlookMethods outlookMethods = new OutlookMethods();

    public EmailFeatureMethods composeNewEmail(String toFieldValue, String subjectFieldValue) {
        outlookMethods.composeNewEmail(toFieldValue, subjectFieldValue);
        return this;
    }

    public EmailFeatureMethods verifyRequiredMailResponse(String mailResponse) {
        outlookMethods.verifyRequiredMailResponse(mailResponse);
        return this;
    }

    public String getINCIDFromMailMessage(String mailResponse) {
        return outlookMethods.getINCIDFromMailMessage(mailResponse);
    }

    public EmailFeatureMethods switchToOutlookPage(WebDriver webDriver) {
        outlookMethods.switchToOutlookPage(webDriver);
        return this;
    }

    public EmailFeatureMethods clickOnResolvedButton(String mailResponse) {
        outlookMethods.clickOnResolvedButton(mailResponse);
        return this;
    }

    public EmailFeatureMethods clickOnReopenButton(String mailResponse) {
        outlookMethods.clickOnReopenButton(mailResponse);
        return this;
    }

}
