package pages.ITSM.SmartIT;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;
import java.util.Map;

public class Asset extends SmartITCommonOperations {

    static SolqaLogger log = new SolqaLogger(Asset.class.getName());
    CommonWaits commonWaits;

    public Asset() {
        PageFactory.initElements(getDriver(), this);
        commonWaits = new CommonWaits();
    }

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Record outage')]")
    WebElement recordOutageButton;
    @FindBy(xpath = "//table[contains(normalize-space(.), 'Outage details')]//*[@data-testid='adapt-table_column-0_row-0']//adapt-row-selection-checkbox")
    WebElement outageDetailsExpandRowButton;
    @FindBy(xpath = "(//table[contains(normalize-space(.), 'Outage details')]//adapt-row-selection-checkbox)[last()]")
    WebElement outageDetailsSelectRowCheckbox;
    @FindBy(xpath = "//button[@id='304446901' and @aria-label='Delete']")
    WebElement outageDetailsDeleteButton;
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Yes')]")
    WebElement yesButton;

    public Asset createAsset(Map<Object, String> smartITTestData) {
        waitForCreateAndViewPageLoaded();
        if (smartITTestData == null || smartITTestData.isEmpty()) {
            return this;
        }
        if (smartITTestData.get("AssetType") != null) {
            setTypeaheadField(SolQAConstants.TYPE, smartITTestData.get("AssetType"));
        }
        if (smartITTestData.get("AssetName") != null) {
            setTextField(SolQAConstants.Asset_name_Label, smartITTestData.get("AssetName"));
        }
        if (smartITTestData.get("AssetDescription") != null) {
            setTextAreaField(SolQAConstants.Asset_description_Label, smartITTestData.get("AssetDescription"));
        }
        if (smartITTestData.get("CIID") != null) {
            setTextField(SolQAConstants.CIID_Label, smartITTestData.get("CIID"));
        }
        if (smartITTestData.get("TagNumber") != null) {
            setTextField(SolQAConstants.Tag_number_Label, smartITTestData.get("TagNumber"));
        }
        if (smartITTestData.get("SerialNumber") != null) {
            setTextField(SolQAConstants.Serial_number_Label, smartITTestData.get("SerialNumber"));
        }
        if (smartITTestData.get("PartNumber") != null) {
            setTextField(SolQAConstants.Part_number_Label, smartITTestData.get("PartNumber"));
        }
        if (smartITTestData.get("SystemRole") != null) {
            setTextField(SolQAConstants.System_role_Label, smartITTestData.get("SystemRole"));
        }
        if (smartITTestData.get("Hostname") != null) {
            setTextField(SolQAConstants.Hostname_Label, smartITTestData.get("Hostname"));
        }
        if (smartITTestData.get("Domain") != null) {
            setTextField(SolQAConstants.Domain_Label, smartITTestData.get("Domain"));
        }
        if (smartITTestData.get("Floor") != null) {
            setTextField(SolQAConstants.Floor_Label, smartITTestData.get("Floor"));
        }
        if (smartITTestData.get("InvoiceNumber") != null) {
            setTextField(SolQAConstants.Invoice_number_Label, smartITTestData.get("InvoiceNumber"));
        }
        if (smartITTestData.get("BudgetCode") != null) {
            setTextField(SolQAConstants.Budget_code_Label, smartITTestData.get("BudgetCode"));
        }
        if (smartITTestData.get("ProjectNumber") != null) {
            setTextField(SolQAConstants.Project_number_Label, smartITTestData.get("ProjectNumber"));
        }
        if (smartITTestData.get("Status") != null) {
            setDropdownField(SolQAConstants.Status_Label, smartITTestData.get("Status"));
        }
        if (smartITTestData.get("Impact") != null) {
            setDropdownField(SolQAConstants.Impact_Label, smartITTestData.get("Impact"));
        }
        if (smartITTestData.get("Urgency") != null) {
            setDropdownField(SolQAConstants.Urgency_Label, smartITTestData.get("Urgency"));
        }
        if (smartITTestData.get("PrimaryCapability") != null) {
            setDropdownField(SolQAConstants.Primary_capability_Label, smartITTestData.get("PrimaryCapability"));
        }
        if (smartITTestData.get("IsVirtual") != null) {
            setDropdownField(SolQAConstants.Is_virtual_Label, smartITTestData.get("IsVirtual"));
        }
        if (smartITTestData.get("VirtualSystemType") != null) {
            setDropdownField(SolQAConstants.Virtual_system_type_Label, smartITTestData.get("VirtualSystemType"));
        }
        if (smartITTestData.get("SystemType") != null) {
            setDropdownField(SolQAConstants.System_type_Label, smartITTestData.get("SystemType"));
        }
        if (smartITTestData.get("SupplierName") != null) {
            setTypeaheadField(SolQAConstants.Supplier_Label, smartITTestData.get("SupplierName"));
        }
        if (smartITTestData.get("ManufacturerName") != null) {
            setTypeaheadField(SolQAConstants.Manufacturer_Label, smartITTestData.get("ManufacturerName"));
        }
        if (smartITTestData.get("CompanyName") != null) {
            setTypeaheadField(SolQAConstants.Company_Label, smartITTestData.get("CompanyName"));
        }
        if (smartITTestData.get("CapabilityList") != null) {
            setTypeaheadField(SolQAConstants.Capability_list_Label, smartITTestData.get("CapabilityList"));
        }
        if (smartITTestData.get("RegionName") != null) {
            setTypeaheadField(SolQAConstants.Region_Label, smartITTestData.get("RegionName"));
        }
        if (smartITTestData.get("SiteGroupName") != null) {
            setTypeaheadField(SolQAConstants.Site_group_Label, smartITTestData.get("SiteGroupName"));
        }
        if (smartITTestData.get("SiteName") != null) {
            setTypeaheadField(SolQAConstants.Site_Label, smartITTestData.get("SiteName"));
        }
        if (smartITTestData.get("CostCenterName") != null) {
            setTypeaheadField(SolQAConstants.Cost_center_Label, smartITTestData.get("CostCenterName"));
        }
        if (smartITTestData.get("AvailableDate") != null) {
            setDateField(SolQAConstants.Available_date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("AvailableDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("InstallationDate") != null) {
            setDateField(SolQAConstants.Installation_date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("InstallationDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("ReceivedDate") != null) {
            setDateField(SolQAConstants.Received_date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("ReceivedDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("ReturnDate") != null) {
            setDateField(SolQAConstants.Return_date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("ReturnDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("DisposalDate") != null) {
            setDateField(SolQAConstants.Disposal_date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("DisposalDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("PurchaseDate") != null) {
            setDateField(SolQAConstants.Purchase_date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("PurchaseDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("LastScanDate") != null) {
            setDateField(SolQAConstants.Last_scan_date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("LastScanDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("TotalPhysicalMemory") != null) {
            setTextField(SolQAConstants.Total_physical_memory_Label, smartITTestData.get("TotalPhysicalMemory"));
        }
        if (smartITTestData.get("RoomName") != null) {
            setTextField(SolQAConstants.Room_Label, smartITTestData.get("RoomName"));
        }
        clickSave();
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForPageFullyLoaded(10, 2);
        return this;
    }

    public Asset createOutage(Map<Object, String> smartITTestData) {
        commonWaits.clickWithoutInterception(recordOutageButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        handlePageLoadWaitAlertIfPresent();

        if (smartITTestData.get("UnavailabilityClass") != null) {
            setDropdownField(SolQAConstants.Unavailability_Class_Label, smartITTestData.get("UnavailabilityClass"));
        }
        if (smartITTestData.get("UnavailabilityType") != null) {
            setDropdownField(SolQAConstants.Unavailability_Type_Label, smartITTestData.get("UnavailabilityType"));
        }
        if (smartITTestData.get("OutageDescription") != null) {
            setTextAreaField(SolQAConstants.Outage_Description_Label, smartITTestData.get("OutageDescription"));
        }
        if (smartITTestData.get("ScheduledStartDate") != null) {
            setDateField(SolQAConstants.Scheduled_Start_Date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("ScheduledStartDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("ScheduledEndDate") != null) {
            setDateField(SolQAConstants.Scheduled_End_Date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("ScheduledEndDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("ActualStartDate") != null) {
            setDateField(SolQAConstants.Actual_Start_Date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("ActualStartDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("ActualEndDate") != null) {
            setDateField(SolQAConstants.Actual_End_Date_Label, getFutureDateTime(Integer.parseInt(smartITTestData.get("ActualEndDate")), "MM/dd/yyyy hh:mm:ss a"));
        }
        if (smartITTestData.get("Assignee") != null) {
            setTypeaheadField(SolQAConstants.Assignee_Label, smartITTestData.get("Assignee"));
        }
        if (smartITTestData.get("AssignmentMethod") != null) {
            setRadioField(SolQAConstants.Assignment_Method_Label, smartITTestData.get("AssignmentMethod"));
        }

        commonWaits.clickWithoutInterception(saveOutageButton);
        return this;
    }
    @FindBy(xpath = "//button[@id='304448291' and contains(normalize-space(.), 'Save')]")
    WebElement saveOutageButton;

    public Asset removeOutage() {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        handlePageLoadWaitAlertIfPresent();
        commonWaits.clickWithoutInterception(outageDetailsExpandRowButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        commonWaits.waitForElementClickable(outageDetailsDeleteButton).click();
        commonWaits.clickWithoutInterception(yesButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        handlePageLoadWaitAlertIfPresent();
        return this;
    }

    @FindBy(xpath = "//*[@class='modal-body' and text()='The status has changed to In Inventory. Please select the storage information. (ARNOTE 44599)']")
    WebElement inInventoryStatusChangeAlert;
    @FindBy(xpath = "//button[@aria-label='Close']")
    WebElement closeAlertButton;

    public Asset closeInfoAlertIfPresent() {
        if (commonWaits.waitForElementVisible_withFluentWait(getDriver(), inInventoryStatusChangeAlert, 5)) {
            commonWaits.clickWithoutInterception(closeAlertButton);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        }
        return this;
    }

    @FindBy(xpath = "//button[@id='350100001' and contains(normalize-space(.), 'Save')]")
    WebElement saveLocationButton;
    @FindBy(xpath = "//fieldset[contains(normalize-space(.), 'Select inventory location')]")
    WebElement selectInventoryLocationSection;
    String locationOptionRadioBtn = "//td[contains(normalize-space(.), '%s')]//label";
    public Asset selectInventoryLocation(String location) {
        Assert.assertTrue(commonWaits.waitForElementVisible_withFluentWait(getDriver(), selectInventoryLocationSection, 5), "Select inventory location section is not displayed");
        setTypeaheadField("Location", location);
        WebElement option = commonWaits.waitForByElementToBeVisible(By.xpath(String.format(locationOptionRadioBtn, location)));
        commonWaits.clickWithoutInterception(option);
        commonWaits.clickWithoutInterception(saveLocationButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appSmartIT);
        return this;
    }



}