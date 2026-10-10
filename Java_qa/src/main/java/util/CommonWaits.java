package util;

/* *********************************************************************
	Purpose:		This class contains common wait methods accessed in all page classes
	File Name:		CommonWaitMethods.Java
	Suite Name:		Framework File
	Module Name:	NA
	@author			akarhadk
	@version		1.0
	@since			2020-02-08
	Comments:		
********************************************************************** */
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.function.Function;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import framework.BasePage;

public class CommonWaits extends BasePage {

	public int minDelay = 2000;
	public int moreDelay = 5000;
	public int defaultTimeout = Integer.parseInt(configProp.get("DefaultTimeout"));
	public static Logger log = Logger.getLogger(CommonWaits.class.getName());
	public String mainWindowHandle;

	// Waits for element to be visible uses By Locator
public WebElement waitForByElementToBeVisible(By locator) {
    int timeInSeconds = 40;

    try {
        log.debug("Waiting for element to be visible: " + locator);

        Wait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(timeInSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        WebElement visibleElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        log.debug("Element is now visible: " + locator);
        return visibleElement;

    } catch (TimeoutException e) {
		log.error("Timeout: Element was not visible after " + timeInSeconds +
				" seconds. Locator: " + locator, e);
		throw new AssertionError("Element not visible after waiting for " + timeInSeconds + " seconds: " + locator, e);

	} catch (Exception e) {
		log.error("Unexpected error while waiting for element visibility. Locator: " + locator, e);
		throw new RuntimeException("Unexpected error while waiting for element visibility: " + locator, e);
	}
}


	// Waits for element to be visible uses WebElement
	public WebElement waitForElementVisible(WebElement element, int timeInSeconds) {
		try {
			Wait<WebDriver> wait = new FluentWait<>(getDriver())
					.withTimeout(Duration.ofSeconds(timeInSeconds))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(WebDriverException.class)
					.ignoring(NoSuchElementException.class)
					.ignoring(StaleElementReferenceException.class);

			return wait.until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			log.error("Timeout: Element was not visible after " + timeInSeconds +
					" seconds. Element: " + element, e);
			throw new AssertionError("Element not visible after waiting for " + timeInSeconds + " seconds: " + element, e);
		} catch (Exception e) {
			log.error("Unexpected error while waiting for element visibility: " + element, e);
			throw new RuntimeException("Unexpected error while waiting for element visibility: " + element, e);
		}
	}

	// Waits for element to be visible uses By WebElement
	public WebElement waitForElementVisible(WebElement element) {

		Wait<WebDriver> wait = new FluentWait<>(getDriver())
				.withTimeout(Duration.ofSeconds(50))
				.pollingEvery(Duration.ofMillis(1000))
				.ignoring(WebDriverException.class);

		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	// Waits for element to be present uses By Locator
	public WebElement waitForElementPresenceLocated(By element) {
		try {
			// Use BasePage.findElement() which has healing built-in!
			// This returns the healed element if healing was successful
			WebElement foundElement = findElement(element);
			// Element is already found (with healing if needed), just return it
			// doesn't need to wait for the original locator again
			log.debug("Element found (with healing if needed): " + element);
			return foundElement;

		} catch (NoSuchElementException e) {
			// If findElement with healing failed, log and throw
			log.error("Element not found even after healing attempts: " + element);
			throw new org.openqa.selenium.TimeoutException(
				"Expected condition failed: waiting for presence of element located by: " + element +
				" (tried for 50 second(s) with 1000 milliseconds interval)", e);
		}
	}

	// Waits for element to be visible uses WebElement with long time
	public WebElement waitForLongTimeForElementVisible(WebElement element) {

		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(120))
				.pollingEvery(Duration.ofMillis(1000))
				.ignoring(WebDriverException.class);

		return wait.until(ExpectedConditions.visibilityOf(element));
	}
	// An expectation for checking the element to be invisible
	// Waits for element to be clickable uses WebElement
	public WebElement waitForElementClickable(WebElement element) {

		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class)
				.ignoring(StaleElementReferenceException.class);
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	// Waits for element to be clickable uses WebElement with custom time
	public WebElement waitForElementClickable(WebElement element, long seconds) {

		Wait<WebDriver> wait = null;
		try {
			wait = new FluentWait<>(getDriver())
					.withTimeout(Duration.ofSeconds(seconds))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(WebDriverException.class);

			return wait.until(ExpectedConditions.elementToBeClickable(element));

		} catch (Exception e) {
			log.error("Waiting for the element to be clickable: ---> " + element);
		}

		assert wait != null;
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}
    
	// Waits for element to be clickable uses WebElement with custom time and returns status
	public boolean waitForElementClickableAsBoolean(WebElement element, long seconds) {
		try {
			Wait<WebDriver> wait = new FluentWait<>(getDriver())
					.withTimeout(Duration.ofSeconds(seconds))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(WebDriverException.class);

			wait.until(ExpectedConditions.elementToBeClickable(element));
			return true;
		} catch (Exception e) {
			log.debug("Element not clickable within timeout: " + element + " | timeout(s): " + seconds);
			return false;
		}
	}

	// Check if element is visible
	public boolean waitForVisibleElement(WebElement element) {
		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(40))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);

