package pages.ITOM.Platform;

import framework.BasePage;
import util.SolqaLogger;

public class PlatformFeatureMethods extends BasePage {

    static SolqaLogger log = new SolqaLogger(PlatformFeatureMethods.class.getName());
    PlatformLandingPage platformLandingPage = new PlatformLandingPage(getDriver());

    public void crossLaunchProduct(String productName) {
        platformLandingPage.crossLaunchProduct(productName);
        log.info("Cross launched product: " + productName);
    }
}
