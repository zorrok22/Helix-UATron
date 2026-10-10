"""
Logging Configuration for HelixGPT Python API

This module provides centralized logging configuration.
"""

import logging
import os
from datetime import datetime

def setup_logging(log_level=logging.INFO, log_file=None):
    """
    Setup logging configuration
    
    Args:
        log_level: Logging level (DEBUG, INFO, WARNING, ERROR)
        log_file: Optional log file path
    """
    
    # Create logs directory if it doesn't exist
    log_dir = "logs"
    if not os.path.exists(log_dir):
        os.makedirs(log_dir)
    
    # Default log file with timestamp
    if not log_file:
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        log_file = os.path.join(log_dir, f"helixgpt_api_{timestamp}.log")
    
    # Configure logging format
    log_format = '%(asctime)s - %(name)s - %(levelname)s - %(funcName)s:%(lineno)d - %(message)s'
    
    # Configure root logger
    logging.basicConfig(
        level=log_level,
        format=log_format,
        handlers=[
            logging.FileHandler(log_file),
            logging.StreamHandler()  # Also log to console
        ]
    )
    
    # Set specific loggers to appropriate levels
    logging.getLogger('urllib3').setLevel(logging.WARNING)
    logging.getLogger('requests').setLevel(logging.WARNING)
    
    logger = logging.getLogger(__name__)
    logger.info(f"Logging configured - Level: {logging.getLevelName(log_level)}")
    logger.info(f"Log file: {log_file}")
    
    return log_file

def enable_debug_logging():
    """Enable debug level logging"""
    return setup_logging(log_level=logging.DEBUG)

def get_logger(name):
    """Get a logger with the specified name"""
    return logging.getLogger(name)
