package ITOM.Discovery;

import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Discovery.DiscoveryFeatureMethods;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import util.*;
import java.io.File;
import java.util.Map;

public class DiscoveryTests extends Launcher {
    public static SolqaLogger log = new SolqaLogger(DiscoveryTests.class.getName());
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    Map<Object, String> aiopsTestData;

    IAFeatureMethod iaFeatureMethod;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    DiscoveryFeatureMethods discoveryFeatureMethods;
    String accessKey = SolQAConstants.API_ACCESS_KEY;
    String secreteKey = SolQAConstants.API_SECRET_KEY;
    ReadJson readJson;
    WebDriver adeDriver;

    public void doPreconditions() {
        adeDriver=launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        setDriver(adeDriver);
        iaFeatureMethod = new IAFeatureMethod();
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        discoveryFeatureMethods = new DiscoveryFeatureMethods();
        readJson = new ReadJson();
    }

    @Test(description = "Import - Export blueprint in AIOps and verify models in discovery",groups={"Regression"})
    @SolqaDetails(authorName = "vshelke", JiraID = "12279", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12279_ImportExportBlueprintInAIOpsAndVerifyModelsInDiscovery(ITestContext context) {
        log.info("------- START: DRSQA_12279 Import - Export blueprint in AIOps and verify models in discovery");
        context.setAttribute("12279_Product", Product.Service_Monitoring);
        context.setAttribute("12279_WebDriver", getDriver());
        doPreconditions();

        String blueprint_content_filename = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "ITOM" + File.separator + "Aiops" + File.separator + "servicemodeling_k8s_blueprint.json";
        String downloadDir = getDefaultDownloadPath();
        aiopsTestData = readJson.getInputValuesMapIncludingLabelField(File.separator + SolQAConstants.appITOM + File.separator + SolQAConstants.appAiops, "AiopsTestData",
                "DRSQA_12279_testData");
        String blueprintName = aiopsTestData.get("blueprintName") + RandomStringUtils.randomAlphanumeric(4);
        login(config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"), SolQAConstants.appITOM);
        aiOpsFeatureMethods.verifyLoginToADEPlatform();
        aiOpsFeatureMethods.navigateToAIOps();
        String blueprintId = aiOpsFeatureMethods.createBlueprint(blueprintName, blueprint_content_filename, accessKey, secreteKey);
           log.info("-------Blueprint Created with name: " + blueprintName + "-------");
           try {
               String filePath = aiOpsFeatureMethods.exportBlueprintsFromAiops(blueprintName, downloadDir);
               aiOpsFeatureMethods.deleteBlueprint(blueprintId, accessKey, secreteKey);
               discoveryFeatureMethods.navigateToDiscoveryAndImportBlueprint(filePath);
               aiOpsFeatureMethods.verifyBlueprintImportedSuccessfully(blueprintName);
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
           finally {
               log.info("---------clean up------------");
               aiOpsFeatureMethods.deleteBlueprint(blueprintId, accessKey,secreteKey);
               aiOpsFeatureMethods.closeBrowser();
           }
        log.info("------- END: DRSQA_12279 Import - Export blueprint in AIOps and verify models in discovery");
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        if (iaFeatureMethod != null) {
            iaFeatureMethod.closeBrowser();
        }if (aiOpsFeatureMethods != null) {
            aiOpsFeatureMethods.closeBrowser();
        }if (discoveryFeatureMethods != null) {
            discoveryFeatureMethods.closeBrowser();
        }
    }
}
