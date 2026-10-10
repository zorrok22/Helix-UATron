import time
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from utils.constants import SolQAConstants
from utils.common_waits import CommonWaits
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class SmartITPage:
    def __init__(self, driver):
        self.driver = driver
        self.common_waits=CommonWaits(driver=driver)
        # Constants
        self.CREATE_REQUESTED_FLOW = "//span[contains(text(),'{}')]"
        self.CREATE_NEW_TEMPLATE_CHOOSER = "//div[contains(@class,'template-chooser')]"
        self.ASSIGN_TO_ME_BUTTON = "//button[@title='Assign to me']"
        self.global_search_icon = "//a[@id='header-helixGPT_search_button']"
        self.global_search_input = "//input[@id='rx-search-7']"
        self.view_full_incident_link = "//a[normalize-space()='View full incident']"
        self.view_full_ticket_frame = "//iframe[contains(@src, 'pwa')]"
        self.pwa_iframe = "//iframe[@id='pwa-frame']"
        self.SEARCHFRAME="//iframe[@id='studio-frame']"
        self.ULTERNATE_SEARCH="//a[@id='header-search_button']"
        self.ULTERNATE_SEARCH_INPUT="//input[@id='globalSearchBox']"
        self.SEARCHRESULT_FRAME="//*[@class='preview-panel__item ng-scope']/iframe"
        self.LAUNCHCHATOPS_SEARCHRESULTS="//button[@name='ar304460081']"

    def global_search_and_open_ticket(self, ticket_id):
        # Determine which global search control is present
        if self.is_element_present(By.XPATH, self.global_search_icon):
            print("Click the global search GPT icon")
            self.common_waits.click_without_interception(self.driver.find_element(By.XPATH, self.global_search_icon))
            search_input_xpath = self.global_search_input
            search_frame_xpath = self.SEARCHFRAME
            self.driver.switch_to.frame(self.driver.find_element(By.XPATH, search_frame_xpath))

        elif self.is_element_present(By.XPATH, self.ULTERNATE_SEARCH):
            print("Click the alternate global search icon")
            self.common_waits.click_without_interception(self.driver.find_element(By.XPATH, self.ULTERNATE_SEARCH))
            search_input_xpath = self.ULTERNATE_SEARCH_INPUT
            search_frame_xpath = self.SEARCHFRAME
        else:
            raise Exception("No global search control found on the page.")

        print("Enter the ticket ID and press ENTER")
        search_input = self.common_waits.wait_for_element_presence(By.XPATH,search_input_xpath, timeout=600)
        search_input.send_keys(ticket_id)
        time.sleep(1)  # Wait for search results to load
        search_input.send_keys(Keys.RETURN)

    def launch_chatops_searchresults(self):
        self.common_waits.wait_for_element_visible_with_fluent_wait(
            self.driver, self.view_full_ticket_frame, 300
        )
        self.driver.switch_to.frame(self.driver.find_element(By.XPATH, self.view_full_ticket_frame))
        self.common_waits.wait_for_element_visible_with_fluent_wait(
            self.driver, self.LAUNCHCHATOPS_SEARCHRESULTS, 60
        )
        print("Click the Launch chatOps button in search results")
        self.common_waits.click_without_interception(self.driver.find_element(By.XPATH, self.LAUNCHCHATOPS_SEARCHRESULTS))

    def click_view_full_incident(self):
        """
        Click the 'View full incident' link in the search results.
        This method assumes that the user is already on the search results page.
        """
        print("Wait for the \"View full incident\" link to be visible")
        
        WebDriverWait(self.driver, 600).until(
                    EC.presence_of_element_located((By.XPATH, self.view_full_ticket_frame)))

        self.driver.switch_to.frame(self.driver.find_element(By.XPATH, self.view_full_ticket_frame))
        
        WebDriverWait(self.driver, 600).until(
                    EC.presence_of_element_located((By.XPATH, self.view_full_incident_link)))

        
        print("Click the \"View full incident\" link")
        self.common_waits.click_without_interception(self.driver.find_element(By.XPATH, self.view_full_incident_link))
        
    
    def open_ticket_from_search_results(self):
        print("Wait for the \"View full incident\" link to be visible")
        print("Switch to the ticket frame")
        self.driver.switch_to.frame(self.driver.find_element(By.XPATH, self.view_full_ticket_frame))
        print("Click the \"View full incident\" link")
        self.common_waits.wait_for_element_visible_with_fluent_wait(
            self.driver, self.view_full_incident_link, 30
        )
        self.common_waits.click_without_interception(self.driver.find_element(By.XPATH, self.view_full_incident_link))
        print("Wait for all spinners to hide")
        self.common_waits.wait_for_all_spinners_to_hide(SolQAConstants.APP_SMARTIT)
        print("Switch to default frame")
        self.driver.switch_to.default_content()
        print("Switch to the PWA iframe")
        self.driver.switch_to.frame(self.driver.find_element(By.XPATH, self.pwa_iframe))
    

    def navigate_to_create_new_incident(self):
        """
        Navigate to create new incident page in SmartIT
        """
        print("Click on New Incident")
        
        # Try to find and click the incident creation option directly
        incident_xpath = self.CREATE_REQUESTED_FLOW.format("incident")
        if self.is_element_present(By.XPATH, incident_xpath):
            self.find_element(By.XPATH, incident_xpath).click()
        # If direct option not found, try through template chooser
        elif self.is_element_present(By.XPATH, self.CREATE_NEW_TEMPLATE_CHOOSER):
            self.find_element(By.XPATH, self.CREATE_NEW_TEMPLATE_CHOOSER).click()
            self.common_waits.wait_for_by_element_to_be_visible(By.XPATH, incident_xpath)
            self.find_element(By.XPATH, incident_xpath).click()
            
        # Wait for page to load completely
        self.common_waits.wait_for_page_fully_loaded(self.common_waits.default_timeout, 1)
        self.common_waits.wait_for_all_spinners_to_hide("SmartIT")
        
    def click_assign_to_me(self):
        """
        Click the 'Assign to me' button
        
        Returns:
            bool: True if button was clicked successfully, False otherwise
        """
        try:
            self.log.info("Clicking on 'Assign to me' button")
            self.common_waits.wait_for_by_element_to_be_visible(By.XPATH, self.ASSIGN_TO_ME_BUTTON)
            self.find_element(By.XPATH, self.ASSIGN_TO_ME_BUTTON).click()
            self.common_waits.wait_for_page_fully_loaded(self.common_waits.default_timeout, 1)
            return True
        except Exception as e:
            self.log.error(f"Failed to click 'Assign to me' button: {str(e)}")
            return False
        
    def is_element_present(self, by, value):
        """
        Check if an element is present on the page
        
        Args:
            by: The locator strategy (e.g., By.XPATH)
            value: The locator value
            
        Returns:
            bool: True if element is present, False otherwise
        """
        try:
            self.driver.find_element(by, value)
            return True
        except:
            return False
            
    def find_element(self, by, value):
        """
        Find an element on the page
        
        Args:
            by: The locator strategy (e.g., By.XPATH)
            value: The locator value
            
        Returns:
            WebElement: The found element
        """
        return self.driver.find_element(by, value) 