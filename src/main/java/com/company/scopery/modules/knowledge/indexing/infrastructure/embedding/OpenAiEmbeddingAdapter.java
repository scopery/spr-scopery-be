package com.company.scopery.modules.knowledge.indexing.infrastructure.embedding;

import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiEmbeddingAdapter implements EmbeddingProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiEmbeddingAdapter.class);
    private static final String OPENAI_EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";
    private static final int BATCH_SIZE = 100;

    private final String apiKey;
    private final RestClient restClient;

    public OpenAiEmbeddingAdapter(
            @Value("${scopery.embedding.api-key:}") String apiKey,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public String providerCode() {
        return "OPENAI";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<float[]> embed(List<String> texts, String modelCode) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OpenAI API key not configured — returning zero embeddings");
            return texts.stream().map(t -> new float[1536]).toList();
        }

        List<float[]> result = new ArrayList<>(texts.size());
        for (int i = 0; i < texts.size(); i += BATCH_SIZE) {
            List<String> batch = texts.subList(i, Math.min(i + BATCH_SIZE, texts.size()));
            try {
                var body = Map.of("model", modelCode, "input", batch);
                var response = restClient.post()
                        .uri(OPENAI_EMBEDDINGS_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                        .body(body)
                        .retrieve()
                        .body(Map.class);

                var data = (List<Map<String, Object>>) response.get("data");
                data.stream()
                        .sorted((a, b) -> Integer.compare((int) a.get("index"), (int) b.get("index")))
                        .forEach(item -> {
                            List<Number> vec = (List<Number>) item.get("embedding");
                            float[] arr = new float[vec.size()];
                            for (int j = 0; j < vec.size(); j++) arr[j] = vec.get(j).floatValue();
                            result.add(arr);
                        });
            } catch (Exception e) {
                throw KnowledgeExceptions.knowledgeRetrievalProviderUnavailable("OPENAI");
            }
        }
        return result;
    }
}
