package com.company.scopery.modules.profitability.shared.event;
import java.util.UUID;
public record ProfitabilityRebuildRequestedEvent(UUID projectId, String reason) {}
