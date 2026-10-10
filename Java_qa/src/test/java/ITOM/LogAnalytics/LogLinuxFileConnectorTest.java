package ITOM.LogAnalytics;

import com.jcraft.jsch.JSchException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Api.LogAnalystics;
import pages.ITOM.LogAnalytics.LogAnalyticsDownloadPage;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.*;
import org.openqa.selenium.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import framework.Launcher;
import java.time.Instant;


import org.apache.commons.lang3.RandomStringUtils;

/**
 * Java translation of Robot "Linux file Connector" (logcollection.robot).
 * Focuses on REST-based creation/cleanup of parsing & collection rules and connector setup.
 * Log push/validation steps are left as TODO where shell/SSH automation is required.
 */
public class LogLinuxFileConnectorTest extends Launcher{

    private static final SolqaLogger log = new SolqaLogger(LogLinuxFileConnectorTest.class.getName());
    private static final String PARSING_RULE_NAME = "automation-linux-rule";
    private static final String COLLECTION_RULE_NAME = "automation-linux-collection";
    private static final String TAG_NAME = "bmc-tag-linux-collection";
    private static final String LOG_PATH = "/opt/error*";
    private static final CommonWaits commonwait = new CommonWaits();
    private LogAnalystics loganalystics = new LogAnalystics();
    private String templateId = null;
    private String app = SolQAConstants.appITOM;
    private String tenantId = null;
    private String hostName = "host-01";
    private String jwtToken = null;
    private CommonMethods commonmethod = new CommonMethods();
    private String ConnectorPolicyName = "automation-linux-connector";
    private static final String SSH_CONFIG_SHEET = "sshconfig";
    private static final int SSH_CONFIG_TC = 13336;
    private final Map<String, Object> sshConfig = loadSshConfig();
    private final String Linux8MachineName = requireSshValue("ssh_host");
    private final String Linux8MachineName_uname = requireSshValue("ssh_user");
    private final String Linux8MachineName_pwd = requireSshValue("ssh_password");
    LogAnalyticsDownloadPage logdownloadconnector = new LogAnalyticsDownloadPage();
    private static ExcelReaderForMLSituation excelReaderForML = new ExcelReaderForMLSituation();

    private WebDriver driver;

    private void CleanupExisingConnectorInstallation() throws JSchException, IOException {
        log.info("Starting cleanup of existing connector installation");
        List<String> commands = List.of(
                "cd /data1 && ./cleanup.sh"
        );
        SSHConnector sshcon = new SSHConnector();
        sshcon.connect(Linux8MachineName, Linux8MachineName_uname, Linux8MachineName_pwd, 22);
        log.info("SSH connected for cleanup");
        sshcon.openChannel();
        sshcon.executeCommands(commands, 15000);
        log.info("Cleanup commands executed");
        sshcon.closeChannel();
        sshcon.disconnect();
        log.info("SSH disconnected after cleanup");
    }




