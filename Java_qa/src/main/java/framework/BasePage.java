package framework;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import util.*;

public class BasePage extends Launcher {

	static SolqaLogger log = new SolqaLogger(BasePage.class.getName());
	public static int defaultTimeout = 0;
	
	// Locator Healer - Enable self-healing locators (loaded from config.properties)
	private static boolean locatorHealingEnabled = loadLocatorHealingConfig();
	private static final ThreadLocal<String> currentTestName = new ThreadLocal<>();
	
	/**
	 * Load locator healing configuration from properties.config via Launcher reference
	 * @return true if enabled, false otherwise (defaults to true if not found)
	 */
	private static boolean loadLocatorHealingConfig() {
		try {
			// First check if set as system property (for runtime override)
			String configValue = System.getProperty("locatorHealingEnabled");
			
			// If not set as system property, read from properties.config via Launcher's configProp
			if (configValue == null) {
				// Access configProp from Launcher (inherited from Driver class)
				if (configProp != null && configProp.containsKey("locatorHealingEnabled")) {
					configValue = configProp.get("locatorHealingEnabled");
				} else {
					// If configProp is not initialized yet, read directly from properties.config
					String userDir = System.getProperty("user.dir");
					String configFilePath = userDir + File.separator + "properties.config";
					Map<String, String> properties = ReadPropertiesFile.readProperties(configFilePath);
					if (properties != null && properties.containsKey("locatorHealingEnabled")) {
						configValue = properties.get("locatorHealingEnabled");
					}
				}
			}
			
			if (configValue != null) {
				boolean enabled = Boolean.parseBoolean(configValue.trim());
				log.debug("Locator healing configured from properties.config: " + enabled);
				return enabled;
			}
		} catch (Exception e) {
			log.warn("Could not read locatorHealingEnabled from properties.config, using default: true. Error: " + e.getMessage());
		}
		
		// Default to true if not configured
		log.debug("Locator healing using default value: true");
		return true;
	}
	
	/**
	 * Initialize a page object with self-healing @FindBy elements
	 * This should be called instead of PageFactory.initElements() to enable healing on @FindBy
	 * 
	 * @param pageObject The page object to initialize
	 * @param <T> Type of page object
	 * @return The initialized page object with healing-enabled elements
	 */
	public static <T> T initPageWithHealing(T pageObject) {
	    // Early return if healing is disabled
	    if (!locatorHealingEnabled) {
	        WebDriver driver = getDriver();
	        org.openqa.selenium.support.PageFactory.initElements(driver, pageObject);
	        return pageObject;
	    }
	    
	    // Use healing factory when healing is enabled
	    WebDriver driver = getDriver();
	    String testName = getCurrentTestName();
	    
	    HealingElementLocatorFactory factory = new HealingElementLocatorFactory(driver, testName);
	    org.openqa.selenium.support.PageFactory.initElements(factory, pageObject);
	    
	    log.debug("Initialized page object with self-healing @FindBy elements: " + pageObject.getClass().getSimpleName());
	    
	    return pageObject;
	}
	
	/**
	 * Set the current test name for locator healing tracking
	 * @param testName Name of the current test
	 */
	public static void setCurrentTestName(String testName) {
		currentTestName.set(testName);
	}
	
	/**
	 * Get the current test name
	 * @return Current test name
	 */
	public static String getCurrentTestName() {
		String testName = currentTestName.get();
		return testName != null ? testName : "UnknownTest";
	}
	
	/**
	 * Enable or disable locator healing
	 * @param enabled true to enable, false to disable
	 */
	public static void setLocatorHealingEnabled(boolean enabled) {
		locatorHealingEnabled = enabled;
		log.debug("Locator healing " + (enabled ? "enabled" : "disabled"));
	}
	
	/**
	 * Check if locator healing is enabled
	 * @return true if enabled, false otherwise
	 */
	public static boolean isLocatorHealingEnabled() {
		return locatorHealingEnabled;
	}

