package ZDT_API.DWP;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import ZDT_API.ZDTUtils;
import ZDT_API.ZDTUtils.ZDTUserSetup;
import util.ReadPropertiesFile;
import util.SolqaLogger;
import java.io.File;
import java.util.Map;
import util.Product;
import util.SolqaDetails;

public class DWPTests {
    private DWPLogin login;
    private static final SolqaLogger log = new SolqaLogger(DWPTests.class.getName());
    
    private ZDTUserSetup zdtUserSetup;
    Map<String, String> config = ReadPropertiesFile.readProperties(System.getProperty("user.dir") + File.separator + "properties.config");

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Starting Data-Driven Multi-User Test Execution");
    }

    @BeforeTest(alwaysRun = true)
    public void setupZDTUsers() {
        // Use ZDTUtils to setup user data from Excel
        zdtUserSetup = ZDTUtils.setupZDTUserData(config);
        
        // Setup login with user credentials
        login = new DWPLogin();
        login.setUserDetails(zdtUserSetup.getUsername(), zdtUserSetup.getPassword());
    }

    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "DWP"})   
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99101", Products = {Product.Helix_DWP})
    public void GetKnowledgeArticlesforIncident()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        String incidentId = incidentManagement.searchIncidentDetails("status");    
        KnowldegeManagement knowldegeManagement = new KnowldegeManagement(login);
        knowldegeManagement.searchArticleDetails("incident", incidentId);
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "DWP"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99102", Products = {Product.Helix_DWP})
    public void GetWorkNotesforIncident()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        String incidentId = incidentManagement.searchIncidentDetails("status");
        WorkNotesManagement workNotesManagement = new WorkNotesManagement(login);
        workNotesManagement.getWorkNotesDetails("incident", incidentId);
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "DWP"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99103", Products = {Product.Helix_DWP})
    public void CreateWorkNotesforIncident()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        String incidentId = incidentManagement.searchIncidentDetails("status");
        WorkNotesManagement workNotesManagement = new WorkNotesManagement(login);
        workNotesManagement.createWorkNotes("incident", incidentId);
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("Completed Data-Driven Multi-User Test Execution");
    }
    
}
