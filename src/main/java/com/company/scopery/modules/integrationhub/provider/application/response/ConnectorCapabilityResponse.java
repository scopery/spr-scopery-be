package com.company.scopery.modules.integrationhub.provider.application.response;
import com.company.scopery.modules.integrationhub.provider.domain.model.ConnectorCapability;
import java.util.UUID;
public record ConnectorCapabilityResponse(UUID id, String providerCode, String capabilityCode, String direction,
        boolean enabled, String description) {
    public static ConnectorCapabilityResponse from(ConnectorCapability c) {
        return new ConnectorCapabilityResponse(c.id(), c.providerCode(), c.capabilityCode(), c.direction(),
                c.enabled(), c.description());
    }
}
