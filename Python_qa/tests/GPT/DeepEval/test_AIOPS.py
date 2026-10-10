import os
import sys
import time
import pytest
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
sys.path.append(project_root)
from pages.ITOM.ADEPortal.ui_utils import ADEPortalPage
from pages.ITOM.AiOps.aiops_page import AIOpsPage
from pages.ITOM.AiOps.situations_page import SituationsPage
from pages.ITSM.SmartIT.create_incident import create_incident
from pages.ITSM.SmartIT.smart_it_dashboard import smart_it_dashboard
from pages.GPT.Teams.teams_page  import teams_page
from pages.GPT.HelixGPT.helixgpt_manager import HelixGPTManager
from utils.ui_utils import utilities
from test_config import TestConfig
from pages.ITOM.AiOps.API.Events_API import EventsAPI
from utils.constants import SolQAConstants as Constants
from pages.ITOM.Platform.platform_landing import PlatformLandingPage
from pages.ITOM.BHOM.bhom_landing import BhomLandingPage
from utils.common_waits import CommonWaits
from pages.ITSM.SmartIT.smartit_page import SmartITPage
from pages.ITSM.SmartIT.view_edit_incident import ViewEditIncident
from pages.GPT.DeepEval.DeepEvalUtil import DeepEvalUtil
import json

PRODUCT_TYPE = "GPT"
TEST_DESCRIPTION = "GPT Testcases"
WAIT_FOR_REPLY=60
LONG_WAIT=80
MAX_WAIT=200
def __init__():
    pass

__init__()

