package com.company.scopery.modules.ratecard.ratecard.application.query;
import java.util.UUID;
public record SearchRateCardQuery(String scope, UUID organizationId, UUID workspaceId, String status,
        String currency, String code, int page, int size) {}
