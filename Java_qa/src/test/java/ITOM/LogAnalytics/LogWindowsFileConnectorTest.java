package ITOM.LogAnalytics;

import com.jcraft.jsch.JSchException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
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
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import framework.BasePage;
import org.openqa.selenium.Keys;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import framework.Launcher;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.HashMap;
import java.time.Duration;
import java.time.Instant;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Java translation of Robot "Windows file Connector" (logcollection.robot).
 * Focuses on REST-based creation/cleanup of parsing & collection rules and connector setup.
 * Log push/validation steps are left as TODO where shell/SSH automation is required.
 */
public class LogWindowsFileConnectorTest extends Launcher{

    private static final SolqaLogger log = new SolqaLogger(LogWindowsFileConnectorTest.class.getName());
    private static final String BASE_URL = configProp.get("ADE_PLATFORM_URL");
    private static final String PARSING_RULE_NAME = "automation-windows-rule";
    private static final String COLLECTION_RULE_NAME = "automation-windows-collection";
    private static final String CONNECTOR_NAME = "automation-windows-connector";
    private static final String TAG_NAME = "bmc-tag-windows-collection";
    private static final String LOG_PATH = "/opt/error*";
    private static final String TEMPLATE_FILTER = "Collect Logs from File";
    private static final CommonWaits commonwait = new CommonWaits();
    private String app = SolQAConstants.appITOM;
    private LogAnalystics loganalystics = new LogAnalystics();
    private String templateId = null;
    private String tenantId = null;
    private String hostName = "host-01";
    private String jwtToken = null;
    private String ConnectorPolicyName = "automation-Windows-connector";
    private static final String SSH_CONFIG_SHEET = "sshconfig";
    private static final int WINDOWS_SSH_CONFIG_TC = 13335;
    private static final int SCRIPT_SSH_CONFIG_TC = 13330;
    private String WindowsMachineName = "";
    private String WindowsMachineName_uname = "";
    private String WindowsMachineName_pwd = "";
    private String ScriptsMachineName = "";
    private String ScriptsMachineName_uname = "";
    private String ScriptsMachineName_pwd = "";
    LogAnalyticsDownloadPage logdownloadconnector = new LogAnalyticsDownloadPage();
    private CommonMethods commonmethods = new CommonMethods();
    private static ExcelReaderForMLSituation excelReaderForML = new ExcelReaderForMLSituation();


    private WebDriver driver;

    private void CleanupExisingConnectorInstallation() throws JSchException, IOException {
        SSHConnector.CommandResult output;
        SSHConnector sshcon = new SSHConnector();
        sshcon.connect(WindowsMachineName, WindowsMachineName_uname, WindowsMachineName_pwd, 22);
        sshcon.openChannel();

        List<String> UnInstallCommands = List.of(
                "cd C:\\wincon\\downloads\\BMC-DevTools\\ && uninstall-connector.bat"
        );

        //sshcon.executeCommands(UnInstallCommands, 90000);

        output = sshcon.executeSingleCommand("sc queryex type=service state=all | find /i \"SERVICE_NAME: automation\"", 15000);
        if ((output.exitCode == 0) && (!output.stdout.isEmpty())) {
            String[] parts = output.stdout.split("\\r\\n");
            for (String part : parts) {
                String[] serviceParts = part.split("\\s+");
                if (serviceParts.length < 1) {
                    continue;
                }
                sshcon.executeSingleCommand("sc stop " + serviceParts[1], 15000);
                commonwait.waitForSometime(10000);
                sshcon.executeSingleCommand("sc delete " + serviceParts[1], 15000);
                commonwait.waitForSometime(10000);
            }
        }

        sshcon.executeSingleCommand("cd C:\\wincon && del /F /Q *", 15000);
        output = sshcon.executeSingleCommand("cd C:\\wincon && dir /b *", 15000);
        if ((output.exitCode == 0) && (!output.stdout.isEmpty())) {
            String[] lines = output.stdout.split("\\R");
            for (String line : lines) {
                sshcon.executeSingleCommand("rmdir /s /q C:\\wincon\\" + line, 15000);
                commonwait.waitForSometime(10000);
            }
        }

        sshcon.closeChannel();
        sshcon.disconnect();
    }




