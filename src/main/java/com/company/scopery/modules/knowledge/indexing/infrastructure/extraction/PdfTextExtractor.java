package com.company.scopery.modules.knowledge.indexing.infrastructure.extraction;

import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PdfTextExtractor implements DocumentTextExtractor {

    @Override
    public boolean supports(String contentType) {
        return "application/pdf".equalsIgnoreCase(contentType);
    }

    @Override
    public String extract(InputStream content, String contentType) {
        try (PDDocument doc = Loader.loadPDF(content.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            if (text == null || text.isBlank()) {
                throw KnowledgeExceptions.knowledgeExtractionEmptyOrScanned();
            }
            return text;
        } catch (com.company.scopery.common.exception.AppException e) {
            throw e;
        } catch (Exception e) {
            throw KnowledgeExceptions.knowledgeExtractionEmptyOrScanned();
        }
    }
}
