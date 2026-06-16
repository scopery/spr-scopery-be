package com.company.scopery.modules.knowledge.documenttype.infrastructure.seed;

import com.company.scopery.modules.knowledge.documenttype.domain.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class SystemDocumentTypeInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(SystemDocumentTypeInitializer.class);

    private final DocumentTypeRepository documentTypeRepository;

    public SystemDocumentTypeInitializer(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<TypeDef> catalog = buildCatalog();
        int seeded = 0;
        for (TypeDef def : catalog) {
            DocumentTypeCode code = DocumentTypeCode.of(def.code);
            if (!documentTypeRepository.existsByCodeAndScopeSystem(code)) {
                documentTypeRepository.save(DocumentType.createSystem(code, def.name, def.description));
                seeded++;
            }
        }
        if (seeded > 0) {
            log.info("Knowledge document type catalog: seeded {} system types", seeded);
        }
    }

    private List<TypeDef> buildCatalog() {
        return List.of(
                new TypeDef("ARTICLE",       "Article",       "General knowledge article or blog post"),
                new TypeDef("GUIDE",         "Guide",         "Step-by-step guide or how-to document"),
                new TypeDef("REFERENCE",     "Reference",     "Technical reference or API documentation"),
                new TypeDef("MEETING_NOTE",  "Meeting Note",  "Meeting notes and action items"),
                new TypeDef("SPECIFICATION", "Specification", "Product or technical specification document"),
                new TypeDef("TEMPLATE",      "Template",      "Reusable document template"),
                new TypeDef("REPORT",        "Report",        "Periodic or ad-hoc report"),
                new TypeDef("POLICY",        "Policy",        "Company policy or compliance document")
        );
    }

    private record TypeDef(String code, String name, String description) {}
}
