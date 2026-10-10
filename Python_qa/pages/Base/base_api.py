from typing import Dict, Any, Optional
import unittest
import requests
import json
from dataclasses import dataclass
from urllib.parse import urljoin
from abc import ABC, abstractmethod
from test_config import TestConfig

@dataclass
class APIResponse:
    """Data class to hold API response"""
    status_code: int
    message: str
    data: Optional[Dict[str, Any]] = None

class BaseAPIClient(ABC):
    """Base class for API clients"""
    
    def __init__(self, base_url: str = None, jwt_token: str = None):
        """
        Initialize Base API Client
        
        Args:
            base_url (str, optional): Base URL of the API. Defaults to TestConfig.ADEURL
            jwt_token (str, optional): JWT token for authentication. Defaults to None
        """
        self.base_url = base_url or TestConfig.ADEURL
        self.jwt_token = jwt_token
        
    def _prepare_headers(self) -> Dict[str, str]:
        """Prepare headers for the API request"""
        headers = {
            'Content-Type': 'application/json'
        }
        if self.jwt_token:
            headers['Authorization'] = f'Bearer {self.jwt_token}'
        return headers
        
    def _prepare_payload_from_file(self, file_path: str) -> str:
        """
        Prepare payload from a JSON file
        
        Args:
            file_path (str): Path to the JSON file
            
        Returns:
            str: JSON string payload
            
        Raises:
            FileNotFoundError: If the file doesn't exist
            json.JSONDecodeError: If the file contains invalid JSON
        """
        try:
            with open(file_path, 'r') as file:
                return file.read()
        except FileNotFoundError:
            raise FileNotFoundError(f"Payload file not found: {file_path}")
        except json.JSONDecodeError as e:
            raise json.JSONDecodeError(f"Invalid JSON in file: {file_path}", e.doc, e.pos)
            
    def _make_request(self, method: str, endpoint: str, payload: Optional[str] = None) -> APIResponse:
        """
        Make an API request
        
        Args:
            method (str): HTTP method (GET, POST, etc.)
            endpoint (str): API endpoint
            payload (Optional[str]): Request payload
            
        Returns:
            APIResponse: Response from the API
        """
        try:
            url = urljoin(self.base_url, endpoint)
            response = requests.request(
                method,
                url,
                headers=self._prepare_headers(),
                data=payload
            )
            
            response_data = response.json()
            
            return APIResponse(
                status_code=response.status_code,
                message='Success' if response.status_code == 200 else response_data.get('message', 'Unknown error'),
                data=response_data if response.status_code == 200 else None
            )
            
        except requests.exceptions.RequestException as e:
            return APIResponse(
                status_code=500,
                message=f"Request failed: {str(e)}"
            )
        except json.JSONDecodeError as e:
            return APIResponse(
                status_code=500,
                message=f"Invalid JSON response: {str(e)}"
            ) 
    
    def generate_jwt_token(self, access_key, access_secret_key, basic_auth_user=None, basic_auth_pass=None):
        url = TestConfig.ADEURL + "/ims/api/v1/access_keys/login"
        request_body = {
            "access_key": access_key,
            "access_secret_key": access_secret_key
        }
        #auth = (basic_auth_user, basic_auth_pass) if basic_auth_user and basic_auth_pass else None
        response = requests.post(url, json=request_body)
        unittest.TestCase().assertEqual(response.status_code, 200, "Failed to generate JWT token")
        self.jwt_token = response.json().get("json_web_token")
        unittest.TestCase().assertIsNotNone(self.jwt_token, "JWT token is null")
        print("Generated JWT Token:", self.jwt_token)

    def get_jwt_token(self, access_key, access_secret_key,basic_auth_user=None, basic_auth_pass=None):
        if self.jwt_token is None:
            self.generate_jwt_token(access_key, access_secret_key,basic_auth_user, basic_auth_pass)
        print("Using JWT Token:", self.jwt_token)
        return self.jwt_token
     