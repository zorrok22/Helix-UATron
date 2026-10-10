package ITSM.SmartIT;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import framework.Launcher;
import pages.ITSM.ITSM.ITSMFeatureMethods;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import util.SolQAConstants;
import util.SolqaLogger;

public class MAT_BeforeSuite extends Launcher{
	  SolqaLogger log = new SolqaLogger(MAT_BeforeSuite.class.getName());
	  WebDriver ITSMdriver, smartITdriver;
	  ITSMFeatureMethods itsmFeatureMethods;
	  InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	  private static final String[] COMPONENT_MENU_SMARTIT = {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
	
	
	@BeforeSuite (alwaysRun = true)
	public MAT_BeforeSuite disableSharedTicketConsole() {
		try {
		    log.info("--------- Disabling Shared ticket console ---------");
	        ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	        setDriver(ITSMdriver);
	        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
	        itsmFeatureMethods = new ITSMFeatureMethods();

	        itsmFeatureMethods.navigateToCentralizedConfiguration();
	        itsmFeatureMethods.selectMenuItemFromComponentMenu(COMPONENT_MENU_SMARTIT);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.FALSE);
		}
		
		 catch (Throwable th){
	            th.printStackTrace();
	        }
		   return this;
	}
	
	@BeforeSuite (alwaysRun = true)
	public MAT_BeforeSuite disableISNavbar(ITestContext context) {
		try {
		  log.info("AR System Administrator Console > Login to AR System Administrator and navigate to System → General → CCS configuration under com.bmc.arsys.itsm. > Check the following values: > EnableNetOps=true > Change_Impact_Analyzer_Option=both");
		     ITSMdriver = launchWeb(SolQAConstants.appITSM, SolQAConstants.appITSM);
	        setDriver(ITSMdriver);
	        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appITSM);
	        log.info("Logged in user (ITSM/AR): " + configProp.get("adminUserName"));
	        itsmFeatureMethods = new ITSMFeatureMethods();

	        itsmFeatureMethods.navigateToCentralizedConfiguration();
	        log.info(" Log in to ITSM and go to centralized configuration and enable the enableISNavbar, EnableISTicketConsole, enableISAssetConsole, enableSharedTicketConsole, PV enabled settings.");
	        String[] componentMenu1 = new String[] {SolQAConstants.ITSM_SMARTIT_COMPONENT, "*"};
	        itsmFeatureMethods.selectMenuItemFromComponentMenu(componentMenu1);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_NAVBAR, SolQAConstants.False);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_IS_ASSET_CONSOLE, SolQAConstants.False);
	        itsmFeatureMethods.centralizedConfigurationSetting(SolQAConstants.ENABLE_SHARED_TICKET_CONSOLE, SolQAConstants.False);
	        itsmFeatureMethods.setSelfService_PV(true, configProp.get("ARServerAlias"));
	        itsmFeatureMethods.closeBrowser();

	        log.info(": Enable Advanced Responsive Navigation in ITSM Application Settings (Innovation Studio)");
	        smartITdriver = launchWeb(SolQAConstants.appSmartIT, SolQAConstants.appSmartIT);
	        setDriver(smartITdriver);
	        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appSmartIT);
	        innovationSuiteFeatureMethodsPage = new InnovationSuiteFeatureMethodsPage();
	        innovationSuiteFeatureMethodsPage.clickOnSettings();
	        innovationSuiteFeatureMethodsPage.clickOnConfiguration(SolQAConstants.CommonConfig);
	        innovationSuiteFeatureMethodsPage.disableConfiguration(SolQAConstants.ADVANCED_RESPONSIVE_NAVIGATION);
	        smartITdriver.quit();
	        ITSMdriver.quit();
		}
		 catch (Throwable th){
	            th.printStackTrace();
	        }
		   return this;
	}

}
