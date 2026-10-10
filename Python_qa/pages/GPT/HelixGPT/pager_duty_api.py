import json
import logging
import os
from datetime import datetime, timezone
from typing import Dict, Any, Optional
from pages.GPT.HelixGPT.login_request_api import LoginRequestAPI
from pages.GPT.HelixGPT.config_api import ConfigAPI


class PagerDutyAPI:
    """Python equivalent of PagerDuty_API Java class"""
    
    def __init__(self):
        self.config = ConfigAPI.get_config()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.cookies = {}
        self.login = LoginRequestAPI()
        self.pager_duty_id = None
        
        # Configure logging
        logging.basicConfig(level=logging.INFO)
        
        # Configure SSL settings for API requests using reusable method
        self.session = ConfigAPI.setup_ssl_session()
        
    def get_pager_duty_id(self) -> str:
        """
        Get PagerDuty ID from API response
        
        Returns:
            PagerDuty ID string
        """
        self.cookies = self.login.login_action(self.config)
        get_pager_duty_id_url = f"{self.config.get('IS_URL')}/api/rx/application/datapage"
        
        payload_path = os.path.join(
            "Python_qa", "resources", "HelixGPT", "Get_Connection_Details.json"
        )
        
        try:
            with open(payload_path, 'r') as file:
                body_json = file.read()
                body_json = body_json.replace("${1}", "Connection_PagerDuty")
                
        except FileNotFoundError:
            # Fallback payload if file not found
            body_json = '{"query": "Connection_PagerDuty"}'
            self.logger.warning(f"Payload file not found: {payload_path}. Using fallback.")
        except Exception as e:
            self.logger.error(f"Exception in PagerDuty ID retrieval: {str(e)}")
            raise
            
        headers = {
            "Content-Type": "application/json",
            "X-Requested-By": "XMLHttpRequest"
        }
        
        try:
            response = self.session.post(
                get_pager_duty_id_url,
                headers=headers,
                cookies=self.cookies,
                json=json.loads(body_json) if body_json.startswith('{') else {"query": body_json},
                timeout=30
            )
            
            assert response.status_code == 200, f"The get PagerDuty Id request is not successful: {response.status_code}"
            
            response_data = response.json()
            self.pager_duty_id = response_data.get("data", [{}])[0].get("379")
            
            assert self.pager_duty_id is not None, "The PagerDuty Id is not available"
            self.logger.info(f"Retrieved PagerDuty ID: {self.pager_duty_id}")
            
            return self.pager_duty_id
            
        except Exception as e:
            self.logger.error(f"Error getting PagerDuty ID: {str(e)}")
            raise
            
    def update_pager_duty_status(self, status: str) -> None:
        """
        Update PagerDuty status (enable/disable)
        
        Args:
            status: "enable" or "disable"
        """
        update_pager_duty_status_url = (
            f"{self.config.get('IS_URL')}/api/rx/application/record/recordinstance/"
            f"com.bmc.dsom.hgm:Connection_PagerDuty/{self.pager_duty_id}"
        )
        
        payload_path = os.path.join(
            "Python_qa", "resources", "HelixGPT", "Update_Connection_Status.json"
        )
        
        # Set status code
        status_code = "0" if status.lower() == "enable" else "10"
        
        # Generate timestamp
        now = datetime.now(timezone.utc)
        formatted_time = now.strftime("%Y-%m-%dT%H:%M:%S.%f")[:-3] + "Z"
        
        try:
            with open(payload_path, 'r') as file:
                body_json = file.read()
                body_json = (body_json
                           .replace("${0}", self.pager_duty_id)
                           .replace("${1}", "PagerDuty")
                           .replace("${2}", formatted_time)
                           .replace('"${3}"', status_code))
                           
        except FileNotFoundError:
            # Fallback payload
            body_json = json.dumps({
                "id": self.pager_duty_id,
                "type": "PagerDuty",
                "timestamp": formatted_time,
                "status": int(status_code)
            })
            self.logger.warning(f"Payload file not found: {payload_path}. Using fallback.")
        except Exception as e:
            self.logger.error(f"Exception in PagerDuty status update: {str(e)}")
            raise
            
        headers = {
            "Content-Type": "application/json",
            "X-Requested-By": "XMLHttpRequest"
        }
        
        try:
            response = self.session.put(
                update_pager_duty_status_url,
                headers=headers,
                cookies=self.cookies,
                json=json.loads(body_json) if body_json.startswith('{') else {"data": body_json},
                timeout=30
            )
            
            print(response.text)  # Debug output
            
            assert response.status_code == 204, f"The Update PagerDuty Status process is not successful: {response.status_code}"
            self.logger.info(f"PagerDuty status updated to: {status}")
            
        except Exception as e:
            self.logger.error(f"Error updating PagerDuty status: {str(e)}")
            raise
            
    def verify_pager_duty_status_details(self, status: str) -> None:
        """
        Verify PagerDuty status details
        
        Args:
            status: Expected status ("enable" or "disable")
        """
        get_pager_duty_details_url = (
            f"{self.config.get('IS_URL')}/api/rx/application/record/recordinstance/"
            f"com.bmc.dsom.hgm:Connection_PagerDuty/{self.pager_duty_id}"
        )
        
        headers = {
            "Content-Type": "application/json",
            "X-Requested-By": "XMLHttpRequest"
        }
        
        try:
            response = self.session.get(
                get_pager_duty_details_url,
                headers=headers,
                cookies=self.cookies,
                timeout=30
            )
            
            assert response.status_code == 200, f"The get PagerDuty Details request is not successful: {response.status_code}"
            
            response_data = response.json()
            response_id = response_data.get("id")
            
            assert self.pager_duty_id == response_id, f"The Id in response does not match. Expected: {self.pager_duty_id}, Got: {response_id}"
            
            # Get status value from fieldInstances
            status_value = response_data.get("fieldInstances", {}).get("7", {}).get("value")
            expected_status_code = 0 if status.lower() == "enable" else 10
            
            assert int(status_value) == expected_status_code, f"The Status of PagerDuty does not match. Expected: {expected_status_code}, Got: {status_value}"
            
            self.logger.info(f"PagerDuty status verification successful for: {status}")
            
        except Exception as e:
            self.logger.error(f"Error verifying PagerDuty status: {str(e)}")
            raise
