import pytest
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import os
from datetime import datetime
import sys
from utils.reporting import Reporting
import chromedriver_autoinstaller
import base64
import time
import traceback
from typing import Dict, Optional
import re
from collections import defaultdict
import pytest_html

# Add Python_qa directory to Python path
python_qa_dir = os.path.dirname(os.path.abspath(__file__))
if python_qa_dir not in sys.path:
    sys.path.insert(0, python_qa_dir)

# Global storage for deepeval data
_current_test_data = {}

def pytest_configure(config):
    """Configure pytest"""
    # Create reports directory if it doesn't exist
    report_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), 'Python_qa', 'reports')
    if not os.path.exists(report_dir):
        os.makedirs(report_dir)
    
    # Create screenshots directory

def pytest_runtest_setup(item):
    """Clear global test data at the start of each test"""
    global _current_test_data
    _current_test_data.clear()

@pytest.fixture(scope="function")
def reporting(request):
    """Fixture to handle test reporting"""
    reporting = Reporting(request)
    if hasattr(request, 'node'):
        request.node.reporting = reporting
    return reporting

@pytest.fixture(scope="function")
def driver(request):
    """
    Fixture to create and manage WebDriver instance
    """
    try:
        # Install/update ChromeDriver
        chromedriver_autoinstaller.install()
        
        # Setup Chrome options
        chrome_options = webdriver.ChromeOptions()
        chrome_options.add_argument('--start-maximized')
        chrome_options.add_argument('--disable-gpu')
        chrome_options.add_argument('--no-sandbox')
        chrome_options.add_argument('--disable-dev-shm-usage')
        chrome_options.add_argument('--remote-debugging-port=9222')
        chrome_options.add_argument("--disable-notifications")
        chrome_options.add_argument("--disable-infobars")
        chrome_options.add_argument("--disable-features=ExternalProtocolDialog")
        chrome_options.add_experimental_option("prefs", {
            "profile.default_content_setting_values.notifications": 2,  # Block notifications
            "profile.default_content_setting_values.media_stream_mic": 2,  # Block mic
            "profile.default_content_setting_values.media_stream_camera": 2,  # Block camera
             "protocol_handler.excluded_schemes": {
             "msteams": False,
             "zoommtg": False,
              "skype": False
    }
             })   
        # Initialize Chrome driver with local ChromeDriver
        driver_path = chromedriver_autoinstaller.install()
        service = Service(driver_path)
        driver = webdriver.Chrome(service=service, options=chrome_options)
        driver.implicitly_wait(10)
        
        # Store driver in request for cleanup
        request.node.driver = driver
        
        # Yield the driver to the test
        yield driver
        
    except Exception as e:
        print(f"Error initializing WebDriver: {str(e)}")
        raise
    finally:
        # Teardown - Close driver after each test
        if 'driver' in locals() and driver:
            try:
                driver.quit()
                print(f"Driver closed successfully for test: {request.node.name}")
            except Exception as e:
                print(f"Error closing WebDriver: {str(e)}")
                # Attempt to force close if normal quit fails
                try:
                    driver.close()
                except:
                    pass

def take_screenshot(driver, item, reporting, context=""):
    """Take a screenshot and store it"""
    try:
        # Check if driver is still alive
        if driver is None:
            reporting.log_warning("Driver is None, cannot take screenshot")
            return False
        
        # Try to check if driver session is still active
        try:
            driver.current_url  # This will fail if session is closed
        except Exception as session_error:
            reporting.log_warning(f"Driver session is not active: {str(session_error)}")
            return False
        
        # Wait a moment for any animations to complete
        time.sleep(1)
        
        # Take screenshot
        screenshot = driver.get_screenshot_as_base64()
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        
        # Initialize screenshots list if not present
        if not hasattr(item, 'screenshots'):
            item.screenshots = []
        
        # Store screenshot with context
        item.screenshots.append((timestamp, screenshot, context))
        
        # Save screenshot to file for backup
        screenshot_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), 'Python_qa', 'reports', 'screenshots')
        if not os.path.exists(screenshot_dir):
            os.makedirs(screenshot_dir)
        
        test_name = item.name
        screenshot_filename = f'{test_name}_{timestamp.replace(":", "-")}_{context}.png'
        screenshot_path = os.path.join(screenshot_dir, screenshot_filename)
        
        # Ensure the screenshot is valid before saving
        if not screenshot:
            raise Exception("Screenshot data is empty")
            
        with open(screenshot_path, 'wb') as f:
            f.write(base64.b64decode(screenshot))
        
        # Verify the file was created
        if not os.path.exists(screenshot_path):
            raise Exception(f"Screenshot file was not created at {screenshot_path}")
            
        reporting.log_info(f"Screenshot captured: {screenshot_filename}")
        return True
        
    except Exception as e:
        error_msg = f"Failed to capture screenshot: {str(e)}\n{traceback.format_exc()}"
        reporting.log_warning(error_msg)
        print(error_msg)  # Print to console for immediate feedback
        return False

