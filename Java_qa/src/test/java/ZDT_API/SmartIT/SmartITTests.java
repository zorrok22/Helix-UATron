package ZDT_API.SmartIT;

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

public class SmartITTests {
    private LoginHelper loginHelper;
    private static final SolqaLogger log = new SolqaLogger(SmartITTests.class.getName());
    
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
        
        // Setup login helper with user credentials
        loginHelper = new LoginHelper();
        loginHelper.setUserDetails(zdtUserSetup.getUsername(), zdtUserSetup.getPassword());
    }

    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "SmartIT"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99121", Products = {Product.SmartIT})
    public void CreateIncidentTest()
    {
        new CreateIncident(loginHelper).run();
    }

    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "SmartIT"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99122", Products = {Product.SmartIT})
    public void CreateWorkOrderTest()
    {
        new CreateWorkOrder(loginHelper).run();
    }

    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "SmartIT"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99123", Products = {Product.SmartIT})
    public void CreateChangeRequestTest()
    {
         //LoginHelper loginHelper = new LoginHelper("cluong");
         new CreateChange(loginHelper).run();
    }

    @Test(priority = 1, invocationCount = 1,groups = {"ZDT", "SmartIT"})
    @SolqaDetails(authorName = "vbakhru", JiraID = "DRSQA-99124", Products = {Product.SmartIT})
    public void CreateProblemTest()
    {
        //LoginHelper loginHelper = new LoginHelper("pchang");
        new CreateProblem(loginHelper).run();
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("Completed Data-Driven Multi-User Test Execution");
    }
    
}
