package ITSM.BWF;

import java.util.Map;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.WebDriver;
import ITSM.ITSM.ITSM_IS_SmartIT;
import framework.Launcher;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import pages.ITSM.bwf.BWFFeatureMethods;
import util.Product;
import util.ReadJson;
import util.SolQAConstants;
import util.SolqaDetails;
import util.SolqaLogger;

public class BWF_HKM  extends Launcher {

	    static SolqaLogger log = new SolqaLogger(ITSM_IS_SmartIT.class.getName());
	    ReadJson readJson = new ReadJson();
	    Map<Object, String> bwfUserData, bwfTestData, isTestData;
	    InnovationSuiteFeatureMethodsPage innovationSuiteFeatureMethodsPage;
	    BWFFeatureMethods bwfFeatureMethods;
		WebDriver bwfdriver,isDriver = null;
    
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Use ThreadLocal WebDriver management - only create one WebDriver per thread
        this.bwfdriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
        setDriver(bwfdriver);
		this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		setDriver(isDriver);
    }
	    
	    @Test(description = "CLONE - Knowledge Curator - Create KA from HelixGPT generated summary in HKM", enabled = false, groups = {"HelixGPT", "ITSM","BWF"})
	    @SolqaDetails(authorName = "shudeshp", JiraID = "DRSQA-12972",Products = {Product.Helix_ITSM,Product.Helix_BWF, Product.HelixGPT})
	    public void DRSQA_12972_CLONE_Knowledge_Curator_Create_KA_from_HelixGPT_generated_summary_in_HKM(ITestContext context) throws Exception {

	        log.info("**************** Start DRSQA_12972 CLONE - Knowledge Curator - Create KA from HelixGPT generated summary in HKM ******************");

	        log.info("****** Pre-requisite Steps ******");
			this.bwfdriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
			setDriver(bwfdriver);
			context.setAttribute("DRSQA_12972_Driver", bwfdriver);
	        context.setAttribute("DRSQA_12972_Product", Product.Helix_BWF);
	        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
	        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_12972_Data");
	        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
	        bwfFeatureMethods = new BWFFeatureMethods();
	        bwfFeatureMethods.checkKnowledgeSourceConfigValue();
	        bwfFeatureMethods.updateKnowledgeCuratorValue(SolQAConstants.TrueValue);
	        closeBrowser();

	        this.isDriver = launchWeb(SolQAConstants.InnovationStudio, SolQAConstants.appIS);
		    setDriver(isDriver);	
	        innovationSuiteFeatureMethodsPage= new InnovationSuiteFeatureMethodsPage();
	        context.setAttribute("DRSQA_12972_Driver", getDriver());
	        context.setAttribute("DRSQA_12972_Product", Product.Helix_ITSM);
	        login(configProp.get("adminUserName"), configProp.get("adminPassword"), SolQAConstants.appIS);
	        isTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appIS,"ISTestData","ComAroundDetails");
			String knowledgeManagement = isTestData.get("knowledgeManagement");

			log.info("Navigate to Administration > Knowledge Management > Knowledge Configuration and set HKM configuration");
			innovationSuiteFeatureMethodsPage.navigateToAdminPage();
			innovationSuiteFeatureMethodsPage.navigateToConfiguration(knowledgeManagement);
			innovationSuiteFeatureMethodsPage.configurationComaroundIntegrations(isTestData);
	        closeBrowser();
	        
	        log.info("****** Step1 : Launch BWF  ******");
	        this.bwfdriver = launchWeb(SolQAConstants.bundleName, SolQAConstants.appBWF);
			setDriver(bwfdriver);
	        bwfUserData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFUsers", "Elizabeth");
	        bwfTestData = readJson.getInputValuesMapIncludingLabelField(SolQAConstants.appBWF, "BWFTestData", "DRSQA_12972_Data");
	        login(bwfUserData.get("UserName"), bwfUserData.get("Password"), SolQAConstants.appBWF);
	        bwfFeatureMethods = new BWFFeatureMethods();

	        log.info("**** Step 2 : Navigating to Create New Case********");
	        bwfFeatureMethods.createCase(bwfTestData);
	        bwfFeatureMethods.captureScreenshot("DRSQA_12972_navigatedToCreateCase",SolQAConstants.appBWF);
	        bwfFeatureMethods.changeCaseStatusToResolvedWithNotes(bwfTestData);
	       
	        log.info("**** Step 3 : Open created case and create a new knowledge ********");
	        String titleName = bwfFeatureMethods.createNewKnowledge();
	        
	        log.info("**** Step 4 : Navigate to resource tab and open created article or open article in view full article ********");
	        bwfFeatureMethods.navigateToResourceTab();
	        String knowledgeID= bwfFeatureMethods.getKnowledgeID(titleName);
	        bwfFeatureMethods.navigateToActivityTab();
	        
	        log.info("**** Step 5 : Validate new article is created for the case ********");
	        bwfFeatureMethods.verifyArticleCreatedForCase(bwfTestData, knowledgeID);
	        
	    }

}
