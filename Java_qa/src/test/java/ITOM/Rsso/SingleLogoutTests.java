package ITOM.Rsso;

import framework.Launcher;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import util.*;

import java.io.File;
import java.util.Map;

public class SingleLogoutTests extends Launcher {
    private static SolqaLogger log = new SolqaLogger(SingleLogoutTests.class.getName());
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    AIOpsFeatureMethods aiOpsFeatureMethods;
    Portal_FeatureMethods portalFeatureMethods;

    public void doPreconditions() {
        launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
    }

    @Test(description = "[RSSO] Immediate Single Logout for ADE applications - Interop / Converged server scenario",groups={"Regression"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12537", Products = {Product.SmartIT, Product.Dashboards})
    public void DRSQA_12537_ImmediateSingleLogoutForAdeApplicationsWithSmartIT(ITestContext context) throws Exception {
        log.info("------- START: DRSQA_12537 [RSSO] Immediate Single Logout for ADE applications - Interop / Converged server scenario");
        context.setAttribute("12537_Product", Product.Service_Monitoring);
        context.setAttribute("12537_WebDriver", getDriver());
        doPreconditions();
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12537");
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), SolQAConstants.appITOM);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
        portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
        portalFeatureMethods.logoutFromDashboardAndSwitchBackToTab(SolQAConstants.HELIX_ITSM);

    }

    @Test(description = "[RSSO] Immediate Single Logout for ADE only application - Non Interop / Converged server scenario",groups={"Regression"})
    @SolqaDetails(authorName = "vshelke", JiraID = "DRSQA-12536", Products = {Product.SmartIT, Product.Dashboards})
    public void DRSQA_12536_ImmediateSingleLogoutForAdeOnlyApplications(ITestContext context) throws Exception {
        log.info("------- START: 12536 [RSSO] Immediate Single Logout for ADE only application - Non Interop / Converged server scenario");
        log.info("------- Please makes sure to run this use cae on ADE only Stack (ITSM not integrated only ADE)--------");
        context.setAttribute("12536_Product", Product.Service_Monitoring);
        context.setAttribute("12536_WebDriver", getDriver());
        doPreconditions();
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12536");
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), SolQAConstants.appITOM);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_DASHBOARD);
        portalFeatureMethods.logoutFromDashboardAndSwitchBackToTab(SolQAConstants.BMC_HELIX_PORTAL);
        portalFeatureMethods.closeBrowser();
    }

    @AfterMethod(alwaysRun = true)
    public void quitdriver(){
            if (portalFeatureMethods != null){
                portalFeatureMethods.closeBrowser();}
        if (aiOpsFeatureMethods != null){
            aiOpsFeatureMethods.closeBrowser();}
        }

}
