import time
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from test_config import TestConfig
from utils.constants import SolQAConstants
from utils.common_waits import CommonWaits

class Portal(BasePage):
    def __init__(self, driver):
        self.driver = driver
        self.common_waits = CommonWaits(driver=driver)
        self.wait = WebDriverWait(driver, TestConfig.WAIT_TIMEOUT)
    
    def navigate_to_userskeys(self):
        ade_url = TestConfig.ADEURL
        self.driver.get(f"{ade_url}/settings/usersandkeys")
        self.common_waits.wait_for_all_spinners_to_hide(SolQAConstants.APP_ITOM)
        self.wait.until(EC.presence_of_element_located((By.XPATH, "//h1[text()='Users & Keys']")))