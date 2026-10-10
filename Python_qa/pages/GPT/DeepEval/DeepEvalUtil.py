import os
import sys
import shutil
import pytest
import subprocess
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
sys.path.append(project_root)
from deepeval import evaluate
from deepeval.metrics import HallucinationMetric, AnswerRelevancyMetric, ContextualRelevancyMetric
from deepeval.test_case import LLMTestCase
from test_config import TestConfig

class DeepEvalUtil:

    def __init__(self):
        self.configure_deepeval()
        self.register_api_key()
        pass

    def EvaluateResults(self, context, input, expected_components, actual_output):
        """ Evaluates the results of a test case using specified metrics and automatically stores data for model comparison report.

        Args:
            context: The context in which the test case is run.
            input: The input for the test case.
            expected_components: The expected components of the output.
            actual_output: The actual output from the test case.
        """
        try:
            metrics = [
                HallucinationMetric(threshold=0.3),
                AnswerRelevancyMetric(threshold=0.7)]
            test_case = LLMTestCase(
            input=str(input),
            actual_output=actual_output,
            expected_output=str(expected_components),
            context=context
            )
            evaluation_result = evaluate(test_cases=[test_case], metrics=metrics)
            print("Evaluation Result:",evaluation_result)
            
            # Access scores by metric name
            for metric_data in evaluation_result.test_results[0].metrics_data:
                print(f"{metric_data.name} score: {metric_data.score}")       

            # Extract scores and collect all metric scores
            hallucination_score = None
            relevancy_score = None
            metric_scores = {}
            
            for metric_data in evaluation_result.test_results[0].metrics_data:
                print(f"Metric name: {metric_data.name}")  # Debug print
                metric_scores[metric_data.name] = metric_data.score  # Store all scores
                if "Hallucination" in metric_data.name:
                    hallucination_score = metric_data.score
                elif "Relevancy" in metric_data.name:
                    relevancy_score = metric_data.score

            print(f"Hallucination Score: {hallucination_score}")
            print(f"Relevancy Score: {relevancy_score}") 
            
            # Automatically capture test case data for model comparison report
            self._capture_test_data_for_report(
                hallucination_score=hallucination_score,
                relevancy_score=relevancy_score,
                metric_scores=metric_scores,
                context=context,
                test_input=input,
                expected_output=expected_components,
                actual_output=actual_output
            )
            
            # Assert that all metrics meet their thresholds
            if hallucination_score is None:
                pytest.fail("Hallucination score was not computed. Check metric execution and logs.")
            if relevancy_score is None:
                pytest.fail("Relevancy score was not computed. Check metric execution and logs.")

            assert hallucination_score <= 0.3, "Bot response contains hallucinations"
            assert relevancy_score >= 0.7, "Bot response is not relevant enough"
        except Exception as e:
            print(f"Error during evaluation: {e}")
            pytest.fail(f"Evaluation failed with error: {e}")
    
    def _capture_test_data_for_report(self, hallucination_score, relevancy_score, metric_scores, context, test_input, expected_output, actual_output):
        """Capture test case data for the model comparison report."""
        try:
            # Import here to avoid circular imports
            from conftest import capture_deepeval_data
            
            # Capture the test data globally
            capture_deepeval_data(
                context=str(context),
                test_input=str(test_input),
                expected_output=str(expected_output),
                actual_output=str(actual_output),
                hallucination_score=hallucination_score,
                relevancy_score=relevancy_score,
                metric_scores=metric_scores
            )
            print("Test case data captured for model comparison report")
        except Exception as e:
            print(f"Warning: Could not capture test data for report: {e}")

    def configure_deepeval(self):
        """
        Configure deepeval by:
        1. Removing existing .deepeval folder (ignoring errors)
        2. Setting up Azure OpenAI configuration from TestConfig
        """
        try:
            # Command 1: Remove existing .deepeval folder (ignore errors)
            print("Attempting to remove existing .deepeval configuration...")
            try:
                deepeval_path = os.path.join(os.getcwd(), ".deepeval")
                if os.path.exists(deepeval_path):
                    shutil.rmtree(deepeval_path, ignore_errors=True)
                print("Removed existing .deepeval configuration")
            except Exception as e:
                print(f"Could not remove .deepeval folder (may not exist): {e}")   
            
            # Command 2: Configure Azure OpenAI using TestConfig values
            print("Configuring deepeval with Azure OpenAI from TestConfig...")
            deepeval_exe = shutil.which("deepeval") or "deepeval"
            deepeval_cmd = [
                deepeval_exe, "set-azure-openai",
                f"--openai-endpoint={TestConfig.AZURE_OPENAI_ENDPOINT}",
                f"--openai-api-key={TestConfig.AZURE_OPENAI_API_KEY}",
                f"--deployment-name={TestConfig.AZURE_OPENAI_DEPLOYMENT_NAME}",
                f"--openai-api-version={TestConfig.AZURE_OPENAI_API_VERSION}"
            ]
            
            result = subprocess.run(deepeval_cmd, check=True, capture_output=True, text=True, timeout=30)
            print("DeepEval Azure OpenAI configuration completed successfully")
            if result.stdout:
                print(f"   Output: {result.stdout.strip()}")
                
        except subprocess.TimeoutExpired:
            print("Warning: deepeval configuration command timed out")
        except subprocess.CalledProcessError as e:
            print(f"Warning: deepeval configuration failed: {e}")
            if e.stderr:
                print(f"   Error: {e.stderr.strip()}")
        except Exception as e:
            print(f"Warning: Could not configure deepeval: {e}")

    def register_api_key(self):
        """
        Ensure the OpenAI/Azure OpenAI API key is available to deepeval by setting OPENAI_API_KEY.
        Priority order:
        1. Environment variables (AZURE_OPENAI_API_KEY or OPENAI_API_KEY)
        2. TestConfig values (fallback)
        """
        # Try to get from environment first, then fall back to TestConfig
        api_key = os.getenv("AZURE_OPENAI_API_KEY") or os.getenv("OPENAI_API_KEY") or TestConfig.AZURE_OPENAI_API_KEY
        if not api_key:
            raise EnvironmentError(
                "Missing API key: set AZURE_OPENAI_API_KEY/OPENAI_API_KEY in environment or TestConfig."
            )
        
        # Explicitly set OPENAI_API_KEY (overwrite if exists)
        os.environ["OPENAI_API_KEY"] = api_key

        # Get Azure configuration (env vars first, then TestConfig fallback)
        endpoint = os.getenv("AZURE_OPENAI_ENDPOINT") or TestConfig.AZURE_OPENAI_ENDPOINT
        deployment = os.getenv("AZURE_OPENAI_DEPLOYMENT_NAME") or TestConfig.AZURE_OPENAI_DEPLOYMENT_NAME
        api_version = os.getenv("AZURE_OPENAI_API_VERSION") or TestConfig.AZURE_OPENAI_API_VERSION

        # If using Azure OpenAI, set additional required environment variables
        if endpoint:
            os.environ["AZURE_OPENAI_ENDPOINT"] = endpoint
            os.environ["OPENAI_API_BASE"] = endpoint  # Some libraries use this
        if deployment:
            os.environ["AZURE_OPENAI_DEPLOYMENT_NAME"] = deployment
            os.environ["OPENAI_API_DEPLOYMENT_NAME"] = deployment  # Alternative name
        if api_version:
            os.environ["AZURE_OPENAI_API_VERSION"] = api_version
            os.environ["OPENAI_API_VERSION"] = api_version  # Alternative name
        
        # Set API type for Azure
        if endpoint or deployment:
            os.environ["OPENAI_API_TYPE"] = "azure"

        print("OpenAI API key registered successfully.")
        if endpoint:
            print(f"Using Azure endpoint: {endpoint}")
        if deployment:
            print(f"Using Azure deployment: {deployment}")
        if api_version:
            print(f"Using Azure API version: {api_version}")