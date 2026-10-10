package pages.ITOM.Api;

import ai.onnxruntime.*;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.huggingface.tokenizers.Encoding;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.Arrays;

public class SemanticSimilarityONNX {

    private OrtEnvironment env;
    private OrtSession session;
    private HuggingFaceTokenizer tokenizer;
    String basePath = System.getProperty("user.dir");
    String modelPath = Paths.get(basePath, "resources", "ITOM", "AIOPS", "model.onnx").toString();

    public SemanticSimilarityONNX() throws OrtException, IOException {
        this.env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        this.session = env.createSession(modelPath, options);

        // ✅ Initialize tokenizer once with explicit max length
        this.tokenizer = HuggingFaceTokenizer.builder()
                .optTokenizerName("bert-base-uncased")
                .optMaxLength(512)
                .build();
    }

    public double calculateSemanticSimilarity(String sentence1, String sentence2) throws OrtException {
        float[] embedding1 = getEmbedding(sentence1);
        float[] embedding2 = getEmbedding(sentence2);
        return cosineSimilarity(embedding1, embedding2);
    }

    private float[] getEmbedding(String sentence) throws OrtException {
        if (sentence == null || sentence.trim().isEmpty()) {
            throw new IllegalArgumentException("Input sentence cannot be null or empty");
        }

        Encoding encoding = tokenizer.encode(sentence);

        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();
        long[] tokenTypeIds = new long[inputIds.length];
        Arrays.fill(tokenTypeIds, 0);

        try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, new long[][]{inputIds});
             OnnxTensor maskTensor = OnnxTensor.createTensor(env, new long[][]{attentionMask});
             OnnxTensor tokenTypeTensor = OnnxTensor.createTensor(env, new long[][]{tokenTypeIds})) {

            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("input_ids", inputTensor);
            inputs.put("attention_mask", maskTensor);
            inputs.put("token_type_ids", tokenTypeTensor);

            try (OrtSession.Result result = session.run(inputs)) {
                float[][][] output = (float[][][]) result.get(0).getValue();
                return output[0][0]; // [CLS] token
            }
        }
    }

    private static double cosineSimilarity(float[] vec1, float[] vec2) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public void close() throws OrtException {
        if (tokenizer != null) {
            tokenizer.close();
        }
        if (session != null) {
            session.close();
        }
        if (env != null) {
            env.close();
        }
    }
}