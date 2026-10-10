import time
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from utils.constants import SolQAConstants
from utils.common_waits import CommonWaits
from utils.constants import SolQAConstants as Constants

class sm_landing(BasePage):
        
    SERVICE_MONITORLABEL="//span[contains(text(),'Service Monitor')]"
    SITUATION_SUMMARY="//span[@class='situation-summary__issue']"

    def __init__(self, driver):
        self.driver = driver
        self.common_waits = CommonWaits(driver=driver)
    

    def verify_sm_landing_page_load(self):
        """
        Verify that the Service Management landing page is displayed.
        """
        try:
            self.common_waits.switch_to_tab_by_title(Constants.SERVICEMONITOR)
            # Wait for the page to load and check for a specific element
            self.common_waits.wait_for_element_visible(
                self.driver, 
                (By.XPATH,self.SERVICE_MONITORLABEL ), 
                timeout=600
            )
            print("Service Management landing page is displayed.")
            return True
        except Exception as e:
            print(f"Service Management landing page is not displayed: {e}")
            return False
        
    def verify_BAP_Displayed(self,situation_type="High CPU Utilization"):
        """
        Verify that the BAP (Best Action Plan) is displayed.
        """
        try:
            self.common_waits.wait_for_element_visible(
                self.driver, 
                (By.XPATH, self.SITUATION_SUMMARY), 
                timeout=600
            )
            print("BAP is displayed.")
            SITUATION_LABEL = self.driver.find_element(By.XPATH, self.SITUATION_SUMMARY)
            if situation_type in SITUATION_LABEL.text :
                print(f"BAP is displayed with the expected situation type: {situation_type}")
                return True
            else :
                print(f"BAP is displayed but with unexpected situation type: {SITUATION_LABEL.text}")
                return False
        except Exception as e:
            print(f"BAP is not displayed: {e}")
            return False
        