package pages.ITSM.Apex;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import pages.ITSM.InnovationSuite.InnovationSuite;
import pages.ITSM.InnovationSuite.InnovationSuiteTicketConsolePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ApexInnovationSuitePage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;
    InnovationSuite innovationSuitePage;
    InnovationSuiteTicketConsolePage innovationSuiteTicketConsolePage;

    public ApexInnovationSuitePage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(ApexInnovationSuitePage.class.getName());
        commonWaits=new CommonWaits();
        innovationSuitePage = new InnovationSuite();
        innovationSuiteTicketConsolePage = new InnovationSuiteTicketConsolePage();
    }

    @FindBy(xpath = "//input[contains(@placeholder,'Filter')]")
    WebElement apexColumnSearchBox;

    public void updateBWFKnowledgeSource() {
        log.info("Updating BWF Knowledge Source in Application Configuration");
        innovationSuitePage.navigateToBundle(SolQAConstants.SHARED_SERVICES);
        innovationSuitePage.selectRecordDefinition(SolQAConstants.APPLICATION_CONFIGURATION);
        innovationSuitePage.clickEditData(SolQAConstants.APPLICATION_CONFIGURATION);
        apexAddVisibleColumn(SolQAConstants.CONFIGURATION_NAME);
        innovationSuitePage.searchUserText(SolQAConstants.KNOWLEDGE_SOURCE);
        innovationSuitePage.selectRecord();
        if(!getValueFromRecord(SolQAConstants.DISPLAY_VALUE).equalsIgnoreCase(SolQAConstants.appBWF)) {
            innovationSuitePage.setValueOfField(SolQAConstants.DISPLAY_VALUE, SolQAConstants.appBWF);
            innovationSuitePage.selectRecord();
        }

        if(!getValueFromRecord(SolQAConstants.VALUE).equalsIgnoreCase(SolQAConstants.appBWF)) {
            innovationSuitePage.setValueOfField(SolQAConstants.VALUE, SolQAConstants.appBWF);
        }
    }

    public void apexAddVisibleColumn(String columnName) {
        log.info("Adding visible column: " + columnName);
        commonWaits.waitForElementVisible(innovationSuiteTicketConsolePage.visibleColumnsButton);
        clickWithoutInterception(innovationSuiteTicketConsolePage.visibleColumnsButton);
        if(commonWaits.isElementPresent(innovationSuiteTicketConsolePage.columnSearchBox)) {
            innovationSuiteTicketConsolePage.columnSearchBox.clear();
            innovationSuiteTicketConsolePage.columnSearchBox.sendKeys(columnName);
        } else {
            apexColumnSearchBox.clear();
            apexColumnSearchBox.sendKeys(columnName);
        }
        if(isElementPresent(By.xpath(String.format(innovationSuiteTicketConsolePage.columnNameUnhecked, columnName)))) {
            log.info("Column " + columnName + " is not checked, checking it now.");
            clickWithoutInterception(By.xpath(String.format(innovationSuiteTicketConsolePage.columnNameUnhecked, columnName)));
        }
    }

    public String getValueFromRecord(String labelName) {
        String value = findElement(By.xpath(String.format(innovationSuitePage.ValueField, labelName))).getAttribute(SolQAConstants.ATTRIBUTE_VALUE);
        return value;
    }

}
