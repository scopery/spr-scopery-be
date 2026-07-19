package com.company.scopery.modules.configuration.customfield.domain.model;
import com.company.scopery.modules.configuration.customfield.domain.enums.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class CustomFieldDefinitionDomainTest {
    @Test void archiveField() {
        var d = CustomFieldDefinition.create(UUID.randomUUID(), "TASK", "priority_custom", "Custom Priority", CustomFieldType.SELECT, false, false, false);
        d = d.archive();
        assertThat(d.status()).isEqualTo(CustomFieldStatus.ARCHIVED);
    }
}
