from deepeval.metrics import FaithfulnessMetric, AnswerRelevancyMetric
from deepeval.test_case import LLMTestCase
from deepeval import evaluate
from typing import List
from deep_eval_config import AZURE_OPENAI_API_KEY
from deepeval.metrics.base_metric import BaseMetric
from deepeval.test_case import LLMTestCase, LLMTestCaseParams
from sentence_transformers import SentenceTransformer, util
from keybert import KeyBERT
import numpy as np

class LogInsightsEvaluator:
    """
    Class to evaluate Log Insights summary against event messages using DeepEval metrics
    """
    
    # def __init__(self, summary: str, event_messages: List[str]):
    #     """
    #     Initialize the evaluator with summary and event messages
        
    #     Args:
    #         summary (str): The Log Insights summary to evaluate
    #         event_messages (List[str]): List of event messages to compare against
    #     """
    #     self.summary = summary
    #     self.retrieval_context = event_messages  # Keep context as a list of strings
    
    def evaluate_faithfulness(self, input, actual_output, retrieval_context) -> float:
        """
        Evaluate how faithful the summary is to the event messages
        Returns:
            float: Faithfulness score between 0 and 1
        """
        try:
            print("Evaluating faithfulness of summary to event messages...")
            
            # Create test case for faithfulness evaluation
            test_case = LLMTestCase(
                input=input,
                actual_output=actual_output,
                retrieval_context=retrieval_context,
                expected_output="Detailed description of infra issues affecting performance and timeouts.",
            )
            
            # Initialize and run faithfulness metric
            faithfulness_metric = FaithfulnessMetric(
                verbose_mode=True,
                include_reason=True
            )
            
            # metric = FaithfulnessMetric(
            #     threshold=0.7,
            #     model="gpt-4",
            #     include_reason=True
            # )
            # faithfulness_score = faithfulness_metric.measure(test_case)
            
            # print(f"Faithfulness score: {faithfulness_score}")
            # return faithfulness_score
            # Evaluate the test case using the metric
            evaluate(test_cases=[test_case], metrics=[faithfulness_metric])
            
        except Exception as e:
            print(f"Failed to evaluate faithfulness: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return 0.0
        
    def evaluate_question_answer_relevance(self, bar_answer_string, bar_question, context=None) -> float:
        """
        Evaluate the relevance of the question to the summary
        Returns:
            float: Relevance score between 0 and 1
        """
        # Placeholder for relevance evaluation logic
        # This can be implemented using a different metric or method
        try:
            print("Evaluating question-answer relevance...")
            # Implement your logic here
            relevance_score = 0.0  # Replace with actual calculation
            metric = AnswerRelevancyMetric(
                threshold=0.7,
                include_reason=True
            )
            if context:
                # Create test case for relevance evaluation with context
                test_case = LLMTestCase(
                    input=bar_question,
                    actual_output=bar_answer_string,
                    retrieval_context=context,
                    expected_output="Detailed description of infra issues affecting performance and timeouts.",
                )
            else:
                test_case = LLMTestCase(
                input=bar_question,
                # Replace this with the output from your LLM app
                actual_output=bar_answer_string
            )

            evaluate(test_cases=[test_case], metrics=[metric])

            # print(f"Relevance score: {relevance_score}")
            return relevance_score
        except Exception as e:
            print(f"Failed to evaluate question-answer relevance: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")

        return 0.0
    
    def evaluate(self) -> dict:
        """
        Run faithfulness evaluation
        Returns:
            dict: Dictionary containing the faithfulness score
        """
        try:
            print("Running evaluation of Log Insights summary...")
            
            faithfulness_score = self.evaluate_faithfulness()
            
            results = {
                'faithfulness': faithfulness_score
            }
            
            print(f"Evaluation results: {results}")
            return results
            
        except Exception as e:
            print(f"Failed to run evaluation: {str(e)}")
            print(f"Exception type: {type(e).__name__}")
            import traceback
            print(f"Stack trace: {traceback.format_exc()}")
            return {'faithfulness': 0.0}
        

class CoverageMetric(BaseMetric):
    """
    Custom Coverage Metric for evaluating how well the answer covers key facts
    from the retrieval_context (infra event messages).
    """

    def __init__(self, threshold: float = 0.5, top_n_keyphrases: int = 10):
        super().__init__()
        self.threshold = threshold
        self.top_n_keyphrases = top_n_keyphrases
        self.sbert_model = SentenceTransformer('all-MiniLM-L6-v2')
        self.kw_model = KeyBERT(model=self.sbert_model)

    def extract_keyphrases(self, text: str):
        keywords = self.kw_model.extract_keywords(
            text, keyphrase_ngram_range=(1, 3), stop_words='english', top_n=self.top_n_keyphrases
        )
        return [kw for kw, score in keywords]

    def compute_coverage_score(self, retrieval_context: str, answer: str):
        context_phrases = self.extract_keyphrases(retrieval_context)
        
        if not context_phrases:
            return 0.0, "No keyphrases extracted from retrieval context."

        answer_embedding = self.sbert_model.encode(answer, convert_to_tensor=True)
        phrase_embeddings = self.sbert_model.encode(context_phrases, convert_to_tensor=True)

        cosine_scores = util.cos_sim(answer_embedding, phrase_embeddings)[0].cpu().numpy()

        coverage_score = np.mean(cosine_scores)

        matched_phrases = [
            context_phrases[i] for i, score in enumerate(cosine_scores) if score > self.threshold
        ]
        reason = (
            f"Coverage Score: {coverage_score:.2f} | "
            f"Matched Keyphrases ({len(matched_phrases)}/{len(context_phrases)}): {matched_phrases}"
        )
        
        return float(coverage_score), reason

    def measure(self, test_case: LLMTestCase) -> float:
        retrieval_context = test_case.retrieval_context or ""
        print("type(retrieval_context) in measure:", type(retrieval_context))
        answer = test_case.actual_output or ""

        

        score, reason = self.compute_coverage_score(" ".join(retrieval_context), answer)

        print("Score:", score)
        print("Reason:", reason)
        # test_case.result.reason = reason
        # test_case.result.metric_name = self.name()
        # test_case.result.score = score
        return score

    def is_successful(self, score: float) -> bool:
        # You can tweak success criteria here if needed
        return score >= self.threshold

    def name(self) -> str:
        return "Coverage Metric"

# ✅ Example Usage with DeepEval:
# if __name__ == "__main__":
#     from deepeval.test_case import LLMTestCaseParams
#     from deepeval import assert_test

#     retrieval_context = """
#         High CPU usage spike on node 3, resulting in degraded performance.
#         Memory utilization exceeded 90%, causing intermittent timeouts.
#         Upstream network latency of 120ms observed between datacenters.
#     """

#     summary_answer = "The impact is good."

#     test_case = LLMTestCase(
#         input="What is the impact?",
#         actual_output=summary_answer,
#         retrieval_context=retrieval_context,
#         expected_output="Detailed description of infra issues affecting performance and timeouts.",
#         params=LLMTestCaseParams(),
#     )

#     metric = CoverageMetric(threshold=0.5, top_n_keyphrases=10)
#     assert_test(test_case, [metric])

#     print(f"Final Coverage Score: {test_case.result.score}")
#     print(f"Reason: {test_case.result.reason}")


# from deepeval.metrics import FaithfulnessMetric
# from deepeval.test_case import LLMTestCase
# from typing import List

# class LogInsightsEvaluator:
#     """
#     Class to evaluate Log Insights summary against event messages using DeepEval metrics
#     """
    
#     def __init__(self, summary: str, event_messages: List[str]):
#         """
#         Initialize the evaluator with summary and event messages
        
#         Args:
#             summary (str): The Log Insights summary to evaluate
#             event_messages (List[str]): List of event messages to compare against
#         """
#         self.summary = summary
#         self.retrieval_context = event_messages  # Keep context as a list of strings
    
#     def evaluate_faithfulness(self) -> float:
#         """
#         Evaluate how faithful the summary is to the event messages
#         Returns:
#             float: Faithfulness score between 0 and 1
#         """
#         try:
#             print("Evaluating faithfulness of summary to event messages...")
            
#             # Create test case for faithfulness evaluation
#             test_case = LLMTestCase(
#                 input="Event messages",
#                 actual_output=self.summary,
#                 retrieval_context=self.retrieval_context
#             )
            
#             # Initialize and run faithfulness metric
#             faithfulness_metric = FaithfulnessMetric(verbose_mode=True)
#             faithfulness_score = faithfulness_metric.measure(test_case)
            
#             print(f"Faithfulness score: {faithfulness_score}")
#             return faithfulness_score
            
#         except Exception as e:
#             print(f"Failed to evaluate faithfulness: {str(e)}")
#             print(f"Exception type: {type(e).__name__}")
#             import traceback
#             print(f"Stack trace: {traceback.format_exc()}")
#             return 0.0
    
#     def evaluate(self) -> dict:
#         """
#         Run faithfulness evaluation
#         Returns:
#             dict: Dictionary containing the faithfulness score
#         """
#         try:
#             print("Running evaluation of Log Insights summary...")
            
#             faithfulness_score = self.evaluate_faithfulness()
            
#             results = {
#                 'faithfulness': faithfulness_score
#             }
            
#             print(f"Evaluation results: {results}")
#             return results
            
#         except Exception as e:
#             print(f"Failed to run evaluation: {str(e)}")
#             print(f"Exception type: {type(e).__name__}")
#             import traceback
#             print(f"Stack trace: {traceback.format_exc()}")
#             return {'faithfulness': 0.0} 