	/**
	 * Find element with self-healing capability
	 * If the original locator fails, it attempts to heal the locator using various strategies
	 * @param by Locator to find element
	 * @return WebElement found
	 */
	public WebElement findElement(By by) {
	    // Early return if healing is disabled
	    if (!locatorHealingEnabled) {
	        return getDriver().findElement(by);
	    }
	    
	    // Healing is enabled - try normal find first, then heal if needed
	    try {
	        return getDriver().findElement(by);
	    } catch (NoSuchElementException e) {
	        log.debug("Element not found with original locator, attempting to heal...");
	        
	        // Get timeout - use default if commonWaits is null
	        int timeout = (commonWaits != null && commonWaits.defaultTimeout > 0) 
	            ? commonWaits.defaultTimeout 
	            : 30; // Default 30 seconds
	        
	        LocatorHealer healer = new LocatorHealer(getDriver(), timeout);
	        return healer.findElement(by, getCurrentTestName());
	    }
	}
	
	/**
	 * Find element without healing (for cases where healing should be skipped)
	 * @param by Locator to find element
	 * @return WebElement found
	 */
	public WebElement findElementWithoutHealing(By by) {
		return getDriver().findElement(by);
	}

	public List<WebElement> findElements(By by) {
		if (locatorHealingEnabled) {
			try {
				List<WebElement> elements = getDriver().findElements(by);
				if (elements != null && !elements.isEmpty()) {
					return elements;
				}
				// If empty list, try healing
				log.debug("Elements list empty with original locator, attempting to heal...");
				// Get timeout - use default if commonWaits is null
				int timeout = (commonWaits != null && commonWaits.defaultTimeout > 0) 
					? commonWaits.defaultTimeout 
					: 30; // Default 30 seconds
				LocatorHealer healer = new LocatorHealer(getDriver(), timeout);
				return healer.findElements(by, getCurrentTestName());
			} catch (NoSuchElementException e) {
				log.debug("Elements not found with original locator, attempting to heal...");
				// Get timeout - use default if commonWaits is null
				int timeout = (commonWaits != null && commonWaits.defaultTimeout > 0) 
					? commonWaits.defaultTimeout 
					: 30; // Default 30 seconds
				LocatorHealer healer = new LocatorHealer(getDriver(), timeout);
				return healer.findElements(by, getCurrentTestName());
			}
		} else {
			return getDriver().findElements(by);
		}
	}
	
	/**
	 * Find elements without healing (for cases where healing should be skipped)
	 * @param by Locator to find elements
	 * @return List of WebElements found
	 */
	public List<WebElement> findElementsWithoutHealing(By by) {
		return getDriver().findElements(by);
	}

	public boolean isElementPresent(By by) {
		try {
			if (!findElements(by).isEmpty()) {
				return new WebDriverWait(getDriver(), Duration.ofSeconds(commonWaits.defaultTimeout)).until(ExpectedConditions.visibilityOf(findElement(by))).isDisplayed();
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public void click(String locator) {
		commonWaits.waitForElementPresent(locator);
		findElement(By.xpath(locator)).click();
	}

	public void click(WebElement element) {
		commonWaits.waitForElementVisible(element);
		element.click();
	}

	public void click(By locator) {
		commonWaits.waitForElementPresent(locator);
		findElement(locator).click();
	}

	protected String getValueFromText(final String locator) {
		Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), By.xpath(locator), 20));
		return getDriver().findElement(By.xpath(locator)).getText();
	}

	protected WebElement getElementFromLocator(final String locator) {
		try {
			return getWebDriver().findElement(byLocator(locator));
		} catch (Exception e) {
			log.debug("ERROR: " + e);
			return null;
		}
	}

	protected By byLocator(String locator) {
		if (locator.startsWith("//") || locator.startsWith("(//")) {
			return By.xpath(locator);
		} else if (locator.startsWith("css=")) {
			return By.cssSelector(locator.substring(4));
		} else if (locator.startsWith("#")) {
			return By.cssSelector(locator);
		} else {
			return By.id(locator);
		}
	}

	public void keyClickAndType(String locator, final String value) {
		log.debug("Clicking field and Typing text");
		commonWaits.waitForElementPresent(locator, commonWaits.defaultTimeout);
		commonWaits.waitForElement(By.xpath(locator));
		getDriver().findElement(By.xpath(locator)).sendKeys(value);
	}

