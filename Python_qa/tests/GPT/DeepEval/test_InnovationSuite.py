import os
import sys
import time
from importlib_metadata import files
import pytest
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
sys.path.append(project_root)
from pages.ITSM.DWP.dwp_landing import dwp_landing
from pages.GPT.HelixGPT.helixgpt_manager import HelixGPTManager
from pages.GPT.DeepEval.DeepEvalUtil import DeepEvalUtil
from utils.ui_utils import utilities
from test_config import TestConfig
from pages.GPT.HelixGPT.agent_studio import agentStudio
from pages.ITSM.DWP.dwp_administration import dwp_administration
import json
PRODUCT_TYPE = "GPT"
TEST_DESCRIPTION = "GPT Testcases"
LONGWAIT = 30

def __init__():
   pass

__init__()

@pytest.mark.smoke #13287
@pytest.mark.HVA
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13287_HelixGPTManager_ResponseToImage(driver, request, provider_config_key):
    """
    Test Case: DRSQA-13287
        Precondition:
        Add DWP image prompt to required skill
        Test Steps:
        1. Launch HelixGPT Manager app
        2. Select Application as BMC Digital Workplace
        3. Select any skill you want to use and add "DWP Image prompt"
        4. Go to test option for same skill and upload the attachment
        5. Verify test bot responds correctly responds to the image uploaded
        Verifications:
        1. Admin logins to HelixGPT manager app
        2. DWP Application selected successfully
        3. Prompt is successfully added
        4. Image should be uploaded successfully
    """
 
    try:
        print("=== Starting test_HelixGPTManager_ResponseToImage ===")
        print("Initializing page objects...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        agentStudioObj = agentStudio(driver)
        config = uiuti_obj.load_config("azure_config.json")
        # 1. Launch HelixGPT Manager app
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        # 2. Select Application as BMC Digital Workplace
        print("Selecting BMC Helix Digital Workplace application...")
        helixgpt_manager.select_application("BMC Helix Digital Workplace")
        
        #Select GPT Model
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]

        helixgpt_manager.configure_agent(skill_name=common_data['SKILL_NAME'],provider=provider, model=model)
        # 3. Select any skill you want to use and add "DWP Image prompt"
        print("Launching GPT skill for testing...")
        helixgpt_manager.launch_gpt_from_skill(skill_name=common_data['SKILL_NAME'])
        # 4. Go to test option for same skill and upload the attachment
        print("Attaching image file to the bot...")        
        agentStudioObj.click_attach_files_button()
        time.sleep(2)  # Wait for the file upload dialog to appear
        uiuti_obj.upload_file_with_autoit(os.path.join(project_root, "testdata", "GPT", "13287.jpg"))
        time.sleep(1)  # Wait for the file to be uploaded
        agentStudioObj.send_message()
        time.sleep(10)  # Wait for the bot to respond
        # 5. Verify test bot responds correctly responds to the image uploaded
        print("Evaluating bot response with DeepEval metrics...")
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)
        expected_components = [
    "Steps to fix paper jams in different parts of a printer (e.g., exit cover, trays, document feeder)",
    "Possible causes of printer paper jams (e.g., overfilled tray, improper paper size)",
    "Preventive measures to avoid future jams (e.g., loading paper correctly, keeping printer clean)"
        ]

        context = [
    "Printer troubleshooting help",
    "Common printer hardware issues like paper jams and maintenance"
        ]

        input = "Image showing a jammed printer with the heading '[QUICK FIX] How To Fix Paper Jam in Printer?' and URL linking to a printer support page"
        deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)
        print("Test completed successfully. Bot response evaluated.")
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_HelixGPTManager_ResponseToImage ===")

