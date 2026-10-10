from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from test_config import TestConfig
from utils.ui_utils import utilities
import time

class dwp_administration(BasePage):

    def __init__(self, driver):
        super().__init__(driver)
        global uiutil_obj
        uiutil_obj = utilities(driver)
        self.ADMIN_URL_ENDPOINT = "/app/#/admin"
        self.SEARCH_URL_ENDPOINT = self.ADMIN_URL_ENDPOINT + "/search"
        self.CHATBOT_URL_ENDPOINT = self.ADMIN_URL_ENDPOINT + "/chatbot"
        self.DWP_SEARCH_TITLE="Digital Workplace - Search"
        self.DWP_CHATBOT_TITLE="Digital Workplace - Administration"
        self.SKILL_COMBOBOX_XPATH="//adapt-rx-select[.//label[contains(.,'Skill')]]//button"
        self.SKILLS_LIST_XPATH="//button[.//*[(text()= {})]]"
        self.SAVE_BUTTON_XPATH="//button[contains(text(),'Save')]"
        self.SELECTED_SKILL_XPATH="//adapt-rx-select[.//label[contains(.,'Skill')]]//button/div/div"
        self.TECHNOLOGY_COMBO_XPATH="//adapt-rx-select[.//label[contains(.,'Technology')]]//button"
        self.TECHNOLOGY_LIST_XPATH="//button[.//span[contains(text(),'{}')]]"
        self.USERPROFILE="//adapt-profile[@data-ta='userProfile']"
        self.SIGNOUT_BUTTON="//button[contains(text(),'Sign out')]"
        self.YES_BUTTON="//button[normalize-space(text())='Yes']"
        self.AI_CHAT_SWITCH_XPATH = "//legend[contains(.,'AI chat')]/parent::fieldset//input[@role='switch']"

    def navigate_to_dwp_search(self,userName,password,login=False):
        try:
            dwp_search_url = uiutil_obj.get_ITSM_product_url("dwp") + self.SEARCH_URL_ENDPOINT
            print(f"Navigating to DWP Search URL: {dwp_search_url}")
            if login:
                assert uiutil_obj.login(userName, password,URL=dwp_search_url),"Login to DWP failed"
            else:
                self.driver.get(dwp_search_url)
            assert uiutil_obj.verify_browser_title(self.DWP_SEARCH_TITLE), "Failed to load DWP Search Administration page"
            print("Successfully navigated to DWP Search Administration page.")
        except Exception as e:
            print(f"Error navigating to DWP Search Administration page: {e}")
            raise

    def select_skill(self, skill_name):
        try:
            assert self.is_element_present(By.XPATH, self.SKILL_COMBOBOX_XPATH), "Skills combo box not found."
            selected_skill_elem = self.find_element(By.XPATH, self.SELECTED_SKILL_XPATH)
            if skill_name in selected_skill_elem.text.strip():
                print(f"Skill '{skill_name}' is already selected in DWP Search Administration.")
                return
            print(f"Selecting skill '{skill_name}' in DWP Search Administration...")
            self.click_element(By.XPATH, self.SKILL_COMBOBOX_XPATH)
            skill_xpath = self.SKILLS_LIST_XPATH.format(f'"{skill_name}"')
            assert self.is_element_present(By.XPATH, skill_xpath), f"Skill '{skill_name}' not found in DWP Search Administration."
            print(f"Skill '{skill_name}' is present in DWP Search Administration.")
            self.click_element(By.XPATH, skill_xpath)
        except Exception as e:
            print(f"Error selecting skill '{skill_name}': {e}")
            raise

    def save_configuration(self):
        try:
            assert self.is_element_present(By.XPATH, self.SAVE_BUTTON_XPATH), "Save button not found in DWP Search Administration."
            self.click_if_enabled(By.XPATH, self.SAVE_BUTTON_XPATH)
            print("Clicked on Save button in DWP Search Administration.")
            time.sleep(5)  # Wait for save operation to complete; adjust as necessary
            print("Configuration saved successfully in DWP Search Administration.")
        except Exception as e:
            print(f"Error saving configuration in DWP Search Administration: {e}")
            raise
    
    def logout(self):
        try:
            dewp_landingPage = uiutil_obj.get_ITSM_product_url("dwp")
            self.driver.get(dewp_landingPage)
            self.click_element(By.XPATH, self.USERPROFILE)
            self.click_element(By.XPATH, self.SIGNOUT_BUTTON)
            self.click_element(By.XPATH, self.YES_BUTTON) 
            print("Logged out successfully.")
        except Exception as e:
            print(f"Error during logout: {e}")
            raise

    def navigate_to_dwp_chatbot(self):
        try:
            dwp_chatbot_url = uiutil_obj.get_ITSM_product_url("dwp") + self.CHATBOT_URL_ENDPOINT
            print(f"Navigating to DWP Chatbot URL: {dwp_chatbot_url}")
            self.driver.get(dwp_chatbot_url)
            assert uiutil_obj.verify_browser_title("Digital Workplace - Administration")
            print("Successfully navigated to DWP Chatbot Administration page.")
        except Exception as e:
            print(f"Error navigating to DWP Chatbot Administration page: {e}")
            raise
    
    def select_technology(self, technology_name):
        try:
            assert self.is_element_present(By.XPATH, self.TECHNOLOGY_COMBO_XPATH), f"Technology combo box not found in DWP Chatbot Administration."
            selected_technology_elem = self.find_element(By.XPATH, self.TECHNOLOGY_COMBO_XPATH+"/div/div")
            if technology_name in selected_technology_elem.text.strip():
                print(f"Technology '{technology_name}' is already selected in DWP Chatbot Administration.")
                return
            print(f"Selecting technology '{technology_name}' in DWP Chatbot Administration...")
            self.click_element(By.XPATH, self.TECHNOLOGY_COMBO_XPATH)
            technology_xpath = self.TECHNOLOGY_LIST_XPATH.format(technology_name)
            assert self.is_element_present(By.XPATH, technology_xpath), f"Technology '{technology_name}' not found in DWP Chatbot Administration."
            print(f"Technology '{technology_name}' is present in DWP Chatbot Administration.")
            self.click_element(By.XPATH, technology_xpath)


        except Exception as e:
            print(f"Error selecting technology '{technology_name}': {e}")
            raise

    def enable_ai_chat (self):
        try:
            print("Enabling AI Chat in DWP Chatbot Administration...")
            assert self.is_element_present(By.XPATH, self.AI_CHAT_SWITCH_XPATH), "AI Chat switch not found in DWP Chatbot Administration."
            ai_chat_checkbox = self.find_element(By.XPATH, self.AI_CHAT_SWITCH_XPATH)
            is_checked = ai_chat_checkbox.get_attribute("aria-checked") == "true"
            if not is_checked:
                self.driver.execute_script("arguments[0].click();", ai_chat_checkbox)
                time.sleep(1)
                print("AI Chat switch enabled.")
            else:
                print("AI Chat switch is already enabled.")
            print("AI Chat enabled successfully in DWP Chatbot Administration.")
        except Exception as e:
            print(f"Error enabling AI Chat: {e}")
            raise