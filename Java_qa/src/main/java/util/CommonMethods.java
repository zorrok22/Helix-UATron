package util;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

import pages.ITOM.LogAnalytics.LogAnalyticsPage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import framework.Launcher;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class CommonMethods extends Launcher {

    CommonWaits commonWaits=new CommonWaits();
    static SolqaLogger log = new SolqaLogger(CommonMethods.class.getName());
    Actions actions;
    private long jwtTokenExpiryTime = 0;

    /** BHAC Import Scan modal: hidden file input on {@code adapt-rx-uploader} (same row as Attach button). */
    private static final String ADAPT_RX_UPLOADER_FILE_INPUT_XPATH =
            "//button[contains(@class,'adapt-rx-uploader-attach-btn')]/ancestor::adapt-rx-uploader[1]//input[@type='file']";

    public static File createTextFile(String fileName) {
        try {
            File textFile = new File(fileName);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textFile), StandardCharsets.UTF_8))) {
                writer.write("something");
            }
            return textFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void attachFile(File file, WebElement inpAttachFile) {
        if (file != null) {
            //            log.info("Attaching file: '" + file.getAbsolutePath() + "'");
            inpAttachFile.sendKeys(file.getAbsolutePath());
        } else {
            //            log.warn("Can not attach file=null");
        }
    }

    public static String readConfig(String ConfigParameter) {
		Properties props = new Properties();
		String fileName = "properties.config";
		String value = null;
		try {
			props.load(new FileInputStream(fileName));
			value = props.getProperty(ConfigParameter, String.valueOf(ConfigParameter));

			// Allow overrides via -Dkey=value and environment variables to avoid hardcoding secrets in files.
			String sysProp = System.getProperty(ConfigParameter);
			if (sysProp != null && !sysProp.trim().isEmpty()) {
				return sysProp.trim();
			}
			String envDirect = System.getenv(ConfigParameter);
			if (envDirect != null && !envDirect.trim().isEmpty()) {
				return envDirect.trim();
			}
			String envUpper = System.getenv(ConfigParameter.toUpperCase());
			if (envUpper != null && !envUpper.trim().isEmpty()) {
				return envUpper.trim();
			}
			String envSnake = System.getenv(ConfigParameter.replace('.', '_').toUpperCase());
			if (envSnake != null && !envSnake.trim().isEmpty()) {
				return envSnake.trim();
			}

			// Common aliases for credentials.
			if ("adminUserName".equalsIgnoreCase(ConfigParameter)) {
				String alias = System.getenv("ADMIN_USERNAME");
				if (alias != null && !alias.trim().isEmpty()) return alias.trim();
			}
			if ("adminPassword".equalsIgnoreCase(ConfigParameter)) {
				String alias = System.getenv("ADMIN_PASSWORD");
				if (alias != null && !alias.trim().isEmpty()) return alias.trim();
			}

			// Fail fast if placeholder tokens were not replaced (prevents attempting login with __adminUserName__).
			if (value != null) {
				String v = value.trim();
				if (v.startsWith("__") && v.endsWith("__")) {
					throw new IllegalStateException(
							"Config '" + ConfigParameter + "' is still a placeholder (" + v + "). "
									+ "Update properties.config or provide an override via -D" + ConfigParameter + "=... "
									+ "or environment variable " + ConfigParameter.toUpperCase() + ".");
				}
			}
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to read " + fileName + " from working directory. " + ioe.getMessage(), ioe);
		}
		return value;
	}

	public String randomString(Integer len) {
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(new Random().nextInt(AB.length())));
		return sb.toString();
	}

    public static String getHelixGPTBotIDFromtxtFile() {
		String path = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appHelixGPT;
		Path filePath = Paths.get(path + File.separator + "HelixGPTBot_id.txt");
		String HelixGPTBotIdFromFile = null;
		log.info("Attempting to read HelixGPT Bot ID from file: " + filePath.toString());
		try {
			HelixGPTBotIdFromFile = new String(Files.readAllBytes(filePath)).trim();
			log.info("Successfully read HelixGPT Bot ID from file.");
		} catch (IOException e) {
			log.error("Error reading HelixGPT Bot ID from file: " + filePath.toString(), e);
			e.printStackTrace();
		}
		log.info("Fetched Bot ID from file: " + HelixGPTBotIdFromFile);
		return HelixGPTBotIdFromFile;
	}

    public static boolean createFolder(String foldernamepath) {
        boolean bvalue = true;
        File folderpath = new File(foldernamepath);
        if (folderpath.exists()) {
            log.info("**Folder Path " + foldernamepath + " is already present**");
            bvalue = true;
        } else {
            bvalue = folderpath.mkdirs();
            log.info("**Folder Path " + foldernamepath + " created**");
        }

        return bvalue;
    }

    public static void unzipplugin(String sourceFolder, String destinationFolder) {
        String password = "password";

        try {
            log.info(" ****** Unzip File is started at  " + sourceFolder + " from Source Location *******");
            try (ZipFile zipFile = new ZipFile(sourceFolder)) { // zip4j plugin
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password.toCharArray());
                }
                zipFile.extractAll(destinationFolder);
            }
            log.info(" ****** Unzip File is done " + destinationFolder + " at Destination Location *******");
        } catch (Exception var4) {
            log.info(" ****** Error Occured while Unzip****");
            var4.printStackTrace();
        }

    }

    public static void killProcess(String process) {
        try {
            String killProcess = "taskkill /F /IM " + process + " /T";
            Runtime.getRuntime().exec(killProcess);
        } catch (IOException var2) {
            log.error("No Process to kill:" + var2.getMessage());
            var2.printStackTrace();
        }

        log.info(" Kill open " + process + " is Completed");
    }

    public static boolean copyFileFromSourceToDestination(String source, String destination) {
        log.info("Copying file from Source to Destination Starts!");
        boolean bReturn = false;
        log.info("Source : " + source + "\nDestination : " + destination);

        try {
            FileUtils.copyFile(new File(source), new File(destination));
            bReturn = true;
        } catch (Exception e) {
            log.error("Exception in copying directory from Source: "+ source + " to Destination: " + destination);
            log.info(e.getMessage());
        }

        return bReturn;
    }

    public void typeKeysWithEnter(WebElement element, String keys) {
        log.info("Typing keys with Enter: " + keys);
        commonWaits.waitForElementVisible(element);
        element.clear();
        element.sendKeys(keys);
        element.sendKeys(Keys.ENTER);
        commonWaits.waitForExecutionComplete();
    }

    public void pressEscapeWithActions() {
        log.info("Pressing Escape key using Actions.");
        Actions actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).perform();
        commonWaits.waitForExecutionComplete();
    }

    /**
     * Finds the most common substring pattern that appears in more than 1 item from the list
     * Useful for identifying common values or patterns in filtered data
     * 
     * @param values List of string values to analyze
     * @param minPatternLength Minimum length of the pattern to search for (default: 3 characters)
     * @return The most common pattern found, or empty string if no pattern appears in more than 1 item
     */
    public String findCommonPatternInList(List<String> values, int minPatternLength) {
        log.info("Finding common pattern in list of " + values.size() + " items (min length: " + minPatternLength + ")");
        
        if (values == null || values.size() < 2) {
            log.warn("List has less than 2 items. Cannot find common pattern.");
            return "";
        }
        
        // Remove empty and null values
        List<String> validValues = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                validValues.add(value.trim());
            }
        }
        
        if (validValues.size() < 2) {
            log.warn("List has less than 2 valid items after filtering. Cannot find common pattern.");
            return "";
        }
        
        // Check for exact matches first (most common case)
        java.util.Map<String, Integer> exactMatches = new java.util.HashMap<>();
        for (String value : validValues) {
            exactMatches.put(value, exactMatches.getOrDefault(value, 0) + 1);
        }
        
        for (java.util.Map.Entry<String, Integer> entry : exactMatches.entrySet()) {
            if (entry.getValue() > 1) {
                log.info("Found exact match pattern: '" + entry.getKey() + "' (appears " + entry.getValue() + " times)");
                return entry.getKey();
            }
        }
        
        // If no exact matches, look for common substrings
        java.util.Map<String, Integer> patternCount = new java.util.HashMap<>();
        
        for (int i = 0; i < validValues.size(); i++) {
            String value1 = validValues.get(i);
            
            for (int j = i + 1; j < validValues.size(); j++) {
                String value2 = validValues.get(j);
                
                // Find common substrings between these two values
                String commonSubstring = findLongestCommonSubstring(value1, value2, minPatternLength);
                
                if (!commonSubstring.isEmpty()) {
                    patternCount.put(commonSubstring, patternCount.getOrDefault(commonSubstring, 0) + 1);
                }
            }
        }
        
        // Find the most common pattern
        String mostCommonPattern = "";
        int maxCount = 0;
        
        for (java.util.Map.Entry<String, Integer> entry : patternCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommonPattern = entry.getKey();
            }
        }
        
        if (!mostCommonPattern.isEmpty()) {
            log.info("Found common substring pattern: '" + mostCommonPattern + "' (found in " + (maxCount + 1) + " pairs)");
            return mostCommonPattern;
        } else {
            log.info("No common pattern found in the list");
            return "";
        }
    }
    
    /**
     * Helper method to find the longest common substring between two strings
     * 
     * @param str1 First string
     * @param str2 Second string
     * @param minLength Minimum length of substring to consider
     * @return Longest common substring, or empty string if none found
     */
    private String findLongestCommonSubstring(String str1, String str2, int minLength) {
        if (str1 == null || str2 == null || str1.isEmpty() || str2.isEmpty()) {
            return "";
        }
        
        String longestSubstring = "";
        
        // Convert to lowercase for case-insensitive comparison
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        
        for (int i = 0; i < str1.length(); i++) {
            for (int j = i + minLength; j <= str1.length(); j++) {
                String substring = str1.substring(i, j);
                if (str2.contains(substring) && substring.length() > longestSubstring.length()) {
                    longestSubstring = substring;
                }
            }
        }
        
        return longestSubstring;
    }

    
    /**
	 * Common method to check and set toggle status.
	 * Enables the toggle if it is not already enabled.
	 * Accepts multiple locator formats: String (XPath), By, or WebElement.
	 * 
	 * @param inputLocator Locator for the toggle input element - can be String (XPath), By, or WebElement
	 * @param clickableLocator Locator for the clickable element - can be String (XPath), By, or WebElement
	 * @param appName Application name for spinner wait (e.g., SolQAConstants.appIS)
	 * @param toggleName Name of the toggle for logging purposes
	 */
	public void enableToggleIfNotEnabled(Object inputLocator, Object clickableLocator, String appName, String toggleName) {
		log.info("Checking and enabling '" + toggleName + "' toggle if not enabled");
		commonWaits.waitForAllSpinnersToHide(appName);
		commonWaits.waitForPageFullyLoaded(commonWaits.defaultTimeout, 1);

		WebElement input = resolveToWebElement(inputLocator);
		WebElement clickableElement = resolveToWebElement(clickableLocator);

		commonWaits.waitForElementClickable(clickableElement);

		String ariaChecked = input.getAttribute("aria-checked");
		log.info("Toggle '" + toggleName + "' aria-checked status: " + ariaChecked);

		if (!"true".equalsIgnoreCase(ariaChecked)) {
			log.info("Toggle '" + toggleName + "' is OFF. Enabling it now.");
			commonWaits.clickWithoutInterception(clickableElement);
			commonWaits.waitForAllSpinnersToHide(appName);
			log.info("Toggle '" + toggleName + "' enabled successfully");
		} else {
			log.info("Toggle '" + toggleName + "' already enabled. Skipping.");
		}
	}

	/**
	 * Resolves different locator types to WebElement.
	 * Supports String (treated as XPath), By, and WebElement.
	 * 
	 * @param locator The locator object - can be String (XPath), By, or WebElement
	 * @return WebElement resolved from the locator
	 * @throws IllegalArgumentException if locator type is not supported
	 */
	private WebElement resolveToWebElement(Object locator) {
		if (locator instanceof WebElement) {
			return (WebElement) locator;
		} else if (locator instanceof By) {
			return getDriver().findElement((By) locator);
		} else if (locator instanceof String) {
			return getDriver().findElement(By.xpath((String) locator));
		} else {
			throw new IllegalArgumentException("Unsupported locator type: " + locator.getClass().getName() + 
				". Supported types are: String (XPath), By, WebElement");
		}
	}
  /**
     * Refreshes JWT token if expired.
  */
  public String refreshJwtToken(String jwtToken) {
      long currentTimestamp = System.currentTimeMillis() / 1000;
      if ((currentTimestamp - jwtTokenExpiryTime) > 90 || jwtToken == null) {
          LogAnalyticsPage logAnalyticsPage = new LogAnalyticsPage();
          logAnalyticsPage.generateJWTToken();
          jwtTokenExpiryTime = currentTimestamp;
          jwtToken = logAnalyticsPage.auth_token;
          log.info("Generated new JWT token: " + jwtToken);
      }
      return jwtToken;
  }
  
	/**
	 * Opens a link in a new tab using JavaScript window.open
	 * Accepts multiple locator types: String (URL or XPath), By, or WebElement
	 * 
	 * @param locator The locator - can be String (URL or XPath), By, or WebElement
	 * @param switchToNewTab If true, switches focus to the new tab after opening
	 */
	public CommonMethods openLinkInNewTab(Object locator, boolean switchToNewTab) {
		String url = resolveToUrl(locator);
		
		if (url == null || url.isEmpty()) {
			throw new IllegalArgumentException("Could not extract URL from the provided locator");
		}
		
		int existingWindowCount = getDriver().getWindowHandles().size();
		log.info("Opening URL in new tab: " + url + " | Current window count: " + existingWindowCount);
		
		// Open URL in new tab using JavaScript
		org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) getDriver();
		js.executeScript("window.open('" + url + "', '_blank');");
		
		// Wait for new tab to open
		commonWaits.waitForNumberWindowsToBe(existingWindowCount + 1);
		log.info("New tab opened. Total windows: " + getDriver().getWindowHandles().size());
		
		if (switchToNewTab) {
			commonWaits.switchToLastWindow();
		}
		return this;
	}
	
	/**
	 * Opens a link in a new tab and switches to it by default
	 */
	public CommonMethods openLinkInNewTab(Object locator) {
		openLinkInNewTab(locator, true);
		return this;
	}
	
	/**
	 * Resolves locator to URL string
	 */
	private String resolveToUrl(Object locator) {
		if (locator instanceof String) {
			String strLocator = (String) locator;
			if (strLocator.startsWith("http") || strLocator.startsWith("https") || strLocator.startsWith("#") || strLocator.startsWith("/")) {
				return strLocator;
			} else {
				return getDriver().findElement(By.xpath(strLocator)).getAttribute("href");
			}
		} else if (locator instanceof By) {
			return getDriver().findElement((By) locator).getAttribute("href");
		} else if (locator instanceof WebElement) {
			return ((WebElement) locator).getAttribute("href");
		} else {
			throw new IllegalArgumentException("Unsupported locator type: " + locator.getClass().getName());
		}
	}

    public CommonMethods pressEnterKey() {
        actions = new Actions(getDriver());
        actions.sendKeys(Keys.ENTER).perform();
        commonWaits.waitForExecutionComplete();
        return this;
    }

    public CommonMethods openNewTab() {
        getDriver().switchTo().newWindow(WindowType.TAB);
        log.info("New tab opened");
        return this;
    }

    public void switchToWindowByTitle(String expectedTitle) {
        
        for (String windowHandle : getDriver().getWindowHandles()) {
            getDriver().switchTo().window(windowHandle);

            if (getDriver().getTitle().contains(expectedTitle)) {
                log.info("Switched to window with title: " + getDriver().getTitle());
                return;
            }
        }
        throw new RuntimeException("No window found with title: " + expectedTitle);
    }

    public void switchToWindowByTitleWithRetry(String expectedTitle) {
        int maxRetries = 10;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                log.info("Attempt " + (retryCount + 1) + " to find window with title: " + expectedTitle);
                
                for (String windowHandle : getDriver().getWindowHandles()) {
                    getDriver().switchTo().window(windowHandle);

                    if (getDriver().getTitle().contains(expectedTitle)) {
                        log.info("Successfully switched to window with title: " + getDriver().getTitle());
                        return;
                    }
                }
                
                retryCount++;
                if (retryCount < maxRetries) {
                    log.info("Window not found, waiting 2 seconds before retry...");
                    Thread.sleep(2000);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting for window with title: " + expectedTitle, e);
            }
        }
        
        throw new RuntimeException("No window found with title: " + expectedTitle + " after " + maxRetries + " attempts");
    }

    /** Sends {@code filePath} to the Import Scan {@code adapt-rx-uploader} file input (no native OS dialog). */
    public void uploadFileInWindowsOpenDialog(String filePath) {
        File uploadFile = new File(filePath);
        if (!uploadFile.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a file: " + uploadFile.getAbsolutePath());
        }
        String absolutePath = uploadFile.getAbsolutePath();
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
        WebElement fileInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(ADAPT_RX_UPLOADER_FILE_INPUT_XPATH)));
        fileInput.sendKeys(absolutePath);
        log.info("Set file on import uploader: " + absolutePath);
    }

    /** Escape single quotes for XPath string literals delimited with single quotes. */
    public static String escapeSingleQuotesForXPath(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("'", "''");
    }
}
