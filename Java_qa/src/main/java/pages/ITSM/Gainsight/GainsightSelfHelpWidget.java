package pages.ITSM.Gainsight;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import util.CommonWaits;

import java.util.List;

public class GainsightSelfHelpWidget extends BasePage {

	private final CommonWaits commonWaits = new CommonWaits();

	// Reused across Helix apps (SmartIT/CMDB/etc)
	private static final String IFRAME_WIDGET = "//iframe[@id='px-widget-window']";
	private static final String BTN_WIDGET_ICON = "//button[contains(@class,'px-widget-dragging-handle')]";
	private static final String BTN_WIDGET_CLOSE =
		"//button[@aria-label='Click to close the Knowledge Center Bot window.' and contains(@class,'px-widget-dragging-handle')]";

	public boolean isKnowledgeBotLoaded() {
		commonWaits.switchtoDefaultFrame();
		try {
			WebElement btn = commonWaits.waitForByElementToBeVisible(By.xpath(BTN_WIDGET_ICON));
			return btn != null && btn.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public GainsightSelfHelpWidget openBot() {
		WebElement btn = commonWaits.waitForByElementToBeVisible(By.xpath(BTN_WIDGET_ICON));
		Assert.assertTrue(btn.isDisplayed(), "Gainsight Knowledge Bot icon is not visible");
		commonWaits.clickWithoutInterception(btn);
		commonWaits.waitForByElementToBeVisible(By.xpath(IFRAME_WIDGET));
		return this;
	}

	public GainsightSelfHelpWidget verifyHelpsAndGuides(String expectedText) {
		Assert.assertNotNull(expectedText, "expectedText must not be null");
		switchtoDefaultFrame();
		commonWaits.switchToFrame(By.xpath(IFRAME_WIDGET));

		String xp = "//*[contains(normalize-space(.)," + quoteForXpathContains(expectedText) + ")]";
		List<WebElement> matches = findElements(By.xpath(xp));
		Assert.assertFalse(matches == null || matches.isEmpty(), "Expected help/guide not present in Gainsight widget: " + expectedText);

		switchtoDefaultFrame();
		return this;
	}

	public GainsightSelfHelpWidget closeBotIfOpened() {
		switchtoDefaultFrame();
		try {
			WebElement close = findElement(By.xpath(BTN_WIDGET_CLOSE));
			if (close != null && close.isDisplayed()) {
				commonWaits.clickWithoutInterception(close);
			}
		} catch (Exception ignored) {}
		switchtoDefaultFrame();
		return this;
	}

	private static String quoteForXpathContains(String text) {
		if (text.contains("'") && text.contains("\"")) {
			String[] parts = text.split("'");
			StringBuilder concat = new StringBuilder("concat(");
			for (int i = 0; i < parts.length; i++) {
				if (i > 0) {
					concat.append(", \"'\", ");
				}
				concat.append("'").append(parts[i]).append("'");
			}
			concat.append(")");
			return concat.toString();
		}
		if (text.contains("'")) {
			return "\"" + text + "\"";
		}
		return "'" + text + "'";
	}
}

