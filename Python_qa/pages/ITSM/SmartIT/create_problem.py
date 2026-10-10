from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time
from utils.ui_utils import utilities
from pages.Base.base_page import BasePage
from selenium.webdriver.support import expected_conditions as EC
from test_config import TestConfig
from utils.constants import SolQAConstants

class create_problem(BasePage) : 

    def __init__(self, driver):
        super().__init__(driver)
               
    CREATE_NEW = "//a[text()='Create New ']"
    CREATE_NEW_PROBLEM = "//span[@class='template-chooser-dropdown-item__title ng-binding' and text()='Problem']"
    IFRAME_XPATH = "//iframe[@id='pwa-frame']"
    CREATE_PROBLEM_LABEL="//h1[normalize-space(text())='Create problem']"
    ELEMENT_XPATH_BY_ID = "//*[@id='{}']"
    PROBLEM_SUMMARY_IFRAME_XPATH = "//iframe[@id='pwa-frame']"
    PROBLEMID_LABEL_XPATH = "//div[@id='ar1000000232_data']"
    IMPACT_DROPDOWN_XPATH="//button[@aria-label='Impact']"
    IMPACTSTATUS_XPATH="//button[.//adapt-highlight[contains(normalize-space(.),'{}')]]"
    URGENCY_DROPDOWN_XPATH="//button[@aria-label='Urgency']"
    URGENCYSTATUS_XPATH="//button[.//adapt-highlight[contains(normalize-space(.),'{}')]]"
    TYPEHEAD_LIST_ELEMENT_TEXT = "//*[contains(text(), '{}')]"

    def navigate_to_create_problem(self):
        print("Navigating to create new -> Problem ")
        try:
            uiutil_obj = utilities(self.driver)
            self.driver.get(uiutil_obj.get_ITSM_product_url("SmartIT") + SolQAConstants.SMARTIT_CREATE_PROBLEM_PATH)
            self.switch_frame(By.XPATH, self.IFRAME_XPATH)

            wait = WebDriverWait(self.driver, 300)  # 5 minutes
            
            print("Verify the navigation is successful by checking 'Create Problem' label presence")
            create_problem_label = wait.until(
                EC.element_to_be_clickable((By.XPATH, self.CREATE_PROBLEM_LABEL))
            )
            if create_problem_label:
                print("Navigation to create problem page successful.")
                return True
            else:
                print("Create Problem label not found, navigation may have failed.")
                return False
        except Exception as e:
            print(f"Create new problem navigation failed: {e}")
            return False

    def create_problem_from_template(self, smart_it_test_data,navigate=True):
        timeout=180
        """Fill Problem details with the provided test data"""
        try:
            self.driver.switch_to.default_content()

            if navigate:
                self.navigate_to_create_problem()
            
            self.driver.switch_to.default_content()

            print("Filling Problem Details")
            print(f"Waiting up to {timeout} seconds for iframe to appear...")
            WebDriverWait(self.driver, timeout).until(
                EC.frame_to_be_available_and_switch_to_it((By.XPATH, self.IFRAME_XPATH))
            )
            print("Switched to iframe.")
        except Exception as e:
            print(f"Failed to switch to iframe within {timeout} seconds: {e}")
            raise   

        # Customer Name
        if smart_it_test_data.get("Summary"):
            print("Entering Summary")
            self.enter_summary(smart_it_test_data["Summary"])
        time.sleep(1)

        # Impact
        if smart_it_test_data.get("Impact"):
            print("Entering Impact")
            self.set_impact(smart_it_test_data["Impact"])
        time.sleep(1)

        # Urgency
        if smart_it_test_data.get("Urgency"):
            print("Entering Urgency")
            self.set_urgency(smart_it_test_data["Urgency"])

        # Region
        if smart_it_test_data.get("Region"):
            print("Entering Region")
            self.set_region(smart_it_test_data["Region"])
        time.sleep(1)

        #CI
        if smart_it_test_data.get("CI"):
            print("Entering CI Name")
            self.set_CI(smart_it_test_data["CI"])
        time.sleep(1)

        self.save_problem()

        return self.get_problemID()

    def set_region(self, region_name):
        print("Set region name and select from dropdown")
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("ar200000012"), 
            region_name
        )
        self.click_element(By.XPATH, self.TYPEHEAD_LIST_ELEMENT_TEXT.format(region_name))

    def enter_summary(self, summary):
        print("Set summary field.")
        time.sleep(3)
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("1000000000"), 
            summary
        )
       
    def set_impact(self, status):
        print("Set impact status")
        wait = WebDriverWait(self.driver, 60)  

        print("Wait for the dropdown to be clickable and click it")
        dropdown = wait.until(
            EC.element_to_be_clickable((By.XPATH, self.IMPACT_DROPDOWN_XPATH))
        )
        dropdown.click()

        print("Wait for the status input to be present and send keys")
        status_input = wait.until(
            EC.presence_of_element_located((By.XPATH, self.IMPACTSTATUS_XPATH.format(status)))
        )
        status_input.send_keys(status + Keys.RETURN)

    def set_urgency(self, status):
        print("Set urgency status")
        wait = WebDriverWait(self.driver, 60)  

        print("Wait for the dropdown to be clickable and click it")
        dropdown = wait.until(
            EC.element_to_be_clickable((By.XPATH, self.URGENCY_DROPDOWN_XPATH))
        )
        dropdown.click()

        print("Wait for the status input to be present and send keys")
        status_input = wait.until(
            EC.presence_of_element_located((By.XPATH, self.URGENCYSTATUS_XPATH.format(status)))
        )
        status_input.send_keys(status + Keys.RETURN)

    def set_Region(self, region_name):
        print("Set region name and select from dropdown")
        time.sleep(3)
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("ar200000012"), 
            region_name
        )
        time.sleep(2)
        self.click_element(By.XPATH, self.TYPEHEAD_LIST_ELEMENT_TEXT.format(region_name))

    def set_CI(self, ci_name):
        print("Set CI name and select from dropdown")
        time.sleep(3)
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("ar303497400"), 
            ci_name
        )
        time.sleep(2)
        self.click_element(By.XPATH, self.TYPEHEAD_LIST_ELEMENT_TEXT.format(ci_name))

    def get_problemID(self):
        print("Getting Problem ID")
        try:
            self.driver.switch_to.default_content()
            self.switch_frame(By.XPATH, self.PROBLEM_SUMMARY_IFRAME_XPATH)
            wait = WebDriverWait(self.driver, 300) 
            problemID_label = wait.until(
                EC.presence_of_element_located((By.XPATH, self.PROBLEMID_LABEL_XPATH))
            )
            problemID=problemID_label.text
            print(f"Problem ID is: {problemID}")
            self.driver.switch_to.default_content()
            return problemID
        except Exception as e:
            print(f"Failed to get Problem ID: {e}")
            return None
        
    def save_problem(self):
        print("Saving the Problem")
        try:
            self.click_element(By.XPATH, self.ELEMENT_XPATH_BY_ID.format("304440891"))
            print("Clicked on Save button.")
            return True
        except Exception as e:
            print(f"Saving Problem failed: {e}")
            return False