	public void verifyEquals(Object actual, Object expected, String message) {
		try {
			Assert.assertEquals(actual, expected, message);
		} catch (Exception e) {
			log.debug("ERROR" + e);
		}
	}

	public void verifyTrue(boolean condition, String message) {
		try {
			Assert.assertTrue(condition, message);
		} catch (Exception e) {
			log.debug("Verification Failed : " + message);
			log.debug("ERROR" + e);
		}
	}

	public void clickByAction(WebElement webelement) {
		Actions builder = new Actions(getDriver());
		builder.moveToElement(webelement).click(webelement);
		builder.perform();
	}

	public boolean isElementNotPresent(String locator) {
		
		return getDriver().findElements(By.xpath(locator)).isEmpty();
	}

	public WebElement typeKeys(final WebElement we, final String value) {
		we.clear();
		we.sendKeys(value);
		return we;
	}

	public void selectValueByVisibleText(String selectElement, String text) {
		Select select = new Select(commonWaits.waitForByElementToBeVisible(By.xpath(selectElement)));
		select.selectByVisibleText(text);
	}

	public WebElement scrollToElement(WebElement locator) {
		Point coordinates = locator.getLocation();
		scroll(coordinates.x, coordinates.y);
		return locator;
	}

	public void scroll(int x, int y) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("window.scrollTo(" + x + ", " + y + ")");
	}

	public void scrollToPageTop() {
		log.debug("Scrolling to the page top");
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		jse.executeScript("let h = document.body.scrollHeight \n" +
				"window.scrollBy(0,-1 * h)", "");
	}

	public void keyClearAndType(String locator, final String value) {
		log.debug("Clearing field and Typing text");
		commonWaits.waitForElementPresent(locator, commonWaits.defaultTimeout);
		commonWaits.waitForElement(By.xpath(locator)).clear();
		commonWaits.clickWithoutInterception(By.xpath(locator));
		getDriver().findElement(By.xpath(locator)).sendKeys(value);
	}

	public void keyClearAndClick(String locator) {
		log.debug("Clearing field and Clicking element");
		commonWaits.waitForElementPresent(locator, commonWaits.defaultTimeout);
		commonWaits.waitForElement(By.xpath(locator)).clear();
		this.click(locator);
	}

	public void verifyElementPresent(By locator, String message) {
		Assert.assertTrue(isElementPresent(locator), message);
	}

	public void clickByJS(WebElement element) {
		commonWaits.waitForElementVisible(element);
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].click();", element);
	}

	public void switchtoDefaultFrame() {
		try {
			getDriver().switchTo().defaultContent();
			log.debug("Navigated back to webpage from frame");
		} catch (Exception var3) {
			log.error("Unable to navigate back to main webpage from frame", var3);
		}
	}

	public void clickWithoutInterception(By locator) {
		try {
			commonWaits.waitForByElementToBeVisible(locator);
			commonWaits.waitForElementPresenceLocated(locator).click();
		} catch (ElementClickInterceptedException | StaleElementReferenceException exception) {
			log.debug("Getting ElementClickInterceptedException so clicking by javascript ");
			this.executeJS("arguments[0].click()", commonWaits.waitForByElementToBeVisible(locator));
		}
	}

	public void clickWithoutInterception(WebElement element) {
		try {
			commonWaits.waitForElementVisible(element,40);
			commonWaits.waitForElementClickable(element).click();
		} catch (ElementClickInterceptedException | StaleElementReferenceException exception) {
			log.debug("Getting ElementClickInterceptedException so clicking by javascript ");
			this.executeJS("arguments[0].click()", commonWaits.waitForElementVisible(element));
		}
	}

	public Object executeJS(String script, WebElement we) {
		return ((JavascriptExecutor) getDriver()).executeScript(script, new Object[]{we});
	}

	public Object executeJS(String script) {
		return ((JavascriptExecutor) getDriver()).executeScript(script);
	}

	public void javaScriptScrollIntoView(WebElement webelement) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].scrollIntoView(true);", webelement);
	}

	public void sendKeysByAction(WebElement webelement, String message) {
		Actions act = new Actions(getDriver());
		act.sendKeys(webelement, message).build().perform();
	}

	public void switchToFrame(WebElement element) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver()).withTimeout(Duration.ofSeconds(60))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
	}

    public void switchToFrameByNameOrID(String nameOrId) {
        Wait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(WebDriverException.class);

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
        log.debug("Switched to frame by name/id: " + nameOrId);
    }

	public void switchToFrame(By locator) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver()).withTimeout(Duration.ofSeconds(60))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
	}

	public void clickToElement(WebElement element) {
		JavascriptExecutor jsexec = (JavascriptExecutor) getDriver();
		jsexec.executeScript("arguments[0].click();", element);
	}

	// Switch To Child window
	public void switchToChildWindow(String currentHandle) {
		Set<String> handles = getDriver().getWindowHandles();
		for (String handle : handles) {
			if (!handle.equals(currentHandle)) {
				getDriver().switchTo().window(handle);
			}
		}
	}

	// Verify Page Title
	public void verifyTitle(String expected) {
		//logger.info("Verifing Page Title");
		commonWaits.waitForPageLoad(getDriver());
		String actual = getDriver().getTitle();
		Assert.assertTrue(actual.contains(expected));
		//logger.info("Verifing Page Title Completed");
	}

	// Switch To Parent Window
	public void switchToParentWindow() {
		Set<String> handles = getDriver().getWindowHandles();
		for (String handle : handles) {
			getDriver().switchTo().window(handle);
		}
	}

	// Generate random integers in range 0 to 99999
	public int randomNumber() {
		Random random = new Random();
        return random.nextInt(100000);
	}

	public String generateRandomString() {
		StringBuilder random = new StringBuilder();
		Random rnd = new Random();
		while (random.length() < 18) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SolQAConstants.SALTCHARS.length());
			random.append(SolQAConstants.SALTCHARS.charAt(index));
		}
		return random.toString();
	}

	public void refreshBrowser() {
		getDriver().navigate().refresh();
		commonWaits.waitForPageLoad(getDriver());
		commonWaits.waitForAngularRequestsToFinish();
        commonWaits.waitForPageReadyState();
	}

	// get text of element
	public void getText(WebElement element, String expectedText) {
		String actualText = element.getText();
		Assert.assertEquals(actualText, expectedText);
	}

	// get text of element
	public String getText(WebElement element) {
        return element.getText();
	}

	public static boolean isVisible(final WebElement element) {
		try {
			return element.isDisplayed();
		} catch (Exception ignored) {
			// We can assume a stale element isn't displayed.
			return false;
		}
	}

	public void captureScreenshot(String screenShotName, String app) throws IOException {
		GetScreenshot.capture(screenShotName, app);
	}

	public void sendEscKey() {
		Actions keyEvnt = new Actions(getDriver());
		keyEvnt.sendKeys(new CharSequence[]{Keys.ESCAPE}).build().perform();
	}

	public void sendEnterKey() {
		Actions keyEvent = new Actions(getDriver());
		keyEvent.sendKeys(Keys.ENTER).build().perform();
	}

	public String getattributeValue(WebElement we, String attributeName) {
		return we.getAttribute(attributeName);
	}

	public void switchToFrame(String locator) {
		switchtoDefaultFrame();
		switchToFrame((By.xpath(locator)));
	}

	public void switchToFrameInsideFrame(String parentLocator, String childLocator) {
		switchToFrame((By.xpath(parentLocator)));
		switchToFrame((By.xpath(childLocator)));
	}

	public String getCurrentDateTime(String pattern) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return now.format(formatter);
	}

	public String getPastDateTime(int iHowManyDaysBefore, String datepattern) {
		LocalDate today = LocalDate.now();
		LocalDate todayMinusDate = today.minusDays(iHowManyDaysBefore);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datepattern);
		return todayMinusDate.format(formatter);
	}

	public String getFutureDateTime(int daysToAdd, String datePattern) {
		LocalDate futureDate = LocalDate.now().plusDays(daysToAdd);
		LocalDateTime futureDateTime = futureDate.atStartOfDay(); // Sets time to 12:00:00 AM
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern, Locale.ENGLISH);
		return futureDateTime.format(formatter);
	}

	public boolean isElementClickable(WebElement webElement) {
		try {
			new WebDriverWait(getDriver(), Duration.ofSeconds(commonWaits.defaultTimeout))
					.until(ExpectedConditions.elementToBeClickable(webElement));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void scrollToBottomOfPage() {
		log.debug("Scroll page till the bottom of the page.");
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	public void pasteTextUsingActions(String locator, String textToPaste) {
		try {
			StringSelection stringSelection = new StringSelection(textToPaste);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);

			WebElement element = getDriver().findElement(By.xpath(locator));
			// Use Actions to paste text
			Actions actions = new Actions(getDriver());
			actions.moveToElement(element).click()
					.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
					.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL)
					.build().perform();

			log.debug("Successfully pasted value into element: " + textToPaste);
		} catch (Exception e) {
			log.error("Failed to paste text using Actions on locator: " + locator, e);
		}
	}

	public void clickByAction(String locator) {
		WebElement element = getDriver().findElement(By.xpath(locator));
		Actions builder = new Actions(getDriver());
		builder.moveToElement(element).click().perform();
	}

	public WebElement scrollToElement(String locator) {
		WebElement element = getElement(locator);
		Point coordinates = element.getLocation();
		scroll(coordinates.getX(), coordinates.getY());
		return element;
	}

	public WebElement getElement(String locator) {
		WebDriver driver = getDriver();
		if (locator.startsWith("xpath=")) {
			return driver.findElement(By.xpath(locator.replace("xpath=", "")));
		} else if (locator.startsWith("css=")) {
			return findElement(By.cssSelector(locator.replace("css=", "")));
		} else if (locator.startsWith("id=")) {
			return findElement(By.id(locator.replace("id=", "")));
		} else if (locator.startsWith("name=")) {
			return findElement(By.name(locator.replace("name=", "")));
		} else {
			throw new IllegalArgumentException("Unsupported locator strategy: " + locator);
		}
	}

	public boolean isAlertPresent(WebDriver driver, long timeoutInSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
			wait.until(ExpectedConditions.alertIsPresent());
			return true; // Alert is present
		} catch (NoAlertPresentException e) {
			return false; // No alert found within the timeout
		} catch (org.openqa.selenium.TimeoutException e) {
			return false; // Alert did not appear within the specified timeout
		}
	}

  public void typeKeysWithEnter(WebElement element, String value) {
    element.clear();
    element.sendKeys(value + Keys.ENTER);
}

  public void keyClearAndType(WebElement element, String value) {
      log.debug("Clearing field and Typing text");
      element.clear();
      element.sendKeys(value);
  }

	//Element Text contains
	public void elementTextContains(WebElement element, String expectedText) {
		String actualText = element.getText();
		log.debug("Actual Text: " + actualText + "\n Expected Text: " + expectedText);
		Assert.assertTrue(actualText.contains(expectedText));
	}

	public WebElement typeKeys(WebElement we, String value, boolean clear, boolean keyClear) {
		if (clear)
			we.clear();
		if (keyClear) {
			we.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			we.sendKeys(Keys.chord(Keys.DELETE));
		}
		we.sendKeys(value);
		return we;
	}

	public String getFutureDate(int daysToAdd, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = new Date();
		if (daysToAdd != 0) {
			date = new Date(System.currentTimeMillis() + (long) daysToAdd * 24 * 60 * 60 * 1000);
		}
		return sdf.format(date);
	}

	public String getDateByOffset(int daysOffset) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
		return LocalDate.now().plusDays(daysOffset).format(formatter);
	}

	
	public void switchToAlertAndAccept(long timeoutInSeconds) {
		if (isAlertPresent(getDriver(), timeoutInSeconds)) {
			log.debug("Alert is present, accepting it");
			getDriver().switchTo().alert().accept();
		} else {
			Assert.fail("No alert present to accept within timeout of " + timeoutInSeconds + " seconds");
		}
	}

}

