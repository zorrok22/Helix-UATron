package util;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;

import framework.Launcher;

public class DeepEval extends Launcher {

    static SolqaLogger log = new SolqaLogger(DeepEval.class.getName());
    String hallucinationScore;
    String relevancyScore;
    // Azure OpenAI config
    public static final String AZURE_OPENAI_ENDPOINT = configProp.get("AZURE_OPENAI_ENDPOINT");
    public static final String AZURE_OPENAI_API_KEY = configProp.get("AZURE_OPENAI_API_KEY");
    public static final String AZURE_OPENAI_DEPLOYMENT_NAME = configProp.get("AZURE_OPENAI_DEPLOYMENT_NAME");
    public static final String AZURE_OPENAI_API_VERSION = configProp.get("AZURE_OPENAI_API_VERSION");

    public DeepEval() {
        this.hallucinationScore = null;
        this.relevancyScore = null;
    }

    public String getHallucinationScore() {
        return hallucinationScore;
    }

    public String getRelevancyScore() {
        return relevancyScore;
    }

    public void getDeepEvalScore(String context, String input, String expected, String actual) {
        try {
            String pythonScriptPath = ".." + File.separator + "Python_qa" + File.separator + "utils" + File.separator + "DeepEvalForJava.py";

            // Use configurable Python executable (default "py")
            String pythonExec = System.getenv().getOrDefault("PYTHON_EXEC", "py");

            ProcessBuilder pb = new ProcessBuilder(
                pythonExec, "-Xutf8", pythonScriptPath, context, input, expected, actual
            );

            // Fix: Pass correct environment variable names for Python
            Map<String, String> env = pb.environment();
            env.put("OPENAI_ENDPOINT", AZURE_OPENAI_ENDPOINT);
            env.put("OPENAI_API_KEY", AZURE_OPENAI_API_KEY);
            env.put("DEPLOYMENT_NAME", AZURE_OPENAI_DEPLOYMENT_NAME);
            env.put("OPENAI_API_VERSION", AZURE_OPENAI_API_VERSION);

            pb.redirectErrorStream(true);
            env.put("PYTHONUTF8", "1");

            Pattern hallucinationPattern = Pattern.compile(
                "Hallucination\\s*score\\s*:?\\s*([0-9.]+)", Pattern.CASE_INSENSITIVE);
           
            Pattern relevancyPattern = Pattern.compile("Answer\\s*Relevancy\\s*score\\s*:?\\s*([0-9.]+)", Pattern.CASE_INSENSITIVE);

            Double hallucinationScore = null;
            Double relevancyScore = null;

            Process process = pb.start();
            StringBuilder outputLog = new StringBuilder();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLog.append(line).append("\n");
                    
                    Matcher m1 = hallucinationPattern.matcher(line);
                    if (m1.find()) {
                        hallucinationScore = Double.parseDouble(m1.group(1));
                    }

                    Matcher m2 = relevancyPattern.matcher(line);
                    if (m2.find()) {
                        relevancyScore = Double.parseDouble(m2.group(1));
                    }
                }
            }

            int exitCode = process.waitFor();
            log.info("Python exited with code: " + exitCode);
            
            // Check if this is a bot refusal (before checking exit code)
            boolean isRefusal = outputLog.toString().contains("BOT REFUSAL DETECTED");
            String refusalPattern = isRefusal ? extractRefusalPattern(outputLog.toString()) : "none";
            
            if (exitCode != 0) {
                log.error("Python script output:\n" + outputLog.toString());
                
                if (isRefusal) {
                    // This is a bot refusal - provide specific guidance
                    log.error("=== BOT REFUSAL DETECTED ===");
                    log.error("The bot refused to answer the question. This indicates:");
                    log.error("1. Skill/model configuration issue in HelixGPT Agent Studio");
                    log.error("2. Data source (CSM/Sharepoint/etc.) not properly configured for this model");
                    log.error("3. Context/information not being retrieved for this query");
                    log.error("4. Model-specific prompt configuration needed");
                    log.error("Action: Check HelixGPT Agent Studio configuration for this model/provider combination");
                } else {
                    // Generic DeepEval failure
                    log.error("DeepEval script failed. Check that:");
                    log.error("1. Python deepeval package is installed (pip install deepeval)");
                    log.error("2. Azure OpenAI credentials are correct in properties.config");
                    log.error("3. Network connectivity to Azure OpenAI endpoint");
                }
            }

            if (exitCode != 0) {
                // Capture test data for report even if test failed
                if (isRefusal) {
                    // For refusal cases, we have hallucination=0.0 and relevancy=0.0 from Python output
                    ModelComparisonReportListener.captureTestData(
                        context, input, expected, actual, 
                        hallucinationScore != null ? hallucinationScore : 0.0, 
                        relevancyScore != null ? relevancyScore : 0.0, 
                        refusalPattern
                    );
                    Assert.fail("Bot refused to answer. Refusal pattern detected: " + refusalPattern + 
                               ". This is a HelixGPT skill/model configuration issue, not a DeepEval problem. " +
                               "Hallucination score: " + hallucinationScore + ", Relevancy score: " + relevancyScore);
                } else {
                    Assert.fail("DeepEval Python script failed with exit code " + exitCode + 
                               " and hallucinationScore: " + hallucinationScore + 
                               " and relevancyScore: " + relevancyScore);
                }
            }

            if (hallucinationScore == null || relevancyScore == null) {
                Assert.fail("Could not extract hallucination or relevancy score from DeepEval output");
            }

            this.hallucinationScore = hallucinationScore.toString();
            this.relevancyScore = relevancyScore.toString();

            log.info("Extracted Hallucination Score: " + hallucinationScore);
            log.info("Extracted Relevancy Score: " + relevancyScore);
            log.info("Refusal Pattern: " + refusalPattern);

            // Automatically capture test data for model comparison report
            ModelComparisonReportListener.captureTestData(
                context, input, expected, actual, hallucinationScore, relevancyScore, refusalPattern
            );

        } catch (Exception e) {
            Assert.fail("Error while running DeepEval script: " + e.getMessage(), e);
        }
    }
    
    private String extractRefusalPattern(String output) {
        try {
            Pattern pattern = Pattern.compile("BOT REFUSAL DETECTED: ([^\n]+)");
            Matcher matcher = pattern.matcher(output);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            // Ignore extraction errors
        }
        return "unknown refusal pattern";
    }
}