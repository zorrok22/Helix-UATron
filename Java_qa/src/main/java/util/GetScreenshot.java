package util;
/** *********************************************************************
Purpose:        This class will take screenshots and copy it to appropriate folders
File Name:      GetScreenshot.Java
Suite Name:     Framework File
Module Name:    NA
@author         akarhadk
@version        1.0
@since          2025-04-18
Comments:       
 ********************************************************************** */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import framework.Launcher;

public class GetScreenshot extends Launcher {

    private static Properties configProp;
    private static final String USERDIR = System.getProperty("user.dir");
    static WebDriver driver;

    public GetScreenshot(WebDriver driver) {
        this.driver = getDriver();
    }

    static {
        try {
            configProp = new Properties();
            FileInputStream fis = new FileInputStream(USERDIR + File.separator + "properties.config");
            configProp.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String capture(String screenShotName, String app) throws IOException {
        String encodedBase64 = null;
        FileInputStream fileInputStream = null;
        TakesScreenshot ts = (TakesScreenshot) getDriver();
        File source = ts.getScreenshotAs(OutputType.FILE);
        String dest = getDirPathByappName(app) + screenShotName + ".png";
        File destination = new File(dest);
        FileUtils.copyFile(source, destination);

        try {
            fileInputStream = new FileInputStream(destination);
            byte[] bytes = new byte[(int) destination.length()];
            fileInputStream.read(bytes);
            encodedBase64 = Base64.getEncoder().encodeToString(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IOException("Screenshot file not found after capture: " + dest, e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return encodedBase64;
    }

    public static String getDirPathByappName(String appName) {
        String dirPath = USERDIR + File.separator + "Screenshots" + File.separator;
        if (configProp != null && configProp.getProperty("appName") != null) {
            dirPath += appName + File.separator;
        }
        return dirPath;
    }

}