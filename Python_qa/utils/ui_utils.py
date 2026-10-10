import time
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException, NoSuchElementException
from pages.ITOM.AiOps.locators import AIOPSLocators
from test_config import TestConfig
from pages.Base.base_page import BasePage
import json
import os
import re
import autoit


class ServiceDataManager:
    """Class to manage service test data from JSON files"""
    
    _instances = {}  # Dictionary to store instances for different JSON files
    _service_data = {}  # Dictionary to store data for different JSON files
    
    def __new__(cls, json_file_path):
        if json_file_path not in cls._instances:
            cls._instances[json_file_path] = super(ServiceDataManager, cls).__new__(cls)
        return cls._instances[json_file_path]
    
    def __init__(self, json_file_path):
        """
        Initialize ServiceDataManager with JSON file path
        
        Args:
            json_file_path (str): Full path to the JSON file containing service data
        """
        self.json_file_path = json_file_path
        if json_file_path not in self._service_data:
            self._service_data[json_file_path] = self._load_service_data()
       
    def _load_service_data(self):
        """Load service test data from JSON file"""
        try:
            print(f"Loading service data from: {self.json_file_path}")
            with open(self.json_file_path, 'r') as file:
                return json.load(file)
        except FileNotFoundError:
            print(f"Service data file not found: {self.json_file_path}")
            return None
        except json.JSONDecodeError as je:
            print(f"Invalid JSON in file {self.json_file_path}: {str(je)}")
            return None
        except Exception as e:
            print(f"Error loading service data from {self.json_file_path}: {str(e)}")
            return None
    
    def get_service_info(self, service_key=None):
        """
        Get service information from the loaded JSON data
        
        Args:
            service_key (str, optional): Key of the service to get info for.
                                       If not provided, uses default service.
            
        Returns:
            dict: Service information if found, None otherwise
        """
        if not self._service_data[self.json_file_path]:
            print(f"No service data available in {self.json_file_path}")
            return None
            
        if not service_key:
            service_key = self._service_data[self.json_file_path]['search'].get('default_service')
            
        if service_key not in self._service_data[self.json_file_path]['services']:
            print(f"Service key '{service_key}' not found in {self.json_file_path}")
            return None
            
        return self._service_data[self.json_file_path]['services'][service_key]
    
    def get_all_services(self):
        """
        Get all available services from the JSON data
        
        Returns:
            dict: Dictionary of all services if data is available, None otherwise
        """
        if not self._service_data[self.json_file_path]:
            return None
        return self._service_data[self.json_file_path]['services']
    
    def get_search_params(self):
        """
        Get search parameters from the JSON data
        
        Returns:
            dict: Search parameters if data is available, None otherwise
        """
        if not self._service_data[self.json_file_path]:
            return None
        return self._service_data[self.json_file_path]['search']
    
    def get_verification_params(self):
        """
        Get verification parameters from the JSON data
        
        Returns:
            dict: Verification parameters if data is available, None otherwise
        """
        if not self._service_data[self.json_file_path]:
            return None
        return self._service_data[self.json_file_path]['verification']
    
    def reload_data(self):
        """
        Reload the service data from the JSON file
        
        Returns:
            bool: True if reload was successful, False otherwise
        """
        try:
            self._service_data[self.json_file_path] = self._load_service_data()
            return self._service_data[self.json_file_path] is not None
        except Exception as e:
            print(f"Error reloading service data from {self.json_file_path}: {str(e)}")
            return False

