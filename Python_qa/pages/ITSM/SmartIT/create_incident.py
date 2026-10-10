from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time
from pages.Base.base_page import BasePage
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from pages.ITSM.SmartIT import smart_it_dashboard
from utils.ui_utils import utilities


class create_incident(BasePage) : 
    
    def __init__(self, driver):
        super().__init__(driver)

    # Constants
    ELEMENT_XPATH_BY_ID = "//*[@id='{}']"
    ELEMENT_DROPDOWN_XPATH_BY_ID = "//button[@name='ar7']" 
    STATUS_INPROGRESSXPATH="//button[@role='option' and .//*[normalize-space(text())='In Progress']]"
    TYPEHEAD_LIST_ELEMENT_TEXT = "//*[contains(text(), '{}')]"
    TYPEHEAD_LIST_ELEMENT_ID = "//*[@id='{}']"
    STATUS_DROPDOWN_CREATE_PAGE = "//div[contains(text(), '{}')]"
    ELEMENT_XPATHBYID= "//*[@id='%s']"
    IFRAME= "//iframe[@id='pwa-frame']"
    ASSIGN_TO_ME_BUTTON = "//button[@title='Assign to me']"
    Launch_CHAT_OPS="ar304460081"
    ADD_MEMBERSID="ar304456811"
    Add_BUTTONID="304459821"
    LAUNCH_DEVOPS_ADDTEAM="//div[@automationid='1015794947']//button"
    INCIDENTID_LABEL="ar1000000161_data"
    NOTESIFRAME="//iframe[@title='View Field']"
    AUTOMATEDNOTES ="//*[contains(text(), 'New test note')]"
    SAVEBUTTON="//button[@id='304440891']"
    INNERFRAME="ar1000000218_data"
    REFRESHBUTTON="//button[@title='Refresh']"

    def fill_incident_template(self, smart_it_test_data):
        timeout=180
        """Fill incident details with the provided test data"""
        print("Filling Incident Details")
        try:
            print(f"Waiting up to {timeout} seconds for iframe to appear...")
            WebDriverWait(self.driver, timeout).until(
                EC.frame_to_be_available_and_switch_to_it((By.XPATH, self.IFRAME))
            )
            print("Switched to iframe.")
        except Exception as e:
            print(f"Failed to switch to iframe within {timeout} seconds: {e}")
            raise   
        # Customer Name
        if smart_it_test_data.get("Customer Name"):
            print("Entering Customer Name")
            self.set_customer(smart_it_test_data["Customer Name"])
        time.sleep(1)

        # Template Name5
        if smart_it_test_data.get("Template Name"):
            print("Entering template name")
            self.set_template(smart_it_test_data["Template Name"])
        time.sleep(1)

        # Description
        if smart_it_test_data.get("Incident Description"):
            print("Entering Description")
            self.set_description(smart_it_test_data["Incident Description"])
        time.sleep(1)

        # Title
        if smart_it_test_data.get("Incident Title"):
            print("Entering Summary")
            self.set_summary(smart_it_test_data["Incident Title"])
        time.sleep(1)

        # Status
        if smart_it_test_data.get("Status"):
            print("Entering Status")
            self.set_status(smart_it_test_data["Status"])
        time.sleep(1)

        #CI
        if smart_it_test_data.get("CI"):
            print("Entering CI Name")
            self.set_CI(smart_it_test_data["CI"])
        time.sleep(1)

    def set_customer(self, person_name):
        print("Set customer name and select from dropdown")
        time.sleep(3)
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("ar303530000"), 
            person_name[:-1]
        )
        time.sleep(2)
        self.click(self.TYPEHEAD_LIST_ELEMENT_TEXT.format(person_name))

    def set_template(self, template_name):
        print("Set template name and select from dropdown")

        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("ar303558600"), 
            template_name
        )
        time.sleep(2)
        element =self.driver.find_element(By.XPATH, self.ELEMENT_XPATH_BY_ID.format("ar303558600")).send_keys(Keys.RETURN)
        
    def set_description(self, description):
        print("Set incident description")
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("1000000151"),
            description
        )

    def set_summary(self, summary):
        print("Set incident summary")
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("1000000000"),
            summary
        )

    def set_status(self, status):
        print("Set incident status")
        wait = WebDriverWait(self.driver, 60)  # 60 seconds timeout

        print("Wait for the dropdown to be clickable and click it")
        dropdown = wait.until(
            EC.element_to_be_clickable((By.XPATH, self.ELEMENT_DROPDOWN_XPATH_BY_ID))
        )
        dropdown.click()

        print("Wait for the status input to be present and send keys")
        status_input = wait.until(
            EC.presence_of_element_located((By.XPATH, self.STATUS_INPROGRESSXPATH))
        )
        status_input.send_keys(status + Keys.RETURN)

    def set_CI(self, ci_name):
        print("Set CI name and select from dropdown")
        time.sleep(3)
        self.key_clear_and_type(
            self.ELEMENT_XPATH_BY_ID.format("ar303497400"), 
            ci_name
        )
        time.sleep(2)
        self.click(self.TYPEHEAD_LIST_ELEMENT_TEXT.format(ci_name))
        
    def click_assign_to_me(self):
        print("Click the 'Assign To Me' link")
        wait = WebDriverWait(self.driver, 60) 
        element = self.driver.find_element(By.XPATH, self.ASSIGN_TO_ME_BUTTON)
        self.scroll_to_element(element)
        element = wait.until(
            EC.visibility_of_element_located((By.XPATH, self.ASSIGN_TO_ME_BUTTON))
        )
        self.click(By.XPATH, self.ASSIGN_TO_ME_BUTTON)

    def save_incident(self):
        print("Save the incident")
        self.click(By.XPATH, self.SAVEBUTTON)

    def scroll_to_launchops(self):
        print("Scroll to Launch chatOps button")
        toScroll_DescreptionID=self.driver.find_element(By.XPATH,"//button[@role='tab' and .//span[normalize-space(text())='Tasks']]")
        toScroll_DescreptionID.send_keys(Keys.PAGE_UP)
        time.sleep(0.5)

    def launch_chatOps(self):
        print("Launch chatOps")
        try:
            element = self.driver.find_element(By.NAME, self.Launch_CHAT_OPS)
            self.driver.execute_script("window.scrollTo(0, 0);")
            # self.scroll_to_element(element)
            self.click(By.NAME, self.Launch_CHAT_OPS)
        except Exception as e:
            print(f"Primary click failed: {e}. Trying JS click as fallback.")
            element = self.driver.find_element(By.NAME, self.Launch_CHAT_OPS)
            self.driver.execute_script("arguments[0].click();", element)

    def key_clear_and_type(self, locator, text,timeout=300):
        try:
            print(f"Waiting for element to be visible: {locator}")
            element = WebDriverWait(self.driver, timeout).until(
                EC.visibility_of_element_located((By.XPATH, locator))
            )
            print("Element found. Sending keys...")
            element.clear()
            element.send_keys(text)
            print("Input sent.")
        except Exception as e:
            print(f"Failed to interact with element within {timeout} seconds: {e}")
            raise

    def click(self, *args):
        print("Helper method to click an element")
        if len(args) == 1:
            # If only one argument is provided, assume it's an xpath string
            WebDriverWait(self.driver, 30).until(EC.element_to_be_clickable((By.XPATH, args[0])))
            element = self.driver.find_element(By.XPATH, args[0])
        else:
            # If two arguments are provided, assume it's (By, value)
            WebDriverWait(self.driver, 30).until(EC.element_to_be_clickable((args[0], args[1])))
            element = self.driver.find_element(args[0], args[1])
        element.click() 
    
    def get_incidentid(self):
        if self.is_element_ready(By.ID,self.INCIDENTID_LABEL,timeout=180):
            ID= self.driver.find_element(By.ID,self.INCIDENTID_LABEL).text
            print ("Fetching Incident ID as {ID} for further use.")
            return ID

    def add_members(self,name):
        element =self.driver.find_element(By.ID, self.ADD_MEMBERSID)
        element.clear()
        element.send_keys(name)
        time.sleep(2)
        element.send_keys(Keys.RETURN)
        time.sleep(2)
        if self.is_element_ready(By.ID,self.Add_BUTTONID):
            element = self.driver.find_element(By.ID,self.Add_BUTTONID)
            element.click()
        time.sleep(2)
        element =self.driver.find_element(By.XPATH,self.LAUNCH_DEVOPS_ADDTEAM)
        element.click()
        time.sleep(5)

    def verify_element_has_text_automated_note(self):
        """
        Tries up to 5 times (within 20 seconds) to find 'Automated note' inside any iframe.

        Returns:
            bool: True if found, False otherwise.
        """
        total_timeout = 5
        retries = 5
        per_try_timeout = total_timeout // retries
        #testing direct approach 
        # elementtoverify=self.driver.find_element(By.XPATH,self.AUTOMATEDNOTES)
        # if elementtoverify :
        #     return True
        for attempt in range(retries):
            print(f"\nAttempt {attempt + 1} of {retries}")

            try:
                elementtoverify=self.driver.find_element(By.XPATH,self.AUTOMATEDNOTES)
                if elementtoverify :
                    return True
                # Get all iframes on the page
                iframes = self.driver.find_elements(By.TAG_NAME, "iframe")
                print(f"Found {len(iframes)} iframes on the page")

                for index, iframe in enumerate(iframes):
                    try:
                        self.driver.switch_to.default_content()
                        print(f"Switching to iframe {index}")
                        self.driver.switch_to.frame(iframe)

                        # Wait for the target element in this iframe
                        WebDriverWait(self.driver, per_try_timeout).until(
                            EC.presence_of_element_located((By.XPATH, self.AUTOMATEDNOTES))
                        )
                        print("Notes found inside iframe.")
                        return True

                    except TimeoutException:
                        print(f"Notes not found in iframe {index}, continuing...")

                    finally:
                        self.driver.switch_to.default_content()

            except Exception as e:
                print(f"Unexpected error during attempt {attempt + 1}: {e}")

        print("'Automated note' not found in any iframe after all retries.")
        return False

    def verifyassigned_To_me(self,timeout=300,name =None,assign_to_me=True):
        self.driver.switch_to.default_content()
        print(f"Waiting up to {timeout} seconds for iframe to appear...")

        WebDriverWait(self.driver, timeout).until(
            EC.frame_to_be_available_and_switch_to_it((By.XPATH, self.IFRAME))
        )
        print("Switched to iframe.")
        element = WebDriverWait(self.driver, timeout).until(
        EC.presence_of_element_located((By.ID, self.INNERFRAME))
        )
        if name in element.text:
            print(f"Assigned to me: {name} is present in the element text.")
        else:
            try:
                if assign_to_me==True:
                    self.click_assign_to_me()
                    print(f"Assigned to me: {name}.")
            except Exception as e:
                print(f"Failed to switch to iframe within {timeout} seconds: {e}")
        time.sleep(5) #wait for page to load properly
        element = WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((By.XPATH, self.REFRESHBUTTON))
        )
        self.scroll_to_element(element)
        self.click_element(By.XPATH, self.REFRESHBUTTON)
        time.sleep(3)
        self.launch_chatOps()
  
    def find_text_in_any_iframe(self, text_to_find, timeout=10) -> bool:
        """
        Tries up to 5 times (with 3s wait between) to find specific text in any iframe on the page.

        Args:
            text_to_find (str): Text to locate.
            timeout (int): Wait time for each iframe content to load.

        Returns:
            bool: True if text is found, False otherwise.
        """
        for attempt in range(1, 6):  # Retry up to 5 times
            print(f"Attempt {attempt}/5 to find text in iframes")
            try:
                self.driver.refresh()
                time.sleep(3)  # Wait for page to settle after refresh

                iframes = self.driver.find_elements(By.TAG_NAME, "iframe")
                print(f"Found {len(iframes)} iframes")

                for index, iframe in enumerate(iframes):
                    try:
                        WebDriverWait(self.driver, timeout).until(
                            EC.frame_to_be_available_and_switch_to_it(iframe)
                        )
                        print(f"Switched to iframe {index + 1}")

                        WebDriverWait(self.driver, timeout).until(
                            lambda d: d.execute_script("return document.readyState") == "complete"
                        )

                        elements = self.driver.find_elements(
                            By.XPATH, f"//*[contains(text(), '{text_to_find}')]"
                        )
                        if elements:
                            print(f"Found text '{text_to_find}' in iframe {index + 1}")
                            return True
                    except Exception as e:
                        print(f"Error in iframe {index + 1}: {e}")
                    finally:
                        self.driver.switch_to.default_content()

            except Exception as e:
                print(f"Attempt {attempt} failed with exception: {e}")

            print(f"Waiting 3 seconds before next retry...")
            time.sleep(3)

        print(f"Text '{text_to_find}' not found in any iframe after 5 attempts")
        return False

    def scroll_to_bottom(self,by,value, pause_time=1):
        
        element =self.driver.find_element(By.ID,self.LAUNCH_DEVOPS_ADDTEAM)
        element.click()

        last_height = self.driver.execute_script("return document.body.scrollHeight")

        while True:
            print("Scroll down to bottom")
            self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            print("Wait for the page to load")
            time.sleep(pause_time)
            print("Calculate new scroll height and compare with last scroll height")
            new_height = self.driver.execute_script("return document.body.scrollHeight")
            if new_height == last_height:
                break
            last_height = new_height

    def create_incident_from_template(self,testdata):
        try:
            print("Creating incident...")
            self.fill_incident_template(testdata)
            time.sleep(2)  # Wait for the form to process
            if testdata.get("AssignToMe", False):
                print("Assigning incident to self...")
                self.click_assign_to_me()
                time.sleep(2)  
            self.save_incident()
            time.sleep(2)  # Wait for the incident to be saved
            INCIDENT_ID = self.get_incidentid()
            print(f"Created incident with ID: {INCIDENT_ID}")
            time.sleep(3)
            self.driver.switch_to.default_content()
            return INCIDENT_ID
        except Exception as e:
            print(f"Exception occurred during incident creation: {e}")
            return None
    