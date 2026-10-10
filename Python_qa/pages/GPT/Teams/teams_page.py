from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time
from pages.Base.base_page import BasePage
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains
import autoit
from test_config import TestConfig

class teams_page(BasePage):
        
        def __init__(self, driver):
            super().__init__(driver)
            
        # =============================
        # Login Page Locators
        # =============================
        USERNAME_INPUT = "//input[@name='loginfmt']"
        NEXT_BUTTON = "idSIButton9"
        PASSWORD_INPUT = "//input[@name='passwd']"
        SIGN_IN_BUTTON = "idSIButton9"  # Reused for sign-in after password
        STAY_SIGNED_IN_YES = "idSIButton9" # Reused again for stay signed in
        STAY_SIGNED_IN_NO = "//*[@id='idBtn_Back']"

        # =============================
        # Chat Locators
        # =============================
        AUTOCOMPLETEPATH = "//div[contains(@class, 'autocompleteItem__header')]//b[text()='{}']"
        AUTOCOMPLETE_PICKER_ITEM_XPATH = "//li[@data-tid='autocomplete-picker-item-{}']"

        TEAMS_SEARCH="//input[@id='ms-searchux-input']"
        # Full container of message body
        CHAT_MESSAGE_CONTAINER ="//div[@id][starts-with(@id,'new-message-')]"
        FIND_CHAT="//div[@data-tid=\"chat-pane-item\"]/div/div[contains(text(),'text')]"
        # All tables in latest message
        MESSAGE_TABLES = "//div[@id][starts-with(@id,'content-')]//table"
        GROUP_TAB="//button[@data-tid='groupchats-tab']"
        CANT_FIND_TEXT="//*[contains(text(),\"We couldn't find any results for\")]"
        # First table: Incident Details
        INCIDENT_TABLE = "//div[@id][starts-with(@id,'content-')]//table[1]"
        INCIDENT_HEADERS = "//div[@id][starts-with(@id,'content-')]//table[1]//thead/tr/th"
        INCIDENT_ROW = "//div[@id][starts-with(@id,'content')]//table[1]/tbody/tr"
        INCIDENT_CELLS = "//div[@id][starts-with(@id,'content')]//table[1]/tbody/tr/td"

        # Second table: Work Logs
        WORKLOG_TABLE = "//div[@id][starts-with(@id,\"content-\")]//table)[2]"
        WORKLOG_HEADERS ="//div[@id][starts-with(@id,\"content-\")]//table)[2]//thead/tr/th"
        WORKLOG_ROW = "//div[@id][starts-with(@id,\"content-\")]//table)[2]//tbody/tr"
        WORKLOG_CELLS = "//div[@id][starts-with(@id,\"content-\")]//table)[2]//tbody/tr/td"

        # Optional: Message author & timestamp
        AUTHOR_MESSAGES = "//span[contains(@id,\"author\") and text()=\"SolQA_April_APP\"]/ancestor::div[@data-tid=\"chat-pane-item\"]"
        LAST_MESSAGE_TIMESTAMP = "//time[contains(@id,\"timestamp\")])[last()]"
        LAST_MESSAGE_BODY = "(//div[@id][starts-with(@id,\"content-\")])[last()]"
        USE_ANOTHER_XPATH = "//div[contains(text(),'Use another account')]"
        USE_WEB_XPATH = "//button[@id='openTeamsClientInBrowser']"
        MESSAGE_BLOCK="//div[@data-tid='chat-pane-message']"
        CHAT_MESSAGE=".//div[@id[contains(., 'content-')]]"
        INCIDENT_CHAT_XPATH = "//li[@role='group']//span[contains(text(),'{}')]"

        #Dev Portal Info
        DEV_PORTAL_URL = f"https://dev.teams.microsoft.com/tools/bots/{TestConfig.SolQA_Bot2_ID}/configure"
        ENDPOINT_XPATH="//input[@id='field-r7__control']"
        SAVE_BUTTON="//button[normalize-space(text())='Save']"
        
        def handle_open_teams_alert(self):
            try:
                time.sleep(3)
                print("Wait for alert and dismiss it")
                #WebDriverWait(self.driver, 5).until(EC.alert_is_present())
                alert = self.driver.switch_to.alert
                print("Alert Text:", alert.text)
                alert.dismiss() 
                print("Alert dismissed.")
            except:
                try:
                    window_title = "Join conversation - Google Chrome"
                    # Wait for window to appear
                    if autoit.win_wait(window_title, 10) == 1:
                        print("Popup detected.")

                        # Activate the window
                        autoit.win_activate(window_title)
                        time.sleep(1)
                        
                        # Send {ESC} key
                        autoit.send("{ESC}")
                        print("Escape key sent.")
                    else:
                        print("Popup window not found.")
                except Exception as inner_e:
                     print("Caught an error inside handle_open_teams_alert block:", inner_e)
        
        def click_use_web_instead(self,mainWindow):
            try:
                for handle in self.driver.window_handles:
                    if handle != mainWindow:
                        self.driver.switch_to.window(handle)
                        time.sleep(1)  
                        print("Check condition to confirm it's teams window")
                        try:
                            if "Join conversation" in self.driver.title or \
                            self.driver.find_element(By.XPATH, self.USE_WEB_XPATH) :
                                print("Switched to Teams window")
                                teamsWindow=self.driver.current_window_handle
                                break
                        except:
                            continue
                print("Clicked 'Use the web instead'")
                self.handle_open_teams_alert()
                useWebElement=self.driver.find_element(By.XPATH, self.USE_WEB_XPATH)
                useWebElement.click()
            except:
                print("'Use the web instead' not found.")

        def login_teams(self,email,password,mainWindow):
            try:
                print("Wait for new window to appear (max 10 seconds)")
                WebDriverWait(self.driver, 10).until(lambda d: len(d.window_handles) > 2)
                print("Switch to the new popup window")
                signupWindow=""
                for handle in self.driver.window_handles:
                    if handle != mainWindow:
                        self.driver.switch_to.window(handle)
                        print("Check condition to confirm it's signup window")
                        try:
                            # Example condition: title or specific element
                            if "Sign in to" in self.driver.title or \
                            self.driver.find_element(By.XPATH, self.USERNAME_INPUT):
                                print(" Switched to Sign-Up window")
                                signupWindow=self.driver.current_window_handle
                                break
                        except:
                            continue
                print("Switched to popup window.")
                print("Logging into Teams.")
                print("Entering username as {email} and password as {password}")
                self.driver.find_element(By.XPATH,self.USERNAME_INPUT).send_keys(email + Keys.RETURN)
                time.sleep(2)
                self.driver.find_element(By.XPATH,self.PASSWORD_INPUT).send_keys(password + Keys.RETURN)
                time.sleep(3)

                print("Handle \"Stay signed in?\" prompt")
                try:
                    print("Looking for Stay sign in page.")
                    self.driver.find_element(By.XPATH,self.STAY_SIGNED_IN_NO).click() 
                except:
                    pass  

                print("Wait for Teams UI to load")
                time.sleep(4)

                for handle in self.driver.window_handles:
                    if handle!=mainWindow and handle!=signupWindow:
                        teamsWindow =handle

                print("Switch back to teams window")
                self.driver.switch_to.window(teamsWindow)
            except Exception as e:
                print(f"Error clicking 'Use another account': {e}")

        def wait_till_teams_loads(self, timeout=600):
            """
            Waits until the Teams UI is fully loaded by checking for the presence of the search box.
            """
            try:
                
                print("Waiting for Teams to load...")
                WebDriverWait(self.driver, timeout).until(
                    EC.presence_of_element_located((By.XPATH, self.CHAT_MESSAGE_CONTAINER))
                )
                print("Teams UI loaded successfully.")
            except Exception as e:
                print(f"Error while waiting for Teams to load: {e}")

        def select_teams_chat(self , chat_name):
            print("Find the search box and perform search")
            try:
                
                # Wait for the search input to be present and clickable
                search_input = WebDriverWait(self.driver, 180).until(
                    EC.element_to_be_clickable((By.XPATH, self.TEAMS_SEARCH))
                )
                search_input.clear()
                search_input.send_keys(chat_name)
                time.sleep(1)
                search_input.send_keys(Keys.RETURN)
                time.sleep(3) #wait for search results to load
                print(f"Searched for: {chat_name}")
                # Wait for the group tab button to be present and clickable
                group_Tab_button = WebDriverWait(self.driver, 30).until(
                    EC.element_to_be_clickable((By.XPATH, self.GROUP_TAB))
                )
                print("Clicked on Group tab.")
                group_Tab_button.click()
                time.sleep(3) #wait for group tab to load
                # Check if "We couldn't find any results for" text is present
                # Wait up to 10 seconds for "We couldn't find any results for" text to appear
                try:
                    cant_find_elements = WebDriverWait(self.driver, 10).until(
                        lambda d: d.find_elements(By.XPATH, self.CANT_FIND_TEXT)
                    )
                except Exception:
                    cant_find_elements = []
                if cant_find_elements:
                    print(f"We couldn't find any results for '{chat_name}'. Please check the chat name.")
                    return

                # Make incident_chat locator a global class variable
                incident_chat = WebDriverWait(self.driver, 20).until(
                    EC.presence_of_element_located(
                        (By.XPATH, self.INCIDENT_CHAT_XPATH.format(chat_name))
                    )
                )
                print(f"Chat with '{chat_name}' found in Group tab.")
                incident_chat.click()
                time.sleep(2)
                print(f"Chat with '{chat_name}' selected.")
            except Exception as e:
                print("Error performing while selecting the chat:", e)

        def send_message_old(self, message_text, botName=None):
            try:
                print("Waiting for Teams chat input box to load...")
                WebDriverWait(self.driver, 600).until(
                    EC.presence_of_element_located((By.XPATH, self.CHAT_MESSAGE_CONTAINER))
                )

                WebDriverWait(self.driver, 600).until(
                    EC.element_to_be_clickable((By.XPATH, self.CHAT_MESSAGE_CONTAINER))
                )
                message_box = self.driver.find_element(By.XPATH, self.CHAT_MESSAGE_CONTAINER)
                message_box.click()
                message = ""
                time.sleep(2)
                if botName is not None:
                    message_box.send_keys('@' + botName)
                    time.sleep(2)
                    WebDriverWait(self.driver, 600).until(
                    EC.presence_of_element_located((By.XPATH, f"//div[contains(@class, 'autocompleteItem__header')]//b[text()='{botName}']"))
                )
                    self.driver.find_element(By.XPATH, f"//div[contains(@class, 'autocompleteItem__header')]//b[text()='{botName}']").click()
                    time.sleep(2)
                else:
                    message_box.send_keys(message_text)

                time.sleep(2)
                message=self.driver.find_element(By.XPATH, self.CHAT_MESSAGE_CONTAINER).text
                print(f"Message to send: {message}")
                self.driver.find_element(By.XPATH, self.CHAT_MESSAGE_CONTAINER).click()
                message_box.send_keys(Keys.ENTER)
                print("Message sent successfully.")
                time.sleep(2)
                return message
            except Exception as e:
                print(f"Error while sending message: {e}")

        def send_message(self, message_text, botName=None):
            try:
                print("Waiting for Teams chat input box to load...")
                WebDriverWait(self.driver, 600).until(
                    EC.presence_of_element_located((By.XPATH, self.CHAT_MESSAGE_CONTAINER))
                )
                WebDriverWait(self.driver, 600).until(
                    EC.element_to_be_clickable((By.XPATH, self.CHAT_MESSAGE_CONTAINER))
                )

                message_box = self.driver.find_element(By.XPATH, self.CHAT_MESSAGE_CONTAINER)
                message_box.click()
                time.sleep(2)

                if botName is not None:
                    # Type '@' first
                    message_box.send_keys('@')
                    time.sleep(0.5)
                    
                    # Type the bot name character by character with delay to trigger autocomplete
                    # Use first few characters to trigger suggestion (usually 3-4 chars is enough)
                    chars_to_type = botName[:4] if len(botName) > 4 else botName
                    for char in chars_to_type:
                        message_box.send_keys(char)
                        time.sleep(0.2)  # Small delay between each character
                    
                    time.sleep(2)  # Wait for autocomplete dropdown to appear

                    # Wait and click on the suggestion using data-tid attribute (more reliable)
                    autocomplete_xpath = self.AUTOCOMPLETE_PICKER_ITEM_XPATH.format(botName)
                    WebDriverWait(self.driver, 30).until(
                        EC.presence_of_element_located((By.XPATH, autocomplete_xpath))
                    )
                    WebDriverWait(self.driver, 30).until(
                        EC.element_to_be_clickable((By.XPATH, autocomplete_xpath))
                    )
                    self.driver.find_element(By.XPATH, autocomplete_xpath).click()
                    print(f"Selected bot '{botName}' from autocomplete.")
                    time.sleep(1)

                    # Send rest of the message (after bot mention)
                    message_box.send_keys(" " + message_text)
                else:
                    # If no bot, just send the message directly
                    message_box.send_keys(message_text)

                time.sleep(1)
                # Optional: Print current text (may not always reflect full content)
                message = self.driver.find_element(By.XPATH, self.CHAT_MESSAGE_CONTAINER).text
                print(f"Message to send: {message}")

                # Send the message
                message_box.send_keys(Keys.ENTER)
                print("Message sent successfully.")
                
                return message

            except Exception as e:
                print(f"Error while sending message: {e}")


        def get_last_teams_message(self, timeout=300, lastMessage=None):
            """
            Retrieves the content of the last Teams message block, skipping feedback/system prompts.

            Args:
                timeout (int): Maximum time to wait for a new message (seconds).
                lastMessage (str, optional): Previous message content to compare against.

            Returns:
                str or None: The text of the last real message block, or None if not found/changed.
            """
            attempt = 0
            start_time = time.time()
            feedback_phrases = [
                "Was this information helpful?",
                "Give feedback",
                "Send feedback",
                "Your feedback helps us improve",
                # Add more phrases if Teams UI changes
            ]
            try:
                message_content = None
                while attempt < 60 and (time.time() - start_time) < timeout:
                    print("Get all message blocks")
                    message_blocks = self.driver.find_elements(By.XPATH, self.MESSAGE_BLOCK)

                    if not message_blocks:
                        print("No messages found.")
                        time.sleep(2)
                        attempt += 1
                        continue

                    # Loop through message blocks in reverse (latest first)
                    for msg_block in reversed(message_blocks):
                        try:
                            msg_text = msg_block.find_element(By.XPATH, self.CHAT_MESSAGE).text.strip()
                        except Exception as e:
                            print(f"Error extracting message text: {e}")
                            continue

                        # Skip feedback/system messages
                        if any(phrase in msg_text for phrase in feedback_phrases):
                            print(f"Skipping feedback/system message: {msg_text}")
                            continue

                        # If lastMessage is provided, check if the new message is different
                        if (
                            lastMessage is not None and (
                                lastMessage.strip() == msg_text or
                                lastMessage.strip() in msg_text or
                                msg_text in lastMessage.strip()
                            )
                        ):
                            print("Last message is same as previous or contains previous, waiting and retrying...")
                            break  # Try again after waiting

                        print("Last real message content:")
                        print(msg_text)
                        return msg_text

                    attempt += 1
                    time.sleep(5)

                print("Last message did not change after 300 seconds or timeout reached.")
                return None
            except Exception as e:
                print(f"Error while getting last message: {e}")
                return None

        def verify_incident_details(self, expected_details: list,lastSentMessage=None) -> bool:
            """
            Verifies that the incident details extracted from the Teams message match the expected details.

            Args:
                expected_details (list): List of expected values (strings).

            Returns:
                bool: True if all values are found in the message, False otherwise.
            """
            for attempt in range(2):  
                try:
                    print(f"Attempt {attempt + 1} to verify incident details.")
                    time.sleep(3)  
                    message = self.get_last_teams_message()

                    if lastSentMessage is not None:
                        if lastSentMessage in message:
                            print(f"Last sent message '{lastSentMessage}' found in Teams message waiting for reply.")
                            continue

                    for value in expected_details:
                        if value not in message:
                            print(f"Value '{value}' not found in message.")
                            break
                        else:
                            print("All expected values matched.")
                            return True  # All values found

                except Exception as e:
                    print(f"ttempt {attempt + 1} failed with exception: {e}")

            print("Verification failed after 3 attempts.")
            return False
        
        def verify_similar_incidents(self,lastSentMessage=None) -> bool:
            """
            Verifies if the Teams message contains the string indicating that similar incidents are listed.

            Returns:
                bool: True if the expected message is found, False otherwise.
            """
            for attempt in range(7):  # Retry up to 5 times
                try:
                    print(f"Attempt {attempt + 1} to verify similar incidents.")
                    time.sleep(3)

                    message_element = self.get_last_teams_message(self.driver)
                    if not message_element:
                        print("No message element found.")
                        continue

                    if "Here are" in message_element:
                        print("'Here are the' found.")
                        return True
                except Exception as e:
                    print(f"Attempt {attempt + 1} failed: {e}")

            print("Verification failed after 5 attempts.")
            return False
        
        def verify_incident_details_contains(self,incident_id, expected_strings, timeout=10):
            try:
                print("Verifying Incident details")
                incident_element = WebDriverWait(self.driver, timeout).until(
                    EC.presence_of_element_located((By.XPATH, f"//*[contains(text(), '{incident_id}')]"))
                )
                text = incident_element.text
                for keyword in expected_strings:
                    if keyword.lower() not in text.lower():
                        print(f"Missing: {keyword}")
                        return False
                print("All keywords found.")
                return True
            except Exception as e:
                print(f"Exception during verification: {str(e)}")
                return False
            
        def verify_teams_tables(self, expected_incident_data=None, expected_worklog_data=None):
            """
            Verifies two tables in a Teams message: Incident Details and Work Logs.
            Fields with None will be skipped.

            :param driver: Selenium WebDriver instance
            :param expected_incident_data: dict like:
                {
                    "Incident Number": "INC000000058602",
                    "Description": "VPN is not working",
                    "Assignee": "Irwin Allen",
                    "Assigned Group": "LA Support 1"
                }

            :param expected_worklog_data: dict like:
                {
                    "Date Submitted": "2025-06-05 07:38:51 UTC",
                    "Notes": "need more info"
                }
            """

            try:
                # Find all tables inside the latest message body
                tables = self.driver.find_elements(By.XPATH,self.MESSAGE_TABLES)

                if len(tables) < 2:
                    print("Expected 2 tables in message, found", len(tables))
                    return

                # --- Incident Table Verification ---
                if expected_incident_data:
                    print("\nVerifying Incident Table:")
                    incident_table = tables[0]
                    self.verify_table_against_expected(self,incident_table, expected_incident_data)

                # # --- Work Log Table Verification ---
                # if expected_worklog_data:
                #     print("\nVerifying Work Logs Table:")
                #     worklog_table = tables[1]
                #     self.verify_table_against_expected(self,worklog_table, expected_worklog_data)

            except Exception as e:
                print(f"Error during Teams table verification: {e}")

        def verify_table_against_expected(self,table_element, expected_row_dict):
            """
            Helper to verify a single row table against expected values.

            :param table_element: WebElement of the table
            :param expected_row_dict: dictionary of expected column -> value
            """
            headers = table_element.find_elements(By.XPATH,self.INCIDENT_HEADERS)
            header_map = {header.text.strip(): i for i, header in enumerate(headers)}

            body_row = table_element.find_element(By.XPATH,self.INCIDENT_ROW)
            cells = body_row.find_elements(By.XPATH,self.INCIDENT_CELLS)

            for key, expected_value in expected_row_dict.items():
                if expected_value is None:
                    continue

                if key not in header_map:
                    print(f"Column '{key}' not found in table headers.")
                    continue

                index = header_map[key]
                if index >= len(cells):
                    print(f"Column index {index} out of range for row cells.")
                    continue

                actual_value = cells[index].text.strip()

                if actual_value not in expected_value:
                    print(f"Mismatch in '{key}': expected '{expected_value}', got '{actual_value}'")
                else:
                    print(f"Match for '{key}': '{actual_value}'")

        def verify_table_has_rows(self):
            """
            Verifies that the last Teams message contains a table with > 0 data rows using Selenium only.

            Args:
                driver: Selenium WebDriver instance.

            Returns:
                bool: True if table with at least one <tr> inside <tbody> is found, else False.
            """
            try:
                time.sleep(1)
                message_element = self.get_last_teams_message(self.driver)
                if not message_element:
                    print("No message element found.")
                    return False

                if "Here are the similar incidents" in message_element:
                    return True
                # Find table inside the last message
                table = message_element.find_element(By.TAG_NAME, "table")
                tbody = table.find_element(By.TAG_NAME, "tbody")
                rows = tbody.find_elements(By.TAG_NAME, "tr")

                if len(rows) == 0:
                    print("Table is present but has 0 rows.")
                    return False

                print(f"Table found with {len(rows)} rows.")
                return True

            except Exception as e:
                print(f"Exception while verifying table rows: {e}")
                return False

        def logout_from_teams(self):
            try:
                # Step 1: Click on the profile icon (top-right corner)
                profile_icon_xpath = "//button[@id='idna-me-control-avatar-trigger']"
                WebDriverWait(self.driver, 15).until(
                    EC.element_to_be_clickable((By.XPATH, profile_icon_xpath))
                ).click()
                print("Clicked profile icon.")

                # Step 2: Click "Sign out" (might vary slightly by region or Microsoft update)
                sign_out_xpath = "//button[@id='me-control-sign-out-button' and starts-with(@aria-label, 'Sign out')]"
                WebDriverWait(self.driver, 10).until(
                    EC.element_to_be_clickable((By.XPATH, sign_out_xpath))
                ).click()
                print("Clicked 'Sign out'.")

            except Exception as e:
                print(f"Logout failed: {e}")    

        def launch_dev_portal(self):
            self.driver.get(self.DEV_PORTAL_URL)
            try:
                for handle in self.driver.window_handles:
                    self.driver.switch_to.window(handle)
                    print("Check condition to confirm it's signup window")
                    try:
                        # Example condition: title or specific element
                        if "Sign in to" in self.driver.title or \
                        self.driver.find_element(By.XPATH, self.USERNAME_INPUT):
                            print(" Switched to Sign-Up window")
                            signupWindow=self.driver.current_window_handle
                            break
                    except:
                        continue
                print("Switched to popup window.")
                print("Logging into Teams.")
                print(f"Entering username as {TestConfig.TENANT_USERNAME} and password as {TestConfig.TENANT_PASSWORD}")
                WebDriverWait(self.driver, 30).until(
                    EC.presence_of_element_located((By.XPATH, self.USERNAME_INPUT))
                )
                self.driver.find_element(By.XPATH, self.USERNAME_INPUT).send_keys(TestConfig.TENANT_USERNAME + Keys.RETURN)
                time.sleep(2)
                WebDriverWait(self.driver, 30).until(
                    EC.presence_of_element_located((By.XPATH, self.PASSWORD_INPUT))
                )
                self.driver.find_element(By.XPATH,self.PASSWORD_INPUT).send_keys(TestConfig.TENANT_PASSWORD + Keys.RETURN)
                time.sleep(3)

                print("Handle \"Stay signed in?\" prompt")
                try:
                    print("Looking for Stay sign in page.")
                    self.driver.find_element(By.XPATH,self.STAY_SIGNED_IN_NO).click() 
                except:
                    pass  
                print("Waiting for page to load...")
                WebDriverWait(self.driver, 300).until(
                    EC.presence_of_element_located((By.XPATH, self.ENDPOINT_XPATH))
                )
                print("Dev portal is ready.")
            except Exception as e:
                print(f"Error launching dev portal: {e}")

        def update_bot_endpoint_dev_portal(self, new_endpoint):
          try:
              print("Waiting for endpoint input to load...")
              endpoint_input = WebDriverWait(self.driver, 300).until(
                  EC.presence_of_element_located((By.XPATH, self.ENDPOINT_XPATH))
              )
              current_value = endpoint_input.get_attribute("value")
              print(f"Current endpoint value: {current_value}")
              if current_value != new_endpoint:
                  endpoint_input.clear()
                  endpoint_input.send_keys(new_endpoint)
                  save_button = WebDriverWait(self.driver, 300).until(
                      EC.element_to_be_clickable((By.XPATH, self.SAVE_BUTTON))
                  )
                  save_button.click()
                  WebDriverWait(self.driver, 30).until(
                      lambda d: not d.find_element(By.XPATH, self.SAVE_BUTTON).is_enabled()
                  )
                  print(f"Updated endpoint to: {new_endpoint}")
              else:
                  print("Endpoint already set to the desired value.")
          except Exception as e:
              print(f"Error updating bot endpoint: {e}")
          