@pytest.mark.smoke #14025
@pytest.mark.HVA
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_14025_HelixGPTManager_ResponsePrinterImage(driver, request, provider_config_key):
    """
    Test Case: DRSQA-14025
        Precondition:AC1: Ability to test prompts, see the combination of Question, Prompts and Context sent to the LLM, as well as the asnwer

        AC2, Ability to see the chain of steps and variables Globally, within the steps and passed between the steps.

        AC3: Ability to analyze the Answer and get it rated

        AC4 Ability to get recommendation on how to improve the prompts

 
        Verify:
        Image upload testing should work 
        Input token end nodes should be available in logs

        Add DWP image prompt to required skill
        Test Steps:
        1. Launch HelixGPT Manager app
        2. Select Application as BMC Digital Workplace
        3. Select any skill you want to use and add "DWP Image prompt"
        4. Go to test option for same skill and upload the attachment
        5. Verify test bot responds correctly responds to the image uploaded
        Verifications:
        1. Admin logins to HelixGPT manager app
        2. DWP Application selected successfully
        3. Prompt is successfully added
        4. Image should be uploaded successfully
    """
 
    try:
        print("=== Starting test_HelixGPTManager_ResponsePrinterImage ===")
        print("Initializing page objects...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        agentStudioObj = agentStudio(driver)
        config = uiuti_obj.load_config("azure_config.json")
        # 1. Launch HelixGPT Manager app
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        # 2. Select Application as BMC Digital Workplace
        print("Selecting BMC Helix Digital Workplace application...")
        helixgpt_manager.select_application("BMC Helix Digital Workplace")
        
        #Select GPT Model
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]

        helixgpt_manager.configure_agent(skill_name=common_data['SKILL_NAME'],provider=provider, model=model)
        # 3. Select any skill you want to use and add "DWP Image prompt"
        print("Launching GPT skill ",common_data['SKILL_NAME'])
        helixgpt_manager.launch_gpt_from_skill(skill_name=common_data['SKILL_NAME'])
        # 4. Go to test option for same skill and upload the attachment
        print("Attaching image file to the bot...")        
        agentStudioObj.click_attach_files_button()
        time.sleep(2)  # Wait for the file upload dialog to appear
        uiuti_obj.upload_file_with_autoit(os.path.join(project_root, "testdata", "GPT", "13287.jpg"))
        time.sleep(1)  # Wait for the file to be uploaded
        agentStudioObj.send_message()
        time.sleep(10)  # Wait for the bot to respond
        # 5. Verify test bot responds correctly responds to the image uploaded
        print("Evaluating bot response with DeepEval metrics...")
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)
        expected_components = [
    "Steps to fix paper jams in different parts of a printer (e.g., exit cover, trays, document feeder)",
    "Possible causes of printer paper jams (e.g., overfilled tray, improper paper size)",
    "Preventive measures to avoid future jams (e.g., loading paper correctly, keeping printer clean)"
        ]

        context = [
    "Printer troubleshooting help",
    "Common printer hardware issues like paper jams and maintenance"
        ]

        input = "Image showing a jammed printer with the heading '[QUICK FIX] How To Fix Paper Jam in Printer?' and URL linking to a printer support page"
        deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)
        print("Test completed successfully. Bot response evaluated.")
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_HelixGPTManager_ResponsePrinterImage ===")
 
