package com.company.scopery.modules.knowledge.indexing.infrastructure.extraction;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class MarkdownTextExtractor implements DocumentTextExtractor {

    @Override
    public boolean supports(String contentType) {
        if (contentType == null) return false;
        String ct = contentType.toLowerCase();
        return ct.startsWith("text/markdown") || ct.startsWith("text/x-markdown");
    }

    @Override
    public String extract(InputStream content, String contentType) {
        try {
            return new String(content.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Markdown extraction failed", e);
        }
    }
}
