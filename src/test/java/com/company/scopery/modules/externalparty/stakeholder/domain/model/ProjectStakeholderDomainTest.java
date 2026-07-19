package com.company.scopery.modules.externalparty.stakeholder.domain.model;
import com.company.scopery.modules.externalparty.stakeholder.domain.enums.StakeholderStatus;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class ProjectStakeholderDomainTest {
    @Test void createActive() {
        var s = ProjectStakeholder.create(UUID.randomUUID(), UUID.randomUUID(), null, null, UUID.randomUUID(), "SPONSOR", true);
        assertEquals(StakeholderStatus.ACTIVE, s.status());
        assertTrue(s.clientFacing());
    }
}
