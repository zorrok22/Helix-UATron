package pages.ITOM.Api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import util.ReadPropertiesFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OpenAIQuery {
    private static final Map<String, String> openAIConfig = ReadPropertiesFile.readProperties(
        System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "AIOPS" + File.separator + "openApiproperties.config");

    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static String readJSon(String jsonFileName){
        try {
            String jsonContent;
            String jsonFile = System.getProperty("user.dir") + File.separator+ "resources"+ File.separator + "ITOM" +File.separator + "AIOPS"+File.separator+jsonFileName;
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            return jsonContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendChatRequest(String userMessage) throws IOException {
        return sendOpenAIChatRequest(userMessage);
    }

    public static String sendAzureChatRequest(String userMessage) throws IOException {
        String jsonFile = "openaiusermesssage.json";
        String jsonBody = readJSon(jsonFile);
        String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=%s",
                getAzureEndpoint(), getAzureDeploymentId(), getAzureApiVersion());

        // Build request body
        userMessage = escapeJson(userMessage);
        jsonBody = jsonBody.replace("<usermessage>", userMessage);

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("api-key", getAzureApiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body was empty for Azure chat request: " + response.request().url());
            }
            JsonNode root = mapper.readTree(responseBody.string());
            return root.path("choices").get(0).path("message").path("content").asText();
        }
    }

    public static String sendOpenAIChatRequest(String userMessage) throws IOException {
        String jsonFile = "openaiusermessageopenai.json";
        String jsonBody = readJSon(jsonFile);

        userMessage = escapeJson(userMessage);
        jsonBody = jsonBody.replace("<usermessage>", userMessage);
        jsonBody = jsonBody.replace("<model>", getOpenAIModel());

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(OPENAI_ENDPOINT)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + getOpenAIApiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("Unexpected code " + response + ", body: " + errorBody);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body was empty for OpenAI chat request: " + response.request().url());
            }
            JsonNode root = mapper.readTree(responseBody.string());
            return root.path("choices").get(0).path("message").path("content").asText();
        }
    }

    private static String getOpenAIApiKey() {
        return getRequiredConfig("OPENAI_API_KEY", "OPENAI_API_KEY");
    }

    private static String getOpenAIModel() {
        return getConfig("OPENAI_MODEL", "OPENAI_MODEL", "gpt-4o");
    }

    private static String getAzureApiKey() {
        return getRequiredConfig("AZURE_OPENAI_API_KEY", "AZURE_OPENAI_API_KEY");
    }

    private static String getAzureEndpoint() {
        return trimTrailingSlash(getRequiredConfig("AZURE_OPENAI_ENDPOINT", "AZURE_OPENAI_ENDPOINT"));
    }

    private static String getAzureDeploymentId() {
        return getRequiredConfig("AZURE_OPENAI_DEPLOYMENT_NAME", "AZURE_OPENAI_DEPLOYMENT_NAME");
    }

    private static String getAzureApiVersion() {
        return getConfig("AZURE_OPENAI_API_VERSION", "AZURE_OPENAI_API_VERSION", "2024-10-21");
    }

    private static String getRequiredConfig(String envName, String propertyName) {
        String value = getConfig(envName, propertyName, null);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                    "Missing required configuration: set " + envName + " environment variable or "
                            + propertyName + " in properties.config");
        }
        return value;
    }

    private static String getConfig(String envName, String propertyName, String defaultValue) {
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        String propertyValue = openAIConfig.get(propertyName);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }

        return defaultValue;
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public static double computeSimilarityAzureEmbeding(String text1, String text2) throws IOException {
        double[] embedding1 = getEmbedding(text1);
        double[] embedding2 = getEmbedding(text2);
        return cosineSimilarity(embedding1, embedding2);
    }

    public static double[] getEmbedding(String text) throws IOException {
        String url = String.format("%s/openai/deployments/%s/embeddings?api-version=%s",
                getAzureEndpoint(), getAzureDeploymentId(), getAzureApiVersion());

        String jsonBody = "{"
                + "\"input\": \"" + escapeJson(text) + "\""
                + "}";

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("api-key", getAzureApiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response + ", body: " + response.body().string());
            }

            String responseBody = response.body().string();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode embeddingNode = root.path("data").get(0).path("embedding");

            return mapper.convertValue(embeddingNode, double[].class);
        }
    }

    public static double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vector dimensions must match");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            return 0.0; // avoid division by zero
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}