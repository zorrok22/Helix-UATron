package pages.ITSM.HelixGPT;

import java.util.List;
import java.util.Map;
import io.restassured.path.json.JsonPath;
import java.util.logging.Logger;

public class ModelDetails {

    public enum ModelName {
        GEMINI_2_0_FLASH_001("gemini-2.0-flash-001"),
        GEMINI_2_5_FLASH("gemini-2.5-flash"),
        GPT_4("gpt-4"),
        GPT_4_TURBO_AZURE("GPT-4 Turbo"),
        GPT_4_TURBO_OPENAI("gpt-4-turbo"),
        GPT_4_1_AZURE("GPT-4.1"),
        GPT_4_1_OPENAI("gpt-4.1"),
        GPT_4_1_MINI("GPT-4.1 mini"),
        GPT_4O_AZURE("GPT-4o"),
        GPT_4O_OPENAI("gpt-4o"),
        GPT_4O_MINI("GPT-4o mini"),
        HELIX_GPT_V6("HelixGPT-v6"),
        LLAMA_3_1_70B_INSTRUCT("llama-3.1-70b-instruct"),
        LLAMA_3_2_90B_VISION_INSTRUCT("llama-3.2-90b-vision-instruct"),
        LLAMA3_8B_CHAT_001("llama3-8b-chat-001"),
        META_LLAMA_4_MAVERICK_17B_128E_INSTRUCT_FP8("meta.llama-4-maverick-17b-128e-instruct-fp8"),
        META_LLAMA_4_MAVERICK_17B_128E_INSTRUCT_MAAS("meta/llama-4-maverick-17b-128e-instruct-maas"),
        US_META_LLAMA4_MAVERICK_17B_INSTRUCT_V1_0("us.meta.llama4-maverick-17b-instruct-v1:0"),
        VERTEX_AI_GEMINI_1_5_PRO("VertexAI Gemini 1.5 Pro");

        private final String modelName;

        ModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getModelName() {
            return modelName;
        }

          /**
         * Find ModelName enum by its string value
         * @param modelName The string value to search for
         * @return The matching ModelName enum, or null if not found
         */
        public static ModelName fromString(String modelName) {
            for (ModelName model : ModelName.values()) {
                if (model.getModelName().equals(modelName)) {
                    return model;
                }
            }
            return null;
        }

        /**
        * Extracts model ID from JSON response where name exactly matches the specified model name
        * @param jsonResponse The JSON response as string
        * @param modelName The exact model name to match
        * @param logger Logger instance for logging
        * @return Model ID if found, null otherwise
        */
        public static String getModelIdByName(String jsonResponse, ModelDetails.ModelName modelName, Logger logger) {
            try {
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<Map<String, Object>> dataList = jsonPath.getList("data");
            
            for (Map<String, Object> model : dataList) {
                if (model.get("name") != null && 
                    model.get("name").toString().equals(modelName.getModelName())) {
                    String modelId = model.get("id").toString();
                    logger.info("Found model ID for name '" + modelName.getModelName() + "': " + modelId);
                    return modelId;
                }
            }
            logger.warning("No model found with name: " + modelName.getModelName());
            return null;
            
            } catch (Exception e) {
                logger.severe("Error parsing JSON response for model name '" + modelName.getModelName() + "': " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }
}
