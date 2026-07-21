package com.company.scopery.modules.aiaction.application.port;

import java.util.Map;
import java.util.UUID;

public interface AiActionSensitiveFieldMaskingPort {

    String maskDiff(String entityType, UUID entityId, Map<String, Object> rawDiff);

    String maskInput(String toolCode, Map<String, Object> rawInput);
}
