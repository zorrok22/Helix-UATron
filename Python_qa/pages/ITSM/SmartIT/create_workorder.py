from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time
from pages.Base.base_page import BasePage
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from test_config import TestConfig
from utils.constants import SolQAConstants
from utils.ui_utils import utilities

class create_workorder(BasePage):
    
    def __init__(self, driver):
        super().__init__(driver)
        
    # String locators
    IFRAME_CREATE_WORKORDER = "//iframe[@id='pwa-frame']"
    ELEMENT_XPATH_BY_ID = "//*[@id='{}']"
    DROPDOWN_ELEMENT = "//*[@id='{}']/div/button"
    DROPDOWN_ELEMENT_VALUE = "//*[contains(@class, 'rx-select')]/button/div/div/adapt-highlight[contains(text(),'{}')]"
    CREATE_WORKORDER_LABEL="//h1[normalize-space(text())='Create work order']"
    WORKORDER_ID_ELEMENT = "ar1000000182_data"
    TYPEHEAD_LIST_ELEMENT_TEXT = "//*[contains(text(), '{}')]"

    # Element IDs
    CUSTOMER_BOX = "ar301395300"
    CUSTOMER_OPTION = "ar1000000048"
    SUMMARY_BOX = "1000000000"
    PRIORITY_DROPDOWN = "1000000164"
    STATUS_DROPDOWN = "7"
    ASSIGN_TO_ME_LINK = "304421551"
    SAVE_WORKORDER_BUTTON = "304440891"
    TEMPLATE_BOX="ar300077001" 
    TEMPLATE_OPTION="ar300077001_popup_item-0"

    def navigate_to_create_workorder(self):
        print("Navigating to create new -> Workorder ")
        try:
            uiutil_obj = utilities(self.driver)
            self.driver.get(uiutil_obj.get_ITSM_product_url("SmartIT") + SolQAConstants.CREATE_WORKORDER_PAGE_URL_SUFFIX)
            self.switch_frame(By.XPATH, self.IFRAME_CREATE_WORKORDER)

            wait = WebDriverWait(self.driver, 300)  
            
            print("Verify the navigation is successful by checking 'Create Workorder' label presence")
            create_workorder_label = wait.until(
                EC.element_to_be_clickable((By.XPATH, self.CREATE_WORKORDER_LABEL))
            )
            if create_workorder_label:
                print("Navigation to create workorder page successful.")
                return True
            else:
                print("Create Workorder label not found, navigation may have failed.")
                return False
        except Exception as e:
            print(f"Create new workorder navigation failed: {e}")
            return False

    def create_workorder_from_template(self, smart_it_test_data,navigate=True):
        """
        Fill work order details with the provided test data
        
        Args:
            smart_it_test_data (dict): Dictionary containing work order data
        """
        print("Filling Workorder Details")
        timeout = 180
        try:
            if navigate:
                self.navigate_to_create_workorder()

            self.driver.switch_to.default_content()
            print(f"Waiting up to {timeout} seconds for iframe to appear...")
            WebDriverWait(self.driver, timeout).until(
                EC.frame_to_be_available_and_switch_to_it((By.XPATH, self.IFRAME_CREATE_WORKORDER))
            )
            print("Switched to iframe.")
        except Exception as e:
            print(f"Failed to switch to iframe within {timeout} seconds: {e}")
            raise
        
        WebDriverWait(self.driver, timeout).until(
            EC.visibility_of_element_located((By.XPATH, self.ELEMENT_XPATH_BY_ID.format(self.CUSTOMER_BOX)))
        )
        
        if smart_it_test_data.get("Customer Name"):
            print("Entering Customer Name")
            self.set_customer(smart_it_test_data["Customer Name"])
        
        if smart_it_test_data.get("Template"):
            print("Entering Template")
            self.set_template(smart_it_test_data["Template"])

        if smart_it_test_data.get("Workorder Title"):
            print("Entering Summary")
            self.set_summary(smart_it_test_data["Workorder Title"])
        
        if smart_it_test_data.get("Priority"):
            print("Entering Priority")
            self.set_priority(smart_it_test_data["Priority"])
        
        if smart_it_test_data.get("Status"):
            print("Entering Status")
            self.set_status(smart_it_test_data["Status"])
        time.sleep(1)
        self.save_workorder()
        return self.get_workorderID()
        
    def set_customer(self, person_name):
        """
        Set the customer name for the work order
        
        Args:
            person_name (str): Name of the customer
        """
        print(f"Setting customer: {person_name}")
        self.key_clear_and_type(self.ELEMENT_XPATH_BY_ID.format(self.CUSTOMER_BOX), person_name)
        
        # Wait for the customer option to appear and click it
        WebDriverWait(self.driver, 30).until(
            EC.element_to_be_clickable((By.XPATH, self.ELEMENT_XPATH_BY_ID.format(self.CUSTOMER_OPTION)))
        )
        self.click_element(By.XPATH, self.ELEMENT_XPATH_BY_ID.format(self.CUSTOMER_OPTION))
        time.sleep(1)

    def set_template(self, template_name):
        """
        Set the template  for the work order
        
        Args:
            template_name (str): Name of the template
        """
        print(f"Setting template: {template_name}")
        self.key_clear_and_type(self.ELEMENT_XPATH_BY_ID.format(self.TEMPLATE_BOX), template_name)
        
        WebDriverWait(self.driver, 30).until(
            EC.element_to_be_clickable((By.XPATH, self.ELEMENT_XPATH_BY_ID.format(self.TEMPLATE_OPTION)))
        )
        self.click_element(By.XPATH,self.ELEMENT_XPATH_BY_ID.format(self.TEMPLATE_OPTION))
        time.sleep(1)
    
    def set_summary(self, summary):
        """
        Set the work order summary/title
        
        Args:
            summary (str): Summary text for the work order
        """
        print(f"Setting summary: {summary}")
        self.key_clear_and_type(self.ELEMENT_XPATH_BY_ID.format(self.SUMMARY_BOX), summary)
        time.sleep(1)
    
    def set_priority(self, priority):
        """
        Set the work order priority
        
        Args:
            priority (str): Priority value (e.g., "Low", "Medium", "High")
        """
        print(f"Setting priority: {priority}")
        # Click on the priority dropdown
        dropdown_xpath = self.DROPDOWN_ELEMENT.format(self.PRIORITY_DROPDOWN)
        self.click_element(By.XPATH, dropdown_xpath)
        
        # Wait for and click the priority value
        value_xpath = self.DROPDOWN_ELEMENT_VALUE.format(priority)
        WebDriverWait(self.driver, 30).until(
            EC.element_to_be_clickable((By.XPATH, value_xpath))
        )
        self.click_element(By.XPATH, value_xpath)
        time.sleep(1)
    
    def set_status(self, status):
        """
        Set the work order status
        
        Args:
            status (str): Status value (e.g., "Assigned", "In Progress")
        """
        print(f"Setting status: {status}")
        # Click on the status dropdown
        dropdown_xpath = self.DROPDOWN_ELEMENT.format(self.STATUS_DROPDOWN)
        self.click_element(By.XPATH,dropdown_xpath)
        
        # Wait for and click the status value
        value_xpath = self.DROPDOWN_ELEMENT_VALUE.format(status)
        WebDriverWait(self.driver, 30).until(
            EC.element_to_be_clickable((By.XPATH, value_xpath))
        )
        self.click_element(By.XPATH, value_xpath)
        time.sleep(1)
    
    def click_assign_to_me(self):
        """
        Click the 'Assign to me' link to assign the work order to the current user
        """
        print("Clicking on 'Assign To Me' link")
        self.click_element(By.XPATH,self.ELEMENT_XPATH_BY_ID.format(self.ASSIGN_TO_ME_LINK))
        time.sleep(2)  # Wait for page to update
    
    def save_workorder(self):
        """
        Click the save button to save the work order
        """
        print("Clicking on Save Button on Workorder")
        # Wait for save button to be visible
        WebDriverWait(self.driver, 30).until(
            EC.element_to_be_clickable((By.XPATH, self.ELEMENT_XPATH_BY_ID.format(self.SAVE_WORKORDER_BUTTON)))
        )
        self.click_element(By.XPATH,self.ELEMENT_XPATH_BY_ID.format(self.SAVE_WORKORDER_BUTTON))
        time.sleep(2)  # Wait for page to save
    
    def get_workorderID(self):

        """
        Retrieve the work order ID from the page after saving
        
        Returns:
            str: Work order ID
        """
        self.driver.switch_to.default_content()
        self.switch_frame(By.XPATH, self.IFRAME_CREATE_WORKORDER)
        print("Retrieving Workorder ID from the page")
        workorder_id_element = WebDriverWait(self.driver, 120).until(
            EC.visibility_of_element_located((By.XPATH, self.ELEMENT_XPATH_BY_ID.format(self.WORKORDER_ID_ELEMENT)))
        )
        workorder_id = workorder_id_element.text
        self.driver.switch_to.default_content()

        print(f"Workorder ID retrieved: {workorder_id}")
        return workorder_id

