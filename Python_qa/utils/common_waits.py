from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import WebDriverException, NoSuchElementException
from pages.Base.base_page import BasePage

import time

class CommonWaits(BasePage):
    def __init__(self, driver, default_timeout=30):
        self.driver = driver
        self.default_timeout = default_timeout

    def wait_for_element_to_be_visible(self, by, locator, timeout=None):
        timeout = timeout or self.default_timeout
        return WebDriverWait(self.driver, timeout, poll_frequency=0.5, ignored_exceptions=[WebDriverException]).until(
            EC.visibility_of_element_located((by, locator))
        )

    def wait_for_element_visible(self, element, timeout=None):
        timeout = timeout or self.default_timeout
        return WebDriverWait(self.driver, timeout, poll_frequency=0.5, ignored_exceptions=[WebDriverException]).until(
            EC.visibility_of(element)
        )

    def wait_for_element_clickable(self, element, timeout=None):
        timeout = timeout or self.default_timeout
        return WebDriverWait(self.driver, timeout, poll_frequency=0.5, ignored_exceptions=[WebDriverException]).until(
            EC.element_to_be_clickable(element)
        )

    def wait_for_element_presence(self, by, locator, timeout=None):
        timeout = timeout or self.default_timeout
        return WebDriverWait(self.driver, timeout, poll_frequency=0.5, ignored_exceptions=[WebDriverException]).until(
            EC.presence_of_element_located((by, locator))
        )

    def wait_for_element_invisible(self, element, timeout=None):
        timeout = timeout or self.default_timeout
        return WebDriverWait(self.driver, timeout, poll_frequency=0.5, ignored_exceptions=[WebDriverException]).until(
            EC.invisibility_of_element(element)
        )

    def wait_for_invisibility_of_element_located(self, by, locator, timeout=None):
        timeout = timeout or self.default_timeout
        return WebDriverWait(self.driver, timeout, poll_frequency=0.5, ignored_exceptions=[WebDriverException]).until(
            EC.invisibility_of_element_located((by, locator))
        )

    def wait_for_all_spinners_to_hide(self, app_name=None):
        # You can expand this mapping as needed
        spinner_locators = {
            "DWP": "//*[contains(@class, 'loader-container')]",
            "BHOM": "//*[contains(@class, 'loader-container')]",
            "BWF": "//*[contains(@class, 'application-loader')]",
            "SMARTIT": "//*[contains(@class, 'loader')]",
            # Add more as needed
        }
        locator = spinner_locators.get((app_name or "").upper())
        if locator:
            try:
                elements = self.driver.find_elements(By.XPATH, locator)
                for el in elements:
                    self.wait_for_element_invisible(el)
            except Exception as e:
               print("Spinners did not finish loading: %s", e)

    def wait_for_angular_requests_to_finish(self):
        print("Waiting for AngularJS requests to complete")
        try:
            is_angular = self.driver.execute_script("return window.angular !== undefined;")
            if not is_angular:
                print("AngularJS not present, skipping wait")
                return
            WebDriverWait(self.driver, 40, poll_frequency=2, ignored_exceptions=[WebDriverException]).until(
                lambda d: d.execute_script(
                    "return angular.element(document).injector().get('$http').pendingRequests.length === 0;")
            )
        except Exception as e:
           print("AngularJS wait failed/skipped: %s", e)

    def scroll_to_element(self, element):
        self.driver.execute_script("arguments[0].scrollIntoView();", element)

    def click_without_interception(self, element):
        try:
            self.wait_for_element_clickable(element)
            element.click()
        except Exception as e:
            print("Click failed: %s", e)
            time.sleep(0.5)
            element.click()

    def switch_to_tab_by_title(self, title):
        for handle in self.driver.window_handles:
            self.driver.switch_to.window(handle)
            if title in self.driver.title:
                print(f"Switched to tab with title: {title}")
                return
        print(f"No tab found with title: {title}")

    def wait_for_element_visible_with_fluent_wait(self, driver, locator, seconds):
        try:
            WebDriverWait(driver, seconds, poll_frequency=0.5, ignored_exceptions=[WebDriverException]).until(
                EC.visibility_of_element_located((By.XPATH, locator))
            )
            return True
        except Exception as e:
            print(f"Exception in wait_for_element_visible_with_fluent_wait: {e}")
            return False

    def wait_for_sometime(self, ms):
        time.sleep(ms / 1000.0)

    def pause(self):
        time.sleep(0.5)