@pytest.mark.smoke #13275
@pytest.mark.EN
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13275_HelixGPT_InlineLinksWithinTable(driver, request, provider_config_key):
    """
    Test Case: DRSQA-13275

        Precondition:
        Add DWP image prompt to required skill
        Test Steps:
        1. Launch HelixGPT Manager app
        2. Select Application as BMC Digital Workplace
        3. Select any skill you want to use and add "DWP Image prompt"
        4. Go to test option for same skill and upload the attachment
        5. Verify test bot responds correctly responds to the image uploaded

        Verifications:
            Log in to DWP as a requester and open the pre-created studio page with the DWP Employee Navigator Agent - V1

            Open chat and type "show me all active events"

            Open the request details through the ID link.

            Click on the "Search" button at the left-right corner to get back to the chat

            Cancel any approval request
            Type "cancel event "ID"

            Type "yes"

            Type "show my past events"
            """
    try:
        print("=== Starting test_HelixGPT_InlineLinksWithinTable ===")
        print("Initializing page objects...")

        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        agentStudioObj = agentStudio(driver)
        dwp_adminObj= dwp_administration(driver)
        dwp_landingObj= dwp_landing(driver)
        config = uiuti_obj.load_config("azure_config.json")

        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix Digital Workplace application...")
        helixgpt_manager.select_application("BMC Helix Digital Workplace")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['DWP_EMP_SKILL'],provider=provider, model=model)

        print("Enable Chat and search configuration in DWP Admin with skill "+common_data['DWP_EMP_SKILL'])
        dwp_adminObj.navigate_to_dwp_search(common_data['ADE_USERNAME'],common_data['ADE_PASSWORD'])
        dwp_adminObj.enable_ai_chat() 
        dwp_adminObj.select_skill(common_data['DWP_EMP_SKILL'])
        dwp_adminObj.save_configuration()
        time.sleep(5)  # Wait for the configuration to be saved
        dwp_adminObj.navigate_to_dwp_chatbot()  
        dwp_adminObj.select_technology("Employee Navigator")
        dwp_adminObj.select_skill(common_data['DWP_EMP_SKILL'])
        dwp_adminObj.save_configuration()
        time.sleep(5)  # Wait for the configuration to be saved
        dwp_adminObj.logout()
        time.sleep(6)
        dwp_landingObj.navigate_to_dwp_landing(common_data['END_USER'],common_data['ADE_PASSWORD'])
        dwp_landingObj.enter_message_and_send("show me all active events")
        time.sleep(LONGWAIT)  # Wait for the bot to respond
        # 5. Verify test bot responds correctly responds to the image uploaded
        print("Evaluating bot response with DeepEval metrics...")
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)

        expected_components = [
    "The response should directly address the user's question or request.",
    "If the answer is a list or table, it should include clear labels or headings for each item or column.",
    "If the answer is a summary, it should concisely capture the main points relevant to the user's input.",
    "If the answer is an explanation, it should be clear, factual, and relevant to the user's context.",
    "If the answer includes actions, instructions, or suggestions, they should be actionable and appropriate.",
    "If the answer references data (IDs, names, statuses, etc.), these should be accurate and consistent with the context.",
    "If the answer includes multiple sections (e.g., events by status), each section should be clearly separated and labeled.",
    "If the answer cannot be determined, the bot should politely indicate that and offer to help further.",
    "Any additional information should not be treated as hallucinated or fabricated content."
        ]

        context = [
    "The chatbot's response should be factual, relevant, and directly address the user's input.",
    "The response may be a direct answer, a list, a table, a summary, an explanation, or a combination, depending on the input.",
    "If grouping or categorization is appropriate (e.g., events by status), the response should clearly group and label each section.",
    "All data presented should be accurate and not contradict the context or known facts.",
    "No hallucinated, fabricated, or irrelevant information should be present.",
    "If the bot cannot answer, it should state so clearly and offer further assistance."
        ]
        input = "show me all active events"
        deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)
        
        assert agentStudioObj.open_table_link(),"Failed to locate or open link from table"
        time.sleep(5)
        assert uiuti_obj.verify_browser_title("Digital Workplace - Details"),"Failed to open request details page from Bot response"
        dwp_landingObj.click_switch_to_full_chat()
        REQ_ID=uiuti_obj.extract_req_id(bot_response)
        agentStudioObj.enter_message("cancel event "+REQ_ID)
        agentStudioObj.send_message()
        time.sleep(LONGWAIT)
        agentStudioObj.enter_message("yes")
        agentStudioObj.send_message()
        time.sleep(LONGWAIT)
        agentStudioObj.enter_message("show my past events")
        agentStudioObj.send_message()
        time.sleep(LONGWAIT)  # Wait for the bot to respond
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)
        assert REQ_ID in bot_response, "Cancelled request ID not found in bot response"
        
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_HelixGPT_InlineLinksWithinTable ===")

