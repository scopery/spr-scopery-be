package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionSensitiveFieldMaskingPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

// Stub implementation — replaced by real masking logic
@Component
public class StubAiActionSensitiveFieldMaskingPort implements AiActionSensitiveFieldMaskingPort {

    @Override
    public String maskDiff(String entityType, UUID entityId, Map<String, Object> rawDiff) {
        return "{}";
    }

    @Override
    public String maskInput(String toolCode, Map<String, Object> rawInput) {
        return "{}";
    }
}
