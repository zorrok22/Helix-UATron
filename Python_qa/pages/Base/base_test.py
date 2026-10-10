import unittest
import pytest
from selenium import webdriver
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.service import Service
from pages.ITOM.ADEPortal.ui_utils import login_to_product

class BaseTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        """Setup before all tests in the class"""
        # Setup Chrome driver using webdriver-manager
        service = Service(ChromeDriverManager().install())
        cls.driver = webdriver.Chrome(service=service)
        cls.driver.maximize_window()
    
    @classmethod
    def tearDownClass(cls):
        """Cleanup after all tests in the class"""
        if cls.driver:
            cls.driver.quit()
    
    def setUp(self):
        """Setup before each test"""
        pass
    
    def tearDown(self):
        """Cleanup after each test"""
        pass 