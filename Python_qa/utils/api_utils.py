from typing import Dict, Optional
import requests
import json
from dataclasses import dataclass
from urllib.parse import urljoin
from abc import ABC, abstractmethod
from pages.Base.base_api import BaseAPIClient
from test_config import TestConfig
@dataclass
class TokenResponse:
    """Data class to hold token response information"""
    token: str
    status_code: int
    message: str

class TokenGenerator(ABC):
    """Abstract base class for token generators"""
    
    @abstractmethod
    def generate_token(self) -> TokenResponse:
        """Generate authentication token"""
        pass

class BHOMTokenGenerator(TokenGenerator):
    """Concrete implementation of BHOM token generator"""
    
    def __init__(self, base_url: str = None, access_key: str = None, access_secret: str = None):
        """
        Initialize BHOM token generator
        
        Args:
            base_url (str, optional): Base URL of the BHOM API. Defaults to TestConfig.API_BASE_URL
            access_key (str, optional): Access key for authentication. Defaults to TestConfig.API_ACCESS_KEY
            access_secret (str, optional): Access secret for authentication. Defaults to TestConfig.API_ACCESS_SECRET
        """
        
        self.base_url = base_url or TestConfig.ADEURL
        self.access_key = access_key or TestConfig.ADE_ACCESSKEY
        self.access_secret = access_secret or TestConfig.ADE_SECRETKEY
        self.endpoint = "/ims/api/v1/access_keys/login"
        
    def _prepare_headers(self) -> Dict[str, str]:
        """Prepare headers for the API request"""
        return {
            'Content-Type': 'application/json'
        }
        
    def _prepare_payload(self) -> str:
        """Prepare payload for the API request"""
        return json.dumps({
            "access_key": self.access_key,
            "access_secret_key": self.access_secret
        })
        
    def generate_token(self) -> TokenResponse:
        """
        Generate JWT token using BHOM API
        
        Returns:
            TokenResponse: Response containing token and status information
        """
        try:
            url = urljoin(self.base_url, self.endpoint)
            response = requests.post(
                url,
                headers=self._prepare_headers(),
                data=self._prepare_payload()
            )
            
            response_data = response.json()
            
            # Handle both list and dictionary responses
            if isinstance(response_data, list) and len(response_data) > 0:
                token = response_data[0].get('json_web_token', '')
            else:
                token = response_data.get('json_web_token', '')
            
            return TokenResponse(
                token=token,
                status_code=response.status_code,
                message='Success' if response.status_code == 200 else response_data.get('message', 'Unknown error')
            )
            
        except requests.exceptions.RequestException as e:
            return TokenResponse(
                token='',
                status_code=500,
                message=f"Request failed: {str(e)}"
            )
        except json.JSONDecodeError as e:
            return TokenResponse(
                token='',
                status_code=500,
                message=f"Invalid JSON response: {str(e)}"
            )

class TokenGeneratorFactory:
    """Factory class for creating token generators"""
    
    @staticmethod
    def create_token_generator(
        generator_type: str = 'bhom',
        base_url: str = None,
        access_key: str = None,
        access_secret: str = None
    ) -> Optional[TokenGenerator]:
        """
        Create a token generator based on the specified type
        
        Args:
            generator_type (str): Type of token generator to create. Defaults to 'bhom'
            base_url (str, optional): Base URL for the API. Defaults to TestConfig.API_BASE_URL
            access_key (str, optional): Access key for authentication. Defaults to TestConfig.API_ACCESS_KEY
            access_secret (str, optional): Access secret for authentication. Defaults to TestConfig.API_ACCESS_SECRET
            
        Returns:
            Optional[TokenGenerator]: Instance of the requested token generator
        """
        generators = {
            'bhom': BHOMTokenGenerator
        }
        
        generator_class = generators.get(generator_type.lower())
        if generator_class:
            return generator_class(base_url, access_key, access_secret)
        return None

def get_api_client() -> BaseAPIClient:
    """
    Get an API client with authentication
    
    Returns:
        BaseAPIClient: Initialized API client with JWT token
    """
    # Generate token
    token_generator = TokenGeneratorFactory.create_token_generator()
    if not token_generator:
        raise ValueError("Failed to create token generator")
        
    token_response = token_generator.generate_token()
    if token_response.status_code != 200:
        raise ValueError(f"Failed to generate token: {token_response.message}")
        
    # Create and return API client
    return BaseAPIClient(jwt_token=token_response.token) 