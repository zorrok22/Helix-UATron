import sys
import json
import os
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
sys.path.append(project_root)
from pages.GPT.DeepEval.DeepEvalUtil import DeepEvalUtil


#Author: Daniel M


def deep_eval(context, input, expected_components, actual_output):
    deep_eval_util = DeepEvalUtil()
    deep_eval_util.EvaluateResults(
        context=context,
        input=input,
        expected_components=expected_components,
        actual_output=actual_output
    )


if __name__ == "__main__":
    context_arg = sys.argv[1]
    input_arg = sys.argv[2]
    expected_arg = sys.argv[3]
    actual_arg = sys.argv[4]
    try:
        context = json.loads(context_arg)
    except Exception as e:
        print("Failed to parse context JSON:", e)
        context = context_arg  # fallback
    print(deep_eval(context, input_arg, expected_arg, actual_arg))