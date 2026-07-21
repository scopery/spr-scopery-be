package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionPhase21CompatibilityPort;
import com.company.scopery.modules.aiaction.application.port.AiActionRequestedAction;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;

import java.util.List;

// Stub implementation — replaced in Step 20
@Component
public class StubAiActionPhase21CompatibilityPort implements AiActionPhase21CompatibilityPort {

    @Override
    public List<AiActionRequestedAction> mapToRequestedActions(String legacyPayloadJson) {
        throw AiActionExceptions.legacyPayloadUnsupported(legacyPayloadJson);
    }

    @Override
    public boolean isSupported(String legacyPayloadJson) {
        return false;
    }
}
