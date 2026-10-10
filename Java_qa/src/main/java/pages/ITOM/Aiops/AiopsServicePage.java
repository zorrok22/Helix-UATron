package pages.ITOM.Aiops;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiopsServicePage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;
    CommonWaits commonWaits = new CommonWaits();

    static SolqaLogger log = new SolqaLogger(AiopsServicePage.class.getName());

    public AiopsServicePage() {
        this.driver = getDriver();
        PageFactory.initElements(this.driver, this);
        wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
    }

    @FindBy(xpath = "//div[text()='yFiles HTML Development License']")
    WebElement yfilesDevLicenseText;

    String topology_aggregation = "//button[@id='topology-decoration-aggregation']";
    //WebElement topology_aggregation;

    @FindBy(xpath = "//span[text()='Hierarchic']")
    WebElement select_hirarchic_menu;

    @FindBy(xpath = "//button[@data-testid='topology-layout-hierarchic-leftright']")
    WebElement select_hirarchic_left_to_right;

    @FindBy(xpath = "//button[@data-testid='topology-layout-hierarchic-bottomtop']")
    WebElement select_hirarchic_bottom_to_top;

    @FindBy(xpath = "//button[@data-testid='topology-layout-hierarchic-rightleft']")
    WebElement select_hirarchic_right_to_left;

    @FindBy(xpath = "//button[@data-testid='topology-layout-hierarchic-directiontopbottom']")
    WebElement select_hirarchic_top_to_bottom;

    @FindBy(xpath = "//button[@id=\"topology-expansion-button__exit\"]")
    WebElement topology_graph_expansion_btn;

    @FindBy(xpath = "//button[@id=\"topology-expansion-button__enter\"]")
    List<WebElement> topology_enter_fullscreen_btn;

    @FindBy(xpath = "//button[@id=\"topology-expansion-button__exit\"]")
    WebElement topology_exit_fullscreen_btn;

    @FindBy(xpath = "//div[@role='tooltip']")
    WebElement topology_graph_expansion_btn_tooltip;

    @FindBy(xpath = "//button[contains(@class,'close dp-close')]")
    WebElement close_btn;

    @FindBy(xpath = "//button[@data-testid=\"topology-layout-organic\"]")
    WebElement topology_hierarchic_dropdown_options_organic;

    @FindBy(xpath = "//button[@data-testid=\"topology-layout-hierarchic\"]")
    WebElement topology_hierarchic_dropdown_options_hierarchic;

    String loader = "//div[contains(@class,'ng-busy')]";
    String refreshBtn = "//button[@id='toggle-refresh']";
    String adapt_loader_bar = "//adapt-busy//div[contains(@class, 'a-loader_bar')]";
    String serviceDetailsTab = "//button//span[contains(normalize-space(),'%s') or contains(normalize-space(),'%s')]";
    String topology_graph_zoom_btn = "//button[@id=\"topology-decoration-%s\"]";
    String selected_tab_event_count = "//button[@aria-controls='pca-node-details-tab-%s']//span[@class=\"badge badge-primary\"]";
    String topology_causal_node_count = "//topology-graph-component//*[name()='svg' and contains(@class,'yfiles-canvascomponent-svg')]//*[name()='g' and @nodeText='%s']/..//*[name()='g' and @data-internal-id='node-external-label-causalnode-count']//*[name()='text']";
    String topology_graph_node_icons_by_text = "//topology-graph-component//*[name()='svg' and contains(@class,'yfiles-canvascomponent-svg')]//*[name()='g' and contains(@nodeText, '%s') ]";
    String topology_node_expander_collapsed = "//topology-graph-component//*[name()='svg' and contains(@class,'yfiles-canvascomponent-svg')]//*[name()='g' and @nodeText='%s']//*[name()='g' and @class='node-expander collapsed']";
    String node_details_configurations = "//aiops-node-details/div//aiops-node-attribute-list/div/table/parent::div/div/parent::div//span";


    public boolean aiopsSelectTabOnServiceDetailsPage(String tabName, boolean refreshPage) {

        try {
            Map<String, String> tabAccordionMap = new HashMap<>();
            tabAccordionMap.put("Root Cause Isolation", "Analyze Root Cause");
            tabAccordionMap.put("Probable Cause", "Analyze Probable Cause");
            tabAccordionMap.put("RCA Metric", "Analyze Root Cause");
            tabAccordionMap.put("PCA Metric", "Analyze Probable Cause");

            tabAccordionMap.put("Service Hierarchy", "Service Hierarchy");
            tabAccordionMap.put("Insights", "Analyze Service Insights");
            tabAccordionMap.put("Health Indicators", "View Health Indicators");
            tabAccordionMap.put("Analyze Situations", "Analyze Situations");
            tabAccordionMap.put("CI Topology", "CI Topology");

            if (refreshPage) {
                aiopsPageRefresh();
            }

            String accordionValue = tabAccordionMap.getOrDefault(tabName, "notapplicabletest");
            click(String.format(serviceDetailsTab, tabName, accordionValue));
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean aiopsCiTopologyVerifyDevLicenses() {
        try {
            commonWaits.waitForElementClickable(yfilesDevLicenseText);
            return yfilesDevLicenseText != null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void aiopsPageRefresh() {
        try {
            commonWaits.waitForElementClickable(findElement(By.xpath(refreshBtn)));
            click(refreshBtn);
            commonWaits.waitForElementInvisible(findElement((By.xpath(adapt_loader_bar))));
        } catch (Exception e) {
            System.out.println("Unable to click on Refresh button: " + e.getMessage());
        }
    }

    public boolean aiopsCiTopologyNodePresentByKind(String kind, boolean doAggregation) {

        for (int count = 0; count < 10; count++) {
            if (doAggregation) {
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
                String aggrBtnClass = findElement(By.xpath(topology_aggregation)).getAttribute("class");
                boolean isAggrBtnSelected = aggrBtnClass.contains("btn-primary");

                if (!isAggrBtnSelected) {
                    aiopsCiTopologyClickAggregation();
                }
            }

            List<WebElement> elements = driver.findElements(By.xpath(String.format(topology_graph_node_icons_by_text, kind)));

            if (!elements.isEmpty()) {
                return true;
            } else {
                if (count < 9) {
                    driver.navigate().refresh();
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void aiopsCiTopologyClickAggregation() {
        commonWaits.waitForElementClickable(findElement(By.xpath(topology_aggregation)));
        clickToElement(findElement(By.xpath(topology_aggregation)));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
    }

    public void aiopsCiTopologyClickHierarchicAction(String hierarchicType, String hierarchicArrangement) {
        commonWaits.scrollToPageTop();
        click(select_hirarchic_menu);

        if ("Hierarchic".equals(hierarchicType)) {
            Actions actions = new Actions(driver);
            commonWaits.waitForElementClickable(topology_hierarchic_dropdown_options_hierarchic);
            actions.moveToElement(topology_hierarchic_dropdown_options_hierarchic).build().perform();

            switch (hierarchicArrangement) {
                case "Left to right":
                    actions.moveToElement(topology_hierarchic_dropdown_options_hierarchic).build().perform();
                    commonWaits.waitForElementClickable(select_hirarchic_left_to_right);
                    click(select_hirarchic_left_to_right);
                    break;
                case "Bottom to top":
                    actions.moveToElement(topology_hierarchic_dropdown_options_hierarchic).build().perform();
                    commonWaits.waitForElementClickable(select_hirarchic_bottom_to_top);
                    click(select_hirarchic_bottom_to_top);
                    break;
                case "Right to left":
                    actions.moveToElement(topology_hierarchic_dropdown_options_hierarchic).build().perform();
                    commonWaits.waitForElementClickable(select_hirarchic_right_to_left);
                    click(select_hirarchic_right_to_left);
                    break;
                case "Top to bottom":
                    commonWaits.waitForElementClickable(select_hirarchic_top_to_bottom);
                    click(select_hirarchic_top_to_bottom);
                    break;
            }
        } else {
            click(topology_hierarchic_dropdown_options_organic);
        }
    }

    public void aiopsCITopologyClickNodeExpandCollapse(String kind, boolean doAggregation) {
        try {
            String containsClass = "btn-primary";
            if (doAggregation) {
                String aggrBtnClass = findElement(By.xpath(topology_aggregation)).getAttribute("class");
                boolean isAggrBtnSelected = aggrBtnClass.contains(containsClass);

                if (!isAggrBtnSelected) {
                    aiopsCiTopologyClickAggregation();
                }
            }
            commonWaits.waitForElementClickable(findElement(By.xpath(String.format(topology_node_expander_collapsed, kind))));
            WebElement nodeElement = findElement(By.xpath(String.format(topology_node_expander_collapsed, kind)));
            if (nodeElement.isDisplayed()) {
                click(String.format(topology_node_expander_collapsed, kind));
            } else {
                System.out.println("not present");
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    public void aiopsCiTopologyResizeView(String view) {
        try {

            if (topology_enter_fullscreen_btn.isEmpty()) { // Adapt tooltip - new tooltips
                String currentView = topology_enter_fullscreen_btn.isEmpty() ? "fullscreen" : "normal";

                if (view.equals("maximize") && currentView.equals("normal")) {
                    click(topology_enter_fullscreen_btn.get(0));
                } else if (view.equals("restore") && currentView.equals("fullscreen")) {
                    click(topology_exit_fullscreen_btn);
                }
            } else {
                Actions actions = new Actions(driver);
                actions.moveToElement(topology_graph_expansion_btn);
                String tooltip = topology_graph_expansion_btn_tooltip.getText().trim();

                if (view.equals("maximize") && tooltip.equals("Enter full screen")) {
                    click(topology_enter_fullscreen_btn.get(0));
                } else if (view.equals("restore") && tooltip.equals("Exit full screen")) {
                    click(topology_exit_fullscreen_btn);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void aiopsCiTopologyCiTopologyBtn(String btn) {
        click(String.format(topology_graph_zoom_btn, btn));
    }

    public void aiopsCITopologyClickOnNodeByKind(String nodeKind) {
        try {
            log.info("Wait for loading icon to disappear");
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);

            log.info("Get web elements by locator with node kind parameter");
            getDriver().findElements(By.xpath(String.format(topology_graph_node_icons_by_text, nodeKind)));

            log.info("Click on the element");
            click(String.format(topology_graph_node_icons_by_text, nodeKind));
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    public int getNodesSelectedTabEventsCount(String tabName) {
        String recordsStr = "";
        Map<String, String> eventTypeTabDict = new HashMap<>();
        eventTypeTabDict.put("Events", "events");

        for (int i = 0; i < 10; i++) {
            try {
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
                recordsStr = commonWaits.waitForElementClickable(findElement(By.xpath(String.format(selected_tab_event_count, eventTypeTabDict.get(tabName))))).getText();
                break;
            } catch (Exception ex) {
                System.out.println("Not able to get the text of element: " + ex.getMessage());
            }
        }

        int count = 0;
        if (!recordsStr.equals("No records")) {
            if (recordsStr.contains("Records from")) {
                count = Integer.parseInt(recordsStr.split(" ")[recordsStr.split(" ").length - 1]);
            } else if (recordsStr.contains("row total")) {
                count = Integer.parseInt(recordsStr.split(" ")[0]);
            } else if (recordsStr.contains("rows total")) {
                count = Integer.parseInt(recordsStr.split(" ")[0]);
            } else {
                count = Integer.parseInt(recordsStr.split(" ")[0]);
            }
        }
        return count;
    }

    private Map<String, String> getConfigurationsFromNodeOrEventDetails(By configLocators) {
        Map<String, String> eventDict = new HashMap<>();
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(configLocators));
            List<WebElement> slots = driver.findElements(configLocators);

            for (int i = 0; i < slots.size(); i += 2) {
                String nodeDetails = slots.get(i).getText().split(":")[0];
                eventDict.put(nodeDetails, slots.get(i + 1).getText());
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
        return eventDict;
    }

    public Map<String, String> aiopsPcaGetConfigurationFromNodeDetails() {
        return getConfigurationsFromNodeOrEventDetails(By.xpath(node_details_configurations));
    }

    public void aiopsPcaCloseNodeDetailsPage() {
        commonWaits.waitForElementClickable(close_btn);
        clickToElement(close_btn);
    }

    public int aiopsCiTopologyGetCausalNodeCountByKind(String kind, boolean doAggregation) {
        try {
            String containsClass = "btn-primary";
            if (doAggregation) {
                String aggrBtnClass = findElement(By.xpath(topology_aggregation)).getAttribute("class");
                boolean isAggrBtnSelected = aggrBtnClass.contains(containsClass);

                if (!isAggrBtnSelected) {
                    aiopsCiTopologyClickAggregation();
                }
            }

            if (!commonWaits.isElementPresent(String.format(topology_causal_node_count, kind))) {
                String aggrBtnClass = findElement(By.xpath(topology_aggregation)).getAttribute("class");
                boolean isAggrBtnSelected = aggrBtnClass.contains(containsClass);

                if (!isAggrBtnSelected) {
                    aiopsCiTopologyClickAggregation();
                }
            }

            String text = findElement(By.xpath(String.format(topology_causal_node_count, kind))).getText();
            String causalNodeIntValue = text.replaceAll("[^0-9]", "");
            return Integer.parseInt(causalNodeIntValue);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage());

        }
    }
    
    public boolean aiopsCiTopologyNodePresentByKindAndType(String kindAndType, boolean doAggregation) {

        for (int count = 0; count < 5; count++) {
            if (doAggregation) {
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appAiops);
                String aggrBtnClass = findElement(By.xpath(topology_aggregation)).getAttribute("class");
                boolean isAggrBtnSelected = aggrBtnClass.contains("btn-primary");

                if (!isAggrBtnSelected) {
                    aiopsCiTopologyClickAggregation();
                }
            }
            aiopsCiTopologyCiTopologyBtn("zoomin");
            List<WebElement> elements = driver.findElements(By.xpath(String.format(topology_graph_node_icons_by_text, kindAndType)));

            if (!elements.isEmpty()) {
                return true;
            } else {
                if (count < 5) {
                	aiopsCiTopologyCiTopologyBtn("zoomin");
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
