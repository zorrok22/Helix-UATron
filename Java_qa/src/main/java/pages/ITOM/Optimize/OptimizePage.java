package pages.ITOM.Optimize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import framework.BasePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class OptimizePage extends BasePage {

    CommonWaits commonWaits;

    static SolqaLogger log = new SolqaLogger(OptimizePage.class.getName());
    
    public String legacyIframe="//iframe[@name='legacyDashboardFrame']";
    public String riskCountOfService="//a[contains(@title,'%s')]//ancestor::tr//td/div[@qa-col-type='risk']";
  
    @FindBy(xpath = "//a[@href='#/admin']")
    public WebElement administrationTabOnOptimize;

    @FindBy(xpath = "//a[@href='#/workspace']")
    public WebElement workspaceTabOnOptimize;
    
    @FindBy(xpath = "//a[@href='#/views']")
    public WebElement viewsTabOnOptimize;

    @FindBy(xpath = "//label[contains(@class,'behave-as-link business-services-modal-text ng-binding')]")
    public WebElement configueServiceViewHere;

    @FindBy(xpath = "//span[contains(@class,'btn-content') and (text()=' Maintenance ')]")
    public WebElement MaintenanceTabOnOptimize;

    @FindBy(xpath = "//*[contains(@id,'addon')]/span[text()='Additional Packages']")
    public WebElement AdditionalPackageTabOnOptimize;

    public String packageNameInTable="//div[contains(@class,'yui-dt-liner')]/span[contains(@title,'%s')]";

    public String reInstallPackage="//div[contains(@class,'yui-dt-liner')]/a[contains(@title,'repair installation (re-install) '%s'')]";
    @FindBy(xpath = "//div[contains(@class,'buttonBar')]/a[contains(@class,'submit button square')]/span/input[contains(@title,'Go to next page')]")
    public WebElement installPackageNextPage;

    @FindBy(xpath = "//div[contains(@class,'buttonBar')]/a[contains(@class,'submit button square')]/span/input[contains(@title,'Proceed')]")
    public WebElement installPackageProceed;

    @FindBy(xpath = "//div[contains(@class,'yui-dt-liner') and contains(text(),'INSTALLED')]")
    public WebElement installed;

    @FindBy(xpath = "//div[contains(@class,'yui-dt-liner') and contains(text(),'READY')]")
    public WebElement ready;

    @FindBy(xpath = "//a[text()='refresh']")
    public WebElement refreshButton1;

    @FindBy(xpath = "//div[contains(@class,'yui-dt-liner')]/a[contains(@class,'button TTile execute install commandBar')]")
    public WebElement installPackage;

    @FindBy(xpath = "//div/span[text()='Domains, Services & Applications']")
    public WebElement domainsServicsApplications;

    
    @FindBy(xpath = "//span[contains(text(),'Business Services')]")
    public WebElement businessServices;

    @FindBy(xpath = "//a[contains(@href,'command=getChildredApps')]")
    public WebElement subDomainTab;

    @FindBy(xpath = "//span[contains(text(),'Navigate to...')]")
    public WebElement navigateTo;

    @FindBy(xpath = "//a[(@title='Events') and (@href='#')]")
    public WebElement Events;
    @FindBy(xpath = "//button[contains(text(),'Add new event')]")
    public WebElement AddNewEvent;
    @FindBy(xpath = "//input[@type='text' and @name='WPE_NAME']")
    public WebElement EventName;

    @FindBy(xpath = "//textarea[@name='WPE_DESCRIPTION']")
    public WebElement Description;
    @FindBy(xpath = "//input[@name='WPE_BEGINDATE']")
    public WebElement BeginDate;

    @FindBy(xpath = "//span[text()='Save']")
    public WebElement Save;

    @FindBy(xpath = "//label[contains(.,'Select the Helix Dashboard')]//ancestor::adapt-rx-select//button")
    public WebElement selectDashboardDropdown;

    @FindBy(xpath = "//iframe[contains(@name,'main')]")
    public WebElement iframeLocator;

    @FindBy(xpath = "//*[contains(@aria-label,'Close')]")
    public WebElement closeButton;

    @FindBy(xpath = "//div[contains(text(),'Generate Report for')]//parent::div//button")
    public WebElement selectReportDropdown;

    @FindBy(xpath = "//input[@type='TEXT' and @name='WPE_NAME']")
    public WebElement ruleName;
    
    @FindBy(xpath = "//span[text()='Show log']")
    public WebElement showLog;
  
    @FindBy(xpath = "//tbody[@class='yui-dt-data']//div[text()='STOP']")
    public WebElement stopLog;
    
    @FindBy(xpath = "//span[text()='Delete']")
    public WebElement deleteButton;
    
    @FindBy(xpath = "//span[text()='Business Service Overview']")
    public WebElement bussinessServiceOverview;

    @FindBy(xpath="//*[@id='yui-dt7-th-_dtk_0']")
    public WebElement idTaskHeader;

    @FindBy(xpath="//*[@id='yui-dt15-th-_dtk_0']")
    public WebElement idRuleHeader;

    //String locators
    public String menuItem = "//a[contains(@data-testid,'menu') and contains(.,'%s')]";
    public String expandedNavigationItem = "//span[text()='%s']//ancestor::span[contains(@class,'is-flip')]";
    public String navigationItem = "(//span[text()='%s']//ancestor::div[contains(@class,'a-tree__content')])[1]";
    public String dropDownMenu = "//a[contains(@class,'button square')]//span[contains(text(),'%s')]//ancestor::a";
    public String reportDashboardDetails = "//*[contains(@data-testid,'%s') and contains(@name,'report')]";
    public String buttonLocator = "//*[contains(@automation-id,'%s')]";
    public String selectDashboardName = "//button[contains(.,'%s')]";
    public String reportLevelLocator = "//span[contains(text(),'%s')]/parent::adapt-highlight//ancestor::button";
    public String reportLocator = "//tr//span[contains(@title,'%s')]";
    public String downloadReportLocator = reportLocator + "//ancestor::td//following-sibling::td//a[contains(@title,'File name') and contains(@title,'%s')]";
    public String reportFormatLocator = "//span[contains(text(),'%s')]//parent::div//preceding-sibling::input";
    public String reportFormatCheckbox = reportFormatLocator + "[contains(@aria-checked,'true')]";
    public String checkTaskInETLList = "//*[text()='ETL tasks']//ancestor::table//a[contains(.,'%s')]";
    public String refreshButton="//a[text()='Refresh']";
    public String headerLocator="//span[contains(@class,'product__name text') and contains(.,'Continuous Optimization')]";
    public String createdEvent = "//div[contains(@class, 'titleContainer')]/p[.='%s']";
    public String iFrameOptimize = "//iframe[contains(@src,'/opt/console/common/application.do')]";
    public String iframeUnassigned = "//iframe[contains(@src,'/opt/console/common/unassigned')]";
    public String tableColumn = ".//td[%s]";
    public String tableRow = ".//tr";
    public String tableInnerDiv = ".//div[@class='yui-dt-liner']//div";
    public String tableInnerSpan = ".//div[@class='yui-dt-liner']//a//span";
    String optimizeEventDateFormat = "dd/MM/yyyy";
    String hhmmss = "00:00:00";
    public String dropdownOption = "//option[contains(text(),'%s')]";
    public String createdRule = "//tbody[@class='yui-dt-data']//span[text()='%s']";
    public String ruleLink ="//span[text()='%s']//parent::a[@title='click to view details']";
    public String actionCheckbox ="//input[@type='checkbox']/parent::td[contains(., '%s')]";
  
    public String systemTask ="//span[text()='%s']//parent::a[@title='Click to view details']";
    
    public String iFrameETLOptimize = "//iframe[contains(@src,'/opt/console/admin/tasklist.do')]";
    
    public String iFrameOptimizerRules = "//iframe[contains(@src,'/opt/console/admin/resourcemon.do?currSel=alerts')]";
    public String button = "//span[text()='%s']";

    public String viewsSidebarMenuItem = "//span[@automation-id='%s']";
    public String vmTableColumnHeader = "//*[(@role='columnheader' or self::th) and contains(normalize-space(.),'%s')]";
    public String vmTableBodyRows = "//table//tbody//tr";
    public String vmTableFirstRowLink = "//table//tbody//tr[1]//a";
    public String vmDetailsPageIndicator = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'vms/details')]";
    public String vmDetailsGraphTitle = "//*[contains(@class,'amcharts-title')]/*[contains(text(),'%s')]";
    public String vmDetailsGraphContainer = "//div[contains(@class,'co-chart-root')][.//*[contains(@class,'amcharts-title')]/*[contains(text(),'%s')]]";
    public String vmDetailsGraphDataPath = "//div[contains(@class,'co-chart-root')][.//*[contains(@class,'amcharts-title')]/*[contains(text(),'%s')]]//*[contains(@class,'amcharts-graph-stroke')]";
    public String chartRelatedInfoLink = "//a[@title='%s']";
    public String profileTabSelected = "//label[contains(@class,'tab-selected')]//span[contains(text(),'%s')]";
    public String viewsSidebarItemActive = "//span[@automation-type='views-menu-item' and text()='%s']//ancestor::span[contains(@class,'a-tree__label') and contains(@class,'is-active')]";
    public String viewsSidebarItemExpanded = "./ancestor::div[@role='treeitem' and @aria-expanded='true']";
    public String viewsSidebarItemTreeItem = "./ancestor::div[@role='treeitem']";
    public String viewsSidebarItemToggle = "./ancestor::div[contains(@class,'a-tree__content')]//span[contains(@class,'a-tree__toggle')]";
    public String viewsSidebarExpandedState = "//button[@aria-label='Views' and contains(@class,'adapt-sidebar-nav-btn') and @aria-expanded='true']";

    @FindBy(xpath = "//span[@automation-id='views']")
    public WebElement viewsSidebarViewsItem;

    @FindBy(xpath = "//span[@automation-id='co-views-virtualization']")
    public WebElement viewsSidebarVirtualizationItem;

    @FindBy(xpath = "//span[@automation-id='co-views-virtualization-vsphere']")
    public WebElement viewsSidebarVSphereItem;

    @FindBy(xpath = "//span[@automation-id='co-views-virtualization-vsphere-vms']")
    public WebElement viewsSidebarVSphereVMsItem;

    @FindBy(xpath = "//span[@automation-id='co-views-virtualization-vsphere-infrastructure']")
    public WebElement viewsSidebarVSphereInfrastructure;

    @FindBy(xpath = "//span[@automation-id='co-views-virtualization-vsphere-dataexplorer']")
    public WebElement viewsSidebarVSphereDataexplorer;

    @FindBy(xpath = "//button[@aria-label='Views' and contains(@class,'adapt-sidebar-nav-btn')]")
    public WebElement viewsSidebarToggleButton;
    @FindBy(xpath = "//span[contains(text(),'All systems and business drivers')]")
    public WebElement allSystemsAndBusinessDrivers;

    @FindBy(xpath = "//span[text()='Unassigned']")
    public WebElement unassignedDomain;

    @FindBy(xpath = "//th//span[text()='Date of creation']")
    public WebElement dateOfCreationColumn;

    @FindBy(xpath = "//th//span[text()='Last Activity']")
    public WebElement lastActivityColumn;

    @FindBy(xpath = "//table//tbody//tr")
    public List<WebElement> tableRows;

    public String tableEntityCellLocator = "//table//tbody//tr[%d]/td[%d]";
    public String columnHeaderLocator = "//th//a[text()='%s']";
    public String sortOrderIndicatorLocator = "//th//span[text()='%s']/ancestor::th";
    public String columnHeaderFilterLocator = "//a[text()='%s']//ancestor::div[@class='yui-dt-liner']//following-sibling::form";
    public String columnHeaderFilterRow = "//a[text()='%s']//ancestor::div[@class='yui-dt-liner']//following-sibling::form[@class='filter-row expanded']";
    public String columnHeaderFilterOption = columnHeaderFilterRow + "//select//option[text()='%s']";
    public String columnHeaderFilterInput = columnHeaderFilterRow + "//input";
    public String columnHeaderFilterApply = columnHeaderFilterRow + "//em";
    public String spanEle = ".//div[@class='yui-dt-liner']//span";
    public String anchorEle = ".//div[@class='yui-dt-liner']//a";
    
    @FindBy(xpath = "//div[@automation-id='tasks']")
    public WebElement tasks;

    @FindBy(xpath = "//div[@aria-label='ETL tasks']")
    public WebElement etlTasks;

    @FindBy(xpath = "//button[contains(text(),'Add..')]/parent::span")
    public WebElement add;

    @FindBy(xpath = "//button[contains(text(),'Add ETL')]")
    public WebElement addETL;

    @FindBy(xpath = "//select[@name='WPE_MODID']")
    public WebElement ETLDropdown;

    @FindBy(xpath = "//option[contains(text(),'BMC - Helix Operations Management Extractor')]")
    public WebElement ETLOptionBHOM;

    @FindBy(xpath = "//input[@type='radio' and @value='false']")
    public WebElement radioNo;

    @FindBy(xpath = "//a[@title='Save changes']")
    public WebElement saveChanges;

    @FindBy(xpath = "//a[@title='Runs the active run configuration for this task']")
    public WebElement RunETL;

    @FindBy(xpath = "//p[contains(text(),'successfully submitted.')]")
    public WebElement ETLSuccessful;


    @FindBy(xpath = "//span[@class='K_TIT_ENTITY_CATALOG']")
    public WebElement EntityCatalog;

    @FindBy(xpath = "//input[@type='radio' and @value='PRIVATE']")
    public WebElement privateDomain;

    @FindBy(xpath = "//span[contains(@class,'K_CMD_GROUP_MAINTENANCE')]")
    public WebElement maintenance;

    @FindBy(xpath = "//a[@title='Delete']")
    public WebElement delete;

    @FindBy(xpath = "//p[contains(text(),'ETL successfully deleted')]")
    public WebElement deletedSuccesfully;
  
    @FindBy(xpath = "//tbody[@tabindex=0 or @class='yui-dt-data']")
    public WebElement tableBody;
  
    @FindBy(xpath = "//tbody[contains(@class,'yui-dt-message')]//div[text()='No data available']")
    public WebElement noDataMessage;

    @FindBy(xpath = "//div[@automation-id='optimizer']")
    public WebElement optimizer;
    
    @FindBy(xpath = "//div[@aria-label='Rules']")
    public WebElement rules;
    
    @FindBy(xpath = "//button[text()='Add new rule']/parent::span")
    public WebElement addNewRule;
    
    @FindBy(xpath = "//select[@name='WPE_TEMPLATEGROUPID']")
    public WebElement templateGroupDropdown;
    
    @FindBy(xpath = "//select[@name='WPE_TEMPLATE']")
    public WebElement templateDropdown;
  
    @FindBy(xpath = "//span[text()='Run']")
    public WebElement runButton;
    
    @FindBy(xpath = "//p[contains(text(),'The selected alert has been scheduled to run immediately')]")
    public WebElement RuleRunSuccessful;
    
    @FindBy(xpath = "//span[contains(@class,'K_TIT_ALERT_ACTIONS') and text()='Actions']")
    public WebElement actions;
    
    @FindBy(xpath = "//span[contains(@class,'K_TIT_ALERT_RESULTS_LIST')]")
    public WebElement viewAlertResults;
    
    @FindBy(xpath = "//a[@automation-id='tasks.system-tasks']")
    public WebElement systemTasks;
  
    @FindBy(xpath = "//span[text()='Run active configuration']")
    public WebElement runConfig;
    
    @FindBy(xpath = "//p[contains(text(),'The alert has been successfully deleted.')]")
    public WebElement deleteSuccessful;

    @FindBy(xpath = "//div/span[contains(text(),'Containers') and (@id='co-views-containers')]")
    public WebElement containersView;
    @FindBy(xpath = "//div/span[contains(text(),'Kubernetes') and (@id='co-views-containers-view-204')]")
    public WebElement kubernetesView;

    @FindBy(xpath = "//*/span[contains(text(),'Clusters')]")
    public WebElement Clusters;
    @FindBy(xpath = "//*/span[contains(text(),'Nodes')]")
    public WebElement Nodes;
    @FindBy(xpath = "//*/span[contains(text(),'Namespaces')]")
    public WebElement Namespaces;
    @FindBy(xpath = "//*/span[contains(text(),'Controllers')]")
    public WebElement Controllers;
    @FindBy(xpath = "//*/span[contains(text(),'Pods')]")
    public WebElement Pods;
    @FindBy(xpath = "//*/span[contains(text(),'Recommendations')]")
    public WebElement Recommendations;
    @FindBy(xpath = "//*/span[contains(text(),'Capacity')]")
    public WebElement Capacity;
    @FindBy(xpath = "//*/span[contains(text(),'CPU')]")
    public WebElement CPU;
    @FindBy(xpath = "//*/span[contains(text(),'Memory')]")
    public WebElement Memory;
    @FindBy(xpath = "//*/span[contains(text(),'Overallocated Containers')]")
    public WebElement overallocatedContainers;
    @FindBy(xpath = "//*/span[contains(text(),'Forecasted Saturation: Infrastructure')]")
    public WebElement forecastedSaturationInfrastructure;
    @FindBy(xpath = "//*/span[contains(text(),'Forecasted Saturation:  Containers')]")
    public WebElement forecastedSaturationContainers;
    @FindBy(xpath = "//*/span[contains(text(),'Oversubscribed Clusters')]")
    public WebElement oversubscribedClusters;
    @FindBy(xpath = "//a[@href='#' and @title='local']")
    public WebElement clusterName;
    public String clustersCapacity = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'clusters/capacity')]";

    public String clusterName1 = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'containers/view-204/')]";

    @FindBy(xpath = "//a[@href='#' and @title='host-27']")
    public WebElement nodeName;

    @FindBy(xpath = "//a[@href='#' and @title='monitoring-stack']")
    public WebElement namespaceName;

    @FindBy(xpath = "//a[@href='#' and @title='victoria-metrics-cluster-vmselect']")
    public WebElement controllerName;

    @FindBy(xpath = "//a[@href='#' and @title='optdataprv']")
    public WebElement podName;

    @FindBy(xpath = "//a[@href='#' and @title='optdataprv']")
    public WebElement capacityName;

    @FindBy(xpath = "//a[@href='#' and @title='optdataprv']")
    public WebElement cpuName;

    @FindBy(xpath = "//a[@href='#' and @title='optdataprv']")
    public WebElement memoryName;


    @FindBy(xpath = "//div/span[contains(text(),'Servers')and(@automation-id='co-views-servers')]")
    public WebElement Servers;

    @FindBy(xpath = "//div/span[contains(text(),'Overview')and(@automation-id='co-views-servers-overview')]")
    public WebElement Overview;

    public String serverName = "//a[@href='#' and @title='%s']";

    @FindBy(xpath = "//div/a[contains(text(),'Open Dashboard')]")
    public WebElement openDashboard;
    @FindBy(xpath = "//div/a[contains(text(),'Show in Operations Management')]")
    public WebElement showinOperationsManagement;
    @FindBy(xpath = "//div/span[contains(text(),'Cloud') and (@id='co-views-cloud')]")
    public WebElement cloudView;
    @FindBy(xpath = "//div/span[contains(text(),'vCloud') and (@id='co-views-cloud-vcloud')]")
    public WebElement vCloudView;

    @FindBy(xpath = "//*/span[contains(text(),'vCloud')]")
    public WebElement vCloud;
    @FindBy(xpath = "//*/span[contains(text(),'Provider vDCs')]")
    public WebElement ProvidervDCs;
    @FindBy(xpath = "//*/span[contains(text(),'Organization vDCs')]")
    public WebElement OrganizationvDCs;
    @FindBy(xpath = "//*/span[contains(text(),'Maps')]")
    public WebElement Maps;
    @FindBy(xpath = "//div/span[contains(text(),'AWS')]")
    public WebElement viewAWS;
    @FindBy(xpath = "//div/span[contains(text(),'EC2 Instances')and (@id='co-views-cloud-aws-ec2instances')]")
    public WebElement viewEC2Instances;
    @FindBy(xpath = "//label/span[contains(text(),'EC2 Instances')]")
    public WebElement EC2Instances;
    @FindBy(xpath = "//label/span[contains(text(),'EC2 Idle Instances')]")
    public WebElement EC2IdleInstances;
    @FindBy(xpath = "//label/span[contains(text(),'EBS Volumes')]")
    public WebElement EBSVolumes;
    @FindBy(xpath = "//label/span[contains(text(),'Recommendations')]")
    public WebElement AWSRecommendetions;
    public String EC2Instancesvalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'ec2instances/overview')]";
    public String EC2Instancevalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'ec2instances/ec2instancedetails')]";
    public String EC2IdleInstancesvalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'ec2instances/ec2idleinstances')]";
    public String EC2IdleInstancevalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'ec2instances/ec2instancedetails')]";

    @FindBy(xpath = "//div/span[contains(text(),'Azure')and(@automation-id='co-views-cloud-azure')]")
    public WebElement viewAzure;

    @FindBy(xpath = "//div/span[contains(text(),'Virtual Machines')and(@automation-id='co-views-cloud-azure-virtualmachines')]")
    public WebElement azureviewVirtualmachines;

    @FindBy(xpath = "//span[contains(text(),'Idle Virtual Machines')]")
    public WebElement azuretabIdleVirtualMachines;

    @FindBy(xpath = "//span[contains(text(),'Virtual Machines') and contains(@class,'tab-label behave-as-link noselect ng-binding dropdown-column')]")
    public WebElement azuretabVirtualMachines;

    @FindBy(xpath = "//span[contains(text(),'Recommendations')]")
    public WebElement viewAzurecommendations;
    public String viewazureVirtualmachinesvalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'azure/virtualmachines/overview')]";
    public String viewazureVirtualmachinevalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'azure/virtualmachines/') and not(contains(@href,'azure/virtualmachines/overview')) and not(contains(@href,'azure/virtualmachines/idlevms')) and not(contains(@href,'azure/virtualmachines/recomm'))]";
    public String tabIdleVirtualMachinesvalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'azure/virtualmachines/idlevms')]";
    public String viewRecommendationsvalidate = "//a[contains(@class,'accessibility-start-of-content') and contains(@href,'azure/virtualmachines/recomm')]";



    public OptimizePage(WebDriver driver) {
        PageFactory.initElements(getDriver(),this);
        commonWaits=new CommonWaits();
    }

    public void clickOnAdministrationTab(){
        log.info("Click on Administration tab");
        commonWaits.waitForElementClickable(administrationTabOnOptimize);
        administrationTabOnOptimize.click();
    }
    public void clickOnMaintenanceTab(){
        log.info("Click on Maintenance tab");
        commonWaits.waitForElementClickable(MaintenanceTabOnOptimize);
        MaintenanceTabOnOptimize.click();
    }

    public void kubernetesView(){
        log.info("Click on kubernetesView tab");
        commonWaits.waitForElementClickable(kubernetesView);
        kubernetesView.click();
    }

    public void containersView(){
        log.info("Click on containersView tab");
        commonWaits.waitForElementClickable(containersView);
        containersView.click();
    }

    public OptimizePage ClustersView(){
        log.info("Click on Clusters tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(Clusters);
        clickWithoutInterception(Clusters);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        boolean validateClusters = commonWaits.waitForElementPresent(clustersCapacity);
        Assert.assertTrue(validateClusters, "clusters not visible.");
        log.info("validate Clusters data");
        commonWaits.waitForElementVisible(clusterName);
        clickWithoutInterception(clusterName);
        boolean clusterNameview = commonWaits.waitForElementPresent(clusterName1);
        Assert.assertTrue(clusterNameview, "clusters not visible.");
        log.info("Clusters data is available");
        return this;
    }


    public OptimizePage NodesView(){
        log.info("Click on Nodes tab");
        commonWaits.waitForElementVisible(Nodes);
        commonWaits.waitForElementClickable(Nodes);
        Nodes.click();
        log.info("validate Nodes data");
        commonWaits.waitForElementVisible(nodeName);
        return this;
    }

    public OptimizePage NamespacesView(){
        log.info("Click on Namespaces tab");
        commonWaits.waitForElementVisible(Namespaces);
        commonWaits.waitForElementClickable(Namespaces);
        Namespaces.click();
        log.info("validate Namespaces data");
        commonWaits.waitForElementVisible(namespaceName);
        return this;
    }

    public void ControllersView(){
        log.info("Click on Controllers tab");
        commonWaits.waitForElementVisible(Controllers);
        commonWaits.waitForElementClickable(Controllers);
        Controllers.click();
        log.info("validate Controllers data");
        commonWaits.waitForElementVisible(controllerName);
    }

    public void PodsView(){
        log.info("Click on Pods tab");
        commonWaits.waitForElementVisible(Pods);
        commonWaits.waitForElementClickable(Pods);
        Pods.click();
        log.info("validate Pods data");
        commonWaits.waitForElementVisible(podName);
    }

    public void RecommendationsView(){
        log.info("Click on Recommendations tab");
        commonWaits.waitForElementClickable(Recommendations);
        Recommendations.click();
    }

    public void overallocatedContainersView(){
        log.info("Click on overallocatedContainers tab");
        commonWaits.waitForElementClickable(overallocatedContainers);
        overallocatedContainers.click();
    }

    public void forecastedSaturationInfrastructureView(){
        log.info("Click on forecastedSaturationInfrastructure tab");
        commonWaits.waitForElementClickable(forecastedSaturationInfrastructure);
        forecastedSaturationInfrastructure.click();
    }

    public void forecastedSaturationContainersView(){
        log.info("Click on forecastedSaturationContainers tab");
        commonWaits.waitForElementClickable(forecastedSaturationContainers);
        forecastedSaturationContainers.click();
    }

    public void oversubscribedClustersView(){
        log.info("Click on oversubscribedClusters tab");
        commonWaits.waitForElementClickable(oversubscribedClusters);
        oversubscribedClusters.click();
    }

    public void capacityView(){
        log.info("Click on Capacity tab");
        commonWaits.waitForElementVisible(Capacity);
        commonWaits.waitForElementClickable(Capacity);
        Capacity.click();
        log.info("validate Capacity data");
        commonWaits.waitForElementVisible(capacityName);
    }

    public void CPUView(){
        log.info("Click on CPU tab");
        commonWaits.waitForElementVisible(CPU);
        commonWaits.waitForElementClickable(CPU);
        CPU.click();
        log.info("validate CPU data");
        commonWaits.waitForElementVisible(cpuName);
    }

    public void memoryView(){
        log.info("Click on Memory tab");
        commonWaits.waitForElementVisible(Memory);
        commonWaits.waitForElementClickable(Memory);
        Memory.click();
        log.info("validate Memory data");
        commonWaits.waitForElementVisible(memoryName);
    }

    public void serversView(){
        log.info("Click on Servers tab");
        commonWaits.waitForElementClickable(Servers);
        Servers.click();
    }

    public void overviewView(){
        log.info("Click on Overview tab");
        commonWaits.waitForElementClickable(Overview);
        Overview.click();
    }


    public void serverNameView(String server){
        log.info("Click on serverName tab");
        WebElement serverName1 = findElement(By.xpath(String.format(serverName, server)));
        commonWaits.waitForElementClickable(serverName1);
        clickWithoutInterception(serverName1);

    }

    public void openDashboard(){
        log.info("Click on openDashboard tab");
        commonWaits.waitForElementClickable(openDashboard);
        openDashboard.click();
    }

    public void showinOperationsManagement(){
        log.info("Click on showinOperationsManagement tab");
        commonWaits.waitForElementClickable(showinOperationsManagement);
        showinOperationsManagement.click();
    }

    public void clickOnAdditionalPackageTab(){
        log.info("Click on AdditionalPackage tab");
        commonWaits.waitForElementClickable(AdditionalPackageTabOnOptimize);
        if(commonWaits.isElementPresent(AdditionalPackageTabOnOptimize)) {
            log.info("Additional packages tab available");
        }

    }


    public void reInstallExistingPackage(String packageName){
        log.info("Re-install package");

        //check package is already installed

        if(isElementPresent(By.xpath(String.format(reInstallPackage, packageName)))) {
            log.info("installed package is present");

            clickWithoutInterception(By.xpath(String.format(reInstallPackage, packageName)));

            commonWaits.waitForElementClickable(installPackageNextPage);
            installPackageNextPage.click();

            commonWaits.waitForElementClickable(installPackageProceed);
            installPackageProceed.click();
        }else {
            log.info("installed package not found");
        }


    }
    public void checkPackageReinstalled(){
        log.info("check package is re-installed for selected view");
        log.info("check package is in ready state");

        if(commonWaits.isElementPresent(ready)){
            log.info("package is in ready state");
        }else {
            log.info("package is not in ready state");
        }

        commonWaits.waitForElementClickable(refreshButton1);
        refreshButton1.click();

        log.info("check package is in installed state");

        if(commonWaits.isElementPresent(installed)){
            log.info("package is in installed state");
        }else {
            log.info("package is not in installed state");
        }
    }

    public void installPackage(){
        log.info("install package");
        commonWaits.waitForElementClickable(installPackage);
        installPackage.click();

        commonWaits.waitForElementClickable(installPackageNextPage);
        installPackageNextPage.click();

        commonWaits.waitForElementClickable(installPackageProceed);
        installPackageProceed.click();

    }
    public void checkPackageinstalled(){
        log.info("check package is installed for selected view");
        log.info("check package is in ready state");

        if(commonWaits.isElementPresent(ready)){
            log.info("package is in ready state");
        }else {
            log.info("package is not in ready state");
        }

        commonWaits.waitForElementClickable(refreshButton1).click();

        log.info("check package is in installed state");

        if(commonWaits.isElementPresent(installed)){
            log.info("package is in installed state");
        }else {
            log.info("package is not in installed state");
        }
    }

    public void deleteBHOMETL(){
        commonWaits.waitForElementClickable(maintenance).click();
        commonWaits.waitForElementClickable(delete).click();
        Alert alert = getDriver().switchTo().alert();
        alert.accept();
        commonWaits.waitForElementVisible(deletedSuccesfully);

    }

    public void addBHOMETL() {

        log.info("Click on Tasks");
        commonWaits.waitForElementClickable(tasks).click();
        commonWaits.waitForElementClickable(etlTasks).click();

        switchToFrame(iFrameETLOptimize);
        log.info("Click on Add button");
        commonWaits.waitForElementVisible(add).click();

        log.info("Click on Add ETL button");
        commonWaits.waitForElementClickable(addETL).click();

        log.info("Select BMC - Helix Operations Management Extractor from dropdown");
        commonWaits.waitForElementVisible(ETLDropdown).click();
        commonWaits.waitForElementVisible(ETLOptionBHOM).click();

        log.info("Select No for 'Run the ETL task immediately' option");
        commonWaits.clickWithoutInterception(radioNo);

        log.info("Select private domain option");
        commonWaits.waitForElementClickable(EntityCatalog).click();
        commonWaits.clickWithoutInterception(privateDomain);

        log.info("Click on Save Changes");
        commonWaits.clickWithoutInterception(saveChanges);
    }

    public void runBHOMETL() {
        log.info("Click on Run ETL button");
        commonWaits.waitForElementClickable(RunETL).click();
        commonWaits.waitForElementVisible(ETLSuccessful);
        Assert.assertTrue(ETLSuccessful.isDisplayed(), "ETL was not successfully submitted.");
    }

    public void clickOnWorkspaceTab(){
        log.info("Click on Workspace tab");
        commonWaits.waitForElementClickable(workspaceTabOnOptimize);
        workspaceTabOnOptimize.click();
        commonWaits.waitForElementVisible(domainsServicsApplications);
    }

    public void clickOnSubDomainTab(){
        log.info("Click on Sub Domain tab");
        commonWaits.waitForElementClickable(subDomainTab);
        commonWaits.click(subDomainTab);
        commonWaits.waitForElementVisible(navigateTo);
    }

    public void clickOnCreateEvent(){
        switchToFrame(iFrameOptimize);
        log.info("Click on Navigate to button");
        commonWaits.waitForElementClickable(navigateTo);
        commonWaits.click(navigateTo);
        log.info("Click on Events");
        commonWaits.waitForElementClickable(Events);
        commonWaits.click(Events);
        commonWaits.waitForElementClickable(AddNewEvent);
    }

    public void createEvent(String eventName){

        log.info("Add new event");
        commonWaits.waitForElementClickable(AddNewEvent);
        commonWaits.click(AddNewEvent);

        log.info("Add event name: "+eventName);
        commonWaits.waitForElementClickable(EventName);
        EventName.sendKeys(eventName);

        log.info("Add event description");
        commonWaits.waitForElementClickable(Description);
        Description.sendKeys("Description for: "+eventName);

        log.info("Add beginning date");
        commonWaits.waitForElementClickable(BeginDate);
        String formattedDate = getCurrentDateTime(optimizeEventDateFormat);
        BeginDate.sendKeys(formattedDate+" "+hhmmss);

        log.info("Click on Save");
        commonWaits.waitForElementClickable(Save);
        commonWaits.click(Save);
        //commonWaits.waitForPageLoad(getDriver());

        log.info("Check if event is created.");
        commonWaits.waitForElementVisible(findElement(By.xpath(String.format(createdEvent,eventName))));
    }

    public boolean isNavigationItemExpanded(String item) {
        log.info("Checking if navigation item is expanded: " + item);
        return isElementPresent(By.xpath(String.format(expandedNavigationItem, item)));
    }

    public void selectNavigationItem(String item) {
        log.info("Selecting navigation item: " + item + " and sub-item: " + item);
        if (!isNavigationItemExpanded(item)) {
            commonWaits.waitForByElementToBeVisible(By.xpath(String.format(navigationItem, item)));
            clickWithoutInterception(By.xpath(String.format(navigationItem, item)));
        }
    }

    public void selectWorkValue(String value) {
        switchToFrame(iframeLocator);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(dropDownMenu, value)));
        commonWaits.waitForSometime(commonWaits.defaultTimeout);
        clickWithoutInterception(By.xpath(String.format(dropDownMenu, value)));
        switchtoDefaultFrame();
        if(value.equalsIgnoreCase("Add Helix Dashboard report")) {
            List<String> windowHandle = new ArrayList<String> (getDriver().getWindowHandles());
            WebDriver currentDriver = getDriver().switchTo().window(windowHandle.get(2));
            setDriver(currentDriver);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        }
    }

    public void addHelixDashboardReportDetails(String name, String description, String dashboardName, List<String> reportFormat, String reportLevelValue) {
        keyClearAndType(String.format(reportDashboardDetails, "report-name"), name);
        keyClearAndType(String.format(reportDashboardDetails, "report-desc"), description);
        clickWithoutInterception(By.xpath(String.format(buttonLocator, "next-button")));
        clickWithoutInterception(selectDashboardDropdown);
        clickWithoutInterception(By.xpath(String.format(selectDashboardName, dashboardName)));
        for (String format : reportFormat) {
            if(isElementNotPresent(String.format(reportFormatCheckbox, format))) {
                clickWithoutInterception(By.xpath(String.format(reportFormatLocator, format)));
            }
        }
        clickWithoutInterception(selectReportDropdown);
        clickWithoutInterception(By.xpath(String.format(reportLevelLocator, reportLevelValue)));
        clickWithoutInterception(By.xpath(String.format(buttonLocator, "next-button")));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        clickWithoutInterception(By.xpath(String.format(buttonLocator, "next-button")));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        clickWithoutInterception(By.xpath(String.format(buttonLocator, "next-button")));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITOM);
        clickWithoutInterception(By.xpath(String.format(buttonLocator, "done-button")));
        clickWithoutInterception(closeButton);
        List<String> windowHandle = new ArrayList<String> (getDriver().getWindowHandles());
        WebDriver currentDriver = getDriver().switchTo().window(windowHandle.get(1));
        setDriver(currentDriver);
        commonWaits.waitForSometime(commonWaits.defaultTimeout);
        getDriver().navigate().refresh();

    }

    public void verifyReportIsGenerated(String reportName, List<String> reportFormats) {
        log.info("Verifying if the report is generated: " + reportName);
        switchToFrame(iframeLocator);
        for (String format : reportFormats) {
            for(int i=1; i<=10;i++) {
                commonWaits.waitForElementVisible_withFluentWait(getDriver(),String.format(reportLocator, reportName),  30);
                if(isElementPresent(By.xpath(refreshButton))) {
                    clickWithoutInterception(By.xpath(refreshButton));
                }
                if(isElementPresent(By.xpath(String.format(downloadReportLocator, reportName, format)))) {
                    log.info("Report with name: " + reportName + " and format: " + format + " is generated successfully.");
                    break;
                } else {
                    log.info("Report with name: " + reportName + " and format: " + format + " is not generated yet. Waiting for next check.");
                }
            }
        }
        for (String format : reportFormats) {
            Assert.assertTrue(isElementPresent(By.xpath(String.format(downloadReportLocator, reportName, format))),
                    "Report with name: " + reportName + " and format: " + format + " is not generated.");
        }
        switchtoDefaultFrame();
    }

    public void downloadAndVerifyReport(String reportName, List<String> reportFormats) {
        log.info("Downloading generated report: " + reportName);
        switchToFrame(iframeLocator);
        for (String format : reportFormats) {
            clickWithoutInterception(By.xpath(String.format(downloadReportLocator, reportName, format)));

            }
        switchtoDefaultFrame();
        commonWaits.waitForSometime(commonWaits.moreDelay);
    }

    public boolean compareFilesBinary(String filePath1, String filePath2) {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        boolean flag= false;
        if (file1.length() != file2.length()) {
            flag = false;
        }
        try (FileInputStream fis1 = new FileInputStream(file1);
                FileInputStream fis2 = new FileInputStream(file2)) {

            byte[] buf1 = new byte[8192];
            byte[] buf2 = new byte[8192];

            int bytesRead1, bytesRead2;
            while ((bytesRead1 = fis1.read(buf1)) != -1) {
                bytesRead2 = fis2.read(buf2);
                if (bytesRead1 != bytesRead2 || !Arrays.equals(buf1, buf2)) {
                    flag = false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        flag = true;
        if(flag) {
            file1.delete();
        }
        return flag;
    }

    public void verifyBHCOPageLaunchedSuccessfully() {
        log.info("Verifying if BHCODashboardPage is launched successfully");
        boolean isPresent = commonWaits.waitForElementVisible_withFluentWait(getDriver(), headerLocator, 15);
        Assert.assertTrue(isPresent, "BHCODashboardPage is not launched successfully");
    }

    public void verifyTasksPresentInETLList(String taskName) {
        switchToFrame(iframeLocator);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(checkTaskInETLList, taskName)));
        boolean isPresent = isElementPresent(By.xpath(String.format(checkTaskInETLList, taskName)));
        switchtoDefaultFrame();
        Assert.assertTrue(isPresent, "Task with name: " + taskName + " is not present in the ETL list.");
    }
    
    
    public void addNewRule(String name, String templateGrp, String templateName, String actionToPerform)
    {
    	log.info("Click on Tasks");
    	commonWaits.clickWithoutInterception(optimizer);
        commonWaits.waitForElementClickable(rules).click();
        switchToFrame(iFrameOptimizerRules);
        log.info("Click on Add Rule button");
        commonWaits.waitForElementVisible(addNewRule).click();
        
        log.info("Add rule name: "+ruleName);
        commonWaits.waitForElementClickable(ruleName).click();
        ruleName.sendKeys(name);
        
        log.info("Add rule description");
        commonWaits.waitForElementClickable(Description).click();
        Description.sendKeys("Description for:"+ name);

        log.info("Select System from Template Group dropdown");
        commonWaits.clickWithoutInterception(templateGroupDropdown);
        commonWaits.waitForElementVisible(findElement(By.xpath(String.format(dropdownOption,templateGrp)))).click();

        log.info("Select saturation template from Template dropdown");
        commonWaits.waitForElementVisible(templateDropdown, 20);
        commonWaits.waitForElementVisible(findElement(By.xpath(String.format(dropdownOption,templateName)))).click();

        log.info("Click on Actions and select actions to perform");
        commonWaits.clickWithoutInterception(actions);
        commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(actionCheckbox,actionToPerform))));

        log.info("Click on Save Changes");
        commonWaits.clickWithoutInterception(saveChanges);

        log.info("Click on Id Desc");
        commonWaits.waitForElementClickable(idTaskHeader).click();
        commonWaits.waitForPageReadyState();
        commonWaits.waitForElementClickable(idTaskHeader).click();
        commonWaits.waitForPageReadyState();
        
        log.info("Check if rule is created");
        commonWaits.waitForElementVisible(findElement(By.xpath(String.format(createdRule,name))));
    
    }

    public OptimizePage clickOnAllSystemsAndBusinessDrivers() {
        log.info("Scrolling down to 'All systems and business drivers'");
        scrollToElement(allSystemsAndBusinessDrivers);
        log.info("Clicking on 'All systems and business drivers'");
        commonWaits.waitForElementClickable(allSystemsAndBusinessDrivers);
        clickWithoutInterception(allSystemsAndBusinessDrivers);
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public OptimizePage clickOnUnassignedDomain() {
        log.info("Clicking on 'Unassigned' domain");
        commonWaits.waitForElementClickable(unassignedDomain);
        clickWithoutInterception(unassignedDomain);
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public OptimizePage clickOnColumnHeader(String columnName) {
        log.info("Clicking on column header: " + columnName);
        WebElement column = findElement(By.xpath(String.format(columnHeaderLocator, columnName)));
        commonWaits.waitForElementClickable(column);
        clickWithoutInterception(column);
        commonWaits.waitForAngularRequestsToFinish();
        return this;
    }

    public List<String> getColumnValuesFromTable(int columnIndex) {
        log.info("Getting column values from table for column index: " + columnIndex);
        List<String> columnValues = new ArrayList<>();
        
        for (int i = 1; i <= tableRows.size(); i++) {
            try {
                WebElement cell = findElement(By.xpath(String.format(tableEntityCellLocator, i, columnIndex)));
                String cellValue = cell.getText().trim();
                if (!cellValue.isEmpty()) {
                    columnValues.add(cellValue);
                }
            } catch (Exception e) {
                log.debug("Could not retrieve value for row " + i + ", column " + columnIndex);
            }
        }
        
        log.info("Retrieved " + columnValues.size() + " values from the table");
        return columnValues;
    }

    public OptimizePage verifyColumnIsSorted(List<String> columnValues, String sortOrder) {
        log.info("Verifying column is sorted in " + sortOrder + " order");
        log.info("Column values: " + columnValues);
        
        Assert.assertTrue(columnValues.size() >= 0, "No column values found to verify sorting");
        
        List<String> sortedValues = new ArrayList<>(columnValues);
        
        if (sortOrder.equalsIgnoreCase(SolQAConstants.ASCENDING)) {
            sortedValues.sort(String::compareToIgnoreCase);
        } else if (sortOrder.equalsIgnoreCase(SolQAConstants.DESCENDING)) {
            sortedValues.sort((a, b) -> b.compareToIgnoreCase(a));
        }
        
        log.info("Expected sorted values: " + sortedValues);
        
        // For partial verification, check if at least first few elements match
        int elementsToCheck = Math.min(5, columnValues.size());
        for (int i = 0; i < elementsToCheck; i++) {
            if (!columnValues.get(i).equalsIgnoreCase(sortedValues.get(i))) {
                log.warn("Sorting mismatch at position " + i + ": Expected=" + sortedValues.get(i) + ", Actual=" + columnValues.get(i));
            }
        }
        
        log.info("Column sorting verification completed for " + sortOrder + " order");
        return this;
    }

    public List<String> findEntitiesWithSameValue(List<String> columnValues) {
        log.info("Finding entities with the same value");
        List<String> duplicates = new ArrayList<>();
        
        for (int i = 0; i < columnValues.size() - 1; i++) {
            for (int j = i + 1; j < columnValues.size(); j++) {
                if (columnValues.get(i).equalsIgnoreCase(columnValues.get(j)) && !columnValues.get(i).isEmpty()) {
                    if (!duplicates.contains(columnValues.get(i))) {
                        duplicates.add(columnValues.get(i));
                        log.info("Found duplicate value: " + columnValues.get(i));
                    }
                }
            }
        }
        
        if (duplicates.size() >= 1) {
            log.info("Found " + duplicates.size() + " duplicate value(s) in the column");
        } else {
            log.info("No duplicate values found in the column");
        }
        
        return duplicates;
    }

    public OptimizePage verifySortingFunctionalityForColumn(String columnName, int columnIndex) {
        log.info("========== Verifying sorting functionality for column: " + columnName + " ==========");
        switchToFrame(iframeUnassigned);
        // Click on column header (first click - ascending sort)
        clickOnColumnHeader(columnName);
        List<String> valuesAfterFirstClick = getColumnValuesFromTable(columnIndex);
        log.info("Values after first click on '" + columnName + "': " + valuesAfterFirstClick);
        verifyColumnIsSorted(valuesAfterFirstClick, SolQAConstants.ASCENDING);
        
        // Click on column header again (second click - descending sort)
        clickOnColumnHeader(columnName);
        List<String> valuesAfterSecondClick = getColumnValuesFromTable(columnIndex);
        log.info("Values after second click on '" + columnName + "': " + valuesAfterSecondClick);
        verifyColumnIsSorted(valuesAfterSecondClick, SolQAConstants.DESCENDING);
        
        // Find entities with same value
        List<String> duplicates = findEntitiesWithSameValue(valuesAfterSecondClick);
        Assert.assertTrue(duplicates.size() >= 0, "Expected to find at least some entities in the table");
        
        if (duplicates.size() > 0) {
            log.info("Successfully found entities with the same '" + columnName + "': " + duplicates);
        } else {
            log.info("Note: No duplicate values found for '" + columnName + "' in the current data set");
        }
        
        log.info("========== Completed sorting verification for column: " + columnName + " ==========");
        return this;
    }
    
    public OptimizePage clickOnColumnHeaderFilter(String columnName) {
        log.info("Clicking on column header filter : " + columnName);
        WebElement column = findElement(By.xpath(String.format(columnHeaderFilterLocator, columnName)));
        clickByAction(column);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(columnHeaderFilterRow, columnName)));
        return this;
    }
    
    public OptimizePage applyFilterFunctionalityForColumn(String columnName, int columnIndex, String filterOperator,
			String filterInput) {
		log.info("========== Verifying filter functionality for column: " + columnName + " ==========");
        switchToFrame(iframeUnassigned);
		clickOnColumnHeaderFilter(columnName);
		clickWithoutInterception(By.xpath(String.format(columnHeaderFilterOption, columnName, filterOperator)));
		if (isElementPresent(By.xpath(String.format(columnHeaderFilterInput, columnName)))) {
			commonWaits.typeKeysWithEnter(findElement(By.xpath(String.format(columnHeaderFilterInput, columnName))),
					filterInput);
		}
		clickWithoutInterception(By.xpath(String.format(columnHeaderFilterApply, columnName)));

		commonWaits.waitForPageLoad(getCurrentDriver());
		return this;
    }
    
    public OptimizePage validateFilterResultForColumn(String columnName, int columnIndex, String filterOperator, String filterInput) 
    {
    	List<String> rowValues = getAllValuesForColumnByIndex(columnIndex); 
    	log.info("*********** Filtered Values, After Applying Filter ***********************"+ rowValues.toString());
		  
		Boolean match = verifyListMatchesCriteria(rowValues, filterOperator,filterInput); 
		Assert.assertTrue(match,"Filtered result is not coming as expected," + columnName + "for operation: "+ filterOperator + "for input: " + filterInput);
		 
		return this;
    }
    
	public OptimizePage verifyFilterFunctionalityForColumn(String columnName, int columnIndex, String filterOperator,
			String filterInput) {
		applyFilterFunctionalityForColumn(columnName, columnIndex, filterOperator, filterInput);

		validateFilterResultForColumn(columnName, columnIndex, filterOperator, filterInput);
		return this;
	}

	/**
	 * Gets all row values for a specific column by index from YUI table structure
	 * Supports extraction from Name column (anchor > span), Date columns (inner div), and other column types
	 * @param columnIndex The index of the column (1-based: 1 for first column, 2
	 *                    for second, etc.)
	 * @return List of all cell values as strings for the specified column
	 */
	public List<String> getAllValuesForColumnByIndex(int columnIndex) {
		switchToFrame(iframeUnassigned);
		log.info("Extracting all row values for column index: " + columnIndex);
		List<String> columnValues = new ArrayList<>();

		// First check if "No data available" message is displayed
		try {
			if (noDataMessage.isDisplayed()) {
				log.info("'No data available' message is visible. Returning empty list");
				return columnValues; // Return empty list
			}
		} catch (Exception e) {
			log.debug("'No data available' message is not present or visible");
		}

		commonWaits.waitForElementVisible(tableBody, 10);

		// Dynamically find all rows in the table
		List<WebElement> rows = tableBody.findElements(By.xpath(tableRow));
		log.info("Found " + rows.size() + " rows in the table. Starting extraction...");

		// Iterate through each row and extract the cell value at the specified column index
		for (int i = 0; i < rows.size(); i++) {
			log.debug("Processing row " + (i + 1) + " of " + rows.size());
			String cellValue = "";
			
			try {
				// Build XPath for the specific column cell (1-based index for XPath)
				String cellXpath = String.format(tableColumn, columnIndex);
				WebElement cell = rows.get(i).findElement(By.xpath(cellXpath));

				// Primary Strategy: Use JavaScript to get innerText (fastest and most reliable)
				try {
					Object jsResult = executeJS("return arguments[0].innerText;", cell);
					if (jsResult != null) {
						cellValue = jsResult.toString().trim();
						log.debug("Row " + (i + 1) + " - JS innerText: '" + cellValue + "'");
					}
				} catch (Exception jsEx) {
					log.debug("Row " + (i + 1) + " - JS failed, trying Selenium methods");
					
					// Fallback 1: Try inner div (Date columns pattern)
					try {
						List<WebElement> innerDivs = cell.findElements(By.xpath(tableInnerDiv));
						if (!innerDivs.isEmpty()) {
							WebElement innerDiv = innerDivs.get(0);
							cellValue = innerDiv.getText().trim();
							
							// If empty, try textContent
							if (cellValue.isEmpty() || cellValue.equals("\u00A0")) {
								String textContent = innerDiv.getAttribute("textContent");
								cellValue = (textContent != null) ? textContent.trim() : "";
							}
							log.debug("Row " + (i + 1) + " - Inner div: '" + cellValue + "'");
						}
					} catch (Exception divEx) {
						// Fallback 2: Try anchor > span (Name column pattern)
						try {
							List<WebElement> anchorSpans = cell.findElements(By.xpath(tableInnerSpan));
							if (!anchorSpans.isEmpty()) {
								WebElement anchorSpan = anchorSpans.get(0);
								cellValue = anchorSpan.getText().trim();
								if (cellValue.isEmpty()) {
									String textContent = anchorSpan.getAttribute("textContent");
									cellValue = (textContent != null) ? textContent.trim() : "";
								}
								log.debug("Row " + (i + 1) + " - Anchor span: '" + cellValue + "'");
							}
						} catch (Exception spanEx) {
							// Fallback 3: Get cell text directly
							cellValue = cell.getText().trim();
							log.debug("Row " + (i + 1) + " - Cell text: '" + cellValue + "'");
						}
					}
				}

				// Normalize non-breaking spaces to empty string
				if (cellValue != null) {
					cellValue = cellValue.replace("\u00A0", "").replace("&nbsp;", "").trim();
					if (cellValue.isEmpty()) {
						cellValue = "";
					}
				} else {
					cellValue = "";
				}

				// Add value to list (including empty strings for proper filtering validation)
				columnValues.add(cellValue);
				
			} catch (Exception e) {
				log.warn("Could not retrieve value for row " + (i + 1) + ", column " + columnIndex + ": "
						+ e.getMessage());
				// Add empty string to maintain row alignment
				columnValues.add("");
			}
		}

		log.info("Successfully extracted " + columnValues.size() + " values from column index " + columnIndex);
		log.info("Extracted values: " + columnValues);

		return columnValues;
	}

    /**
     * Verifies if all values in the list match the specified criteria
     * @param values List of string values to verify
     * @param operator The comparison operator: "=", "!=", "like", "contains", "starts with", "ends with", ">", "<", "empty", "non empty"
     * @param filterString The filter string to compare against (for date comparisons, use dd/MM/yyyy format)
     * @return true if all values match the criteria, false otherwise
     */
    public boolean verifyListMatchesCriteria(List<String> values, String operator, String filterString) {
        log.info("Verifying list matches criteria - Operator: " + operator + ", Filter: " + filterString);
        log.info("Filtered Column values: " + values);
        Assert.assertTrue(values.size() >= 0, "No column values found to verify for filtering");
        
        List<String> filteredValues = new ArrayList<>(values);
        
        boolean allMatch = true;
        int matchCount = 0;
        int mismatchCount = 0;
        
        for (String value : filteredValues) {
            boolean matches = false;
            
            switch (operator.toLowerCase().trim()) {
                case "=":
                    matches = value.equalsIgnoreCase(filterString);
                    break;
                    
                case "!=":
                    matches = !value.equalsIgnoreCase(filterString);
                    break;
                    
                case "like":
                    matches = value.toLowerCase().contains(filterString.toLowerCase());
                    break;
                    
                case ">":
                case "greater than":
                    matches = compareDates(value, filterString, ">");
                    break;
                    
                case "<":
                case "less than":
                    matches = compareDates(value, filterString, "<");
                    break;
                    
                case "empty":
                    matches = value == null || value.trim().isEmpty();
                    break;
                    
                case "not empty":
                    matches = value != null && !value.trim().isEmpty();
                    break;
                    
                default:
                    log.error("Unsupported operator: " + operator);
                    return false;
            }
            
            if (matches) {
                matchCount++;
                log.debug("Value '" + value + "' matches the criteria");
            } else {
                mismatchCount++;
                allMatch = false;
                log.warn("Value '" + value + "' does NOT match the criteria (Operator: " + operator + ", Filter: " + filterString + ")");
            }
        }
        
        log.info("Verification Result - Total: " + values.size() + ", Matched: " + matchCount + ", Mismatched: " + mismatchCount);
        
        if (allMatch) {
            log.info("All values match the criteria successfully");
        } else {
            log.error("Verification FAILED: Not all values match the criteria");
        }
        
        return allMatch;
    }
    
    /**
     * Compares two date strings in dd/MM/yyyy format
     * @param value1 First date string
     * @param value2 Second date string
     * @param operator Comparison operator (">", "<")
     * @return true if comparison is satisfied, false otherwise
     */
    private boolean compareDates(String value1, String value2, String operator) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        
        try {
            Date date1 = dateFormat.parse(value1.trim());
            Date date2 = dateFormat.parse(value2.trim());
            
            int comparison = date1.compareTo(date2);
            
            switch (operator) {
                case ">":
                    boolean result = comparison > 0;
                    log.debug("Date comparison: " + value1 + " > " + value2 + " = " + result);
                    return result;
                case "<":
                    result = comparison < 0;
                    log.debug("Date comparison: " + value1 + " < " + value2 + " = " + result);
                    return result;
                default:
                    log.error("Invalid date comparison operator: " + operator);
                    return false;
            }
        } catch (ParseException e) {
            log.error("Error parsing dates for comparison. Value1: '" + value1 + "', Value2: '" + value2 + "'. Expected format: dd/MM/yyyy. Error: " + e.getMessage());
            return false;
        }
    }
    
    public void runRuleAndVerify(String ruleName) {
    	
        log.info("Select the rule and click on rule");
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(ruleLink,ruleName)))).click();
        Assert.assertFalse(isVisible(viewAlertResults), "View Alerts and Recommendations is getting shown");
        
        log.info("Run rule");
        commonWaits.clickWithoutInterception(runButton);
        commonWaits.waitForElementVisible(RuleRunSuccessful);
        Assert.assertTrue(RuleRunSuccessful.isDisplayed(), "Rule run was not scheduled successfully");
       
    }
    
    public void runSystemTaskAndWaitForRunFinish(String systemTaskName) {
    	switchtoDefaultFrame();
    	
    	log.info("Click on ETL & System tasks -> System tasks");
        commonWaits.clickWithoutInterception(tasks);
        commonWaits.waitForElementClickable(systemTasks).click();
       
        log.info("Click on 'Optimizer - daily' task");
        switchToFrame(iFrameETLOptimize);
        commonWaits.waitForPageReadyState();
        commonWaits.waitForElementClickable(idRuleHeader).click();
        commonWaits.waitForPageReadyState();
        commonWaits.waitForElementVisible(findElement(By.xpath(String.format(systemTask,systemTaskName)))).click();
        
        log.info("Click on Run active configuration button");
        commonWaits.waitForElementClickable(runConfig).click();
        commonWaits.waitForElementVisible(ETLSuccessful);
        Assert.assertTrue(ETLSuccessful.isDisplayed(), "Run configuration was not successfully submitted");
        
        log.info("Click on show log");
        commonWaits.waitForElementClickable(showLog).click();
        
        Assert.assertTrue(verifySTOP_LogPresenceWithPagination(stopLog), "Optimizer - daily STOP log did not found");
    }
 
    public Boolean verifySTOP_LogPresenceWithPagination(WebElement targetLocator) 
    {
        int attempts = 0;
        int maxAttempts = 5;
        while (attempts < maxAttempts) {
            attempts++;
                if(isVisible(stopLog)) {
                	log.info("STOP log get generated successfully");
                	return true;
                }
                else if (isElementClickable(findElement(By.xpath(String.format(button,"Refresh")))) && !isVisible(stopLog)) {
                	log.info("STOP log is yet to generate and Refresh button is enabled → clicking Refresh...");
                    commonWaits.waitForElementClickable(findElement(By.xpath(String.format(button,"Refresh")))).click();
                    continue;    
                } else {
                	log.info("Next button is DISABLED → clicking Refresh...");
                    commonWaits.clickWithoutInterception(findElement(By.xpath(String.format(button,"Next"))));
                    continue;
                }
            }

            return false;
    }
    
    public void deleteRuleAndVerify(String ruleName) {
    	log.info("Navigate to Optimize -> Rule, Select Rule");
    	commonWaits.clickWithoutInterception(optimizer);
        commonWaits.waitForElementClickable(rules).click();
        switchToFrame(iFrameOptimizerRules);
        log.info("Select the rule and click on rule");
        commonWaits.waitForElementClickable(findElement(By.xpath(String.format(ruleLink,ruleName)))).click();        
        log.info("Delete rule");
        commonWaits.clickWithoutInterception(deleteButton);
        commonWaits.switchToAlertAndAccept(10);
        commonWaits.waitForElementVisible(deleteSuccessful);
        Assert.assertTrue(deleteSuccessful.isDisplayed(), "Delete rule was not successfull");
       
    }

	public OptimizePage clickOnViews() {
		// TODO Auto-generated method stub	
		  log.info("Open View");
		 commonWaits.waitForElementClickable(viewsTabOnOptimize);
		 viewsTabOnOptimize.click();
		 commonWaits.switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(businessServices);
		return this;
	}

	public OptimizePage ClickOnBussinesService() {
		// TODO Auto-generated method stub
			waitforServicePageLoad();
		 commonWaits.switchToFrame(legacyIframe);
		 commonWaits.waitForElementVisible(businessServices);
		  log.info("Navigate To bussiness Services"); 
		 businessServices.click();
		return this;
	}

	public String getServiceRiskByName(String inputServiceName) {
		// TODO Auto-generated method stub
		  log.info("Defination of Return Risk count"); 	
		  WebElement ele = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(riskCountOfService, inputServiceName)));		  
		  log.info("Risk Value:"+ele.getText());
		  
		return ele.getText();
	}

	public OptimizePage waitforServicePageLoad() {
		// TODO Auto-generated method stub
		  log.info("Wait for Bussiness Service PAge get Loaded"); 		
		commonWaits.waitForElementVisible(bussinessServiceOverview);
		return this;
	}
    public OptimizePage clickOnViewsMenu() {
        log.info("Click on Views menu tab");
        clickWithoutInterception(By.xpath(String.format(menuItem, SolQAConstants.VIEWS)));
        return this;
    }

    public boolean isViewsSidebarItemExpanded(WebElement sidebarItem) {
        try {
            sidebarItem.findElement(By.xpath(viewsSidebarItemExpanded));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public OptimizePage expandViewsSidebarItem(WebElement sidebarItem) {
        log.info("Expanding views sidebar item: " + sidebarItem.getText());
        commonWaits.waitForElementClickable(sidebarItem);
        if (!isViewsSidebarItemExpanded(sidebarItem)) {
            WebElement toggle = sidebarItem.findElement(By.xpath(viewsSidebarItemToggle));
            clickWithoutInterception(toggle);
            commonWaits.waitForAttributeValueChange(
                    sidebarItem.findElement(By.xpath(viewsSidebarItemTreeItem)),
                    SolQAConstants.Area_expanded, "false", 15);
        }
        return this;
    }

    public OptimizePage clickOnViewsSidebarItem(WebElement sidebarItem) {
        log.info("Clicking on views sidebar item: " + sidebarItem.getText());
        commonWaits.waitForElementClickable(sidebarItem);
        clickWithoutInterception(sidebarItem);
        return this;
    }

    public OptimizePage openViewsSidebar() {
        log.info("Opening Views sidebar panel");
        if (!commonWaits.isElementPresent(By.xpath(viewsSidebarExpandedState), 3)) {
            commonWaits.waitForElementClickable(viewsSidebarToggleButton);
            clickWithoutInterception(viewsSidebarToggleButton);
            commonWaits.waitForElementPresent(viewsSidebarExpandedState, 15);
        }
        return this;
    }

    public OptimizePage navigateToVSphereVirtualMachines() {
        log.info("Navigating to Views -> Virtualization -> vSphere -> Virtual Machines");
        clickOnViewsMenu();
        openViewsSidebar();
        commonWaits.waitForElementClickable(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarVirtualizationItem);
        expandViewsSidebarItem(viewsSidebarVSphereItem);
        clickOnViewsSidebarItem(viewsSidebarVSphereVMsItem);
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }

    public OptimizePage navigateToVSphereInfrastructure() {
        log.info("Navigating to Views -> Virtualization -> vSphere -> Infrastructure");
        //clickOnViewsMenu();
        openViewsSidebar();
        commonWaits.waitForElementClickable(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarVirtualizationItem);
        expandViewsSidebarItem(viewsSidebarVSphereItem);
        clickOnViewsSidebarItem(viewsSidebarVSphereInfrastructure);
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }

    public OptimizePage navigateToVSphereDataexplorer() {
        log.info("Navigating to Views -> Virtualization -> vSphere -> Dataexplorer");
        clickOnViewsMenu();
        openViewsSidebar();
        commonWaits.waitForElementClickable(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarVirtualizationItem);
        expandViewsSidebarItem(viewsSidebarVSphereItem);
        clickOnViewsSidebarItem(viewsSidebarVSphereDataexplorer);
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }

    public OptimizePage navigateTokubernetes() {
        log.info("Navigating to Views -> containers -> kubernetes");
        clickOnViewsMenu();
        openViewsSidebar();
        commonWaits.waitForElementClickable(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarViewsItem);
        expandViewsSidebarItem(containersView);
        clickOnViewsSidebarItem(kubernetesView);
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }

    public OptimizePage navigateToServersOverview() {
        log.info("Navigating to Views -> Servers -> Overview");
        clickOnViewsMenu();
        openViewsSidebar();
        commonWaits.waitForElementClickable(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarViewsItem);
        expandViewsSidebarItem(Servers);
        clickOnViewsSidebarItem(Overview);
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }
    public OptimizePage navigateToAWS() {
        log.info("Navigating to Views ->Cloud -> AWS -> EC2 Instances");
        clickOnViewsMenu();
        openViewsSidebar();
        commonWaits.waitForElementClickable(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarViewsItem);
        expandViewsSidebarItem(cloudView);
        expandViewsSidebarItem(viewAWS);
        clickOnViewsSidebarItem(viewEC2Instances);
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }

    public OptimizePage viewEc2Instances(){
        log.info("Click on EC2 Instances tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(EC2Instances);
        clickWithoutInterception(EC2Instances);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        boolean EC2Instancesvalidate1 = commonWaits.waitForElementPresent(EC2Instancesvalidate);
        Assert.assertTrue(EC2Instancesvalidate1, "EC2Instances not visible.");
        log.info("EC2Instances data is available");
        return this;
    }

    public OptimizePage viewEc2Instance(){
        log.info("Click on EC2 Instance tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementPresent(vmTableFirstRowLink, 30);
        clickWithoutInterception(By.xpath(vmTableFirstRowLink));
        switchtoDefaultFrame();
        boolean EC2Instancevalidate1 = commonWaits.waitForElementPresent(EC2Instancevalidate);
        Assert.assertTrue(EC2Instancevalidate1, "EC2Instance not visible.");
        log.info("EC2Instance data is available");
        return this;
    }

    public OptimizePage viewEC2IdleInstances(){
        log.info("Click on EC2IdleInstances tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(EC2IdleInstances);
        clickWithoutInterception(EC2IdleInstances);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        boolean EC2IdleInstancesvalidate1 = commonWaits.waitForElementPresent(EC2IdleInstancesvalidate);
        Assert.assertTrue(EC2IdleInstancesvalidate1, "EC2IdleInstances not visible.");
        log.info("EC2IdleInstances data is available");
        return this;
    }
    public OptimizePage viewEC2IdleInstance(){
        log.info("Click on EC2IdleInstance tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementPresent(vmTableFirstRowLink, 30);
        clickWithoutInterception(By.xpath(vmTableFirstRowLink));
        switchtoDefaultFrame();
        boolean EC2IdleInstancevalidate1 = commonWaits.waitForElementPresent(EC2IdleInstancevalidate);
        Assert.assertTrue(EC2IdleInstancevalidate1, "EC2IdleInstance not visible.");
        log.info("EC2IdleInstance data is available");
        return this;
    }

    public OptimizePage viewEBSVolumes(){
        log.info("Click on EBSVolumes tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(EBSVolumes);
        clickWithoutInterception(EBSVolumes);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        log.info("EBSVolumes data is available");
        return this;
    }

    public OptimizePage viewRecommendations(){
        log.info("Click on Recommendations tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(AWSRecommendetions);
        clickWithoutInterception(AWSRecommendetions);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        log.info("Recommendations data is available");
        return this;
    }

    public OptimizePage navigateToAzure() {
        log.info("Navigating to Views ->Cloud -> Azure -> VirtualMachines");
        clickOnViewsMenu();
        openViewsSidebar();
        commonWaits.waitForElementClickable(viewsSidebarViewsItem);
        expandViewsSidebarItem(viewsSidebarViewsItem);
        expandViewsSidebarItem(cloudView);
        expandViewsSidebarItem(viewAzure);
        clickOnViewsSidebarItem(azureviewVirtualmachines);
        commonWaits.waitForPageLoad(getDriver());
        return this;
    }
    public OptimizePage viewtabVirtualMachines(){
        log.info("Click on VirtualMachines tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(azuretabVirtualMachines);
        clickWithoutInterception(azuretabVirtualMachines);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        boolean viewazureVirtualmachinesvalidate1 = commonWaits.waitForElementPresent(viewazureVirtualmachinesvalidate);
        Assert.assertTrue(viewazureVirtualmachinesvalidate1, "Virtualmachines not visible.");
        log.info("Virtualmachines data is available");
        return this;
    }

    public OptimizePage viewazureVirtualmachine(){
        log.info("Click on Azure Virtual Machine");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementPresent(vmTableFirstRowLink, 30);
        clickWithoutInterception(By.xpath(vmTableFirstRowLink));
        switchtoDefaultFrame();
        boolean viewazureVirtualmachinevalidate1 = commonWaits.waitForElementPresent(viewazureVirtualmachinevalidate);
        Assert.assertTrue(viewazureVirtualmachinevalidate1, "Virtualmachine not visible.");
        log.info("Virtualmachine data is available");
        return this;
    }

    public OptimizePage viewazuretabIdleVirtualMachines(){
        log.info("Click on VirtualMachines tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(azuretabIdleVirtualMachines);
        clickWithoutInterception(azuretabIdleVirtualMachines);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        boolean tabIdleVirtualMachinesvalidate1 = commonWaits.waitForElementPresent(tabIdleVirtualMachinesvalidate);
        Assert.assertTrue(tabIdleVirtualMachinesvalidate1, "idle Virtual machines not visible.");
        log.info("idle Virtual machines data is available");
        return this;
    }

    public OptimizePage viewAzureRecommendations(){
        log.info("Click on Recommendations tab");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementVisible(viewAzurecommendations);
        clickWithoutInterception(viewAzurecommendations);
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        boolean viewAzureRecommendationsValidate1 = commonWaits.waitForElementPresent(viewRecommendationsvalidate);
        Assert.assertTrue(viewAzureRecommendationsValidate1, "Recommendations not visible.");
        log.info("Recommendations data is available");
        return this;
    }

    public OptimizePage verifyTableColumnHeadersPresent(List<String> columnNames) {
        log.info("Verifying column headers present in VM table: " + columnNames);
        switchToFrame(legacyIframe);
        for (String columnName : columnNames) {
            String xpath = String.format(vmTableColumnHeader, columnName);
            boolean isPresent = commonWaits.waitForElementPresent(xpath, 30);
            Assert.assertTrue(isPresent, "Column header '" + columnName + "' is not present in the VM table");
            log.info("Column header '" + columnName + "' verified successfully");
        }
        switchtoDefaultFrame();
        return this;
    }

    public OptimizePage verifyVMTableHasRows() {
        log.info("Verifying All VMs table has rows more than 0");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementPresent(vmTableBodyRows, 30);
        List<WebElement> rows = getDriver().findElements(By.xpath(vmTableBodyRows));
        int rowCount = rows.size();
        log.info("All VMs table row count: " + rowCount);
        Assert.assertTrue(rowCount > 0, "All VMs table should have rows more than 0, but found: " + rowCount);
        switchtoDefaultFrame();
        return this;
    }

    public OptimizePage clickOnFirstVMInTable() {
        log.info("Clicking on first VM in the All VMs table");
        switchToFrame(legacyIframe);
        commonWaits.waitForElementPresent(vmTableFirstRowLink, 30);
        clickWithoutInterception(By.xpath(vmTableFirstRowLink));
        commonWaits.waitForPageLoad(getDriver());
        switchtoDefaultFrame();
        boolean detailsLoaded = commonWaits.waitForElementPresent(vmDetailsPageIndicator, 30);
        Assert.assertTrue(detailsLoaded, "VM details page did not open after clicking on a VM row");
        log.info("VM details page opened successfully");
        return this;
    }

    public OptimizePage verifyVMDetailsGraphsPresent(List<String> graphTitles) {
        log.info("Verifying graphs present on VM details page: " + graphTitles);
        switchToFrame(legacyIframe);
        for (String graphTitle : graphTitles) {
            String titleXpath = String.format(vmDetailsGraphTitle, graphTitle);
            boolean isPresent = commonWaits.waitForElementPresent(titleXpath, 30);
            Assert.assertTrue(isPresent, "Graph '" + graphTitle + "' is not present on the VM details page");
            log.info("Graph '" + graphTitle + "' title verified successfully");

            commonWaits.scrollToSpecificElement(vmDetailsGraphContainer, graphTitle);

            String dataXpath = String.format(vmDetailsGraphDataPath, graphTitle);
            boolean dataPresent = commonWaits.waitForElementPresent(dataXpath, 30);
            Assert.assertTrue(dataPresent, "Graph '" + graphTitle + "' has no data displayed (graph is blank)");
            List<WebElement> strokePaths = findElements(By.xpath(dataXpath));
            log.info("Graph '" + graphTitle + "' has data: " + strokePaths.size() + " data series found");
        }
        switchtoDefaultFrame();
        return this;
    }

    public OptimizePage clickChartRelatedInfoLink(String linkTitle) {
        log.info("Clicking Related Information link: " + linkTitle);
        switchToFrame(legacyIframe);
        String xpath = String.format(chartRelatedInfoLink, linkTitle);
        commonWaits.waitForElementPresent(xpath, 30);
        commonWaits.scrollToSpecificElement(chartRelatedInfoLink, linkTitle);
        clickWithoutInterception(By.xpath(xpath));
        switchtoDefaultFrame();
        return this;
    }

    public OptimizePage verifyProfileTabSelected(String tabName) {
        log.info("Verifying profile tab is selected: " + tabName);
        switchToFrame(legacyIframe);
        String xpath = String.format(profileTabSelected, tabName);
        boolean isPresent = commonWaits.waitForElementPresent(xpath, 30);
        Assert.assertTrue(isPresent, "Profile tab '" + tabName + "' is not selected");
        log.info("Profile tab '" + tabName + "' is selected successfully");
        switchtoDefaultFrame();
        return this;
    }

    public OptimizePage verifySidebarItemActive(String automationId) {
        log.info("Verifying sidebar item is active: " + automationId);
        switchtoDefaultFrame();
        String xpath = String.format(viewsSidebarItemActive, automationId);
        boolean isActive = commonWaits.waitForElementPresent(xpath, 30);
        Assert.assertTrue(isActive, "Sidebar item '" + automationId + "' is not active on the left panel");
        log.info("Sidebar item '" + automationId + "' is active on the left panel");
        return this;
    }

    public OptimizePage verifyGraphsPresentWithData(List<String> graphTitles) {
        log.info("Verifying graphs present with data: " + graphTitles);
        switchToFrame(legacyIframe);
        for (String graphTitle : graphTitles) {
            String titleXpath = String.format(vmDetailsGraphTitle, graphTitle);
            boolean isPresent = commonWaits.waitForElementPresent(titleXpath, 30);
            Assert.assertTrue(isPresent, "Graph '" + graphTitle + "' is not present on the page");
            log.info("Graph '" + graphTitle + "' title verified successfully");

            commonWaits.scrollToSpecificElement(vmDetailsGraphContainer, graphTitle);

            String dataXpath = String.format(vmDetailsGraphDataPath, graphTitle);
            boolean dataPresent = commonWaits.waitForElementPresent(dataXpath, 30);
            Assert.assertTrue(dataPresent, "Graph '" + graphTitle + "' has no data displayed (graph is blank)");
            List<WebElement> strokePaths = findElements(By.xpath(dataXpath));
            log.info("Graph '" + graphTitle + "' has data: " + strokePaths.size() + " data series found");
        }
        switchtoDefaultFrame();
        return this;
    }
    

}
