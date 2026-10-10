package ITOM.LogAnalytics;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import util.*;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Api.LogAnalystics;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;

public class LogEventAlert {
    private static final SolqaLogger log = new SolqaLogger(LogEventAlert.class.getName());
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    private static final CommonWaits commanwait = new CommonWaits();
    String logstackhost = SolQAConstants.logstack_host;
    String logstackuser = SolQAConstants.logstack_admin_user;
    String logstackpassword = CommonMethods.readConfig("logstackAdminPassword");
    private String keystore_path = "/data1/logstash-7.8.1/logstash.jks";
    CommonMethods commonmethod = new CommonMethods();
    SoftAssert softAssert = new SoftAssert();
    private String jwtToken = null;
    Aiops aiopsApiCore = new Aiops();
    HMEvents hm_events = new HMEvents();
    CommonWaits commonwait = new CommonWaits();
    private static final String SSH_CONFIG_SHEET = "sshconfig";
    private static final int SSH_CONFIG_TC = 13345;
    private ExcelReaderForMLSituation excelReaderForMLSituation = new ExcelReaderForMLSituation();


    private void startLogstash() throws Exception {
        String kibana_dashboard = config.get("ADE_PLATFORM_URL") + "/log-service/api/v1.0/logs";
        String bhom_url = kibana_dashboard.replaceAll("^(https?://)", "");
        kibana_dashboard = kibana_dashboard.replace("/", "\\/");
        jwtToken = commonmethod.refreshJwtToken(null);
        log.info("Refreshed JWT token for starting logstash");
        
        String apiKey = hm_events.getBhomApiKey(jwtToken);
        log.info("Resolved BHOM API key for logstash");

        List<String> commands = List.of(
                "ps -eaf |grep logstash | awk '{print $2}' | xargs kill -9",
                "sed -i 's/apiKey.*$/apiKey "+ apiKey+ "\"/g' " + SolQAConstants.logstack_conf_file,
                "keytool -delete -alias dc -keystore " + keystore_path + " -storepass password",
                "keytool -printcert -sslserver " + bhom_url + " -rfc | keytool -importcert -keystore " + keystore_path + " -storepass password -noprompt -alias dc",
                "rm -f " + SolQAConstants.logstack_log_file,
                "sed -i 's/url=.*$/url=>\"" + kibana_dashboard + "\"/g' " + SolQAConstants.logstack_conf_file
        );
        List<Map<String, Object>> rows = excelReaderForMLSituation.getTestDataFromExcel(SSH_CONFIG_TC, SSH_CONFIG_SHEET);
        logstackhost = (String) rows.get(0).get("ssh_host");
        logstackuser = (String) rows.get(0).get("ssh_user");
        logstackpassword = (String) rows.get(0).get("ssh_password");

        SSHConnector sshcon = new SSHConnector();
        sshcon.connect(logstackhost, logstackuser, logstackpassword, 22);
        sshcon.openChannel();
        log.info("SSH channel opened to logstack host");

        sshcon.executeCommands(commands, 15000);
        log.info("Executed logstash setup commands");


        sshcon.write("cd "+ SolQAConstants.logstack_home + "\n");
        String output = sshcon.read(1000);
        System.out.println(output);
        commanwait.waitForSometime(2000);
        log.info("Changed directory to logstash home");
        sshcon.write("nohup " + SolQAConstants.logstack_home + "/bin/logstash -f " + SolQAConstants.logstack_conf_file + " --config.reload.automatic \n");
        commanwait.waitForSometime(2000);
        sshcon.write("\n");
        output = sshcon.read(1000);
        System.out.println(output);
        sshcon.closeChannel();
        sshcon.disconnect();
        log.info("Closed SSH channel and disconnected");
    }

