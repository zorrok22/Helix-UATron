from email.policy import default
import os
import sys

import importlib.util
from test_config import TestConfig

class EnvironmentUtil:
    def __init__(self, stack_name):
        self.stack_name = stack_name

    FILE_NAME = "test_config.py"

  #gets the stack specific data from Test_config.py
    def get_tag_value_old(self, tag) -> str:
        project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../"))
        sys.path.append(project_root)
        config_path = os.path.join(project_root, self.FILE_NAME)
        spec = importlib.util.spec_from_file_location("test_config", config_path)
        test_config = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(test_config)
        stack_config = getattr(test_config, 'STACK_CONFIG', {})
        stack_data = stack_config.get(self.stack_name, {})
        return stack_data.get(tag, "")
    
    def  get_tag_value(self,tag: str, stack_name: str = None, default=None):
      """
      Fetches a value from TestConfig.STACK_CONFIG for the given stack.
      
      Args:
          key (str): The key to fetch from the stack.
          stack_name (str): The stack name to use (default: TestConfig.STACKT_NAME).
          default: Value to return if key is not found (default: None).
      
      Returns:
          Value from STACK_CONFIG[stack_name][key] if exists, else default.
      """
      stack = stack_name or TestConfig.STACKT_NAME
      return TestConfig.STACK_CONFIG.get(stack, {}).get(tag, default)