    private void deleteParsingRuleIfExists() {
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
            }
        }
    }

    private void deleteCollectionRuleIfExists() {
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
        Map<String,String> usergroup = loganalystics.getUserGroupDetail();

        boolean status = loganalystics.CreateCollectionRule(COLLECTION_RULE_NAME,templateId,tenantId,hostName,usergroup.get("Administrators"),"Administrators",
                ruleID,PARSING_RULE_NAME, LOG_PATH,TAG_NAME);
        if (!status){
            Assert.fail("Fail to create Collection Rule");
        }

        return true;
    }

    private boolean WaitForConnectorStatus(String connectorName) {
        Map<String,String> usergroup = loganalystics.getUserGroupDetail();
        for(int i =0; i < 40; i++) {
            JSONObject connector = loganalystics.getConnector(connectorName);
            if (connector.getJSONObject("response").getJSONArray("data").length() == 0) {
                commonwait.waitForSometime(10000);
                continue;
            }
            String status = connector.getJSONObject("response").getJSONArray("data").getJSONObject(0).getString("status");
            if (status.equalsIgnoreCase("active")) {
                return true;
            }
            commonwait.waitForSometime(10000);
        }
        return false;
    }


    private boolean validateWindowsConnector() throws JSchException, IOException, InterruptedException {
        HMEvents hm_events = new HMEvents();
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        for(int i = 0; i < 10; i++) {
            long currenttime = hm_events.getBhomServerTime(jwtToken) / 1000;
            long starttime = currenttime - 43200;
            String currentTime = Instant.ofEpochSecond(currenttime).toString();
            String startTime = Instant.ofEpochSecond(starttime).toString();
            String query = "{\"bool\":{\"must\":[],\"filter\":[{\"match_all\":{}},{\"match_phrase\":{\"bmc_tags\":\"bmc-tag-Windows-collection\"}},{\"range\":{\"@timestamp\":{\"gte\":\"" + startTime + "\",\"lte\":\"" + currentTime + "\",\"format\":\"strict_date_optional_time\"}}}],\"should\":[],\"must_not\":[]}}";
            JSONObject logAn = logAnalyticsPage.logRetrieval(query, "15");
            Assert.assertNotNull(logAn);
            int totalrecord = logAn.getJSONArray("responses").getJSONObject(0).getJSONObject("_shards").getInt("total");
            if (totalrecord < 1) {
                commonwait.waitForSometime(10000);
                continue;
            }
            return true;
        }
        return false;
    }

    private void pushLogFile() throws JSchException, IOException {

        SSHConnector sshcon = new SSHConnector();
        sshcon.connect(ScriptsMachineName, ScriptsMachineName_uname, ScriptsMachineName_pwd, 22);
        sshcon.openChannel();
        SSHConnector.CommandResult output = sshcon.executeSingleCommand("cd /data1 && sh add_error_line.sh winerror_n.log", 15000);
        if (output.exitCode != 0) {
            Assert.fail("Fail to get connector file name: " + output.stderr);
        }
        if (output.stdout.isEmpty()) {
            Assert.fail("Fail to get connector file name: " + output.stderr);
        }
        sshcon.transferFile("/data1/winerror_n.log", "C:\\temp\\winerror_n.log", false);
        sshcon.closeChannel();
        sshcon.disconnect();

        sshcon.connect(WindowsMachineName, WindowsMachineName_uname, WindowsMachineName_pwd, 22);
        sshcon.openChannel();
        sshcon.transferFile("C:\\temp\\winerror_n.log","C:/wincon/winerror_n.log", true);
        sshcon.closeChannel();
        sshcon.disconnect();
        return;
    }

    private boolean pushDeployWindowsConnector() throws JSchException, IOException {
        String tmpDir = logdownloadconnector.getDownloadBaseDir();
        String downloadFilepath = logdownloadconnector.getDownloadBaseDir() + File.separator +  "downloadLocation";

        SSHConnector sshcon = new SSHConnector();
        sshcon.connect(WindowsMachineName, WindowsMachineName_uname, WindowsMachineName_pwd, 22);
        sshcon.openChannel();
        SSHConnector.CommandResult output = sshcon.executeSingleCommand("mkdir -p C:\\wincon\\downloads", 15000);
        File dl = new File(downloadFilepath);
        File[] files = dl.listFiles();
        if (files != null) {

            for (File f : files) {
                if (f.isFile()) {
                    String remotePath = "C:/wincon/downloads/" + f.getName();
                    sshcon.transferFile(f.getAbsolutePath(), remotePath);
                }
            }
        }

        output = sshcon.executeSingleCommand("cd C:\\wincon\\downloads && dir /b |find /i \"zip\"", 15000);
        if (output.exitCode != 0) {
            Assert.fail("Fail to get connector file name: " + output.stderr);
        }
        if (output.stdout.isEmpty()) {
            Assert.fail("Fail to get connector file name: " + output.stderr);
        }


        List<String> UnzipCommands = List.of(
                "cd C:\\wincon\\downloads && 7z.exe x " +  output.stdout +  " -y > NUL 2>&1"
        );

        sshcon.executeCommands(UnzipCommands, 90000);

        output = sshcon.executeSingleCommand("cd C:\\wincon\\downloads && dir /b |find /i \"bat\"", 15000);
        if (output.exitCode != 0) {
            Assert.fail("Fail to start fluentd service: " + output.stderr);
        }

        List<String> InstallCommands = List.of(
                "cd C:\\wincon\\downloads  && echo N | " +  output.stdout.split("/R")[0].trim()

        );
        sshcon.executeCommands(InstallCommands, 90000);
        sshcon.closeChannel();
        sshcon.disconnect();
        return true;
    }

    @Test(description = "LogAnalytics: Verify Windows connector for log push", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12448", Products = {Product.ADE_LogAnalytics})
    public void DRSQA_12448_Verify_Windows_connector_for_log_push(ITestContext context) throws JSchException, IOException, InterruptedException {
        HMEvents hm_events = new HMEvents();
        LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
        logAnalyticsPage.generateJWTToken();
        Map<String, Object> sshConfig = loadSshConfig(WINDOWS_SSH_CONFIG_TC);
        WindowsMachineName = requireSshValue(sshConfig, "ssh_host");
        WindowsMachineName_uname = requireSshValue(sshConfig, "ssh_user");
        WindowsMachineName_pwd = requireSshValue(sshConfig, "ssh_password");
        sshConfig = loadSshConfig(SCRIPT_SSH_CONFIG_TC);
        ScriptsMachineName = requireSshValue(sshConfig, "ssh_host");
        ScriptsMachineName_uname = requireSshValue(sshConfig, "ssh_user");
        ScriptsMachineName_pwd = requireSshValue(sshConfig, "ssh_password");

        jwtToken = logAnalyticsPage.auth_token;
        tenantId = hm_events.getTanantId(jwtToken);
        CleanupExisingConnectorInstallation();
        commonwait.waitForSometime(60000);
        logAnalyticsPage.auth_type = 1;
        logAnalyticsPage.getloganalyticscookies();
        loganalystics.setrestassuredcookies(logAnalyticsPage.seleniumCookies);
        String downloadFilepath = logdownloadconnector.getDownloadBaseDir() + File.separator +  "downloadLocation";
        setDisablePopupGC(true);
        logdownloadconnector.deleteDirRecursive(new File(downloadFilepath));
        Files.createDirectory(Paths.get(downloadFilepath));
        logdownloadconnector.setloganalytics_url("/loganalytics/#");
        setChromeDownloadFilepath(downloadFilepath);



        deleteCollectionRuleIfExists();
        log.info("Delete Parsing rule");
        deleteParsingRuleIfExists();
        String ruleID = createParsingRule();
        log.info("Collection Rule");
        commonwait.waitForSometime(15000); // wait for Policy rule
        createCollectionRule(ruleID);


        // Set OS-specific temp and prepare download location

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
            logdownloadconnector.downloadConnector(randomString, ConnectorPolicyName,	"Windows Connector", fileCount);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Assert.fail("Fail to download Windows connector");
        }
        log.info("Download Complete");

        boolean status = pushDeployWindowsConnector();
        if (!status){
            Assert.fail("Fail to push and deploy Windows connector zip and bat file");
        }
        logAnalyticsPage.auth_type = 1;
        logAnalyticsPage.getloganalyticscookies();
        loganalystics.setrestassuredcookies(logAnalyticsPage.seleniumCookies);
        status = WaitForConnectorStatus(ConnectorPolicyName + "-" + randomString);
        if (!status){
            Assert.fail("Windows connector not in connected state");
        }

        pushLogFile();
        commonwait.waitForSometime(60000);

        String jwtToken = commonmethods.refreshJwtToken(null);
        status = validateWindowsConnector();
        if (!status){
            Assert.fail("No Log push using Windows connector");
        }

    }

    private Map<String, Object> loadSshConfig(int SSH_CONFIG_TC) {
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

    private String requireSshValue(Map<String, Object> sshConfig, String key) {
        Object val = sshConfig.get(key);
        if (val == null || val.toString().trim().isEmpty()) {
            throw new IllegalStateException("Missing SSH config '" + key + "' in sheet '" + SSH_CONFIG_SHEET + "'");
        }
        return val.toString();
    }

}
