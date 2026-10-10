package pages.ITOM.Developer_Tool;

import framework.BasePage;

public class DeveloperToolFeatureMethods extends BasePage {

    DeveloperToolPage developerToolPage = new DeveloperToolPage();

    public void verifyLogin() {
        developerToolPage.verifyDeveloperToolPageLaunchedSuccessfully();
    }
}
