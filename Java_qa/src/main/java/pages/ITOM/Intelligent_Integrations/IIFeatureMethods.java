package pages.ITOM.Intelligent_Integrations;

import framework.BasePage;
import framework.Launcher;
import org.openqa.selenium.WebDriver;
import pages.ITOM.Multiservice.ServiceMonitoringPage;

import java.io.IOException;

public class IIFeatureMethods extends Launcher {

    SWPIntelligentIntegration iaPage = new SWPIntelligentIntegration();
    ServiceMonitoringPage serviceMonitoringPage;
    public IIFeatureMethods() {
        serviceMonitoringPage = new ServiceMonitoringPage(getDriver());
    }

    public void verifyLogin() {
        iaPage.verifyIntelligentIntegrationPageLaunchedSuccessfully();
    }


    public void deleteSWIIConnector(String connectorName) throws IOException {
        String token = serviceMonitoringPage.getJwtToken();
        serviceMonitoringPage.delete_SWII_connector(connectorName);
    }


}
