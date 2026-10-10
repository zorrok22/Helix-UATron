from multiprocessing.connection import wait
import time
from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException

from utils.ui_utils import utilities
from utils.constants import SolQAConstants

class smart_it_dashboard(BasePage):

    def __init__(self, driver):
        self.driver = driver
        
        # Locators for Smart IT Dashboard
        self.CREATE_NEW = "//a[text()='Create New ']"
        self.CREATE_NEW_INCIDENT = "//span[@class='template-chooser-dropdown-item__title ng-binding' and text()='Incident']"
        self.HELIX_GPT_LINK_XPATH = "//a[@id='header-helixGPT_search_button']"
        self.CHATBUTTON_XPATH="//button[normalize-space(text())='Chat']"
        self.SEARCHBUTTON_XPATH="//button[normalize-space(text())='Search']"
        self.CHATBOX_INPUT_XPATH="//div[@class='rx-textarea--disable-resize']//textarea"
        self.SEND_BUTTON="//button[.//span[text()='Send message']]"
        self.REPLY_XPATH = "//adapt-speech-bubble[contains(@class,'speech-bubble--received')]//div[contains(@class,'speech-bubble__item')]/div"
        self.STUDIO_IFRAME="//iframe[@id='studio-frame']"
        self.NEWTOPICXPATH="//button[contains(text(),'Start a new topic')]"
        
    def navigate_to_create_new_incident(self):
        print("Navigating to create new -> Incident ")
        try:
            wait = WebDriverWait(self.driver, 300)  # 5 minutes

            print("Wait for 'Create New' button to be clickable")
            create_new_btn = wait.until(
                EC.element_to_be_clickable((By.XPATH, self.CREATE_NEW))
            )
            print("Click on Create new..")
            create_new_btn.click()
            time.sleep(2)
            print("Wait for 'Incident' option to be clickable")
            try:  
                incident_btn = wait.until(
                    EC.element_to_be_clickable((By.XPATH, self.CREATE_NEW_INCIDENT))
                )
                time.sleep(2) 
                print("Click on Incident")
                incident_btn.click()
            except:
                element = self.driver.find_element(By.XPATH, self.CREATE_NEW_INCIDENT)
                self.driver.execute_script("arguments[0].click();", element)   

        except Exception as e:
            print(f"Create new incident navigation failed: {e}")
    
    def open_Helix_GPT_chat(self):
        print("Launching Helix GPT Manager from Smart IT Dashboard")
        try:
            print("Open on Helix GPT chat.")
            self.click_element(By.XPATH, self.CHATBUTTON_XPATH)
            return True
        except Exception as e:
            print(f"Launching Helix GPT Manager failed: {e}")
        return False
    
    def Open_global_search(self):
        print("Opening Global Search from Smart IT Dashboard")
        try:
            self.click_element(By.XPATH, self.HELIX_GPT_LINK_XPATH)
            print("Clicked on Helix GPT Search button.")
            return True
        except Exception as e:
            print(f"Opening Global Search failed: {e}")
        return False
    
    def verify_chat_and_search_options(self):
        print("Verifying Chat and Search options in Global Search")
        try:
            self.Open_global_search()
            wait = WebDriverWait(self.driver, 60)
            self.switch_frame(By.XPATH, self.STUDIO_IFRAME, timeout=60)
            chat_button = wait.until(EC.presence_of_element_located((By.XPATH, self.CHATBUTTON_XPATH)))
            search_button = wait.until(EC.presence_of_element_located((By.XPATH, self.SEARCHBUTTON_XPATH)))
            if chat_button and search_button:
                print("Chat and Search buttons are present.")
                self.open_Helix_GPT_chat()
                return True
            else:
                if not chat_button:
                    print("Chat button is not present.")
                if not search_button:
                    print("Search button is not present.")
                return False
        except Exception as e:
            print(f"Verification of Chat and Search options failed: {e}")
        return False
    
    def enter_message_in_chatbox(self, message):
        print("Entering message in Helix GPT chatbox")
        try:
            WebDriverWait(self.driver, 60).until(
            EC.presence_of_element_located((By.XPATH, self.CHATBOX_INPUT_XPATH))
        ) #Added to wait till page loads
            
            try:
                print("Waiting for New Topic button to be clickable")
                WebDriverWait(self.driver, 3).until(
                EC.presence_of_element_located((By.XPATH, self.NEWTOPICXPATH))
                )
                print("New Topic button found.")
                WebDriverWait(self.driver, 3).until(
                EC.element_to_be_clickable((By.XPATH, self.NEWTOPICXPATH))
                )
                start_new_topic= self.find_element(By.XPATH, self.NEWTOPICXPATH)
                if start_new_topic:
                    start_new_topic.click()
                    time.sleep(2)
                    print("New Topic button clicked.")
            except TimeoutException:
                print("New Topic button not found.")
                pass  

            chatbox_input = self.find_element(By.XPATH, self.CHATBOX_INPUT_XPATH)
            if chatbox_input:
                print("Chatbox input field found.")
                chatbox_input.clear()
                chatbox_input.send_keys(message)
                print("Message entered successfully.",message)
                return True
            else:
                print("Chatbox input field not found.")
                return False
        except Exception as e:
            print(f"Entering message in chatbox failed: {e}")
        return False
    
    def send_message_in_chatbox(self):
        print("Sending message in Helix GPT chatbox")
        try:
            self.click_element(By.XPATH, self.SEND_BUTTON)
            return True
        except Exception as e:
            print(f"Sending message in chatbox failed: {e}")
        return False

    def get_full_assistant_reply(self, timeout=60, wait_stability=2, cleanReply=True):
        """
        Waits for the full assistant reply to load completely using XPath and returns the text.
        :param driver: Selenium WebDriver instance.
        :param timeout: Maximum wait time for response to start.
        :param wait_stability: Time to wait with no DOM changes (indicates stability).
        :return: Full reply as a string.
        """

        # XPath to locate assistant response bubbles (received messages)
        placeholder_reply = SolQAConstants.SMARTIT_PLACEHOLDER_REPLY
        placeholder_reply_lower =placeholder_reply.lower()

        WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((By.XPATH, self.REPLY_XPATH))
        )

        previous_text = ""
        stable_count = 0

        for _ in range(timeout):
            elements = self.driver.find_elements(By.XPATH, self.REPLY_XPATH)
            # Get only the last response element
            if elements:
                last_element = elements[-1]
                current_text = last_element.text.strip()
            else:
                current_text = ""

            # Skip temporary placeholder replies and keep waiting for the final response.
            if current_text and placeholder_reply_lower in current_text.lower():
                stable_count = 0
                previous_text = ""
                time.sleep(1)
                continue

            if current_text == previous_text and current_text:  # Ensure text is not empty
                stable_count += 1
            else:
                stable_count = 0
                previous_text = current_text

            if stable_count >= wait_stability:
                break
            # Wait for a short period to ensure the last response is fully rendered
            time.sleep(1)
        
        if cleanReply:
            return self.clean_reply_text(previous_text)

        return previous_text
    
    def clean_reply_text(self, text):
        """
        Removes unwanted substrings from the assistant reply text.
        Substrings removed: 'HelixGPT replied:', '\n', '\r'
        """
        cleaned = text.replace('HelixGPT replied:', '')
        cleaned = cleaned.replace('\r', '')
        cleaned = cleaned.replace('\n', '')
        return cleaned.strip()