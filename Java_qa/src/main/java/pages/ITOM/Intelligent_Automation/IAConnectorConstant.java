package pages.ITOM.Intelligent_Automation;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintStream;

public class IAConnectorConstant {
    public static String PULGIN_PATH;
    public static String REMOTE_RESTAPI_PLUGIN;
    public static String REMOTE_RESTAPI_PLUGIN_API;
    public static String CREDS_PATH;
    public static String CONFIGJSON_PATH;
    public static String PULGIN_PATH_CRDOWNLOAD;
    public static String INPUT_FILE;
    public static String BATCH_FILE;
    public static String RUNCOMMAND;
    public static String INSTALLCOMMAND;
    public static String INSTALLSTARTCMD;
    public static String INSTALLSTOPCMD;
    public static String UNINSTALLCMD;
    public static String CREDENTIALDBPATH;
    public static String SOURCEDBPATH;
    public static String SOURCEDBPATHFORBHOMPSR;
    public static String SOURCEDBPATHFOREVENTFORWARDING;
    public static String ONPREM_CERT_PATH;
    public static String PULGIN_PATH_API;
    public static final String Failed_Status="FAILED";
    public static final String Service_Name="remote-restapi-plugin";
    public static String Running_Status="RUNNING";
    public static String pluginName="SolQATestPlugin";
    public static String USERDIR = "user.dir";

    static {
        PULGIN_PATH = System.getProperty(USERDIR) + File.separator + "target" + File.separator + "remote-restapi-plugin.zip";
        REMOTE_RESTAPI_PLUGIN = "remote-restapi-plugin";
        REMOTE_RESTAPI_PLUGIN_API = "unzip-remote-restapi-plugin";
        CREDS_PATH = System.getProperty(USERDIR) + File.separator + "target" + File.separator + "creds.json";
        CONFIGJSON_PATH = File.separator + "remote-restapi-plugin" + File.separator + "config" + File.separator + "creds.json";
        PULGIN_PATH_CRDOWNLOAD = PULGIN_PATH + ".crdownload";
        INPUT_FILE = "resources" + File.separator + "ITOM" + File.separator + "IA" + File.separator + "ia" + File.separator + "inputjson" + File.separator + "eventjs.json";
        BATCH_FILE = "resources" + File.separator + "ITOM" + File.separator + "IA" + File.separator + "batchFile";
        RUNCOMMAND = "run.bat";
        INSTALLCOMMAND = "install.bat";
        INSTALLSTARTCMD = "install.bat start";
        INSTALLSTOPCMD = "install.bat stop";
        UNINSTALLCMD = "install.bat uninstall";
        CREDENTIALDBPATH = "C:" + File.separator + "ProgramData" + File.separator + "BMC" + File.separator + "ia_remote_plugins";
        SOURCEDBPATH = "resources" + File.separator + "ITOM" + File.separator + "IA" + File.separator + "ia_remote_plugins";
        SOURCEDBPATHFORBHOMPSR = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testdata" + File.separator + "BHOMPSR" + File.separator + "ia_remote_plugins";
        SOURCEDBPATHFOREVENTFORWARDING = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testdata" + File.separator + "EventForwarding" + File.separator + "ia_remote_plugins";
        ONPREM_CERT_PATH = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testdata" + File.separator + "Certificate" + File.separator + "tso1_tso2_tssa.pem";
        PULGIN_PATH_API = System.getProperty(USERDIR) + File.separator + "target" + File.separator + "remote-restapi-plugin.zip";
    }
}
