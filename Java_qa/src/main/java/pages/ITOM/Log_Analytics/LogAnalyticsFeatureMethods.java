package pages.ITOM.Log_Analytics;

import framework.BasePage;

public class LogAnalyticsFeatureMethods extends BasePage {

    LogAnalytics logAnalytics = new LogAnalytics();
    public void verifyLogin() {
        logAnalytics.verifyLogAnalyticsPageLaunchedSuccessfully();
    }

    public void runPythonScript(String pythonCmd) {
        logAnalytics.runPythonScript(pythonCmd);
    }

    public void verifyLogHostName(String hostName, String filterName) {
        logAnalytics.verifyLogHostName(hostName, filterName);
    }
}
