import requests
import urllib3
import ssl
import logging
from typing import Dict, Any


class LoginRequestAPI:
    """Python equivalent of LoginRequest_API Java class"""
    
    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.session = requests.Session()
        
        # Disable SSL warnings for testing environments
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        
        # Configure session with SSL settings for problematic environments
        self.session.verify = False
        
        # Create a custom SSL context to handle SSL issues
        try:
            # Set up more permissive SSL context
            ssl_context = ssl.create_default_context()
            ssl_context.check_hostname = False
            ssl_context.verify_mode = ssl.CERT_NONE
            ssl_context.set_ciphers('DEFAULT:@SECLEVEL=1')
            
            # Configure session adapter with custom SSL context
            from requests.adapters import HTTPAdapter
            from urllib3.util.ssl_ import create_urllib3_context
            
            class SSLAdapter(HTTPAdapter):
                def init_poolmanager(self, *args, **kwargs):
                    kwargs['ssl_context'] = ssl_context
                    return super().init_poolmanager(*args, **kwargs)
                    
            self.session.mount('https://', SSLAdapter())
            
        except Exception as e:
            self.logger.warning(f"Could not configure custom SSL context: {e}")
            # Fall back to basic SSL bypass
            pass
        
    def login_action(self, config: Dict[str, str]) -> Dict[str, str]:
        """
        Perform login action and return cookies
        
        Args:
            config: Configuration dictionary containing login details
            
        Returns:
            Dictionary of cookies from successful login
        """
        try:
            login_url = f"{config.get('IS_URL')}/api/rx/authentication/loginrequest"
            
            # Use correct config keys from properties.config
            login_payload = {
                "userName": config.get('ADE_USERNAME'),
                "password": config.get('ADE_PASSWORD')
            }
            
            # Debug logging
            self.logger.debug(f"Login URL: {login_url}")
            self.logger.debug(f"Username: {login_payload['userName']}")
            self.logger.debug(f"Password length: {len(login_payload['password']) if login_payload['password'] else 0}")
            
            headers = {
                "Content-Type": "application/json",
                "X-Requested-By": "XMLHttpRequest",
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
            }
            
            self.logger.debug(f"Request headers: {headers}")
            
            # Add timeout and additional SSL bypass options
            response = self.session.post(
                login_url, 
                json=login_payload,  # Use json parameter for application/json
                headers=headers,
                verify=False,  # Skip SSL verification
                timeout=30,    # Add timeout
                allow_redirects=True
            )
            
            self.logger.debug(f"Response status: {response.status_code}")
            self.logger.debug(f"Response headers: {dict(response.headers)}")
            self.logger.debug(f"Response content (first 200 chars): {response.text[:200]}")
            
            if response.status_code == 200:
                self.logger.info("Login successful")
                cookies = dict(response.cookies)
                self.logger.debug(f"Cookies received: {list(cookies.keys())}")
                return cookies
            else:
                self.logger.error(f"Login failed with status code: {response.status_code}")
                self.logger.error(f"Response content: {response.text}")
                raise Exception(f"Login failed: {response.text}")
                
        except requests.exceptions.SSLError as ssl_err:
            self.logger.error(f"SSL Error during login: {str(ssl_err)}")
            self.logger.info("Trying alternative SSL bypass...")
            
            # Try with alternative SSL settings
            try:
                response = requests.post(
                    login_url,
                    json=login_payload,  # Use json parameter for application/json
                    headers=headers,
                    verify=False,
                    timeout=30
                )
                
                if response.status_code == 200:
                    self.logger.info("Login successful with alternative method")
                    return dict(response.cookies)
                else:
                    raise Exception(f"Login failed with alternative method: {response.text}")
                    
            except Exception as alt_err:
                self.logger.error(f"Alternative login method also failed: {str(alt_err)}")
                raise ssl_err
                
        except requests.exceptions.Timeout:
            self.logger.error("Request timeout during login")
            raise Exception("Login request timed out")
            
        except requests.exceptions.ConnectionError as conn_err:
            self.logger.error(f"Connection error during login: {str(conn_err)}")
            raise Exception(f"Could not connect to server: {conn_err}")
            
        except Exception as e:
            self.logger.error(f"Exception during login: {str(e)}")
            raise
