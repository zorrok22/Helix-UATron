package ZDT_API.ITSM;

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

public class ITSMTests {
    private ITSMLogin login;
    private static final SolqaLogger log = new SolqaLogger(ITSMTests.class.getName());
    
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
        login = new ITSMLogin();
        login.setUserDetails(zdtUserSetup.getUsername(), zdtUserSetup.getPassword());
    }

    
    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "ITSM"},enabled=false)
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99104", Products = {Product.Helix_ITSM})
    public void SearchIncidentByStatus()
    {   
        IncidentManagement incidentManagement = new IncidentManagement(login);
        incidentManagement.searchIncidentDetails("status");
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99105", Products = {Product.Helix_ITSM})
    public void SearchIncidentByName()
    {   
        IncidentManagement incidentManagement = new IncidentManagement(login);
        incidentManagement.searchIncidentDetails("user");
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99106", Products = {Product.Helix_ITSM})
    public void SearchIncidentById()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        incidentManagement.searchIncidentDetails("status");
        incidentManagement.searchIncidentById();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"},enabled=false)
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99107", Products = {Product.Helix_ITSM})
    public void ModifyIncident()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        incidentManagement.searchIncidentDetails("status");
        incidentManagement.modifyIncidentDetails();
    }

    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99108", Products = {Product.Helix_ITSM})
    public void CreateIncident()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        incidentManagement.createIncident();
    }

    @Test(priority=1, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99109", Products = {Product.Helix_ITSM})
    public void CreateChangeRequest()
    {
        ChangeManagement changeManagement = new ChangeManagement(login);
        changeManagement.createChange();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99110", Products = {Product.Helix_ITSM})
    public void SearchChangeRequestDetails()
    {
        ChangeManagement changeManagement = new ChangeManagement(login);
        changeManagement.searchChangeDetails();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99111", Products = {Product.Helix_ITSM})
    public void SearchChangeRequestById()
    {
        ChangeManagement changeManagement = new ChangeManagement(login);
        changeManagement.searchChangeDetails();
        changeManagement.searchChangeDetailsById();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99112", Products = {Product.Helix_ITSM})
    public void ModifyChangeRequest()
    {
        ChangeManagement changeManagement = new ChangeManagement(login);
        changeManagement.searchChangeDetails();
        changeManagement.modifyChange();
    }

    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99113", Products = {Product.Helix_ITSM})
    public void CreateProblemInvestigateion()
    {   
        ProblemManagement problemManagement = new ProblemManagement(login);
        problemManagement.createProblem();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99114", Products = {Product.Helix_ITSM})
    public void SearchProblemInvestigationDetails()
    {
        ProblemManagement problemManagement = new ProblemManagement(login);
        problemManagement.searchProblemDetails();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99115", Products = {Product.Helix_ITSM})
    public void SearchProblemInvestigationById()
    {
        ProblemManagement problemManagement = new ProblemManagement(login);
        problemManagement.searchProblemDetails();
        problemManagement.searchProblemById();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"},enabled=false)
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99116", Products = {Product.Helix_ITSM})
    public void ModifyProblemInvestigation()
    {
        ProblemManagement problemManagement = new ProblemManagement(login);
        problemManagement.searchProblemDetails();
        problemManagement.modifyProblem();
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99117", Products = {Product.Helix_ITSM})
    public void GetKnowledgeArticlesforIncident()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        String incidentId = incidentManagement.searchIncidentDetails("status");    
        KnowldegeManagement knowldegeManagement = new KnowldegeManagement(login);
        knowldegeManagement.searchArticleDetails("incident", incidentId);
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99118", Products = {Product.Helix_ITSM})
    public void GetKnowledgeArticleforProblem()
    {
        ProblemManagement problemManagement = new ProblemManagement(login);
        String problemId = problemManagement.searchProblemDetails();
        KnowldegeManagement knowldegeManagement = new KnowldegeManagement(login);
        knowldegeManagement.searchArticleDetails("problem", problemId);
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99119", Products = {Product.Helix_ITSM})
    public void GetWorkNotesforIncident()
    {
        IncidentManagement incidentManagement = new IncidentManagement(login);
        String incidentId = incidentManagement.searchIncidentDetails("status");
        WorkNotesManagement workNotesManagement = new WorkNotesManagement(login);
        workNotesManagement.getWorkNotesDetails("incident", incidentId);
    }

    @Test(priority = 2, invocationCount = 1,groups = {"ZDT", "ITSM"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99120", Products = {Product.Helix_ITSM})
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
