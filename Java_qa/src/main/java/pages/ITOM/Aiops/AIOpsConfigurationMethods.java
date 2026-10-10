package pages.ITOM.Aiops;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.Launcher;
import pages.ITOM.Ade_Portal_Landing.AdePortalLandingPage;
import pages.ITOM.Api.Discovery;
import pages.ITOM.Api.Events_API;
import pages.ITOM.Api.HMEvents;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITOM.Multiservice.ServiceMonitoringPage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class AIOpsConfigurationMethods extends Launcher {

	private static SolqaLogger log = new SolqaLogger(AIOpsFeatureMethods.class.getName());
	AdePortalLandingPage adePortalLandingPagePage;
	AiopsServicePage aiopsServicePage;
	ServiceMonitoringPage serviceMonitoringPage;
	Discovery discovery;
	AIOpsPage aiOpsPage;
	CommonWaits commonWaits;
	WebDriver driver;
	Events_API eventsAPI;
	HMEvents hmEvents;
	SituationsPage situationsPage;
	AiopsServiceBlueprintPage serviceBlueprint;
	IntelligentAutomationPage iaPage;
	ServiceMonitoringPage smPage;

	public AIOpsConfigurationMethods() {
		this.driver = getDriver();
		PageFactory.initElements(this.driver, this);
		adePortalLandingPagePage = new AdePortalLandingPage(this.driver);
		aiopsServicePage = new AiopsServicePage();
		aiOpsPage = new AIOpsPage();
		serviceMonitoringPage = new ServiceMonitoringPage(this.driver);
		discovery = new Discovery();
		commonWaits = new CommonWaits();
		eventsAPI = new Events_API();
		iaPage = new IntelligentAutomationPage();
		smPage = new ServiceMonitoringPage(getDriver());
		serviceBlueprint = new AiopsServiceBlueprintPage();

		hmEvents = new HMEvents();
		situationsPage = new SituationsPage();
	}

	public void navigateToConfigurationsManageServiceBlueprints() {
		log.info("Navigate to  manage Service BluePrint");
		aiOpsPage.navigateToConfigurationsManageServiceBlueprints();
	}

	public String createNewBlueprint() {
		// TODO Auto-generated method stub
		log.info("In side Create the new blueprint");
		return serviceBlueprint.createNewBlueprint();
	}

	public void searchBlueprintByName(String bluePrintName) {
		// TODO Auto-generated method stub
		serviceBlueprint.aiopsConfigSearchBlueprintByColumnAndValue(SolQAConstants.colName, bluePrintName);
		log.info("In side Search blueprint");

	}

	public void editBlueprint(String bluePrintName) {
		// TODO Auto-generated method stub
		log.info("In side Edit the blueprint");
		serviceBlueprint.editBlueprint(bluePrintName);

	}

}