class ADEPortalPage(BasePage):
    def __init__(self, driver):
        self.driver = driver
        self.locators = AIOPSLocators()
        self.wait = WebDriverWait(driver, TestConfig.WAIT_TIMEOUT)
   
    def send_keys(self, by, value, text):
        """Send keys to an element"""
        element = self.wait.until(EC.presence_of_element_located((by, value)))
        element.clear()
        element.send_keys(text)
    
    def click_element(self, by, value):
        """Click an element"""
        element = self.wait.until(EC.element_to_be_clickable((by, value)))
        element.click()

    def get_product_url(self, product, baseURL=None):
        """Get the complete URL for a specific product"""
        if baseURL is None:
            baseURL = TestConfig.ADEURL
        if product not in BasePage.PRODUCT_ENDPOINTS:
            raise ValueError(f"Product {product} not found in configuration")
        return f"{baseURL}{BasePage.PRODUCT_ENDPOINTS[product]}"

    def login(self, username, password, product, baseURL=None):
        """Login to a specific product in ADE Portal and verify successful login"""
        try:
            # Navigate to product URL
            if baseURL is None:
                baseURL = TestConfig.ADEURL

            product_url = self.get_product_url(product)
            print("product_url:",product_url)
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
        
    

    def navigate_to_situations(self):
        """
        Navigate to the Situations page in ADE portal
        
        Returns:
            bool: True if navigation was successful, False otherwise
        """
        try:
            # Click on Situations menu item
            situations_menu = (By.XPATH, "//span[contains(text(),'Situations')]")
            self.click_element(*situations_menu)
            
            # Wait for Situations page to load
            situations_title = (By.XPATH, "//h1[contains(text(),'Situations')]")
            self.wait.until(EC.presence_of_element_located(situations_title))
            
            # Wait for page load to complete
            self.wait.until(lambda driver: driver.execute_script('return document.readyState') == 'complete')
            
            print("Successfully navigated to Situations page")
            return True
            
        except TimeoutException as te:
            print(f"Timeout while navigating to Situations page: {str(te)}")
            return False
        except NoSuchElementException as nse:
            print(f"Could not find Situations menu item: {str(nse)}")
            return False
        except Exception as e:
            print(f"Error navigating to Situations page: {str(e)}")
            return False

    def search_service(self, service_name):
        """
        Search for a service by name in the Situations page
        
        Args:
            service_name (str): Name of the service to search for
            
        Returns:
            bool: True if service is found, False otherwise
        """
        try:
            # Click search button
            search_button = (By.XPATH, "//button[@data-testid='global-search_button']")
            self.click_element(*search_button)
            
            # Enter service name in search field
            search_input = (By.XPATH, "//input[@data-testid='global-search_input']")
            self.send_keys(*search_input, service_name)
            
            # Wait for search results
            service_result = (By.XPATH, f"//div[contains(@class,'search-result-item')]//span[contains(text(),'{service_name}')]")
            self.wait.until(EC.presence_of_element_located(service_result))
            
            # Click on the service result
            self.click_element(*service_result)
            
            # Verify service details are displayed
            service_details = (By.XPATH, "//div[contains(@class,'service-details')]")
            self.wait.until(EC.presence_of_element_located(service_details))
            
            print(f"Successfully found and selected service: {service_name}")
            return True
            
        except TimeoutException as te:
            print(f"Timeout while searching for service {service_name}: {str(te)}")
            return False
        except NoSuchElementException as nse:
            print(f"Service {service_name} not found: {str(nse)}")
            return False
        except Exception as e:
            print(f"Error searching for service {service_name}: {str(e)}")
            return False

    def login_to_product(driver, product="AIOPS"):
        """Helper function to login to a specific product in ADE Portal"""
        portal_page = ADEPortalPage(driver)
        return portal_page.login(product)

