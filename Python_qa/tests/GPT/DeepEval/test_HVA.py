import os
import sys
import time
import pytest
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
sys.path.append(project_root)
from pages.GPT.HelixGPT.helixgpt_manager import HelixGPTManager
from pages.GPT.DeepEval.DeepEvalUtil import DeepEvalUtil
from utils.ui_utils import utilities
from test_config import TestConfig
from pages.GPT.HelixGPT.agent_studio import agentStudio

PRODUCT_TYPE = "GPT"
TEST_DESCRIPTION = "GPT Testcases"
LONGWAIT = 30

def __init__():
   pass

__init__()

@pytest.mark.smoke #12293
@pytest.mark.HVA
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_12293_SearchArticleOverWeb(driver, request, provider_config_key):
    """
    Test Case: DRSQA-13285
        Precondition:
            1. Create a data connection for "WEB"
            (https://docs.bmc.com/docs/pages/viewpage.action?spaceKey=HelixGPTMaster&title=.Adding+data+sources+in+BMC+HelixGPT+v23.3.01)
            >> Go to Datasource editor and enable status for "Web"

            2. Create DataConnectionJob for "WEB"
            (For detailed Steps Click https://docs.bmc.com/docs/display/HelixGPTMaster/.Ingesting+data+into+BMC+HelixGPT+v23.3.01)
            >> Creating Connection_Web,

            3. Verify if data ingestion job is completed
            (For detailed Steps Click https://docs.bmc.com/docs/display/HelixGPTMaster/.Ingesting+data+into+BMC+HelixGPT+v23.3.01)
            4. Negative Scenarios
        Test Steps:
           Log in to IS

            Go to HelixGPT Manager >> Skills
            Open desired HVA Skill and update configuration value from "Enterprise" to "World". and save        
            Open desired HVA Skill and update configuration value from "Enterprise" to "World". and save

            Go to IS >> Chatbots >> Configured updated skill with HelixGPT bot

            Go to HelixGPT bot and search for "What is Big Bang Theory"

            Once tested with all models change the configuration value back to enterprise

        Verifications:
            1. Bot is launched
            2. Verify response is received from global web
            3. Verify response is received from global web
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

        print("Step 1 .user launches the bot for model which needs to be tested")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix Virtual Agent application...")
        helixgpt_manager.select_application("BMC Helix Virtual Agent")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]

        helixgpt_manager.configure_agent(skill_name=common_data['HVA_SKILL_NAME'],provider=provider, model=model)
        helixgpt_manager.update_Config_flag("knowledgeSearchScope", "WORLD")
        # helixgpt_manager.logout()
        # time.sleep(5)
        # helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        # helixgpt_manager.select_application("BMC Helix Virtual Agent")
        print("step 2. Search the keyword which can be any word in crawled web page")
        helixgpt_manager.launch_gpt_from_skill(skill_name=common_data['HVA_SKILL_NAME'])
        input = "What is Big Bang Theory"
        agentStudioObj.enter_message(input)
        agentStudioObj.send_message()
        time.sleep(LONGWAIT)  # Wait for the bot to respond    
        print("Step 3. Verify if Bot returns the Knowledge Articles from web with description")
        print("Evaluating bot response with DeepEval metrics...")
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)
        print("Sanitizing bot response for evaluation...")
        bot_response = agentStudioObj.sanitize_output(bot_response)
        expected_components = [
                                "A brief explanation of the Big Bang Theory",
                                "Response may include irrelevant information and metadata do not consider the answer as irrelevant.",
                                "If there is any data related to big bang theory mark answer as relevant"
                            ]

        context = [
                    "When asked about the Big Bang Theory, the response should:",
                    "1. Explain the main idea of the theory.",
                    "2. Some related information",
                    "3. Ignore lines like 'Country', 'Time Zone', 'Sources', and 'test' when evaluating relevancy."
                    "4. The response may contain numerous irrelevant statements—such as system messages, country information, time zone details, attributions, and repeated test phrases—that do not mark the answer as irrelevant."
                ]
        deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)
        print("Test completed successfully. Bot response evaluated.")
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("Running final cleanup...")
       helixgpt_manager.update_Config_flag("knowledgeSearchScope", "ENTERPRISE")
       helixgpt_manager.logout()
       print("=== Completed test_SearchArticleOverWeb ===")

@pytest.mark.smoke #12290
@pytest.mark.HVA
@pytest.mark.parametrize("provider_config_key", ["azure_config", "google_vertex_config"])
def test_DRSQA_12290_XWikiArticleSearch(driver, request, provider_config_key):
    """
    Test Case: DRSQA-13284
        Precondition:
            Login to IS >> Goto HelixGPT Agent Studio and perform below steps

            1. Enabling datasource for "X-Wiki"
            >> Go to Datasource editor and enable status for "X-Wiki" >>

            2. Go to Connection_X-Wiki record and verify if connection is present, if not create a new
            Test Data: Connection Name= Xwiki_Test
            URL: https://www.xwikiplayground.org/xwiki
            Space: Automation%20Framework/
            Auth_Type: Basic
            Username: SolQA_Xwiki
            Password: __USER_PASSWORD__
                
            3. Creating DataConnectionJob X-Wiki, and execute it

            4. Verify if data ingestion job is completed
            5. Go to DataConnectionJopStep and verify data is ingested and in Done status
                

            6. For Chatbot (HVA) based usecases.

            a. Login to IS >> HelixGPT manager application >> Launch GPT manager application >> Select Virtual chat from Application >> Import Skill for All Providers which needs to be tested.
            (New skills export need to be updated every release)

            b. Verify Chatbot corresponding to All AI providers are present
            Login to IS >> BMC Chatbots >> Chatbots tab

            c. Open chatbot for respective model for which testing is to be done

        Test Steps:
            1. End user launches the bot for model which needs to be tested
            2. Search the keyword "How to Develop a Test Automation Framework From Scratch?"
            3. Verify if article/documents which contains the keyword is return by Bot.
            """
    try:    
        print("=== Starting test_XWikiArticleSearch ===")
        print("Initializing page objects...")
        uiuti_obj = utilities(driver)
        common_data = uiuti_obj.load_common_data()
        helixgpt_manager = HelixGPTManager(driver)
        deepeval_util = DeepEvalUtil()
        agentStudioObj = agentStudio(driver)
        config = uiuti_obj.load_config("azure_config.json")

        print("Step 1 .user launches the bot for model which needs to be tested")
        print("Logging in to HelixGPT Manager...")
        helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        print("Selecting BMC Helix Digital Workplace application...")
        helixgpt_manager.select_application("BMC Helix Virtual Agent")
        provider_config = config[provider_config_key]
        provider = provider_config["provider"]
        model = provider_config["model"]

        helixgpt_manager.configure_agent(skill_name=common_data['HVA_SKILL_NAME'],provider=provider, model=model)

        labels = [
            "X-Wiki"
        ]

        helixgpt_manager.enable_search_settings(labels)
        # helixgpt_manager.logout()
        # time.sleep(5)
        # helixgpt_manager.login(username=common_data['SU_USERNAME'], password=common_data['SU_PASSWORD'])
        # helixgpt_manager.select_application("BMC Helix Digital Workplace")
        print("step 2. Search the keyword which can be any word in crawled xwiki page")
        helixgpt_manager.launch_gpt_from_skill(skill_name=common_data['SKILL_NAME'])
        input = "How to Develop a Test Automation Framework From Scratch?"
        agentStudioObj.enter_message(input)
        agentStudioObj.send_message()
        time.sleep(LONGWAIT)  # Wait for the bot to respond    
        print("Step 3. Verify if Bot returns the Knowledge Articles from xwiki with description")
        print("Evaluating bot response with DeepEval metrics...")
        bot_response = agentStudioObj.get_full_assistant_reply(timeout=60, wait_stability=2)
        print("Bot response received:", bot_response)
        print("Sanitizing bot response for evaluation...")
        bot_response = agentStudioObj.sanitize_output(bot_response)
        expected_components = [
            "Step-by-step explanation on how to build a test automation framework from scratch.",
            "Should include references to Selenium Grid or similar distributed testing setup.",
            "Key focus areas include setup, configuration, test design, execution, and maintenance.",
            "Response may include additional best practices or optimization tips — do not treat them as irrelevant.",
            "If the response covers framework creation process, Selenium setup, or testing strategy, mark answer as relevant."
        ]

        context = [
            "When asked about developing a test automation framework from scratch, the response should:",
            "1. Describe the overall goal of creating a scalable and maintainable automation framework.",
            "2. Explain environment setup — including hub and node configuration using Selenium Grid.",
            "3. Include sections on test suite design, parallel execution, and distributed testing.",
            "4. Provide steps for monitoring, optimization, and framework maintenance.",
            "5. Ignore unrelated metadata such as sources, country, or timestamp when evaluating relevancy.",
            "6. If the response includes valid framework setup or explanation content, mark it as relevant even if extra info exists."
        ]
        deepeval_util.EvaluateResults(context=context, input=input, expected_components=expected_components, actual_output=bot_response)
        print("Test completed successfully. Bot response evaluated.")
    except Exception as e:
        print(f"Test failed with error: {str(e)}")
        pytest.fail(f"Test failed with error: {str(e)}")
 
    finally:
       print("=== Completed test_XWikiArticleSearch ===")