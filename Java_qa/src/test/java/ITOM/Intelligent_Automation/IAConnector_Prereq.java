package ITOM.Intelligent_Automation;

import framework.Launcher;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Intelligent_Automation.IAFeatureMethod;
import util.*;

import java.io.File;
import java.util.Map;

public class IAConnector_Prereq extends Launcher {
    private static SolqaLogger log = new SolqaLogger(IAConnector_Prereq.class.getName());
    public String app= SolQAConstants.appITOM;
    public String portal=SolQAConstants.BMC_HELIX_PORTAL;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    IAFeatureMethod iaFeatureMethod;
    BhomFeatureMethods bhomFeatureMethods;

    public void doPreconditions() {
        launchWeb(app, app);
        login(config.get("ADE_USERNAME"),config.get("ADE_PASSWORD"),app);
        iaFeatureMethod = new IAFeatureMethod();
        bhomFeatureMethods = new BhomFeatureMethods();
    }

    @Test(description = " This is a one time pre-req before running IA test cases where Generic rest Api connector is used")
    @SolqaDetails(authorName = "vshelke",  TTID="TBD", JiraID = "TBD", Products = {Product.intelligent_automation})
    public void IAConnector_Pre_Req() throws Exception {

        log.info("------- START: Pre req test for IA Connector : Generic rest Api Connector ------------");
        doPreconditions();

        log.info("------- START: Create Automation Pre-requisite IA policy ------------");
        iaFeatureMethod.connect_GenericRestAPI_Connector();

        log.info("------- END: Pre req test for IA Connector : Generic rest Api Connector ------------");
    }

    @AfterTest
    public void quitedriver(){
        getDriver().quit();
    }
}
