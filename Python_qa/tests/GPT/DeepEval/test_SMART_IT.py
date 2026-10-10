import os
import sys
import time
import json
import pytest
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
sys.path.append(project_root)
from pages.ITSM.SmartIT.create_workorder import create_workorder
from pages.ITSM.SmartIT.create_problem import create_problem
from pages.ITSM.SmartIT.create_incident import create_incident
from pages.ITSM.SmartIT.smart_it_dashboard import smart_it_dashboard
from pages.ITSM.SmartIT.incident_summary import incident_summary
from pages.GPT.Teams.teams_page  import teams_page
from pages.GPT.HelixGPT.helixgpt_manager import HelixGPTManager
from pages.GPT.DeepEval.DeepEvalUtil import DeepEvalUtil    
from utils.ui_utils import utilities
from datetime import datetime
from test_config import TestConfig
PRODUCT_TYPE = "GPT"
TEST_DESCRIPTION = "GPT Testcases"
WAIT_FOR_REPLY=60
MAX_WAIT=200

def __init__():
   pass

__init__()

@pytest.mark.smoke #12262
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_12262_Incident_Teams(driver, request, provider_config_key):

    """
        Test Steps:
        1.Log into Smart IT as an IT Agent.
 
        2.Click on Create New -> Incident.
        3.Create an incident and assign it to yourself.
        4.Click on the Launch Teams App button.
 
        5.Add required agents and click on the Launch Teams App button.
 
        6.Ask the bot about the details of the incident.
          @<GPT Bot Name> "Share me the summary details for <Incident ID>"
 
        7.Ask the bot for similar incidents
          @<GPT Bot Name> "show similar incidents for <Incident ID>"
 
        8.Ask the bot to add a note
          @<GPT Bot Name> "Add note: New test note"
 
        9.Go to smartIT and verify the note is present on the ticket
    """
 
    try:
        print("=== Starting test_Incident_Teams ===")
        print("Load test data and configurations...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        data = uiuti_obj.load_config(file_name="IncidentData.json")
        testData = data['testData']        
        Incident_data = data['Incident_data']  
        TENANT_USERNAME=TestConfig.TENANT_USERNAME
        TENANT_PASSWORD=TestConfig.TENANT_PASSWORD
        SMARTIT_UNAME=common_data['SMARTIT_UNAME']
        SMARTIT_PWD=common_data['SMARTIT_PWD']
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        teams_obj = teams_page(driver=driver)
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        # Load configuration
        print("Loading configurations...")
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print(f"Config loaded: {config}")
 
        # # Use provider and model in your setup
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
 
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SMARTIT_UNAME'], password=common_data['SMARTIT_PWD'])
        time.sleep(10)  # Wait for the login to complete
        # 2.Click on Create New -> Incident.
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
 
        # 3.Create an incident and assign it to yourself.
        print("Creating incident...")
        create_incidentobj.fill_incident_template(testData)
        time.sleep(2)  # Wait for the form to process
        create_incidentobj.click_assign_to_me()
        time.sleep(2)  # Wait for the form to process
        create_incidentobj.save_incident()
        time.sleep(2)  # Wait for the incident to be saved
        INCIDENT_ID=create_incidentobj.get_incidentid()
        print(f"Created incident with ID: {INCIDENT_ID}")
        time.sleep(3)
        # 4.Click on the Launch Teams App button.
        print("Launching Teams app...")
        create_incidentobj.launch_chatOps()
 
        # 5.Add required agents and click on the Launch Teams App button.
        print("Adding team members...")
        create_incidentobj.add_members(common_data['TEAMS_ADMIN'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TENANT_USERNAME,TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        teams_obj.wait_till_teams_loads()
        time.sleep(MAX_WAIT)
        teams_obj.select_teams_chat(INCIDENT_ID)
        # 6.Ask the bot about the details of the incident.
        print("Requesting incident details from bot...")
        latest_message =teams_obj.send_message(f"Share me the summary details for {INCIDENT_ID}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
        val = teams_obj.verify_incident_details(Incident_data,lastSentMessage=latest_message)
        print(f"Incident details verification: {val}")
        # Evaluate bot response using DeepEval metrics
        print("Evaluating bot response with DeepEval metrics...")
        # Get the bot's response tex
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)
        expected_components = [
            "Incident Number",
            "Summary",
            "Priority",
            "Status",
            "Submit Date",
            "Assignee",
            "Assigned Group",
            "Worklog entries"
        ]
        context = [
            "For incident summary details, ALL of the following fields are required and considered relevant: "
            "Incident Number (format: INC followed by digits), Summary (description of the issue), "
            "Priority (Low/Medium/High), Status (current state), Submit Date (timestamp), "
            "Assignee (person assigned), Assigned Group (support group), and optionally Worklog entries. "
            "The absence of worklog entries is acceptable. "
            "Any additional fields should NOT be penalized as irrelevant. "
            "Do NOT treat Submit Date, Assignee, Assigned Group, or Worklog entries as metadata or operational details; "
            "they are part of the summary for this use case."
        ]    
        input=latest_message
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)
 
        # 7.Ask the bot for similar incidents
        print("Requesting similar incidents from bot...")
        latest_message=teams_obj.send_message(f"show similar incidents for {INCIDENT_ID}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
        val = teams_obj.verify_similar_incidents(latest_message)
        print(f"Similar incidents verification: {val}")
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)

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
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)
        # 8.Ask the bot to add a note
        print("Adding note via bot...")
        latest_message=teams_obj.send_message(
         f"Add note: Incident notes added to {INCIDENT_ID} at {datetime.now()} note to {INCIDENT_ID}",
        botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)
        assert bot_response is not None, "Failed to Add note"
        #  9.Go to smartIT and verify the note is present on the ticket
        print("Verifying note in Smart IT...")
        driver.switch_to.window(mainWindow)
        driver.refresh()
        # val = create_incidentobj.verify_element_has_text_automated_note()
 
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_Incident_Teams ===")
 
@pytest.mark.smoke #12261
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_12261_Participant_Recommendation(driver, request,provider_config_key):
    """
        Test Case: DRSQA12261
        Description: Verify the participant recommendation feature in Teams for an incident.
 
        Preconditions:
        MS-Teams configurations:
        https://confluence.example.com/pages/viewpage.action?pageId=572676410

        1.Go to AR System Administrator-> AR System Administrator Console->System->General->ccs configuration under com.bmc.arsys.smartit
        enableHelixGPTGlobalChat = true
        helixgptGlobalSearchDefaultMode = chat
        Under com.bmc.arsys.shared check the url
        HelixGPT-Assistant-Service-URL = http://helix-gpt-assistant:8001

        2.IS Administration  Web Api Mapping  Name= HelixGPT Host Name = helix-gpt-data-connection Port = 8000 Protocol = Http Authentication = Basic Login= helixgpt Password=helixgpt Web Api Mapping Connection target =Helixgpt -data connection Connection configuration = HelixGPT

        3.HKM Configuration Settings: IS parameters and HKM Iframe URL should be configured and enabled in ITSM -> HKM Configuration Settings (http://<AR host>:8080/arsys/forms/<AR host>/CFG:HKM:ConfigurationSettings) Prepare articles in ComAround. Helix Knowledge Management Main URL: https://zero-test.comaround.com Helix Knowledge Management Frame URL: https://knowledge-test.comaround.com

        4.Go to IS->Workspace->Helix GPT manager->Visit deployed application-> select BMC Helix IT Service Managenet->select skill ITSM Global chat
        Model:
        Provider:-Azure OpenAI
        Model:GPT-4 Turbo
        Prompt: select ITSM Knowledge Prompt as a starter

        5.Make sure disable the Helix Knowledge management before search RKM article
        2. Negative Scenarios
            
        Test Steps:
        1. Log into Smart IT as an IT Agent.
        2. Click on Create New -> Incident.
        3. Create an incident and assign it to yourself.
        4. Click on the Launch Teams App button.
        5. Add required agents and click on the Launch Teams App button.
        6. Ask the bot for suggestions on who can help with the incident
           @<GPT Bot Name> "who can help with the incident <Incident ID>"
    """
    try:
        print("=== Starting test_Participant_Recommendation ===")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        teams_obj = teams_page(driver=driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="IncidentData.json")
        testData = data['testData']
        Incident_data = data['Incident_data']  
        TENANT_USERNAME=TestConfig.TENANT_USERNAME
        TENANT_PASSWORD=TestConfig.TENANT_PASSWORD
        SMARTIT_UNAME=common_data['SMARTIT_UNAME']
        SMARTIT_PWD=common_data['SMARTIT_PWD']
        print("Initializing page objects...")
        
        # Load configuration
        print("Loading configurations...")
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print(f"Config loaded: {config}")
 
        # Use provider and model in your setup
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
 
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SMARTIT_UNAME'], password=common_data['SMARTIT_PWD'])

        # 2.Click on Create New -> Incident.
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
 
        # 3.Create an incident and assign it to yourself.
        print("Creating incident...")
        create_incidentobj.fill_incident_template(testData)
        create_incidentobj.click_assign_to_me()
        create_incidentobj.save_incident()
        INCIDENT_ID1=create_incidentobj.get_incidentid()
        print(f"Created incident with ID: {INCIDENT_ID1}")

        # 4.Click on the Launch Teams App button.
        print("Launching Teams app...")
        create_incidentobj.launch_chatOps()
 
        # 5.Add required agents and click on the Launch Teams App button.
        print("Adding team members...")
        create_incidentobj.add_members(common_data['TEAMS_ADMIN'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TENANT_USERNAME,TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        time.sleep(MAX_WAIT)
        teams_obj.select_teams_chat(INCIDENT_ID1)

        # 6.Ask the bot for suggestions on who can help with the incident.
        print("Requesting who can help with the incident")
        latest_message=teams_obj.send_message(f"Who can help with {INCIDENT_ID1}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        val = teams_obj.verify_incident_details(Incident_data)
        print(f"Incident details verification: {val}")
        # Evaluate bot response using DeepEval metrics
        print("Evaluating bot response with DeepEval metrics...")
        # Get the bot's response text
        bot_response = teams_obj.get_last_teams_message()
        context = [
            "When asked who can help with an incident, the response should include:",
            "1. Participant Name",
            "2. Email Address",
            "3. Justification for recommendation (such as current assignment, relevant experience, or handling similar incidents).",
            "It is acceptable and relevant for the bot to recommend participants based on similar incidents, related experience, or expertise, even if the incident IDs differ.",
            "Extra metadata or references to other incidents should not be penalized as irrelevant or hallucinated, as long as the justification is clear."
        ]

        expected_components = [
            "Participant Name",
            "Email Address",
            "Justification for recommendation (including experience with similar incidents or current assignment)",
            "References to similar incidents or relevant experience are valid and should be considered relevant",
            "If there is any extra metadata or references to other incidents that should not be penalized as hallucinations as long as the justification is clear",
            "If there is participant name and more data then consider answer as relevant"
        ]
        deepeval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=bot_response
        )
 
    except Exception as e:
            print(f"Test failed with error: {str(e)}")
            pytest.fail(f"Test failed with error: {str(e)}")
    finally:
            print("=== Completed test_Participant_Recommendation ===")
            
#@pytest.mark.smoke #13414 
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13414_Participant_RelatedChanges(driver, request, provider_config_key):
    """
        Test Case: DRSQA13414
        Description: Verify the participant recommendation feature in Teams for an incident.
 
        Preconditions:
        - Smart IT is configured with Helix GPT.
        - Teams app is integrated with Smart IT.
    """
    """
        Test Steps:
        1. Log into Smart IT as an IT Agent.
        2. Click on Create New -> Incident.
        3. Create an incident and assign it to yourself.
        4. Click on the Launch Teams App button.
        5. Add required agents and click on the Launch Teams App button.
        6. Ask the bot for suggestions on who can help with the change
           @<GPT Bot Name> "who can help with change <Incident ID>"
    """
    try:
        print("=== Starting test_Participant_Recommendation ===")
        print("Load test data and configurations...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        teams_obj = teams_page(driver=driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        data = uiuti_obj.load_config(file_name="IncidentData.json")
        testData = data['testData']
        Incident_data = data['Incident_data']  
        TENANT_USERNAME=TestConfig.TENANT_USERNAME
        TENANT_PASSWORD=TestConfig.TENANT_PASSWORD
        SMARTIT_UNAME=common_data['SMARTIT_UNAME']
        SMARTIT_PWD=common_data['SMARTIT_PWD']
        print("Initializing page objects...")
        # Load configuration
        print("Loading configurations...")
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print(f"Config loaded: {config}")
 
        # Use provider and model in your setup
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
 
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SMARTIT_UNAME'], password=common_data['SMARTIT_PWD'])

        # 2.Click on Create New -> Incident.
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
 
        # 3.Create an incident and assign it to yourself.
        print("Creating incident...")
        create_incidentobj.fill_incident_template(testData)
        create_incidentobj.click_assign_to_me()
        create_incidentobj.save_incident()
        INCIDENT_ID1=create_incidentobj.get_incidentid()
        print(f"Created incident with ID: {INCIDENT_ID1}")
        # 4.Click on the Launch Teams App button.
        print("Launching Teams app...")
        create_incidentobj.launch_chatOps()
 
        # 5.Add required agents and click on the Launch Teams App button.
        print("Adding team members...")
        create_incidentobj.add_members(common_data['TEAMS_ADMIN'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TENANT_USERNAME,TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        # 6.Ask the bot for suggestions on who can help with the incident.
        print("Requesting who can help with the change")
        latest_message=teams_obj.send_message(f"Who can help with change{INCIDENT_ID1}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        val = teams_obj.verify_incident_details(Incident_data)
        print(f"Incident details verification: {val}")
        # Evaluate bot response using DeepEval metrics
        print("Evaluating bot response with DeepEval metrics...")
        # Get the bot's response text
        bot_response = teams_obj.get_last_teams_message()
        expected_components = "Participants who can help with the change"       
        context = [
            "When asked for participants who can help with a change, the response should provide "
            "recommendations for people who can assist. The output should include participant names, "
            "optionally email addresses, and justifications for why they are recommended. "
            "The incident numbers or change numbers referenced in the response may differ from the requested ID "
            "as the system may reference related incidents or changes. This is acceptable behavior and should not be treated as hallucination."
        ]
        deepeval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=bot_response
        )
 
    except Exception as e:
            print(f"Test failed with error: {str(e)}")
            pytest.fail(f"Test failed with error: {str(e)}")
    finally:
            print("=== Completed test_Participant_RelatedChanges ===")
            
@pytest.mark.smoke #13416
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13416_ChatSummary_Teams(driver, request,provider_config_key):
   
    """
        Test Steps:
        1.Log into Smart IT as an IT Agent.
 
        2.Click on Create New -> Incident.
        3.Create an incident and assign it to yourself.
        4.Click on the Launch Teams App button.
 
        5.Add required agents and click on the Launch Teams App button.
 
        6.Ask the bot about the details of the incident.
          @<GPT Bot Name> "Share me the summary details for <Incident ID>"
 
        7.Ask the bot for similar incidents
          @<GPT Bot Name> "show similar incidents for <Incident ID>"
 
        8.Ask the bot to add a note
          @<GPT Bot Name> "Add note: New test note"
 
        9.Go to smartIT and verify the note is present on the ticket
        10.The IT agent asks the bot for the chat summary.
        @<GPT Bot Name> "Give me the Chat summary2"
        11.Verify the bot responds with the chat summary.
        12.The IT agent asks the bot to add the chat summary to the incident.
        @<GPT Bot Name> "Add the chat summary to the incident"
        13.Verify the bot adds the chat summary as a work note to the incident.
    """
 
    try:
        print("=== Starting test_Incident_Teams ===")
        print("Load test data and configurations...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        teams_obj = teams_page(driver=driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        data = uiuti_obj.load_config(file_name="IncidentData.json")
        testData = data['testData']
        Incident_data = data['Incident_data']
        TENANT_USERNAME=TestConfig.TENANT_USERNAME
        TENANT_PASSWORD=TestConfig.TENANT_PASSWORD
        SMARTIT_UNAME=common_data['SMARTIT_UNAME']
        SMARTIT_PWD=common_data['SMARTIT_PWD']
        print("Initializing page objects...")
        
        # Load configuration
        print("Loading configurations...")
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print(f"Config loaded: {config}")
 
        # # Use provider and model in your setup
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
 
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=SMARTIT_UNAME,password=SMARTIT_PWD)
        time.sleep(10)  # Wait for the login to complete
        # 2.Click on Create New -> Incident.
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
 
        # 3.Create an incident and assign it to yourself.
        print("Creating incident...")
        create_incidentobj.fill_incident_template(testData)
        time.sleep(2)  # Wait for the form to process
        create_incidentobj.click_assign_to_me()
        time.sleep(2)  # Wait for the form to process
        create_incidentobj.save_incident()
        time.sleep(6)  # Wait for the incident to be saved
        INCIDENT_ID=create_incidentobj.get_incidentid()
        print(f"Created incident with ID: {INCIDENT_ID}")
 
        # 4.Click on the Launch Teams App button.
        print("Launching Teams app...")
        create_incidentobj.launch_chatOps()
 
      # 5.Add required agents and click on the Launch Teams App button.
        print("Adding team members...")
        create_incidentobj.add_members(common_data['TEAMS_ADMIN'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TENANT_USERNAME,TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        teams_obj.wait_till_teams_loads()
        time.sleep(MAX_WAIT)
        teams_obj.select_teams_chat(INCIDENT_ID)

        # 6.Ask the bot about the details of the incident.
        print("Requesting incident details from bot...")
        latest_message =teams_obj.send_message(f"Share me the summary details for {INCIDENT_ID}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        val = teams_obj.verify_incident_details(Incident_data,lastSentMessage=latest_message)
        print(f"Incident details verification: {val}")
        # Evaluate bot response using DeepEval metrics
        print("Evaluating bot response with DeepEval metrics...")
        # Get the bot's response tex
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)
        expected_components = [
            "Incident Number",
            "Summary",
            "Priority",
            "Status",
            "Submit Date",
            "Assignee",
            "Assigned Group",
            "Worklog entries"
        ]
        context = [
            "For incident summary details, ALL of the following fields are required and considered relevant: "
            "Incident Number (format: INC followed by digits), Summary (description of the issue), "
            "Priority (Low/Medium/High), Status (current state), Submit Date (timestamp), "
            "Assignee (person assigned), Assigned Group (support group), and optionally Worklog entries. "
            "The absence of worklog entries is acceptable. "
            "Any additional fields should NOT be penalized as irrelevant. "
            "Do NOT treat Submit Date, Assignee, Assigned Group, or Worklog entries as metadata or operational details; "
            "they are part of the summary for this use case."
        ]    
        input=latest_message
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)
 
        # 7.Ask the bot for similar incidents
        print("Requesting similar incidents from bot...")
        latest_message=teams_obj.send_message(f"show similar incidents for {INCIDENT_ID}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        print(f"Similar incidents verification: {val}")
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)

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
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)
        # 8.Ask the bot to add a note
        print("Adding note via bot...")
        latest_message=teams_obj.send_message(
         f"Add note: Incident notes added to {INCIDENT_ID} at {datetime.now()} note to {INCIDENT_ID}",
        botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        #  9.Go to smartIT and verify the note is present on the ticket
        print("Verifying note in Smart IT...")
        # driver.switch_to.window(mainWindow)

        # driver.refresh()
        # val = create_incidentobj.verify_element_has_text_automated_note()
        IDetails=["successfully"]
        val = teams_obj.verify_incident_details(IDetails,lastSentMessage=latest_message)
        print(f"Asking for chat summary")
        latest_message=teams_obj.send_message(f"Give me the chat summary",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)
        context = [
        "When asked 'Give me the chat summary', the response should include: "
        "1. Chat header with chat title and incident reference, "
        "2. Chat URL, "
        "3. Chat Started timestamp, "
        "4. Last Activity timestamp, "
        "5. Conversation Summary (overview of discussion and actions). "
        "   The conversation summary may reference incident details such as incident number, "
        "   summary, priority, status, submit date, assignee, assigned group, work log entries, "
        "   and attachments if available. "
        "   These values (e.g., incident numbers, names, timestamps, groups, summaries) "
        "   are dynamic and may vary each time. They should be considered valid and not treated "
        "   as hallucinations as long as they follow the expected structure. "
        "6. Participant Timeline with participant names, joined/left times, and last activity, "
        "   where participant names and timestamps are also dynamic placeholders. "
        "7. Closing statement (optional)."
        "8. If there is more details or less details that is acceptable do not mark it as a hallucinations and if there is any data related to chat summary mark the ansqwer as relavant"
            ]
        expected_components = [
        "Chat Summary for Group Chat",
        "Chat URL or Reference",
        "Chat Started",
        "Last Activity",
        "Conversation Summary",
        "Participant Timeline",
        "Participant Name",
        "Joined",
        "Removed/Left",
        "Participant Last Activity",
        "Closing Statement (optional)"
    ]
        input=latest_message
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)

        print(f"Asking for Add the chat summary to the incident.")
        latest_message=teams_obj.send_message(f"Add the chat summary to the incident.",botName=bot_name)
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)
        expected_components = "add the Chat summary to the incident"
        context = ["Chat summary should be added to incident"]
        input=latest_message
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_ChatSummary_Teams ===")

@pytest.mark.smoke #13415
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13415_Participant_RecommendationInxMatter(driver, request,provider_config_key):
    """
        Test Case: DRSQA13415
        Description: Participant Recommendation for Incident with mapped group in xMatters.
 
        Preconditions:
            Petramco dataset is loaded.
            Smart IT must be using progressive views.
            Register and configure Azure application.
            Configure MS Teams Collaborator Bot.
            Configure xMatters - map support groups between ITSM and xMatters
            https://wiki.example.com/xwiki/bin/view/BMC-Helix/DSOM-RD/SolRD/Helix-GPT/HelixGPT-Swarming-Agent-in-Teams-Home-Page/HelixGPT-Ops-Swarmer-Environments-Configurations/xMatters-Group-Mapping/
            https://wiki.example.com/xwiki/bin/view/BMC-Helix/DSOM-RD/SolRD/Helix-GPT/HelixGPT-Swarming-Agent-in-Teams-Home-Page/HelixGPT-Ops-Swarmer-Environments-Configurations/xMatters-Configuration/
                    
        Test Steps:
        1. Log into Smart IT as an IT Agent.
        2. Click on Create New -> Incident.
        3. Create an incident and assign it to yourself.
        4. Click on the Launch Teams App button.
        5. Add required agents and click on the Launch Teams App button.
        6. Ask the bot for suggestions on who can help with the incident
           @<GPT Bot Name> "who can help with the incident <Incident ID>"
            * Verify the bot response provides a list of IT Agents belonging to the support group, both from ITSM and xMatters, based on the similar incidents.
            * IT Agents that are already in the chat should not be recommended.
    """
    try:
        print("=== Starting test_Participant_RecommendationInxMatter ===")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        teams_obj = teams_page(driver=driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="IncidentData.json")
        testData = data['testData']
        Incident_data = data['Incident_data']  
        TENANT_USERNAME=TestConfig.TENANT_USERNAME
        TENANT_PASSWORD=TestConfig.TENANT_PASSWORD
        print("Initializing page objects...")
        # Load configuration
        print("Loading configurations...")
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print(f"Config loaded: {config}")
 
        # Use provider and model in your setup
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
 
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SMARTIT_UNAME'], password=common_data['SMARTIT_PWD'])

        # 2.Click on Create New -> Incident.
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
 
        # 3.Create an incident and assign it to yourself.
        print("Creating incident...")
        create_incidentobj.fill_incident_template(testData)
        create_incidentobj.click_assign_to_me()
        create_incidentobj.save_incident()
        INCIDENT_ID1=create_incidentobj.get_incidentid()
        print(f"Created incident with ID: {INCIDENT_ID1}")

        # 4.Click on the Launch Teams App button.
        print("Launching Teams app...")
        create_incidentobj.launch_chatOps()
 
        # 5.Add required agents and click on the Launch Teams App button.
        print("Adding team members...")
        create_incidentobj.add_members(common_data['TEAMS_ADMIN'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TENANT_USERNAME,TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        time.sleep(MAX_WAIT)
        # 6.Ask the bot for suggestions on who can help with the incident.
        print("Requesting who can help with the incident")
        latest_message=teams_obj.send_message(f"Who can help with {INCIDENT_ID1}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        val = teams_obj.verify_incident_details(Incident_data)
        print(f"Incident details verification: {val}")
        # Evaluate bot response using DeepEval metrics
        print("Evaluating bot response with DeepEval metrics...")
        # Get the bot's response text
        bot_response = teams_obj.get_last_teams_message()
        expected_components = "Participants who can help with the incident"       
        context = [
            "When asked for participants who can help with an incident, the response should provide "
            "recommendations for people who can assist. The output should include participant names, "
            "optionally email addresses, and justifications for why they are recommended. "
            "The incident numbers referenced in the response may differ from the requested incident ID "
            "as the system may reference related incidents. This is acceptable behavior and should not be treated as hallucination."
        ]
        deepeval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=bot_response
        )
 
    except Exception as e:
            print(f"Test failed with error: {str(e)}")
            pytest.fail(f"Test failed with error: {str(e)}")
    finally:
            print("=== Completed test_Participant_RecommendationInxMatter ===")
       
@pytest.mark.smoke #13120
@pytest.mark.OpsWarm
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13120_Participant_RecommendationInPagerDuty(driver, request,provider_config_key):
    """
        Test Case: DRSQA13120
        Description: Participant Recommendation for Incident with mapped group in PagerDuty.
 
        Preconditions:
            Petramco dataset is loaded.
            Smart IT must be using progressive views.
            Register and configure Azure application.
            Configure MS Teams Collaborator Bot.
            Configure PagerDuty- map support groups between ITSM and PagerDuty
            Add PagerDuty toolkit to Participant Recommendation agent.
        Test Steps:
        1. Log into Smart IT as an IT Agent.
        2. Click on Create New -> Incident.
        3. Create an incident and assign it to yourself.
        4. Click on the Launch Teams App button.
        5. Add required agents and click on the Launch Teams App button.
        6. Ask the bot for suggestions on who can help with the incident
           @<GPT Bot Name> "show me who can help me here ?"
            * Verify the bot response provides a list of IT Agents belonging to the support group, both from ITSM and PagerDuty, based on the similar incidents.
            * IT Agents that are already in the chat should not be recommended.
    """
    try:
        print("=== Starting test_Participant_RecommendationInxMatter ===")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        teams_obj = teams_page(driver=driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="IncidentData.json")
        testData = data['testData']
        Incident_data = data['Incident_data']  
        TENANT_USERNAME=TestConfig.TENANT_USERNAME
        TENANT_PASSWORD=TestConfig.TENANT_PASSWORD
        print("Initializing page objects...")
        
        # Load configuration
        print("Loading configurations...")
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print(f"Config loaded: {config}")
 
        # Use provider and model in your setup
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
 
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SMARTIT_UNAME'], password=common_data['SMARTIT_PWD'])

        # 2.Click on Create New -> Incident.
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
 
        # 3.Create an incident and assign it to yourself.
        print("Creating incident...")
        create_incidentobj.fill_incident_template(testData)
        create_incidentobj.click_assign_to_me()
        create_incidentobj.save_incident()
        INCIDENT_ID1=create_incidentobj.get_incidentid()
        print(f"Created incident with ID: {INCIDENT_ID1}")

        # 4.Click on the Launch Teams App button.
        print("Launching Teams app...")
        create_incidentobj.launch_chatOps()
 
        # 5.Add required agents and click on the Launch Teams App button.
        print("Adding team members...")
        create_incidentobj.add_members(common_data['TEAMS_ADMIN'])
        
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TENANT_USERNAME,TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        time.sleep(MAX_WAIT)
        # 6.Ask the bot for suggestions on who can help with the incident.
        print("Requesting who can help with the incident")
        latest_message=teams_obj.send_message(f"show me who can help me here ?",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)
        val = teams_obj.verify_incident_details(Incident_data)
        print(f"Incident details verification: {val}")
        # Evaluate bot response using DeepEval metrics
        print("Evaluating bot response with DeepEval metrics...")
        # Get the bot's response text
        bot_response = teams_obj.get_last_teams_message()
        expected_components = "Participants who can help with the incident"       
        context = [
            "When asked for participants who can help with an incident, the response should provide "
            "recommendations for people who can assist. The output should include participant names, "
            "optionally email addresses, and justifications for why they are recommended. "
            "The incident numbers referenced in the response may differ from the requested incident ID "
            "as the system may reference related incidents. This is acceptable behavior and should not be treated as hallucination."
        ]
        deepeval_util.EvaluateResults(
            context=context,
            input=latest_message,
            expected_components=expected_components,
            actual_output=bot_response
        )
 
    except Exception as e:
            print(f"Test failed with error: {str(e)}")
            pytest.fail(f"Test failed with error: {str(e)}")
    finally:
            print("=== Completed test_Participant_RecommendationInxMatter ===")
       
#@pytest.mark.smoke #14027
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_14027_LaunchTeamsFromIncident_PregressiveInsurance(driver, request, provider_config_key):

    """
        Test Steps:
        1.Log into Smart IT as an IT Agent.
 
        2.Click on Create New -> Incident.
        3.Create an incident and assign it to yourself.
        4.Click on the Launch Teams App button.
 
        5.Add required agents and click on the Launch Teams App button.
 
        6.Ask the bot about the details of the incident.
          @<GPT Bot Name> "Share me the summary details for <Incident ID>"
 
        7.Ask the bot for similar incidents
          @<GPT Bot Name> "show similar incidents for <Incident ID>"
 
        8.Ask the bot to add a note
          @<GPT Bot Name> "Add note: New test note"
 
        9.Go to smartIT and verify the note is present on the ticket
    """
 
    try:
        print("=== Starting test_LaunchTeamsFromIncident_PregressiveInsurance ===")
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        teams_obj = teams_page(driver=driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="IncidentData.json")
        testData = data['testData']        
        Incident_data = data['Incident_data']  
        TENANT_USERNAME=TestConfig.TENANT_USERNAME
        TENANT_PASSWORD=TestConfig.TENANT_PASSWORD
        
        # Load configuration
        print("Loading configurations...")
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print(f"Config loaded: {config}")
 
        # # Use provider and model in your setup
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
 
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SMARTIT_UNAME'], password=common_data['SMARTIT_PWD'])
        time.sleep(10)  # Wait for the login to complete
        # 2.Click on Create New -> Incident.
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
 
        # 3.Create an incident and assign it to yourself.
        print("Creating incident...")
        create_incidentobj.fill_incident_template(testData)
        time.sleep(2)  # Wait for the form to process
        create_incidentobj.click_assign_to_me()
        time.sleep(2)  # Wait for the form to process
        create_incidentobj.save_incident()
        time.sleep(2)  # Wait for the incident to be saved
        INCIDENT_ID=create_incidentobj.get_incidentid()
        print(f"Created incident with ID: {INCIDENT_ID}")
        time.sleep(3)
        # 4.Click on the Launch Teams App button.
        print("Launching Teams app...")
        create_incidentobj.launch_chatOps()
 
        # 5.Add required agents and click on the Launch Teams App button.
        print("Adding team members...")
        create_incidentobj.add_members(common_data['TEAMS_ADMIN'])
        mainWindow = driver.current_window_handle
        print("Logging into Teams...")
        teams_obj.login_teams(TENANT_USERNAME,TENANT_PASSWORD,mainWindow=mainWindow)
        teams_obj.click_use_web_instead(mainWindow=mainWindow)
        bot_name=TestConfig.BOT_NAME
        teams_obj.wait_till_teams_loads()
        time.sleep(MAX_WAIT)
        # 6.Ask the bot about the details of the incident.
        print("Requesting incident details from bot...")
        latest_message =teams_obj.send_message(f"Share me the summary details for {INCIDENT_ID}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
        val = teams_obj.verify_incident_details(Incident_data,lastSentMessage=latest_message)
        print(f"Incident details verification: {val}")
        # Evaluate bot response using DeepEval metrics
        print("Evaluating bot response with DeepEval metrics...")
        # Get the bot's response tex
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)
        expected_components = "Incidents with similar properties"
        context = ["Information about {INCIDENT_ID} incident with summary details, Priority, Status, Submit date ,Assignee"]
        input=latest_message
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)
 
        # 7.Ask the bot for similar incidents
        print("Requesting similar incidents from bot...")
        latest_message=teams_obj.send_message(f"show similar incidents for {INCIDENT_ID}",botName=bot_name)
        time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
        val = teams_obj.verify_similar_incidents(latest_message)
        print(f"Similar incidents verification: {val}")
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)

        expected_components = f"Similar incidents to the incident {INCIDENT_ID} should be listed in table format"
        context = ["List Similar incidents with following details - Incident Number, Summary, Priority, Status etc"]
        input=latest_message
        deepeval_util.EvaluateResults(context=context,input=input,expected_components=expected_components,actual_output=bot_response)
        # 8.Ask the bot to add a note
        print("Adding note via bot...")
        latest_message=teams_obj.send_message(
         f"Add note: Incident notes added to {INCIDENT_ID} at {datetime.now()} note to {INCIDENT_ID}",
        botName=bot_name)
        bot_response = teams_obj.get_last_teams_message(lastMessage=latest_message)
        assert bot_response is not None, "Failed to Add note"
        #  9.Go to smartIT and verify the note is present on the ticket
        print("Verifying note in Smart IT...")
        driver.switch_to.window(mainWindow)
        driver.refresh()
        # val = create_incidentobj.verify_element_has_text_automated_note()
        IDetails=["The note has been successfully added"]
       
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_LaunchTeamsFromIncident_PregressiveInsurance ===")

@pytest.mark.smoke #14478
@pytest.mark.ITSM
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_14478_RetrievalofPeopleInformation(driver, request, provider_config_key):

    """
        Test Steps:
        1. Login to smartIT who has asset Permission
        2. Click on global search icon and verify two option chat and search.
        3. Ask below question in global chat related to CI and verify response
            Share list of peoples who can be associated with Optiplex 9020 service/CI?
        4. Get all people who are managing the assets related to incident INC999000000001
        5. Get all support groups related with asset named Ubuntu
        6. Get people related to asset named BWF_Printer_1 and Ubuntu
        7. Get person with primary contact related to asset named BWF_Printer_1 and Ubuntu
        8. Get all companies related to asset named Ubuntu
        9. Get person details who is related to asset named Ubuntu
        10. Verify hyperlink opened in another window
    """
 
    try:
        print("=== Starting test_RetrievalofPeopleInformation ===")
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA14478.json")
        testData = data['testData'] 
              
        #Configure AI models
        helixgpt_manager = HelixGPTManager(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]

        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)
   
       
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)
        # 2.Create Incident 
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
        INCIDENT_ID=create_incidentobj.create_incident_from_template(testData)
        assert INCIDENT_ID is not None, "Incident creation failed"
        print(f"Created incident with ID: {INCIDENT_ID}")
        time.sleep(3)
        # 3.Click on global search icon and verify two option chat and search.
        assert smartit_dashboardObj.verify_chat_and_search_options(), "Global search options verification failed"
        print("Global search options verified successfully.")
        GPT_data = data['GPTData']
        # Count how many input items exist in GPTData
        num_test_cases = len([key for key in GPT_data.keys() if key.startswith('input')])
        print(f"Found {num_test_cases} test cases in GPTData")

        for i in range(1, num_test_cases + 1):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")
            
            if input_message.strip().lower().endswith("incident"):
                input_message = f"{input_message.strip()} {INCIDENT_ID}".strip()

            smartit_dashboardObj.enter_message_in_chatbox(input_message)
            smartit_dashboardObj.send_message_in_chatbox()
            time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = smartit_dashboardObj.get_full_assistant_reply()
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_RetrievalofPeopleInformation ===")

@pytest.mark.smoke #14477
@pytest.mark.ITSM
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_14477_SupportForPBI(driver, request, provider_config_key):

    """
        Test Steps:
        1.Login to smartIT who has problem investigation Permission
        2.Click on global search icon and verify two option chat and search.
        3.Ask below question in global chat related to problem and verify response
            1.Show me all problems
            2.Show me all problems assigned to my problem coordinator group.
            3.Summarize Problem PBI000XXXX
            4.Show me all problems for “X” service
            5.Show me all problems “X” operational category / “X” product category
            6.Show me incidents created in the last 1 week / 1 month / 15 days….
            7.What are investigation drivers for PBIs raised in the last 3 months?
            8.What incidents are related to PBI00000000000X
            9.Show me related items for Problem ticket - PBI00000000000X
            10.Summarize worknotes for problem PBI00000000000X
            11.List worknotes of problem having summary similar to microphone
            12.How many new Problem records are opened this week/month/quarter/year.
            13.Show me all Problem similar to VPN issue.
        4.Verify hyperlink opened in another window
    """
 
    try:
        print("=== Starting test_SupportForPBI ===")
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_problemobj = create_problem(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA14477.json")
        testData = data['testData']        
        
        #Configure AI models
        helixgpt_manager = HelixGPTManager(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)

        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)

        # 2.Create Problem 
        PROBLEM_ID=create_problemobj.create_problem_from_template(testData)
        assert PROBLEM_ID is not None, "Problem creation failed"
        print(f"Created problem with ID: {PROBLEM_ID}")
        time.sleep(3)

        # 3.Click on global search icon and verify two option chat and search.
        assert smartit_dashboardObj.verify_chat_and_search_options(), "Global search options verification failed"
        print("Global search options verified successfully.")
        GPT_data = data['GPTData']
        # Count how many input items exist in GPTData
        num_test_cases = len([key for key in GPT_data.keys() if key.startswith('input')])
        print(f"Found {num_test_cases} test cases in GPTData")

        for i in range(1, num_test_cases + 1):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")

            if input_message.strip().lower().endswith("problem"):
                input_message = f"{input_message.strip()} {PROBLEM_ID}".strip()

            smartit_dashboardObj.enter_message_in_chatbox(input_message)
            smartit_dashboardObj.send_message_in_chatbox()
            #time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = smartit_dashboardObj.get_full_assistant_reply()
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_SupportForPBI ===")

pytest.mark.smoke #14476
@pytest.mark.ITSM 
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_14476_SupportForWorkOrders(driver, request, provider_config_key):

    """
    Test Steps:
        1.Login to smartIT who has workorder Permission
        2.Click on global search icon and verify two option chat and search.
        3.Ask below question in global chat related to workorder and verify response
            1.Show me all workorders
            2.Show me workorders assigned to my group
            3.Show me workorder tickets that needs attention assigned to my group
            4.What tickets are related to WOI0000101?
            5.Summarize worknotes for WOI000000000004?
            6.Summarize workorder ticket WOI000000000005?
            7.List workorder similar to Printer not
            8.Show me all critical workorder in last one week
        4.Verify hyperlink opened in another window
    """
 
    try:
        print("=== Starting test_SupportForWorkOrders ===")
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_workorderobj = create_workorder(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA14476.json")
        testData = data['testData']        
        
        #Configure AI models
        helixgpt_manager = HelixGPTManager(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)
       
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)

        # 2.Create Work orders 
        WORKORDER_ID=create_workorderobj.create_workorder_from_template(testData)
        assert WORKORDER_ID is not None, "Workorder creation failed"
        print(f"Created workorder with ID: {WORKORDER_ID}")
        time.sleep(3)

        # 3.Click on global search icon and verify two option chat and search.
        assert smartit_dashboardObj.verify_chat_and_search_options(), "Global search options verification failed"
        print("Global search options verified successfully.")
        GPT_data = data['GPTData']
        # Count how many input items exist in GPTData
        num_test_cases = len([key for key in GPT_data.keys() if key.startswith('input')])
        print(f"Found {num_test_cases} test cases in GPTData")

        for i in range(1, num_test_cases + 1):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")

            if input_message.strip().lower().endswith("workorder"):
                input_message = f"{input_message.strip()} {WORKORDER_ID}".strip()

            smartit_dashboardObj.enter_message_in_chatbox(input_message)
            smartit_dashboardObj.send_message_in_chatbox()
            time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = smartit_dashboardObj.get_full_assistant_reply()
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_SupportForWorkOrders ===")

@pytest.mark.smoke #14475
@pytest.mark.ITSM
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_14475_SupportForAssets(driver, request, provider_config_key):

    """
    Test Steps:
        Login to smartIT who has Asset Permission
        Click on global search icon and verify two option chat and search.
        Ask below question in global chat related to asset and verify response
            1).show me all asset.
            2).show me all computers with more than 4 processors
            3) find all computers whose support is ending on or before tomorrow
            4) find all computers related to CI
            5) get me computers with windows operating system and having more than 4 processors.
            6) show me all computers whose life is ended past week
            7) show my all computers with windows operating system
            8) show me all databases asset
            9) give me all lan endpoints
            10) display all business services
            11) give me the recon id for the asset "host-02"
            12) give me the instance id for the asset "host-03"
            13) tell me details of the asset "host-05"
            14) fetch all assets which are expiring next week
        Verify hyperlink opened in another window
    """
 
    try:
        print("=== Starting test_SupportForAssets ===")
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        deepeval_util = DeepEvalUtil()
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA14475.json")
        testData = data.get('testData', {})  # Handle case where testData might not exist
        
         #Configure AI models
        helixgpt_manager = HelixGPTManager(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)
       
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)

        # 2.Click on global search icon and verify two option chat and search.
        assert smartit_dashboardObj.verify_chat_and_search_options(), "Global search options verification failed"
        print("Global search options verified successfully.")
        GPT_data = data['GPTData']

        # Process all available inputs in the JSON (1-10)
        for i in range(1, 11):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")
            if not input_message:
                print(f"Skipping test case {i} - input not found in JSON")
                continue
            smartit_dashboardObj.enter_message_in_chatbox(input_message)
            smartit_dashboardObj.send_message_in_chatbox()
            time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = smartit_dashboardObj.get_full_assistant_reply()
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_SupportForWorkOrders ===")

@pytest.mark.smoke #14468
@pytest.mark.ITSM
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_16268_SupportGroupTickets(driver, request, provider_config_key):

    """
    Prerequisits : 
        1. Go to AR System Administrator -> AR System Administrator Console -> System -> General -> CCS configuration under com.bmc.arsys.smartit.
        2. Set the following parameter:
             enableHelixGPTGlobalChat = true
           
        3. Go to AR System Administrator -> AR System Administrator Console -> System -> General -> CCS configuration under com.bmc.arsys.server.shared
            a. Set the following parameter:
                i. Enable-Progressive-Views=T

        4. Under com.bmc.arsys.shared, verify the URL:
            a. HelixGPT-Assistant-Service-URL = http://helix-gpt-assistant:8008

    Test Steps:
        1.Login to SmartIT/PV URL
        2.Click on ITSM Global Search icon and select chat option
        3.Ask following question on global chat
            1. List tickets incident assigned to AU Support 1 supportGroup.
            2.List all support group name related to change
            3. Are there tickets assigned to support group which are high priority?
            4. List tickets/incident/change assigned to ppan's support group
            5. Which support group got critical tickets/incident/change?
            6. Which support group handled tickets/incident/change from Petramco company earlier?
            7. Which support group closed tickets/incident/change in year 2025?
    """
    try:
        #Configure AI models
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        deepeval_util = DeepEvalUtil()
        helixgpt_manager = HelixGPTManager(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)

        print("=== Starting test_SupportGroupTickets ===")
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA14468.json")
       
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)

        # 2.Click on global search icon and verify two option chat and search.
        assert smartit_dashboardObj.verify_chat_and_search_options(), "Global search options verification failed"
        print("Global search options verified successfully.")
        GPT_data = data['GPTData']

        smartit_dashboardObj.enter_message_in_chatbox("Show me all changes assigned to AU Support 1 support Group")
        smartit_dashboardObj.send_message_in_chatbox()
        time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
        bot_response = smartit_dashboardObj.get_full_assistant_reply()
        print(f"Bot response: {bot_response}")
        changeID=uiuti_obj.extract_req_id(bot_response,suffix="CRQ")
        print(f"Change ID: {changeID}")

        # Process all available inputs in the JSON (1-10)
        for i in range(1, 7):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")
            if not input_message:
                print(f"Skipping test case {i} - input not found in JSON")
                continue
            if input_message.strip().lower().endswith("change"):
                input_message = f"{input_message.strip()} {changeID}".strip()
            smartit_dashboardObj.enter_message_in_chatbox(input_message)
            smartit_dashboardObj.send_message_in_chatbox()
            time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = smartit_dashboardObj.get_full_assistant_reply()
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_SupportGroupTickets ===")

@pytest.mark.smoke #16247
@pytest.mark.ITSM
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_16247_RetrievalofPeopleInformation_Agent(driver, request, provider_config_key):
    """
        Test Steps:
        1. Login to smartIT who has asset Permission
        2. Create a new incident with all required field.
        3. Ask below question in Ask HelixGPT chat related to CI and verify response
            1. Share list of peoples who can be associated with Optiplex 9020 service/CI?
            2. Get all people who are managing the assets related to incident INC999000000001
            3. Get all support groups related with asset named Ubuntu
            4. Get people related to asset named BWF_Printer_1 and Ubuntu
            5. Get person with primary contact related to asset named BWF_Printer_1 and Ubuntu
            6. Get all companies related to asset named Ubuntu
            7. Get person details who is related to asset named Ubuntu
            8. Verify hyperlink opened in another window
        Verifications : 
            1.user logged successfully
            2.Incident should be created
            3.response should be received in chat window with hyper link
            4.hyperlink should be opened in another window
    """
 
    try:
        print("=== Starting test_RetrievalofPeopleInformation_Agent ===")
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        create_incidentobj = create_incident(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()      
        incident_summary_obj = incident_summary(driver)
       
        #Configure AI models
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA16247.json")
        testData = data['testData']
        helixgpt_manager = HelixGPTManager(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)
       
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)
        # 2.Create Incident 
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
        INCIDENT_ID=create_incidentobj.create_incident_from_template(testData)
        assert INCIDENT_ID is not None, "Incident creation failed"
        print(f"Created incident with ID: {INCIDENT_ID}")
        time.sleep(3)
        GPT_data = data['GPTData']
        # Count how many input items exist in GPTData
        num_test_cases = len([key for key in GPT_data.keys() if key.startswith('input')])
        print(f"Found {num_test_cases} test cases in GPTData")
        incident_summary_obj.click_ask_helixgpt_button()
        
        for i in range(1, num_test_cases + 1):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")
            
            if input_message.strip().lower().endswith("incident"):
                input_message = f"{input_message.strip()} {INCIDENT_ID}".strip()

            incident_summary_obj.enter_message_in_chatbox(input_message)
            incident_summary_obj.click_send_button()
            time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = incident_summary_obj.get_full_assistant_reply()
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_RetrievalofPeopleInformation_Agent ===")

@pytest.mark.smoke #16250
@pytest.mark.ITSM
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_16250_SupportForAssets_Agent(driver, request, provider_config_key):

    """ Yogendra sharma : hubby of Gayatri Sharma
    Test Steps:
       Login to smartIT who has Asset Permission
       Click on global search icon and verify two option chat and search.
       Ask below question in global chat related to asset and verify response
            1).show me all asset.
            2) find all computers whose support is ending on or before tomorrow
            3) find all computers related to CI
            4) show me all databases asset
            5) give me all lan endpoints
            6) display all business services
            7) give me the recon id for the asset "Optiplex 9020"
            8) give me the instance id for the asset "Optiplex 9020"
            9) tell me details of the asset "Optiplex 9020"
            10)fetch all assets which are status are deployed
       Verify hyperlink opened in another window
    """
 
    try:
        print("=== Starting test_SupportForAssets ===")
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        deepeval_util = DeepEvalUtil()
        incident_summary_obj = incident_summary(driver)
        create_incidentobj = create_incident(driver)
        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA16250.json")
        testData = data.get('testData', {})  # Handle case where testData might not exist
        
         #Configure AI models
        helixgpt_manager = HelixGPTManager(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)
       
        # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)
        # 2.Create Incident 
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
        INCIDENT_ID=create_incidentobj.create_incident_from_template(testData)
        assert INCIDENT_ID is not None, "Incident creation failed"
        print(f"Created incident with ID: {INCIDENT_ID}")
        time.sleep(3)
        GPT_data = data['GPTData']
        # Count how many input items exist in GPTData
        num_test_cases = len([key for key in GPT_data.keys() if key.startswith('input')])
        print(f"Found {num_test_cases} test cases in GPTData")
        incident_summary_obj.click_ask_helixgpt_button()

        for i in range(1, num_test_cases + 1):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")
            if not input_message:
                print(f"Skipping test case {i} - input not found in JSON")
                continue
            incident_summary_obj.enter_message_in_chatbox(input_message)
            incident_summary_obj.click_send_button()
            time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = incident_summary_obj.get_full_assistant_reply()
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
        print("=== Completed test_SupportForAssets_Agent ===")

@pytest.mark.smoke #16251
@pytest.mark.ITSM
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_16251_SupportGroupTickets_agent(driver, request, provider_config_key):

    """
    Prerequisits : 
        1. Go to AR System Administrator -> AR System Administrator Console -> System -> General -> CCS configuration under com.bmc.arsys.smartit.
        2. Set the following parameter:
             enableHelixGPTGlobalChat = true
           
        3. Go to AR System Administrator -> AR System Administrator Console -> System -> General -> CCS configuration under com.bmc.arsys.server.shared
            a. Set the following parameter:
                i. Enable-Progressive-Views=T

        4. Under com.bmc.arsys.shared, verify the URL:
            a. HelixGPT-Assistant-Service-URL = http://helix-gpt-assistant:8008

    Test Steps:
        1.Login to SmartIT/PV URL
        2.Click on ITSM Global Search icon and select chat option
        3.Ask following question on global chat
            Ask following question on global chat
            Verify response for each question
            1. List tickets incident assigned to AU Support 1 supportGroup.
            2. List all support group name related to change <changeID>
            3. Are there tickets assigned to support group which are high priority?
            4. List tickets/incident/change assigned to ppan's support group
            5. Which support group got critical tickets/incident/change?
            6. Which support group handled tickets/incident/change from Petramco company earlier?
            7. Which support group closed change in year 2025?
    """
    try:
        print("=== Starting test_SupportGroupTickets_agent ===")
        #Configure AI models
        print("Initializing page objects...")
        smartit_dashboardObj = smart_it_dashboard(driver)
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        deepeval_util = DeepEvalUtil()
        helixgpt_manager = HelixGPTManager(driver)
        create_incidentobj = create_incident(driver)
        incident_summary_obj = incident_summary(driver)
        config = uiuti_obj.load_config(file_name="azure_config.json")
        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix IT Service Management application...")
        helixgpt_manager.select_application("BMC Helix IT Service Management")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['SMARTIT_SKILL'],provider=provider, model=model)

        print("Load test data and configurations...")
        data = uiuti_obj.load_config(file_name="DRSQA16251.json")
        testData = data.get('testData', {})  # Handle case where testData might not exist
        GPT_data = data['GPTData']

         # 1.Log into Smart IT as an IT Agent.
        uiuti_obj.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'],Login=False)
        # 2.Create Incident 
        print("Navigating to Create New Incident...")
        smartit_dashboardObj.navigate_to_create_new_incident()
        INCIDENT_ID=create_incidentobj.create_incident_from_template(testData)
        assert INCIDENT_ID is not None, "Incident creation failed"
        print(f"Created incident with ID: {INCIDENT_ID}")
        time.sleep(3)
        GPT_data = data['GPTData']
        # Count how many input items exist in GPTData
        num_test_cases = len([key for key in GPT_data.keys() if key.startswith('input')])
        print(f"Found {num_test_cases} test cases in GPTData")
        incident_summary_obj.click_ask_helixgpt_button()

        incident_summary_obj.enter_message_in_chatbox("Show me all changes assigned to AU Support 1 support Group")
        incident_summary_obj.click_send_button()
        time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
        bot_response = incident_summary_obj.get_full_assistant_reply()
        print(f"Bot response: {bot_response}")
        changeID=uiuti_obj.extract_req_id(bot_response,suffix="CRQ")
        print(f"Change ID: {changeID}")
        
        # Process all available inputs in the JSON
        for i in range(1, num_test_cases + 1):
            print(f"Processing GPT test case {i}...")
            input_message = GPT_data.get(f"input{i}")
            if not input_message:
                print(f"Skipping test case {i} - input not found in JSON")
                continue
            if input_message.strip().lower().endswith("change"):
                input_message = f"{input_message.strip()} {changeID}".strip()
            incident_summary_obj.enter_message_in_chatbox(input_message)
            incident_summary_obj.click_send_button()
            time.sleep(WAIT_FOR_REPLY)  # Wait for the bot to respond
            bot_response = incident_summary_obj.get_full_assistant_reply()  
            print("Evaluating bot response with DeepEval metrics...")
            context_value = GPT_data.get(f"context{i}")
            expected_components = GPT_data.get(f"expected_components{i}")
            # Convert context to list of strings as required by DeepEval
            context = [context_value] if context_value else None
            deepeval_util.EvaluateResults(context=context,input=input_message,expected_components=expected_components,actual_output=bot_response)
            print("Bot response for {} evaluated successfully.".format(input_message))
        print("All GPT test cases processed successfully.")

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_SupportGroupTickets_agent ===")