@pytest.mark.smoke #15150
@pytest.mark.EN
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_15150_HelixGPT_CopyButtonForResults(driver, request, provider_config_key):
    """
    Test Case: DRSQA-15150

        Precondition:
        In DWP Admin Console, go to Configuration > Chat.
        Enable Employee Navigator from the Technology dropdown.
        Choose the needed skill from the Skill dropdown.
        Click Save.
        In DWP Admin Console, go to Configuration > Search.
        Turn on AI chat (core pages) toggle.
        Select the right skill.
        Click Save.
        Skills to test:
        DWP Employee Navigator Agent - V1
       
        Test Steps:
            1.In DWP Helix GPT chat, ask test questions. Each reply from LLM should have a copy text button at the bottom.
            2.Go to DWP Activity, click Global Panel, ask the same questions, and check the copy text button shows below each response.
        Test Data :
            1.End user:bhill/__USER_PASSWORD__
            2.Show my requests
            3.Show my todos
            4.Show users with last name Admin
            5.Show my favorite service health item
            6.Find services about laptops (agent skills only)
            7.Add a comment to request <active service request ID> (agent skills only)
            8.What is the maternity leave policy? (or another knowledge article)
        Verifications:
            1.User can see a copy text button below each response.
            2.User can see the global panel icon and the copy text button under each response there
            """
    try:
        print("=== Starting test_HelixGPT_InlineLinksWithinTable ===")
        print("Initializing page objects...")

        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        agentStudioObj = agentStudio(driver)
        dwp_adminObj= dwp_administration(driver)
        dwp_landingObj= dwp_landing(driver)
        config = uiuti_obj.load_config("azure_config.json")

        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix Digital Workplace application...")
        helixgpt_manager.select_application("BMC Helix Digital Workplace")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['DWP_EMP_SKILL'],provider=provider, model=model)

        print("Enable Chat and search configuration in DWP Admin with skill "+common_data['DWP_EMP_SKILL'])
        dwp_adminObj.navigate_to_dwp_search(common_data['ADE_USERNAME'],common_data['ADE_PASSWORD'])
        dwp_adminObj.enable_ai_chat() 
        dwp_adminObj.select_skill(common_data['DWP_EMP_SKILL'])
        dwp_adminObj.save_configuration()
        time.sleep(5)  # Wait for the configuration to be saved
        dwp_adminObj.navigate_to_dwp_chatbot()  
        dwp_adminObj.select_technology("Employee Navigator")
        dwp_adminObj.select_skill(common_data['DWP_EMP_SKILL'])
        dwp_adminObj.save_configuration()
        time.sleep(5)  # Wait for the configuration to be saved
        dwp_adminObj.logout()
        time.sleep(6)
        dwp_landingObj.navigate_to_dwp_landing(common_data['END_USER'], common_data['END_USER_PASSWORD'])
        dwp_landingObj.enter_message_and_send("show my requests with request number")
        time.sleep(LONGWAIT)  # Wait for the bot to respond
        print("Evaluating bot response with DeepEval metrics...")
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)

        serviceRequestID=uiuti_obj.extract_req_id(bot_response,suffix="REQ")
        if not serviceRequestID:
            serviceRequestID=uiuti_obj.extract_req_id(bot_response,suffix="CTPL")

        bot_response = bot_response.replace('\n', '').replace('\r', '')
        expected_components = [
    "The response should directly address the user's question or request.",
    "If the answer is a summary, it should concisely capture the main points relevant to the user's input.",
    "If the answer is an explanation, it should be clear, factual, and relevant to the user's context.",
    "If the answer includes actions, instructions, or suggestions, they should be actionable and appropriate.",
    "If the answer references data (IDs, names, statuses, etc.), these should be accurate and consistent with the context.",
    "If the answer includes multiple sections (e.g., events by status), each section should be clearly separated and labeled.",
    "If the answer cannot be determined, the bot should politely indicate that and offer to help further.",
    "Any additional information should not be treated as hallucinated or fabricated content."
        ]

        context = [
    "The chatbot's response should be factual, relevant, and directly address the user's input.",
    "The response may be a direct answer, a list, a table, a summary, an explanation, or a combination, depending on the input.",
    "If grouping or categorization is appropriate (e.g., events by status), the response should clearly group and label each section.",
    "All data presented should be accurate and not contradict the context or known facts.",
    "No hallucinated, fabricated, or irrelevant information should be present.",
    "If the bot cannot answer, it should state so clearly and offer further assistance."
        ]
        input = "show my requests with request number"
        deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)
      
        
        dwp_landingObj.enter_message("Show my todos")
        dwp_landingObj.click_send_button()
        time.sleep(LONGWAIT)  # Wait for the bot to respond
       
        dwp_landingObj.enter_message("Show users with last name Admin")
        dwp_landingObj.click_send_button()
        time.sleep(LONGWAIT)  # Wait for the bot to respond
      
        dwp_landingObj.enter_message("Show my favorite service health item")
        dwp_landingObj.click_send_button()
        time.sleep(LONGWAIT)  # Wait for the bot to respond
      
        dwp_landingObj.enter_message("Find services about laptops (agent skills only)")
        dwp_landingObj.click_send_button()
        time.sleep(LONGWAIT)  # Wait for the bot to respond
      
        dwp_landingObj.enter_message("Add a comment to request "+serviceRequestID+" comment from user")
        dwp_landingObj.click_send_button()
        time.sleep(LONGWAIT)  # Wait for the bot to respond
      
        dwp_landingObj.enter_message("What is the maternity leave policy?")
        dwp_landingObj.click_send_button()
        time.sleep(LONGWAIT)  # Wait for the bot to respond
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        assert agentStudioObj.verify_copy_buttons(count=7),"Copy buttons are not present as expected"
        
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_HelixGPT_CopyButtonForResults ===")

