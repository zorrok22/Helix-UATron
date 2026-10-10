package pages.ITSM.SmartIT;
import framework.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import pages.ITSM.dwp.DWPDashboard;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ViewRequestIDSmartRecorderPage extends BasePage {

	static SolqaLogger log;
	CommonWaits commonWaits;
	public ViewRequestIDSmartRecorderPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(ViewRequestIDSmartRecorderPage.class.getName());
		commonWaits=new CommonWaits();
	}

	@FindBy(xpath = "//div[contains(text(),'Request ID')]")
	WebElement dwpRequestID;

	public String PWA_Frame= "//iframe[@id='pwa-frame']";
	public String FetchDWPRequestID() {
		log.info("Fetching DWP Request ID");
        commonWaits.waitForPageFullyLoaded(30,3);
		commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
//		commonWaits.switchtoDefaultFrame();
//		commonWaits.switchToFrame(By.xpath(PWA_Frame));
		String rawText = commonWaits.waitForElementVisible(dwpRequestID).getText().trim(); 
		String requestID = rawText.replaceAll("\\D+", "");    
		log.info("DWP Request ID: " + requestID);
		return requestID;
	}
}
