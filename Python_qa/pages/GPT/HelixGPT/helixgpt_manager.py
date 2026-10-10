import json
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.keys import Keys
from conftest import driver
from pages.Base.base_page import BasePage
from test_config import TestConfig
from utils.common_waits import CommonWaits
from utils.constants import SolQAConstants as Constants
import time
from utils.ui_utils import utilities
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.common.action_chains import ActionChains

class HelixGPTManager(BasePage):
    """Class for interacting with the Helix GPT Manager page"""
    
    def __init__(self, driver):
        super().__init__(driver)
        self.wait = WebDriverWait(driver, 300)

        # Login page locators
        self.USERNAME_FIELD = (By.ID, "user_login")
        self.PASSWORD_FIELD = (By.ID, "login_user_password")
        self.LOGIN_BUTTON = (By.ID, "login-jsp-btn")
        
        # Application selection locators
        self.APPLICATION_DROPDOWN = (By.XPATH, "//button[contains(@id,'rx-select-1')]")
        self.HELIX_OPS_SWARMER = (By.XPATH, "//span[contains(text(),'BMC Helix Ops Swarmer')]")
        self.HELIX_OPS_SWARMER = (By.XPATH, "//span[contains(text(),'BMC Helix Ops Swarmer')]")

        self.SEARCH_INPUT = (By.XPATH, "//adapt-rx-search[@data-testid='rx-select-1_search']//input")
        self.RADIO_XPATH = "//tr[.//span[@class='btn-content' and normalize-space()='{}']]//input[@type='radio']"
        self.ACTIONBUTTON_XPATH = "//tr[.//span[@class='btn-content' and normalize-space()='{}']]//button[@type='button']"
        self.TestButton_XPATH = "//div[contains(@class, 'cdk-overlay-pane')]//button[contains(@class, 'dropdown-item') and contains(., 'Test')]"
        # Agent selection locators
        self.COLLABORATION_AGENT_RADIO = (By.XPATH, "(//div[contains(@class,'ellipsis')]//span[contains(text(),'')])[1]/ancestor::adapt-button/parent::div/preceding-sibling::div//input[@role='radio']")
        self.COLLABORATION_AGENT_BUTTON = (By.XPATH, "//span[contains(text(),'Collaboration Agent')]/ancestor::adapt-button")
        self.SEARCHBOX_SKILL= (By.XPATH, "//input[@class='form-control adapt-rx-search__input']")
        # AI Provider settings locators
        self.AI_PROVIDER_DROPDOWN = (By.XPATH, "//adapt-rx-select[.//label[contains(.,'Provider')]]//button")
        self.AZURE_OPENAI = (By.XPATH, "//div[contains(@class,'rx-select__search-button-title') and contains(text(),'OpenAI')]")
        self.MODEL_DROPDOWN = (By.XPATH, "//adapt-rx-select[.//label[contains(.,'Model')]]//button")
        self.GPT40_SOLQA = (By.XPATH, "//div[contains(@class,'rx-select__search-button-title') and contains(text(),'gpt-4o')]")
        self.MODEL_TAB = (By.XPATH, "//*[@id='cdk-drop-list-4']/li[1]/button")
        self.APPLY_BUTTON = (By.XPATH, "//adapt-button[@rx-id='skill-details-tab-set-model-apply']")
        self.CHANNEL_TAB = (By.XPATH, "//li[.//button//span[contains(normalize-space(.), 'Channels')]]")
        self.COLLABORATION_AGENT_CHANNEL_RADIO =(By.XPATH, "//input[@type='radio' and contains(@value, 'MS Teams channel for collaboration agent')]")
        self.EDITBUTTON = (By.XPATH, "//adapt-button[@rx-id='channel-selection-edit']")
        self.CONFIGURE_TAB ="//button[@role='tab' and .//span[contains(., 'Configure')]]"
        self.SEARCH_SETTINGS_TAB="//button[@role='tab' and .//span[contains(., 'Search settings')]]"
        # Logout locators
        self.USER_MENU = (By.XPATH, "//button[contains(@aria-label,'Profile menu')]")
        self.LOGOUT_BUTTON = (By.XPATH, "//button[contains(text(),'Sign out')]")
        self.APP_OPTION_XPATH = "//span[contains(text(),'{}')]"

        #Channel configuration
        self.NAME_INPUT = (By.XPATH, "//input[@id='rx-textfield-37']")
        self.DESCRIPTION_TEXTAREA = (By.XPATH, "//textarea[@id='rx-textarea-38']")
        self.BOT_ID_INPUT = (By.XPATH, "//input[@id='rx-textfield-40']")
        self.APP_PASSWORD_INPUT = (By.XPATH, "//input[@id='rx-textfield-41']")
        self.UPDATE_BUTTON = (By.XPATH, "//adapt-button[@rx-id='com-bmc-dsom-hgm-add-channel-docked-update-button']")

        #Configure tab
        self.CONFIGURE_TEXTAREA="//*[@rx-id='skill-details-tab-set-default-config']//textarea"
        self.CONFIG_APPLY_BUTTON ="//adapt-tab-panel[@adapt-tab-title='Configure']//adapt-button[.//span[normalize-space()='Apply']]"

        #search settings tab
        self.CHECKBOX_BOX_XPATH=".//td[1]//div[contains(@class,'ui-chkbox-box')]"
        self.ROW_XPATH="//tr[.//div[contains(normalize-space(.),'{}')]]"

    HELIXGPTMANAGERURL = "/helix/index.html#/com.bmc.dsom.hgm/view/com.bmc.dsom.hgm:Home"
    
    def login(self, username, password):
        """
        Login to Helix GPT Manager
        
        Args:
            username (str): Username for login
            password (str): Password for login
            
        Returns:
            bool: True if login was successful, False otherwise
        """
        try:
            print(f"Attempting to login to Helix GPT Manager with username: {username}")
            
            # Navigate to Helix GPT Manager URL
            self.driver.get(TestConfig.ISURL+self.HELIXGPTMANAGERURL)
            time.sleep(5)  # Wait for page to load
            
            # Wait for and enter username
            username_field = self.wait.until(EC.presence_of_element_located(self.USERNAME_FIELD))
            username_field.clear()
            username_field.send_keys(username)
            
            # Wait for and enter password
            password_field = self.wait.until(EC.presence_of_element_located(self.PASSWORD_FIELD))
            password_field.clear()
            password_field.send_keys(password)
            
            # Click login button
            login_button = self.wait.until(EC.element_to_be_clickable(self.LOGIN_BUTTON))
            login_button.click()
            
            # Wait for login to complete
            time.sleep(5)
            
            print("Successfully logged into Helix GPT Manager")
            return True
            
        except Exception as e:
            print(f"Failed to login to Helix GPT Manager: {str(e)}")
            return False

    def select_application(self, app_name):
        """
        Select an application from the application dropdown by name.

        Args:
            app_name (str): The name of the application to select.

        Returns:
            bool: True if selection was successful, False otherwise
        """
        try:
            print(f"Attempting to select application: {app_name}")

            # Step 1: Click the application dropdown to open the selection menu
            max_wait = 240
            print("Waiting for dropdown to be clickable...")
            dropdown = WebDriverWait(self.driver, max_wait).until(
                EC.element_to_be_clickable(self.APPLICATION_DROPDOWN)
            )
            dropdown.click()
            print("Application dropdown clicked.")
            # Step 2: Wait for the search input to be available and enter the application name
            search_input = self.wait.until(EC.presence_of_element_located(self.SEARCH_INPUT))
            search_input.clear()
            search_input.send_keys(app_name)
            # Step 3: Wait for and select the application option
            app_location_path=self.APP_OPTION_XPATH.format(app_name)
            app_option = self.wait.until(EC.element_to_be_clickable((By.XPATH, app_location_path)))
            app_option.click()
            time.sleep(2)
            print(f"Successfully selected application: {app_name}")
            return True

        except Exception as e:
            print(f"Failed to select application '{app_name}': {str(e)}")
            return False

    def launch_gpt_from_skill(self,skill_name="DWP_GPT"):
            """
            Select skill from the table by clicking its action button 
            
            Returns:
                bool: True if selection was successful, False otherwise
            """
            try:
                print("Attempting to select skill",skill_name)
                              
                # Wait for the action button to be present
                action_button_path= self.ACTIONBUTTON_XPATH.format(skill_name)
                action_button = self.wait.until(EC.presence_of_element_located((By.XPATH, action_button_path)))
                
                # Try multiple approaches to click the action button
                try:
                    action_button.click()
                except:
                    try:
                        self.driver.execute_script("arguments[0].click();", action_button)
                    except:
                        try:
                            from selenium.webdriver.common.action_chains import ActionChains
                            ActionChains(self.driver).move_to_element(action_button).click().perform()
                        except:
                            self.driver.execute_script("clicked action button", action_button)
                
                time.sleep(2)
                test_button = self.wait.until(EC.presence_of_element_located((By.XPATH, self.TestButton_XPATH)))
                test_button.click()
                time.sleep(2)
                print("Successfully launched GPT Agent")
                return True
                
            except Exception as e:
                print(f"Failed to launched GPT Agent: {str(e)}")
                return False

    def configure_agent(self, skill_name, provider, model):
            try:
                    print("Attempting to select skill",skill_name)
                    search_box = self.wait.until(EC.element_to_be_clickable(self.SEARCHBOX_SKILL))
                    search_box.click()
                    search_box.clear()
                    search_box.send_keys(skill_name)
                    time.sleep(1)
                    search_box.send_keys(Keys.ENTER)
                     # Wait for the action button to be present
                    radio_button_path= self.RADIO_XPATH.format(skill_name)
                    radio_button = self.wait.until(EC.presence_of_element_located((By.XPATH, radio_button_path)))
                    radio_button.click()
                    self.configure_ai_provider(provider, model)
                    time.sleep(2)
                    return True
            except Exception as e:
                print(f"Failed to configure AI provider to '{skill_name}': {str(e)}")
                return False
                
    def select_collaboration_agent(self):
        """
        Select Collaboration Agent from the table by clicking its radio button and button
        
        Returns:
            bool: True if selection was successful, False otherwise
        """
        try:
            print("Attempting to select Collaboration Agent")
            
            # Wait for the radio button to be present
            radio_button = self.wait.until(EC.presence_of_element_located(self.COLLABORATION_AGENT_RADIO))
            
            # Try multiple approaches to click the radio button
            try:
                # Method 1: Direct click
                radio_button.click()
            except:
                try:
                    # Method 2: JavaScript click
                    self.driver.execute_script("arguments[0].click();", radio_button)
                except:
                    try:
                        # Method 3: Actions click
                        from selenium.webdriver.common.action_chains import ActionChains
                        ActionChains(self.driver).move_to_element(radio_button).click().perform()
                    except:
                        # Method 4: JavaScript set checked property
                        self.driver.execute_script("arguments[0].checked = true;", radio_button)
            
            time.sleep(2)
            
           
            
            print("Successfully selected Collaboration Agent")
            return True
            
        except Exception as e:
            print(f"Failed to select Collaboration Agent: {str(e)}")
            return False

    def select_helix_ops_swarmer(self):
            """
            Select BMC Helix Ops Swarmer from the application dropdown
            
            Returns:
                bool: True if selection was successful, False otherwise
            """
            try:
                print("Attempting to select BMC Helix Ops Swarmer")
                
                # Step 1: Click the application dropdown to open the selection menu
                try:
                        max_wait=300
                        print("Waiting for dropdown to be clickable...")
                        dropdown = WebDriverWait(self.driver, max_wait).until(
                            EC.element_to_be_clickable(self.APPLICATION_DROPDOWN)
                        )
                        dropdown.click()
                        print("Select Application drop down clicked.")
                except Exception as e:
                        print(f"Select Application drop not clickable within {max_wait} seconds: {e}")
                        raise
            
                #self.driver.execute_script("arguments[0].click();", dropdown)
                time.sleep(3)  # Increased wait time for dropdown to fully open

                # Step 2: Wait for the search input to be available and enter the application name
                search_input = self.wait.until(EC.presence_of_element_located(self.SEARCH_INPUT))
                search_input.clear()
                search_input.send_keys("BMC Helix Ops Swarmer")
                time.sleep(2)
                
                # Step 3: Wait for and select the BMC Helix Ops Swarmer option
                swarmer_option = self.wait.until(EC.element_to_be_clickable(self.HELIX_OPS_SWARMER))
                swarmer_option.click()
                #self.driver.execute_script("arguments[0].click();", swarmer_option)
                time.sleep(2)
                
                print("Successfully selected BMC Helix Ops Swarmer")
                return True
                
            except Exception as e:
                print(f"Failed to select BMC Helix Ops Swarmer: {str(e)}")
    
                return False
   
    def configure_ai_provider(self, provider="Azure OpenAI", model="gpt-40-solqa"):
       
        try:
            print("Attempting to configure AI provider settings")
            
            # Click on Model tab
            model_tab = self.wait.until(EC.element_to_be_clickable(self.MODEL_TAB))
            try:
                model_tab.click()
            except Exception:
                self.driver.execute_script("arguments[0].click();", model_tab)
            #self.driver.execute_script("arguments[0].click();", model_tab)
            time.sleep(2)
            print("Selected Model tab")

            # Select provider
            provider_dropdown = self.wait.until(EC.presence_of_element_located(self.AI_PROVIDER_DROPDOWN))
            try:
                provider_dropdown.click()
            except Exception:
                self.driver.execute_script("arguments[0].click();", provider_dropdown)
            #self.driver.execute_script("arguments[0].click();", provider_dropdown)
            time.sleep(2)
            
            MODEL_XPATH=f"//button[.//*[contains(text(), '{provider}')]]"
            model_option = self.wait.until(EC.presence_of_element_located(
                 (By.XPATH, MODEL_XPATH)
             ))
            try:
                self.driver.find_element(By.XPATH, MODEL_XPATH).click()
            except Exception:
                model_option = self.driver.find_element(By.XPATH, MODEL_XPATH)
                self.driver.execute_script("arguments[0].click();", model_option)

            # # More specific locator for provider options
            # provider_option = self.wait.until(EC.presence_of_element_located(
            #     (By.XPATH, f"//div[contains(@class,'rx-select__search-button-title') and normalize-space(text())='{provider}']")
            # ))
            # self.driver.execute_script("arguments[0].click();", provider_option)
            time.sleep(2)
            
            # Select model
            model_dropdown = self.wait.until(EC.presence_of_element_located(self.MODEL_DROPDOWN))
            #self.driver.execute_script("arguments[0].click();", model_dropdown)
            try:
                model_dropdown.click()
            except Exception:
                self.driver.execute_script("arguments[0].click();", model_dropdown)
            time.sleep(2)
            
            MODEL_OPTIONXPATH=f"//button[.//*[contains(text(), '{model}')]]"


            # More specific locator for model options
            model_option = self.wait.until(EC.presence_of_element_located(
                 (By.XPATH, MODEL_OPTIONXPATH)
             ))
            model_option=self.driver.find_element(By.XPATH, MODEL_OPTIONXPATH)
            try:
                model_option.click()
            except Exception:
                self.driver.execute_script("arguments[0].click();", model_option)
            #self.driver.execute_script("arguments[0].click();", model_option)
            time.sleep(5)
            
            # Click Apply button
            apply_button = self.wait.until(EC.element_to_be_clickable(self.APPLY_BUTTON))
            try:
                apply_button.click()
            except Exception:
                self.driver.execute_script("arguments[0].click();", apply_button)
            #self.driver.execute_script("arguments[0].click();", apply_button)
            time.sleep(5)
            
            print("Successfully configured AI provider settings")
            return True
            
        except Exception as e:
            print(f"Failed to configure AI provider settings: {str(e)}")
            return False

    def logout(self):
        """
        Logout from Helix GPT Manager
        
        Returns:
            bool: True if logout was successful, False otherwise
        """
        try:
            print("Attempting to logout from Helix GPT Manager")
            
            # Click on user menu
            user_menu = self.wait.until(EC.element_to_be_clickable(self.USER_MENU))
            self.driver.execute_script("arguments[0].click();", user_menu)
            time.sleep(2)
            
            # Click logout button
            logout_button = self.wait.until(EC.element_to_be_clickable(self.LOGOUT_BUTTON))
            self.driver.execute_script("arguments[0].click();", logout_button)
            time.sleep(2)
            #self.driver.quit()
           
            print("Successfully logged out from Helix GPT Manager")
            return True
            
        except Exception as e:
            print(f"Failed to logout from Helix GPT Manager: {str(e)}")
            return False 

    def setup_helix_gpt(self, username, password, provider="Azure OpenAI", model="gpt-40-solqa",skill_name=None):
        """
        Complete setup process for Helix GPT including login, application selection, agent selection,
        and AI provider configuration.
        
        Args:
            username (str): Username for login
            password (str): Password for login
            provider (str): The AI provider to select (default: "Azure OpenAI")
            model (str): The model to select (default: "gpt-40-solqa")
            
        Returns:
            bool: True if entire setup was successful, False otherwise
        """
        try:
            print("Starting complete Helix GPT setup process...")
            
            # Load common_data if skill_name not provided
            if skill_name is None:
                uiutil_obj = utilities(self.driver)
                common_data = uiutil_obj.load_common_data()
                skill_name = common_data['OPSWARMER_SKILL']
            
            # Step 1: Login
            if not self.login(username, password):
                raise Exception("Login failed")
            # Step 2: Select BMC Helix Ops Swarmer
            if not self.select_helix_ops_swarmer():
                raise Exception("Failed to select BMC Helix Ops Swarmer")
            
            # Step 3: Select Collaboration Agent
            if not self.configure_agent(skill_name, provider, model):
                raise Exception("Failed to select Collaboration Agent")
            
            print("Successfully completed Helix GPT setup process")
            return True
            
        except Exception as e:
            print(f"Helix GPT setup failed: {str(e)}")
            return False 
    
    def navigate_to_helixgpt_channels(self):
        '''Navigate to the Helix GPT channels page.'''
        self.driver.get(TestConfig.ISURL+self.HELIXGPTMANAGERURL)
        common_waitsObj= CommonWaits(self.driver)
        common_waitsObj.switch_to_tab_by_title(Constants.HELIXGPT_AGENTSTUDIO)
        uiutil_obj = utilities(self.driver)
        common_data = uiutil_obj.load_common_data()
        if not self.login(common_data['SU_USERNAME'], common_data['SU_PASSWORD']):
                raise Exception("Login failed")
        time.sleep(10)  # Wait for login to complete
        if not self.select_helix_ops_swarmer():
                raise Exception("Failed to select BMC Helix Ops Swarmer")
            
        if not self.select_collaboration_agent():
                raise Exception("Failed to select Collaboration Agent")

        channel_tab = self.wait.until(EC.element_to_be_clickable(self.CHANNEL_TAB))
        channel_tab.click()
        time.sleep(2)

    def edit_channel(self):
        '''Edit the collaboration agent channel settings.'''
        self.navigate_to_helixgpt_channels()
        radio_button = self.wait.until(EC.presence_of_element_located(self.COLLABORATION_AGENT_CHANNEL_RADIO))
        if not radio_button.is_selected():
            try:
                radio_button.click()
            except Exception:
                self.driver.execute_script("arguments[0].click();", radio_button)
        edit_button = self.wait.until(EC.element_to_be_clickable(self.EDITBUTTON))
        edit_button.click()

    def configure_channel(self, expected_name, expected_description,
                         expected_bot_id, expected_app_password):
        '''Configure the collaboration agent channel settings.'''
        self.edit_channel()
        field_data = {
            "Name": expected_name,
            "Description": expected_description,
            "Microsoft Bot ID": expected_bot_id,
            "Microsoft App Password": expected_app_password
        }

        self.set_fields_from_dict(field_data)
        # Click the Update button
        update_button = self.wait.until(EC.element_to_be_clickable(self.UPDATE_BUTTON))
        update_button.click()
        time.sleep(2)

    def set_fields_from_dict(self, field_values, timeout=15):
        """
        field_values: dict where key = label text, value = expected value
        Example:
            {
                "Name": "My Channel Name",
                "Description": "My channel description",
                "Microsoft Bot ID": "BOT123456",
                "Microsoft App Password": "Pass@123"
            }
        """
        for label_text, expected_value in field_values.items():
            try:
                # Build XPath: finds input/textarea by its label
                field_xpath = (
                    f"//span[normalize-space()='{label_text}']"
                    f"/ancestor::*[self::adapt-rx-textfield or self::adapt-rx-textarea]"
                    f"//*[self::input or self::textarea]"
                )

                elem = WebDriverWait(self.driver, timeout).until(
                    EC.presence_of_element_located((By.XPATH, field_xpath))
                )

                current_value = elem.get_attribute("value")

                if current_value != expected_value:
                    elem.clear()
                    elem.send_keys(expected_value)
                    print(f"Updated '{label_text}' to: {expected_value}")
                else:
                    print(f"'{label_text}' already has correct value: {expected_value}")

            except Exception as e:
                print(f"Could not update '{label_text}': {e}")

    def open_config(self):
        print("Navigating to Helix GPT Manager configuration page...")
        self.click_element(By.XPATH, self.CONFIGURE_TAB)

    def update_Config_flag(self, flag_name, flag_value):
        try:
            print(f"Attempting to update config flag '{flag_name}' to '{flag_value}'")
            # Navigate to the configuration flags page
            self.open_config()
            uiutil_obj = utilities(self.driver)
            if uiutil_obj.update_flag_value_in_textarea(
                xpath=self.CONFIGURE_TEXTAREA,
                key=flag_name,
                new_value=flag_value,
                timeout=120
            ) is False:
                raise Exception("Failed to update flag value in textarea")
            
            print(f"Successfully updated config flag '{flag_name}'")
            self.click_element(By.XPATH, self.CONFIG_APPLY_BUTTON, timeout=60)
            return True

        except Exception as e:
            print(f"Failed to update config flag '{flag_name}': {str(e)}")
            return False
        
    def open_search_settings(self):
        print("Navigating to Helix GPT Manager search settings page...")
        self.click_element(By.XPATH, self.SEARCH_SETTINGS_TAB)

    def enable_search_settings(self,labels_to_select,timeout=5):
        """
            Selects all checkboxes corresponding to the given list of labels
            if not already selected.
        """
        
        print("Enabling search settings in Helix GPT Manager...")
        self.open_search_settings()
        wait = WebDriverWait(self.driver, timeout)

        for label in labels_to_select:
            try:
                # 1Locate row by label text
                row_xpath = self.ROW_XPATH.format(label)
                row = wait.until(EC.presence_of_element_located((By.XPATH, row_xpath)))

                # Find the checkbox element inside the first <td>
                checkbox_box = row.find_element(By.XPATH, self.CHECKBOX_BOX_XPATH)

                # Check selection state via class
                is_selected = "ui-state-active" in checkbox_box.get_attribute("class")

                # Click only if not already selected
                if not is_selected:
                    self.driver.execute_script("arguments[0].scrollIntoView(true);", checkbox_box)
                    ActionChains(self.driver).move_to_element(checkbox_box).click().perform()
                    print(f"Enabled checkbox for: {label}")
                else:
                    print(f"Already enabled: {label}")

            except TimeoutException:
                print(f"Row not found for label: {label}")
            except Exception as e:
                print(f"Error while handling '{label}': {e}")
                print("Successfully enabled search settings.")
                return True

    