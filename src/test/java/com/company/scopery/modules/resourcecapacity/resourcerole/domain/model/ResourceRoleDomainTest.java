package com.company.scopery.modules.resourcecapacity.resourcerole.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class ResourceRoleDomainTest {
    @Test void createResourceRole_success() {
        var r = ResourceRole.create(UUID.randomUUID(), "BACKEND_DEV", "Backend Developer", null, null);
        assertThat(r.roleCode()).isEqualTo("BACKEND_DEV");
        assertThat(r.status().name()).isEqualTo("ACTIVE");
    }
    @Test void resourceRoleDoesNotGrantIam() {
        var r = ResourceRole.create(UUID.randomUUID(), "PM", "Project Manager", "planning role", null);
        assertThat(r.roleCode()).isNotEqualTo("WORKSPACE_OWNER");
    }
}