		return wait.until(visibilityOfElement(element));
	}

	private static ExpectedCondition<Boolean> visibilityOfElement(final WebElement element) {
		return new ExpectedCondition<>() {
			public Boolean apply(WebDriver webDriver) {
				return isVisible(element);
			}

			@Override
			public String toString() {
				return "Visibility of " + element;
			}
		};
	}

	// Check if element is invisible
	public boolean waitForElementInvisible(WebElement element) {

		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(40))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);

		return wait.until(ExpectedConditions.invisibilityOf(element));
	}

	// Check if element is invisible with custom time
	public boolean waitForElementInvisible_withFluentWait(WebDriver driverInstance, String locator, int seconds) {
		try {
			Wait<WebDriver> wait = new FluentWait<>(driverInstance).pollingEvery(Duration.ofMillis(500))
					.withTimeout(Duration.ofSeconds(seconds)).ignoring(WebDriverException.class);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator)));

		} catch (Exception e) {
			log.error("***************** Got exception: " + e.getMessage());
			return false;
		}
		return true;
	}

	// Check if element located by locator is invisible
	public boolean waitForInvisibilityOfElementLocated(By locator) {

		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(40))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);

		return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	// Check if list of elements are invisible
	public boolean waitForElementsInvisible(List<WebElement> elements) {

		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(40))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);

		return wait.until(ExpectedConditions.invisibilityOfAllElements(elements));
	}

	// Switch to frame using locator
	public void switchToFrame(By locator) {
		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(60))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);

		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
	}

	// Function to use thread.sleep
	public void waitForSometime(long timeInMilliSecond) {
		try {
			Thread.sleep(timeInMilliSecond);
		} catch (Exception e) {
			log.error(e);
		}
	}
