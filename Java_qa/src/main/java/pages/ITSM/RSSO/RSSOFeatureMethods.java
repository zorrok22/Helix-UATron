package pages.ITSM.RSSO;

import framework.BasePage;
import util.SolQAConstants;
import util.SolqaLogger;

public class RSSOFeatureMethods extends BasePage {
    static SolqaLogger log = new SolqaLogger(RSSOFeatureMethods.class.getName());
    RSSODashboard rssoDashboard = new RSSODashboard();
    SolQAConstants solqaConstants = new SolQAConstants();

    public RSSOFeatureMethods verifyLogin() {
        rssoDashboard.verifyLogin();
        return this;
    }

    public RSSOFeatureMethods clickOnRSSOTab(String tabName) {
        rssoDashboard.clickOnRSSOTab(tabName);
        return this;
    }

    public RSSOFeatureMethods verifyTenantsIsPresent(String tenantName) {
        rssoDashboard.verifyTenantsIsPresent(tenantName);
        return this;
    }

    public RSSOFeatureMethods clickOnEnableUIIdleTimeout(String tenantName) {
        rssoDashboard.clickOnEnableUIIdleTimeout(tenantName);
        return this;
    }

    public RSSOFeatureMethods clickOnPinButton() {
        rssoDashboard.clickOnPinButton();
        return this;
    }

    public RSSOFeatureMethods setUIIdealTimeOutAndWarningTime(String UIIdleTimeoutAllowedTime, String UIIdleTimeoutWarningTime) {
        rssoDashboard.setRequiredTimeOut(SolQAConstants.UI_IDLE_TIMEOUT_ALLOWED_TIME, UIIdleTimeoutAllowedTime);
        rssoDashboard.setRequiredTimeOut(SolQAConstants.UI_IDLE_TIMEOUT_WARNING_TIME, UIIdleTimeoutWarningTime);
        rssoDashboard.selectTimeUnit(SolQAConstants.MINUTES);
        rssoDashboard.clickOnUpdateButton();
        return this;
    }

    public RSSOFeatureMethods verifySessionIdleWarningMessage() {
        rssoDashboard.verifySessionIdleWarningMessage();
        return this;
    }

    public RSSOFeatureMethods clickOnOkButton() {
        rssoDashboard.clickOnOkButton();
        return this;
    }

    public RSSOFeatureMethods verifySessionLogout() {
        rssoDashboard.verifySessionLogout();
        return this;
    }

    public RSSOFeatureMethods verifySessionLogoutIsNotPresent() {
        rssoDashboard.verifySessionLogoutIsNotPresent();
        return this;
    }

    public RSSOFeatureMethods openNewTab() {
        rssoDashboard.openNewTab();
        return this;
    }

    public RSSOFeatureMethods verifySessionIdleWarningMessageIsNotPresent() {
        rssoDashboard.verifySessionIdleWarningMessageIsNotPresent();
        return this;
    }

    public RSSOFeatureMethods clickOnCrossButton() {
        rssoDashboard.clickOnCrossButton();
        return this;
    }

    public RSSOFeatureMethods clickOnDisableUIIdleTimeout(String tenantName) {
        rssoDashboard.clickOnDisableUIIdleTimeout(tenantName);
        return this;
    }

}