def capture_deepeval_data(context=None, test_input=None, expected_output=None, actual_output=None, hallucination_score=None, relevancy_score=None, metric_scores=None):
    """Capture deepeval data for the current test (can be called from anywhere)"""
    global _current_test_data
    if context is not None:
        _current_test_data['context'] = context
    if test_input is not None:
        _current_test_data['test_input'] = test_input
    if expected_output is not None:
        _current_test_data['expected_output'] = expected_output
    if actual_output is not None:
        _current_test_data['actual_output'] = actual_output
    if hallucination_score is not None:
        _current_test_data['hallucination_score'] = hallucination_score
    if relevancy_score is not None:
        _current_test_data['relevancy_score'] = relevancy_score
    if metric_scores is not None:
        _current_test_data['metric_scores'] = metric_scores

def store_deepeval_scores(item, hallucination_score: Optional[float] = None, relevancy_score: Optional[float] = None, overall_metric_pass: Optional[bool] = None, metric_scores: Optional[dict] = None, context: Optional[str] = None, test_input: Optional[str] = None, expected_output: Optional[str] = None, actual_output: Optional[str] = None):
    """Store DeepEval scores and test case data in the test item for reporting"""
    global _current_test_data
    
    if not hasattr(item, 'deepeval_scores'):
        item.deepeval_scores = {}
    
    # Use provided values or fall back to globally captured data
    if hallucination_score is not None:
        item.deepeval_scores['hallucination'] = hallucination_score
    elif 'hallucination_score' in _current_test_data:
        item.deepeval_scores['hallucination'] = _current_test_data['hallucination_score']
        
    if relevancy_score is not None:
        item.deepeval_scores['relevancy'] = relevancy_score
    elif 'relevancy_score' in _current_test_data:
        item.deepeval_scores['relevancy'] = _current_test_data['relevancy_score']
        
    if overall_metric_pass is not None:
        item.deepeval_scores['overall_metric_pass'] = overall_metric_pass
    else:
        # Calculate from scores
        h_score = hallucination_score or _current_test_data.get('hallucination_score')
        r_score = relevancy_score or _current_test_data.get('relevancy_score')
        if h_score is not None and r_score is not None:
            item.deepeval_scores['overall_metric_pass'] = (h_score <= 0.3 and r_score >= 0.7)
        
    if metric_scores is not None:
        item.deepeval_scores['metric_scores'] = metric_scores
    elif 'metric_scores' in _current_test_data:
        item.deepeval_scores['metric_scores'] = _current_test_data['metric_scores']
        
    if context is not None:
        item.deepeval_scores['context'] = context
    elif 'context' in _current_test_data:
        item.deepeval_scores['context'] = _current_test_data['context']
        
    if test_input is not None:
        item.deepeval_scores['test_input'] = test_input
    elif 'test_input' in _current_test_data:
        item.deepeval_scores['test_input'] = _current_test_data['test_input']
        
    if expected_output is not None:
        item.deepeval_scores['expected_output'] = expected_output
    elif 'expected_output' in _current_test_data:
        item.deepeval_scores['expected_output'] = _current_test_data['expected_output']
        
    if actual_output is not None:
        item.deepeval_scores['actual_output'] = actual_output
    elif 'actual_output' in _current_test_data:
        item.deepeval_scores['actual_output'] = _current_test_data['actual_output']

