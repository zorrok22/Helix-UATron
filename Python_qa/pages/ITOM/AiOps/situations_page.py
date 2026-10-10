from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait as Wait
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException,StaleElementReferenceException
import time
from utils.common_waits import CommonWaits
from .aiops_page import AIOpsPage
from selenium.webdriver.support.ui import WebDriverWait

class SituationsPage(AIOpsPage):
    """Class for interacting with the Situations page in AIOps"""
    def __init__(self, driver):
        super().__init__(driver)
        commonWaitsObj= CommonWaits(driver)
    
    def remove_filter(self, filter_name):
        """
        Remove a specific filter from the situations list
        Args:
            filter_name (str): The name of the filter to remove (e.g., 'Status: Open, Acknowledged, Assigned')
        Returns:
            bool: True if filter was successfully removed, False otherwise
        """
        try:
            print(f"Attempting to remove filter: {filter_name}")
            
            # Format the locator with the filter name
            filter_locator = (By.XPATH, self.locators.FILTER_REMOVE_BUTTON[1].format(filter_name))
            print(f"Constructed filter locator: {filter_locator}")
            
            # Wait for the element to be present before clicking
            print("Waiting for filter remove button to be present...")
            element = self.wait.until(EC.presence_of_element_located(filter_locator))
            print("Filter remove button found, attempting to click...")
            
            # Add delay before clicking
            time.sleep(2)
            
            # Try to click the element directly
            element.click()
            print("Successfully clicked the filter remove button")
            
            # Add delay after clicking
            time.sleep(2)
            
            # Wait for the filter to be removed
            print("Waiting for filter to be removed...")
            self.wait.until(EC.invisibility_of_element_located(filter_locator))
            
            print(f"Successfully removed filter: {filter_name}")
            return True
            
        except Exception as e:
            print(f"Failed to remove filter {filter_name}: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
    
    def open_advanced_filter(self):
        """
        Click the advanced filter dropdown to open the filter menu
        Returns:
            bool: True if the dropdown was successfully opened, False otherwise
        """
        try:
            print("Attempting to open advanced filter dropdown...")
            
            # Wait for and click the advanced filter dropdown
            print("Waiting for advanced filter dropdown to be present...")
            element = self.wait.until(EC.element_to_be_clickable(self.locators.ADVANCED_FILTER_DROPDOWN))
            print("Advanced filter dropdown found, attempting to click...")
            
            # Add delay before clicking
            time.sleep(2)
            
            element.click()
            print("Successfully clicked the advanced filter dropdown")
            
            # Add delay after clicking
            time.sleep(2)
            
            # TODO: Add verification that the dropdown menu is visible
            # This might require adding a locator for the dropdown menu itself
            
            return True
            
        except Exception as e:
            print(f"Failed to open advanced filter dropdown: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
    
    def select_ml_based_situation(self):
        """
        Select the ML-based Situation type from the filter and apply the filters
        Returns:
            bool: True if the type was successfully selected and filters applied, False otherwise
        """
        try:
            print("Attempting to select ML-based Situation type...")
            
            # Click on Type filter
            print("Waiting for Type filter to be present...")
            type_element = self.wait.until(EC.element_to_be_clickable(self.locators.TYPE_FILTER))
            print("Type filter found, attempting to click...")
            
            # Add delay before clicking
            time.sleep(2)
            
            type_element.click()
            print("Successfully clicked Type filter")
            
            # Add delay after clicking
            time.sleep(2)
            
            # Select ML-based Situation option
            print("Waiting for ML-based Situation option to be present...")
            ml_option = self.wait.until(EC.element_to_be_clickable(self.locators.ML_BASED_SITUATION_OPTION))
            print("ML-based Situation option found, attempting to click...")
            
            # Add delay before clicking
            time.sleep(2)
            
            ml_option.click()
            print("Successfully selected ML-based Situation")
            
            # Add delay after clicking
            time.sleep(2)
            
            # Click Apply filters button
            print("Waiting for Apply filters button to be present...")
            apply_button = self.wait.until(EC.element_to_be_clickable(self.locators.APPLY_FILTERS_BUTTON))
            print("Apply filters button found, attempting to click...")
            
            # Add delay before clicking
            time.sleep(2)
            
            apply_button.click()
            print("Successfully clicked Apply filters button")
            
            # Add delay after clicking
            time.sleep(5)
            
            return True
            
        except Exception as e:
            print(f"Failed to select ML-based Situation: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
    
    def click_first_situation_message(self):
        """
        Click the first situation message element in the list
        Returns:
            bool: True if the element was successfully clicked, False otherwise
        """
        try:
            print("Attempting to click first situation message...")
            
            # Wait for and click the first situation message element
            print("Waiting for situation message element to be clickable...")
            wait = WebDriverWait(self.driver, 120)
            element = wait.until(
                EC.element_to_be_clickable(self.locators.FIRST_SITUATION_MESSAGE)
            )
            print("Situation message element found, attempting to click...")
            
            # Add small delay before clicking
            time.sleep(2)
            
            element.click()
            print("Successfully clicked the first situation message")
            
            # Add delay after clicking
            time.sleep(2)
            
            return True
            
        except Exception as e:
            print(f"Failed to click first situation message: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
    
    def click_ask_helixgpt_button(self):
        """
        Wait for and click the Ask HelixGPT button
        Returns:
            bool: True if the button was successfully clicked, False otherwise
        """
        try:
            print("Waiting for Ask HelixGPT button to appear...")
            
            # First wait for the element to be present in the DOM
            print("Waiting for Ask HelixGPT button to be present in DOM...")
            self.wait.until(EC.presence_of_element_located(self.locators.ASK_HELIXGPT_BUTTON))
            
            # Then wait for it to be visible
            print("Waiting for Ask HelixGPT button to be visible...")
            self.wait.until(EC.visibility_of_element_located(self.locators.ASK_HELIXGPT_BUTTON))
            
            # Finally wait for it to be clickable
            print("Waiting for Ask HelixGPT button to be clickable...")
            element = self.wait.until(EC.element_to_be_clickable(self.locators.ASK_HELIXGPT_BUTTON))
            print("Ask HelixGPT button found, attempting to click...")
           
            # Add small delay before clicking
            time.sleep(2)
            
            # Try to click using JavaScript as a fallback
            try:
                element.click()
            except Exception as click_exception:
                print("Regular click failed, trying JavaScript click...")
                self.driver.execute_script("arguments[0].click();", element)
            
            print("Successfully clicked Ask HelixGPT button")
            
            # Add delay after clicking
            time.sleep(2)
            
            return True
            
        except Exception as e:
            print(f"Failed to click Ask HelixGPT button: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
    
    def click_helixgpt_tab(self, tab_id):
        """
        Click a specific tab in the HelixGPT interface
        Args:
            tab_id (str): The ID of the tab to click (e.g., 'helixgpt-log-insights', 'ask-helixgpt-questions')
        Returns:
            bool: True if the tab was successfully clicked, False otherwise
        """
        try:
            print(f"Attempting to click HelixGPT tab: {tab_id}")
            
            # Format the locator with the tab ID
            tab_locator = (By.XPATH, self.locators.HELIXGPT_TAB[1].format(tab_id))
            print(f"Constructed tab locator: {tab_locator}")
            
            # First wait for the element to be present in the DOM
            print("Waiting for tab to be present in DOM...")
            self.wait.until(EC.presence_of_element_located(tab_locator))
            
            # Then wait for it to be visible
            print("Waiting for tab to be visible...")
            self.wait.until(EC.visibility_of_element_located(tab_locator))
            
            # Finally wait for it to be clickable
            print("Waiting for tab to be clickable...")
            element = self.wait.until(EC.element_to_be_clickable(tab_locator))
            print("Tab found, attempting to click...")
            
            # Scroll the element into view
            self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
            
            # Add small delay before clicking
            time.sleep(2)
            
            # Try to click using JavaScript as a fallback
            try:
                element.click()
            except Exception as click_exception:
                print("Regular click failed, trying JavaScript click...")
                self.driver.execute_script("arguments[0].click();", element)
            
            print(f"Successfully clicked HelixGPT tab: {tab_id}")
            
            # Add delay after clicking
            time.sleep(2)
            
            return True
            
        except Exception as e:
            print(f"Failed to click HelixGPT tab {tab_id}: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
    
    def get_log_insights_summary(self):
        """
        Extract and return the summary text from the Log Insights tab
        Returns:
            str: The summary text if found, None otherwise
        """
        try:
            print("Attempting to extract Log Insights summary...")
            
            # Wait for the summary element to be present
            print("Waiting for summary element to be present...")
            self.wait.until(EC.presence_of_element_located(self.locators.LOG_INSIGHTS_SUMMARY))
            
            # Get the element and extract text
            print("Extracting summary text...")
            element = self.driver.find_element(*self.locators.LOG_INSIGHTS_SUMMARY)
            summary_text = element.text.strip()
            
            print(f"Successfully extracted summary: {summary_text}")
            return summary_text
            
        except Exception as e:
            print(f"Failed to extract Log Insights summary: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return None
    
    def click_events_display_in_situation_explanation(self):
        """
        Click the events display button in situation explanation
        Returns:
            bool: True if the button was successfully clicked, False otherwise
        """
        try:
            print("Attempting to click events display button in situation explanation...")
            
            # Wait for and click the events display button
            print("Waiting for events display button to be clickable...")
            element = self.wait.until(EC.element_to_be_clickable(self.locators.EVENTS_DISPLAY_IN_SITUATION_EXPLANATION))
            print("Events display button found, attempting to click...")
            
            # Add small delay before clicking
            time.sleep(2)
            
            # Execute Javascript click
            self.driver.execute_script("arguments[0].click();", element)
            # element.click()
            print("Successfully clicked events display button")
            
            # Add delay after clicking
            time.sleep(2)
            
            return True
            
        except Exception as e:
            print(f"Failed to click events display button: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
    
    def get_event_messages(self):
        """
        Extract and return all event messages from the events table
        Returns:
            list: List of event messages if found, empty list otherwise
        """
        try:
            print("Attempting to extract event messages from table...")
            
            # Wait for the table to be present
            print("Waiting for events table to be present...")
            self.wait.until(EC.element_to_be_clickable(self.locators.EVENT_MESSAGES))
            
            # Get all event message elements
            print("Extracting event messages...")
            elements = self.driver.find_elements(*self.locators.EVENT_MESSAGES)
            
            # Extract text from each element
            event_messages = [element.text.strip() for element in elements]
            
            print(f"Successfully extracted {len(event_messages)} event messages")
            for i, message in enumerate(event_messages, 1):
                print(f"Event {i}: {message}")
            
            return event_messages
            
        except Exception as e:
            print(f"Failed to extract event messages: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return []
    
    def get_root_cause_summary(self):
        """
        Extract the root cause summary text
        Returns:
            str: The root cause summary text if found, None otherwise
        """
        try:
            print("Extracting root cause summary...")
            element = self.find_element(*self.locators.ROOT_CAUSE_SUMMARY)
            summary = element.text
            print(f"Root cause summary: {summary}")
            return summary
        except Exception as e:
            print(f"Failed to extract root cause summary: {str(e)}")
            return None
    
    def get_situation_summary_description(self):
        """
        Extract the situation summary description text
        Returns:
            str: The situation summary description text if found, None otherwise
        """
        try:
            print("Extracting situation summary description...")
            element = self.find_element(*self.locators.SITUATION_SUMMARY_DESCRIPTION)
            description = element.text
            print(f"Situation summary description: {description}")
            return description
        except Exception as e:
            print(f"Failed to extract situation summary description: {str(e)}")
            return None

    def clear_all_filters(self):
        """
        Clear all filters applied to the situations list
        Returns:
            bool: True if all filters were successfully cleared, False otherwise
        """
        try:
            print("Attempting to clear all filters...")
            
            time.sleep(2)

            # XPath: Adjust as per your structure.
            # This XPath finds all <button> elements that are great-grandchildren of a div with class 'parent'
            xpath = "//adapt-subnav[contains(@class, 'advanced-filter-tags')]//adapt-subnav-item[contains(@class, 'advanced-filter-tags__item')]//div[contains(@class, 'ng-star-inserted')]//adapt-tag[@placement='top']//span[contains(@class, 'close-btn-always-visible')]//button"

            count = 1
            
            try:
                # Wait for the filter buttons to be present
                print("Waiting for filter buttons to be present...")
                self.filter_button_wait.until(EC.element_to_be_clickable((By.XPATH, xpath)))

                # Get the count of buttons first (optional, useful for controlling loop)
                total_buttons = len(self.driver.find_elements(By.XPATH, xpath))
                print(f"Found {total_buttons} buttons to click.")
                
                
                while total_buttons > 0:
                # Re-fetch the button list in each iteration to avoid stale references
                    buttons = self.driver.find_elements(By.XPATH, xpath)
                    total_buttons = len(buttons)
                    buttons[0].click()
                    print(f"Clicked button {count}")
                    count += 1
                print("Buttons:", buttons)
            except Exception as e:
                print(f"Failed to click button {count}: {e}")
            time.sleep(1)
            
            return True
            
        except Exception as e:
            print(f"Failed to clear all filters: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False

    def click_BAR_questions(self, question_type):
        """
        Click the BAR questions button in the HelixGPT interface
        Returns:
            bool: True if the button was successfully clicked, False otherwise
        """
        try:
            print("Attempting to click BAR questions button...")

            locator = self.locators.QUESTION_ID[question_type]

            print("Constructed locator for BAR questions button: ", locator)
            
            # Wait for and click the BAR questions button
            print("Waiting for BAR questions button to be clickable...")
            element = self.wait.until(EC.element_to_be_clickable(locator))
            print("BAR questions button found, attempting to click...")
            
            # Add small delay before clicking
            time.sleep(2)
            
            element.click()
            print("Successfully clicked BAR questions button")
            
            # Add delay after clicking
            time.sleep(2)
            
            return True
            
        except Exception as e:
            print(f"Failed to click BAR questions button: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
        
    def extract_BAR_answers(self, answer_type):
        """
        Extract the answers from the BAR questions
        Returns:
            list: List of answers if found, empty list otherwise
        """
        try:
            print("Extracting BAR answers...")
            
            locator = self.locators.ANSWER_ID[answer_type]
            print("Constructed locator for BAR answers: ", locator)
            # Wait for the answers to be present
            print("Waiting for BAR answers to be present...")
            self.wait.until(EC.presence_of_all_elements_located(locator))
            
            # Get all answer elements
            print("Extracting BAR answers...")
            elements = self.driver.find_elements(*locator)
            
            # Extract text from each element
            bar_answers = [element.text.strip() for element in elements]
            
            print(f"Successfully extracted {len(bar_answers)} BAR answers")
            for i, answer in enumerate(bar_answers, 1):
                print(f"Answer {i}: {answer}")
            
            return bar_answers
            
        except Exception as e:
            print(f"Failed to extract BAR answers: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return []
        
    def change_time_range_to_last_30_days(self):
        """
        Change the time range for the situations
        Args:
            time_range (str): The time range to set (e.g., 'Last 1 hour', 'Last 24 hours')
        Returns:
            bool: True if the time range was successfully changed, False otherwise
        """
        try:
            print(f"Attempting to change time range to: Last 30 days")
            
            # Wait for and click the time range dropdown
            print("Waiting for time range dropdown to be clickable...")
            element = self.wait.until(EC.element_to_be_clickable(self.locators.TIME_RANGE_DROPDOWN))
            print("Time range dropdown found, attempting to click...")
            
            # Add small delay before clicking
            time.sleep(2)
            
            element.click()
            print("Successfully clicked time range dropdown")

            # Wait for Last 30 Days option to be clickable
            print("Waiting for Last 30 Days option to be clickable...")
            element = self.wait.until(EC.element_to_be_clickable(self.locators.TIME_RANGE_LAST_30_DAYS))
            print("Last 30 Days option found, attempting to click...")

            # Add small delay before clicking
            time.sleep(2)
            # Click the Last 30 Days option
            element.click()
            print("Successfully clicked Last 30 Days option")
            
            # Add delay after clicking
            time.sleep(2)
            
            return True
            
        except Exception as e:
            print(f"Failed to change time range: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return False
        
    def close_all_situations(self):
        
        try:
            # Wait for the table body to load
            self.wait.until(EC.presence_of_element_located((By.XPATH, self.locators.SITUATIONS_TABLE)))
            # Get all rows in the table body
            rows = self.driver.find_elements(By.XPATH, self.locators.SITUATIONS_TABLE_ROWS)
            print(f"Found {len(rows)} rows.")

            for index, row in enumerate(rows):
                try:
                    print(f"Processing row {index + 1}")
                    # Scroll into view if needed
                    self.driver.execute_script("arguments[0].scrollIntoView(true);", row)
                    time.sleep(0.5)
                    # Find and click the three-dot action menu
                    action_button = row.find_element(By.XPATH, self.locators.ACTION_BUTTON)
                    action_button.click()
                    print(f"Clicked action menu in row {index + 1}")
                    time.sleep(1)
                    self.wait.until(EC.presence_of_element_located((By.XPATH, self.locators.CLOSE_BUTTON)))

                    self.driver.find_element(By.XPATH, self.locators.CLOSE_BUTTON).click()
                    print(f"Clicked close button in row {index + 1}")

                    self.wait.until(EC.presence_of_element_located((By.XPATH, self.locators.CONFIRMATIONPOPUP_YES_BUTTON)))

                    self.driver.find_element(By.XPATH, self.locators.CONFIRMATIONPOPUP_YES_BUTTON).click()
                    print(f"Clicked yes button in Confirmation popup {index + 1}")
                    time.sleep(1)  # Wait for the action to complete
                except Exception as e:
                    print(f"Error processing row {index + 1}: {e}")
            return True
        except Exception as e:
            print(f"Failed to close all situations: {e}")
            return False

    def open_askgpt(self):
        print("Attempting to open Ask HelixGPT ...")
        self.wait.until(EC.element_to_be_clickable(*self.locators.ASK_HELIXGPT_BUTTON)).click()
        print("Successfully opened Ask HelixGPT.")
    
    def click_BAP(self):
        print("Attempting to click Best Action Plan...")
        self.wait.until(EC.element_to_be_clickable(self.locators.BAP_TAB)).click()
        print("Successfully clicked Best Action Plan.")

    def ask_impact(self):
        """
        Click the Impact question in the BAR questions section
        Returns:
            bool: True if the Impact question was successfully clicked, False otherwise
        """
        wait = Wait(self.driver, 10)

        try:
            print("Attempting to click Impact question...")
            try:
                # Try to find and click the "Impact?" button
                impact_button = wait.until(
                    EC.presence_of_element_located(
                        (By.XPATH, self.locators.IMPACT_BUTTON)
                    )
                )
                impact_button.click()
                print("Clicked 'Impact?' button.")
                return True
            except TimeoutException:
                try:
                    # If "Impact?" not found, click the "Show more" button
                    show_more_button = wait.until(
                        EC.element_to_be_clickable(
                            (By.XPATH, self.locators.SHOW_MORE_BUTTON)
                        )
                    )
                    show_more_button.click()
                    print("Clicked 'Show more' button.")
                    impact_button = wait.until(
                    EC.presence_of_element_located(
                        (By.XPATH, self.locators.IMPACT_BUTTON)
                    )
                )
                    impact_button.click()
                    print("Clicked 'Impact?' button.")
                    return True
                except TimeoutException:
                        print("'Impact?' and 'Show more' buttons not found.")
                        print("Successfully clicked Impact question")
        except Exception as e:
            print(f"Failed to click Impact question: {str(e)}")
            return False
        
    def find_incident_by_situation_OLD(self, situation_name, max_attempts=20,click=True):
        """
        Search the table for a row where a cell starts with `situation_name`,
        then return the cell text that starts with 'INC' from that row. 
        Refresh table up to max_attempts times if not found.
        """
        for attempt in range(max_attempts):
            try:
                # 1. Wait for the table to be present in the DOM
                table = WebDriverWait(self.driver, 10).until(
                    EC.presence_of_element_located((By.XPATH, self.locators.SITUATIONS_TABLE))
                )
                # 2. Iterate all rows in the table body
                rows = table.find_elements(By.TAG_NAME, "tr")
                for row in rows:
                    try:
                        cells = row.find_elements(By.TAG_NAME, "td")
                    except StaleElementReferenceException:
                        # If this row became stale, skip it
                        continue
                    # 3. Check if any cell starts with the situation name
                    if any(cell.text.startswith(situation_name) for cell in cells):
                        # 4. If found, look for the cell that starts with 'INC' in the same row
                        for cell in cells:
                            if cell.text.startswith("INC"):
                                if click:
                                    try:
                                        WebDriverWait(self.driver, 5).until(EC.element_to_be_clickable(cell))
                                        cell.click()
                                    except Exception as e:
                                        print(f"Warning: Could not click INC cell: {e}")
                                return cell.text.strip()
                                
                # 5. No matching row found: click the refresh button
                refresh_btn = WebDriverWait(self.driver, 10).until(
                    EC.element_to_be_clickable((By.XPATH, "//button[@id='toggle-refresh']"))
                )
                refresh_btn.click()
                # 6. Wait for the table to reload: wait for old table to go stale
                WebDriverWait(self.driver, 10).until(EC.staleness_of(table))
                # Optional: could also wait for invisibility of a spinner, e.g.
                # WebDriverWait(driver, 10).until(EC.invisibility_of_element_located((By.CSS_SELECTOR, ".loading-spinner")))
                # 7. Pause before next attempt
                time.sleep(10)
            except (TimeoutException, StaleElementReferenceException):
                # If waiting timed out or elements went stale, retry after a short delay
                time.sleep(1)
                continue
        # 8. If not found after max_attempts, return None or raise an error
        return None

    def find_incident_by_situation(self, situation_name, max_attempts=40, click=True):
        """
        Search the table for a row where any cell starts with `situation_name`,
        then return the 'Incident ID' cell text from that row.
        Optionally click the 'Incident ID' cell before returning it.
        """

        for attempt in range(max_attempts):
            try:
                # 1. Wait for table to appear
                table = WebDriverWait(self.driver, 10).until(
                    EC.presence_of_element_located((By.XPATH, self.locators.SITUATIONS_TABLE))
                )

                # 2. Get Incident ID column index from thead
                header_cells = table.find_element(By.TAG_NAME, "thead").find_elements(By.TAG_NAME, "th")
                incident_col_index = None
                for idx, th in enumerate(header_cells):
                    if "Incident ID" in th.text.strip():
                        incident_col_index = idx
                        break
                if incident_col_index is None:
                    raise Exception("Could not find 'Incident ID' column in headers")

                # 3. Iterate over tbody rows
                rows = table.find_element(By.TAG_NAME, "tbody").find_elements(By.TAG_NAME, "tr")
                for row in rows:
                    try:
                        cells = row.find_elements(By.TAG_NAME, "td")
                    except StaleElementReferenceException:
                        continue

                    # If any cell in row starts with situation_name
                    if any(situation_name in cell.text.strip() for cell in cells):
                        # Get Incident ID cell by column index
                        if incident_col_index < len(cells):
                            inc_cell = cells[incident_col_index]
                            if "INC" in inc_cell.text.strip():
                                if click:
                                    try:
                                        WebDriverWait(self.driver, 5).until(EC.element_to_be_clickable(inc_cell))
                                        inc_cell.click()
                                    except Exception as e:
                                        print(f"Warning: Could not click Incident ID cell: {e}")
                                return inc_cell.text.strip()

                # 4. Refresh if not found
                refresh_btn = WebDriverWait(self.driver, 10).until(
                    EC.element_to_be_clickable((By.XPATH, self.locators.REFRESH_BUTTON))
                )
                refresh_btn.click()
                WebDriverWait(self.driver, 10).until(EC.staleness_of(table))
                time.sleep(10)

            except (TimeoutException, StaleElementReferenceException):
                time.sleep(1)
                continue

        return None

    def find_incident_id(self, situation_prefix, max_retries=20, wait_timeout=15):
        table_xpath = "//table[@class='ui-table-scrollable-body-table']"
        refresh_btn_xpath = "//button[@id='toggle-refresh']"
        spinner_xpath = "//div[contains(@class,'busyLoader') or contains(@class,'a-loader')]"

        for attempt in range(1, max_retries + 1):
            try:
                # Wait until table is present and spinner disappears
                WebDriverWait(self.driver, wait_timeout).until(
                    EC.presence_of_element_located((By.XPATH, table_xpath))
                )
                WebDriverWait(driver, wait_timeout).until_not(
                    EC.presence_of_all_elements_located((By.XPATH, spinner_xpath))
                )

                # Get all rows in the table
                rows = driver.find_elements(By.XPATH, f"{table_xpath}//tr[td]")
                for row in rows:
                    cells = row.find_elements(By.XPATH, ".//td")
                    texts = [cell.text.strip() for cell in cells if cell.text.strip()]

                    # Check if any cell starts with situation prefix
                    if any(text.startswith(situation_prefix) for text in texts):
                        # Look for Incident ID starting with INC
                        for text in texts:
                            if text.startswith("INC"):
                                return text  # Found incident ID

                # If not found, refresh table
                refresh_btn = WebDriverWait(driver, wait_timeout).until(
                    EC.element_to_be_clickable((By.XPATH, refresh_btn_xpath))
                )
                refresh_btn.click()
                print(f"[Retry {attempt}] Situation not found, refreshing table...")
                time.sleep(2)  # Short pause before next check

            except TimeoutException:
                print(f"[Retry {attempt}] Timeout while waiting for table/spinner.")
                try:
                    driver.find_element(By.XPATH, refresh_btn_xpath).click()
                except:
                    pass
                time.sleep(2)

        return None  # Not found after all retries
        
