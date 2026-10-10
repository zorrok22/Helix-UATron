# import unittest
# from ..utils.api_utils import get_api_client
# from ..base.base_api import BaseAPIClient

# class BaseAPITest(unittest.TestCase):
#     """Base class for API tests"""
    
#     @classmethod
#     def setUpClass(cls):
#         """Setup before all tests in the class"""
#         try:
#             cls.api_client = get_api_client()
#         except Exception as e:
#             cls.fail(f"Failed to initialize API client: {str(e)}")
    
#     def setUp(self):
#         """Setup before each test"""
#         pass
    
#     def tearDown(self):
#         """Cleanup after each test"""
#         pass 