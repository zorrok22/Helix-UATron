        package util;

import java.util.List;
        import org.openqa.selenium.By;
        import org.openqa.selenium.WebElement;
        import org.testng.Assert;


    public class HelixGPTUtil {
            static SolqaLogger log = new SolqaLogger(HelixGPTUtil.class.getName());
             CommonWaits commonWaits=new CommonWaits();

    //String locators
    String gptChatMessage = "//div[contains(@class,'helix-gpt-chat-thread') and contains(@data-testid,'helixGPTChatMessage')]";


        public HelixGPTUtil verifyHelixGPTResult(String context, String situationSummaryTxt, String expected, String output){
                            // Print all parameters before processing
                            log.info("=== HelixGPT Verification Parameters ===");
                            log.info("Context: " + context);
                            log.info("Situation Summary: " + situationSummaryTxt);
                            log.info("Expected: " + expected);
                            log.info("Output: " + output);
                            log.info("=======================================");
                            
                            DeepEval deepEval = new DeepEval();
                            deepEval.getDeepEvalScore(context, situationSummaryTxt, expected, output);
                            String hallucinationScore = deepEval.getHallucinationScore();
                            String relevanceScore = deepEval.getRelevancyScore();
                            log.info("HelixGPT Hallucination Score: " + hallucinationScore);
                            log.info("HelixGPT Relevance Score: " + relevanceScore);
                            double hallucination = Double.parseDouble(hallucinationScore);
                            double relevance = Double.parseDouble(relevanceScore);
                            
                            // Capture test data for model comparison report
                            ModelComparisonReportListener.captureTestData(
                                context, 
                                situationSummaryTxt, 
                                expected, 
                                output, 
                                hallucination, 
                                relevance
                            );
                            log.info("Test data captured for model comparison report: Context=" + context + ", Input=" + situationSummaryTxt);
                            
                            Assert.assertTrue(hallucination < 0.3, "HelixGPT Hallucination Score is above acceptable limit");
                            Assert.assertTrue(relevance > 0.7, "HelixGPT Relevance Score is below acceptable limit");
                            return this;
                            }


       public String getAllGPTResponseString(List<WebElement> allMessages) {
        try {
            commonWaits.waitForSometime(commonWaits.moreDelay);
            commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHelixGPT);

            // If caller passed an empty list, attempt to locate messages dynamically to avoid stale proxies
            List<WebElement> messages = (allMessages == null || allMessages.isEmpty())
                    ? commonWaits.getElements(By.xpath(gptChatMessage))
                    : allMessages;

            log.info("Collecting GPT messages. initial sizes - allMessages param: "
                    + (allMessages == null ? "null" : allMessages.size()) + ", resolved messages: "
                    + (messages == null ? "null" : messages.size()));

            StringBuilder responseBuilder = new StringBuilder();
            String lastLine = null;
            int repeatCount = 0;

            long startTime = System.currentTimeMillis();
            long timeout = 10_000; // 10 sec

            while (System.currentTimeMillis() - startTime < timeout) {
                commonWaits.waitForAllSpinnersToHide(SolQAConstants.appHelixGPT);

                responseBuilder.setLength(0);

                // Re-resolve elements to handle staleness if we originally fetched by locator
                try {
                    messages = commonWaits.getElements(By.xpath(gptChatMessage));
                } catch (Exception ignored) {
                    if (messages == null) {
                        messages = allMessages == null ? java.util.Collections.emptyList() : allMessages;
                    }
                }

                for (WebElement message : messages) {
                    try {
                        // Use a lightweight visibility check; ignore individual failures
                        if (commonWaits.isElementPresent(message, 5)) {
                            String inner = message.getAttribute("innerText");
                            responseBuilder.append(inner == null ? "" : inner).append("\n");
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (!messages.isEmpty()) {
                    try {
                        String[] lines = messages.get(messages.size() - 1).getText().split("\\r?\\n");
                        if (lines.length > 0) {
                            String currentLine = lines[lines.length - 1];
                            if (lastLine != null && lastLine.equals(currentLine)) {
                                repeatCount++;
                            } else {
                                repeatCount = 1;
                                lastLine = currentLine;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (repeatCount >= 1) {
                    log.info("End of response detected.");
                    break;
                }

                commonWaits.waitForSometime(commonWaits.moreDelay);
            }

            log.info("HelixGPT Full Conversation collected: " + responseBuilder.toString().trim());
            return responseBuilder.toString().trim();

        } catch (IndexOutOfBoundsException e) {
            log.error("Error while getting GPT response: " + e.getMessage());
            return "";
        }
}




        }
