package ITOM.Intelligent_Automation;

import framework.Launcher;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Intelligent_Automation.*;
import util.*;

import java.io.File;
import java.util.Map;

public class SNOW_Prereq extends Launcher
{
    private static SolqaLogger log = new SolqaLogger(SNOW_Prereq.class.getName());
    public String app=SolQAConstants.appITOM;
    public String portal=SolQAConstants.BMC_HELIX_PORTAL;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    String cluster_CI=config.get("CLUSTER_CI");

    IAFeatureMethod IAFeatureMethod;
    BhomFeatureMethods bhomFeatureMethods;

    public void doPreconditions() {
        launchWeb(app, app);
        login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
        IAFeatureMethod = new IAFeatureMethod();
        bhomFeatureMethods = new BhomFeatureMethods();
     }


    @Test(description = " This is a one time pre-req before running service now tests.")
    @SolqaDetails(authorName = "dmascare",  TTID="TBD", JiraID = "TBD", Products = {Product.intelligent_automation,Product.AiOPS, Product.discovery, Product.ADE_OperationsManagement})
    public void ServiceNow_Pre_Req() {

        log.info("------- START: This is a one time pre-req before running service now tests. ------------");
        doPreconditions();

        log.info("Wake up SNOW instance if its hibernating.");
        IAFeatureMethod.wakeUpSNOWFromHibernation();

        log.info("Check if SNOW CMDB is present. If not create it.");
        IAFeatureMethod.createSNOWCMDBIfNotPresent(SolQAConstants.SOLQA_SNOW_CMDB);

        log.info("***** Create two Services --> " + SolQAConstants.SNOW_Service_Secondary +", "+SolQAConstants.SNOW_Service_Secondary+ " <-- with CI in Discovery*******");

        IAFeatureMethod.CreatePlainServiceInAIOps(SolQAConstants.SNOW_Service_Primary,cluster_CI);
        IAFeatureMethod.switchToPortalFromDiscovery();
        IAFeatureMethod.CreatePlainServiceInAIOps(SolQAConstants.SNOW_Service_Secondary,cluster_CI);

        log.info("Perform CMDB Sync from discovery side. This will take time based on load of data in the system to be Sync-ed. If it takes more than 30 min. Script execution might fail owing to session timeout. In that case monitor it manully. ");
        IAFeatureMethod.syncServiceNowCMDB(SolQAConstants.SOLQA_SNOW_CMDB);

        log.info("Now perform ADDM Job reconcilation from CMDB side.");
        IAFeatureMethod.runADDMJob();

        log.info("------- END: Pre req test for SNOW ------------");
    }


    @AfterTest
    public void quitedriver(){
        getDriver().quit();
    }

}
