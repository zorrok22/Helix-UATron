from pages.Base.base_page import BasePage
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

class incident_summary(BasePage):
    def __init__(self, driver):
        super().__init__(driver)

    ASK_HELIXGPT_BUTTON="//button[@role='tab' and contains(.,'Ask HelixGPT')]"
    CHAT_INPUT="//textarea[@aria-label='Send message']"
    SEND_BUTTON="//button[contains(@class, 'adapt-chat-text-entry__action_send-message')]"
    REPLY_XPATH = "//adapt-speech-bubble[contains(@class,'speech-bubble--received')]//div[contains(@class,'speech-bubble__item')]"
    STUDIO_IFRAME="//iframe[@id='pwa-frame']"
   
    def click_ask_helixgpt_button(self):
        print("Clicking on Ask HelixGPT button")
        self.switch_frame(By.XPATH, self.STUDIO_IFRAME)
        self.click_element(By.XPATH, self.ASK_HELIXGPT_BUTTON)
        time.sleep(2)
        self.wait_for_chat_input_ready()
        print("Chat input is ready to accept inputs")

    def wait_for_chat_input_ready(self, timeout=20):
        print("Waiting for chat input to be enabled and ready to accept inputs")
        """Wait for chat input to be enabled and ready to accept inputs"""
        wait = WebDriverWait(self.driver, timeout)
        # Wait for element to be present, visible, and enabled
        chat_input = wait.until(EC.element_to_be_clickable((By.XPATH, self.CHAT_INPUT)))
        # Additional check to ensure it's not readonly or disabled
        wait.until(lambda driver: chat_input.get_attribute('aria-readonly') == 'false')
        wait.until(lambda driver: chat_input.get_attribute('aria-invalid') == 'false')
        print("Chat input is ready to accept inputs")
        return chat_input

    def enter_message_in_chatbox(self, message):
        """Enter message in chatbox after waiting for it to be ready"""
        print("Entering message in chatbox", message)
        self.key_clear_and_type(self.CHAT_INPUT, message)
    
    def click_send_button(self):
        """Click the send button to submit the message"""
        print("Clicking on Send button")
        self.click_element(By.XPATH, self.SEND_BUTTON)

    def get_full_assistant_reply(self, timeout=60, wait_stability=2, cleanReply=True):
        """
        Waits for the full assistant reply to load completely using XPath and returns the text.
        :param driver: Selenium WebDriver instance.
        :param timeout: Maximum wait time for response to start.
        :param wait_stability: Time to wait with no DOM changes (indicates stability).
        :return: Full reply as a string.
        """
        WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((By.XPATH, self.REPLY_XPATH))
        )

        previous_text = ""
        stable_count = 0

        for _ in range(timeout):
            elements = self.driver.find_elements(By.XPATH, self.REPLY_XPATH)
            # Get the last non-empty response element
            current_text = ""
            if elements:
                # Iterate from last to first to find the last non-empty element
                for element in reversed(elements):
                    text = element.text.strip()
                    if text:  # Found non-empty text
                        current_text = text
                        break

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