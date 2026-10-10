package util;

import com.google.gson.Gson;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class MultiProviderConfig {

    @DataProvider(name = "providerConfig")
    public static Object[][] providerConfig() {
        Map<String, Map<String, String>> configData = readJsonFile("providerConfig.json");
        return configData.values().stream()
                .map(cfg -> new Object[]{ cfg.get("provider"), cfg.get("model") })
                .toArray(Object[][]::new);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, String>> readJsonFile(String fileName) {
        String path = System.getProperty("user.dir") + File.separator + "resources" + File.separator + SolQAConstants.appHelixGPT;
        try (FileReader reader = new FileReader(path + File.separator + fileName)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load provider config JSON", e);
        }
    }
}
