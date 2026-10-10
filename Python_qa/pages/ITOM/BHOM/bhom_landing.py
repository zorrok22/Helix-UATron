import time
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from pages.Base.base_page import BasePage
from utils.constants import SolQAConstants
from utils.common_waits import CommonWaits
class BhomLandingPage(BasePage):
    def __init__(self, driver):
        self.driver = driver
        self.common_waits = CommonWaits(driver=driver)
        
        # Define your locators or elements here as needed
        self.SEARCH_EVENT_INPUT = "//input[@id='txtSearchInp']"
        self.TICKET_ID_XPATH = "//*[@role='gridcell']//*[contains(@title, 'INC')]"
        self.EVENT_SEARCH_RESULT_BY_MESSAGE = "//*[contains(@class, 'text-info') and @title='{}']"
        self.REFRESH_BUTTON_XPATH = "//a[@id='eventRefreshButton']"
        self.event_search_result_by_message = "//*[contains(@class, 'text-info') and @title='{}']"
        self.ROWS_XPATH = "(//table//tr)[position()>1 and position()<=11]"  # First 10 rows, skipping header
        #self.ROWS_XPATH = "//div[@id='contenttableeventGrid']//div[@role='row']"
        self.MULTISERVICE_CELL=".//div[contains(@title, 'Multi-Service')]"
        self.INC_CELL=".//div[starts-with(@title, 'INC')]"

    def refresh_events(self):
        refresh_button = self.driver.find_element(By.XPATH, self.REFRESH_BUTTON_XPATH)
        self.common_waits.wait_for_element_visible(refresh_button)
        refresh_button.click()

    def get_ticket_id(self, event_msg):
        self.refresh_events()
        self.search_event_input = self.driver.find_element(By.XPATH, self.SEARCH_EVENT_INPUT)
        self.common_waits.wait_for_element_visible(self.search_event_input)
        self.search_event_input.clear()
        self.search_event_input.send_keys(event_msg)
        self.search_event_input.send_keys(Keys.ENTER)
        self.common_waits.wait_for_all_spinners_to_hide(SolQAConstants.APP_ITOM)
        self.common_waits.wait_for_element_visible_with_fluent_wait(
            self.driver,
            self.event_search_result_by_message.format(event_msg),
            10
        )
        self.ticket_id = self.driver.find_element(By.XPATH, self.TICKET_ID_XPATH)
        found_text= self.common_waits.wait_for_element_visible(self.ticket_id).text
        inc_index = found_text.find("INC0")
        if inc_index != -1:
            return found_text[inc_index:]
        return found_text

    def wait_for_multi_service_entry_old(self, max_attempts=20, wait_seconds=2):
        """
        Waits for any entry in the table with prefix 'Multi-Service - ' in the first 10 rows.
        If not found, clicks the refresh button, waits, and retries up to max_attempts times.
        Returns the row WebElement if found, else None.
        """
        for attempt in range(max_attempts):
            rows = self.driver.find_elements(By.XPATH, self.ROWS_XPATH)
            for row in rows:
                try:
                    cells = row.find_elements(By.XPATH, ".//td")
                    if len(cells) > 0 and cells[0].text.startswith("Multi-Service - "):
                        if len(cells) > 1:
                            return cells[1].text
                        else:
                            return None
                except Exception:
                    continue
            print("Situation Not found, refresh and retry")
            refresh_button = self.driver.find_element(By.XPATH, self.REFRESH_BUTTON_XPATH)
            self.common_waits.wait_for_element_visible(refresh_button)
            refresh_button.click()
            time.sleep(wait_seconds)
        return None
    
    def wait_for_multi_service_entry(self, max_attempts=20, wait_seconds=2):
        """
        Scans each row in the event table to find any cell containing text that starts with 'Multi-Service -'.
        If found, returns the value of the corresponding 'INC0...' text from the same row (column 12).
        If not found, refreshes the table and retries until max_attempts.
        """
       
        for attempt in range(max_attempts):
            try:
                rows = self.driver.find_elements(By.XPATH, self.ROWS_XPATH)
                for row in rows:
                    try:
                        multi_service_cell = row.find_element(By.XPATH,self.MULTISERVICE_CELL)
                        inc_cell = row.find_element(By.XPATH,self.INC_CELL)
                        inc_text = inc_cell.get_attribute("title").strip()
                        if inc_text.startswith("INC0"):
                            return inc_text
                    except Exception:
                        continue
            except Exception as e:
                print(f"[Attempt {attempt+1}] Error: {e}")

            print(f"[Attempt {attempt+1}] 'Multi-Service -' not found. Refreshing...")
            try:
                refresh_button = self.driver.find_element(By.XPATH, self.REFRESH_BUTTON_XPATH)
                self.common_waits.wait_for_element_visible(refresh_button)
                refresh_button.click()
            except Exception as e:
                print(f"[Attempt {attempt+1}] Failed to click refresh: {e}")

            time.sleep(wait_seconds)

        return None

    def get_inc_from_multi_service_row(self, max_retries=25, wait_time=5, prefix="host-04"):
        time.sleep(10)
        for attempt in range(max_retries):
            print(f"Attempt {attempt + 1} of {max_retries}")
            self.driver.refresh()
            # Wait for table to load (adjust xpath if needed)
            WebDriverWait(self.driver, 30).until(
                EC.presence_of_element_located((By.XPATH, "//div[@id='contenttableeventGrid']"))
            )

            # Get the first 25 rows
            try:
                rows = self.driver.find_elements(By.XPATH, "//div[starts-with(@id, 'row') and contains(@id, 'eventGrid')][position() <= 25]")
                for row_index in range(len(rows)):
                    # Re-fetch the row to avoid stale element
                    rows = self.driver.find_elements(By.XPATH, "//div[starts-with(@id, 'row') and contains(@id, 'eventGrid')][position() <= 25]")
                    row = rows[row_index]
                    cells = row.find_elements(By.XPATH, ".//div[@role='gridcell']")
                    multi_service_found = False
                    for cell_index in range(len(cells)):
                        # Re-fetch the cell to avoid stale element
                        cells = row.find_elements(By.XPATH, ".//div[@role='gridcell']")
                        cell = cells[cell_index]
                        cell_text = cell.text.strip()
                        if cell_text.startswith(prefix):
                            multi_service_found = True
                            break

                    if multi_service_found:
                        for cell_index in range(len(cells)):
                            cells = row.find_elements(By.XPATH, ".//div[@role='gridcell']")
                            cell = cells[cell_index]
                            cell_text = cell.text.strip()
                            if cell_text.startswith("INC0"):
                                print(f"Found: {cell_text}")
                                return cell_text
            except Exception as e:
                print(f"Error while processing rows: {e}")

            print("Not found. Clicking refresh and retrying...")
            try:
                refresh_button = WebDriverWait(self.driver, 10).until(
                    EC.element_to_be_clickable((By.XPATH, "//a[@id='eventRefreshButton']"))
                )
                refresh_button.click()
                time.sleep(wait_time)
            except Exception as e:
                print(f"Refresh failed: {e}")
                break

        print("Multi-Service row with INC0 not found after max retries.")
        return None
    