@pytest.mark.smoke #13121 
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13121_GettingBAP_AIOPS_Teams(driver, request,provider_config_key):

    """
        Test Steps:
        1.Log into Smart IT as an IT Agent.
 
        2.* View an incident created from AIOps.
        3.* Assign it to yourself.

        4.Click on the Launch Teams App button.
 
        5.Add another agents and click on the Launch Teams App button.
 
        6.Ask the bot about the best action plan for the incident.
          @<GPT Bot Name> "Get the best action plan for INC000000000xxx."
 
        7.Ask the bot about the best action plan without specifying the incident ID.
          @<GPT Bot Name> "Get the best action recommendation"
 
        8.Click on the hyperlink.
        9.Verify the BAP is opened in AIOps.
    """
 
    try:
        print("=== Starting test_GettingBAP_AIOPS_Teams ===")
        print("Load test data and configurations...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        teams_obj = teams_page(driver=driver)
        eventsAPIObj= EventsAPI()
        common_waitsObj= CommonWaits(driver)
        create_incidentObj = create_incident(driver)
        helixgpt_manager = HelixGPTManager(driver=driver)
        deep_eval_util = DeepEvalUtil()
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")

        # Use provider and model in your setup
        config=uiuti_obj.load_config("azure_config.json")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        driver.get(TestConfig.ISURL+helixgpt_manager.HELIXGPTMANAGERURL)
        setup_success = helixgpt_manager.setup_helix_gpt(
            username=common_data['SU_USERNAME'],
            password=common_data['SU_PASSWORD'],
            provider=provider,
            model=model
        )
        assert helixgpt_manager.logout(), "Logout from Helix GPT Manager failed for Azure OpenAI"
        assert setup_success, f"Helix GPT setup failed for {provider}"
 
        print("Creating incident from AIOps...")
        Access_Key = TestConfig.ADE_ACCESSKEY
        Access_Secret_Key = TestConfig.ADE_SECRETKEY
        print(f"Access Key: {Access_Key}, Access Secret Key: {Access_Secret_Key}")  
        print("---- Create Incident using event----------")
        response = eventsAPIObj.inject_event_ticket_for_bhom(Access_Key, Access_Secret_Key, jsonFilePath, common_data['ADE_USERNAME'], common_data['ADE_PASSWORD'])
        print(f"Response status code: {response.status_code}")
        print("Login to ADE Platform")

        ade_portal = ADEPortalPage(driver)
        aiops_page = AIOpsPage(driver)
        situations_page = SituationsPage(driver)
        print("Login to ADE Platform")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        uiuti_obj.login(username=common_data['ADE_USERNAME'], password=common_data['ADE_PASSWORD'], URL=ade_portal.get_product_url("AIOPS"))
        print("\nStep 2: Navigating to Situations tab...")
        assert aiops_page.navigate_to_tab("Situations"), "Failed to navigate to Situations tab"
        time.sleep(5)  # Wait for Situations tab to load
        INCIDENT_ID=situations_page.find_incident_by_situation(situation_name=TestConfig.SITUATION_PREFIX, max_attempts=20, click=True)
        time.sleep(10) #Wait ITSM to launch and load
        common_waitsObj.switch_to_tab_by_title(Constants.HELIX_ITSM)
        create_incidentObj.verifyassigned_To_me(name=common_data['ADMIN'],assign_to_me=True)
        print("Adding team members...")
        create_incidentObj.add_members(common_data['END_USER'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TestConfig.TENANT_USERNAME,TestConfig.TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        print(f"Bot Name: {bot_name}")
        print("Wait teams to load...")
        common_waitsObj.switch_to_tab_by_title(Constants.TEAMS)
        teams_obj.wait_till_teams_loads()
        time.sleep(MAX_WAIT)
        teams_obj.select_teams_chat(INCIDENT_ID)

        print("Ask the bot about the best action plan for the incident.")
        latest_message =teams_obj.send_message(f"Get the best action plan for  {INCIDENT_ID}",botName=bot_name)
        time.sleep(LONG_WAIT)
        last_msg=teams_obj.get_last_teams_message(timeout=600,lastMessage=latest_message)
        #DeepEvalUtil to evaluate the results
        expected_components= """The best action plan response should include:
                                1. Incident summary with key details (ID, status)
                                2. Technical context including host names, cluster details, and POD deployment information
                                3. Current status assessment showing the infrastructure component readiness issue
                                4. Specific action plan highlights identifying the core problem areas
                                5. Reference to work logs and timeline information
                                6. Actionable next steps for issue resolution
                                7. Any additional information like Analysing string should be considered relevant and not penalized as hallucinations."""
        

        context = [
                        "Critical priority incident regarding POD not ready state",
                        "CRITICAL event severity requiring immediate attention",
                        "Best action plans should include troubleshooting steps, monitoring recommendations, and coordination instructions",
                        "Action plans should reference technical details like host information,root cause",
                        ]
        input=latest_message
        actual_output=last_msg
        deep_eval_util.EvaluateResults(
            context=context,
            input=input,
            expected_components=expected_components,
            actual_output=actual_output            
        )
        
        print("Get the best action recommendation without specifying the incident ID...")
        latest_message =teams_obj.send_message(f"Get the best action recommendation",botName=bot_name)
        time.sleep(LONG_WAIT)
        last_msg=teams_obj.get_last_teams_message(timeout=600,lastMessage=latest_message)
        actual_output=last_msg
        expected_components= """The best action plan response should include:
                            1. Incident summary with key details (ID,status)
                            2. Hostname,Priority details
                            3. Action recommendation"""

        context = [
                    "Kubernetes cluster with POD deployment issues",
                    "Incident assigned to team members", 
                    "CRITICAL event severity requiring immediate attention",
                    "Best action plans should include troubleshooting steps, monitoring recommendations, and coordination instructions",
                    ]

        deep_eval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=actual_output
        )
        #feature not available 
        # print("Verify the BAP is opened in AIOps...")
        # print("Clicking on the hyperlink in the message...")
        # uiuti_obj.open_link_from_teams_message(last_msg)
        # print("Verify the BAP is opened in AIOps...")
        # sm_landingObj.verify_sm_landing_page_load()
        # sm_landingObj.verify_BAP_Displayed(situation_type="High CPU Utilization")
        # common_waitsObj.switch_to_tab_by_title(Constants.TEAMS)
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")

    finally:
        print("Navigating to Situations ..")
        common_waitsObj.switch_to_tab_by_title(Constants.SERVICEMONITOR)
        aiopsObj=AIOpsPage(driver=driver)
        situations_pageObj = SituationsPage(driver=driver)
        aiopsObj.navigate_to_tab("Situations")
        situations_pageObj.close_all_situations()
        print("=== Completed test_GettingBAP_AIOPS_Teams ===")
 
@pytest.mark.smoke #12295 
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_12295_GettingInformation_AIOPS_Teams(driver, request,provider_config_key):
    """
        Test Steps:
        1.Log into Smart IT as an IT Agent.
 
        2.Open any incident assigned to the user 
        3.Click on launch MS Team button

        4.Click on the Launch Teams App button.
 
        5.Verify Agent is able to get similar incident list for query "Show similar incidents "
 
        6.Verify agent is able to get situation details from AiOps with query
    """
    try:
        print("=== Starting test_GettingInformation_AIOPS_Teams ===")
        print("Load test data and configurations...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        teams_obj = teams_page(driver=driver)
        eventsAPIObj= EventsAPI()
        common_waitsObj= CommonWaits(driver)
        create_incidentObj = create_incident(driver)
        helixgpt_manager = HelixGPTManager(driver=driver)
        deep_eval_util = DeepEvalUtil()
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")
        #Use provider and model in your setup
        config=uiuti_obj.load_config("azure_config.json")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        driver.get(TestConfig.ISURL+helixgpt_manager.HELIXGPTMANAGERURL)
        setup_success = helixgpt_manager.setup_helix_gpt(
            username=common_data['SU_USERNAME'],
            password=common_data['SU_PASSWORD'],
          provider=provider,
            model=model
         )
        assert helixgpt_manager.logout(), "Logout from Helix GPT Manager failed for Azure OpenAI"
        assert setup_success, f"Helix GPT setup failed for {provider}"
        print("Creating first Situation->incident from AIOps...")
        Access_Key = TestConfig.ADE_ACCESSKEY
        Access_Secret_Key = TestConfig.ADE_SECRETKEY
        print(f"Access Key: {Access_Key}, Access Secret Key: {Access_Secret_Key}")
        print("---- Create Incident using event----------")
        response = eventsAPIObj.inject_event_ticket_for_bhom(Access_Key, Access_Secret_Key, jsonFilePath, common_data['ADE_USERNAME'], common_data['ADE_PASSWORD'])
        print(f"Response status code: {response.status_code}")
        response_json = response.json()
        resource_ids = response_json.get("resourceId", [])
        print(f"Event IDs: {resource_ids}")

        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")

        ade_portal = ADEPortalPage(driver)
        aiops_page = AIOpsPage(driver)
        situations_page = SituationsPage(driver)
        print("Login to ADE Platform")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        uiuti_obj.login(username=common_data['ADE_USERNAME'], password=common_data['ADE_PASSWORD'], URL=ade_portal.get_product_url("AIOPS"))
        print("\nStep 2: Navigating to Situations tab...")
        assert aiops_page.navigate_to_tab("Situations"), "Failed to navigate to Situations tab"
        time.sleep(5)  # Wait for Situations tab to load
        INCIDENT_ID=situations_page.find_incident_by_situation(situation_name=TestConfig.SITUATION_PREFIX, max_attempts=20, click=True)
        time.sleep(10) #Wait ITSM to launch and load
        common_waitsObj.switch_to_tab_by_title(Constants.HELIX_ITSM)

        create_incidentObj.verifyassigned_To_me(name=common_data['TEAMS_ADMIN_FULLNAME'],assign_to_me=True)
        print("Adding team members...")
        create_incidentObj.add_members(common_data['END_USER'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TestConfig.TENANT_USERNAME,TestConfig.TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        print(f"Bot Name: {bot_name}")
        print("Wait teams to load...")
        common_waitsObj.switch_to_tab_by_title(Constants.TEAMS)
        teams_obj.wait_till_teams_loads()
        time.sleep(MAX_WAIT)
        teams_obj.select_teams_chat(INCIDENT_ID)

        print("Ask the bot about the similar incident.")
        latest_message =teams_obj.send_message(f"Show similar incidents",botName=bot_name)
        time.sleep(LONG_WAIT)
        last_msg=teams_obj.get_last_teams_message(timeout=600,lastMessage=latest_message)
        #DeepEvalUtil to evaluate the results
        expected_components = [
            "Incident Number",
            "Summary",
            "Priority",
            "Status",
            "Submit Date",
            "Assignee",
            "Assigned Group"
        ]
        context = [
        "When asked to show similar incidents for a given INCIDENT_ID, "
        "the response should return a list of incidents with the following fields: "
        "Incident Number (format: INC followed by digits), Summary (description), "
        "Priority (Low/Medium/High), Status (current state), Submit Date (timestamp), "
        "Assignee (person assigned), and Assigned Group (support group). "
        "The incident numbers in the response may be different from the requested incident number "
        "as these are similar incidents, not the same incident. This is expected behavior and should not be treated as hallucination."
          ]    
        input=latest_message
        actual_output=last_msg
        deep_eval_util.EvaluateResults(
            context=context,
            input=input,
            expected_components=expected_components,
            actual_output=actual_output
        )

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
    finally:
        print("Performing cleanup actions...")
        print("Navigating to Situations ..")
        common_waitsObj.switch_to_tab_by_title(Constants.SERVICEMONITOR)
        aiopsObj=AIOpsPage(driver=driver)
        situations_pageObj = SituationsPage(driver=driver)
        aiopsObj.navigate_to_tab("Situations")
        situations_pageObj.close_all_situations()
        print("=== Completed test_GettingInformation_AIOPS_Teams ===")

@pytest.mark.smoke #13123 
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"]) 
def test_DRSQA_13123_Getting5Whys(driver, request,provider_config_key):
    """
        Test Case: DRSQA13123
        Description: Verify the 5 Whys feature in Teams for an incident.
 
        Preconditions:
        Petramco dataset is loaded.
        Smart IT must be using progressive views.
        Register and configure Azure application.
        Configure MS Teams Collaborator Bot.
        Configure AIOps with HelixGPT.
        Create some incidents from AIOps.

        Test Steps:
        1. Log into Smart IT as an IT Agent.    
        2.* View an incident created from AIOps.
          * Assign it to yourself.
        3. Click on the Launch Teams App button.
        4. Add another agent and click on the Launch Teams App button.  
        5. Ask the bot about the 5 Whys for the incident.
           @<GPT Bot Name> "What are the 5 Whys for INC000000
        6. Ask the bot about the 5 Whys without specifying the incident ID.
        7. Click on the hyperlink.
        8. Verify the 5 Whys are opened in AIOps.
    """
    try:
        print("=== Starting test_Getting5Whys ===")
        print("Load test data and configurations...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        smartit_dashboardObj = smart_it_dashboard(driver)
        teams_obj = teams_page(driver=driver)
        eventsAPIObj= EventsAPI()
        platform_obj=PlatformLandingPage(driver)
        bhome_landingObj=BhomLandingPage(driver)
        common_waitsObj= CommonWaits(driver)
        smart_itObj=SmartITPage(driver=driver)
        view_edit_incidentObj=ViewEditIncident(driver)
        create_incidentObj = create_incident(driver)
        helixgpt_manager = HelixGPTManager(driver=driver)
        deep_eval_util = DeepEvalUtil()
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")
        #Use provider and model in your setup
        config=uiuti_obj.load_config("azure_config.json")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        driver.get(TestConfig.ISURL+helixgpt_manager.HELIXGPTMANAGERURL)
        setup_success = helixgpt_manager.setup_helix_gpt(
            username=common_data['SU_USERNAME'],
            password=common_data['SU_PASSWORD'],
            provider=provider,
            model=model
        )
        assert helixgpt_manager.logout(), "Logout from Helix GPT Manager failed for Azure OpenAI"
        assert setup_success, f"Helix GPT setup failed for {provider}"
 
        print("Creating incident from AIOps...")
        Access_Key = TestConfig.ADE_ACCESSKEY
        Access_Secret_Key = TestConfig.ADE_SECRETKEY
        print(f"Access Key: {Access_Key}, Access Secret Key: {Access_Secret_Key}")
        print("---- Create Incident using event----------")
        response = eventsAPIObj.inject_event_ticket_for_bhom(Access_Key, Access_Secret_Key, jsonFilePath, common_data['ADE_USERNAME'], common_data['ADE_PASSWORD'])
        print(f"Response status code: {response.status_code}")
       
        ade_portal = ADEPortalPage(driver)
        aiops_page = AIOpsPage(driver)
        situations_page = SituationsPage(driver)
       
        print("Login to ADE Platform")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        uiuti_obj.login(username=common_data['ADE_USERNAME'], password=common_data['ADE_PASSWORD'], URL=ade_portal.get_product_url("AIOPS"))
        #assert ade_portal.login("AIOPS"), "Failed to login to AIOps"
        print("\nStep 2: Navigating to Situations tab...")
        assert aiops_page.navigate_to_tab("Situations"), "Failed to navigate to Situations tab"
        time.sleep(5)  # Wait for Situations tab to load
        INCIDENT_ID=situations_page.find_incident_by_situation(situation_name=TestConfig.SITUATION_PREFIX, max_attempts=20, click=True)
        time.sleep(10) #Wait ITSM to launch and load
        common_waitsObj.switch_to_tab_by_title(Constants.HELIX_ITSM)
        time.sleep(3)
        create_incidentObj.verifyassigned_To_me(name=common_data['TEAMS_ADMIN_FULLNAME'],assign_to_me=True)
        print("Adding team members...")
        create_incidentObj.add_members(common_data['END_USER'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TestConfig.TENANT_USERNAME,TestConfig.TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        print(f"Bot Name: {bot_name}")
        print("Wait teams to load...")
        common_waitsObj.switch_to_tab_by_title(Constants.TEAMS)
        teams_obj.wait_till_teams_loads()
        time.sleep(MAX_WAIT)
        teams_obj.select_teams_chat(INCIDENT_ID)
        print("Ask the bot about the 5 whys for the incident.")
        latest_message =teams_obj.send_message(f"What are the 5 Whys for  {INCIDENT_ID}",botName=bot_name)
        time.sleep(LONG_WAIT)

        last_msg=teams_obj.get_last_teams_message(timeout=600,lastMessage=latest_message)
       
        #DeepEvalUtil to evaluate the results
        expected_components= "there should be 5 whys and possibly a root cause for the incident or situation"
        context = ["5 whys for situation like, Why was the CPU utilization high?,Why was the host experiencing a high load?,Why was the trainapp service using significant resources?,Why were there a high volume of incoming requests?,Why was there a surge in user activity? and root cause"]
        actual_output=last_msg
        deep_eval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=actual_output
        )

        latest_message =teams_obj.send_message(f"What are the 5 Whys",botName=bot_name)
        last_msg=teams_obj.get_last_teams_message(timeout=600,lastMessage=latest_message)
        expected_components= "there should be 5 whys and possibly a root cause for the incident or situation"
        context = ["5 whys for situation like, Why was the CPU utilization high?,Why was the host experiencing a high load?,Why was the trainapp service using significant resources?,Why were there a high volume of incoming requests?,Why was there a surge in user activity? and root cause"]
        actual_output=last_msg
        deep_eval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=actual_output
        )

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")

    finally:
        print("Navigating to Situations ..")
        common_waitsObj.switch_to_tab_by_title(Constants.SERVICEMONITOR)
        aiopsObj=AIOpsPage(driver=driver)
        situations_pageObj = SituationsPage(driver=driver)
        aiopsObj.navigate_to_tab("Situations")
        situations_pageObj.close_all_situations()
        print("=== Completed test_Getting5Whys ===")

#@pytest.mark.smoke #13122  
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"]) 
def test_DRSQA_13122_GettingLogAnalysis(driver, request,provider_config_key):
    """
        Test Case: DRSQA13122
        Description: Getting Log Analysis from AIOps on Teams
 
        Preconditions:
        Petramco dataset is loaded.
        Smart IT must be using progressive views.
        Register and configure Azure application.
        Configure MS Teams Collaborator Bot.
        Configure AIOps with HelixGPT.
        Create some incidents from AIOps.

        Test Steps:
        1. Log into Smart IT as an IT Agent.    
        2.* View an incident created from AIOps.
          * Assign it to yourself.
        3. Click on the Launch Teams App button.
        4. Add another agent and click on the Launch Teams App button.  
        5. Ask the bot about the Log analysis for the incident.
           @<GPT Bot Name> "Log analysis for  for INC000000
        6. Ask the bot about the log analysis without specifying the incident ID..
           @<GPT Bot Name> Log insights
    """
    try:
        print("=== Starting test_GettingLogAnalysis ===")
        print("Load test data and configurations...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        teams_obj = teams_page(driver=driver)
        eventsAPIObj= EventsAPI()
        common_waitsObj= CommonWaits(driver)
        create_incidentObj = create_incident(driver)
        helixgpt_manager = HelixGPTManager(driver=driver)
        deep_eval_util = DeepEvalUtil()
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")
        #Use provider and model in your setup
        config=uiuti_obj.load_config("azure_config.json")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        driver.get(TestConfig.ISURL+helixgpt_manager.HELIXGPTMANAGERURL)
        setup_success = helixgpt_manager.setup_helix_gpt(
            username=common_data['SU_USERNAME'],
            password=common_data['SU_PASSWORD'],
            provider=provider,
            model=model
        )
        assert helixgpt_manager.logout(), "Logout from Helix GPT Manager failed for Azure OpenAI"
        assert setup_success, f"Helix GPT setup failed for {provider}"
 
        print("Creating incident from AIOps...")
        Access_Key = TestConfig.ADE_ACCESSKEY
        Access_Secret_Key = TestConfig.ADE_SECRETKEY
        print(f"Access Key: {Access_Key}, Access Secret Key: {Access_Secret_Key}")
        print("---- Create Incident using event----------")
        response = eventsAPIObj.inject_event_ticket_for_bhom(Access_Key, Access_Secret_Key, jsonFilePath,common_data['ADE_USERNAME'], common_data['ADE_PASSWORD'])
        print(f"Response status code: {response.status_code}")
       
        ade_portal = ADEPortalPage(driver)
        aiops_page = AIOpsPage(driver)
        situations_page = SituationsPage(driver)
       
        print("Login to ADE Platform")
        uiuti_obj.login(username=common_data['ADE_USERNAME'], password=common_data['ADE_PASSWORD'], URL=ade_portal.get_product_url("AIOPS"))
        print("\nStep 2: Navigating to Situations tab...")
        assert aiops_page.navigate_to_tab("Situations"), "Failed to navigate to Situations tab"
        time.sleep(5)  # Wait for Situations tab to load
        INCIDENT_ID=situations_page.find_incident_by_situation(situation_name=TestConfig.SITUATION_PREFIX, max_attempts=20, click=True)
        time.sleep(10) #Wait ITSM to launch and load
        common_waitsObj.switch_to_tab_by_title(Constants.HELIX_ITSM)
        
        time.sleep(3)
        create_incidentObj.verifyassigned_To_me(name=common_data['TEAMS_ADMIN_FULLNAME'],assign_to_me=True)
        print("Adding team members...")
        create_incidentObj.add_members(common_data['END_USER'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TestConfig.TENANT_USERNAME,TestConfig.TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        print(f"Bot Name: {bot_name}")
        print("Wait teams to load...")
        common_waitsObj.switch_to_tab_by_title(Constants.TEAMS)
        teams_obj.wait_till_teams_loads()
        time.sleep(MAX_WAIT)
        print("Ask the bot about the Log analysis for ")
        latest_message =teams_obj.send_message(f"Log analysis for  {INCIDENT_ID}",botName=bot_name)
        last_msg=teams_obj.get_last_teams_message(timeout=600,lastMessage=latest_message)
     
        #DeepEvalUtil to evaluate the results
        expected_components= "Log analysis for the situation should display"
        context = ["Log analysis for the situation"]
        actual_output=last_msg,
        deep_eval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=actual_output
        )

        latest_message =teams_obj.send_message(f"Log insights",botName=bot_name)
        last_msg=teams_obj.get_last_teams_message(timeout=600,lastMessage=latest_message)
        expected_components= "Log insights for the situation should display"
        context = ["Log insights for situation"]
        actual_output=last_msg,
        deep_eval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=actual_output
        )

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")

    finally:
        print("Navigating to Situations ..")
        #common_waitsObj.switch_to_tab_by_title(Constants.SERVICEMONITOR)
        #aiopsObj=AIOpsPage(driver=driver)
        #situations_pageObj = SituationsPage(driver=driver)
        #aiopsObj.navigate_to_tab("Situations")
        #situations_pageObj.close_all_situations()
        print("=== Completed test_GettingLogAnalysis ===")

@pytest.mark.smoke #12285
@pytest.mark.AIOPS
def test_DRSQA_12285_VerifySituationSummary(driver, request):
    """
        Test Case: DRSQA-12285
        Description: View the Situation summary generated by BMC HelixGPT
 
       MISC INFO:
        0. TEST OBJECTIVE: Validate whether the Situation summary is generated by BMC HelixGPT
        1. Pre-Conditions: HelixGPT needs to be enabled
        2. Negative Scenarios

        Test Steps:
        1. Login to ADE Portal.
        2. Click on AIOPS tile.
        3. Go to Situations Tab.
        4. Open any situation.
        5. Once the situation page is loaded, check if the top box contains the summarized information related to the situation.

    """
    try:
        print("=== Starting test_VerifySituationSummary ===")
        print("Load test data and configurations...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        eventsAPIObj= EventsAPI()
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")
        situations_pageObj = SituationsPage(driver=driver)
        ade_portal = ADEPortalPage(driver)
        aiops_page = AIOpsPage(driver)
        print("Creating incident from AIOps...")
        Access_Key = TestConfig.ADE_ACCESSKEY
        Access_Secret_Key = TestConfig.ADE_SECRETKEY
        print(f"Access Key: {Access_Key}, Access Secret Key: {Access_Secret_Key}")
        print("---- Create Incident using event----------")
        response = eventsAPIObj.inject_event_ticket_for_bhom(Access_Key, Access_Secret_Key, jsonFilePath,common_data['ADE_USERNAME'], common_data['ADE_PASSWORD'])
        print(f"Response status code: {response.status_code}")
        time.sleep(WAIT_FOR_REPLY)  # Wait for the event to be processed
        print("Login to ADE Platform")
        uiuti_obj = utilities(driver)
        uiuti_obj.login(username=common_data['ADE_USERNAME'], password=common_data['ADE_PASSWORD'], URL=ade_portal.get_product_url("AIOPS"))
        print("\nStep 2: Navigating to Situations tab...")
        assert aiops_page.navigate_to_tab("Situations"), "Failed to navigate to Situations tab"
        situations_pageObj.click_first_situation_message()
        time.sleep(LONG_WAIT)  # Wait for the situation page to load
        root_cause = situations_pageObj.get_root_cause_summary()
        situation_summary = situations_pageObj.get_situation_summary_description()
        print(f"Root Cause Summary: {root_cause}")
        print(f"Situation Summary: {situation_summary}")
        assert root_cause is not None and root_cause != "", "Root cause summary should not be null or empty"
        assert situation_summary is not None and situation_summary != "", "Situation summary should not be null or empty"

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")

    finally:
        # print("Closing all situations.")
        # aiops_page.navigate_to_tab("Situations")
        # situations_pageObj.close_all_situations()
        print("=== Completed test_VerifySituationSummary ===")

@pytest.mark.smoke #12284 
@pytest.mark.AIOPS
def test_DRSQA_12284_ValidateVirtualAgent(driver, request):
    """
        Test Case: DRSQA-12284
        Description: View the Situation summary generated by BMC HelixGPT
 
       Test Steps:
       1. Login to ADE Portal.

       2. Click on AIOPS tile.

       3. Go to Situations Tab.

       4. Open any situation.

       5. Click on Ask HelixGPT.

       6. Switch to Best Action Recommendations

       7. Ask your questions related to the situation like below.
          For Ex: What is the impact of this issue?

    """
    try:
        print("=== Starting test_ValidateVirtualAgent ===")
        print("Load test data and configurations...")
        eventsAPIObj= EventsAPI()
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")
        situations_pageObj = SituationsPage(driver=driver)  
        ade_portal = ADEPortalPage(driver)
        aiops_page = AIOpsPage(driver)
        deep_eval_util = DeepEvalUtil()
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        print("Creating incident from AIOps...")
        Access_Key = TestConfig.ADE_ACCESSKEY
        Access_Secret_Key = TestConfig.ADE_SECRETKEY
        print(f"Access Key: {Access_Key}, Access Secret Key: {Access_Secret_Key}")
        print("---- Create Incident using event----------")
        response = eventsAPIObj.inject_event_ticket_for_bhom(Access_Key, Access_Secret_Key, jsonFilePath,common_data['ADE_USERNAME'], common_data['ADE_PASSWORD'])
        time.sleep(WAIT_FOR_REPLY)  # Wait for the event to be processed
        print(f"Response status code: {response.status_code}")
        print("Login to ADE Platform")
       
        uiuti_obj.login(username=common_data['ADE_USERNAME'], password=common_data['ADE_PASSWORD'], URL=ade_portal.get_product_url("AIOPS"))
        print("\nStep 2: Navigating to Situations tab...")
        assert aiops_page.navigate_to_tab("Situations"), "Failed to navigate to Situations tab"
        situations_pageObj.click_first_situation_message()
        time.sleep(LONG_WAIT)  # Wait for the situation page to load

        # Click Ask HelixGPT button
        print("\nStep 6: Clicking Ask HelixGPT button...")
        assert situations_pageObj.click_ask_helixgpt_button(), "Failed to click Ask HelixGPT button"

        # Click Impact question
        print("\nStep 7: Clicking Impact question...")
        assert situations_pageObj.ask_impact(), "Failed to click Impact question"
        time.sleep(WAIT_FOR_REPLY)
        # Extract answer
        print("\nStep 8: Extracting answer...")
        latest_message = situations_pageObj.extract_BAR_answers("Impact")
        plain_string = " ".join(latest_message[0] if isinstance(latest_message[0], list) else latest_message)
        context= ["Service health monitoring system that tracks health scores from 0-100",
            "Critical state: health score ",
            "Major state: health score ", 
            "Minor state: health scores",
            "OK state: health score ",
            "Situations are created when service health deteriorates to monitor and address issues",
            "Service health timeline provides historical data to understand impact and trends"
        ]

        expected_components="""The response should provide:
        1. A timeline of service health states showing progression from critical to OK and back to minor
        2. Explanation of what each health score means in terms of service functionality
        3. Clear indication of when the situation was created 
        4. Impact analysis showing service deterioration from OK (95) to minor (85) state
        5. Summary explaining that the situation creation indicates service health issues need addressing
        """

        deep_eval_util.EvaluateResults(
            context=context,
            input="What is the Impact of this situation?",
            expected_components=expected_components,
            actual_output=plain_string
        )

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")

    finally:
        print("Closing all situations.")
        #aiops_page.navigate_to_tab("Situations")
        #situations_pageObj.close_all_situations()
        print("=== Completed test_ValidateVirtualAgent ===")

#@pytest.mark.smoke #12283 
def test_DRSQA_12283_ValidateVirtualAgentLogInsights(driver, request):
    """
        Test Case: DRSQA-12283
        Description: View the Situation summary generated by BMC HelixGPT
 
       Test Steps:
       1. Login to ADE Portal.

       2. Click on AIOPS tile.

       3. Go to Situations Tab.

       4. Open any situation.

       5. Click on Ask HelixGPT.

       6. Click on Log Insights
    """
    try:
        print("=== Starting test_ValidateVirtualAgent ===")
        print("Load test data and configurations...")
        eventsAPIObj= EventsAPI()
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        jsonFilePath = os.path.join(project_root, "testdata", "GPT", "SituationCreation.json")
        situations_pageObj = SituationsPage(driver=driver)          
        ade_portal = ADEPortalPage(driver)
        aiops_page = AIOpsPage(driver)
        deep_eval_util = DeepEvalUtil()
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        print("Creating incident from AIOps...")
        Access_Key = TestConfig.ADE_ACCESSKEY
        Access_Secret_Key = TestConfig.ADE_SECRETKEY
        print(f"Access Key: {Access_Key}, Access Secret Key: {Access_Secret_Key}")
        print("---- Create Incident using event----------")
        response = eventsAPIObj.inject_event_ticket_for_bhom(Access_Key, Access_Secret_Key, jsonFilePath,common_data['ADE_USERNAME'], common_data['ADE_PASSWORD'])
        print(f"Response status code: {response.status_code}")
        time.sleep(WAIT_FOR_REPLY)  # Wait for the event to be processed
        print("Login to ADE Platform")
       
        uiuti_obj.login(username=common_data['ADE_USERNAME'], password=common_data['ADE_PASSWORD'], URL=ade_portal.get_product_url("AIOPS"))
        #assert ade_portal.login("AIOPS"), "Failed to login to AIOps"
        print("\nStep 2: Navigating to Situations tab...")
        assert aiops_page.navigate_to_tab("Situations"), "Failed to navigate to Situations tab"
        situations_pageObj.click_first_situation_message()
        time.sleep(WAIT_FOR_REPLY)  # Wait for the situation page to load

        # Click Ask HelixGPT button
        print("\nStep 6: Clicking Ask HelixGPT button...")
        assert situations_pageObj.click_ask_helixgpt_button(), "Failed to click Ask HelixGPT button"

        # Click Impact question
        print("\nStep 7: Clicking LogInsights question...")
        assert situations_pageObj.ask_impact(), "Failed to click LogInsights question"

        # Extract answer
        print("\nStep 8: Extracting answer...")
        latest_message = situations_pageObj.extract_BAR_answers("Log Insights")
        
        #Need to modify as per answer
        expected_components= "It should summarize the logs related to the issue and display them."
        context = ["logs details related the issue"]
        actual_output=latest_message,
        deep_eval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=actual_output
        )

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")

    finally:
        print("Closing all situations.")
        #situations_pageObj.close_all_situations()
        print("=== Completed test_ValidateVirtualAgentLogInsights ===")

def load_situation_json(json_file_path):
    """Load situation JSON and replace hostname placeholder"""
    with open(json_file_path, 'r') as f:
        content = f.read()
    
    # Replace the hostname placeholder
    content = content.replace("{hostname}", TestConfig.HOSTNAME)
    
    # Parse and return the JSON
    return json.loads(content)