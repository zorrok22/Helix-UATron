from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from utils.ui_utils import utilities
from utils.common_waits import CommonWaits
class ViewEditIncident(BasePage) : 
    
    def __init__(self, driver):
        super().__init__(driver)
        self.common_waits=CommonWaits(driver=driver)
    
    #Constants
    EDIT_INCIDENT_BUTTON_XPATH="//button[@automationid='1094718633']"

    def edit_incident(self):
        edit_element=self.driver.find_element(By.XPATH, self.EDIT_INCIDENT_BUTTON_XPATH)
        print("Waiting for Edit Incident button to be clickable")
        self.common_waits.wait_for_element_clickable(edit_element)
        print("Clicking on Edit Incident button")
        edit_element.click()
        