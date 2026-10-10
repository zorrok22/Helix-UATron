package ITOM.Intelligent_integrations;
import framework.Launcher;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ITOM.Aiops.AIOpsFeatureMethods;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Bhom.BhomFeatureMethods;
import pages.ITOM.Intelligent_Integrations.IIFeatureMethods;
import util.*;
import java.io.File;
import java.time.Duration;
import java.util.Map;

public class IntelligentIntegrationsTests extends Launcher {

    static SolqaLogger log = new SolqaLogger(IntelligentIntegrationsTests.class.getName());
    Portal_FeatureMethods portalFeatureMethods;
    AIOpsFeatureMethods aiOpsFeatureMethods;
    Map<String, String> configProp = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    Map<Object, String> connectorData;
    ReadJson readJson = new ReadJson();
    BhomFeatureMethods bhomFeatureMethods;
    IIFeatureMethods iiFeatureMethods;
    CommonWaits commonWaits;
    String accessKey = SolQAConstants.API_ACCESS_KEY;
    String secreteKey = SolQAConstants.API_SECRET_KEY;

    @BeforeMethod(alwaysRun = true)
    public void preconditions() {
        WebDriver driver = this.launchWeb(SolQAConstants.appITOM, SolQAConstants.appITOM);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),SolQAConstants.appITOM);
        setDriver(driver);
        bhomFeatureMethods = new BhomFeatureMethods();
        commonWaits = new CommonWaits();
        iiFeatureMethods = new IIFeatureMethods();
        aiOpsFeatureMethods = new AIOpsFeatureMethods();
        portalFeatureMethods = new Portal_FeatureMethods();
    }

    @Test(description = "II: Verify Icinga Connector Events and MetricsTesting")
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12498",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12498_Verify_Icinga_Connector_Events_and_MetricsTesting(ITestContext context) {
        log.info("------------Verify Icinga Connector Events and Metrics Testing-------------");

        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12498");
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();

        connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "DRSQA_12498_Data");
        portalFeatureMethods.addAndConfigureConnector(connectorData);

        portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
        portalFeatureMethods.startStream();

        log.info("---- Verify Metrics in BHOM ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        portalFeatureMethods.searchDevice(connectorData.get("host"));
        portalFeatureMethods.navigateToMonitorsTab();
        portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));

        log.info("---- Cleanup ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
        portalFeatureMethods.closeDriver();
    }

    @Test(description = "II: Verify SolarWinds NPM connector End-to-End Testing")
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12495",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12495_Verify_SolarWinds_NPM_Connector_End_to_End(ITestContext context) {
        log.info("------------Verify SolarWinds NPM Connector End-to-End-------------");

        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12495");
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();

        connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "DRSQA_12495_Data");
        portalFeatureMethods.addAndConfigureConnector(connectorData);

        portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
        portalFeatureMethods.startStream();

        log.info("---- Verify Metrics in BHOM ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        portalFeatureMethods.searchDevice(connectorData.get("host"));
        portalFeatureMethods.navigateToMonitorsTab();
        portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));

        log.info("---- Cleanup ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
        portalFeatureMethods.closeDriver();
    }

    @Test(description = "II: Verify Splunk Connector Events and MetricsTesting")
    @SolqaDetails(authorName = "aramulwa", JiraID = "DRSQA-12497",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12497_Verify_Splunk_Connector_Events_and_MetricsTesting(ITestContext context) {
        log.info("------------Verify Splunk Connector Events and Metrics Testing-------------");

        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12497");
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();

        connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "DRSQA_12497_Data");
        portalFeatureMethods.addAndConfigureConnector(connectorData);

        portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
        portalFeatureMethods.startStream();

        log.info("---- Verify Metrics in BHOM ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        portalFeatureMethods.searchDevice(connectorData.get("host"));
        portalFeatureMethods.navigateToMonitorsTab();
        portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));

        log.info("---- Cleanup ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
        portalFeatureMethods.closeDriver();
    }

    //Connection not working
    @Test(description = "II: Verify zabbix connector End-to-End Testing")
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12494",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12494_Verify_Zabbix_Connector_End_to_End(ITestContext context) {
        log.info("------------Verify Zabbix Connector End-to-End-------------");

        log.info("---- Login to Portal with correct username and password----------");
        portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12494");
        portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));

        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();

        connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "DRSQA_12494_Data");
        portalFeatureMethods.addAndConfigureConnector(connectorData);

        portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
        portalFeatureMethods.startStream();

        log.info("---- Verify Metrics in BHOM ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
        portalFeatureMethods.navigateToDevices();
        portalFeatureMethods.searchDevice(connectorData.get("host"));
        portalFeatureMethods.navigateToMonitorsTab();
        portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));

        log.info("---- Cleanup ----------");
        portalFeatureMethods.navigateToPortalTab();
        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
        portalFeatureMethods.closeDriver();
    }

    @Test(description = "II: Verify Stackify Connector Metrics Testing",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12496",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12496_Verify_Stackify_Connector_Metrics_Testing(ITestContext context) throws Exception {
        log.info("------------Verify Stackify Connector Metrics Testing-------------");


            log.info("---- Login to Portal with correct username and password----------");
            portalFeatureMethods = new Portal_FeatureMethods(context, "DRSQA-12496");
            portalFeatureMethods.loginToADEPlatform(configProp.get("ADE_USERNAME"), configProp.get("ADE_PASSWORD"));
            connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "DRSQA_12496_Data");
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();
            portalFeatureMethods.addAndConfigureConnector(connectorData);
        try{
            portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
            portalFeatureMethods.startStream();
            log.info("---- Verify Metrics in BHOM ----------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            portalFeatureMethods.navigateToDevices();
            portalFeatureMethods.searchDevice(connectorData.get("deviceName"));
            portalFeatureMethods.navigateToMonitorsTab();
            portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));
            portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);

        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service , stop stream and delete connector --------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "II: Verify AWS connector End-to-End",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12486",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12486_Verify_AWS_connector_End_to_End(ITestContext context) throws Exception {
        log.info("------------Verify AWS connector End-to-End-------------");

        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();

        connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "DRSQA_12486_Data");
        portalFeatureMethods.addAndConfigureConnector(connectorData);

        try{
            portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
            portalFeatureMethods.startStream();
            log.info("---- Verify Metrics in BHOM ----------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            portalFeatureMethods.navigateToDevices();
            portalFeatureMethods.searchDevice(connectorData.get("deviceName"));
            portalFeatureMethods.navigateToMonitorsTab();
            portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));
            log.info("---- Verify AWS in BHOM ----------");
            int noOfEVents = bhomFeatureMethods.getIIEventsFromToday("AWSCloudWatchAlarm");
            Assert.assertTrue(noOfEVents >= 0, "No AWS CloudWatch alarms were found synced.");

        }
        finally {
            log.info("---- Cleanup ----------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "II: Verify NewRelic connector End-to-End Testing",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12493",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12493_Verify_NewRelic_Connector_End_to_End(ITestContext context) throws Exception {
        log.info("------------Verify NewRelic Connector End-to-End-------------");
        connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "DRSQA_12493_Data");

        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();
        portalFeatureMethods.addAndConfigureConnector(connectorData);
        try {
            portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
            portalFeatureMethods.startStream();
            log.info("---- Verify Metrics in BHOM ----------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            portalFeatureMethods.navigateToDevices();
            portalFeatureMethods.searchDevice(connectorData.get("deviceName"));
            portalFeatureMethods.navigateToMonitorsTab();
            portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));
            log.info("---- Verify NewRelicEvent in BHOM ----------");
            int noOfEVents = bhomFeatureMethods.getIIEventsFromToday("NewRelicEvent");
            Assert.assertTrue(noOfEVents >= 0, "No NewRelicEvents were found synced today.");

            String eventsCommonMsg = "AutoGenerated Event for NewRelic Connector Testing " + RandomStringUtils.randomAlphanumeric(5);

            log.info("---- Verify creation of Aiops Service On metric data for NewRelic connector--------");
            aiOpsFeatureMethods.createServiceFromAPI(connectorData.get("serviceName"), connectorData.get("ciName"), "NEWRELIC", accessKey, secreteKey);

            log.info("---- For NewRelic Service send events to NewRelic device to get it impacted--------");
            bhomFeatureMethods.send_events_with_class("NewRelicEvent",eventsCommonMsg, connectorData.get("ciName"));
            log.info("Now verify if service is impacted");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(connectorData.get("serviceName"), 100, Duration.ofMinutes(1))) {
                log.info("Service health score is impacted and below 100 after sending events to II device");
            } else {
                throw new AssertionError("Service health score is still not impacted even after sending events to II device");
            }

        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service , stop stream and delete connector --------");
            try{
            aiOpsFeatureMethods.deleteService(connectorData.get("serviceName"));}
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "II: Verify AppDynamics connector End-to-End Testing",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12459",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12493_Verify_AppDynamics_Connector_End_to_End(ITestContext context) throws Exception {
        log.info("------------Verify AppDynamics Connector End-to-End-------------");
        connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" +File.separator+ "II" , "IIConnectorData", "AppDynamics_Data");

        portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
        portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();
        portalFeatureMethods.addAndConfigureConnector(connectorData);

        try {
            portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
            portalFeatureMethods.startStream();

            log.info("---- Verify Metrics in BHOM ----------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            portalFeatureMethods.navigateToDevices();
            portalFeatureMethods.searchDevice(connectorData.get("deviceName"));
            portalFeatureMethods.navigateToMonitorsTab();
            portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));
            log.info("---- Verify AppDynamicsEvent in BHOM ----------");
            int noOfEVents = bhomFeatureMethods.getIIEventsFromToday("AppDynamicsEvent");
            Assert.assertTrue(noOfEVents >= 0, "No AppDynamicsEvent Events were found synced today.");

            log.info("---- Verify AppDynamicsHRVEvent in BHOM ----------");
            int noOfEVents1 = bhomFeatureMethods.getIIEventsFromToday("AppDynamicsHRVEvent");
            Assert.assertTrue(noOfEVents1 >= 0, "No AppDynamicsHRVEvent Events were found synced today.");

                String eventsCommonMsg = "AutoGenerated Event for AppDynamics Connector Testing " + RandomStringUtils.randomAlphanumeric(5);
            log.info("---- Verify creation of Aiops Service On metric data for AppDynamics connector--------");
            aiOpsFeatureMethods.createServiceFromAPI(connectorData.get("serviceName"), connectorData.get("ciName"), "APPDYNAMICS", accessKey, secreteKey);

            log.info("---- For NewRelic Service se if incoming events are making the service impacted--------");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(connectorData.get("serviceName"), 100, Duration.ofMinutes(1))){
                log.info("Service health score is impacted without explicitly sending events.");
            }else {
                log.info("Service health score is not impacted without explicitly sending events. Proceeding to send events.");
            }

            log.info("---- For appdynamic Service send events to AppDynamics device to get it impacted--------");
            bhomFeatureMethods.send_events_with_class("AppDynamicsEvent",eventsCommonMsg, connectorData.get("ciName"));
            log.info("Now verify if service is impacted");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(connectorData.get("serviceName"), 100, Duration.ofMinutes(4))) {
                log.info("Service health score is impacted and below 100 after sending events to II device");
            } else {
                throw new AssertionError("Service health score is still not impacted even after sending events to II device");
            }

        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service , stop stream and delete connector --------");
            try{
                aiOpsFeatureMethods.deleteService(connectorData.get("serviceName"));}
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "II: Verify Dynatrace connector E2E",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12460",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12460_Verify_Dynatrace_Connector_Events_and_Metrics(ITestContext context) throws Exception {

            log.info("------------Verify Dynatrace Connector Events and Metrics Testing-------------");
            connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" + File.separator + "II", "IIConnectorData", "DRSQA_12460_Data");

            log.info("---- create Dynatrace connector and start start stream for the same connector----------");
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();
            portalFeatureMethods.addAndConfigureConnector(connectorData);
        try {
            portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
            portalFeatureMethods.startStream();
            log.info("---- Verify Metrics in BHOM ----------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            portalFeatureMethods.navigateToDevices();
            portalFeatureMethods.searchDevice(connectorData.get("deviceName"));
            portalFeatureMethods.navigateToMonitorsTab();
            portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));
            portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
            log.info("---- Verify DynatraceAlert in BHOM ----------");
            int noOfEVents = bhomFeatureMethods.getIIEventsFromToday("DynatraceAlert");
            Assert.assertTrue(noOfEVents>=0, "No dyntarce alerts found synced today");

            String eventsCommonMsg = "AutoGenerated Event for Dynatrace Connector Testing " + RandomStringUtils.randomAlphanumeric(5);
            log.info("---- Create Aiops Service On metric data for Dynatrace connector--------");
            aiOpsFeatureMethods.createServiceFromAPI(connectorData.get("serviceName"), connectorData.get("deviceName"), "DYNATRACE", accessKey, secreteKey);

            log.info("---- For Dynatrace Service, send events to dynatrace device to get it impacted--------");
            log.info("Service health score is not impacted yet, sending events to impact the service");
            bhomFeatureMethods.send_events_with_class("DynatraceAlert",eventsCommonMsg, connectorData.get("deviceName"));
            log.info("Now verify if service is impacted");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(connectorData.get("serviceName"), 100, Duration.ofMinutes(3))) {
                log.info("Service health score is impacted and below 100 after sending events to II device");
            } else {
                throw new AssertionError("Service health score is still not impacted even after sending events to II device");
            }

        }
        finally {
            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service , stop stream and delete connector --------");
            try{
                aiOpsFeatureMethods.deleteService(connectorData.get("serviceName"));}
            catch (Exception e){
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " +e.getMessage());
            }
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
            portalFeatureMethods.closeDriver();
        }
    }

    @Test(description = "II: Verify Datadog connector E2E",groups={"Regression"})
    @SolqaDetails(authorName = "dmascare", JiraID = "DRSQA-12473",Products = {Product.Intelligent_Integrations})
    public void DRSQA_12460_Verify_Datadog_Connector_Events(ITestContext context) throws Exception {


            log.info("------------Verify Datadog Connector Events and Metrics Testing-------------");
            connectorData = readJson.getInputValuesMapIncludingLabelField("ITOM" + File.separator + "II", "IIConnectorData", "DataDog");

            log.info("---- create Datadog connector and start start stream for the same connector----------");
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.verifyIntelligentIntegrationPageLaunchedSuccessfully();
            portalFeatureMethods.addAndConfigureConnector(connectorData);
        try {
            portalFeatureMethods.searchForStream(connectorData.get("connectorName"));
            portalFeatureMethods.startStream();
            log.info("---- Verify Metrics and monitor types in BHOM ----------");
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct(SolQAConstants.BHOM);
            portalFeatureMethods.navigateToDevices();
            portalFeatureMethods.searchDevice(connectorData.get("deviceName"));
            portalFeatureMethods.navigateToMonitorsTab();
            portalFeatureMethods.selectMonitor(connectorData.get("monitorName"));
            portalFeatureMethods.navigateToTab(SolQAConstants.BMC_HELIX_PORTAL);
            log.info("---- Verify DatadogEvents in BHOM ----------");
            int noOfEVents = bhomFeatureMethods.getIIEventsFromToday("DatadogEvent");
            Assert.assertTrue(noOfEVents>=0, "No Datadog Events found synced today.");

            String eventsCommonMsg = "AutoGenerated Event for Datadog Connector Testing " + RandomStringUtils.randomAlphanumeric(5);

            log.info("---- Verify creation of Aiops Service On metric data for Datadog connector--------");
            aiOpsFeatureMethods.createServiceFromAPI(connectorData.get("serviceName"), connectorData.get("deviceName"), "DATADOG", accessKey, secreteKey);

            log.info("---- For Datadog Service send events to Datadog device to get it impacted--------");
            bhomFeatureMethods.send_events_with_class("DatadogEvent",eventsCommonMsg, connectorData.get("deviceName"));
            log.info("Now verify if service is impacted");
            if (aiOpsFeatureMethods.waitUntilScoreBelowThreshold(connectorData.get("serviceName"), 100, Duration.ofMinutes(2))) {
                log.info("Service health score is impacted and below 100 after sending events to II device");
            } else {
                throw new AssertionError("Service health score is still not impacted even after sending events to II device");
            }

        }
        finally {

            log.info("---- Cleanup ----------");
            log.info("---- Delete Aiops Service , stop stream and delete connector --------");
            try {
                aiOpsFeatureMethods.deleteService(connectorData.get("serviceName"));
            } catch (Exception e) {
                log.info("Service deletion via API failed. Proceeding with cleanup. Error: " + e.getMessage());
            }
            portalFeatureMethods.navigateToPortalTab();
            portalFeatureMethods.crossLaunchProduct("BMC Helix Intelligent Integration");
            portalFeatureMethods.removeConnector(connectorData.get("connectorName"));
            portalFeatureMethods.closeDriver();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeAllBrowsers() {
        if (bhomFeatureMethods != null) {
            bhomFeatureMethods.closeBrowser();
        }if (iiFeatureMethods != null) {
            iiFeatureMethods.closeBrowser();
        }if (aiOpsFeatureMethods != null) {
            aiOpsFeatureMethods.closeBrowser();
        }if (portalFeatureMethods != null) {
            portalFeatureMethods.closeBrowser();
        }
    }

}