package com.company.scopery.modules.documenthub.shared.application;

import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistry;
import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentPayloadMaskingServiceTest {

    @Mock
    private SensitiveFieldRegistryRepository sensitiveFields;

    private DocumentPayloadMaskingService service;

    @BeforeEach
    void setUp() {
        service = new DocumentPayloadMaskingService(sensitiveFields);
    }

    @Test
    void maskDocumentPayload_masksKnownFields_withDefaults() {
        Map<String, Object> raw = new LinkedHashMap<>();
        raw.put("ownerEmail", "alice@client.com");
        raw.put("contactPhone", "+15551234567");
        raw.put("contractValue", "99999.00");
        raw.put("description", "secret terms");

        var masked = service.maskDocumentPayload(raw);

        assertThat(masked.get("ownerEmail")).isEqualTo("a***@client.com");
        assertThat(String.valueOf(masked.get("contactPhone"))).endsWith("4567");
        assertThat(masked.get("contractValue")).isEqualTo("HIDDEN");
        assertThat(masked.get("description")).isEqualTo("REDACTED");
    }

    @Test
    void maskDocumentPayload_usesWorkspaceRegistryWhenPresent() {
        UUID workspaceId = UUID.randomUUID();
        SensitiveFieldRegistry field = SensitiveFieldRegistry.create(
                workspaceId, "DOCUMENT", "ownerEmail", "PII", "MASK_EMAIL");
        when(sensitiveFields.findByWorkspaceId(workspaceId)).thenReturn(List.of(field));

        Map<String, Object> raw = new LinkedHashMap<>();
        raw.put("ownerEmail", "bob@client.com");
        raw.put("description", "visible unless covered");

        var masked = service.maskDocumentPayload(workspaceId, raw);

        assertThat(masked.get("ownerEmail")).isEqualTo("b***@client.com");
        assertThat(masked.get("description")).isEqualTo("REDACTED");
    }
}
