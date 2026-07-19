package com.company.scopery.modules.trust.shared.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SensitiveFieldMaskerTest {

    @Test
    void maskSearchText_masksEmbeddedEmailAndPhone() {
        String raw = "Contact alice@client.com or +1 (555) 123-4567 for details";
        String masked = SensitiveFieldMasker.maskSearchText(raw);
        assertTrue(SensitiveFieldMasker.changed(raw, masked));
        assertTrue(masked.contains("a***@client.com"));
        assertTrue(masked.contains("***4567"));
        assertEquals("REDACTED", SensitiveFieldMasker.redact());
    }
}
