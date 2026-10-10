# import os
# import sys
# import unittest

# # Add the project root to Python path
# project_root = os.path.dirname(os.path.abspath(__file__))
# sys.path.insert(0, project_root)

# # Import and run the test
# # from GPT.testcases.ValidateSituationSummaries import TestSituationSummaries
# # from tests.GPT.DeepEval.ValidateLogInsights import TestValidateLogInsights
# from tests.GPT.DeepEval.ValidateBestActionRecommendations import TestBestActionRecommendations
# # from tests.GPT.DeepEval.ValidatePrimarySituation import TestValidatePrimarySituation

# def run_tests():
#     # Create test suite
#     suite = unittest.TestSuite()
    
#     # Add test cases using TestLoader
#     suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestBestActionRecommendations))
#     # suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestSituationSummaries))
#     # suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestValidateLogInsights))
#     # suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestValidatePrimarySituation))
    
#     # Run tests
#     runner = unittest.TextTestRunner(verbosity=2)
#     runner.run(suite)

# if __name__ == '__main__':
#     run_tests() 