class utilities(BasePage):
    def __init__(self, driver):
        self.driver = driver
       
    SMARIT_LOGINID="user_login"
    SMARIT_LOGINPWD="login_user_password"
    SMARIT_LOGIN_SUBMIT="login-jsp-btn"
    # Go up 2 levels: utils/ -> Python_qa/ -> repository_root/
    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../.."))
    
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(self.driver, 300)
       
    def load_config(self, file_name="azure_config.json"):
        # Don't hardcode repository name - project_root already points to repo root
        config_path = os.path.join(self.project_root, "Python_qa", "testdata", "GPT", file_name)
        
        # Debug: Print the path being used
        print(f"Loading config from: {config_path}")
        
        if not os.path.exists(config_path):
            raise FileNotFoundError(f"Config file not found at: {config_path}")
        
        with open(config_path, 'r') as f:
            config = json.load(f)
        return config
    
    def load_common_data(self):
        """
        Load common data (usernames, passwords, skill names) from config_data.json
        
        Returns:
            dict: Common data dictionary containing all shared test data
        """
        config_path = os.path.join(self.project_root, "Python_qa", "testdata", "GPT", "config_data.json")
        
        print(f"Loading common data from: {config_path}")
        
        if not os.path.exists(config_path):
            raise FileNotFoundError(f"Config file not found at: {config_path}")
        
        with open(config_path, 'r') as f:
            config = json.load(f)
        
        if 'common_data' not in config:
            raise KeyError("'common_data' key not found in config_data.json")
        
        return config['common_data']

    def login(self, username, password, URL=None,Login=True):
        try:
            print("Login to Application")
            if URL is None:
              URL = self.get_ITSM_product_url("SmartIT")
            self.driver.get(URL)
            if not Login:
                print("Skipping login as per parameter.")
                return True
            # Wait up to 5 minutes (300 seconds) for the page and each control to load
            wait = WebDriverWait(self.driver, 300)
            element = wait.until(EC.presence_of_element_located((By.ID, self.SMARIT_LOGINID)))
            print("Enter UserName : ", username)
            element.send_keys(username)
            element = wait.until(EC.presence_of_element_located((By.ID, self.SMARIT_LOGINPWD)))
            print("Enter Password : ", password)
            element.send_keys(password)
            element = wait.until(EC.element_to_be_clickable((By.ID, self.SMARIT_LOGIN_SUBMIT)))
            element.click()
            print("Click on the SmartIT Login Submit")
            return True
        except Exception as e:
            print(f"Error during login: {e}")
            return False

    def is_element_ready(self, by_type, locator, timeout=10):
        try:
            WebDriverWait(self.driver, timeout).until(EC.element_to_be_clickable((by_type, locator)))
            return True
        except TimeoutException:
            return False         
          
    def is_element_present(self, by, value):
        try:
            self.driver.find_element(by, value)
            return True
        except:
            return False
            
    def find_element(self, by, value):
        return self.driver.find_element(by, value)
    
    def update_json(self, json_file_path, tag, value):
        try:
            # Load existing data
            if os.path.exists(json_file_path):
                with open(json_file_path, 'r') as f:
                    data = json.load(f)
            else:
                data = {}

            # Update or add the tag
            data[tag] = value

            # Save back to file
            with open(json_file_path, 'w') as f:
                json.dump(data, f, indent=4)
            return True
        except Exception as e:
            print(f"Error appending tag to JSON: {e}")
            return False

    def get_json_tag(self, json_file_path, tag):
        try:
            if not os.path.exists(json_file_path):
                print(f"File not found: {json_file_path}")
                return None
            with open(json_file_path, 'r') as f:
                data = json.load(f)
            return data.get(tag)
        except Exception as e:
            print(f"Error reading tag from JSON: {e}")
            return None
        
    def get_value_from_json_list(self,json_path, key):
        """
        Reads a JSON file containing a list of dicts and returns the value for the given key from the first dict.
        Returns None if the key is not found.
        """
        with open(json_path, "r") as f:
            data = json.load(f)
        if isinstance(data, list) and data:
            return data[0].get(key)
        return None

    def open_link_from_teams_message(self,teams_message):
    # Wait for the message container to appear (Adjust XPATH as per your Teams DOM)
        for msg in teams_message[::-1]:  # start from latest
            text = msg.text
            urls = re.findall(r'https?://\S+', text)
            if urls:
                for url in urls:
                    print("open the first matching link "+ url)
                    self.driver.execute_script(f"window.open('{url}', '_blank');")
                    return True  # success
        print("No matching URL found in the Teams message"+ str(teams_message))
        return False  # no matching URL found
    
    def open_link_from_table(self,table_element,open_first=True):
        try:
            linksElement=table_element.find_elements(By.TAG_NAME, "a")   
            if linksElement and open_first:   
                print("Clicking on the first records link in the table.")
                try:
                    linksElement[0].click() 
                except Exception as e:
                    self.click_with_js(linksElement[0])

            print("No matching URL found in the specified column of the table.")
            return True  # no matching URL found
        except Exception as e:
            print(f"Error extracting URL from table: {e}")
            return False

    def upload_file_with_autoit(self, file_path, window_title="Open"):
        try:
            # Wait up to 10 seconds for the file dialog to appear
            if autoit.win_wait(window_title, 10) == 1:
                print("File dialog detected.")

                # Activate it
                autoit.win_activate(window_title)
                time.sleep(1)

                # Send file path
                autoit.control_send(window_title, "Edit1", file_path)
                time.sleep(1)

                # Press Enter
                autoit.control_click(window_title, "Button1")
                print(f"File '{file_path}' selected.")
            else:
                print("File dialog not found.")
        except Exception as e:
            print(f"Error during AutoIt upload: {e}")

    def clear_and_type(self,by, locator, text, timeout=180):
        element = self.wait.until(EC.presence_of_element_located((by, locator)), timeout)
        element.clear()
        element.send_keys(text)

    def click_element(self, by, locator, timeout=180):
        """Click an element"""
        element = self.wait.until(EC.element_to_be_clickable((by, locator)), timeout)
        element.click()
    
    def get_ITSM_product_url(self, namespace):
        """
        Returns the full ITSM product URL for the given namespace.
        The namespace should match a key in TestConfig.PRODUCT_ENDPOINTS.
        The base URL is fetched from ITSMBASEURL in the stack config.
        """
        base_url = TestConfig.ITSMBASEURL
        if namespace not in BasePage.PRODUCT_ENDPOINTS:
            raise ValueError(f"Namespace '{namespace}' not found in PRODUCT_ENDPOINTS")
        endpoint = BasePage.PRODUCT_ENDPOINTS[namespace]
        final_endpoint = f"{base_url.format(endpoint.lstrip('/'))}{endpoint}"
        print("final URL is", final_endpoint)
        return final_endpoint
    
    def verify_browser_title(self, expected_substring, timeout=300):
        """
        Verifies that the current browser's title contains the expected substring.

        Args:
            expected_substring (str): Substring expected to be present in the browser title.
            timeout (int): Maximum time to wait for the title to contain the substring.

        Returns:
            bool: True if the title contains the substring within the timeout, False otherwise.
        """
        try:
            WebDriverWait(self.driver, timeout).until(
                lambda driver: expected_substring in driver.title
            )
            print(f"Browser title contains '{expected_substring}'.")
            return True
        except TimeoutException:
            print(f"Browser title does not contain '{expected_substring}' within {timeout} seconds.")
            return False
        
    def extract_req_id(self, text, suffix="REQ"):
        """
        Extracts the first identifier starting with the given suffix (e.g., 'CRQ' or 'REQ')
        followed by optional separators and digits. Returns None if no match is found.
        """
        if not text or not suffix:
            return None

        # Match suffix followed by optional separator(s) and digits, case-insensitive
        pattern = rf'\b{re.escape(suffix)}[\s\-_:]*\d+'
        match = re.search(pattern, text, flags=re.IGNORECASE)
        return match.group(0).replace(" ", "").replace("-", "").replace("_", "").replace(":", "") if match else None
        
    def is_title_in_any_window(self, title):
        """
        Returns True if any open window has the given title, else False.
        """
        original_window = self.driver.current_window_handle
        for handle in self.driver.window_handles:
            self.driver.switch_to.window(handle)
            if title in self.driver.title:
                self.driver.switch_to.window(original_window)
                return True
        self.driver.switch_to.window(original_window)
        return False

    def update_flag_value_in_textarea(self, xpath, key, new_value, timeout=120):
        """
        Fetch JSON data from textarea, replace only the value for the given key,
        and update the textarea with modified JSON.

        Args:
            driver (WebDriver): Selenium WebDriver instance.
            xpath (str): XPath locator for the textarea element.
            key (str): Key whose value needs to be updated.
            new_value (any): New value to assign to the key.
            timeout (int): Maximum wait time for locating textarea.
        """
        # Wait for textarea to be present
        textarea = WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((By.XPATH, xpath))
        )

        # Fetch existing text content
        raw_text = textarea.get_attribute("value")

        # Parse JSON safely
        try:
            data = json.loads(raw_text)
        except json.JSONDecodeError:
            print("Error: Textarea does not contain valid JSON.")
            return False

        print("Original data fetched:")
        print(json.dumps(data, indent=2))

        # Update only the value of the specified key
        if key in data:
            old_value = data[key]
            data[key] = new_value
            print(f"Updated key '{key}': '{old_value}' → '{new_value}'")
        else:
            print(f"Key '{key}' not found in JSON.")
            return False

        # Convert back to formatted JSON
        new_json = json.dumps(data, indent=2)

        # Clear and update the textarea
        textarea.clear()
        textarea.send_keys(new_json)

        print("Textarea updated successfully.")
        return True
    
    def load_situation_json(json_file_path):
        """Load situation JSON and replace hostname placeholder"""
        with open(json_file_path, 'r') as f:
            content = f.read()
        
        # Replace the hostname placeholder
        content = content.replace("{hostname}", TestConfig.HOSTNAME)
        
        # Parse and return the JSON
        return json.loads(content)