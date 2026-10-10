import os
from datetime import datetime
import pytest
from pytest_html import extras

class Reporting:
    def __init__(self, request):
        self.request = request
        # Handle cases where request.node is not available
        if hasattr(request, 'node'):
            self.test_name = request.node.name
        else:
            # For cases where we're in a hook or other context without a node
            self.test_name = getattr(request, 'name', 'Unknown Test')
            
        self.start_time = datetime.now()
        self.test_description = None
        
        # Store reporting instance in session for global access
        if hasattr(request, 'session'):
            request.session.reporting = self
        
    def log_info(self, message):
        """Log an info message"""
        message = str(message)
        if hasattr(self.request, 'node') and hasattr(self.request.node, 'extras'):
            self.request.node.extras.append(extras.html(f'<div class="log-info">{message}</div>'))
            
    def log_pass(self, message):
        """Log a pass message"""
        message = str(message)
        if hasattr(self.request, 'node') and hasattr(self.request.node, 'extras'):
            self.request.node.extras.append(extras.html(f'<div class="log-pass">{message}</div>'))
            
    def log_fail(self, message):
        """Log a fail message"""
        message = str(message)
        if hasattr(self.request, 'node') and hasattr(self.request.node, 'extras'):
            self.request.node.extras.append(extras.html(f'<div class="log-fail">{message}</div>'))
            
    def log_warning(self, message):
        """Log a warning message"""
        message = str(message)
        if hasattr(self.request, 'node') and hasattr(self.request.node, 'extras'):
            self.request.node.extras.append(extras.html(f'<div class="log-warning">{message}</div>'))

    def log_error(self, message):
        """Log an error message"""
        message = str(message)
        if hasattr(self.request, 'node') and hasattr(self.request.node, 'extras'):
            self.request.node.extras.append(extras.html(f'<div class="log-error">{message}</div>'))
            
    def add_screenshot(self, screenshot_path):
        """Add a screenshot to the report"""
        if os.path.exists(screenshot_path) and hasattr(self.request, 'node') and hasattr(self.request.node, 'extras'):
            self.request.node.extras.append(extras.image(screenshot_path))
            
    def add_metadata(self, key, value):
        """Add metadata to the report"""
        value = str(value)
        if hasattr(self.request, 'node') and hasattr(self.request.node, 'extras'):
            self.request.node.extras.append(extras.html(f'<div class="metadata"><strong>{key}:</strong> {value}</div>'))

    def log_description(self, description):
        """Log the test description"""
        self.test_description = description
        self.log_info(f"Test Description: {description}")

    def log_start(self):
        """Log the start of the test"""
        self.log_info(f"Test started at {self.start_time}")

    def log_end(self, result):
        """Log the end of the test"""
        end_time = datetime.now()
        duration = end_time - self.start_time
        self.log_info(f"Test ended at {end_time}")
        self.log_info(f"Test duration: {duration}")
        self.log_info(f"Test result: {result}")

    def log_test_name(self):
        """Log the test name"""
        self.log_info(f"Test Name: {self.test_name}")

    def log_test_description(self):
        """Log the test description"""
        if self.test_description:
            self.log_info(f"Test Description: {self.test_description}")

    def log_test_start(self):
        """Log the start of the test"""
        self.log_start()
        self.log_test_name()
        self.log_test_description()

    def log_test_end(self, result):
        """Log the end of the test"""
        self.log_end(result)

    def log_test_result(self, result):
        """Log the result of the test"""
        self.log_end(result)

    def log_test_failure(self, message):
        """Log a failure message"""
        self.log_fail(message)

    def log_test_warning(self, message):
        """Log a warning message"""
        self.log_warning(message)

    def log_test_screenshot(self, screenshot_path):
        """Log a screenshot of the test"""
        self.add_screenshot(screenshot_path)

    def log_test_metadata(self, key, value):
        """Log metadata for the test"""
        self.add_metadata(key, value) 