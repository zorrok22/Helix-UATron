package pages.ITSM.Apex;

import java.util.Map;

import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import pages.ITSM.ITSM.Problem;
import pages.ITSM.SmartIT.TicketConsolePage;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ApexProblemPage extends BasePage{
	static SolqaLogger log;
	CommonWaits commonWaits;
	Problem problem;

	public ApexProblemPage() {
		PageFactory.initElements(getDriver(), this);
		log = new SolqaLogger(TicketConsolePage.class.getName());
		commonWaits = new CommonWaits();
		problem = new Problem();
	}
	
	public String fillProblemDataApex(Map<Object, String> problemData) {
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appITSM);
        // Set Summary
        if (problemData.get("Summary*") != null) {
        	problem.setTextAreaInput("Summary*", problemData.get("Summary*"));
        }
        
        if (problemData.get("Impact*") != null) {
        	problem.setDropDownField("Impact", problemData.get("Impact*"));
        }

        if (problemData.get("Urgency*") != null) {
        	problem.setDropDownField("Urgency*", problemData.get("Urgency*"));
        }
        
        if (problemData.get("Assigned Group") != null) {
        	problem.setTypeHeadInput("Assigned Group", problemData.get("Assigned Group"));
        }
        
        if (problemData.get("Assignee") != null) {
        	problem.setTypeHeadInput("Assignee", problemData.get("Assignee"));
        }
        
        if (problemData.get("Problem Coordinator+") != null) {
        	problem.setTypeHeadInput("Problem Coordinator+", problemData.get("Problem Coordinator+"));
        }
        
        if (problemData.get("Coordinator Group*+") != null) {
        	problem.setTypeHeadInput("Coordinator Group*+", problemData.get("Coordinator Group*+"));
        }


        return problem.fillProblemData(problemData);
	}
}
