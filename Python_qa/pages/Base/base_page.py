import time
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException, ElementClickInterceptedException
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.remote.webelement import WebElement
from test_config import TestConfig

class BasePage:
    PRODUCT_ENDPOINTS = {
            "AIOPS": "/aiops",
            "BHOM": "/monitor",
            "SmartIT":"/smartit",
            "dwp":"/dwp"
        }
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, TestConfig.WAIT_TIMEOUT)  # Using timeout from TestConfig
        
    def find_element(self, by, value, timeout=None):
        """Find element with explicit wait"""
        wait = self.wait if timeout is None else WebDriverWait(self.driver, timeout)
        return wait.until(EC.presence_of_element_located((by, value)))
    
    def find_elements(self, by, value, timeout=None):
        """Find elements with explicit wait"""
        wait = self.wait if timeout is None else WebDriverWait(self.driver, timeout)
        return wait.until(EC.presence_of_all_elements_located((by, value)))
    
    def click_with_js(self, element: WebElement):
        """Click element using JavaScript"""
        self.driver.execute_script("arguments[0].click();", element)

    def click_element(self, by, value, timeout=TestConfig.WAIT_TIMEOUT):
        """Click element with explicit wait and JavaScript fallback"""
        wait = self.wait if timeout is None else WebDriverWait(self.driver, timeout)
        element = wait.until(EC.element_to_be_clickable((by, value)))
        
        try:
            # Try regular click first
            element.click()
        except ElementClickInterceptedException:
            print("Regular click failed, trying JavaScript click...")
            # Try JavaScript click as fallback
            self.click_with_js(element)
        except Exception as e:
            print(f"Click failed with error: {str(e)}")
            # Try JavaScript click as last resort
            self.click_with_js(element)

    def click_if_enabled(self, by, value, timeout=None):
        """Click the button only if it is enabled"""
        wait = self.wait if timeout is None else WebDriverWait(self.driver, timeout)
        element = wait.until(EC.presence_of_element_located((by, value)))
        if element.is_enabled():
            try:
                element.click()
            except ElementClickInterceptedException:
                print("Regular click failed, trying JavaScript click...")
                self.click_with_js(element)
            except Exception as e:
                print(f"Click failed with error: {str(e)}")
                self.click_with_js(element)
        else:
            print("Button is not enabled, click skipped.")

    def send_keys(self, by, value, text, timeout=None):
        """Send keys to element with explicit wait"""
        element = self.find_element(by, value, timeout)
        # element.clear()
        # self.driver.execute_script("arguments[0].value = 'Your text here';", element)
        element.send_keys(text)

    def scroll_and_send_keys(self, by, value, text, timeout=None):
        """Send keys to element with explicit wait"""
        element = self.find_element(by, value, timeout)
        # element.clear()
        self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
        self.driver.execute_script("arguments[0].value = 'Your text here';", element)
        # element.send_keys(text)

    def scroll_to_element(self, element: WebElement):
        """Scroll to the specified element using JavaScript"""
        self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
        ActionChains(self.driver).move_to_element(element).perform()

    def is_element_ready(self, by_type, locator, timeout=10):
        try:
            WebDriverWait(self.driver, timeout).until(EC.element_to_be_clickable((by_type, locator)))
            return True
        except TimeoutException:
            return False         
          
    def is_element_present(self, by, value, timeout=180):
        try:
            WebDriverWait(self.driver, timeout).until(EC.presence_of_element_located((by, value)))
            return True
        except TimeoutException:
            return False
            
    def find_element(self, by, value):
        return self.driver.find_element(by, value)

    def quit(self):
        """Quit the driver"""
        self.driver.quit()
    
    def switch_frame(self, by, value, timeout=180):
        wait = WebDriverWait(self.driver, timeout)
        iframe = wait.until(EC.frame_to_be_available_and_switch_to_it((by, value)))
        return iframe
    
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
    
    def wait_till_element_loaded(self, by, value, timeout=180):
        """Wait until the element is loaded; return True if present else False"""
        try:
            WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((by, value))
            )
            return True
        except TimeoutException:
            return False
    
    