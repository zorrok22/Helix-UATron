from selenium.webdriver.common.by import By
import time
from test_config import TestConfig
from pages.Base.base_page import BasePage
from utils.ui_utils import utilities
from utils.common_waits import CommonWaits

class ITSMDashboard(BasePage) : 
    
    def __init__(self, driver):
        super().__init__(driver)
        self.commonWaitsObj = CommonWaits(driver)
        self.utils = utilities(driver)
        
    #---------------Locators--------------#
    LOGIN_ACCESS_TAB="//span[@class='Tab']//a[contains(text(),'Login/Access')]"
    LOGINID_FIELD="//label[normalize-space(.)='Login ID']/following-sibling::textarea"
    SEARCH_BUTTON="//a[.//div[normalize-space(text())='Search']]"
    GENERAL_TAB="//span[@class='Tab']//a[contains(text(),'General')]"
    EMAIL_FIELD="//label[normalize-space(.)='Email Address']/following-sibling::textarea"
    SAVE_BUTTON="//a[.//div[normalize-space(text())='Save']]"

    #-------------------------------------#
    PEOPLE_FORM="/arsys/forms/onbmc-s/CTM:People"

    def launch_navigate_dashboard(self):
        print("Navigating to ITSM Dashboard")
        self.driver.get(TestConfig.ISURL+self.PEOPLE_FORM)

        print(f"Current URL: {self.driver.current_url}")
        self.commonWaitsObj.wait_for_element_to_be_visible(by=By.XPATH, locator=self.LOGIN_ACCESS_TAB,timeout=180)
        time.sleep(5)

    def open_people_information(self,userID):
        print(f"Opening People Information for User ID: {userID}")
        print("Click on Login/ access Details tab.")
        self.utils.click_element(by=By.XPATH, locator=self.LOGIN_ACCESS_TAB,timeout=180)
        print(f"Entering loginID as {userID}.")
        self.utils.clear_and_type(by=By.XPATH, locator=self.LOGINID_FIELD, text=userID,timeout=180)
        print("Click on Search button.")
        self.utils.click_element(by=By.XPATH, locator=self.SEARCH_BUTTON,timeout=180)

    def change_user_email(self,userID,email):
        self.launch_navigate_dashboard()
        self.open_people_information(userID)
        self.commonWaitsObj.wait_for_all_spinners_to_hide()
        element = self.commonWaitsObj.wait_for_element_presence(by=By.XPATH, locator=self.GENERAL_TAB,timeout=180)
        self.commonWaitsObj.wait_for_element_clickable(element,timeout=180)
        self.utils.click_element(by=By.XPATH, locator=self.GENERAL_TAB,timeout=180)
        self.commonWaitsObj.wait_for_element_presence(by=By.XPATH, locator=self.EMAIL_FIELD,timeout=180)
        self.utils.clear_and_type(by=By.XPATH, locator=self.EMAIL_FIELD, text=email,timeout=180)
        save_button = self.driver.find_element(By.XPATH, self.SAVE_BUTTON)
        if save_button.is_enabled():
            self.utils.click_element(by=By.XPATH, locator=self.SAVE_BUTTON, timeout=180)
            time.sleep(5)  # Wait for save operation to complete
        else:
            print("Save button is disabled. Cannot click.")
