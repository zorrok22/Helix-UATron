package pages.ITSM.Apex;

import java.util.Map;

import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import pages.ITSM.ITSM.WorkOrder;
import pages.ITSM.SmartIT.TicketConsolePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ApexWorkOrderPage extends BasePage {
	
	static SolqaLogger log;
	CommonWaits commonWaits;
	WorkOrder workOrder;

	public ApexWorkOrderPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(TicketConsolePage.class.getName());
		commonWaits = new CommonWaits();
		workOrder = new WorkOrder();
	}

	
	public String fillWorkOrderDataApex(Map<Object, String> workOrderData) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);

        //Set Company
        if (workOrderData.get("Company*") != null) {
        	workOrder.setDropDownField("Company*", workOrderData.get("Company*"));
        }

        // Set Customer
        if (workOrderData.get("Customer*") != null) {
        	workOrder.setTypeHeadInput("Customer*", workOrderData.get("Customer*"));
        }

        // Set Summary
        if (workOrderData.get("Summary*") != null) {
        	workOrder.setTextAreaInput("Summary*", workOrderData.get("Summary*"));
        }

        // Set manager support Group
        if (workOrderData.get("Manager Support Group Name*") != null) {
        	workOrder.setTypeHeadInput("Support Group Name*", workOrderData.get("Manager Support Group Name*"));
        }
        
        return workOrder.fillWorkOrderData(workOrderData);

    }

}
