package pages.ITSM.CMDB;

import java.util.Map;
import org.testng.Assert;
import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class CMDBFeatureMethods extends BasePage {

    CMDBDashboard cmdbDashboard = new CMDBDashboard();
    CommonWaits commonWaits = new CommonWaits();
    SolqaLogger log = new SolqaLogger(CMDBFeatureMethods.class.getName());

    public String createCI(Map<Object, String> cmdbTestData){
        return cmdbDashboard.createCI(cmdbTestData);
    }

    public void verifyLoginTo_CMDB() {
        cmdbDashboard.verifyLoginTo_CMDB();
    }

    public void searchCI(String searchCIAssetType, String searchCIByType, String ciName) {
        cmdbDashboard.searchCI(searchCIAssetType, searchCIByType, ciName);
    }

    public void navigateToMenu(String Menu, String MenuItem, String SubMenuItem) throws InterruptedException{
        cmdbDashboard.navigateToMenu(Menu, MenuItem, SubMenuItem);
    }

    public void runReconciliationJob(String reconciliationJob) throws InterruptedException {
        cmdbDashboard.runReconciliationJob(reconciliationJob);
    }

    public void switchToCMDBTab(String tabName) {
        commonWaits.switchToTabByTitle(tabName);
    }

    public void validateSelfHelpIconAndHelpGuides() {
        log.info("Step 4: Validate Self Help Icon is visible and click on it");
        cmdbDashboard.validateAndClickSelfHelpIcon();
        log.info("Step 4: Completed - Self Help Icon and Help Guides validation successful");
    }

    public void validateDocumentationAndVideosLinks(String expectedDocUrl) {
        
        log.info("Step 5.1: Click on 'Documentation and videos' tab");
        cmdbDashboard.clickDocumentationAndVideosTab();
        
        log.info("Step 5.2: Validate 'Documentation and videos' header is displayed");
        cmdbDashboard.validateDocumentationAndVideosHeader();
        
        log.info("Step 5.3: Click on 'BMC Helix CMDB documentation' link");
        cmdbDashboard.clickCMDBDocumentationLink();
        
        log.info("Step 5.4: Switch back to CMDB tab");
        commonWaits.switchToTabByTitle(SolQAConstants.CMDB_PORTAL_TAB_TITLE);
        
        log.info("Step 5: Completed - Documentation and Videos links validation successful");
    }

    public void verifyAnalyticsAndImprovementsPopup() {
        log.info("Step 6.1: Close Self Help Icon if opened");
        cmdbDashboard.closeSelfHelpIfOpened();
        
        log.info("Step 6.2: Click on Profile menu");
        cmdbDashboard.clickProfileMenu();
        
        log.info("Step 6.3: Click on Analytics button");
        cmdbDashboard.clickAnalyticsButton();
        
        log.info("Step 6.4: Validate Analytics popup is displayed");
        cmdbDashboard.validateAnalyticsPopupAndClose();
        
        log.info("Step 6: Completed - Analytics and Improvements popup validation successful");
    }

    public CMDBFeatureMethods addQualificationRuleAndSave(String ruleSetName, String ruleName, String qualificationValue, String className) {
        cmdbDashboard.addQualificationRuleAndSave(ruleSetName, ruleName, qualificationValue, className);
        return this;
    }

    public CMDBFeatureMethods addDatasetAndSave(String datasetName, String datasetID, String permission1, String permission2) {
        cmdbDashboard.addDatasetAndSave(datasetName, datasetID, permission1, permission2);
        return this;
    }

    public CMDBFeatureMethods createMultipleCIs(Map<Object, String> cmdbTestData, int numberOfCIs) {
        cmdbDashboard.createMultipleCIs(cmdbTestData, numberOfCIs);
        return this;
    }

    public CMDBFeatureMethods createMultipleRelationships(Map<Object, String> cmdbTestData, int numberOfRelationships) {
        cmdbDashboard.createMultipleRelationships(cmdbTestData, numberOfRelationships);
        return this;
    }

    public CMDBFeatureMethods createReconciliationJobWithRequiredActivities(Map<Object, String> cmdbTestData , String activityType, String jobName) {
        cmdbDashboard.selectActivityType(activityType);
        cmdbDashboard.navigateToTotalJobs();
        boolean isJobCreated = cmdbDashboard.checkIfJobIsCreated(jobName);
        if(isJobCreated) {
            log.info("Job " + jobName + " is already created");
            return this;
        }
        cmdbDashboard.clickOnCreateJob();
        cmdbDashboard.setTextFieldInJobCreationForm(SolQAConstants.JOB_NAME, jobName);
        cmdbDashboard.setDescriptionFieldInJobCreationForm(SolQAConstants.DESCRIPTION, cmdbTestData.get("reconciliationJobDescription"));
        cmdbDashboard.addActivityInJobCreationForm(activityType);
        cmdbDashboard.selectDatasetInActivityForm(SolQAConstants.DATASET, cmdbTestData.get("datasetID"));
        cmdbDashboard.deselectTheQualificationCheckboxInJobCreationForm();
        cmdbDashboard.clickOnSelectQualificationRulesetsButton(cmdbTestData.get("QualificationRuleset"));
        cmdbDashboard.clickOnSaveButtonInJobCreationForm();
        return this;
    }
  
    public CMDBFeatureMethods enableSmartGraphSyncSchedule() {
        cmdbDashboard.enableSmartGraphSyncSchedule();
        return this;
    }

    public CMDBFeatureMethods setSmartGraphSyncSchedule() {
        cmdbDashboard.setSmartGraphSyncSchedule();
        return this;
    }

    public CMDBFeatureMethods validateJobRunStatistics(Map<Object, String> cmdbTestData) {
        cmdbDashboard.validateJobRunStatistics(cmdbTestData);
        return this;
    }

    public CMDBFeatureMethods switchToTabByIndex(int index) {
        commonWaits.switchToTabByIndex(index);
        return this;
    }
  
    public String createNormalizationJob(Map<Object, String> cmdbTestData) {
        return cmdbDashboard.createNormalizationJob(cmdbTestData);
    }

    public String createReconciliationJob(Map<Object, String> cmdbTestData) {
        return cmdbDashboard.createReconciliationJob(cmdbTestData);
    }

    public String createNewDataset(Map<Object, String> cmdbTestData) {
        return cmdbDashboard.createNewDataset(cmdbTestData);
    }

    public String createNewDatasource(Map<Object, String> cmdbTestData, String associatedDataset) {
        return cmdbDashboard.createNewDatasource(cmdbTestData, associatedDataset);
    }

    public boolean verifyJobCreated(String jobName) {
        log.info("Verifying job created: " + jobName);
        return cmdbDashboard.verifyJobCreated(jobName);
    }
    
    public void verifyDataPresent(String datasetName) {
    	cmdbDashboard.verifyDataPresent(datasetName);
	}
    
	public void verifyDataSourcePresent(String datasetName) {
    	cmdbDashboard.verifyDataSourcePresent(datasetName);
	}
    
    public void searchDataset(String datasetName) {
		cmdbDashboard.searchDataset(datasetName);
    }

    /**
     * Verify if specific Atrium Integrator jobs are present
     * @param jobNames Array of job names to verify
     */
    public void verifyAtriumIntegratorJobsPresent(String[] jobNames) {
        log.info("Verifying Atrium Integrator jobs are present");
        for (String jobName : jobNames) {
            boolean isPresent = cmdbDashboard.isAtriumIntegratorJobPresent(jobName);
            Assert.assertTrue(isPresent, "Job " + jobName + " is not present in Atrium Integrator");
            log.info("Job verified: " + jobName);
        }
        log.info("All Atrium Integrator jobs verified successfully");
    }

    /**
     * Execute Atrium Integrator job and verify completion
     * @param jobName Name of the job to execute
     * @return Number of records processed
     */
    public String executeAtriumIntegratorJob(String jobName) {
        log.info("Executing Atrium Integrator job: " + jobName);
        cmdbDashboard.runAtriumIntegratorJob(jobName);
        String recordsProcessed = cmdbDashboard.getRecordsProcessedCount();
        log.info("Job " + jobName + " executed successfully. Records processed: " + recordsProcessed);
        return recordsProcessed;
    }

    /**
     * Verify if specific reconciliation jobs are present
     * @param jobNames Array of job names to verify
     */
    public void verifyReconciliationJobsPresent(String[] jobNames) {
        log.info("Verifying Reconciliation jobs are present");
        for (String jobName : jobNames) {
            boolean isPresent = cmdbDashboard.isAtriumIntegratorJobPresent(jobName);
            Assert.assertTrue(isPresent, "Job " + jobName + " is not present in Manage Reconciliation");
            log.info("Job verified: " + jobName);
        }
        log.info("All Reconciliation jobs verified successfully");
    }

    /**
     * Search for CI and verify it exists in CMDB
     * @param className Class name (e.g., BMC.CORE:BMC_ComputerSystem)
     * @param attributeName Attribute name (e.g., Name)
     * @param deviceName Device name to search
     */
    public void searchAndVerifyCI(String className, String attributeName, String deviceName) {
        log.info("Searching and verifying CI in CMDB");
        cmdbDashboard.searchCIByClassAndAttribute(className, attributeName, deviceName);
        boolean isDisplayed = cmdbDashboard.isCISearchResultDisplayed(deviceName);
        Assert.assertTrue(isDisplayed, "CI with device name " + deviceName + " is not found in search results");
        log.info("CI verified successfully in CMDB: " + deviceName);
    }

    public CMDBFeatureMethods navigateToCMDBURLInNewOpenedTab() {
        cmdbDashboard.navigateToCMDBURLInNewOpenedTab();
        return this;
    }

    public CMDBFeatureMethods enableEditMode() {
        cmdbDashboard.enableEditMode();
        return this;
    }

    public CMDBFeatureMethods rightClickOnCINode() {
        cmdbDashboard.rightClickOnCINode();
        return this;
    }

    public CMDBFeatureMethods clickOnOptionFromRightClickOfCI(String optionText) {
        cmdbDashboard.clickOnOptionFromRightClickOfCI(optionText);
        return this;
    }

    public CMDBFeatureMethods verifyMarkAsDelete() {
        cmdbDashboard.verifyMarkAsDelete();
        return this;
    }

    public CMDBFeatureMethods clickMidTierButtonByText(String buttonText) {
        cmdbDashboard.clickMidTierButtonByText(buttonText);
        return this;
    }

    public CMDBFeatureMethods setMidTierFieldValueById(String fieldId, String value) {
        cmdbDashboard.setMidTierFieldValueById(fieldId, value);
        return this;
    }

    public String getMidTierFieldValueById(String fieldId) {
        return cmdbDashboard.getMidTierFieldValueById(fieldId);
    }

    public CMDBFeatureMethods runNormalizationJob(String jobName, String normalizeType) {
        cmdbDashboard.runNormalizationJob(jobName, normalizeType);
        return this;
    }

    public CMDBFeatureMethods createCatalogMapping(Map<Object, String> cmdbTestData, String discoveredProductName) {
        cmdbDashboard.createCatalogMapping(cmdbTestData, discoveredProductName);
        return this;
    }

    public String getMidTierTextFieldValue(String fieldLabel) {
        return cmdbDashboard.getMidTierTextFieldValue(fieldLabel);
    }

    public CMDBFeatureMethods createCIInComputerSystemForm(Map<Object, String> cmdbTestData) {
        cmdbDashboard.createCIInComputerSystemForm(cmdbTestData);
        return this;
    }

    public CMDBFeatureMethods setDatasetConfiguration(String datasetID, Map<Object, String> configValues) {
        cmdbDashboard.setDatasetConfiguration(datasetID, configValues);
        return this;
    }

    public CMDBFeatureMethods verifyDatasetPresent(String datasetName) {
        cmdbDashboard.verifyDataPresent(datasetName);
        return this;
    }

    public CMDBFeatureMethods verifyDataSourceNamePresent(String dataSourceName) {
        cmdbDashboard.verifyDataSourceNamePresent(dataSourceName);
        return this;
    }
}
