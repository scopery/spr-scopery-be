package com.company.scopery.modules.aiaction.application.port;

import java.util.List;

public interface AiActionPhase21CompatibilityPort {

    List<AiActionRequestedAction> mapToRequestedActions(String legacyPayloadJson);

    boolean isSupported(String legacyPayloadJson);
}
