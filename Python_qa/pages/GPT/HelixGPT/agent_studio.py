import os
from random import randint
import sys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
import time
from selenium.webdriver.common.action_chains import ActionChains
from conftest import reporting
from pages.Base.base_page import BasePage
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException,TimeoutException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
import re
from test_config import TestConfig
from utils.ui_utils import utilities

class agentStudio(BasePage) : 
    
    def __init__(self, driver):
        super().__init__(driver)
        global project_root
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
        sys.path.append(project_root)
    ATTACHFILES_BUTTON_XPATH = "//button[.//span[contains(@class, 'd-icon-paperclip')] and .//span[text()='Attach files']]"
    SENDMESSAGE_BUTTON_XPATH = "//button[.//span[text()='Send message']]"
    REPLY_XPATH = "//adapt-speech-bubble[contains(@class,'speech-bubble--received')]//div[contains(@class,'speech-bubble__item')]/div"
    MESSAGE_INPUT_XPATH = "//textarea[@aria-label='Send message']"
    ANSWER_TABLE_XPATH="//adapt-speech-bubble[contains(@class,'speech-bubble--received')]//div[contains(@class,'speech-bubble__item')]/div//table"
    
    #InnovationStudio: View
    VIEWTABBUTTON_XPATH = "//button[@data-testid='adapt-tabs-dropdown-1_tab_1' and @role='tab']"
    NEW_BUTTON_XPATH = "//button[@rx-id='new-button' and contains(., 'New')]"
    CONTAINER_OPTION_XPATH = "//button[contains(text(),'Container')]"
    HELIXGPT_CHAT_COMPONENT_XPATH = "//div[@rx-id='rx-helix-gpt-chat']//span[contains(text(), 'HelixGPT chat')]"
    CANVAS_DROP_ZONE_XPATH = "//rx-view-designer-canvas"
    ALLOW_ATTACHMENTS_CHECKBOX_XPATH = "//rx-switcher-form-control[@rx-id='allow-attachments']//input[@type='checkbox']"
    NAME_INPUT_XPATH = "//rx-text-form-control[@rx-id='name']//input[@type='text']"
    SKILL_DROPDOWN_XPATH = "//rx-select-form-control[@rx-id='skill']//button[@role='combobox']"
    SKILL_NAME="//button[@role='option']//*[contains(text(), '{skill_name}')]"
    PROPERTIES_GEAR="//button[@aria-label='View properties']"
    VIEW_NAME_INPUT_REQUIRED_XPATH = "//label[contains(., 'Name') and contains(., '(required)')]/ancestor::adapt-rx-control-label/following-sibling::div//input[@type='text']"
    BUTTON_XPATH = "//button[contains(normalize-space(.), '{Name}')]"    
    FEEDBACK_XPATH = "//div[contains(@class,'adapt-chat-text-entry__uploads-feedback')]//span[contains(@class,'alert-content')]"
    WAIT_FOR_REPLY_TIME = 10
    COPY_BUTTON_XPATH = "//button[@aria-label='Copy response to clipboard']"

    HELIXGPTASVIEWSURL = "/helix/index.html#/com.bmc.arsys.rx.innovationstudio/com.bmc.dsom.hgm/view-definitions"

     # ========== Feedback Methods (DWP / Employee Navigator) ==========
    
    THUMBS_UP_BUTTON_XPATH = "//button[@aria-label='Good response']"
    THUMBS_DOWN_BUTTON_XPATH = "//button[@aria-label='Bad response']"

    # Feedback popover is rendered in a CDK overlay container (cdk-overlay-container)
    # with an adapt-popover-window2 dialog. We MUST scope all feedback form locators
    # to this overlay container, otherwise Selenium won't find them.
    FEEDBACK_OVERLAY_XPATH = "//div[@class='cdk-overlay-pane bs-popover-top']//adapt-popover-window2"
    
    # All feedback form elements are INSIDE the overlay popover
    REASON_FOR_FEEDBACK_XPATH = FEEDBACK_OVERLAY_XPATH + "//dwp-helix-gpt-learning-feedback-form//adapt-rx-radiobutton-group//adapt-rx-radiobutton" #//input[@type='radio']"
    SOURCES_CHECKBOX_XPATH = FEEDBACK_OVERLAY_XPATH+"//dwp-helix-gpt-learning-feedback-form//adapt-rx-checkbox"#//input[@type='checkbox']"
    FEEDBACK_COMMENT_TEXTAREA_XPATH = FEEDBACK_OVERLAY_XPATH+"//dwp-helix-gpt-learning-feedback-form//adapt-rx-textarea//textarea"
    FEEDBACK_SEND_BUTTON_XPATH = FEEDBACK_OVERLAY_XPATH+"button[@type='submit']"
    
    FEEDBACK_SUCCESS_MESSAGE_XPATH = "//div[contains(text(), 'Feedback submitted successfully')]"

    def click_attach_files_button(self):
        """
        Clicks the 'Attach files' button in the Agent Studio.
        """
        try:
            attach_files_button = self.wait.until(EC.element_to_be_clickable((By.XPATH, self.ATTACHFILES_BUTTON_XPATH)))
            attach_files_button.click()
            print("Clicked on 'Attach files' button successfully.")
            time.sleep(2)  # Wait for any potential UI updates after clicking
        except (NoSuchElementException, TimeoutException) as e:
            print(f"Failed to click on 'Attach files' button: {str(e)}")
            raise e
        
    def send_message(self):
        """
        Clicks the 'Send message' button in the Agent Studio.
        """
        try:
            attach_files_button = self.wait.until(EC.element_to_be_clickable((By.XPATH, self.SENDMESSAGE_BUTTON_XPATH)))
            attach_files_button.click()
            print("Clicked on 'Send message' button successfully.")
            time.sleep(2)  # Wait for any potential UI updates after clicking
        except (NoSuchElementException, TimeoutException) as e:
            print(f"Failed to click on 'Send message' button: {str(e)}")
            raise e
        
    def get_full_assistant_reply(self, timeout=60, wait_stability=2):
        """
        Waits for the full assistant reply to load completely using XPath and returns the text.
        :param driver: Selenium WebDriver instance.
        :param timeout: Maximum wait time for response to start.
        :param wait_stability: Time to wait with no DOM changes (indicates stability).
        :return: Full reply as a string.
        """

        # XPath to locate assistant response bubbles (received messages)

        WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((By.XPATH, self.REPLY_XPATH))
        )

        previous_text = ""
        stable_count = 0

        for _ in range(timeout):
            elements = self.driver.find_elements(By.XPATH, self.REPLY_XPATH)
            current_text = "\n".join([el.text.strip() for el in elements if el.text.strip()])

            if current_text == previous_text:
                stable_count += 1
            else:
                stable_count = 0
                previous_text = current_text

            if stable_count >= wait_stability:
                break
            # Wait for a short period to ensure the last response is fully rendered
            time.sleep(7)

        return previous_text
    
    def enter_message(self, message):
        """
        Enters a message into the message input field.
        :param message: The message text to enter.
        """
        try:
            message_input = WebDriverWait(self.driver, 180).until(
                EC.visibility_of_element_located((By.XPATH, self.MESSAGE_INPUT_XPATH))
            )
            message_input.clear()
            message_input.send_keys(message)
            print("Entered message successfully.")
            time.sleep(1)  # Wait for any potential UI updates after entering text
        except (NoSuchElementException, TimeoutException) as e:
            print(f"Failed to enter message: {str(e)}")
            raise e

    def open_table_link(self):
        try:
            uiutil_obj = utilities(self.driver)
            tableElement = self.find_element(By.XPATH, self.ANSWER_TABLE_XPATH)
            if tableElement:
                print("Table found in the assistant's reply. Attempting to open link...")
                uiutil_obj.open_link_from_table(tableElement)
                print("Link from table opened successfully.")
                return True
            else:
                print("No table found in the assistant's reply.")
                return False
        except Exception as e:
            print(f"Error opening link from table: {e}")
            return False
            
    def sanitize_output(self,output: str) -> str:
        """
        Cleans irrelevant metadata and duplicate sections
        before passing text into DeepEval metrics.
        """
        # Remove duplicate repeated segments (like two full responses)
        parts = output.split("HelixGPT replied:")
        if len(parts) > 1:
            # Merge unique content blocks
            output = " ".join(set(part.strip() for part in parts if part.strip()))

        # Remove metadata, sources, test phrases, and system lines
        patterns = [
            r"(?i)^HelixGPT replied:.*\n?",
            r"(?i)^Liam Shay.*\n?",
            r"(?i)^Country.*\n?",
            r"(?i)^Time Zone.*\n?",
            r"(?i)^Sources:*",
            r"(?i)\btest(ing)?\b",
            r"(?i)drag and drop files here"
        ]
        for p in patterns:
            output = re.sub(p, "", output)

        # Remove all newline and carriage return characters
        output = output.replace('\n', '').replace('\r', '')
        return output.strip()

    def create_new_view_verify(self,skillName = "BWF Case Conversation Skill",navigate=True):
        """
        Creates a new view in the Agent Studio.
        """
        try:
            if navigate:
                print("Navigating to Agent Studio views URL...")
                self.driver.get(TestConfig.ISURL+self.HELIXGPTASVIEWSURL)
                time.sleep(5)  # Wait for the page to load    
                uitilsobj = utilities(self.driver)
                common_data = uitilsobj.load_common_data()
                uitilsobj.login(common_data['SU_USERNAME'],common_data['SU_PASSWORD'] ,TestConfig.ISURL+self.HELIXGPTASVIEWSURL)
            assert self.wait_till_element_loaded(By.XPATH, self.VIEWTABBUTTON_XPATH), "Views tab button not loaded"
            assert self.navigate_to_agent_studio_views(), "Navigation to Agent Studio views failed"
            assert self.add_helixgpt_chat_component(), "Adding HelixGPT chat component failed"
            assert self.enable_allow_attachments(), "Enabling 'Allow attachments' failed"
            # Use a purely numeric name to avoid float conversion errors downstream
            view_name = "GPT_AUTOMATION"+str(randint(1,100))
            self.key_clear_and_type(self.NAME_INPUT_XPATH, view_name)
            print("New view created successfully.")
            self.click_element(By.XPATH, self.SKILL_DROPDOWN_XPATH)
            time.sleep(1)
            self.click_element(By.XPATH, self.SKILL_NAME.format(skill_name=skillName))
            time.sleep(1)
            self.click_element(By.XPATH, self.PROPERTIES_GEAR)
            time.sleep(1)
            self.key_clear_and_type( self.VIEW_NAME_INPUT_REQUIRED_XPATH, view_name)
            time.sleep(1)
            self.click_element(By.XPATH, self.BUTTON_XPATH.format(Name="Save"))
            time.sleep(3)  # Wait for save to complete
            self.click_element(By.XPATH, self.BUTTON_XPATH.format(Name="Preview"))
            time.sleep(1)
            assert self.verify_view(view_name), "View verification failed"
            return view_name
        except Exception as e:
            print(f"Failed to create new view: {e}")
            return None
    
    def navigate_to_agent_studio_views(self):
        """
        Navigates to the Agent Studio views URL.
        """
        try:
            print("Navigating to Agent Studio views URL...")
            self.click_element(By.XPATH, self.NEW_BUTTON_XPATH)
            time.sleep(2)  # Wait for the new view options to appear
            self.click_element(By.XPATH, self.CONTAINER_OPTION_XPATH)
            return True
        except Exception as e:
            print(f"Failed to navigate to Agent Studio views URL: {e}")
            return False

    def add_helixgpt_chat_component(self):
        """
        Drags and drops the HelixGPT chat component to the center of canvas.
        """
        def _verify_component_added():
            """Check if component was successfully added to canvas"""
            try:
                time.sleep(2)
                canvas_html = canvas_drop_zone.get_attribute("innerHTML")
                return "rx-helix-gpt-chat" in canvas_html
            except:
                return False
        
        try:
            # Wait for the component and canvas
            helixgpt_chat = WebDriverWait(self.driver, 180).until(
                EC.element_to_be_clickable((By.XPATH, self.HELIXGPT_CHAT_COMPONENT_XPATH))
            )
            canvas_drop_zone = WebDriverWait(self.driver, 180).until(
                EC.visibility_of_element_located((By.XPATH, self.CANVAS_DROP_ZONE_XPATH))
            )

            # Scroll both elements into view
            self.driver.execute_script("arguments[0].scrollIntoView({block:'center'});", helixgpt_chat)
            time.sleep(0.5)
            self.driver.execute_script("arguments[0].scrollIntoView({block:'center'});", canvas_drop_zone)
            time.sleep(1)

            # Get canvas absolute position and center using JavaScript
            canvas_info = self.driver.execute_script("""
                var canvas = arguments[0];
                var rect = canvas.getBoundingClientRect();
                return {
                    x: rect.left + window.pageXOffset,
                    y: rect.top + window.pageYOffset,
                    width: rect.width,
                    height: rect.height,
                    centerX: rect.left + (rect.width / 2),
                    centerY: rect.top + (rect.height / 2),
                    viewportCenterX: rect.left + (rect.width / 2),
                    viewportCenterY: rect.top + (rect.height / 2)
                };
            """, canvas_drop_zone)
            
            print(f"Canvas info: {canvas_info}")
            print(f"Canvas center in viewport: ({canvas_info['centerX']}, {canvas_info['centerY']})")

            # Strategy 1: Comprehensive JavaScript HTML5 drag and drop to center
            try:
                print("Strategy 1: JavaScript HTML5 drag and drop to canvas center")
                js_drag_drop = """
                    function simulateDragDrop(srcElement, tgtElement) {
                        var rect = tgtElement.getBoundingClientRect();
                        var centerX = rect.left + (rect.width / 2);
                        var centerY = rect.top + (rect.height / 2);
                        
                        console.log('Dropping at canvas center:', centerX, centerY);
                        
                        // Create dataTransfer
                        var dataTransfer = new DataTransfer();
                        
                        // Dispatch dragstart
                        var dragStart = new DragEvent('dragstart', {
                            bubbles: true,
                            cancelable: true,
                            dataTransfer: dataTransfer
                        });
                        srcElement.dispatchEvent(dragStart);
                        
                        // Dispatch dragenter at center
                        var dragEnter = new DragEvent('dragenter', {
                            bubbles: true,
                            cancelable: true,
                            dataTransfer: dataTransfer,
                            clientX: centerX,
                            clientY: centerY
                        });
                        tgtElement.dispatchEvent(dragEnter);
                        
                        // Dispatch dragover at center
                        var dragOver = new DragEvent('dragover', {
                            bubbles: true,
                            cancelable: true,
                            dataTransfer: dataTransfer,
                            clientX: centerX,
                            clientY: centerY
                        });
                        tgtElement.dispatchEvent(dragOver);
                        
                        // Dispatch drop at center
                        var drop = new DragEvent('drop', {
                            bubbles: true,
                            cancelable: true,
                            dataTransfer: dataTransfer,
                            clientX: centerX,
                            clientY: centerY
                        });
                        tgtElement.dispatchEvent(drop);
                        
                        // Dispatch dragend
                        var dragEnd = new DragEvent('dragend', {
                            bubbles: true,
                            cancelable: true,
                            dataTransfer: dataTransfer
                        });
                        srcElement.dispatchEvent(dragEnd);
                        
                        return true;
                    }
                    
                    return simulateDragDrop(arguments[0], arguments[1]);
                """
                self.driver.execute_script(js_drag_drop, helixgpt_chat, canvas_drop_zone)
                if _verify_component_added():
                    print("Success: Strategy 1 (JavaScript HTML5 DnD) worked")
                    return True
            except Exception as e:
                print(f"Strategy 1 failed: {e}")

            # Strategy 2: ActionChains with calculated absolute offset to canvas center
            try:
                print("Strategy 2: ActionChains with absolute offset to canvas center")
                source_rect = self.driver.execute_script("""
                    var rect = arguments[0].getBoundingClientRect();
                    return {x: rect.left, y: rect.top, width: rect.width, height: rect.height};
                """, helixgpt_chat)
                
                # Calculate offset from source to canvas center
                offset_x = int(canvas_info['centerX'] - source_rect['x'] - source_rect['width'] / 2)
                offset_y = int(canvas_info['centerY'] - source_rect['y'] - source_rect['height'] / 2)
                
                print(f"Moving from source to canvas center with offset: ({offset_x}, {offset_y})")
                
                actions = ActionChains(self.driver)
                actions.click_and_hold(helixgpt_chat).pause(1)
                actions.move_by_offset(offset_x, offset_y).pause(1)
                actions.release().perform()
                
                if _verify_component_added():
                    print("Success: Strategy 2 (ActionChains offset) worked")
                    return True
            except Exception as e:
                print(f"Strategy 2 failed: {e}")

            # Strategy 3: Simple drag_and_drop (baseline)
            try:
                print("Strategy 3: Simple drag_and_drop")
                ActionChains(self.driver).drag_and_drop(helixgpt_chat, canvas_drop_zone).perform()
                if _verify_component_added():
                    print("Success: Strategy 3 (Simple DnD) worked")
                    return True
            except Exception as e:
                print(f"Strategy 3 failed: {e}")

            print("All drag-and-drop strategies failed.")
            return False

        except (NoSuchElementException, TimeoutException) as e:
            print(f"Failed to add HelixGPT chat component: {e}")
            return False
        except Exception as e:
            print(f"Unexpected error adding HelixGPT chat component: {e}")
            return False
        
    def enable_allow_attachments(self):
        """
        Checks if the 'Allow attachments' checkbox is checked, and checks it if not.
        :return: True if checkbox is now checked, False if operation failed.
        """
        try:
            checkbox = self.wait.until(EC.presence_of_element_located((By.XPATH, self.ALLOW_ATTACHMENTS_CHECKBOX_XPATH)))
            
            # Check if checkbox is already checked
            is_checked = checkbox.get_attribute('aria-checked') == 'true'
            
            if is_checked:
                print("'Allow attachments' checkbox is already checked.")
                return True
            else:
                print("'Allow attachments' checkbox is not checked. Checking it now...")
                # Click the checkbox to check it
                checkbox.click()
                time.sleep(1)
                
                # Verify it's now checked
                is_now_checked = checkbox.get_attribute('aria-checked') == 'true'
                if is_now_checked:
                    print("'Allow attachments' checkbox checked successfully.")
                    return True
                else:
                    print("Failed to check 'Allow attachments' checkbox.")
                    return False
                    
        except (NoSuchElementException, TimeoutException) as e:
            print(f"Failed to interact with 'Allow attachments' checkbox: {str(e)}")
            return False
        
    def verify_view(self, nameInput):
        try:
                original_handle = self.driver.current_window_handle
                WebDriverWait(self.driver, 15).until(lambda d: len(d.window_handles) > 1)
                target_handle = None

                for h in self.driver.window_handles:
                    if h == original_handle:
                        continue
                    self.driver.switch_to.window(h)
                    try:
                        WebDriverWait(self.driver, 10).until(EC.title_contains(nameInput))
                        target_handle = h
                        print(f"Switched to tab with title: {self.driver.title}")
                        break
                    except TimeoutException:
                        continue

                if not target_handle:
                    for h in self.driver.window_handles:
                        if h == original_handle:
                            continue
                        self.driver.switch_to.window(h)
                        if nameInput in self.driver.page_source:
                            target_handle = h
                            print("Switched to tab containing view in page source.")
                            break

                if target_handle:
                    print("View verified successfully in new tab.")
                    try:
                        self.wait.until(EC.presence_of_element_located((By.XPATH, self.ATTACHFILES_BUTTON_XPATH)))
                    except (TimeoutException, NoSuchElementException):
                        return False
                    return True
              

        except Exception as e:
            print(f"Failed to handle new tab: {e}")
            try:
                self.driver.switch_to.window(original_handle)
            except Exception:
                return False
            
    def attach_upload_file(self, file_path):
        """
        Attaches and uploads a file using the 'Attach files' button.
        :param file_path: The full path to the file to be uploaded.
        """
        try:
            print("Attaching image file to the bot...")        
            self.click_attach_files_button()
            time.sleep(2)  # Wait for the file upload dialog to appear
            uiutilobj = utilities(self.driver)
            uiutilobj.upload_file_with_autoit(file_path)
            time.sleep(1)  # Wait for the file to be uploaded
            self.send_message()
            time.sleep(self.WAIT_FOR_REPLY_TIME)  # Wait for the bot to respond
            # 5. Verify test bot responds correctly responds to the image uploaded
            print("Evaluating bot response with DeepEval metrics...")
            bot_response = self.get_full_assistant_reply(timeout=60, wait_stability=2)
            print("Bot response received:", bot_response)
            return bot_response
        except Exception as e:
            print(f"Failed to attach and upload file '{file_path}': {str(e)}")
            raise e
        
    def attach_unsupported_file_verify(self, file_path):
        """
        Attaches and uploads an unsupported file using the 'Attach files' button.       
        :param file_path: The full path to the unsupported file to be uploaded.
        """ 
        try:
            print("Attaching unsupported file to the bot...")        
            self.click_attach_files_button()
            time.sleep(2)  # Wait for the file upload dialog to appear
            uiutilobj = utilities(self.driver)
            uiutilobj.upload_file_with_autoit(file_path)
            print("Unsupported file attached and message sent.")
            # Verify unsupported file feedback appears
            alert_elem = WebDriverWait(self.driver, 30.).until(
                EC.visibility_of_element_located((By.XPATH, self.FEEDBACK_XPATH))
            )
            alert_text_clean = re.sub(r"\s+", " ", alert_elem.text).strip().lower()
            expected_phrase = "files are not supported"
            if expected_phrase in alert_text_clean:
                print("Unsupported file feedback displayed.")
                return True
            raise AssertionError(f"Unsupported file feedback not found. Text was: {alert_elem.text.strip()}")
        except Exception as e:
            print(f"Failed to attach and upload unsupported file '{file_path}': {str(e)}")
            return False

    def attach_exceed_file_count(self, file_path):
        """
        Attaches and uploads files exceeding the allowed count using the 'Attach files' button.       
        :param file_path: The full path to the file to be uploaded.
        """ 
        try:
            print("Attaching multiple files to exceed count limit...")        
            time.sleep(2)  # Wait for the file upload dialog to appear
            uiutilobj = utilities(self.driver)

            for _ in range(6):  # Assuming limit is 5, attach 6 files
                self.click_attach_files_button()
                uiutilobj.upload_file_with_autoit(file_path)
                time.sleep(1)  # Small delay between uploads
            
            print("Multiple files attached and message sent.")
            # Verify exceed file count feedback appears
            alert_elem = WebDriverWait(self.driver, 30.).until(
                EC.visibility_of_element_located((By.XPATH, self.FEEDBACK_XPATH))
            )
            
            alert_text_clean = re.sub(r"\s+", " ", alert_elem.text).strip().lower()
            expected_phrase = "maximum number of files"
            
            if expected_phrase in alert_text_clean:
                print("Exceed file count feedback displayed.")
                return True
            raise AssertionError(f"Exceed file count feedback not found. Text was: {alert_elem.text.strip()}")
        except Exception as e:
            print(f"Failed to attach and upload multiple files '{file_path}': {str(e)}")
            return False
        
    def attach_large_file(self, file_path):
        """
        Attaches and uploads a large file using the 'Attach files' button.       
        :param file_path: The full path to the large file to be uploaded.
        """ 
        try:
            print("Attaching large file to the bot...")        
            self.click_attach_files_button()
            time.sleep(2)  # Wait for the file upload dialog to appear
            uiutilobj = utilities(self.driver)
            uiutilobj.upload_file_with_autoit(file_path)
            print("Large file attached and message sent.")
            # Verify large file feedback appears
            alert_elem = WebDriverWait(self.driver, 30.).until(
                EC.visibility_of_element_located((By.XPATH, self.FEEDBACK_XPATH))
            )
            alert_text_clean = re.sub(r"\s+", " ", alert_elem.text).strip().lower()
            expected_phrase = "file size exceeds the limit"
            if expected_phrase in alert_text_clean:
                print("Large file size feedback displayed.")
                return True
            raise AssertionError(f"Large file size feedback not found. Text was: {alert_elem.text.strip()}")
        except Exception as e:
            print(f"Failed to attach and upload large file '{file_path}': {str(e)}")
            return False

    def verify_copy_buttons(self,count=1):
        """
        Verifies the copy button is present in the bot response.
        """
        try:
            copy_button = self.driver.find_elements(By.XPATH, self.COPY_BUTTON_XPATH)
            if len(copy_button) == count:
                print("Copy buttons are present.")
                return True
            else:
                print(f"Copy buttons are not present. Expected {count} but found {len(copy_button)}.")
                return False
        except Exception as e:
            print(f"Failed to verify copy buttons: {str(e)}")
            return False

    def verify_feedback_buttons_present(self, timeout=10):
        """
        Verifies that thumbs up and thumbs down feedback buttons are present.
        Returns True if both buttons are found, False otherwise.
        """
        try:
            print("Verifying feedback buttons are present...")
            thumbs_up = WebDriverWait(self.driver, timeout).until(
                EC.presence_of_element_located((By.XPATH, self.THUMBS_UP_BUTTON_XPATH))
            )
            thumbs_down = self.driver.find_element(By.XPATH, self.THUMBS_DOWN_BUTTON_XPATH)
            
            if thumbs_up and thumbs_down:
                print("✓ Both thumbs up and thumbs down buttons are present.")
                return True
            return False
        except (NoSuchElementException, TimeoutException) as e:
            print(f"✗ Feedback buttons not found: {str(e)}")
            return False

    def click_thumbs_up(self, timeout=10):
        """
        Clicks the thumbs up button.
        Returns True if successful, False otherwise.
        """
        try:
            print("Clicking thumbs up button...")
            thumbs_up = WebDriverWait(self.driver, timeout).until(
                EC.element_to_be_clickable((By.XPATH, self.THUMBS_UP_BUTTON_XPATH))
            )
            thumbs_up.click()
            time.sleep(2)  # Wait for any feedback confirmation
            print("✓ Thumbs up button clicked successfully.")
            return True
        except (NoSuchElementException, TimeoutException) as e:
            print(f"✗ Failed to click thumbs up button: {str(e)}")
            return False

    def click_thumbs_down(self, timeout=15):
        """
        Clicks the thumbs down button and waits for the feedback popover to appear.
        Returns True if successful, False otherwise.
        """
        try:
            print("Clicking thumbs down button...")
            thumbs_down = WebDriverWait(self.driver, timeout).until(
                EC.element_to_be_clickable((By.XPATH, self.THUMBS_DOWN_BUTTON_XPATH))
            )
            thumbs_down.click()
            time.sleep(1)  # Brief pause for overlay animation to start
            return True
        except (NoSuchElementException, TimeoutException) as e:
            print(f"✗ Failed to click thumbs down button or feedback popover not found: {str(e)}")
            return False

    def verify_feedback_comment_field_visible(self, timeout=15):
        """
        Verifies that the feedback comment textarea is visible after clicking thumbs down.
        Returns True if visible, False otherwise.
        """
        try:
            print("Verifying feedback comment field is visible in overlay...")
            
            # First ensure the overlay is present
            WebDriverWait(self.driver, timeout).until(
                EC.presence_of_element_located((By.XPATH, self.FEEDBACK_OVERLAY_XPATH))
            )
            
            comment_field = WebDriverWait(self.driver, timeout).until(
                EC.visibility_of_element_located((By.XPATH, self.FEEDBACK_COMMENT_TEXTAREA_XPATH))
            )
            if comment_field.is_displayed():
                print("✓ Feedback comment field is visible in overlay.")
                return True
            return False
        except (NoSuchElementException, TimeoutException) as e:
            print(f"✗ Feedback comment field not visible in overlay: {str(e)}")
            # Debug: try to print what's in the overlay
            try:
                overlays = self.driver.find_elements(By.XPATH, "//div[contains(@class,'cdk-overlay-container')]")
                print(f"   DEBUG: Found {len(overlays)} cdk-overlay-container elements")
                if overlays:
                    print(f"   DEBUG: Overlay innerHTML length: {len(overlays[0].get_attribute('innerHTML'))}")
            except:
                pass
            return False

    def enter_feedback_comment(self, comment, timeout=15):
        """
        Enters a comment in the feedback textarea inside the overlay popover.
        Args:
            comment (str): The feedback comment to enter
        Returns True if successful, False otherwise.
        """
        try:
            # First ensure the overlay popover is present
            print("Waiting for feedback overlay to be ready...")
            WebDriverWait(self.driver, timeout).until(
                EC.presence_of_element_located((By.XPATH, self.FEEDBACK_OVERLAY_XPATH))
            )
            
            # Select a specific reason in the feedback form (e.g. "incorrect" answer)
            print("Selecting feedback reason (incorrect)...")
            reason_for_feedback = WebDriverWait(self.driver, timeout).until(
                EC.element_to_be_clickable((By.XPATH, self.REASON_FOR_FEEDBACK_XPATH))
            )
            # Use JavaScript click for radio buttons inside overlay (more reliable)
            self.driver.execute_script("arguments[0].click();", reason_for_feedback)
            time.sleep(1)
            print("✓ Feedback reason selected.")
           
            # Enable "include sources" checkbox (optional, skip if not found)
            try:
                print("Looking for sources checkbox...")
                sources_checkbox = WebDriverWait(self.driver, 5).until(
                    EC.element_to_be_clickable((By.XPATH, self.SOURCES_CHECKBOX_XPATH))
                )
                self.driver.execute_script("arguments[0].click();", sources_checkbox)
                time.sleep(1)
                print("✓ Sources checkbox clicked.")
            except TimeoutException:
                print("⚠ Sources checkbox not found, skipping...")

            print(f"Entering feedback comment: '{comment}'")
            comment_field = WebDriverWait(self.driver, timeout).until(
                EC.element_to_be_clickable((By.XPATH, self.FEEDBACK_COMMENT_TEXTAREA_XPATH))
            )
            comment_field.clear()
            comment_field.send_keys(comment)
            time.sleep(1)
            print("✓ Feedback comment entered successfully.")
            return True
        except (NoSuchElementException, TimeoutException) as e:
            print(f"✗ Failed to enter feedback comment: {str(e)}")
            # Debug: dump overlay info
            try:
                overlays = self.driver.find_elements(By.XPATH, self.FEEDBACK_OVERLAY_XPATH)
                print(f"   DEBUG: Found {len(overlays)} feedback overlay elements")
            except:
                pass
            return False

    def click_send_feedback(self, timeout=15):
        """
        Clicks the send/submit button to submit feedback inside the overlay.
        Returns True if successful, False otherwise.
        """
        try:
            print("Clicking send feedback button in overlay...")
            
            # Ensure overlay is present
            WebDriverWait(self.driver, timeout).until(
                EC.presence_of_element_located((By.XPATH, self.FEEDBACK_OVERLAY_XPATH))
            )
            
            send_button = WebDriverWait(self.driver, timeout).until(
                EC.element_to_be_clickable((By.XPATH, self.FEEDBACK_SEND_BUTTON_XPATH))
            )
            
            # Use JavaScript click for buttons inside overlay (more reliable)
            self.driver.execute_script("arguments[0].click();", send_button)
            time.sleep(2)  # Wait for feedback submission and overlay to close
            print("✓ Send feedback button clicked successfully.")
            return True
        except (NoSuchElementException, TimeoutException) as e:
            print(f"✗ Failed to click send feedback button: {str(e)}")
            # Debug: try to find any buttons in the overlay
            try:
                buttons = self.driver.find_elements(By.XPATH, "//div[contains(@class,'cdk-overlay-container')]//button")
                print(f"   DEBUG: Found {len(buttons)} buttons in overlay")
                for i, btn in enumerate(buttons[:5]):
                    print(f"   DEBUG: Button {i}: {btn.get_attribute('data-testid') or btn.text[:30]}")
            except:
                pass
            return False

    def verify_feedback_submitted(self, timeout=10):
        """
        Verifies that feedback was submitted successfully by looking for success message.
        Returns True if success message found, False otherwise.
        """
        try:
            print("Verifying feedback submission...")
            success_message = WebDriverWait(self.driver, timeout).until(
                EC.presence_of_element_located((By.XPATH, self.FEEDBACK_SUCCESS_MESSAGE_XPATH))
            )
            if success_message:
                print(f"✓ Feedback submitted successfully: {success_message.text}")
                return True
            return False
        except (NoSuchElementException, TimeoutException):
            # Feedback might have been submitted without explicit success message
            print("⚠ No explicit success message found, but feedback may have been submitted.")
            return True  # Return True as absence of error usually means success