// Waits for the page to be fully loaded by checking document.readyState and DOM stabilization
	public boolean waitForPageFullyLoaded(int maxTimeoutSeconds, int checkIntervalSeconds) {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (maxTimeoutSeconds * 1000L);
		String previousContent = "";
		int loop = 0;

		while (System.currentTimeMillis() < endTime) {
			loop++;
			// Optional: check document.readyState first
			String readyState = ((JavascriptExecutor) getDriver()).executeScript("return document.readyState").toString();
			if ("complete".equals(readyState)) {
				log.debug("Page readyState is complete.");
				return true;
			}
			// Optional: check full DOM stabilization
			String currentContent = getDocumentInnerHtml();
			if (currentContent.equals(previousContent)) {
				log.debug("Page DOM stabilized in " + ((System.currentTimeMillis() - startTime) / 1000) + "s after " + loop + " checks.");
				return true;
			}

			previousContent = currentContent;

			try {
				Thread.sleep(checkIntervalSeconds * 1000L);
			} catch (InterruptedException e) {
				log.error("Interrupted while waiting for page load", e);
				Thread.currentThread().interrupt();
				return false;
			}
		}

		log.error("Page did not fully load within timeout of " + maxTimeoutSeconds + " seconds.");
		return false;
	}

	// Helper method to get document.body.innerHTML
	public String getDocumentInnerHtml() {
		return (String) ((JavascriptExecutor) getDriver()).executeScript("return document.body.innerHTML;");
	}

	// Overloaded method to get document.body.innerHTML with currentContents parameter
	public String getDocumentInnerHtml(String currentContents) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		currentContents = (String) js.executeScript("return document.body.innerHTML;");
		return currentContents;
	}

	/**
	 * Clears and types into the first element matching {@code candidates} that is displayed and enabled.
	 * Use when several nodes share one locator (for example multiple {@code sb-grid-search-box} inputs where only the
	 * foreground field is interactable).
	 */
	public void keyClearAndTypeFirstInteractable(By candidates, String value) {
		keyClearAndTypeFirstInteractable(candidates, value, 30);
	}

	public void keyClearAndTypeFirstInteractable(By candidates, String value, int timeoutSeconds) {
		Wait<WebDriver> wait = new FluentWait<>(getDriver())
				.withTimeout(Duration.ofSeconds(Math.max(1, timeoutSeconds)))
				.pollingEvery(Duration.ofMillis(400))
				.ignoring(StaleElementReferenceException.class, NoSuchElementException.class)
				.ignoring(WebDriverException.class);
		WebElement interactable = wait.until(driver -> {
			for (WebElement el : driver.findElements(candidates)) {
				try {
					if (el.isDisplayed() && el.isEnabled()) {
						return el;
					}
				} catch (StaleElementReferenceException e) {
					// retry on next poll
				}
			}
			return null;
		});
		keyClearAndType(interactable, value);
	}

	// Click with retry logic to handle transient issues
	public void pureSafeClick(WebElement webElement) {
		this.waitForElementClickable(webElement);
		for (int i = 0; i < 10; i++) {
			try {
				webElement.click();
				break;
			} catch (Exception e) {
				log.error("--- Click failed: " + e.getMessage());
				pause();
			}
		}
	}

	// Click with retry logic to handle transient issues using locator
	public void pureSafeClick(String locator) {
		// ✅ Use healing-enabled findElement
		WebElement webElement = findElement(By.xpath(locator));
		waitForElementClickable(webElement, 30); // assuming 30 seconds as default wait
		for (int i = 0; i < 10; i++) {
			try {
				webElement.click();
				break;
			} catch (Exception e) {
				log.error("--- Click failed: " + e.getMessage());
				pause(); // ensure `pause()` is defined, or replace with Thread.sleep if needed
			}
		}
	}

	// waits for angular requests to finish
	public void waitForAngularRequestsToFinish() {
		waitForAngularRequestsToFinish(10, 2000);
	}

	// waits for angular requests to finish with configurable timeout/polling
	public boolean waitForAngularRequestsToFinish(int timeoutInSeconds, int pollingInMillis) {
		int timeout = Math.max(5, timeoutInSeconds);
		int polling = Math.max(200, pollingInMillis);
		log.debug("Started waiting for AngularJS requests to complete. timeout=" + timeout + "s, polling=" + polling + "ms");

		if (!isAngularAvailable(getDriver())) {
			log.debug("AngularJS not detected on current page; Angular wait not required.");
			return true;
		}

		try {
			Wait<WebDriver> wait = new FluentWait<>(getDriver())
					.pollingEvery(Duration.ofMillis(polling))
					.withTimeout(Duration.ofSeconds(timeout))
					.ignoring(WebDriverException.class)
					.ignoring(JavascriptException.class)
					.ignoring(NoSuchElementException.class)
					.ignoring(StaleElementReferenceException.class);

			Boolean completed = wait.until(driver -> {
				Object scriptResult = ((JavascriptExecutor) driver).executeScript(
						"try {"
								+ "if (!window.angular) return true;"
								+ "var root = document.querySelector('[ng-app]') || document.body;"
								+ "if (!root) return true;"
								+ "var ngEl = window.angular.element(root);"
								+ "if (!ngEl || !ngEl.injector || !ngEl.injector()) return true;"
								+ "var $http = ngEl.injector().get('$http');"
								+ "if (!$http || !$http.pendingRequests) return true;"
								+ "return $http.pendingRequests.length === 0;"
								+ "} catch (e) { return true; }"
				);
				return Boolean.TRUE.equals(scriptResult);
			});

			return Boolean.TRUE.equals(completed);
		} catch (TimeoutException e) {
			// Some Angular pages can keep long polling requests alive; do not fail execution for this.
			log.error("Angular requests did not settle within " + timeout + " seconds; continuing execution.");
			return false;
		}
	}

	// Composite wait for page loading completion: browser ready state + app spinners + angular requests.
	public void waitForPageLoadingComplete(String appName, int timeoutInSeconds) {
		int timeout = Math.max(10, timeoutInSeconds);
		log.debug("Waiting for page loading to complete. app=" + appName + ", timeout=" + timeout + "s");
		waitForPageReadyState();
		if (appName != null && !appName.trim().isEmpty()) {
			waitForAllSpinnersToHide(appName, Duration.ofSeconds(timeout));
		}
		waitForAngularRequestsToFinish(timeout, 500);
	}

	// Check if angular is available on the page
	private static boolean isAngularAvailable(@NotNull WebDriver wd) {
		try {
			Object result = ((JavascriptExecutor) wd).executeScript("return typeof window.angular !== 'undefined';");
			return Boolean.TRUE.equals(result);
		} catch (Exception e) {
			return false;
		}
	}

	// waits for execution complete
	public void waitForExecutionComplete() {
		try {
			waitForAngularRequestsToFinish();
		} catch (Exception var2) {
			log.error("Failed to wait for Execution Complete:" + var2.getMessage());
			throw var2;
		}
	}

	// Simple pause method
	public void pause() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.error("Pause failed");
		}
	}

	// Waits for element to be present uses locator with custom time
	public boolean waitForElementPresent(final String locator, int timeOut) {
		try {
			new WebDriverWait(getDriver(), Duration.ofSeconds(timeOut))
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// Waits for element to be present uses locator with default time
	public boolean waitForElementPresent(final String locator) {
		try {
			new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout))
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// Waits for element to be present uses By locator with default time
	public boolean waitForElementPresent(By by) {
		try {
			new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout))
					.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// Waits for element to be visible uses locator with custom time
	public boolean waitForElementVisible_withFluentWait(WebDriver driverInstance, String locator, int seconds) {

		try {
			Wait<WebDriver> wait = new FluentWait<>(driverInstance).pollingEvery(Duration.ofMillis(500))
					.withTimeout(Duration.ofSeconds(seconds)).ignoring(WebDriverException.class);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));

		} catch (Exception e) {
			log.error("****** Got exception: " + e.getMessage());
			return false;
		}
		return true;
	}

	// Waits for element to be visible uses By locator with custom time (preferred over raw xpath strings)
	public boolean waitForElementVisible_withFluentWait(WebDriver driverInstance, By by, int seconds) {

		try {
			Wait<WebDriver> wait = new FluentWait<>(driverInstance).pollingEvery(Duration.ofMillis(500))
					.withTimeout(Duration.ofSeconds(seconds)).ignoring(WebDriverException.class);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));

		} catch (Exception e) {
			log.error("****** Got exception: " + e.getMessage());
			return false;
		}
		return true;
	}

    // Waits for element to be visible uses By locator with custom time (preferred over raw xpath strings)
    public void waitAndVerifyForElementVisible_withFluentWait(WebDriver driverInstance, By by, int seconds) {
        try {
            Wait<WebDriver> wait = new FluentWait<>(driverInstance)
                    .pollingEvery(Duration.ofMillis(500))
                    .withTimeout(Duration.ofSeconds(seconds))
                    .ignoring(WebDriverException.class);

            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            log.error("Element not visible within timeout. Locator: " + by + ", timeout: " + seconds + "s", e);
            throw new AssertionError("Element not visible within " + seconds + " seconds: " + by, e);
        }
    }

	// Waits for element to be clickable uses By locator with default time
	public WebElement waitForElement(final By locator) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout));
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	// Check if element is present
	public boolean isElementPresent(WebElement element) {
		try {
			return new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout)).until(ExpectedConditions.visibilityOf(element)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// Check if element is present with custom time
	public boolean isElementPresent(By locator, int seconds) {
		try {
			Wait<WebDriver> wait = new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return element.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// Check if element is present with By locator
	public boolean isElementPresent(By locator) {
		try {
			// Use BasePage.findElement() which has healing
			WebElement element = findElement(locator);  // ✅ Healing happens here!
			return new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout))
					.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// Get list of elements with By locator
	public List<WebElement> getElements(By locator) {
		try {
			// Use BasePage.findElements() which has healing
			List<WebElement> elements = findElements(locator);  // ✅ Healing happens here!
			return new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout))
					.until(ExpectedConditions.visibilityOfAllElements(elements));
		} catch (Exception e) {
			log.error("Elements not found: " + locator);
			throw e;
		}
	}

	// Waits for element to be clickable uses locator with custom time
	public boolean waitForElementClickable(String locator, int seconds) {

		try {
			Wait<WebDriver> wait = new FluentWait<>(getDriver()).pollingEvery(Duration.ofMillis(500))
					.withTimeout(Duration.ofSeconds(seconds)).ignoring(WebDriverException.class);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));

		} catch (Exception e) {
			log.error("Got exception while elementToBeClickable: " + e.getMessage() + " and locator value is: " + locator);
			return false;
		}
		return true;
	}

	// Waits for number of windows to be equal to expected number
	public Boolean waitForNumberWindowsToBe(int number) {
		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(defaultTimeout))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);

		return wait.until(ExpectedConditions.numberOfWindowsToBe(number));
	}

	// Switch to last window
	public void switchToLastWindow() {
		log.debug("Switching to the last window");
		Set<String> allWindowHandles = getDriver().getWindowHandles();
		List<String> windowHandlesList = new ArrayList<>(allWindowHandles);
		String lastWindowHandle = windowHandlesList.get(windowHandlesList.size() - 1);
		WebDriver currentDriver = getDriver().switchTo().window(lastWindowHandle);
		setDriver(currentDriver);
		log.debug("Successfully switched to the last window");
	}

	// Waits for URL to contain specific text
	public Boolean waitForURLContains(String url) {
		Wait<WebDriver> wait = new FluentWait<>(getDriver()).withTimeout(Duration.ofSeconds(defaultTimeout))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(WebDriverException.class);

		return wait.until(ExpectedConditions.urlContains(url));
	}

	// Waits for element's text to change to specific value
	public WebElement waitForElementTextChange(final By locator, String attrValueToBe) {
		log.debug("Waiting " + Duration.ofSeconds(defaultTimeout) + " seconds for element's attribute change");
		new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout))
				.until(ExpectedConditions.textToBe(locator, attrValueToBe));
		return findElement(locator);  // ✅ Use healing-enabled findElement
	}

	// Select value from dropdown by visible text
	public void selectValueByVisibleText(String selectElement, String text) {
		Select select = new Select(this.waitForByElementToBeVisible(By.xpath(selectElement)));
		select.selectByVisibleText(text);
	}

	/** Single click via JavaScript after ensuring the element is visible and scrolled into view. */
	public void jsClick(WebElement webElement) {
		WebElement el = waitForElementVisible(webElement);
		javaScriptScrollIntoView(el);
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", el);
	}

	public void jsClickWithoutScroll(WebElement webElement) {
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", webElement);
	}

	// Set attribute value using JavaScript
	public void setAttributeValue(WebElement element, String expectedText) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].innerHTML = arguments[1]", element, expectedText);
	}

	// Double-click on element
	public void doubleClick(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.doubleClick(element).build().perform();
	}

	// Move to element
	public void moveToElement(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).build().perform();
	}

	// Select or deselect checkbox based on boolean value
	public void selectCheckBox(WebElement element, boolean check) {
		if (check) {
			if (!element.isSelected()) {
				element.click();
			}
		} else if (element.isSelected()) {
			element.click();
		}

	}

	// Fluent wait for WebElement
	public static WebElement fluentWait(final WebElement locator) {
		Wait<WebDriver> fluentWait = new FluentWait<>(getDriver())
				.withTimeout(Duration.ofSeconds(30L))
				.pollingEvery(Duration.ofSeconds(2L))
				.ignoring(NoSuchElementException.class);
		return fluentWait.until(driver -> locator);
	}

	// Waits for page to load completely by checking document.readyState
	public boolean waitForPageLoad(WebDriver driver) {
		waitForSometime(7000);
		return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("loaded")
				|| ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
	}

	// Waits for page to load completely by checking document.readyState
	public boolean waitForPageReadyState(WebDriver driver) {
		return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("loaded")
				|| ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
	}

    public void waitForPageReadyState() {
        int timeoutSeconds = 30;
        long endTime = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < endTime) {
            String readyState = ((JavascriptExecutor) getDriver()).executeScript("return document.readyState").toString();
            if ("loaded".equals(readyState) || "complete".equals(readyState)) {
                return;
            }
            waitForSometime(500);
        }
        log.error("Page did not reach 'loaded' or 'complete' state within timeout.");
        throw new AssertionError("Page did not reach 'loaded' or 'complete' state.");
    }

	// Wait for tab fully loaded by tab name
	public void waitForTabToLoadFully(String tabName) {
		List<String> handles = null;
		boolean flag = true;
		while (flag) {
			handles = new ArrayList<>(getDriver().getWindowHandles());
			for (String handle : handles) {
				getDriver().switchTo().window(handle);
				if (getDriver().getTitle().contains(tabName)) {
					flag = false;
					break;
				}
			}
		}
	}

	// Scroll to specific element
	public void scrollToSpecificElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].scrollIntoView();", element);
	}

	// Scroll to specific element using locator format and arguments
	public void scrollToSpecificElement(String locatorFormat, String... args) {
		try {
			String finalLocator = String.format(locatorFormat, (Object[]) args);
			WebElement element = findElement(By.xpath(finalLocator));  // ✅ Use healing-enabled findElement
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			js.executeScript("arguments[0].scrollIntoView();", element);
		} catch (Exception e) {
			log.error("Failed to scroll to element: " + locatorFormat + " | Error: " + e.getMessage());
		}
	}

	// Wait until element to be visible
	public void explicitWaitVisibilityOfElement(WebElement element, int time) {

		WebDriverWait elementToBeVisible = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		elementToBeVisible.until(ExpectedConditions.visibilityOf(element));
	}

	// Implicitly waits for an element to be displayed.
	public void implicitWait(int timeout) {
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	// Waits for loader to disappear
	public void loader(WebDriver driver) {
		try {
			// ⚡ PERFORMANCE FIX: Use direct driver call to avoid healing overhead
			By loaderLocator = By.xpath("//*[contains(@class,'loader-container')] | //*[contains(@class,'Please wait')]");
			List<WebElement> loaders = driver.findElements(loaderLocator);
			
			if (loaders.isEmpty()) {
				log.debug("✅ No loaders found - page already loaded");
				return;
			}
			
			log.debug("⏳ Found " + loaders.size() + " loader(s), waiting for invisibility...");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
			wait.until(ExpectedConditions.invisibilityOfAllElements(loaders));
			log.debug("✅ All loaders disappeared");
		} catch (TimeoutException e) {
			log.error("⚠️ Loader(s) did not disappear within 15s, continuing anyway");
		} catch (Exception e) {
			log.error("Loader wait completed: " + e.getMessage());
		}
	}

	// Wait until element to be clickable
	public void explicitWaitElementToBeClickable(WebElement element, int time) {

		WebDriverWait clickableWait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		clickableWait.until(ExpectedConditions.elementToBeClickable(element));
	}

	// is element present using locator
	public boolean isElementPresent(String locator) {
		try {
			Wait<WebDriver> wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
			return element.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// switch to tab by index
	public void switchToTabByIndex(WebDriver driver, int index) {
		ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(index));
	}

	// switch to tab by index
	public void switchToTabByIndex(int index) {
		ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
		getDriver().switchTo().window(tabs.get(index));
	}

	// switch to tab by title
	public void switchToTabByTitle(String title) {
		Set<String> allWindowHandles = getDriver().getWindowHandles();
		WebDriver currentDriver;
		for (String handle : allWindowHandles) {
			currentDriver = getDriver().switchTo().window(handle);
			if (getDriver().getTitle().contains(title)) {
				log.debug("Switched to tab with title: " + title);
				setDriver(currentDriver);
				return;
			}
		}
		log.warn("No tab found with title: " + title);
	}

	// Wait until element located by locator to be visible
	public WebElement waitForElementVisibility(By locator) {
		Wait<WebDriver> wait = new WebDriverWait(getDriver(), Duration.ofMillis(defaultTimeout))
				.pollingEvery(Duration.ofMillis(500));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	// Wait until element located by locator to be enabled
	public boolean waitForElementEnabled(String locator, int maxTimeOut, int timeSlice) {
		try {

			do {
				maxTimeOut -= timeSlice;
				Thread.sleep(timeSlice);
				if (findElement(By.xpath(locator)).isEnabled())
					return true;
			} while (maxTimeOut > 0);
		} catch (Exception e) {
			log.error("Element Not Found : " + locator);
			log.debug("ERROR" + e);
			return false;
		}
		return false;
	}

	// Wait until element located by locator to be loaded
	public void waitForElementLoad(By by) {
		int timeOut = 40;
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver input) {
				try {
					assert input != null;
					// ✅ Use healing-enabled findElement
					// Note: We use the outer class's findElement since we're in BasePage context
					return findElement(by).isDisplayed();
				} catch (StaleElementReferenceException e) {
					return null;
				}
			}
		};
		try {
			Thread.sleep(1000);
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeOut));
			wait.until(expectation);
		} catch (Throwable error) {
			log.error("Timeout waiting for element Load Request to complete." + error);
		}
	}

	// Wait until element located by locator to be stable (not stale)
	public WebElement waitForStableElement(By locator) {
		int timeoutInSeconds = 20;
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));

		return wait.until(driver -> {
			try {
				// ✅ Use healing-enabled findElement
				WebElement element = findElement(locator);
				if (element.isDisplayed()) {
					element.getText();
					return element;
				}
				return null;
			} catch (StaleElementReferenceException | NoSuchElementException e) {
				return null;
			}
		});
	}

	// Safely get text from element located by locator, handling staleness
	public String getTextSafely(By locator) {
		int timeoutInSeconds = 30;
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));

		return wait.until(driver -> {
			try {
				// ✅ Use healing-enabled findElement
				WebElement element = findElement(locator);
				if (element.isDisplayed()) {
					return element.getText();
				}
				return null;
			} catch (StaleElementReferenceException e) {
				return null; // retry
			}
		});
	}

	// clear text from input element using Ctrl+A and Delete
	public void clearTextFromElement(WebElement element) {
		Actions actions = new Actions(getDriver());
		actions.click(element)
				.keyDown(Keys.CONTROL)
				.sendKeys("a")
				.keyUp(Keys.CONTROL)
				.perform();
	}

	// Click element with retry logic to handle staleness
	public boolean retryingFindClick(WebElement ele) {
		boolean result = false;
		int attempts = 0;
		while (attempts < 5) {
			try {
				clickWithoutInterception(ele);
				result = true;
				break;
			} catch (StaleElementReferenceException ignored) {
			}
			attempts++;
		}
		return result;
	}

	// wait till new comment is added in the comments section
	public void waitTillNewCommentAdded(By by, int initComments) {
		new WebDriverWait(getDriver(), Duration.ofSeconds(defaultTimeout)).until(ExpectedConditions.numberOfElementsToBe(by, initComments + 1));
	}

	// Wait until element with link text is visible
	public void waitForElementWithLinkText(String linkText, int timeOut) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeOut));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(linkText))).isDisplayed();
		} catch (Exception var5) {
			log.error("Element Not Found : " + linkText);
			log.debug("ERROR" + var5);
		}
	}

	// Click element with link text
	public boolean clickByText(String text) {
		try {
			this.waitForElementWithLinkText(text, 30);
			findElement(By.linkText(text)).click();
			return true;
		} catch (Exception var3) {
			log.debug("ERROR" + var3);
			return false;
		}

	}

	// Type and select each part of the text in an input field
	public void typeAndSelectEachPart(WebElement input, String text) {
		String[] parts = text.split("\\s+");
		for (String part : parts) {
			input.clear();
			input.sendKeys(part + Keys.ENTER);
		}
	}

	// Waits until the next two-minute mark (e.g., 12:00, 12:02, 12:04, etc.)
	public void waitUntilNextTwoMinutes() {
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		java.time.LocalDateTime target = now.plusMinutes(2);
		long millisToWait = java.time.Duration.between(java.time.LocalDateTime.now(), target).toMillis();
		if (millisToWait > 0) {
			try {
				Thread.sleep(millisToWait);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	// Waits until the given attribute of the element changes from the initial value, within the specified timeout.
	public boolean waitForAttributeValueChange(WebElement element, String attribute, String initialValue, int timeoutInSeconds) {
		try {
			Wait<WebDriver> wait = new FluentWait<>(getDriver())
					.withTimeout(Duration.ofSeconds(timeoutInSeconds))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(WebDriverException.class);

			return wait.until(driver -> {
				String currentValue = element.getAttribute(attribute);
				return currentValue != null && !currentValue.equals(initialValue);
			});
		} catch (Exception e) {
			log.error("Attribute value did not change within timeout: " + e.getMessage());
			return false;
		}
	}

	// Waits for element to be visible uses WebElement with custom time
    public boolean waitForElementVisible_withFluentWait(WebDriver driverInstance, WebElement element, int seconds) {
        try {
            Wait<WebDriver> wait = new FluentWait<>(driverInstance)
                    .pollingEvery(Duration.ofMillis(1000))
                    .withTimeout(Duration.ofSeconds(seconds))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .ignoring(WebDriverException.class);
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            log.error("****** Got exception: " + e.getMessage());
            return false;
        }
        return true;
    }

	// Check if element is present with custom time
	public boolean isElementPresent(WebElement element, int seconds) {
		try {
			Wait<WebDriver> wait = new FluentWait<>(getDriver())
					.withTimeout(Duration.ofSeconds(seconds))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(WebDriverException.class);

			wait.until(ExpectedConditions.visibilityOf(element));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Double-click on element using JavaScript
	public void doubleClickOnElementUsingJS(WebElement webElement) {
		String jsCode =
				"var evt = new MouseEvent('dblclick', {" +
						"    bubbles: true," +
						"    cancelable: true," +
						"    view: window" +
						"});" +
						"arguments[0].dispatchEvent(evt);";
		((JavascriptExecutor) getDriver()).executeScript(jsCode, webElement);
	}

	// Waits for the number of browser windows/tabs to be at least the expected count within the specified timeout.
	public void waitForNumberOfWindows(WebDriver driver, int expectedWindowCount, int timeoutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
		wait.until(d -> d.getWindowHandles().size() >= expectedWindowCount);
	}

	// Waits for the current URL to match the expected URL within the specified timeout.
	public void waitForUrlToBe(WebDriver driver, String expectedUrl, int timeoutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
		wait.until(ExpectedConditions.urlToBe(expectedUrl));
	}

	// Store the main window handle for later use
	public void storeMainWindow() {
		mainWindowHandle = getDriver().getWindowHandle();
		log.debug("Stored main window handle: " + mainWindowHandle);
	}

	// Switch back to the main window using the stored handle
	public void switchBackToMainWindow() {
		if (mainWindowHandle != null) {
			log.debug("Switching back to the main window: " + mainWindowHandle);
			WebDriver currentDriver = getDriver().switchTo().window(mainWindowHandle);
			setDriver(currentDriver);
			log.debug("Successfully switched back to the main window");
		} else {
			log.error("Main window handle was not stored. Call storeMainWindow() before switching.");
		}
	}

	// Selects an option from a dropdown list based on the visible text, using JavaScript to scroll into view if necessary.
	public void selectFromDropdown(WebDriver driver, String locator, String expectedValue) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// Convert string locator to By (XPath by default)
		By byLocator = By.xpath(locator);

		// Wait for all options
		List<WebElement> options = wait.until(ExpectedConditions
				.visibilityOfAllElementsLocatedBy(byLocator));

		// Iterate and click the exact match
		for (WebElement option : options) {
			String text = option.getText().trim();
			if (text.equalsIgnoreCase(expectedValue)) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
				option.click();
				System.out.println("Selected: " + text);
				return;
			}
		}

		throw new NoSuchElementException("Value not found in dropdown: " + expectedValue);
	}
	
	public void clickIfElementPresent(WebElement input) {
		// Check if element present, If yes then click on it
	    if(isElementPresent(input)) {
	    	clickWithoutInterception(input); 
	    }
	}
	
	public Boolean isElementChecked(WebElement input)
	{
		return (boolean) ((JavascriptExecutor) getDriver()).executeScript("return arguments[0].checked;", input);
	}

    public void switchToActiveWindow() {
        String originalWindow = getDriver().getWindowHandle();
        Set<String> windowHandles = getDriver().getWindowHandles();
        for (String handle : windowHandles) {
            if (!handle.equals(originalWindow)) {
                getDriver().switchTo().window(handle);
                break;
            }
        }
    }

    public void switchToActiveWindow(int timeInterval) {
        try {
            Thread.sleep(timeInterval); // Wait for the new window to appear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting to switch window: " + e.getMessage());
        }
        String originalWindow = getDriver().getWindowHandle();
        Set<String> windowHandles = getDriver().getWindowHandles();
        for (String handle : windowHandles) {
            if (!handle.equals(originalWindow)) {
                getDriver().switchTo().window(handle);
                break;
            }
        }
    }

    public boolean waitForElementInvisible_withFluentWait(WebDriver driverInstance, WebElement element, int seconds) {
        try {
            Wait<WebDriver> wait = new FluentWait<>(driverInstance)
                    .pollingEvery(Duration.ofMillis(500))
                    .withTimeout(Duration.ofSeconds(seconds))
                    .ignoring(WebDriverException.class);
            wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            log.error("***************** Got exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    // Waits until the provided element's value/text/innerText matches expected value.
    public boolean waitForExpectedValue(WebElement element, String expectedValue, int timeoutInSeconds) {
        return waitForExpectedValue(driver -> getElementValueTextOrInnerText(element), expectedValue, timeoutInSeconds, 500);
    }

    private String getElementValueTextOrInnerText(WebElement element) {
        if (element == null) {
            return "";
        }

        String value = element.getAttribute("value");
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        String text = element.getText();
        if (text != null && !text.trim().isEmpty()) {
            return text.trim();
        }

        String innerText = element.getAttribute("innerText");
        if (innerText != null) {
            return innerText.trim();
        }

        return "";
    }

    // Generic polling wait for expected value comparison using any dynamic value provider.
    public boolean waitForExpectedValue(Function<WebDriver, String> actualValueProvider,
                                        String expectedValue,
                                        int timeoutInSeconds,
                                        int pollingIntervalInMillis) {
        try {
            Wait<WebDriver> wait = new FluentWait<>(getDriver())
                    .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                    .pollingEvery(Duration.ofMillis(Math.max(100, pollingIntervalInMillis)))
                    .ignoring(WebDriverException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .ignoring(NoSuchElementException.class);

            return wait.until(driver -> {
                String actualValue = actualValueProvider.apply(driver);
                return Objects.equals(expectedValue, actualValue);
            });
        } catch (TimeoutException e) {
            log.error("Expected value was not matched within timeout. Expected: " + expectedValue
                    + " | Timeout(sec): " + timeoutInSeconds);
            return false;
        } catch (Exception e) {
            log.error("Error while waiting for expected value match: " + e.getMessage());
            return false;
        }
    }

    public void waitForAllSpinnersToHide(String appName) {
        waitForAllSpinnersToHide(appName, Duration.ofSeconds(40));
        waitForPageReadyState();
    }

    // New overload with custom timeout (based on your logic)
    public void waitForAllSpinnersToHide(String appName, Duration secondsToWait) {
        String locator = getSpinnerLocatorForApp(appName);

        if (locator == null || locator.trim().isEmpty()) {
            log.debug("No spinner locator configured for app: " + appName);
            return;
        }

        long seconds = Math.max(40L, secondsToWait == null ? 0L : secondsToWait.toSeconds());
        log.debug("Waiting for all spinners to hide for app [" + appName + "] up to " + seconds + " seconds. Locator: " + locator);

        Duration previousImplicit = getDriver().manage().timeouts().getImplicitWaitTimeout();

        try {
            getDriver().manage().timeouts().implicitlyWait(Duration.ZERO);
            Thread.sleep(300L); // Reduced from 1000ms - just enough for spinner to appear

            Wait<WebDriver> wait = new FluentWait<>(getDriver())
                    .withTimeout(Duration.ofSeconds(seconds))
                    .pollingEvery(Duration.ofMillis(250))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .ignoring(WebDriverException.class);

            By spinnerBy = By.xpath(locator);

            wait.until(driver -> {
                List<WebElement> spinners = driver.findElements(spinnerBy);
                if (spinners == null || spinners.isEmpty()) {
                    return true;
                }

                for (WebElement spinner : spinners) {
                    try {
                        if (spinner.isDisplayed()) {
                            return false;
                        }
                    } catch (StaleElementReferenceException ignored) {
                        // Spinner got detached; treat as hidden for this poll.
                    }
                }
                return true;
            });

            log.debug("All spinners hidden for app [" + appName + "]");
		} catch (TimeoutException e) {
			// Spinner waits should not fail test flow; continue and let next action decide.
			log.error("Spinner still visible for app [" + appName + "] after " + seconds + " seconds, continuing test flow.");
        } catch (Throwable e) {
			log.error("Spinner wait finished with exception for app [" + appName + "]: " + e.getMessage());
        } finally {
            getDriver().manage().timeouts().implicitlyWait(previousImplicit);
        }
    }

    private String getSpinnerLocatorForApp(String appName) {
        if (appName == null) {
            return null;
        }

        switch (appName.toUpperCase()) {
            case "DWP":
            case "FSM":
            case "CSM":
            case "BHOM":
            case "INNOVATIONSTUDIO":
            case "AIOPS":
            case "DWPC":
            case "HELIXGPT":
            case "DOTS_LOADER":
                return "//*[contains(@class, 'loader-container')]";

            case "BWF":
                return "//*[contains(@class, 'application-loader')]";

            case "SMARTIT":
            case "SSPLIVECHAT":
            case "ITOM":
                return "//*[contains(@class, 'loader')]";

            case "HELIX_SMARTIT":
                return "//*[contains(@class, 'loading-spinner')]";

            case "ITSM":
                return "//*[contains(@class, 'loadingContainer')]";

            case "SMART REPORTING":
                return "//*[@id='canvasLoader']";

            case "SERVICE":
                return "(//div[contains(@class,'d-preloader')])[2]";

            case "BWFA":
                return "//*[contains(@class, 'rx-application-loader')]";

            default:
                return null;
        }
    }

    public WebElement findFirstDisplayedWithin(WebElement root, By by) {
        if (root == null) return null;
        List<WebElement> els = root.findElements(by);
        if (els == null) return null;
        for (WebElement el : els) {
            try {
                if (el != null && el.isDisplayed()) return el;
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}