@pytest.mark.smoke #14668
@pytest.mark.EN
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_14668_HelixGPT_ViewComponant(driver, request, provider_config_key):
    """
    Test Case: DRSQA-14668
        Precondition:
        Add DWP image prompt to required skill
        Test Steps:
            1.Login to Innovation studio
            2.Navigate to HelixGPT Agent Studio Application
            3.Click on view tab then click on (+New) Dropdown->Container and drag and drop HelixGPT Chat
            4.On HelixGPT Chat Select Allow Attachement toggle and Select Skill: BWF Case Conversation Skill and click on save and open preview
            5.Verify PNG format:
            6.Attach Printer_issue_image
            .png image
            7.Verify JPG Format:
            8.Attach Printer issue image
            .jpg
            9.Verify JPEG format:
            10.Printer_issue_image_converted
            .jpeg
            11.Verify WEBP Format:
            vpn_issue
            .webp
            12.Verify unsupported file type format
            like pdf, .doc,
            13.Verify error is shown for file exceeding size limit
            14.Verify only 3 images user can upload if images more that 3 it should give error
            15.Enter Value...
            hannah_admin/__USER_PASSWORD__
            Enter Value...
            16.User logged successfully
            17.HelixGPT Agent Studio application should be open
            18.New View should be open
            19.Attachment Symbol should be visible on preview
            20.Response should be received in chat view
            21.Response should be received in chat view
            22.Response should be received in chat view
            23.Response should be received in chat view
            24.csv files are not supported. Supported files include: png,jpeg,jpg,webp,gif.
            25.Some files exceeded the maximum file size of 2.00 MB and were not included. Show files
            26.drag and drop files here
            vpn_issue
            .webp
            Printer_issue_image_converted
            .jpeg
            Printer_issue_image
            .png
            27.Maximum file count exceeded (3). 1 file(s) not included in the upload. Show files
            28.Add new files by deleting an existing file."""
    try:
        print("=== Starting test_HelixGPT_InlineLinksWithinTable ===")
        print("Initializing page objects...")

        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        agentStudioObj = agentStudio(driver)
        dwp_adminObj= dwp_administration(driver)
        dwp_landingObj= dwp_landing(driver)
        config = uiuti_obj.load_config("azure_config.json")

        assert agentStudioObj.create_new_view_verify(),"Failed to create new view in HelixGPT Agent Studio"

        # # 1. Launch HelixGPT Manager app
        # print("Logging in to HelixGPT Manager...")
        # helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        # # 2. Select Application as BMC Digital Workplace
        # print("Selecting BMC Helix Digital Workplace application...")
        # helixgpt_manager.select_application("BMC Helix Digital Workplace")
        
        # #Select GPT Model
        # provider_config = config[provider_config_key]
        # provider = provider_config["provider"]
        # model = provider_config["model"]

        # helixgpt_manager.configure_agent(skill_name=common_data['SKILL_NAME'],provider=provider, model=model)
        # # 3. Select any skill you want to use and add "DWP Image prompt"
        # print("Launching GPT skill for testing...")
        # helixgpt_manager.launch_gpt_from_skill(skill_name=common_data['SKILL_NAME'])
        # # 4. Go to test option for same skill and upload the attachment

        files = [os.path.join(project_root, 'testdata', 'GPT', '14668.png'),
                 os.path.join(project_root, 'testdata', 'GPT', '14668.jpeg'),        
                 os.path.join(project_root, 'testdata', 'GPT', '14668.jpg'),
                 os.path.join(project_root, 'testdata', 'GPT', '14688.webp')]    
        
        test_data_file = uiuti_obj.load_config("DRSQA14668.json")
        expected_components = test_data_file["GPTData"]["expected_components"]
        context = test_data_file["GPTData"]["context"]
        input = test_data_file["GPTData"]["input"]
        
        # Ensure context is a list of strings as required by DeepEval
        if context is not None and isinstance(context, str):
            context = [context]
        
        # Ensure expected_components is a list of strings as required by DeepEval
        if expected_components is not None and isinstance(expected_components, str):
            expected_components = [expected_components]

        for item in files :
            bot_response=agentStudioObj.attach_upload_file(item)
            
            deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)

        
        files = [os.path.join(project_root, 'testdata', 'GPT', '14688.docx'),
                 os.path.join(project_root, 'testdata', 'GPT', '14668.pdf'),    
                ]
        for item in files :
            assert agentStudioObj.attach_unsupported_file_verify(item),"Unsupported file type error not shown as expected"

        assert agentStudioObj.attach_large_file(os.path.join(project_root, "testdata", "GPT", "DRSQA14668_largefile.png")),"Large file size error not shown as expected"
       
        assert agentStudioObj.attach_exceed_file_count([
            os.path.join(project_root, "testdata", "GPT", "14668.png"),
            os.path.join(project_root, "testdata", "GPT", "14668.jpeg"),
            os.path.join(project_root, "testdata", "GPT", "14668.jpg")
            ]),"Exceed file count error not shown as expected"

    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_HelixGPT_ViewComponant ===")