def create_html_report(screenshots, test_name, test_status, error_message=None, deepeval_scores=None):
    """Create a simple HTML report with embedded screenshots and DeepEval scores"""
    html_template = """
    <!DOCTYPE html>
    <html>
    <head>
        <title>Test Report</title>
        <style>
            body {{ font-family: Arial, sans-serif; margin: 20px; }}
            .test-info {{ margin-bottom: 20px; }}
            .pass {{ color: green; }}
            .fail {{ color: red; }}
            .screenshot {{ margin: 20px 0; padding: 10px; border: 1px solid #ddd; }}
            .screenshot img {{ max-width: 100%; height: auto; display: block; margin: 10px 0; }}
            .screenshot h3 {{ margin: 0 0 10px 0; }}
            .error {{ color: red; margin: 20px 0; padding: 10px; border: 1px solid red; }}
            .metrics {{ margin: 20px 0; padding: 10px; border: 1px solid #ddd; background-color: #f9f9f9; }}
            .metric {{ margin: 5px 0; }}
            .metric-good {{ color: green; }}
            .metric-bad {{ color: red; }}
        </style>
    </head>
    <body>
        <h1>Test Report</h1>
        <div class="test-info">
            <h2>Test: {test_name}</h2>
            <p class="{status_class}">Status: {test_status}</p>
            {error_info}
        </div>
        {metrics_html}
        <div class="screenshots">
            <h2>Screenshots</h2>
            {screenshots_html}
        </div>
    </body>
    </html>
    """
    
    error_info = f"<div class='error'><strong>Error:</strong> {error_message}</div>" if error_message else ""
    status_class = "pass" if test_status == "PASS" else "fail"
    
    # Generate metrics HTML as a table (dynamic columns)
    metrics_html = ""
    if deepeval_scores:
        metric_scores = deepeval_scores.get('metric_scores', {})
        if metric_scores:
            metrics_html = (
                '<div class="metrics">'
                '<h2>DeepEval Metrics</h2>'
                '<table class="metric-table">'
                '<tr>'
            )
            for metric_name in metric_scores:
                metrics_html += f"<th>{metric_name} Score</th>"
            metrics_html += "</tr><tr>"
            for score in metric_scores.values():
                metrics_html += f"<td>{score:.2f}</td>"
            metrics_html += "</tr></table></div>"
    
    screenshots_html = ""
    if screenshots:
        for timestamp, screenshot, context in screenshots:
            try:
                # Create relative path for screenshot
                screenshot_dir = os.path.join('Python_qa', 'reports', 'screenshots')
                screenshot_filename = f'{test_name}_{timestamp.replace(":", "-")}_{context}.png'
                relative_path = os.path.join(screenshot_dir, screenshot_filename)
                
                # Add both base64 and file reference
                screenshots_html += f"""
                <div class="screenshot">
                    <h3>Screenshot at {timestamp} - {context}</h3>
                    <img src="data:image/png;base64,{screenshot}" alt="Screenshot at {timestamp}" />
                    <p>File location: {relative_path}</p>
                </div>
                """
            except Exception as e:
                screenshots_html += f"""
                <div class="screenshot">
                    <h3>Failed to process screenshot at {timestamp} - {context}</h3>
                    <p>Error: {str(e)}</p>
                </div>
                """
    else:
        screenshots_html = "<p>No screenshots were captured during the test.</p>"
    
    return html_template.format(
        test_name=test_name,
        test_status=test_status,
        status_class=status_class,
        error_info=error_info,
        metrics_html=metrics_html,
        screenshots_html=screenshots_html
    )

