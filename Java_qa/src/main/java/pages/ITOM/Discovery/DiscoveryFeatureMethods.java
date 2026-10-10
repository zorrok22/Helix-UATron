package pages.ITOM.Discovery;

import framework.Launcher;
import pages.ITOM.Intelligent_Automation.IntelligentAutomationPage;
import util.SolqaLogger;

public class DiscoveryFeatureMethods extends Launcher {
    private static SolqaLogger log = new SolqaLogger(DiscoveryFeatureMethods.class.getName());
    DiscoveryPage discoveryPage;
    IntelligentAutomationPage iaPage;

    public DiscoveryFeatureMethods(){
        discoveryPage = new DiscoveryPage();
        iaPage = new IntelligentAutomationPage();
    }

    public void navigateToServicesAndApplicationSubMenu(){
        discoveryPage.clickOnModel();
        discoveryPage.clickOnServicesAndApplication();
    }

    public void goToBlueprintDefinitionPage(){
        discoveryPage.clickOnCreateAndImport();
        discoveryPage.clickOnCreateWithBlueprintButton();
        discoveryPage.clickOnManageBlueprintDefinition();
    }

    public void navigateToDiscoveryAndImportBlueprint(String filePath){
        navigateToDiscoveryFromApplicationSwitcher();
        navigateToServicesAndApplicationSubMenu();
        goToBlueprintDefinitionPage();
        discoveryPage.importBlueprint(filePath);
    }

    public DiscoveryPage navigateToDiscoveryFromApplicationSwitcher(){
        iaPage.navigateToDiscoveryPage();
        return new DiscoveryPage();
    }

    public DiscoveryFeatureMethods delete_service_model(String serviceName){
        discoveryPage.delete_service_model(serviceName);
        return this;
    }

    public void verifyLogin() {
        discoveryPage.verifyDiscoveryPageLaunchedSuccessfully();
    }

    public void CreateServiceInDiscovery(String AIOpsService,String cluster_CI){
        discoveryPage.Search_cluster_In_Discovery(cluster_CI);
        discoveryPage.Create_Model_And_Publish(AIOpsService);
    }

}
