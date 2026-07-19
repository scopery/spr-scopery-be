package com.company.scopery.modules.traceability.requirement.domain.model;
import com.company.scopery.modules.traceability.requirement.domain.enums.*;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class RequirementDomainTest {
    @Test void approve() {
        var r = Requirement.create(UUID.randomUUID(), UUID.randomUUID(), null, "REQ-1", "Login", null, RequirementType.FUNCTIONAL, RequirementPriority.HIGH);
        assertEquals(RequirementStatus.DRAFT, r.status());
        assertEquals(RequirementStatus.APPROVED, r.approve().status());
    }
}