test_results_summary = []
test_details_html = []
@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Handle test result reporting and screenshots"""
    outcome = yield
    report = outcome.get_result()
    
    # Get the reporting instance
    reporting = getattr(item, 'reporting', None)
    if reporting is None:
        reporting = Reporting(item)
        item.reporting = reporting
    
    # Initialize screenshots list if not present
    if not hasattr(item, 'screenshots'):
        item.screenshots = []
    
    try:
        # Log test start
        if call.when == "setup":
            reporting.log_test_start()
            if hasattr(item, 'driver'):
                take_screenshot(item.driver, item, reporting, "Test Setup")
        
        # Handle test result
        if call.when == "call":
            if hasattr(item, 'driver'):
                driver = item.driver
                if driver:
                    # Take screenshot at test execution
                    take_screenshot(item.driver, item, reporting, "Test Execution")
            
            if call.excinfo is not None:
                # Test failed
                error_message = str(call.excinfo.value)
                reporting.log_test_failure(error_message)
                # Take screenshot on failure
                if hasattr(item, 'driver'):
                    take_screenshot(item.driver, item, reporting, "Test Failure")
            else:
                # Test passed
                reporting.log_test_result("PASS")
            
            # --- Add DeepEval Metric Score to pytest-html report (ytest.html) ---
            deepeval_scores = getattr(item, 'deepeval_scores', None)
            if deepeval_scores:
                if 'hallucination' in deepeval_scores and 'relevancy' in deepeval_scores and 'overall_metric_pass' in deepeval_scores:
                    pass_status = "PASS" if deepeval_scores['overall_metric_pass'] else "FAIL"
                    metric_score = f"H: {deepeval_scores['hallucination']:.2f}, R: {deepeval_scores['relevancy']:.2f}, O: {pass_status}"
                else:
                    metric_score = "N/A"
            else:
                metric_score = "N/A"
            if hasattr(report, 'extra'):
                report.extra.append(pytest_html.extras.text(metric_score, name="DeepEval Metric Score"))
            # --- End pytest-html integration ---
        
        # Generate report at the end of the test
        if call.when == "teardown":
            try:
                # Get test status and error message
                test_status = "PASS" if not hasattr(call, 'excinfo') or call.excinfo is None else "FAIL"
                error_message = str(call.excinfo.value) if hasattr(call, 'excinfo') and call.excinfo is not None else None
                
                # Take final screenshot
                if hasattr(item, 'driver'):
                    take_screenshot(item.driver, item, reporting, "Test Teardown")
                
                # Verify we have screenshots before creating report
                if not item.screenshots:
                    print("Warning: No screenshots were captured during the test")
                
                # Create HTML report
                deepeval_scores = getattr(item, 'deepeval_scores', None)
                
                # If no deepeval_scores found, try to use globally captured data
                if not deepeval_scores and _current_test_data:
                    print("Using globally captured test data for model comparison report")
                    store_deepeval_scores(item)  # This will use the globally captured data
                    deepeval_scores = getattr(item, 'deepeval_scores', None)
                
                print("DEBUG: deepeval_scores in makereport:", deepeval_scores)
                html_content = create_html_report(
                    item.screenshots,
                    item.name,
                    test_status,
                    error_message,
                    deepeval_scores
                )
                test_details_html.append(html_content)
                
                test_results_summary.append({
                    "test_name": item.name,
                    "status": test_status,
                    "error_message": error_message,
                    "metric_scores": deepeval_scores.get('metric_scores', {}) if deepeval_scores else {},
                    "context": deepeval_scores.get('context', 'N/A') if deepeval_scores else 'N/A',
                    "test_input": deepeval_scores.get('test_input', 'N/A') if deepeval_scores else 'N/A',
                    "expected_output": deepeval_scores.get('expected_output', 'N/A') if deepeval_scores else 'N/A',
                    "actual_output": deepeval_scores.get('actual_output', 'N/A') if deepeval_scores else 'N/A',
                    "hallucination_score": deepeval_scores.get('hallucination', None) if deepeval_scores else None,
                    "relevancy_score": deepeval_scores.get('relevancy', None) if deepeval_scores else None
                })
            except Exception as e:
                error_msg = f"Failed to generate test report: {str(e)}\n{traceback.format_exc()}"
                print(error_msg)
                reporting.log_error(error_msg)
            
            reporting.log_test_end(test_status)
            
    except Exception as e:
        error_msg = f"Error in pytest_runtest_makereport: {str(e)}\n{traceback.format_exc()}"
        print(error_msg)
        reporting.log_error(error_msg)

def write_deepeval_model_report(test_results_summary):
    """Generate a deepeval.html report grouped by use case and model/config, showing metric scores."""
    from collections import defaultdict
    import os

    # Group by use case and model/config
    grouped = defaultdict(lambda: defaultdict(list))
    all_metric_names = set()
    for result in test_results_summary:
        # Extract use case and model/config (provider_config_key or similar)
        test_name = result['test_name']
        # Try to extract model/config from test name if parametrize is used: test_func[param]
        if '[' in test_name:
            usecase = test_name.split('[')[0]
            model = test_name.split('[')[1].rstrip(']')
        else:
            usecase = test_name
            model = 'default'
        grouped[usecase][model].append(result)
        all_metric_names.update(result.get('metric_scores', {}).keys())
    all_metric_names = sorted(all_metric_names)

    # Build HTML
    html = [
        '<!DOCTYPE html>',
        '<html><head><title>Model Comparison Report</title>',
        '<meta charset="utf-8"/>',
        '<style>body { font-family: Arial, sans-serif; margin: 20px; }',
        'table { border-collapse: collapse; width: 100%; margin-bottom: 40px; }',
        'th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }',
        'th { background: #f0f0f0; }',
        '.pass { color: green; } .fail { color: red; }',
        '</style></head><body>',
        '<h1>Model Response Comparison</h1>'
    ]
    for usecase, models in grouped.items():
        html.append(f'<h2>Use Case: {usecase}</h2>')
        html.append('<table>')
        html.append('<tr><th>Model/Config</th>')
        for metric_name in all_metric_names:
            html.append(f'<th>{metric_name} Score</th>')
        html.append('<th>Status</th><th>Error Message</th></tr>')
        for model, results in models.items():
            for result in results:
                html.append(f'<tr><td>{model}</td>')
                metric_scores = result.get('metric_scores', {})
                for metric_name in all_metric_names:
                    score = metric_scores.get(metric_name)
                    if score is not None:
                        html.append(f'<td>{score:.2f}</td>')
                    else:
                        html.append('<td>N/A</td>')
                status = result['status']
                status_class = 'pass' if status == 'PASS' else 'fail'
                html.append(f'<td class="{status_class}">{status}</td>')
                html.append(f'<td>{result["error_message"] or ""}</td></tr>')
        html.append('</table>')
    html.append('</body></html>')

    report_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'reports')
    report_path = os.path.join(report_dir, 'deepeval.html')
    with open(report_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(html))
    print(f"\nDeepEval model-wise report written to: {report_path}")

def pytest_sessionfinish(session, exitstatus):
    # Group results by use case (test function name before '[' if parametrize is used)
    grouped_results = defaultdict(list)
    all_metric_names = set()
    for result in test_results_summary:
        # Extract base test name (before '[' for parametrize)
        if '[' in result['test_name']:
            base_name = result['test_name'].split('[')[0]
            param = result['test_name'].split('[')[1].rstrip(']')
        else:
            base_name = result['test_name']
            param = ''
        grouped_results[base_name].append((param, result))
        # Collect all metric names
        metric_scores = result.get('metric_scores', {})
        all_metric_names.update(metric_scores.keys())
    all_metric_names = sorted(all_metric_names)

    summary_html = """
    <h1>DeepEval Test Summary</h1>
    """
    for usecase, runs in grouped_results.items():
        summary_html += f'<h2>Use Case: {usecase}</h2>'
        summary_html += (
            '<table border="1" style="border-collapse: collapse; width: 100%;">'
            '<tr>'
            '<th>Run</th>'
            '<th>Status</th>'
        )
        for metric_name in all_metric_names:
            summary_html += f"<th>{metric_name} Score</th>"
        summary_html += "<th>Metric Score</th><th>Error Message</th></tr>"
        for param, result in runs:
            status = f'<span style="color:green;">{result["status"]}</span>' if result["status"] == "PASS" else f'<span style="color:red;">{result["status"]}</span>'
            summary_html += f"<tr><td>{param or '-'}" + f"</td><td>{status}</td>"
            metric_scores = result.get('metric_scores', {})
            for metric_name in all_metric_names:
                score = metric_scores.get(metric_name)
                if score is not None:
                    summary_html += f"<td>{score:.2f}</td>"
                else:
                    summary_html += "<td>N/A</td>"
            # Add Metric Score summary cell (e.g., average)
            if metric_scores:
                avg_score = sum(metric_scores.values()) / len(metric_scores)
                summary_html += f"<td>{avg_score:.2f}</td>"
            else:
                summary_html += "<td>N/A</td>"
            summary_html += f"<td>{result['error_message'] or ''}</td></tr>"
        summary_html += "</table><hr/>"

    # Write the full HTML report (summary + all test details)
    full_html = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <title>DeepEval Test Report</title>
        <meta charset=\"utf-8\"/>
    </head>
    <body>
    {summary_html}
    {''.join(test_details_html)}
    </body>
    </html>
    """
    report_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'reports')
    report_path = os.path.join(report_dir, 'report.html')
    with open(report_path, 'w', encoding='utf-8') as f:
        f.write(full_html)
    print(f"\nDeepEval summary table and details written to: {report_path}")

    # Call the new function to write the deepeval model report
    write_deepeval_model_report(test_results_summary)
    
    # Generate model comparison report
    try:
        from utils.model_comparison_report import generate_model_comparison_report
        model_comparison_path = generate_model_comparison_report(test_results_summary)
        print(f"\nModel Comparison Report generated: {model_comparison_path}")
    except Exception as e:
        print(f"\nError generating model comparison report: {str(e)}")
        import traceback
        traceback.print_exc()

def pytest_html_results_table_header(cells):
    cells.append("DeepEval Metric Score")

def pytest_html_results_table_row(report, cells):
    metric_score = "N/A"
    for extra in getattr(report, 'extra', []):
        if getattr(extra, 'name', None) == "DeepEval Metric Score":
            metric_score = extra.content
    cells.append(metric_score)
