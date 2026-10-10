package pages.ITSM.Apex;

import java.util.Map;

import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import pages.ITSM.ITSM.Release;
import pages.ITSM.SmartIT.TicketConsolePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ApexReleasePage extends BasePage{
	static SolqaLogger log;
	CommonWaits commonWaits;
	Release release;

	public ApexReleasePage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(TicketConsolePage.class.getName());
		commonWaits = new CommonWaits();
		release = new Release();
	}
	
	public String fillReleaseDataApex(Map<Object, String> releaseData){
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        // Set Summary
        if (releaseData.get("Summary*") != null) {
            release.setTextAreaInput("Summary*", releaseData.get("Summary*"));
        }
        
        if (releaseData.get("Impact*") != null) {
        	release.setDropDownField("Impact", releaseData.get("Impact*"));
        }

        if (releaseData.get("Business Justification*") != null) {
        	release.setDropDownField("Business Justification*", releaseData.get("Business Justification*"));
        }

        if (releaseData.get("Urgency*") != null) {
        	release.setDropDownField("Urgency*", releaseData.get("Urgency*"));
        }

        return release.fillReleaseData(releaseData);
	}
}
