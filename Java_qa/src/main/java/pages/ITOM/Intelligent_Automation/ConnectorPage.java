package pages.ITOM.Intelligent_Automation;

import framework.BasePage;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.PageFactory;
import util.CommonWaits;

import java.io.*;

public class ConnectorPage extends BasePage {
    private static Logger logger = Logger.getLogger(ConnectorPage.class);

    public String currentDir = System.getProperty("user.dir");
    CommonWaits commonWaits;

    public ConnectorPage(){
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }
    public void runCMD(String CommandToBeEnter) {
        try {
            String command = System.getProperty("user.dir") + File.separator + IAConnectorConstant.REMOTE_RESTAPI_PLUGIN + File.separator;
            String commandPath = command + CommandToBeEnter;
            Process child = Runtime.getRuntime().exec(commandPath);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
            String s;
            System.out.println("Standard CMD Run Output: commandPath " + commandPath);
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            OutputStream out = child.getOutputStream();
            out.write("cd C:/ /r/n".getBytes());
            out.flush();
            out.write("dir /r/n".getBytes());
            out.close();
        } catch (IOException ignored) {
        }

    }
}
