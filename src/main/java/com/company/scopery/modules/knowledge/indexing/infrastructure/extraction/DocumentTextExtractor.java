package com.company.scopery.modules.knowledge.indexing.infrastructure.extraction;

import java.io.InputStream;

public interface DocumentTextExtractor {
    boolean supports(String contentType);
    String extract(InputStream content, String contentType);
}
