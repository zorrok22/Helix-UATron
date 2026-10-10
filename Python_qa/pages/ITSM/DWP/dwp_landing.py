from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from test_config import TestConfig
from utils.ui_utils import utilities
import time

class dwp_landing(BasePage):

    def __init__(self, driver):
        super().__init__(driver)
        global uiutil_obj
        uiutil_obj = utilities(driver)
        self.DWP_PAGE_URL = "/app/#"
        self.DWP_SEARCH_TITLE = "Digital Workplace -"
        self.CHAT_BUTTON_XPATH = "//span[contains(text(),'Employee Navigator')]"
        self.MESSAGE_INPUT_XPATH = "//input[contains(@class,'adapt-rx-search__input')]"
        self.SENDMESSAGE_BUTTON_XPATH = "//button[.//span[text()='Send message']]"
        self.CHAT_INPUT="//input[@class='form-control adapt-rx-search__input px-0']"
        self.SUBMIT_MESSAGE="//button[@aria-label='Submit']"
        self.SWITCH_BUTTON_XPATH="//button[span[normalize-space()='Search']]"
        self.INPUT_XPATH="//textarea[contains(@id,'rx-textarea')]"
        self.HELIXGPT_DROPDOWN_XPATH = "//button[@aria-label='More options' or contains(@class, 'dropdown')]"
        self.HELIXGPT_OPTION_XPATH = "//div[contains(text(), 'HelixGPT') or contains(text(), 'Helix GPT')]"
    def navigate_to_dwp_landing(self,userName,password,login=True):
        try:
            dwp_landingPage = uiutil_obj.get_ITSM_product_url("dwp") + self.DWP_PAGE_URL
            print(f"Navigating to DWP Landing URL: {dwp_landingPage}")
            if login:
                assert uiutil_obj.login(userName, password,URL=dwp_landingPage),"Login to DWP failed"
            else:
                self.driver.get(dwp_landingPage)
            assert uiutil_obj.verify_browser_title(self.DWP_SEARCH_TITLE), "Failed to load DWP Landing page"
            print("Successfully navigated to DWP Landing page.")
        except Exception as e:
            print(f"Error navigating to DWP Landing page: {e}")
            raise
    
    def launch_chat(self):
        try:
            assert self.is_element_present(By.XPATH, self.CHAT_BUTTON_XPATH), "Chat button not found on DWP Landing page."
            self.click_element(By.XPATH, self.CHAT_BUTTON_XPATH)
            print("Chat interface launched successfully.")
        except Exception as e:
            print(f"Error launching chat interface: {e}")
            raise
    
    def enter_message(self, message):
        try:
            assert self.is_element_present(By.XPATH, self.INPUT_XPATH), "Message input box not found in chat interface."
            message_input = self.find_element(By.XPATH, self.INPUT_XPATH)
            message_input.clear()
            message_input.send_keys(message)
            print(f"Message '{message}' entered and sent successfully.")
            time.sleep(5)  # Wait for response to load
        except Exception as e:
            print(f"Error entering message '{message}': {e}")
            raise
    
    def click_send_button(self):
        try:
            assert self.is_element_present(By.XPATH, self.SENDMESSAGE_BUTTON_XPATH), "Send button not found in chat interface."
            self.click_element(By.XPATH, self.SENDMESSAGE_BUTTON_XPATH)
            print("Send button clicked successfully.")
            time.sleep(5)  # Wait for response to load
        except Exception as e:
            print(f"Error clicking send button: {e}")
            raise

    def enter_message_and_send(self, message):
        try:
            print(f"Entering message: {message}") 
            uiutil_obj.click_element(by=By.XPATH, locator=self.MESSAGE_INPUT_XPATH)
            uiutil_obj.clear_and_type(by=By.XPATH, locator=self.MESSAGE_INPUT_XPATH, text=message)
            print(f"Clicking on Send button")
            uiutil_obj.click_element(by=By.XPATH, locator=self.SUBMIT_MESSAGE)
        except Exception as e:
            print(f"Error in enter_message_and_send: {e}")
            raise
    
    def open_link_from_table(self, bot_response):
        try:
            print("Attempting to open link from bot response...")
            uiutil_obj.click_link_in_text(bot_response)
            print("Link clicked successfully.")
        except Exception as e:
            print(f"Error opening link from bot response: {e}")
            raise

    def click_switch_to_full_chat(self):
        try:
            assert self.is_element_present(By.XPATH, self.SWITCH_BUTTON_XPATH), "Switch to full chat button not found."
            self.click_element(By.XPATH, self.SWITCH_BUTTON_XPATH)
            print("Switched to full chat interface successfully.")
            time.sleep(5)  # Wait for full chat to load
        except Exception as e:
            print(f"Error switching to full chat interface: {e}")
            raise

    def select_helixgpt_from_dropdown(self):
        """
        Clicks on the dropdown and selects HelixGPT option.
        Returns True if successful, False otherwise.
        """
        try:
            print("Selecting HelixGPT from Employee Navigator dropdown...")
            # Wait for Employee Navigator button to be clickable
            time.sleep(2)
            
            # Click the dropdown button
            dropdown = self.find_element(By.XPATH, self.HELIXGPT_DROPDOWN_XPATH)
            dropdown.click()
            print("Clicked dropdown button.")
            time.sleep(2)
            
            # Select HelixGPT option
            helixgpt_option = self.find_element(By.XPATH, self.HELIXGPT_OPTION_XPATH)
            helixgpt_option.click()
            print("✓ HelixGPT selected successfully from dropdown.")
            time.sleep(3)  # Wait for HelixGPT to load
            return True
        except Exception as e:
            print(f"✗ Error selecting HelixGPT from dropdown: {e}")
            return False