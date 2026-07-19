package com.company.scopery.modules.trust.sensitiveobject.http.request;
public record UpdateSensitiveObjectRegistryRequest(String classification, Boolean exportReasonRequired,
        Boolean searchIndexAllowed, Boolean deactivate) {}