@pytest.mark.smoke #13273
@pytest.mark.EN
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_13273_HelixGPT_Feedback_Employee_Navigator(driver, request, provider_config_key):
    """
    Test Case: HelixGPT Feedback Functionality in Employee Navigator
    
    Description:
        Verify that HelixGPT provides feedback options (thumbs up/down) and allows users
        to submit feedback with comments.
    
    Test Steps:
        0. Setup HelixGPT with appropriate model
        1. Login to DWP with end user
        2. Click Employee Navigator and select HelixGPT from dropdown
        3. Ask queries based on different prompt types
        4. Verify HelixGPT responds correctly
        5. Verify feedback options (thumbs up/down) are present
        6. Test thumbs up functionality
        7. Test thumbs down functionality
        8. Verify comment field appears for thumbs down
        9. Enter feedback comment
        10. Submit feedback
    
    Expected Results:
        - End user logs into DWP successfully
        - HelixGPT opens successfully
        - HelixGPT responds correctly to queries
        - Thumbs up/down buttons are displayed
        - User can provide positive feedback
        - User can provide negative feedback
        - Comment field appears for negative feedback
        - Feedback is submitted successfully
    """
    
    try:
        print("=== Starting test_HelixGPT_Feedback_Employee_Navigator ===")
        
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        agentStudioObj = agentStudio(driver)
        dwp_adminObj= dwp_administration(driver)
        dwp_landingObj= dwp_landing(driver)
        config = uiuti_obj.load_config("azure_config.json")

        print("Configure AI Provider in HelixGPT Manager")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix Digital Workplace application...")
        helixgpt_manager.select_application("BMC Helix Digital Workplace")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]
        helixgpt_manager.configure_agent(skill_name=common_data['DWP_EMP_SKILL'],provider=provider, model=model)

        print("Enable Chat and search configuration in DWP Admin with skill "+common_data['DWP_EMP_SKILL'])
        dwp_adminObj.navigate_to_dwp_search(common_data['ADE_USERNAME'],common_data['ADE_PASSWORD'])
        dwp_adminObj.enable_ai_chat() 
        dwp_adminObj.select_skill(common_data['DWP_EMP_SKILL'])
        dwp_adminObj.save_configuration()
        time.sleep(5)  # Wait for the configuration to be saved
        dwp_adminObj.navigate_to_dwp_chatbot()  
        dwp_adminObj.select_technology("Employee Navigator")
        dwp_adminObj.select_skill(common_data['DWP_EMP_SKILL'])
        dwp_adminObj.save_configuration()
        time.sleep(5)  # Wait for the configuration to be saved
        dwp_adminObj.logout()
        time.sleep(6)

        dwp_landingObj.navigate_to_dwp_landing(common_data['END_USER'],common_data['ADE_PASSWORD'])
        input ="Sample RKM and BWFA article for BMCOps Monitoring"
        dwp_landingObj.enter_message_and_send(input)
        time.sleep(5)  # Wait for the bot to respond
        # 5. Verify test bot responds correctly responds to the image uploaded
        print(f"Evaluating bot response with DeepEval metrics for {input}...")
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)

        expected_components = ["The response may return two types of outputs: a successful sample article OR a not-found message.",
    "If successful, the response should include a Sample RKM article and a Sample BWFA article, each with the fields: Title, Question, Answer, and Technical Notes.",
    "If no article is found, the response may say it cannot find a sample article, apologies, and may ask for more details.",
    "Even if there is metadata, sources, numbering, or additional irrelevant lines, the answer should not be treated as irrelevant.",
    "If any data related to RKM or BWFA sample articles for BMCOps Monitoring is present, the answer must be marked as relevant."
               ]
        context = ["The user query is asking for sample RKM and BWFA knowledge articles related to BMCOps Monitoring. The system should attempt to retrieve predefined sample articles. Each article, if available, must include Title, Question, Answer, and technical notes. The response may also include two separate sections—one for RKM and one for BWFA. If no such articles exist, the system may state that no sample article was found. Data values are dynamic, but structure and field names must remain consistent; ignore additional metadata or formatting."]
        deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)
        
        print(f"\n=== Verifying Feedback Buttons for RKM article for BMCOps Monitoring ===")
        assert agentStudioObj.verify_feedback_buttons_present(), \
            f"Feedback buttons not found for RKM article for BMCOps Monitoring query"
        
        print("\n=== Testing Thumbs Up Functionality ===")
        assert agentStudioObj.click_thumbs_up(), \
            "Failed to click thumbs up button"
        print("✓ Thumbs up button clicked successfully")        
        
        dwp_landingObj.enter_message("Show rkm articles")
        dwp_landingObj.click_send_button()
        time.sleep(5) 

        print("\n=== Testing Thumbs Down Functionality ===")
        assert agentStudioObj.click_thumbs_down(), \
            "Failed to click thumbs down button"
        print("✓ Thumbs down button clicked successfully")
        
        # Step 9: Enter feedback comment
        print("\n=== Step 9: Entering Feedback Comment ===")
        feedback_comment = "The response was not specific enough for my query. Please provide more detailed information."
        assert agentStudioObj.enter_feedback_comment(feedback_comment), \
            "Failed to enter feedback comment"
        print(f"✓ Feedback comment entered: '{feedback_comment}'")
        
        # Step 10: Submit feedback
        print("\n=== Step 10: Submitting Feedback ===")
        assert agentStudioObj.click_send_feedback(), \
            "Failed to click send feedback button"
        print("✓ Send button clicked")
        
    except AssertionError as ae:
        print(f"\nAssertion Failed: {str(ae)}")
        pytest.fail(f"Test failed with assertion error: {str(ae)}")
        
    except Exception as e:
        print(f"\nTest failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
    
    finally:
        print("\n=== Test Cleanup ===")
        print("Test execution completed.")
        print("=== Completed test_HelixGPT_Feedback_Employee_Navigator ===\n")
