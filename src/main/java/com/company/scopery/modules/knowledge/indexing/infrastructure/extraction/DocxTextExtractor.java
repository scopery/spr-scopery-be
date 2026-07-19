package com.company.scopery.modules.knowledge.indexing.infrastructure.extraction;

import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DocxTextExtractor implements DocumentTextExtractor {

    private static final String CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @Override
    public boolean supports(String contentType) {
        return CONTENT_TYPE.equalsIgnoreCase(contentType)
                || "application/docx".equalsIgnoreCase(contentType);
    }

    @Override
    public String extract(InputStream content, String contentType) {
        try (XWPFDocument doc = new XWPFDocument(content);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            String text = extractor.getText();
            if (text == null || text.isBlank()) {
                throw KnowledgeExceptions.knowledgeExtractionEmptyOrScanned();
            }
            return text;
        } catch (com.company.scopery.common.exception.AppException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("DOCX extraction failed", e);
        }
    }
}
