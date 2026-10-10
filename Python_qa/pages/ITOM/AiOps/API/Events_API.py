import json
import requests
import os
import unittest
from utils.ui_utils import utilities
from pages.Base.base_api import BaseAPIClient
from test_config import TestConfig

class EventsAPI(BaseAPIClient):
    EVENTS_ENDPOINT = "/events-service/api/v1.0/events"
    CLOSE_EVENT_ENDPOINT="/events-service/api/v1.0/events/operations/close"
    def __init__(self):
        self.jwt_token = None
       

    def inject_event_ticket_for_bhom(self, access_key, access_secret_key, json_file_path,basic_auth_user=None, basic_auth_pass=None):
        # Load JSON and replace hostname placeholder
        with open(json_file_path, 'r') as f:
            content = f.read()
        content = content.replace("{hostname}", TestConfig.HOSTNAME)
        events_array = json.loads(content)
        
        url = TestConfig.ADEURL + self.EVENTS_ENDPOINT
        headers = {
            "Authorization": f"Bearer {self.get_jwt_token(access_key, access_secret_key,basic_auth_user, basic_auth_pass)}",
            "Content-Type": "application/json"
        }
        response = requests.post(url, headers=headers, json=events_array)
        return response

    def close_event_ticket_for_bhom(self, access_key, access_secret_key, json_file_path, event_IDs, basic_auth_user=None, basic_auth_pass=None):
        with open(json_file_path, "r") as f:
            request_body = json.load(f)
            # Update the eventIds field
            request_body["eventIds"] = event_IDs

        url = TestConfig.ADEURL + self.CLOSE_EVENT_ENDPOINT
        headers = {
            "Authorization": f"Bearer {self.get_jwt_token(access_key, access_secret_key, basic_auth_user, basic_auth_pass)}",
            "Content-Type": "application/json"
        }
       
        response = requests.post(url, headers=headers, json=request_body)
        return response

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
    
    