    @Test(description = "DRSQA-12386 LogAnalytics: Verify cross launch to log analytics from BHOM Alert", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris",  TTID="12386", JiraID = "DRSQA-12346", Products = {Product.ADE_Platform})
    public void DRSQA_12386_LogAnalytics_Verify_cross_launch_to_log_analytics_from_BHOM_Alert() throws Exception {
        String eventClass = "LOGALERT_EV";
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        String msearchQueryFile = "msearchqueryonOpenClass.json";
        String alerthrefstring = "<a href=/loganalytics/#/explorer/app/data-explorer/discover";
        LogAnalystics logAnalysticsApiCore = new LogAnalystics();
        jwtToken = commonmethod.refreshJwtToken(null);
        log.info("Refreshed JWT token for event alert test");
        hm_events.closeAllEventByClass(eventClass, jwtToken);
        log.info("Closed all events for class: " + eventClass);
        commonwait.waitForSometime(10000);
        //hm_events.CloseAllOpenEvent(jwtToken);
        log.info("Closed all open events");
        String subString = "CrossLaunch";
        String randomStr = RandomStringUtils.randomAlphanumeric(7);
        String summaryString = "CrossLaunchToBHOM-" + randomStr;
        log.info("Prepared event alert summary: " + summaryString);
        logAnalyticsPage.auth_type = 1;
        logAnalyticsPage.getloganalyticscookies();
        log.info("Retrieved Log Analytics cookies");
        logAnalysticsApiCore.setrestassuredcookies(logAnalyticsPage.seleniumCookies);
        log.info("Delete Event Alert: " + subString);
        logAnalysticsApiCore.deleteEventAlert(subString);
        logAnalysticsApiCore.deleteEventAlert("Alert-to-bhom");
        startLogstash();
        log.info("Logstash started");
        commonwait.waitForSometime(10000);
        log.info("Create Event Alert: " + summaryString);

        if(!logAnalysticsApiCore.CreateEventAlert(summaryString, logstackhost)) {
            Assert.fail("Failed to create event alert: " + summaryString);
        }
        log.info("Event alert created successfully: " + summaryString);
        
        commonwait.waitForSometime(60000);
        log.info("Get All Open Events of class: " + eventClass);
        String EventId = null;
        for(int i = 0; i < 10; i++) {
            JSONObject events_Resp = hm_events.runMSearchQueryGetSituation(eventClass,jwtToken,msearchQueryFile,2);
            log.info("MSearch attempt " + (i + 1) + " completed");
            if(events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits").length() > 0) {
                EventId = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("_identifier");
                String alert_launch_params = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source").getString("alert_launch_params");
                if(!alert_launch_params.contains(alerthrefstring)) {
                    softAssert.fail("Alert launch params does not contain the alerthrefstring: " + alerthrefstring);
                }
                Pattern p = Pattern.compile("^[^#]+?(?:\\.\\d+)?(?=[. ;])");
                Matcher m = p.matcher(logstackhost);
                List<String> matches = new ArrayList<>();
                while (m.find()) {
                    matches.add(m.group());
                }
                String logHostForPolicy = matches.isEmpty() ? logstackhost : matches.get(0);
                log.info("Parsed hostname candidates from logstackhost: " + matches);
                if(!alert_launch_params.contains(logHostForPolicy)) {
                    softAssert.fail("Alert launch params does not contain Hostname: " + logHostForPolicy);
                }
                log.info("Open event found with ID: " + EventId);
                break;
            }
            commonwait.waitForSometime(10000);
            log.info("Retrying event search attempt " + (i + 1));
        }
        logAnalyticsPage.auth_type = 1;
        logAnalyticsPage.getloganalyticscookies();
        logAnalysticsApiCore.setrestassuredcookies(LogAnalyticsPage.seleniumCookies);
        log.info("Delete Event Alert: " + subString);
        logAnalysticsApiCore.deleteEventAlert(summaryString);
        log.info("Deleted event alert: " + summaryString);
        log.info("Asserting all soft assertions");
        softAssert.assertAll();
        
    }
}
