import os
import json
from datetime import datetime
import xml.etree.ElementTree as ET
from bs4 import BeautifulSoup
import shutil

class ReportMerger:
    def __init__(self, python_reports_dir, java_reports_dir, output_dir):
        self.python_reports_dir = python_reports_dir
        self.java_reports_dir = java_reports_dir
        self.output_dir = output_dir
        self.merged_report = None
        
    def merge_reports(self):
        """Merge Python and Java test reports into a single HTML report"""
        # Create output directory if it doesn't exist
        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)
            
        # Create merged report file
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        merged_report_path = os.path.join(self.output_dir, f'combined_report_{timestamp}.html')
        
        # Start creating the merged report
        self._create_merged_report_header(merged_report_path)
        
        # Process Python reports
        self._process_python_reports(merged_report_path)
        
        # Process Java reports
        self._process_java_reports(merged_report_path)
        
        # Close the merged report
        self._close_merged_report(merged_report_path)
        
        return merged_report_path
        
    def _create_merged_report_header(self, report_path):
        """Create the header section of the merged report"""
        header = f"""<!DOCTYPE html>
<html>
<head>
    <title>Combined Test Report</title>
    <style>
        body {{ font-family: Arial, sans-serif; margin: 20px; }}
        .section {{ margin: 20px 0; padding: 10px; border: 1px solid #ddd; }}
        .python {{ background-color: #f0f8ff; }}
        .java {{ background-color: #fff0f0; }}
        .summary {{ background-color: #f0fff0; }}
        table {{ width: 100%; border-collapse: collapse; }}
        th, td {{ padding: 8px; text-align: left; border: 1px solid #ddd; }}
        th {{ background-color: #f5f5f5; }}
        .pass {{ color: green; }}
        .fail {{ color: red; }}
        .skip {{ color: orange; }}
    </style>
</head>
<body>
    <h1>Combined Test Report</h1>
    <div class="summary">
        <h2>Summary</h2>
        <p>Generated on: {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}</p>
    </div>"""
        
        with open(report_path, 'w') as f:
            f.write(header)
            
    def _process_python_reports(self, report_path):
        """Process and add Python test reports to the merged report"""
        with open(report_path, 'a') as f:
            f.write('<div class="section python"><h2>Python Tests</h2>')
            
            # Process each Python report
            for report_file in os.listdir(self.python_reports_dir):
                if report_file.endswith('.html'):
                    report_path = os.path.join(self.python_reports_dir, report_file)
                    with open(report_path, 'r') as report:
                        soup = BeautifulSoup(report.read(), 'html.parser')
                        # Extract relevant information
                        test_results = soup.find_all('tr', class_=['passed', 'failed', 'skipped'])
                        for result in test_results:
                            f.write(str(result))
                            
            f.write('</div>')
            
    def _process_java_reports(self, report_path):
        """Process and add Java test reports to the merged report"""
        with open(report_path, 'a') as f:
            f.write('<div class="section java"><h2>Java Tests</h2>')
            
            # Process each Java report
            for report_file in os.listdir(self.java_reports_dir):
                if report_file.endswith('.xml'):
                    report_path = os.path.join(self.java_reports_dir, report_file)
                    tree = ET.parse(report_path)
                    root = tree.getroot()
                    
                    # Extract test results
                    for testcase in root.findall('.//testcase'):
                        f.write(f'<tr><td>{testcase.get("name")}</td><td>{testcase.get("classname")}</td>')
                        if testcase.find('failure') is not None:
                            f.write('<td class="fail">Failed</td></tr>')
                        elif testcase.find('skipped') is not None:
                            f.write('<td class="skip">Skipped</td></tr>')
                        else:
                            f.write('<td class="pass">Passed</td></tr>')
                            
            f.write('</div>')
            
    def _close_merged_report(self, report_path):
        """Close the merged report HTML file"""
        with open(report_path, 'a') as f:
            f.write('</body></html>') 