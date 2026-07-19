package com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceProfileStatus;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceType;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class ResourceProfileDomainTest {
    @Test void createResourceProfileInternalUser_success() {
        var p = ResourceProfile.create(UUID.randomUUID(), ResourceType.INTERNAL_USER, "Alice", UUID.randomUUID(), UUID.randomUUID(), null);
        assertThat(p.status()).isEqualTo(ResourceProfileStatus.ACTIVE);
        assertThat(p.resourceType()).isEqualTo(ResourceType.INTERNAL_USER);
    }
    @Test void createPlaceholderResource_success() {
        var p = ResourceProfile.placeholder(UUID.randomUUID(), "Senior QA", UUID.randomUUID());
        assertThat(p.resourceType()).isEqualTo(ResourceType.PLACEHOLDER_ROLE);
        assertThat(p.linkedUserId()).isNull();
    }
    @Test void archiveResourceProfile_success() {
        var p = ResourceProfile.placeholder(UUID.randomUUID(), "Dev", null).archive(UUID.randomUUID());
        assertThat(p.status()).isEqualTo(ResourceProfileStatus.ARCHIVED);
        assertThat(p.archivedAt()).isNotNull();
    }
}
