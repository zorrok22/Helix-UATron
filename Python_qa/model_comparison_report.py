import os
import json
from datetime import datetime
from typing import Dict, List, Any

class ModelComparisonReportGenerator:
    """
    Generates a Model Comparison Report for AI provider testing results.
    Format includes multiple AI providers with their respective scores and outputs.
    """
    
    def __init__(self):
        self.providers = [
            "Azure OpenAI - GPT-4-turbo",
            "GCP Vertex - gemini-2.0-flash-001", 
            "Azure OpenAI - GPT-4.1-helixgpt",
            "Oracle Cloud - llama-3.1-70b-instruct",
            "Google Vertex AI"
        ]
        self.test_results = []
        
    def add_test_result(self, test_data: Dict[str, Any]):
        """
        Add a test result for the model comparison report.
        
        Args:
            test_data: Dictionary containing test information including:
                - ira_link: IRA reference link
                - test_summary: Brief description of the test
                - context: Test context
                - input: Test input
                - expected_output: Expected test output
                - provider_results: Dict of provider results {provider_name: {score, pass_fail, actual_output}}
        """
        self.test_results.append(test_data)
    
    def extract_provider_from_config(self, config_key: str) -> str:
        """
        Map configuration keys to provider display names.
        """
        provider_mapping = {
            "azure_config": "Azure OpenAI - GPT-4-turbo",
            "google_vertex_config": "GCP Vertex - gemini-2.0-flash-001",
            "azure_helixgpt_config": "Azure OpenAI - GPT-4.1-helixgpt",
            "oracle_config": "Oracle Cloud - llama-3.1-70b-instruct",
            "google_vertex_ai_config": "Google Vertex AI"
        }
        return provider_mapping.get(config_key, config_key)
    
    def generate_html_report(self, output_path: str = None) -> str:
        """
        Generate the Model Comparison Report as an HTML file.
        
        Args:
            output_path: Optional path to save the report. If None, saves to reports/model_comparison_report.html
            
        Returns:
            Path to the generated report file
        """
        if output_path is None:
            report_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'reports')
            os.makedirs(report_dir, exist_ok=True)
            output_path = os.path.join(report_dir, 'model_comparison_report.html')
        
        html_content = self._generate_html_content()
        
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(html_content)
        
        print(f"Model Comparison Report generated: {output_path}")
        return output_path
    
    def _generate_html_content(self) -> str:
        """Generate the HTML content for the report."""
        
        html = [
            '<!DOCTYPE html>',
            '<html lang="en">',
            '<head>',
            '    <meta charset="UTF-8">',
            '    <meta name="viewport" content="width=device-width, initial-scale=1.0">',
            '    <title>Model Comparison Report</title>',
            '    <style>',
            self._get_css_styles(),
            '    </style>',
            '</head>',
            '<body>',
            '    <div class="container">',
            '        <header>',
            '            <h1>Model Comparison Report</h1>',
            f'            <p class="timestamp">Generated on: {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}</p>',
            '        </header>',
            '        <div class="table-container">',
            '            <table class="comparison-table">',
            '                <thead>',
            '                    <tr>',
            '                        <th>JIRA Link</th>',
            '                        <th>Test Summary</th>',
            '                        <th>Context</th>',
            '                        <th>Input</th>',
            '                        <th>Expected Output</th>'
        ]
        
        # Add specific provider columns as requested
        html.extend([
            '                        <th class="provider-column">Azure OpenAI - GPT-4-turbo</th>',
            '                        <th class="provider-column">GCP Vertex - gemini-2.0-flash-001</th>',
            '                        <th class="provider-column">Azure OpenAI - GPT-4.1-helixgpt</th>',
            '                        <th class="provider-column">Oracle Cloud - llama-3.1-70b-instruct</th>',
            '                        <th class="provider-column">Google Vertex AI</th>'
        ])
        
        html.extend([
            '                    </tr>',
            '                </thead>',
            '                <tbody>'
        ])
        
        # Add test result rows
        for test_result in self.test_results:
            html.append('                    <tr>')
            html.append(f'                        <td class="jira-link"><a href="{test_result.get("jira_link", "#")}" target="_blank">{test_result.get("jira_link", "N/A")}</a></td>')
            html.append(f'                        <td class="test-summary">{test_result.get("test_summary", "N/A")}</td>')
            html.append(f'                        <td class="context">{test_result.get("context", "N/A")}</td>')
            html.append(f'                        <td class="input">{test_result.get("input", "N/A")}</td>')
            html.append(f'                        <td class="expected-output">{test_result.get("expected_output", "N/A")}</td>')
            
            # Add specific provider result columns
            provider_results = test_result.get("provider_results", {})
            specific_providers = [
                "Azure OpenAI - GPT-4-turbo",
                "GCP Vertex - gemini-2.0-flash-001", 
                "Azure OpenAI - GPT-4.1-helixgpt",
                "Oracle Cloud - llama-3.1-70b-instruct",
                "Google Vertex AI"
            ]
            
            for provider in specific_providers:
                result = provider_results.get(provider, {})
                pass_fail = result.get("pass_fail", "N/A")
                actual_output = result.get("actual_output", "N/A")
                hallucination_score = result.get("hallucination_score", None)
                relevancy_score = result.get("relevancy_score", None)
                
                # Debug: Print what we're processing
                print(f"Processing provider {provider}: pass_fail={pass_fail}, h_score={hallucination_score}, r_score={relevancy_score}")
                
                # Format the cell content with detailed metrics
                cell_class = 'pass' if pass_fail == 'PASS' else 'fail' if pass_fail == 'FAIL' else 'na'
                html.append(f'                        <td class="provider-result {cell_class}">')
                
                # Display test result prominently
                html.append(f'                            <div class="test-result">')
                html.append(f'                                <div class="status">{pass_fail}</div>')
                html.append(f'                            </div>')
                
                # Display individual scores
                html.append(f'                            <div class="metrics">')
                if relevancy_score is not None:
                    relevancy_status = "PASS" if relevancy_score > 0.7 else "FAIL"
                    relevancy_class = "metric-pass" if relevancy_score > 0.7 else "metric-fail"
                    html.append(f'                                <div class="metric-item {relevancy_class}">')
                    html.append(f'                                    <strong>Answer Relevancy:</strong> {relevancy_score:.3f} ({relevancy_status})')
                    html.append(f'                                </div>')
                else:
                    html.append(f'                                <div class="metric-item metric-na">')
                    html.append(f'                                    <strong>Answer Relevancy:</strong> N/A')
                    html.append(f'                                </div>')
                
                if hallucination_score is not None:
                    hallucination_status = "PASS" if hallucination_score <= 0.3 else "FAIL"
                    hallucination_class = "metric-pass" if hallucination_score <= 0.3 else "metric-fail"
                    html.append(f'                                <div class="metric-item {hallucination_class}">')
                    html.append(f'                                    <strong>Hallucination:</strong> {hallucination_score:.3f} ({hallucination_status})')
                    html.append(f'                                </div>')
                else:
                    html.append(f'                                <div class="metric-item metric-na">')
                    html.append(f'                                    <strong>Hallucination:</strong> N/A')
                    html.append(f'                                </div>')
                html.append(f'                            </div>')
                
                # Display actual output
                html.append(f'                            <div class="actual-output">')
                html.append(f'                                <strong>Actual Output:</strong>')
                html.append(f'                                <div class="output-content">{actual_output}</div>')
                html.append(f'                            </div>')
                html.append('                        </td>')
            
            html.append('                    </tr>')
        
        html.extend([
            '                </tbody>',
            '            </table>',
            '        </div>',
            '    </div>',
            '</body>',
            '</html>'
        ])
        
        return '\n'.join(html)
    
    def _get_css_styles(self) -> str:
        """Return CSS styles for the report."""
        return '''
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 100%;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            overflow: hidden;
        }
        
        header {
            background: linear-gradient(45deg, #2c3e50, #3498db);
            color: white;
            padding: 30px;
            text-align: center;
        }
        
        header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        
        .timestamp {
            font-size: 1.1em;
            opacity: 0.9;
        }
        
        .table-container {
            overflow-x: auto;
            padding: 20px;
        }
        
        .comparison-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .comparison-table th {
            background: linear-gradient(45deg, #34495e, #2c3e50);
            color: white;
            padding: 15px 10px;
            text-align: left;
            font-weight: 600;
            font-size: 0.9em;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .provider-column {
            min-width: 200px;
            background: linear-gradient(45deg, #27ae60, #2ecc71) !important;
        }
        
        .comparison-table td {
            padding: 12px 10px;
            border-bottom: 1px solid #ecf0f1;
            vertical-align: top;
            font-size: 0.85em;
            line-height: 1.4;
        }
        
        .comparison-table tr:hover {
            background-color: #f8f9fa;
        }
        
        .jira-link a {
            color: #3498db;
            text-decoration: none;
            font-weight: 500;
        }
        
        .jira-link a:hover {
            text-decoration: underline;
        }
        
        .test-summary {
            max-width: 200px;
            word-wrap: break-word;
        }
        
        .context, .input, .expected-output {
            max-width: 250px;
            word-wrap: break-word;
            font-size: 0.8em;
        }
        
        .provider-result {
            min-width: 280px;
            max-width: 320px;
            padding: 12px;
            vertical-align: top;
        }
        
        .test-result {
            text-align: center;
            margin-bottom: 10px;
        }
        
        .provider-result .status {
            padding: 6px 12px;
            border-radius: 4px;
            font-weight: bold;
            font-size: 0.9em;
            display: inline-block;
            margin-bottom: 8px;
        }
        
        .provider-result.pass .status {
            background-color: #d4edda;
            color: #155724;
        }
        
        .provider-result.fail .status {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        .provider-result.na .status {
            background-color: #e2e3e5;
            color: #6c757d;
        }
        
        .metrics {
            border: 1px solid #e9ecef;
            border-radius: 6px;
            padding: 8px;
            margin-bottom: 10px;
            background-color: #f8f9fa;
        }
        
        .metric-item {
            padding: 4px 6px;
            margin: 3px 0;
            border-radius: 3px;
            font-size: 0.75em;
            line-height: 1.4;
        }
        
        .metric-item strong {
            display: inline-block;
            min-width: 80px;
            font-size: 0.7em;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .metric-pass {
            background-color: #d1ecf1;
            color: #0c5460;
            border-left: 3px solid #17a2b8;
        }
        
        .metric-fail {
            background-color: #f8d7da;
            color: #721c24;
            border-left: 3px solid #dc3545;
        }
        
        .metric-na {
            background-color: #e2e3e5;
            color: #6c757d;
            border-left: 3px solid #6c757d;
        }
        
        .actual-output {
            border-top: 1px solid #e9ecef;
            padding-top: 8px;
        }
        
        .actual-output strong {
            display: block;
            font-size: 0.75em;
            color: #495057;
            margin-bottom: 5px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .output-content {
            font-size: 0.7em;
            line-height: 1.4;
            color: #6c757d;
            word-wrap: break-word;
            overflow-wrap: break-word;
            max-height: 120px;
            overflow-y: auto;
            padding: 6px;
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 4px;
        }
        
        /* Responsive design */
        @media (max-width: 768px) {
            body {
                padding: 10px;
            }
            
            header h1 {
                font-size: 2em;
            }
            
            .comparison-table th,
            .comparison-table td {
                padding: 8px 5px;
                font-size: 0.75em;
            }
            
            .provider-result {
                min-width: 250px;
                max-width: 270px;
            }
            
            .output-content {
                font-size: 0.65em;
                max-height: 80px;
            }
            
            .metric-item {
                font-size: 0.7em;
            }
        }
        
        @media (max-width: 480px) {
            .provider-result {
                min-width: 200px;
                max-width: 220px;
            }
            
            .output-content {
                font-size: 0.6em;
                max-height: 60px;
            }
            
            .metric-item {
                font-size: 0.65em;
                padding: 3px 4px;
            }
        }
        '''
    
    def process_pytest_results(self, test_results_summary: List[Dict]) -> None:
        """
        Process pytest results and convert them to model comparison format.
        
        Args:
            test_results_summary: List of test results from pytest execution
        """
        # Group results by test case (removing provider config suffix)
        grouped_results = {}
        
        for result in test_results_summary:
            test_name = result['test_name']
            
            # Extract base test name and provider config
            if '[' in test_name:
                base_name = test_name.split('[')[0]
                provider_config = test_name.split('[')[1].rstrip(']')
            else:
                base_name = test_name
                provider_config = 'default'
            
            if base_name not in grouped_results:
                grouped_results[base_name] = {
                    'jira_link': f"JIRA-{base_name.replace('_', '-')}",
                    'test_summary': base_name.replace('_', ' ').title(),
                    'context': result.get('context', 'N/A'),
                    'input': result.get('test_input', 'N/A'),
                    'expected_output': result.get('expected_output', 'N/A'),
                    'provider_results': {}
                }
            
            # Map config to provider name
            provider_name = self.extract_provider_from_config(provider_config)
            
            # Extract metrics and scores
            metric_scores = result.get('metric_scores', {})
            
            # First try to get scores directly from the result (new approach)
            hallucination_score = result.get('hallucination_score', None)
            relevancy_score = result.get('relevancy_score', None)
            
            # If not found directly, try from metric_scores dictionary (fallback)
            if hallucination_score is None:
                hallucination_score = metric_scores.get('HallucinationMetric', None)
            if relevancy_score is None:
                relevancy_score = metric_scores.get('AnswerRelevancyMetric', None)
            
            # Debug: Print what we're getting from the test results
            print(f"DEBUG - Processing result for {provider_config}:")
            print(f"  Full result keys: {list(result.keys())}")
            print(f"  Direct hallucination_score: {result.get('hallucination_score', 'Not found')}")
            print(f"  Direct relevancy_score: {result.get('relevancy_score', 'Not found')}")
            print(f"  Metric scores dict: {metric_scores}")
            print(f"  Metric scores keys: {list(metric_scores.keys()) if metric_scores else 'None'}")
            print(f"  Final Hallucination score: {hallucination_score}")
            print(f"  Final Relevancy score: {relevancy_score}")
            
            # Try alternative key names if still not found
            if hallucination_score is None and metric_scores:
                # Try different possible key variations
                for key in metric_scores.keys():
                    if 'hallucination' in key.lower():
                        hallucination_score = metric_scores[key]
                        print(f"  Found hallucination score with key '{key}': {hallucination_score}")
                        break
                        
            if relevancy_score is None and metric_scores:
                # Try different possible key variations
                for key in metric_scores.keys():
                    if 'relevancy' in key.lower() or 'relevance' in key.lower():
                        relevancy_score = metric_scores[key]
                        print(f"  Found relevancy score with key '{key}': {relevancy_score}")
                        break
            
            # Format score display
            if hallucination_score is not None and relevancy_score is not None:
                score_display = f"H: {hallucination_score:.2f}, R: {relevancy_score:.2f}"
            else:
                score_display = "N/A"
            
            # Determine individual provider pass/fail status based on metrics (NOT overall test status)
            provider_status = "N/A"
            if hallucination_score is not None and relevancy_score is not None:
                # Apply thresholds: Hallucination > 0.3 = FAIL, Relevancy <= 0.7 = FAIL
                if hallucination_score > 0.3:
                    provider_status = "FAIL"
                    print(f"Provider {provider_name} FAILED: Hallucination score {hallucination_score:.3f} > 0.3")
                elif relevancy_score <= 0.7:
                    provider_status = "FAIL" 
                    print(f"Provider {provider_name} FAILED: Relevancy score {relevancy_score:.3f} <= 0.7")
                else:
                    provider_status = "PASS"
                    print(f"Provider {provider_name} PASSED: H: {hallucination_score:.3f}, R: {relevancy_score:.3f}")
            else:
                # If scores are not available, fall back to overall test status
                provider_status = result.get('status', 'N/A')
                print(f"Provider {provider_name} status from test result: {provider_status} (no metrics available)")
            
            grouped_results[base_name]['provider_results'][provider_name] = {
                'score': score_display,
                'pass_fail': provider_status,
                'actual_output': result.get('actual_output', 'N/A'),
                'hallucination_score': hallucination_score,
                'relevancy_score': relevancy_score
            }
        
        # Add grouped results to test_results
        for test_data in grouped_results.values():
            self.add_test_result(test_data)


def generate_model_comparison_report(test_results_summary: List[Dict]) -> str:
    """
    Utility function to generate a model comparison report from pytest results.
    
    Args:
        test_results_summary: List of test results from pytest execution
        
    Returns:
        Path to the generated report file
    """
    generator = ModelComparisonReportGenerator()
    generator.process_pytest_results(test_results_summary)
    return generator.generate_html_report()
