package pages.ITOM.Multiservice;

import framework.BasePage;
import org.testng.Assert;
import pages.ITOM.Api.Events_API;
import util.CommonWaits;
import java.io.IOException;

public class ServiceMonitoringFeatureMethods extends BasePage {

    ServiceMonitoringPage serviceMonitoringPage = new ServiceMonitoringPage();
    Events_API eventsAPI = new Events_API();
    CommonWaits commonWaits = new CommonWaits();

    public ServiceMonitoringFeatureMethods verifyLogin() {
        serviceMonitoringPage.verifyLogin();
        return this;
    }

    public ServiceMonitoringFeatureMethods navigateToSituations() {
        serviceMonitoringPage.navigateToSituations();
        return this;
    }

    public ServiceMonitoringFeatureMethods createSituationUsingAPI(String jsonFile) {
        eventsAPI.createSituation(jsonFile);
        return this;
    }

    public ServiceMonitoringFeatureMethods removeExistingAdvancedFilters() {
        serviceMonitoringPage.removeExistingAdvancedFilters();
        return this;
    }

    public ServiceMonitoringFeatureMethods searchSituation(String situationName) {
        serviceMonitoringPage.searchSituation(situationName);
        return this;
    }

    public ServiceMonitoringFeatureMethods openSituation(String situationName) {
        serviceMonitoringPage.openSituation(situationName);
        return this;
    }

    public ServiceMonitoringFeatureMethods verifySituationDetails() {
        serviceMonitoringPage.verifySituationDetails();
        return this;
    }

    public ServiceMonitoringFeatureMethods verifySituationStatus(String situationName, String expectedStatus) {
        Assert.assertEquals(serviceMonitoringPage.getSituationStatus(situationName), expectedStatus, "Situation status does not match the expected status.");
        return this;
    }

    public ServiceMonitoringFeatureMethods closeSituation(String situationName) {
        serviceMonitoringPage.closeSituation(situationName);
        return this;
    }

    public ServiceMonitoringFeatureMethods setFilters(String filterType, String filterValue) {
        serviceMonitoringPage.setFilters(filterType, filterValue);
        return this;
    }

    public ServiceMonitoringFeatureMethods navigateToListView() {
        serviceMonitoringPage.navigateToListView();
        return this;
    }

    public void captureScreenshot(String ssName, String app){
        try {
            commonWaits.captureScreenshot(ssName, app);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}