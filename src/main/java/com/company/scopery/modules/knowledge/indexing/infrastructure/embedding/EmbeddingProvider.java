package com.company.scopery.modules.knowledge.indexing.infrastructure.embedding;

import java.util.List;

public interface EmbeddingProvider {
    List<float[]> embed(List<String> texts, String modelCode);
    String providerCode();
}
