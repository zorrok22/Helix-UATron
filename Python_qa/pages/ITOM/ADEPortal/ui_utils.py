from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from pages.Base.base_page import BasePage
from test_config import TestConfig
from pages.ITOM.AiOps.locators import AIOPSLocators
from pages.ITOM.AiOps.situations_page import SituationsPage
from utils.ui_utils import utilities

class ADEPortalPage(BasePage):
     # Product Endpoints
    
    def __init__(self, driver):
        super().__init__(driver)
        self.locators = AIOPSLocators()
        self.wait = WebDriverWait(driver, TestConfig.WAIT_TIMEOUT)
       

    def get_product_url(self, product):
        """Get the complete URL for a specific product"""
        if product not in BasePage.PRODUCT_ENDPOINTS:
            raise ValueError(f"Product {product} not found in configuration")
        return f"{TestConfig.ADEURL}{BasePage.PRODUCT_ENDPOINTS[product]}"

    def login(self, product, username=None, password=None):
        """Login to a specific product in ADE Portal and verify successful login"""
        try:
            if username is None or password is None:
                uiutil_obj = utilities(self.driver)
                common_data = uiutil_obj.load_common_data()
                if username is None:
                    username = common_data['ADE_USERNAME']
                if password is None:
                    password = common_data['ADE_PASSWORD']

            # Navigate to product URL
            product_url = self.get_product_url(product)
            self.driver.get(product_url)
            
            # Enter credentials
            self.send_keys(*self.locators.USERNAME_FIELD, username)
            self.send_keys(*self.locators.PASSWORD_FIELD, password)
            
            # Click login button
            self.click_element(*self.locators.LOGIN_BUTTON)

            # Check if correct locator is being picked for Profile Avatar
            print(f"Locating profile avatar with locator: {self.locators.PROFILE_AVATAR}")
            
            # Wait for profile avatar to appear (indicating successful login)
            self.wait.until(EC.presence_of_element_located(self.locators.PROFILE_AVATAR))
            print(f"Login successful to {product} - Profile avatar found")
            return True
            
        except Exception as e:
            print(f"Login to {product} failed: {str(e)}")
            return False

    def login_to_product(driver, product="AIOPS"):
        """Helper function to login to a specific product in ADE Portal"""
        portal_page = ADEPortalPage(driver)
        return portal_page.login(product)

    def get_situations_page(driver):
        """Helper function to get the Situations page object"""
        return SituationsPage(driver)