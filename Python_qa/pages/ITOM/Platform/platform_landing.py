from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import pyperclip  
import logging
from utils.common_waits import CommonWaits
from pages.Base.base_page import BasePage

class PlatformLandingPage(BasePage):
    def __init__(self, driver):
        self.driver = driver
        self.common_waits = CommonWaits(driver=driver)

        # Locators
        self.verify_login = "//header/nav//*[contains(text(),'Home')]"
        self.navigation_tab = "//button[contains(@class, 'adapt-menu__link')]//span[text()='{}']"
        self.menu_item = "//span[text()='{}']/ancestor::button"
        self.item = "//span[text()='{}']"
        self.key_input = "//adapt-rx-control-label[contains(. ,'{}')]/following-sibling::div"
        self.key_expiry = "//*[text()='{}']/ancestor::button"
        self.product_tile = "//div[contains(text(),'{}')]/ancestor::div[contains(@class, 'card-test')]"

        # WebElements (as locators)
        self.confirm_btn = (By.XPATH, "//button[normalize-space()='Confirm']")
        self.roles_btn = (By.XPATH, "//*[text()='Roles']/following-sibling::a/span")
        self.close_button = (By.XPATH, "//i[@class='d-icon-cross_adapt']")
        self.home_page_button = (By.XPATH, "//a[@href='#/landing']")

    def verify_platform_landing_page_opened(self):
        self.common_waits.wait_for_angular_requests_to_finish()
        self.common_waits.wait_for_all_spinners_to_hide()
        login_value = self.common_waits.wait_for_element_visible_with_fluent_wait(self.driver, self.verify_login, 30)
        assert login_value, "Login to ADE platform is not successful"
        self.log.info("Platform Landing Page is opened")

    def cross_launch_product(self, product_name):
        product_tile_xpath = self.product_tile.format(product_name)
        product_tile_element = self.common_waits.wait_for_element_to_be_visible(by=By.XPATH, locator=product_tile_xpath,timeout=600)
        self.common_waits.scroll_to_element(product_tile_element)
        product_tile_element.click()
        self.common_waits.wait_for_angular_requests_to_finish()
        self.common_waits.wait_for_all_spinners_to_hide()
        self.common_waits.switch_to_tab_by_title(product_name)

    def navigate_to_page(self, navigation_tab_name, menu_item_name):
        nav_tab_xpath = self.navigation_tab.format(navigation_tab_name)
        menu_item_xpath = self.menu_item.format(menu_item_name)
        self.common_waits.wait_for_element_to_be_visible(By.XPATH, nav_tab_xpath).click()
        self.common_waits.wait_for_element_to_be_visible(By.XPATH, menu_item_xpath).click()
        self.common_waits.wait_for_all_spinners_to_hide()

    def click_by_text(self, text):
        item_xpath = self.item.format(text)
        self.common_waits.wait_for_element_to_be_visible(By.XPATH, item_xpath).click()

    def create_new_key(self, key_name):
        self.navigate_to_page("User access", "Users and keys")
        self.click_by_text("Access keys")
        self.click_by_text("Add key")
        key_input_xpath = self.key_input + "//input"
        we = self.common_waits.wait_for_element_to_be_visible(By.XPATH, key_input_xpath.format("Key name"))
        we.click()
        we.send_keys(key_name)
        we = self.common_waits.wait_for_element_to_be_visible(By.XPATH, self.key_input.format("Key expiry"))
        we.click()
        we = self.common_waits.wait_for_element_to_be_visible(By.XPATH, self.key_expiry.format("Never expires (not recommended)"))
        if we.get_attribute("aria-selected") == "false":
            we.click()
        self.common_waits.click_without_interception(self.driver.find_element(self.confirm_btn))
        we = self.common_waits.wait_for_element_to_be_visible(By.XPATH, self.menu_item.format("Copy key details"))
        we.click()
        # Use pyperclip to get clipboard data
        return pyperclip.paste()

    def close_key_details(self):
        self.common_waits.click_without_interception(self.driver.find_element(self.close_button))

    def navigate_to_home_page(self):
        self.common_waits.click_without_interception(self.driver.find_element(self.home_page_button))
        self.common_waits.wait_for_all_spinners_to_hide()