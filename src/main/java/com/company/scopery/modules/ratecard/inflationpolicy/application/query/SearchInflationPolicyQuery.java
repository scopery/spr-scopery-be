package com.company.scopery.modules.ratecard.inflationpolicy.application.query;
import java.util.UUID;
public record SearchInflationPolicyQuery(String scope, UUID organizationId, UUID workspaceId, String status,
        String code, int page, int size) {}
