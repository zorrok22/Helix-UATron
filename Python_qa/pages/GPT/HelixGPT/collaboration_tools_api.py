import json
import logging
import os
from typing import Dict, Any, Optional, List
from pages.GPT.HelixGPT.login_request_api import LoginRequestAPI
from pages.GPT.HelixGPT.config_api import ConfigAPI


class CollaborationToolsAPI:
    """Python equivalent of Collaboration_tools_API Java class"""
    
    def __init__(self):
        self.config = ConfigAPI.get_config()
        self.logger = logging.getLogger(self.__class__.__name__)
        self.cookies = {}
        self.login = LoginRequestAPI()
        self.collaboration_tools_id = None
        
        # Configure logging
        logging.basicConfig(level=logging.INFO)
        
        # Configure SSL settings for API requests using reusable method
        self.session = ConfigAPI.setup_ssl_session()
        
    def get_collaboration_tools_id(self) -> str:
        """
        Get Collaboration Tools ID from API response
        
        Returns:
            Collaboration Tools ID string
        """
        self.cookies = self.login.login_action(self.config)
        get_collaboration_tools_id_url = f"{self.config.get('IS_URL')}/api/rx/application/datapage"
        
        payload_path = os.path.join(
            "Python_qa", "resources", "HelixGPT", "Get_Connection_Details.json"
        )
        
        try:
            with open(payload_path, 'r') as file:
                body_json = file.read()
                body_json = body_json.replace("${1}", "Toolkit")
                
        except FileNotFoundError:
            # Fallback payload if file not found
            body_json = '{"query": "Toolkit"}'
            self.logger.warning(f"Payload file not found: {payload_path}. Using fallback.")
        except Exception as e:
            self.logger.error(f"Exception in Collaboration Tools: {str(e)}")
            raise
            
        headers = {
            "Content-Type": "application/json",
            "X-Requested-By": "XMLHttpRequest"
        }
        
        try:
            response = self.session.post(
                get_collaboration_tools_id_url,
                headers=headers,
                cookies=self.cookies,
                json=json.loads(body_json) if body_json.startswith('{') else {"query": body_json},
                timeout=30
            )
            
            json_response = response.text
            response_data = response.json()
            
            data_list = response_data.get("data", [])
            
            for record in data_list:
                if (record.get("8") is not None and 
                    str(record.get("8")).lower() == "collaboration tools"):
                    self.collaboration_tools_id = str(record.get("379"))
                    self.logger.info(f"Found field 379 for type 'COLLABORATION_TOOLS': {self.collaboration_tools_id}")
                    break
            else:
                self.logger.warning("No record found with field 8 = 'Collaboration Tools'")
                
            return self.collaboration_tools_id
            
        except Exception as e:
            self.logger.error(f"Error parsing JSON response for type 'COLLABORATION_TOOLS': {str(e)}")
            raise
            
    def toggle_enable_status(self, state: str) -> None:
        """
        Toggle enable status for collaboration tools
        
        Args:
            state: "xmatter" or "pagerduty"
        """
        toggle_enable_status_url = (
            f"{self.config.get('IS_URL')}/api/rx/application/record/recordinstance/"
            f"com.bmc.dsom.hgm:Toolkit/{self.collaboration_tools_id}"
        )
        
        payload_path = os.path.join(
            "Python_qa", "resources", "HelixGPT", "Toggle_XMatter_PagerDuty_Settings.json"
        )
        
        # Set boolean flags based on state
        xmatter_status = state.lower() == "xmatter"
        pagerduty_status = state.lower() == "pagerduty"
        
        try:
            with open(payload_path, 'r') as file:
                body_json = file.read()
                body_json = (body_json
                           .replace("${0}", self.collaboration_tools_id)
                           .replace("${1}", str(xmatter_status).lower())
                           .replace("${2}", str(pagerduty_status).lower()))
                           
        except FileNotFoundError:
            # Fallback payload
            body_json = json.dumps({
                "id": self.collaboration_tools_id,
                "xmatter_enabled": xmatter_status,
                "pagerduty_enabled": pagerduty_status
            })
            self.logger.warning(f"Payload file not found: {payload_path}. Using fallback.")
        except Exception as e:
            self.logger.error(f"Exception in Collaboration Tools: {str(e)}")
            raise
            
        headers = {
            "Content-Type": "application/json",
            "X-Requested-By": "XMLHttpRequest"
        }
        
        try:
            response = self.session.put(
                toggle_enable_status_url,
                headers=headers,
                cookies=self.cookies,
                json=json.loads(body_json) if body_json.startswith('{') else {"data": body_json},
                timeout=30
            )
            
            assert response.status_code == 204, f"The Toggle Collaboration Tools Status process is not successful: {response.status_code}"
            self.logger.info(f"Successfully toggled collaboration tools status for: {state}")
            
        except Exception as e:
            self.logger.error(f"Error toggling collaboration tools status: {str(e)}")
            raise
