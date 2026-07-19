package com.company.scopery.modules.documenthub.document.domain.model;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class DocumentDomainTest {
    @Test void approveTransitions() {
        var d = Document.create(UUID.randomUUID(), UUID.randomUUID(), null, "SPEC", "D-1", "Spec", null);
        assertEquals(DocumentStatus.DRAFT, d.status());
        assertEquals(DocumentStatus.APPROVED, d.approve(UUID.randomUUID()).status());
    }
}
