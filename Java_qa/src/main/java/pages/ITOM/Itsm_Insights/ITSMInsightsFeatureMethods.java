package pages.ITOM.Itsm_Insights;

import framework.Launcher;
import pages.ITOM.Ade_Portal_Landing.Portal_FeatureMethods;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import pages.ITSM.Api.Itsm_Helper;
import util.CommonWaits;
import util.ReadPropertiesFile;
import util.SolQAConstants;
import util.SolqaLogger;
import java.io.File;
import java.util.Map;

public class ITSMInsightsFeatureMethods extends Launcher {

    public static SolqaLogger log = new SolqaLogger(ITSMInsightsFeatureMethods.class.getName());

    CommonWaits commonWaits;
    pages.ITOM.Intelligent_Automation.IntelligentAutomationPage iaPage;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");
    ITSMInsightsPage itsmInsightsPage;
    Itsm_Helper itsm_helper;
    Portal_FeatureMethods portalFeatureMethods;

    public ITSMInsightsFeatureMethods() {
        commonWaits = new CommonWaits();
        iaPage = new IntelligentAutomationPage();
        itsmInsightsPage = new ITSMInsightsPage();
        portalFeatureMethods = new Portal_FeatureMethods();
        itsm_helper = new Itsm_Helper(config.get("CMDBURL"), config.get("ADE_USERNAME"), config.get("ADE_PASSWORD"));
    }

    public void createIncidentForCluster(String JsonPath, String IncidentUniqueMessage) {
        itsm_helper.createIncidentForCluster(JsonPath, IncidentUniqueMessage);
    }

    public String addPermissionsToUserInCTMForm(String remedyLoginId, String personId, String inputGroup) throws Exception {
        return itsm_helper.addPermissionsToUserInCTMForm(remedyLoginId, personId, inputGroup);
    }

    public void deletePermission(String ID) {
        itsm_helper.deletePermissionFromForm(ID);
    }

    public String createUserInCTMForm(String JsonPath) throws Exception {
        return itsm_helper.createUserInCTMForm(JsonPath);
    }

    public void updateUserInCTMForm(String id, String JsonPath) throws Exception {
        itsm_helper.updateUserInCTMForm(id, JsonPath);
    }

    public void deleteUserInCTMForm(String id) throws Exception {
        itsm_helper.deleteUserInCTMForm(id);
    }

    public void updateLicenseForUserInCTMForm(String peopleID, String license) throws Exception {
        itsm_helper.updateLicenseForUserInCTMForm(peopleID, license);
    }

    public void updateLoginNameForUserInCTMForm(String peopleID, String newLoginName) throws Exception {
        itsm_helper.updateLoginNameForUserInCTMForm(peopleID, newLoginName);
    }

    public void updateLoginIDForUserInCTMForm(String oldLoginID, String newLoginID) throws Exception {
        itsm_helper.updateLoginIDInDataWizard(oldLoginID, newLoginID);
    }

    public void updateEmailForUserInCTMForm(String peopleID, String newEmail) throws Exception {
        itsm_helper.updateEmailForUserInCTMForm(peopleID, newEmail);
    }

    public String getEmailOfUserInCTMForm(String peopleID) throws Exception {
        return itsm_helper.getEmailOfUserInCTMForm(peopleID);
    }

    public void navigateToPPM() {
        portalFeatureMethods.navigateToPortalTab();
        setDriver(getDriver());
        portalFeatureMethods.crossLaunchProduct(SolQAConstants.HELIX_ITSM);
        itsmInsightsPage.navigateTo_ProactiveProbManagement();
    }

    public void openManageJobs() {
        itsmInsightsPage.navigateToManageJobs();
    }

    public void createOneTimeJob(String oneTimeJob) {
        itsmInsightsPage.createOneTimeJob(oneTimeJob);
    }

    public void verifyJobStatus(String oneTimeJob) {
        itsmInsightsPage.verifyJobStatus(oneTimeJob);
    }

    public void selectClusterAndRequestForProblemID() {
        itsmInsightsPage.selectClusterAndRequestForProblemID();
    }

    public void checkIfClusterIsHavingProblemCreated() {
        itsmInsightsPage.checkIfClusterIsHavingProblemCreated();
    }

}
