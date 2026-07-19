package com.company.scopery.modules.knowledge.indexing.infrastructure.extraction;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PlainTextExtractor implements DocumentTextExtractor {

    @Override
    public boolean supports(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("text/plain");
    }

    @Override
    public String extract(InputStream content, String contentType) {
        try {
            return new String(content.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Plain text extraction failed", e);
        }
    }
}
