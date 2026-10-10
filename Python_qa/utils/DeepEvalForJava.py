import sys
import os
import shutil
import subprocess
 
# ---------------------------
# Environment setup
# ---------------------------
os.environ["PYTHONIOENCODING"] = "utf-8"
os.environ["PYTHONUTF8"] = "1"
os.environ["TERM"] = "dumb"
os.environ["NO_COLOR"] = "1"
 
# Ensure Python can import from project root (Python_qa)
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if project_root not in sys.path:
    sys.path.append(project_root)
 
from pages.GPT.DeepEval.DeepEvalUtil import DeepEvalUtil
 
 
def deep_eval(context, input, expected_components, actual_output):
    """Run DeepEval evaluation."""
    # Normalize context to always be a list of strings
    if context is None:
        context = []
    elif isinstance(context, str):
        context = [context]
    elif not isinstance(context, list):
        context = [str(context)]
 
    deep_eval_util = DeepEvalUtil()
    return deep_eval_util.EvaluateResults(
        context=context,
        input=input,
        expected_components=expected_components,
        actual_output=actual_output
    )
 
 
if __name__ == "__main__":
    # ---------------------------
    # Step 1: Clean up old .deepeval folder if it exists
    # ---------------------------
    try:
        deepeval_path = os.path.join(os.getcwd(), ".deepeval")
        if os.path.exists(deepeval_path):
            shutil.rmtree(deepeval_path, ignore_errors=True)
    except Exception as e:
        print(f"Warning: Could not remove .deepeval folder (may not exist): {e}")
 
    # ---------------------------
    # Step 2: Set Azure OpenAI config using deepeval CLI
    # ---------------------------
    endpoint = os.getenv("OPENAI_ENDPOINT")
    api_key = os.getenv("OPENAI_API_KEY")
    deployment_name = os.getenv("DEPLOYMENT_NAME")
    api_version = os.getenv("OPENAI_API_VERSION")
 
    if not all([endpoint, api_key, deployment_name, api_version]):
        print("Error: One or more required environment variables are missing!")
        sys.exit(1)
 
    deepeval_exe = shutil.which("deepeval") or "deepeval"
    subprocess.run(
        [
            deepeval_exe, "set-azure-openai",
            f"--openai-endpoint={endpoint}",
            f"--openai-api-key={api_key}",
            f"--deployment-name={deployment_name}",
            f"--openai-api-version={api_version}"
        ],
        check=True
    )
 
    # ---------------------------
    # Step 3: Read arguments from Java call
    # ---------------------------
    if len(sys.argv) < 5:
        print("Usage: python DeepEvalForJava.py <context> <input> <expected> <actual>")
        sys.exit(1)
 
    context_arg = sys.argv[1]
    input_arg = sys.argv[2]
    expected_arg = sys.argv[3]
    actual_arg = sys.argv[4]
 
    # ---------------------------
    # Step 4: Run deep eval and print result
    # ---------------------------
    result = deep_eval(context_arg, input_arg, expected_arg, actual_arg)
    print(result)