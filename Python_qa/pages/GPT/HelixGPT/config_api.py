import logging
import os
import ssl
import urllib3
import requests
from typing import Dict
from requests.adapters import HTTPAdapter


class ConfigAPI:
    """Configuration management class for HelixGPT API classes"""
    
    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        
    def load_config(self) -> Dict[str, str]:
        """
        Load configuration from properties.config file
        
        Returns:
            Dictionary containing configuration key-value pairs
        """
        config_dict = {}
        
        # Look for properties.config in the Java_qa folder
        config_path = os.path.join(os.path.dirname(__file__), '..', '..', '..', '..', 'Java_qa', 'properties.config')
        
        try:
            with open(config_path, 'r') as config_file:
                for line in config_file:
                    line = line.strip()
                    # Skip comments and empty lines
                    if line and not line.startswith('#') and '=' in line:
                        key, value = line.split('=', 1)
                        config_dict[key.strip()] = value.strip()
                        
            self.logger.info(f"Configuration loaded from: {config_path}")
            return config_dict
            
        except FileNotFoundError:
            self.logger.warning(f"Config file not found at: {config_path}. Using default values.")
            # Return default config for testing
            return {
                'IS_URL': 'https://is-server-1.example.com',
                'adminUserName': 'your_username',
                'adminPassword': 'your_password'
            }
        except Exception as e:
            self.logger.error(f"Error loading config file: {str(e)}")
            raise
            
    @staticmethod
    def get_config() -> Dict[str, str]:
        """
        Static method to get configuration
        
        Returns:
            Dictionary containing configuration key-value pairs
        """
        config_api = ConfigAPI()
        return config_api.load_config()
    
    @staticmethod
    def setup_ssl_session() -> requests.Session:
        """
        Setup and return a requests session with SSL bypass configuration.
        This is a reusable method for all API classes that need to make HTTPS requests
        with custom SSL settings.
        
        Returns:
            requests.Session: Configured session object with SSL bypass settings
        """
        logger = logging.getLogger('ConfigAPI.setup_ssl_session')
        session = requests.Session()
        
        # Disable SSL warnings
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        
        # Configure session with SSL settings
        session.verify = False
        
        try:
            # Create custom SSL context
            ssl_context = ssl.create_default_context()
            ssl_context.check_hostname = False
            ssl_context.verify_mode = ssl.CERT_NONE
            ssl_context.set_ciphers('DEFAULT:@SECLEVEL=1')
            
            class SSLAdapter(HTTPAdapter):
                """Custom HTTP adapter with SSL context"""
                def init_poolmanager(self, *args, **kwargs):
                    kwargs['ssl_context'] = ssl_context
                    return super().init_poolmanager(*args, **kwargs)
                    
            session.mount('https://', SSLAdapter())
            logger.info("SSL session configured successfully with custom SSL context")
            
        except Exception as e:
            logger.warning(f"Could not configure custom SSL context: {e}. Using basic SSL bypass.")
            # Fall back to basic SSL bypass (session.verify = False already set)
        
        return session
