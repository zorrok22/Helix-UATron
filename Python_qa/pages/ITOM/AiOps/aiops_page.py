from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from pages.ITOM.AiOps.locators import AIOPSLocators
from test_config import TestConfig

class AIOpsPage(BasePage):
    """Base class for AIOps pages"""
    def __init__(self, driver):
        super().__init__(driver)
        self.locators = AIOPSLocators()
        self.wait = WebDriverWait(driver, TestConfig.WAIT_TIMEOUT)
        self.filter_button_wait = WebDriverWait(driver, TestConfig.WAIT_TIMEOUT)
    
    def navigate_to_tab(self, tab_name):
        """
        Navigate to a specific tab in AIOps
        Args:
            tab_name (str): Name of the tab to navigate to (e.g., 'Situations', 'Overview', 'Services')
        Returns:
            bool: True if navigation was successful, False otherwise
        """
        try:
            # Format the locator with the tab name
            tab_locator = (By.XPATH, self.locators.AIOPS_TAB[1].format(tab_name))
            
            # Wait for and click the tab
            self.click_element(*tab_locator)
            
            # Wait for the tab to be selected
            self.wait.until(EC.presence_of_element_located(tab_locator))
            
            print(f"Successfully navigated to {tab_name} tab")
            return True
            
        except Exception as e:
            print(f"Failed to navigate to {tab_name} tab: {str(e)}")
            return False