package com.company.scopery.modules.trust.sensitivefield.http.request;
public record UpdateSensitiveFieldRegistryRequest(String classification, String maskingStrategy,
        Boolean exportAllowed, Boolean deactivate) {}