    private void deleteParsingRuleIfExists() {
        log.info("Checking for existing parsing rules");
        JSONObject parsingRule = loganalystics.getParsingRule(PARSING_RULE_NAME);
        JSONArray data = parsingRule.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            String name = obj.optString("name", "");
            if (name.contains(PARSING_RULE_NAME)) {
                String id = obj.optString("id", "");
                log.info("Deleting parsing rule: " + name);
                loganalystics.deleteParsingRule(id, jwtToken);
                commonwait.waitForSometime(15000);
                log.info("Deleted parsing rule id: " + id);
            }
        }
    }

    private void deleteCollectionRuleIfExists() {
        log.info("Checking for existing collection rules");
        JSONObject collectionRule = loganalystics.getCollection(COLLECTION_RULE_NAME);
        JSONArray data = collectionRule.getJSONArray("data");
        log.info("Collection record fetch " +  data.length());
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            String name = obj.optString("name", "");
            log.info("Collection record Name " +  name);
            if (name.contains(COLLECTION_RULE_NAME)) {
                String id = obj.optString("id", "");
                loganalystics.disableCollectionRule(id);
                log.info("Disable Collection Policy " +  name);
                commonwait.waitForSometime(10000);
                log.info("Deleting collection policy: " + name);
                loganalystics.deleteCollectionRule(id, jwtToken);
                commonwait.waitForSometime(10000);
                log.info("Deleted collection policy id: " + id);
            }
        }
    }



    private String createParsingRule() {
        String templateName = "Collect Logs from File";
        templateId = loganalystics.getParsingRuleTemplate(templateName);
        String ruleId = loganalystics.CreateParsingRule(PARSING_RULE_NAME,templateId);
        log.info("Created parsing rule id: " + ruleId);
        return ruleId;
    }

    private boolean createCollectionRule(String ruleID) {
        log.info("Creating collection rule with ruleID: " + ruleID);
        Map<String,String> usergroup = loganalystics.getUserGroupDetail();

        boolean status = loganalystics.CreateCollectionRule(COLLECTION_RULE_NAME,templateId,tenantId,hostName,usergroup.get("Administrators"),"Administrators",
                ruleID,PARSING_RULE_NAME, LOG_PATH,TAG_NAME);
        if (!status){
            Assert.fail("Fail to create Collection Rule");
        }

        return true;
    }

    private boolean WaitForConnectorStatus(String connectorName) {
        for(int i =0; i < 40; i++) {
            JSONObject connector = loganalystics.getConnector(connectorName);
            log.info("Wait for Connector Status: " + connector.toString());
            if (connector.getJSONObject("response").getJSONArray("data").length() == 0) {
                commonwait.waitForSometime(10000);
                continue;
            }
            String status = connector.getJSONObject("response").getJSONArray("data").getJSONObject(0).getString("status");
            if (status.equalsIgnoreCase("active")) {
                log.info("Connector is active: " + connectorName);
                return true;
            }
            commonwait.waitForSometime(10000);
        }
        return false;
    }


    private boolean validateLinuxConnector() throws JSchException, IOException, InterruptedException {
        HMEvents hm_events = new HMEvents();
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        for(int i = 0; i < 10; i++) {
            long currenttime = hm_events.getBhomServerTime(jwtToken) / 1000;
            long starttime = currenttime - 43200;
            String currentTime = Instant.ofEpochSecond(currenttime).toString();
            String startTime = Instant.ofEpochSecond(starttime).toString();
            String query = "{\"bool\":{\"must\":[],\"filter\":[{\"match_all\":{}},{\"match_phrase\":{\"bmc_tags\":\"bmc-tag-linux-collection\"}},{\"range\":{\"@timestamp\":{\"gte\":\"" + startTime + "\",\"lte\":\"" + currentTime + "\",\"format\":\"strict_date_optional_time\"}}}],\"should\":[],\"must_not\":[]}}";
            JSONObject logAn = logAnalyticsPage.logRetrieval(query, "15");
            Assert.assertNotNull(logAn);
            int totalrecord = logAn.getJSONArray("responses").getJSONObject(0).getJSONObject("_shards").getInt("total");
            if (totalrecord < 1) {
                commonwait.waitForSometime(10000);
                continue;
            }
            log.info("Validated Linux connector logs present, total shards: " + totalrecord);
            return true;
        }
        return false;
    }

    private void pushLogFile() throws JSchException, IOException {
        log.info("Pushing log file to remote host");
        List<String> commands = List.of(
                "cd /data1 && rm -f error_n.log",
                "cd /data1 && sh add_error_line.sh error_n.log",
                "cd /opt && cp /data1/error_n.log ."
        );
        SSHConnector sshcon = new SSHConnector();
        sshcon.connect(Linux8MachineName, Linux8MachineName_uname, Linux8MachineName_pwd, 22);
        log.info("SSH connected for log push");
        sshcon.openChannel();
        sshcon.executeCommands(commands, 15000);
        sshcon.closeChannel();
        sshcon.disconnect();
        log.info("Log file push complete");
    }

    private boolean pushDeployLinuxConnector() throws JSchException, IOException {
        log.info("Starting push and deploy of Linux connector");
        String downloadFilepath = logdownloadconnector.getDownloadBaseDir() + File.separator +  "downloadLocation";

        List<String> commands = List.of(
                "mkdir -p /data1/downloadLocation",
                "rm -rf /data1/downloadLocation/*"
        );

        SSHConnector sshcon = new SSHConnector();
        sshcon.connect(Linux8MachineName, Linux8MachineName_uname, Linux8MachineName_pwd, 22);
        log.info("SSH connected for connector deployment");
        sshcon.openChannel();
        sshcon.executeCommands(commands, 15000);
        File dl = new File(downloadFilepath);
        File[] files = dl.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    String remotePath = "/data1/downloadLocation/" + f.getName();
                    sshcon.transferFile(f.getAbsolutePath(), remotePath);
                    log.info("Transferred file to remote: " + remotePath);
                }
            }
        }
        SSHConnector.CommandResult output = sshcon.executeSingleCommand("chmod 777 /data1/downloadLocation/*.sh", 15000);

        output = sshcon.executeSingleCommand("cd /data1/downloadLocation && ls -ltr |awk '{print $9}'|grep \"tdc-connector*.*gz\"", 15000);
        if (output.exitCode != 0) {
            Assert.fail("Fail to get connector file name: " + output.stderr);
        }
        if (output.stdout.isEmpty()) {
            Assert.fail("Fail to get connector file name: " + output.stderr);
        }
        log.info("Connector archive identified: " + output.stdout);


        List<String> InstallCommands = List.of(
                "cd /data1/downloadLocation && tar -xvf " +  output.stdout,
                "cd /opt && rm -f *.sh",
                "cd /opt && rm -f *.gz",
                "cd /opt && rm -rf fluent/",
                "cd /data1/downloadLocation && cp -r * /opt/."
        );

        sshcon.executeCommands(InstallCommands, 90000);
        log.info("Executed installation commands");

        output = sshcon.executeSingleCommand("cd /opt && ls -ltr *.sh | tail -1 |awk '{print $9}'", 15000);
        if (output.exitCode != 0) {
            Assert.fail("Fail to start fluentd service: " + output.stderr);
        }

        output = sshcon.executeSingleCommand("cd /opt && ./" +  output.stdout.replace("\r", "").replace("\n", "").trim() + " -u root -g root", 60000);
        if (output.exitCode != 0) {
            Assert.fail("Fail to extract connector file: " + output.stderr);
        }
        commonwait.waitForSometime(10000);
        log.info("Execute Fluentd service start command");

        List<String> serviceCommands = List.of(
                "systemctl start fluentd.service"
        );
        sshcon.executeCommands(serviceCommands, 15000);

        log.info("Fluentd service started");
        sshcon.closeChannel();
        sshcon.disconnect();
        log.info("Connector deployment completed and fluentd started");
        return true;
    }

    @Test(description = "LogAnalytics: Verify Linux connector for log push", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12449", Products = {Product.ADE_LogAnalytics})
    public void DRSQA_12449_Verify_Linux_connector_for_log_push(ITestContext context) throws JSchException, IOException, InterruptedException {
        HMEvents hm_events = new HMEvents();
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage(); 
        jwtToken = commonmethod.refreshJwtToken(null);
        tenantId = hm_events.getTanantId(jwtToken);
        log.info("Starting Linux connector test, tenantId=" + tenantId);
        CleanupExisingConnectorInstallation();
        commonwait.waitForSometime(60000);
        logAnalyticsPage.auth_type = 1;
        logAnalyticsPage.getloganalyticscookies();
        loganalystics.setrestassuredcookies(LogAnalyticsPage.seleniumCookies);
        String downloadFilepath = logdownloadconnector.getDownloadBaseDir() + File.separator +  "downloadLocation";
        deleteCollectionRuleIfExists();
        log.info("Delete Parsing rule");
        deleteParsingRuleIfExists();
        String ruleID = createParsingRule();
        log.info("Collection Rule");
        commonwait.waitForSometime(15000); // wait for Policy rule
        createCollectionRule(ruleID);
        setDisablePopupGC(true);
        logdownloadconnector.deleteDirRecursive(new File(downloadFilepath));
        Files.createDirectory(Paths.get(downloadFilepath));
        logdownloadconnector.setloganalytics_url("/loganalytics/#");
        setChromeDownloadFilepath(downloadFilepath);


        driver = this.launchWeb(app, app);
        login(configProp.get("ADE_USERNAME"),configProp.get("ADE_PASSWORD"),app);
        setDriver(driver);
        logdownloadconnector.SetLogAnalyticsDownloadPage();


        String randomString = RandomStringUtils.insecure()
                .nextAlphabetic(6)
                .toLowerCase();
        try {
            int fileCount = new File(downloadFilepath).listFiles() != null ? new File(downloadFilepath).listFiles().length + 1 : 1;
            String loganalytics_url = logdownloadconnector.getloganalytics_url();
            logdownloadconnector.waitforLogAnalyticsPlatformPageLoad();
            log.info("Platform Page Loaded");
            driver.get(loganalytics_url);
            logdownloadconnector.waitforLogAnalyticsMainPageLoad();
            logdownloadconnector.downloadConnector(randomString, ConnectorPolicyName,	"Linux Connector (RHEL9)", fileCount);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Assert.fail("Fail to download Linux connector");
        }
        log.info("Download Complete");

        boolean status = pushDeployLinuxConnector();
        if (!status){
            Assert.fail("Fail to push and deploy Linux connector");
        }
        logAnalyticsPage.auth_type = 1;
        logAnalyticsPage.getloganalyticscookies();
        loganalystics.setrestassuredcookies(logAnalyticsPage.seleniumCookies);
        status = WaitForConnectorStatus(ConnectorPolicyName + "-" + randomString);
        commonwait.waitForSometime(10000);
        pushLogFile();
        commonwait.waitForSometime(60000);

        logAnalyticsPage.auth_type = 0;
        logAnalyticsPage.generateJWTToken();
        jwtToken = logAnalyticsPage.auth_token;
        status = validateLinuxConnector();
        if (!status){
            Assert.fail("Fail to validate Linux connector");
        }
        log.info("Linux connector validation completed successfully");

    }

    private Map<String, Object> loadSshConfig() {
        try {
            List<Map<String, Object>> rows = excelReaderForML.getTestDataFromExcel(SSH_CONFIG_TC, SSH_CONFIG_SHEET);
            if (rows.isEmpty()) {
                throw new IllegalStateException("No SSH config found in sheet '" + SSH_CONFIG_SHEET + "' for TC " + SSH_CONFIG_TC);
            }
            return rows.get(0);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read SSH config from Excel sheet '" + SSH_CONFIG_SHEET + "'", e);
        }
    }

    private String requireSshValue(String key) {
        Object val = sshConfig.get(key);
        if (val == null || val.toString().trim().isEmpty()) {
            throw new IllegalStateException("Missing SSH config '" + key + "' in sheet '" + SSH_CONFIG_SHEET + "'");
        }
        return val.toString();
    }

}

