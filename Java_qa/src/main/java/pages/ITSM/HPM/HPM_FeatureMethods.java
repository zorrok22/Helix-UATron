package pages.ITSM.HPM;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.ITSM.InnovationSuite.InnovationSuiteFeatureMethodsPage;
import util.SolQAConstants;
import java.io.IOException;
import java.util.Map;

public class HPM_FeatureMethods extends InnovationSuiteFeatureMethodsPage {

    HPM_AppsCommonOperations hpm_AppsCommonOperations = new HPM_AppsCommonOperations();

    public HPM_FeatureMethods createPortfolioRecord(String jsonFilePath, String username, String password, String summary) throws IOException {
        hpm_AppsCommonOperations.createPortfolioRecord(jsonFilePath, username, password, summary);
        return this;
    }

    public HPM_FeatureMethods createRecordUsingAPI(String jsonFilePath, String username, String password) throws IOException {
        hpm_AppsCommonOperations.createRecordUsingAPI(jsonFilePath, username, password);
        return this;
    }

    public HPM_FeatureMethods deletePortfolioRecord(String username, String password, String recordID) {
        hpm_AppsCommonOperations.deletePortfolioRecord(username, password, recordID);
        return this;
    }

    public String searchPortfolioRecord(String jsonFilePath, String username, String password, String portfolioSummary) throws IOException {
        return hpm_AppsCommonOperations.searchPortfolioRecord(jsonFilePath, username, password, portfolioSummary);
    }

    public String searchRecord(String recordType, String jsonFilePath, String username, String password, String recordSummary) throws IOException {
        return hpm_AppsCommonOperations.searchRecord(recordType, jsonFilePath, username, password, recordSummary);
    }

    public HPM_FeatureMethods deleteRecord(String recordType, String username, String password, String recordID) {
        hpm_AppsCommonOperations.deleteRecord(recordType, username, password, recordID);
        return this;
    }

    public void deleteRecord(String recordForm, WebDriver driver, String recordID) {
        hpm_AppsCommonOperations.deleteRecord(recordForm, driver, recordID);
    }

    public HPM_FeatureMethods navigateCreateHPMRecord(String recordType) {
        hpm_AppsCommonOperations.navigateCreateHPMRecord(recordType);
        return this;
    }

    public HPM_FeatureMethods createHPMRecord(String recordType, Map<Object, String> isTestData) {
        hpm_AppsCommonOperations.fillHPMRecordTemplate(recordType, isTestData);
        return this;
    }

    public String getIdeaTicketID() {
        return hpm_AppsCommonOperations.getHPMRecordTicketID();
    }

    public String getDemandTicketID() {
        return hpm_AppsCommonOperations.getHPMRecordTicketID();
    }

    public String getProjectTicketID() {
        return hpm_AppsCommonOperations.getHPMRecordTicketID();
    }

    public String getProgramTicketID() {
        return hpm_AppsCommonOperations.getHPMRecordTicketID();
    }

    public String getResourceRequestTicketID() {
        return hpm_AppsCommonOperations.getResourceRequestTicketID();
    }

    public String getProjectImportTicketID() {
        return hpm_AppsCommonOperations.getResourceRequestTicketID();
    }

    public String getPortfolioTicketID() {
        return hpm_AppsCommonOperations.getHPMRecordTicketID();
    }

    public HPM_FeatureMethods createDemandSubTickets(Map<Object, String> DemandTestData, String subTicketType) {

        switch (subTicketType) {
            case SolQAConstants.Requirement:
                hpm_AppsCommonOperations.addRequirementToDemand(DemandTestData);
                break;

            case SolQAConstants.Risk:
                hpm_AppsCommonOperations.addRiskToDemand(DemandTestData);
                break;

            case SolQAConstants.Stakeholder:
                hpm_AppsCommonOperations.addStakeholderToDemand(DemandTestData);
                break;

            default:
                Assert.fail("Invalid sub ticket type provided: " + subTicketType);
        }

        return this;
    }

    public String getProjectImportDisplayID(String projectImportSummary) {
        hpm_AppsCommonOperations.searchProjectImportTicket(projectImportSummary);
        return hpm_AppsCommonOperations.getProjectImportDisplayID();
    }

    public HPM_FeatureMethods verifyHPMLogin() {
        hpm_AppsCommonOperations.verifyHPMLogin();
        return this;
    }

    public String createRecordUsingAPI(String jsonFilePath, WebDriver driver) throws IOException {
        return hpm_AppsCommonOperations.createRecordUsingAPI(jsonFilePath, driver);
    }

    public HPM_FeatureMethods navigateProjectImport() {
        hpm_AppsCommonOperations.navigateProjectImport();
        return this;
    }

}