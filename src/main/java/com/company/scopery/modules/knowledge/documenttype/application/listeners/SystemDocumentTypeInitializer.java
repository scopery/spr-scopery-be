package com.company.scopery.modules.knowledge.documenttype.application.listeners;

import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldDataType;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.documenttypefield.domain.valueobject.DocumentTypeFieldKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class SystemDocumentTypeInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(SystemDocumentTypeInitializer.class);

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeFieldRepository fieldRepository;

    public SystemDocumentTypeInitializer(DocumentTypeRepository documentTypeRepository,
                                         DocumentTypeFieldRepository fieldRepository) {
        this.documentTypeRepository = documentTypeRepository;
        this.fieldRepository = fieldRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        int seededTypes = 0;
        int seededFields = 0;
        for (TypeDef def : buildCatalog()) {
            DocumentTypeCode code = DocumentTypeCode.of(def.code);
            Optional<DocumentType> existing = documentTypeRepository.findByCodeAndScopeSystem(code);
            DocumentType type;
            if (existing.isEmpty()) {
                type = documentTypeRepository.save(DocumentType.createSystem(
                        code, def.name, def.description, def.category,
                        DocumentClassification.INTERNAL, def.reviewCycleDays, null, null, true));
                seededTypes++;
            } else {
                type = existing.get();
            }
            seededFields += seedBasicFields(type);
        }
        if (seededTypes > 0 || seededFields > 0) {
            log.info("Knowledge document type catalog: seeded {} system types, {} system fields",
                    seededTypes, seededFields);
        }
    }

    private int seedBasicFields(DocumentType type) {
        int created = 0;
        for (FieldDef field : basicFields()) {
            if (!fieldRepository.existsByDocumentTypeIdAndFieldKey(type.id(), field.key)) {
                String options = field.dataType == DocumentTypeFieldDataType.SELECT
                        ? "[\"PUBLIC\",\"INTERNAL\",\"CONFIDENTIAL\",\"RESTRICTED\"]"
                        : null;
                fieldRepository.save(DocumentTypeField.create(
                        type.id(), DocumentTypeFieldKey.of(field.key), field.label, field.description,
                        field.dataType, field.required, true, options, null, null, field.order));
                created++;
            }
        }
        return created;
    }

    private List<TypeDef> buildCatalog() {
        return List.of(
                new TypeDef("ARTICLE", "Article", "General knowledge article or blog post", "GENERAL", null),
                new TypeDef("GUIDE", "Guide", "Step-by-step guide or how-to document", "GENERAL", null),
                new TypeDef("REFERENCE", "Reference", "Technical reference or API documentation", "GENERAL", null),
                new TypeDef("MEETING_NOTE", "Meeting Note", "Meeting notes and action items", "MEETING", 30),
                new TypeDef("SPECIFICATION", "Specification", "Product or technical specification document", "SPEC", 90),
                new TypeDef("TEMPLATE", "Template", "Reusable document template", "GENERAL", null),
                new TypeDef("REPORT", "Report", "Periodic or ad-hoc report", "REPORT", 90),
                new TypeDef("POLICY", "Policy", "Company policy or compliance document", "COMPLIANCE", 180),
                new TypeDef("BRD", "Business Requirements Document", "Business requirements document", "REQUIREMENTS", 90),
                new TypeDef("SRS", "Software Requirements Specification", "Software requirements specification", "REQUIREMENTS", 90),
                new TypeDef("TECHNICAL_SPEC", "Technical Specification", "Technical design / architecture specification", "SPEC", 90),
                new TypeDef("PROJECT_PROPOSAL", "Project Proposal", "Project proposal document", "PROJECT", 60),
                new TypeDef("USER_GUIDE", "User Guide", "End-user guide or handbook", "GENERAL", 180),
                new TypeDef("MEETING_MINUTES", "Meeting Minutes", "Formal meeting minutes", "MEETING", 30),
                new TypeDef("CONTRACT", "Contract", "Contract or legal agreement", "LEGAL", 365),
                new TypeDef("CHANGE_REQUEST", "Change Request", "Change request document", "CHANGE", 60),
                new TypeDef("TEST_PLAN", "Test Plan", "Test plan document", "QA", 90),
                new TypeDef("RELEASE_NOTE", "Release Note", "Release notes", "RELEASE", null),
                new TypeDef("RISK_REGISTER", "Risk Register", "Risk register document", "RISK", 30),
                new TypeDef("DECISION_LOG", "Decision Log", "Architecture / product decision log", "GOVERNANCE", 90),
                new TypeDef("RETROSPECTIVE_NOTE", "Retrospective Note", "Sprint or project retrospective notes", "MEETING", 30)
        );
    }

    private List<FieldDef> basicFields() {
        return List.of(
                new FieldDef("owner", "Owner", "Document owner", DocumentTypeFieldDataType.USER, true, 0),
                new FieldDef("summary", "Summary", "Short summary", DocumentTypeFieldDataType.LONG_TEXT, false, 1),
                new FieldDef("status", "Status", "Lifecycle status label", DocumentTypeFieldDataType.TEXT, false, 2),
                new FieldDef("classification", "Classification", "Document classification", DocumentTypeFieldDataType.SELECT, false, 3),
                new FieldDef("effectiveDate", "Effective Date", "Effective date", DocumentTypeFieldDataType.DATE, false, 4),
                new FieldDef("reviewDueDate", "Review Due Date", "Next review due date", DocumentTypeFieldDataType.DATE, false, 5),
                new FieldDef("relatedProject", "Related Project", "Related project reference", DocumentTypeFieldDataType.PROJECT, false, 6),
                new FieldDef("relatedApplication", "Related Application", "Related application reference", DocumentTypeFieldDataType.APPLICATION, false, 7)
        );
    }

    private record TypeDef(String code, String name, String description, String category, Integer reviewCycleDays) {}

    private record FieldDef(String key, String label, String description, DocumentTypeFieldDataType dataType,
                            boolean required, int order) {}
}
